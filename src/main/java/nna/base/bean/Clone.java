package nna.base.bean;

import java.io.Serializable;

/**
 * d
 *
 * @author NNA-SHUAI
 * @create 2017-05-16 18:04
 **/

public abstract class Clone implements Cloneable,Serializable{

    public Clone clone(){
        try {
            return (Clone)super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
