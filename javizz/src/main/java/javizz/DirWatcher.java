package javizz;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

public class DirWatcher implements Runnable {

	private final Path dir;
	private final WatchService watcher;
	private final WatchKey key;

	@SuppressWarnings("unchecked")
	static <T> WatchEvent<T> cast(WatchEvent<?> event) {
		return (WatchEvent<T>) event;
	}

	/**
	 * Creates a WatchService and registers the given directory
	 */
	public DirWatcher(Path dir) throws IOException {
		this.dir = dir;
		this.watcher = FileSystems.getDefault().newWatchService();
		this.key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
	}

	public void run() {
		try {
			for (;;) {
				// wait for key to be signaled

				System.out.println("heeeeeeee");
				System.out.println("7544653");

				WatchKey key = watcher.take();

				System.out.println("dggggggggggggg");

				if (this.key != key) {
					System.err.println("WatchKey not recognized!");
					continue;
				}

				for (WatchEvent<?> event : key.pollEvents()) {
					WatchEvent<Path> ev = cast(event);
					System.out.format("%s: %s\n", ev.kind(), dir.resolve(ev.context()));

					System.out.println("@@@@@@@@A file change has been detected!");
					// TODO: handle event. E.g. call listeners
				}

				// reset key
				if (!key.reset()) {
					break;
				}
			}
		} catch (InterruptedException x) {
			return;
		}
	}
}
