package org.hive2hive.client.console;

import java.util.ArrayList;
import java.util.Scanner;

import org.hive2hive.client.util.Formatter;

/**
 * An abstract console menu to be used with a {@link UIConsole}.
 * 
 * @author Christian
 * 
 */
public abstract class ConsoleMenu {

	private final ArrayList<ConsoleMenuItem> items;

	private boolean exited;
 
	public ConsoleMenu() {
		this.items = new ArrayList<ConsoleMenuItem>();
		createItems();

	}
	
	/**
	 * Specifies the {@link H2HConsoleMenuItem}s of this menu.<br/>
	 * <b>Note:</b> Not all {@link H2HConsoleMenuItem}s are specified here, as they might also be specified
	 * in-line in {@link ConsoleMenu#addMenuItems()}.</br>
	 * <b>Note:</b> {@link H2HConsoleMenuItem}s with preconditions should be specified by this method.
	 */
	protected void createItems() {
		// do nothing by default
	}

	/**
	 * Enlists the {@link H2HConsoleMenuItem}s of this menu.
	 */
	protected abstract void addMenuItems();

	protected void addExitItem() {
		
		add(new H2HConsoleMenuItem("Back") {
			protected void execute() {
				exit();
			}
		});
	}

	protected final void add(ConsoleMenuItem menuItem) {
		items.add(menuItem);
	}

	public final void open() {
		
		addMenuItems();
		addExitItem();
		
		this.exited = false;
		while (!exited) {
			show();
		}
		onMenuExit();
	}

	private final void show() {
		int chosen = 0;

		System.out.println(getInstruction());
		System.out.println();

		for (int i = 0; i < items.size(); ++i) {
			ConsoleMenuItem item = items.get(i);
			
			if (i == items.size()-1)
				System.out.println();
			System.out.println(String.format("    [%s]  %s", i + 1, item.getDisplayText()));
		}
		System.out.println();

		chosen = awaitIntParameter();

		if (chosen > items.size() || chosen < 1) {
			printError(String.format("Invalid option. Select an option from 1 to %s.\n", items.size()));
		} else {
			ConsoleMenuItem item = items.get(chosen - 1);
			item.invoke();
		}
	}

	public static String awaitStringParameter() {
		Formatter.setInputForeground();

		// do not close input
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);

		String parameter = null;
		boolean success = false;
		while (!success) {
			try {
				parameter = input.next();
				success = true;
			} catch (Exception e) {
				printError("Exception while parsing the parameter.");
			}
		}

		Formatter.setDefaultForeground();
		return parameter;
	}

	public static int awaitIntParameter() {
		boolean success = false;
		int number = 0;
		while (!success) {
			try {
				number = Integer.parseInt(awaitStringParameter());
				success = true;
			} catch (NumberFormatException e) {
				printError("This was not a number... Try again!");
			}
		}
		return number;
	}

	public static boolean awaitBooleanParameter() {
		return Boolean.parseBoolean(awaitStringParameter());
	}

	public static void printError(String errorMsg) {
		Formatter.setErrorForeground();
		System.err.println(errorMsg);
		Formatter.setDefaultForeground();
	}

	protected void exit() {
		exited = true;
	}

	protected void onMenuExit() {
		// do nothing by default
	}

	protected abstract String getInstruction();
}
