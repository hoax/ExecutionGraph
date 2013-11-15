package de.kodekultur.utils.groovy;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

class ExecutionGraphRunner {

	def finishedProcesses = (Set) []
	def processes
	def pool = Executors.newFixedThreadPool(5);

	public ExecutionGraphRunner(Collection<Process> processes) {
		this.processes = processes.clone();
	}

	def run() {
		finishedProcesses = [:];

		def startProcesses = processes.findAll { p -> p.dependsOn.isEmpty() }

		startProcesses.each { p ->
			runProcess(p);
		}
	}

	public void runProcess(Process process) {
		processes.remove(process);
		pool.submit {
			try {
				process.action.call(process);
				processFinished(process, false)
			} catch (Exception e) {
				processFinished(process, true)
			}
		}
	}

	public void processFinished(Process process, boolean failed) {
		finishedProcesses[process.name] = failed
		def processesToStart = []
		if (processes.isEmpty()) {
			pool.shutdown();
		} else {
			processes.each { p ->
				def okToStart = p.dependsOn.every { dep ->
					finishedProcesses.keySet().contains(dep);
				}

				if (okToStart) {
					processesToStart << p
				}
			}
			processesToStart.each { p ->
				runProcess(p)
			}
		}
	}
}