package nna.base.util.view;

import nna.base.bean.Clone;

import java.util.Map;

/**
 * template
 * @author NNA-SHUAI
 * @create 2017-05-14 21:40
 **/

public class Template extends Clone{
    private String[] strs;
    private View[] views;

    public Template Clone(){
        Template template=(Template) super.clone();
        template.setStrs(strs.clone());
        template.setViews(views.clone());
        return template;
    }

    public String render(Map<String,String[]> map){
        StringBuilder renderStr=new StringBuilder("");
        int count=views.length;
        String temp;
        int viewIndex=0;
        View view;
        String tempStr;
        for(int index=0;index <count;index++){
            temp=strs[index];
            if(temp!=null&&!temp.trim().equals("")){
                renderStr.append(temp);
            }else {
                view=views[viewIndex];
                tempStr=renderView(view,map);
                renderStr.append(tempStr);
            }
        }
        return renderStr.toString();
    }

    private String renderView(View view, Map<String, String[]> map) {
        StringBuilder render=new StringBuilder("");
        String[] views=view.getViews();
        String[] keys=view.getRenderNms();
        int count=views.length;
        int renderCount=map.get(keys[0]).length;
        String temp;
        for(int index=0;index < renderCount;index++){
            int nullIndex=0;
            for(int i=0;i < count;i++){
                temp=views[i];
                if(temp!=null&&!temp.trim().equals("")){
                    render.append(temp);
                }else{
                    String[] temps=map.get(keys[nullIndex]);
                    assert temps!=null;
                    assert temps.length >= (index+1);
                    if(temps==null){continue;}
                    render.append(temps[index]);
                    nullIndex++;
                }
            }
        }
        return render.toString();
    }

    public void setStrs(String[] strs) {
        this.strs = strs;
    }

    public void setViews(View[] views) {
        this.views = views;
    }
    public String[] getStrs() {
        return strs;
    }

    public View[] getViews() {
        return views;
    }
}
