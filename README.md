ExecutionGraph
==============

Lib for creating, checking and execution of a graph of dependent actions.

A graph of actions/tasks can be created using class TaskExectionBuilder like this:

```groovy
import de.kodekultur.utils.groovy.*

b = new TaskGraphBuilder()

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
