package de.kodekultur.utils.groovy;

import groovy.lang.Closure;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * Describes a task.
 *
 * @author Tobias Mayer (tma@kodekultur.de)
 */
public class Task {

	// its name
	private String name;
	
	// action to be executed
	private Closure<?> action;

	// names of processes this process depends on
	private Set<String> dependsOn = new TreeSet<String>();

	/**
	 * Creates a new Task object
	 * @param name
	 */
	public Task(String name) {
		this.name = name;
	}

	/**
	 * sets the names of the processes this process depends on
	 * @param name
	 */
	public void dependsOn(String... name) {
		dependsOn.addAll(Arrays.asList(name));
	}
	
	/**
	 * sets the action to be executed
	 * @param action
	 */
	public void action(Closure<?> action) {
		this.action = action;
	}
	
	/**
	 * gets the action to be executed
	 * @return
	 */
	public Closure<?> getAction() {
		return action;
	}
	
	/**
	 * get the name of this process
	 * @return
	 */
	public String getName() {
		return name;
	}
}
