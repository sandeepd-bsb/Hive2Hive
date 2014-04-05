package org.hive2hive.client.menu;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;

import org.apache.commons.io.FileUtils;
import org.hive2hive.client.console.H2HConsoleMenu;
import org.hive2hive.client.console.H2HConsoleMenuItem;

public class FileMenu extends H2HConsoleMenu {

	public H2HConsoleMenuItem CreateRootDirectory;

	private File rootDirectory;

	@Override
	protected void createItems() {

		CreateRootDirectory = new H2HConsoleMenuItem("Create Root Directory") {
			protected void execute() throws Exception {

				rootDirectory = new File(FileUtils.getUserDirectory(), "H2H_" + System.currentTimeMillis());
				
				if (isExpertMode) {
					System.out.printf(
							"Please specify the root directory path or enter 'ok' if you agree with '%s'.",
							rootDirectory.toPath());

					String input = awaitStringParameter();

					if (!input.equalsIgnoreCase("ok")) {
						while (!Files.exists(new File(input).toPath(), LinkOption.NOFOLLOW_LINKS)) {

							printError("This directory does not exist. Please retry.");
							input = awaitStringParameter();
						}
					}
				}

				if (!Files.exists(rootDirectory.toPath(), LinkOption.NOFOLLOW_LINKS)) {
					try {
						FileUtils.forceMkdir(rootDirectory);
					} catch (Exception e) {
						printError(String.format("Exception on creating the root directory %s: " + e,
								rootDirectory.toPath()));
					}
				}
			}
		};

	}

	@Override
	protected void addMenuItems() {

		// add(new H2HConsoleMenuItem("Add File") {
				// @Override
				// protected void execute() throws Hive2HiveException, InterruptedException {
				// IProcessComponent process = node.getFileManager().add(askForFile(true));
				// executeBlocking(process);
				// }
				// });
				//
				// add(new H2HConsoleMenuItem("Update File") {
				// protected void execute() throws Hive2HiveException, InterruptedException {
				// IProcessComponent process = node.getFileManager().update(askForFile(true));
				// executeBlocking(process);
				// }
				// });
				// add(new H2HConsoleMenuItem("Move File") {
				// protected void execute() throws Hive2HiveException, InterruptedException {
				// System.out.println("Source path needed: ");
				// File source = askForFile(true);
				//
				// System.out.println("Destination path needed: ");
				// File destination = askForFile(false);
				//
				// IProcessComponent process = node.getFileManager().move(source, destination);
				// executeBlocking(process);
				// }
				// });
				// add(new H2HConsoleMenuItem("Delete File") {
				// protected void execute() throws Hive2HiveException, InterruptedException {
				// IProcessComponent process = node.getFileManager().delete(askForFile(false));
				// executeBlocking(process);
				// }
				// });
				// add(new H2HConsoleMenuItem("Recover File") {
				// protected void execute() throws Hive2HiveException {
				// // TODO implement recover process
				// notImplemented();
				// }
				// });
				// add(new H2HConsoleMenuItem("Share") {
				// protected void execute() throws IllegalArgumentException, NoSessionException,
				// IllegalFileLocation, NoPeerConnectionException, InterruptedException,
				// InvalidProcessStateException {
				// System.out.println("Specify the folder to share:");
				// File folder = askForFile(true);
				//
				// System.out.println("Who do you want to share with?");
				// String friendId = awaitStringParameter();
				//
				// System.out.println("Read or write permissions? Enter 1 for 'READ-ONLY', 2 for WRITE");
				// int permission = awaitIntParameter();
				// PermissionType perm = PermissionType.WRITE;
				// if (permission == 1) {
				// perm = PermissionType.READ;
				// }
				//
				// IProcessComponent process = node.getFileManager()
				// .share(folder, friendId, perm);
				// executeBlocking(process);
				// }
				// });
				// add(new H2HConsoleMenuItem("Get File list") {
				// protected void execute() throws Hive2HiveException, InterruptedException {
				// IResultProcessComponent<List<Path>> process = node.getFileManager()
				// .getFileList();
				// IProcessResultListener<List<Path>> resultListener = new IProcessResultListener<List<Path>>() {
				// @Override
				// public void onResultReady(List<Path> result) {
				// // print the digest
				// System.out.println("File List:");
				// for (Path path : result) {
				// System.out.println("* " + path.toString());
				// }
				// }
				// };
				//
				// process.attachListener(resultListener);
				// executeBlocking(process);
				// }
				// });
				//
				// add(new H2HConsoleMenuItem("File Observer") {
				// protected void checkPreconditions() {
				// if (root == null) {
				// printPreconditionError("Cannot configure file observer: Root path not defined yet. Please login first.");
				// Login.invoke();
				// }
				// if (node == null) {
				// printPreconditionError("Cannot register: Please create a H2HNode first.");
				// nodeMenu.open();
				// checkPreconditions();
				// }
				// }
				//
				// @Override
				// protected void execute() throws Exception {
				// fileObserverMenu = new FileObserverMenu(root, node.getFileManager());
				// fileObserverMenu.open();
				// }
				// });
				//
				// add(new H2HConsoleMenuItem("Logout") {
				// protected void execute() throws Hive2HiveException, InterruptedException {
				// IProcessComponent process = node.getUserManager().logout();
				// executeBlocking(process);
				// }
				// });

				// add(new H2HConsoleMenuItem("Get Status") {
				// protected void execute() throws Hive2HiveException {
				// IH2HNodeStatus status = nodeMenu.getH2HNode().getStatus();
				// System.out.println("Connected: " + status.isConnected());
				// if (status.isLoggedIn()) {
				// System.out.println("User ID: " + status.getUserId());
				// System.out.println("Root path: " + status.getRoot().getAbsolutePath());
				// } else {
				// System.out.println("Currently, nobody is logged in");
				// }
				// System.out.println("Number of processes: " + status.getNumberOfProcesses());
				// }
				// });
	}
	
	// /**
	// * Asks for a (valid) file
	// */
	// private File askForFile(boolean expectExistence) {
	// File file = null;
	// do {
	// System.out.println("Specify the relative file path to " + root.getAbsolutePath());
	// String path = awaitStringParameter();
	// file = new File(root, path);
	// if (expectExistence && !file.exists())
	// System.out.println("File '" + file.getAbsolutePath() + "' does not exist. Try again.");
	// } while (expectExistence && (file == null || !file.exists()));
	// return file;
	// }

	@Override
	protected String getInstruction() {
		return null;
	}

	public File getRootDirectory() {
		return rootDirectory;
	}
}
