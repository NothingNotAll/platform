package nna.base.util;

import java.util.Map;

public class CharUtil {

    public static String getString(String[] strings){
        StringBuilder stringBuilder=new StringBuilder("");
        int length=strings.length;
        for(int index=0;index < length;index++){
            stringBuilder.append(strings[index]);
        }
        return stringBuilder.toString();
    }

    public static String getJsonStr(Map<String,String[]> map){
        StringBuilder stringBuilder=new StringBuilder("");

        return stringBuilder.toString();
    }

	/**
	 * java 字符串 转义特殊字符 to js
	 * @param toJsonStr
	 * @param isJSjson 是否给js使用 true 则会转义单引号 \'
	 * @return String
	 */
	public static String stringToJson(String toJsonStr,boolean isJSjson){
		StringBuilder sb = new StringBuilder();
		int length=toJsonStr.length();
		for(int i=0; i<length; i++){
			char c =toJsonStr.charAt(i);
			switch(c){
				case'\'': if(isJSjson) {sb.append("\\\'");}else{sb.append("\'");} break;
				case'\"': if(!isJSjson) {sb.append("\\\"");}else{sb.append("\"");} break;
				case'\\':sb.append("\\\\");break; //如果不处理单引号，可以释放此段代码，若结合StringDanYinToJSON()处理单引号就必须注释掉该段代码
				case'/': sb.append("\\/");break;
				case'\b':sb.append("\\b");break;//退格
				case'\f':sb.append("\\f");break;//走纸换页
				case'\n':sb.append("\\n");break;//换行
				case'\r':sb.append("\\r");break;//回车
				case'\t':sb.append("\\t");break;//横向跳格
				default: sb.append(c);
			}}
		return sb.toString();
	}
	public static  boolean checkIsLeftQuota(char currentChar){
		if (currentChar=='<') {
			return true;
		}else {
			return false;
		}
	}
	public static boolean checkIsNonSlash(char currentChar){
		if (currentChar=='\\') {
			return true;
		}else {
			return false;
		}
	}
	public static  boolean checkIsDot(char currentChar){
		if (currentChar=='\''||currentChar=='"') {
			return true;
		}else {
			return false;
		}
	}
	public static  boolean checkIsEqual(char currentChar){
		if (currentChar=='=') {
			return true;
		}else {
			return false;
		}
	}
	public static  boolean checkIsSlash(char currentChar){
		if (currentChar=='/') {
			return true;
		}else {
			return false;
		}
	}
	public static  boolean checkIsRightQuota(char currentChar){
		if (currentChar=='>') {
			return true;
		}else {
			return false;
		}
	}
	public static  boolean checkIsBlank(char currentChar){
		if (currentChar=='\b'||currentChar=='\t'||currentChar==' '||currentChar=='\r'||currentChar=='\n') {
			return true;
		}else {
			return false;
		}
	}
	public static boolean checkIsWarn(char currentChar){
		if (currentChar=='!') {
			return true;
		}else {
			return false;
		}
	}
	public static boolean checkIsQuestion(char currentChar) {
		if (currentChar=='?') {
			return true;
		}else {
			return false;
		}
	}
	public static boolean checkIsCharInitial(char currentChar) {
		if (currentChar=='\u0000') {
			return true;
		}else {
			return false;
		}
	}
	//KMP-PrevAndSuffixVersion Algorithm
	/*
	 *       a1 a2 a3 a4 a5
	 *       a1 a2 a3 a4 a6
	 *       if a5 is not equal to a6 , how far the cursor of matched String we move;
	 * 
	 * */
	public static int[] nexts(String searchStr){
		char[] chars=searchStr.toCharArray();
		int length=chars.length;
		if (length==0)
			return new int[0];
		int[] nexts=new int[length];
		nexts[0]=0;
		char errorChar;
		String rightStr;
		String compareStr;
		char end;
		for(int index=1;index < length;index++){
			errorChar=searchStr.charAt(index);
			rightStr=searchStr.substring(0,index);
			compareStr=rightStr.substring(1);
			int moveCount=1;
			for(;;){
			    if(compareStr.length()==1){
			        if(compareStr.charAt(0)==errorChar){
			            moveCount++;
			            break;
                    }
                }
                if(compareStr.length()==0){
			        break;
                }
				end=rightStr.charAt(rightStr.length()-moveCount);
				if(rightStr.startsWith(compareStr)&&end!=errorChar){
					break;
				}else{
					moveCount++;
					compareStr=compareStr.substring(1);
				}
			}
			nexts[index]=index-moveCount;
		}
		return nexts;
	}

	private CharUtil(){}
	public static void main(String[] args) {
		int[] nexts=nexts("abcaabcdabcd");
		for (int i = 0; i < nexts.length; i++) {
			System.out.println(nexts[i]);
		}
//		System.out.println("\r\n".getBytes().length);
	}
}
