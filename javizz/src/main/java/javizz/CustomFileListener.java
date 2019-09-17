package javizz;

import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;

public class CustomFileListener implements FileListener {

	@Override
	public void fileDeleted(FileChangeEvent event) throws Exception {

		// Code here will trigger whenever the file monitoring detects a file has been deleted
		String fullPath = event.getFile().getName().getPath();
		String shortPath = fullPath.substring(fullPath.indexOf("main"));
		System.out.println("\t" + shortPath + " deleted.");
	}

	@Override
	public void fileCreated(FileChangeEvent event) throws Exception {

		// Code here will trigger whenever the file monitoring detects a file has been created
		String fullPath = event.getFile().getName().getPath();
		String shortPath = fullPath.substring(fullPath.indexOf("main"));
		System.out.println("\t" + shortPath + " created.");
	}

	@Override
	public void fileChanged(FileChangeEvent event) throws Exception {

		// Code here will trigger whenever the file monitoring detects a file has been edited

		String fullPath = event.getFile().getName().getPath();
		String shortPath = fullPath.substring(fullPath.indexOf("main"));
		System.out.println("\t" + shortPath + " changed.");

		System.out.println("@@@@@@@@@@@@@@@@");

		if (getSyncMode()) { // Upon noticing the change, we only act if "sync mode" has been enabled

			System.out.println("Updating the model...");
			// TODO call .updateModel

		}
	}

	public boolean getSyncMode() {
		return Demonstration.syncMode;
	}

}
