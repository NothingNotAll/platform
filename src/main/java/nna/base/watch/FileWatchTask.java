package nna.base.watch;


import nna.base.util.concurrent.AbstractTask;

import java.io.IOException;
import java.nio.file.*;

/**
 * @author NNA-SHUAI
 * @create 2017-06-22 10:15
 **/

public class FileWatchTask extends AbstractTask{

    private static void test(){
        try {
            FileSystem fileSystem=FileSystems.getDefault();
            WatchService watchService=fileSystem.newWatchService();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Paths.get("").register(watchService,new WatchEvent.Kind[StandardWatchEventKinds.ENTRY_CREATE],StandardWatchEventKinds.ENTRY_DELETE,StandardWatchEventKinds.ENTRY_MODIFY,StandardWatchEventKinds.OVERFLOW]);
    }

    public FileWatchTask() {
        super(false);
    }


    public static void main(String[] args) throws IOException {

    }


    protected Object doTask(Object att, int taskType) throws Exception {

        return null;
    }
}
