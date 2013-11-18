package de.kodekultur.utils.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.File;
import java.io.IOException;

import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;

/**
 * Util class to help reading/processing config files.
 * 
 * @author Tobias Mayer (tma@kodekultur.de)
 */
public class ProcessUtil {

	/**
	 * Reads the process configuration from the given file. The returned
	 * {@link ExecutionGraphBuilder} can then be used to determine if an error
	 * occured and to access the created processes.
	 * 
	 * @param file
	 *            File with process configuration
	 * @return With file initialized {@link ExecutionGraphBuilder}
	 * @throws CompilationFailedException
	 * @throws IOException
	 */
	public static ExecutionGraphBuilder processConfigFile(File file)
			throws CompilationFailedException, IOException {
		// use DelegatingScript as Script-superclass for nicer syntax inside file
		CompilerConfiguration config = new CompilerConfiguration();
		config.setScriptBaseClass(DelegatingScript.class.getName());
		
		Binding binding = new Binding() {
			@Override
			public Object getVariable(String name) {
				Object o = super.getVariable(name);
				if (o == null) {
					o = name;
				}
				return o;
			}
		};
		
		// process file with GroovyShell
		GroovyShell sh = new GroovyShell(binding, config);
		DelegatingScript parsed = (DelegatingScript) sh.parse(file);
		ExecutionGraphBuilder egb = new ExecutionGraphBuilder();
		// set builder as delegate, so we can directly use the builder within
		// the config file
		parsed.setDelegate(egb);
		// run the config file
		parsed.run();
		
		// return the filled ExecutionGraphBuilder
		return egb;
	}

}
