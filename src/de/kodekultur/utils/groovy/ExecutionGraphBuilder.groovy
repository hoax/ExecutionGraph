package de.kodekultur.utils.groovy
import Process;


class ExecutionGraphBuilder {

	private Map<String, Process> processes = new HashMap<String, Process>();
	
	def getProcess(String name) {
		def p = processes.get(name) ?: new Process(name);
		processes.put(name, p);
		return p;
	}
	
	def methodMissing(String name, args) {
		def process = getProcess(name);
		Closure<?> c = args[0];
		runClosure(process, c);
		return process;
	}
	
	def runClosure(Process process, Closure<?> processClosure) {
		def runClone = processClosure.clone()
		
		runClone.delegate = process
		runClone.resolveStrategy = Closure.DELEGATE_ONLY
		
		runClone();
	}

	def getAllProcesses() {
		return Collections.unmodifiableMap(processes);
	}
	
	def checkDependencies() {
		// init stack
		def stack = (List) processes.values().findAll { it.dependsOn.isEmpty() }
		def errors = []
		while(!stack.isEmpty()) {
			def current = stack.pop();
			
			def processesDepOnCurrent = processes.values().findAll { it.dependsOn.contains(current.name) }
			processesDepOnCurrent.each {
				if (current.dependsOn.contains(it.name)) {
					errors << current
				} else {
					it.dependsOn += current.dependsOn
					stack.push it
				}
			}
		}
		return errors;
	}
}
