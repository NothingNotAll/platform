package nna.base.util.view;


import java.util.ArrayList;

/**
 *
 * bug: such as that: "<!--adfas--></>" this will be error;
 * @author NNA-SHUAI
 * @create 2017-05-23 14:21
 **/

 class ViewParser {

    static View getView(String temp){
        View view=new View();
        ArrayList<String> views=new ArrayList<String>();
        ArrayList<String> nms=new ArrayList<String>();
         char[] chars=temp.toCharArray();
         boolean isLeft=true;
         int length=chars.length;
         StringBuilder tempStr=new StringBuilder("");
         for(int index=0;index < length;index++){
             if(isLeft){
                 if(chars[index]=='{'){
                     views.add(tempStr.toString());
                     views.add(null);
                    tempStr=new StringBuilder("");
                    isLeft=false;
                 }else{
                     tempStr.append(chars[index]);
                 }
             }else{
                if(chars[index]=='}'){
                    nms.add(tempStr.toString().trim());
                    tempStr=new StringBuilder("");
                    isLeft=true;
                }else{
                    tempStr.append(chars[index]);
                }
             }
         }
         if(!tempStr.equals("")){
             views.add(tempStr.toString());
         }
         view.setRenderNms(nms.toArray(new String[0]));
         view.setViews(views.toArray(new String[0]));
         return view;
    }

    public static void main(String[] args){
        View view=getView("<option value=\"{roleId}\">{roleName}</option>");
        for(int index=0;index < view.getRenderNms().length;index++){
            System.out.println(view.getRenderNms()[index]);
        }
        for(int index=0;index < view.getViews().length;index++){
            System.out.println(view.getViews()[index]);
        }
    }

}
