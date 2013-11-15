
"start" {
	action { p ->
		println "start"
		(1..10).each {
			println "${p.name} ${it}"
			Thread.sleep(300)
		}
	}
}

"proc1" {
	dependsOn "start"
	action { p ->
		(1..10).each {
			println "${p.name} ${it}"
			Thread.sleep(300)
		}
	}
}

"proc2" {
	dependsOn "start"
	action { p ->
		(1..10).each {
			println "${p.name} ${it}"
			Thread.sleep(500)
		}
	}
}

"proc3" {
	dependsOn "proc1", "proc2"
	action { p ->
		(1..10).each {
			println "${p.name} ${it}"
			Thread.sleep(300)
		}
	}
}

