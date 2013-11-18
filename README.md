ExecutionGraph
==============

Lib for creating, checking and execution of a graph of dependent actions.

Create a graph
--------------

A graph of actions/tasks can be created using class _TaskGraphBuilder_ like this:

```groovy
import de.kodekultur.utils.groovy.*

def b = new TaskGraphBuilder()

b."first process" {
	action {
		println "start"
	}
}

b."something else" {
	dependsOn "first process"
	action {
		(1..10).each {
			println "something else ${it}"
			Thread.sleep(200);
		}
	}
}

b."some other stuff" {
	dependsOn "first process"
	action { p ->
		(1..10).each {
			println "some other stuff ${it}"
			Thread.sleep(330);
		}
	}
}

b."last process" {
	dependsOn "something else", "some other stuff"
	action {
		println "the end"
	}
}
```
* _dependsOn_ takes a list of tasknames this task depends on
* _action_ is simply a closure defining the code to be executed by this task

The created graph can then be checked for deadlocks using the method _checkForDeadlocks_, which will return collection of tasks causing errors..
```groovy
def errors = b.checkForDeadlocks()
println "Errors: ${errors}"
```

There is also a method _checkForUndefinedDependencies_ to check if any dependsOn-clause references to a task that is not defined somewhere else.
```groovy
def undefTaskNames = b.checkForUndefinedDependencies()
println "These tasks are missing: ${undefTaskNames}"
```

To get a list of all the defined Tasks simply call method _getAllTasks()_.

Execute a graph
---------------

To execute the definied tasks use class _TaskGraphRunner_. Its contructor takes the list of tasks and optionally an ExecutorService.
If no ExecutorService is provided, a fixed threadpool with 8 threads will be used to execute the tasks.

To start the task execution just call _run_ on your _TaskGraphRunner_:
```groovy
x = new TaskGraphRunner( b.getAllTasks() )
x.run()
```
