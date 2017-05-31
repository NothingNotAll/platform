package nna.base.bean;

/**
 * @author NNA-SHUAI
 * @create 2017-05-19 10:28
 **/


public class ReturnMessage extends Clone{
    private static final Long serialVersionUID=0L;

    public boolean isSucess=true;
    public String message="sucess";
    public char returnFlag='S';
    public int returnCode=000000;
    public Object result;

    public ReturnMessage(){}

    public ReturnMessage(String message,int errorCode){
        this.isSucess=false;
        this.message=message;
        returnFlag='F';
    }

    public ReturnMessage(Exception e){
        isSucess=false;
        message=e.getLocalizedMessage()+"-"+e.getMessage();
        returnFlag='F';
        returnCode=111111;
    }
}
