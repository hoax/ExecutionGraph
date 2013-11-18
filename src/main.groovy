import groovy.lang.Binding;

import org.codehaus.groovy.control.*

import de.kodekultur.utils.groovy.DelegatingScript
import de.kodekultur.utils.groovy.TaskGraphBuilder
import de.kodekultur.utils.groovy.TaskGraphRunner
import de.kodekultur.utils.groovy.Task


file = new File("dsl.groovy")
config = new CompilerConfiguration();
config.setScriptBaseClass(DelegatingScript.class.name)

sh = new GroovyShell(config);
parsed = sh.parse(file)
egb = new TaskGraphBuilder()
parsed.setDelegate(egb)
parsed.run()

errors = egb.checkForDeadlocks();
errors += egb.checkForUndefinedDependencies();
println "Errors: ${errors}"
processes = egb.tasks;

processes.values().each {
	println it.dump()
}

runner = new TaskGraphRunner(egb.getAllTasks());
runner.run();
