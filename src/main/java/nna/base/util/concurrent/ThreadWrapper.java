package nna.base.util.concurrent;

/**
 * @author NNA-SHUAI
 * @create 2017-06-24 17:15
 **/

 class ThreadWrapper {
    private Thread thread;
    private QueueWrapper[] qws;

    ThreadWrapper(QueueWrapper[] qws,Thread thread){
        this.qws=qws;
        this.thread=thread;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    void unPark(ThreadWrapper[] tws){

    }
}
