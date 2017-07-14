package nna.base.watch;


import nna.base.util.concurrent.AbstractTask;

import java.io.IOException;
import java.nio.file.spi.FileSystemProvider;

/**
 * @author NNA-SHUAI
 * @create 2017-06-22 10:15
 **/

public class FileWatchTask extends AbstractTask{

    private static void test(){
        java.util.List<FileSystemProvider> list=FileSystemProvider.installedProviders();
        FileSystemProvider fileSystemProvider=list.get(0);

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
