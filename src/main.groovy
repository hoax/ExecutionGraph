import org.codehaus.groovy.control.*

import de.kodekultur.utils.groovy.DelegatingScript
import de.kodekultur.utils.groovy.ExecutionGraphBuilder
import de.kodekultur.utils.groovy.ExecutionGraphRunner

file = new File("dsl.groovy")
config = new CompilerConfiguration();
config.setScriptBaseClass(DelegatingScript.class.name)
sh = new GroovyShell(config);
parsed = sh.parse(file)
egb = new ExecutionGraphBuilder()
parsed.setDelegate(egb)
parsed.run()

errors = egb.checkDependencies();
println "Errors: ${errors}"
processes = egb.processes;

processes.values().each {
	println it.dump()
}

runner = new ExecutionGraphRunner([] + egb.getAllProcesses().values());
runner.run();

