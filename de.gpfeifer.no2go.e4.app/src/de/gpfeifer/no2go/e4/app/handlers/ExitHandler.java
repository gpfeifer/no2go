package de.gpfeifer.no2go.e4.app.handlers;

import org.eclipse.e4.core.di.annotations.Execute;

public class ExitHandler {

	@Execute
	  public void execute() {
	    System.out.println("Called");
	  }
}
