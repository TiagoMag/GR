import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

public class Watcher extends Thread {
    private Path myDir;
    private WatchService watcher;
    protected Agent agent;

    Watcher(String path,Agent agent) {
        try {
            this.agent = agent;
            myDir = Paths.get(path);
            watcher = myDir.getFileSystem().newWatchService();
            myDir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            try {
                WatchKey watchKey = watcher.take();
                List<WatchEvent<?>> events = watchKey.pollEvents();
                for (WatchEvent<?> event : events) {
                    if(event.context().toString().equals("config.ini~")) {
                        System.out.println("Modificado: " + event.context().toString());
                        agent.populateMib();
                    }
                }
                watchKey.reset();
            }
            catch (Exception e) {
                System.out.println("Error: " + e.toString());
            }
        }
    }
}
