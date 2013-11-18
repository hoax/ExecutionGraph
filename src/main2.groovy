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

println b.checkForCycles();
b.tasks.values().each {
	println it.dump()
}

runner = new TaskGraphRunner(b.getAllTasks());
runner.run();
