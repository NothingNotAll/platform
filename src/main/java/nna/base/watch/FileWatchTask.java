package nna.base.watch;

import nna.Marco;
import nna.base.util.concurrent.AbstractTask;
import nna.base.util.concurrent.SeqFixSizeTasks;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

/**
 * @author NNA-SHUAI
 * @create 2017-06-22 10:15
 **/

public class FileWatchTask extends AbstractTask{
//    private static final WatchService fileMonitor=FileSystems.getDefault().newWatchService();

    public FileWatchTask(String taskName) {
        super(taskName, 1);
        startTask(null,Marco.SEQ_FIX_SIZE_TASK);
    }


    public static void main(String[] args) throws IOException {
//        WatchService watchService=FileSystems.getDefault().newWatchService();
//        Paths.get("").register(fileMonitor,,);
    }

    protected Object doTask(int taskType, Object attach) throws Exception {
//        while(true){
//            try{
//               WatchKey watchKey=fileMonitor.take();
//                List<WatchEvent<?>> watchEvents= watchKey.pollEvents();
//                for(WatchEvent watchEvent:watchEvents){
//
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
        return null;
    }
}
