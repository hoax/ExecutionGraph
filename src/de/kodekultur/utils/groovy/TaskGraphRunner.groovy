package de.kodekultur.utils.groovy;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

/**
 * Executes a graph of dependent tasks using the
 * java concurrency framework.
 * 
 * @author Tobias Mayer (tma@kodekultur.de)
 */
public class TaskGraphRunner {

	def finishedTasks = (Set) []
	def tasks
	ExecutorService executor
	def shutDownExecutor = true
	
	/**
	 * Creates a new ExectionGraphRunner using a
	 * fixed thread pool with 8 threads. 
	 * @param tasks Tasks to execute
	 */
	public TaskGraphRunner(Collection<Task> tasks) {
		this(tasks, Executors.newFixedThreadPool(8))
		shutDownExecutor = false
	}
	
	/**
	 * Creates a new ExectionGraphRunner using the
	 * given executor
	 * @param tasks Tasks to execute
	 * @param executor Executor to be used for task execution
	 */
	public TaskGraphRunner(Collection<Task> tasks, ExecutorService executor) {
		this.executor = executor
		this.tasks = [] + tasks;
	}

	/**
	 * Starts the task execution
	 * @return
	 */
	def run() {
		finishedTasks = [:];

		def startTasks = tasks.findAll { p -> p.dependsOn.isEmpty() }

		startTasks.each { p ->
			runTask(p);
		}
	}

	/**
	 * called when the given task is ready to be executed
	 * @param task task to be executed
	 */
	protected void runTask(Task task) {
		tasks.remove(task);
		def future = executor.submit() {
			try {
				task.action.call(task);
				taskFinished(task, false)
			} catch (Exception e) {
				taskFinished(task, true)
			}
		}
		taskSubmitted(task, future);
	}
	
	/**
	 * Does nothing. To be overwritten to get the Future-Object of a
	 * task when it is submitted to the ExecutionService
	 * @param Task task
	 * @param future Future of the task
	 */
	protected void taskSubmitted(Task Task, Future<?> future) {
	}

	/**
	 * called when a task finished exection.
	 * @param task task that finished
	 * @param failed <code>true</code> if exection ended by throwing an Exception, otherwise <code>false</code>.
	 */
	protected void taskFinished(Task task, boolean failed) {
		finishedTasks[task.name] = failed
		def tasksToStart = []
		if (tasks.isEmpty() && shutDownExecutor) {
			executor.shutdown();
		} else {
			tasks.each { p ->
				def okToStart = p.dependsOn.every { dep ->
					finishedTasks.keySet().contains(dep);
				}

				if (okToStart) {
					tasksToStart << p
				}
			}
			tasksToStart.each { p ->
				runTask(p)
			}
		}
	}

}