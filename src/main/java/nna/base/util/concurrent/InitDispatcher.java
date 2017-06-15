package nna.base.util.concurrent;


/**
 * @author NNA-SHUAI
 * @create 2017-06-13 13:43
 **/

 class InitDispatcher implements Runnable{
    private AbstractTasks temp;
    private Worker worker;

    public void run() {
        //性能瓶頸點
        worker.getWorkMap().put(temp.getList()[0].getIndex(),temp);
        //性能瓶頸點
        worker.getWorkQueue().add(temp);
    }

    public InitDispatcher(AbstractTasks abstractTasks,
                          Worker worker){
        this.temp= abstractTasks;
        this.worker=worker;
    }
}
