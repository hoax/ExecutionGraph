package de.kodekultur.utils.groovy


/**
 * Builder to create a graph of tasks that may depend on each other.
 * It also checks if the created graph is valid, so it does not contain loops
 * or deadlocks.
 * <p>
 * Example:
 * <pre>
 * b = new ExectionGraphBuilder()
 * b."first process" {
 *     action {
 *         println "start"
 *     }
 * }
 * b."something else" {
 *     dependsOn "first process"
 *     action {
 *         (1..10).each {
 *             println "something else ${it}"
 *             Thread.sleep(200);
 *         }
 *     }
 * }
 * b."some other stuff" {
 *     dependsOn "first process"
 *     action { p ->
 *     }
 * }
 * b."last process" {
 *     dependsOn "something else", "some other stuff"
 *     action {
 *         println "the end"
 *     }
 * }
 * 
 * b.allProcesses
 * </pre>
 * @author Tobias Mayer (tma@kodekultur.de)
 *
 */
class TaskGraphBuilder {

	/**
	 * Map of name to task
	 */
	private Map<String, Task> tasks = new HashMap<String, Task>();

	/**
	 * Gets the Task with the given name. A new Task will be created
	 * if none with that name already exists.
	 * @param name
	 * @return
	 */
	def getTask(String name) {
		def p = tasks.get(name) ?: new Task(name);
		tasks.put(name, p);
		return p;
	}

	/**
	 * hierarchy level 1: resolve task name -> closure containing "dependsOn" and "action"
	 * @param name method's name
	 * @param args arguments
	 * @return
	 */
	def methodMissing(String name, args) {
		def process = getTask(name);
		Closure<?> c = args[0];
		runClosure(process, c);
		return process;
	}

	/**
	 * Level 2: Execute statements within a task definition
	 * @param task task
	 * @param processClosure task definition closure
	 * @return 
	 */
	def runClosure(Task task, Closure<?> taskClosure) {
		def runClone = taskClosure

		runClone.delegate = task
		runClone.resolveStrategy = Closure.DELEGATE_FIRST

		runClone();
	}

	/**
	 * Returns a list of all defined tasks
	 * @return
	 */
	def getAllTasks() {
		return Collections.unmodifiableList(tasks.values() as List);
	}

	/**
	 * Checks the task dependencies for deadlocks
	 * @return list of tasks causing deadlocks
	 */
	def checkForDeadlocks() {
		def stack = (List) tasks.values().findAll { it.dependsOn.isEmpty() }
		def errors = []
		while(!stack.isEmpty()) {
			def current = stack.pop();

			def processesDepOnCurrent = tasks.values().findAll { it.dependsOn.contains(current.name) }
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
	
	/**
	 * checks for dependencies on undefinied tasks
	 * @return list of undefined but referenced task names
	 */
	def checkForUndefinedDependencies() {
		def errors = new HashSet();
		tasks.values().each { t ->
			t.dependsOn.each  { name ->
				if (!tasks.containsKey(name)) {
					errors << name;
				}
			}
		}
		return errors;
	}
}
