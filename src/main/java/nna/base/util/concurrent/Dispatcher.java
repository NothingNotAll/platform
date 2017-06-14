package nna.base.util.concurrent;


/**
 * @author NNA-SHUAI
 * @create 2017-06-13 13:43
 **/

 class Dispatcher implements Runnable{
    private Tasks temp;
    private Worker worker;
    private boolean isNewTask;

    public void run() {
        if(isNewTask){
            //性能瓶頸點
            worker.getWorkMap().put(temp.getList()[0].getIndex(),temp);
        }
        //性能瓶頸點
        worker.getWorkQueue().add(temp);
    }

    public Dispatcher(Tasks tasks,
                      Worker worker,
                      boolean isMapDispatch){
        this.temp=tasks;
        this.isNewTask=isMapDispatch;
        this.worker=worker;
    }
}
