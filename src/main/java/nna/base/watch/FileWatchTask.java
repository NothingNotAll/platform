package nna.base.watch;

import nna.Marco;
import nna.base.util.concurrent.AbstractTask;

import java.io.IOException;

/**
 * @author NNA-SHUAI
 * @create 2017-06-22 10:15
 **/

public class FileWatchTask extends AbstractTask{
//    private static final WatchService fileMonitor=FileSystems.getDefault().newWatchService();

    public FileWatchTask(String taskName) {
        super("FILE_CHANGE_WATCH",1,1,Marco.NO_SEQ_LINKED_SIZE_TASK,Marco.TIMER_THREAD_TYPE);
        addNewTask(this,null,WORK_TASK_TYPE,false,null);
    }


    public static void main(String[] args) throws IOException {
//        WatchService watchService=FileSystems.getDefault().newWatchService();
//        Paths.get("").register(fileMonitor,,);
    }


    protected Object doTask(Object att, int taskType) throws Exception {
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
