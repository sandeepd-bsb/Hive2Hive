package org.hive2hive.core.api.managers;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.List;

import org.hive2hive.core.H2HSession;
import org.hive2hive.core.api.interfaces.IFileManager;
import org.hive2hive.core.exceptions.IllegalFileLocation;
import org.hive2hive.core.exceptions.NoPeerConnectionException;
import org.hive2hive.core.exceptions.NoSessionException;
import org.hive2hive.core.model.PermissionType;
import org.hive2hive.core.model.UserPermission;
import org.hive2hive.core.network.NetworkManager;
import org.hive2hive.core.processes.ProcessFactory;
import org.hive2hive.core.processes.framework.decorators.AsyncComponent;
import org.hive2hive.core.processes.framework.decorators.AsyncResultComponent;
import org.hive2hive.core.processes.framework.interfaces.IProcessComponent;
import org.hive2hive.core.processes.framework.interfaces.IResultProcessComponent;
import org.hive2hive.core.processes.implementations.files.recover.IVersionSelector;
import org.hive2hive.core.processes.implementations.files.util.FileRecursionUtil;
import org.hive2hive.core.processes.implementations.files.util.FileRecursionUtil.FileProcessAction;

public class H2HFileManager extends H2HManager implements IFileManager {

	public H2HFileManager(NetworkManager networkManager) {
		super(networkManager);
	}

	@Override
	public IProcessComponent add(File file) throws NoSessionException, NoPeerConnectionException,
			IllegalFileLocation {
		// verify the argument
		H2HSession session = networkManager.getSession();
		if (file == null) {
			throw new IllegalArgumentException("File cannot be null.");
		} else if (!file.exists()) {
			throw new IllegalArgumentException("File does not exist.");
		} else if (session.getRoot().toFile().equals(file)) {
			throw new IllegalArgumentException("Root cannot be added.");
		} else if (!file.getAbsolutePath().toString().startsWith(session.getRootFile().getAbsolutePath())) {
			throw new IllegalFileLocation();
		}

		IProcessComponent addProcess;
		if (file.isDirectory() && file.listFiles().length > 0) {
			// add the files recursively
			List<Path> preorderList = FileRecursionUtil.getPreorderList(file.toPath());
			addProcess = FileRecursionUtil.buildUploadProcess(preorderList, FileProcessAction.NEW_FILE,
					networkManager);
		} else {
			// add single file
			addProcess = ProcessFactory.instance().createNewFileProcess(file, networkManager);
		}

		AsyncComponent asyncProcess = new AsyncComponent(addProcess);

		submitProcess(asyncProcess);
		return asyncProcess;
	}

	@Override
	public IProcessComponent update(File file) throws NoSessionException, IllegalArgumentException,
			NoPeerConnectionException {
		if (!file.isFile()) {
			throw new IllegalArgumentException("A folder can have one version only");
		}

		IProcessComponent updateProcess = ProcessFactory.instance().createUpdateFileProcess(file,
				networkManager);
		AsyncComponent asyncProcess = new AsyncComponent(updateProcess);

		submitProcess(asyncProcess);
		return asyncProcess;

	}

	@Override
	public IProcessComponent move(File source, File destination) throws NoSessionException,
			NoPeerConnectionException {
		IProcessComponent moveProcess = ProcessFactory.instance().createMoveFileProcess(source, destination,
				networkManager);

		AsyncComponent asyncProcess = new AsyncComponent(moveProcess);

		submitProcess(asyncProcess);
		return asyncProcess;
	}

	@Override
	public IProcessComponent delete(File file) throws NoSessionException, NoPeerConnectionException {
		IProcessComponent deleteProcess;
		if (file.isDirectory() && file.listFiles().length > 0) {
			// delete the files recursively
			List<Path> preorderList = FileRecursionUtil.getPreorderList(file.toPath());
			deleteProcess = FileRecursionUtil.buildDeletionProcess(preorderList, networkManager);
		} else {
			// delete a single file
			deleteProcess = ProcessFactory.instance().createDeleteFileProcess(file, networkManager);
		}

		AsyncComponent asyncProcess = new AsyncComponent(deleteProcess);

		submitProcess(asyncProcess);
		return asyncProcess;
	}

	@Override
	public IProcessComponent recover(File file, IVersionSelector versionSelector)
			throws FileNotFoundException, IllegalArgumentException, NoSessionException,
			NoPeerConnectionException {
		// do some verifications
		if (file.isDirectory()) {
			throw new IllegalArgumentException("A foler has only one version");
		} else if (!file.exists()) {
			throw new FileNotFoundException("File does not exist");
		}

		IProcessComponent recoverProcess = ProcessFactory.instance().createRecoverFileProcess(file,
				versionSelector, networkManager);

		AsyncComponent asyncProcess = new AsyncComponent(recoverProcess);

		submitProcess(asyncProcess);
		return asyncProcess;
	}

	@Override
	public IProcessComponent share(File folder, String userId, PermissionType permission)
			throws IllegalFileLocation, IllegalArgumentException, NoSessionException,
			NoPeerConnectionException {
		// verify
		if (!folder.isDirectory())
			throw new IllegalArgumentException("File has to be a folder.");
		if (!folder.exists())
			throw new IllegalFileLocation("Folder does not exist.");

		H2HSession session = networkManager.getSession();
		Path root = session.getRoot();

		// folder must be in the given root directory
		if (!folder.toPath().toString().startsWith(root.toString()))
			throw new IllegalFileLocation("Folder must be in root of the H2H directory.");

		// sharing root folder is not allowed
		if (folder.toPath().toString().equals(root.toString()))
			throw new IllegalFileLocation("Root folder of the H2H directory can't be shared.");

		IProcessComponent shareProcess = ProcessFactory.instance().createShareProcess(folder,
				new UserPermission(userId, permission), networkManager);

		AsyncComponent asyncProcess = new AsyncComponent(shareProcess);

		submitProcess(asyncProcess);
		return asyncProcess;
	}

	@Override
	public IResultProcessComponent<List<Path>> getFileList() {
		IResultProcessComponent<List<Path>> fileListProcess = ProcessFactory.instance()
				.createFileListProcess(networkManager);

		AsyncResultComponent<List<Path>> asyncProcess = new AsyncResultComponent<List<Path>>(fileListProcess);

		submitProcess(asyncProcess);
		return asyncProcess;
	}

}