package nna.base.util.view;

import java.util.Map;

/**
 * @author NNA-SHUAI
 * @create 2017-05-23 20:41
 **/

public class View {
    private String[] views;
    private String[] renderNms;

    public String[] getViews() {
        return views;
    }

    public void setViews(String[] views) {
        this.views = views;
    }

    public String[] getRenderNms() {
        return renderNms;
    }

    public void setRenderNms(String[] renderNms) {
        this.renderNms = renderNms;
    }

    public String toString(){
        return  toString(null);
    }

    public String toString(Map<String,String[]> map){
        StringBuilder builder=new StringBuilder();
        String renderNm=renderNms[0];
        String[] renderValues;
        if(map==null){
            renderValues=new String[1];
        }else{
            renderValues=map.get(renderNm);
        }
        int renderCount=renderValues.length;
        int count=views.length;
        String view;
        String[] vals;
        for(int index=0;index < renderCount;index++){
            for(int index2=0,index3=0;index2 < count;index2++){
                view=views[index2];
                if(view==null){
                    renderNm=renderNms[index3++];
                    if(map==null){
                        view="null";
                    }else{
                        vals=map.get(renderNm);
                        view=vals[index];
                    }
                }
                builder.append(view);
            }
        }
        return builder.toString();
    }
}
