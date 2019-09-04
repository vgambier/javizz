package javizz;

import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;

public class CustomFileListener implements FileListener {

	@Override
	public void fileChanged(FileChangeEvent arg0) throws Exception {

		System.out.println("A file change has been detected: " + arg0);

	}

	@Override
	public void fileCreated(FileChangeEvent arg0) throws Exception {

		System.out.println("A file creation has been detected!");

	}

	@Override
	public void fileDeleted(FileChangeEvent arg0) throws Exception {

		System.out.println("A file deletion has been detected!");

	}

}
