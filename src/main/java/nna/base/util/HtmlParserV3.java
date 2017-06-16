package nna.base.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedList;

//HTML解析引擎
public class HtmlParserV3 {
	private ParserHelper pHelper;//文档游标控制器
	
	private char currentChar;//游标当前指向字符
	private String docType;//文档类型
	private HtmlTag currentTag;//当前解析的标签
	private LinkedList<HtmlTag> tagLinkedList;//文档标签容器
	private LinkedList<HtmlTag> commentTagLinkedList;//文档级别的注释标签。
	private LinkedList<String> textLinkedList;//文档级别的内容。两个完整标签之间的内容 例： <a sdd=sdsd/>docTExt<a/>  <a><a/>docText<a><a/>
	private boolean isEnd;//是否解析完毕。
	
	private int[] kmpScriptNexts=CharUtil.nexts("</script>");
	private int[] kmpCommentNexts=CharUtil.nexts("-->");
	
	void initial(){
		tagLinkedList=new LinkedList<HtmlTag>();
		commentTagLinkedList=new LinkedList<HtmlTag>();
		textLinkedList=new LinkedList<String>();
	}
	public HtmlParserV3(String html) {//字符串形式解析 
		initial();
		pHelper=new StringHtmlParseHelper(html);
		initialDocType();
	}
	public HtmlParserV3(InputStream inputStream, String charSet){//字节流形式进行解析。已转化为缓冲流
		this(new BufferedReader(new InputStreamReader(inputStream, Charset.forName(charSet))));
	}
	public HtmlParserV3(BufferedReader bufferedReader) {//字符缓冲流形式进行解析
		initial();
		pHelper=new BufferedReaderParseHelper(bufferedReader);
		initialDocType();
	}
	public HtmlParserV3(InputStreamReader inputStreamReader){//字符缓冲流形式解析
		this(new BufferedReader(inputStreamReader));
	}
	public static void main(String[] args) {
		String html="<!DOCTYPE SSS><script></script><!--s--><S SD='DD' afdaf dd dd SDSD=DDD>sfdasfas</S><D SD=DD/>sadfasd<SDFSDF sdsadf==sdd>sdfsaf</HTML>";
		System.out.println(html);
		HtmlParserV3 htmlParserV3=new HtmlParserV3(html);
		try {
			htmlParserV3.parseHTML();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			Iterator<HtmlTag> iterator=htmlParserV3.tagLinkedList.iterator();
			while (iterator.hasNext()) {
				HtmlTag tag = (HtmlTag) iterator.next();
				System.out.println("tag name : "+tag.getTagName());
				LinkedList<String> attributeNames=tag.getAttributeNameLinkedList();
				LinkedList<String> attributeValues=tag.getAttributeValueLinkedList();
				LinkedList<String> texts=htmlParserV3.textLinkedList;
				Iterator<String> nameIterator=attributeNames.iterator();
				Iterator<String> valueIterator=attributeValues.iterator();
				Iterator<String> textIterator=texts.iterator();
				while (nameIterator.hasNext()) {
					String string = nameIterator.next();
					System.out.println("attribute name : "+string);
				}
				while (valueIterator.hasNext()) {
					String string = valueIterator.next();
					System.out.println("attribute value : "+string);
				}
				while (textIterator.hasNext()) {
					String string = textIterator.next();
					System.out.println("tag text : "+string);
				}
			}
		}
	}
	
	public void parseHTML(){//core code 核心解析过程 。
		while(!isEnd){
			currentTag=new HtmlTag();
			if (CharUtil.checkIsSlash(currentChar)) {///</T> 形式标签
				currentTag.setTagType('T');//这个标签已经解析完毕 直到 >结束
				incrementChar();//忽略掉 /
			}
			if (CharUtil.checkIsWarn(currentChar)) {//<!-- comment --> 注释标签
				currentTag.setTagType('!');
				currentTag.setTagName("COMMENT");
				getComment();
				setCommentToTag(currentTag);//这个标签已经解析完毕。直到 >结束
				incrementChar();//忽略掉 >
			}
			getNextNonBlank();
			if (!CharUtil.checkIsWarn(currentTag.getTagType())) {
				getTagName();//获取标签名称
			}
			getNextNonBlank();
			if (!CharUtil.checkIsSlash(currentChar)&&!CharUtil.checkIsRightQuota(currentChar)) {
				parseAttributes();//获取标签属性
			}
			if (CharUtil.checkIsSlash(currentChar)) {//<F/> 形式标签
				currentTag.setTagType('F');
				incrementChar();//忽略掉 /
				getNextNonBlank();
			}
			if (CharUtil.checkIsRightQuota(currentChar)) {
				if (CharUtil.checkIsCharInitial(currentTag.getTagType())) {
					currentTag.setTagType('H');//<H> 形式标签
				}
				incrementChar();//忽略掉 >
			}
			getText();//untill <
			incrementChar();//忽略掉 <
			getNextNonBlank();//为下一轮解析做准备
			tagLinkedList.add(currentTag);//添加已经解析好的 tag到容器。
		}
	}
	private void getText(){
		if (currentTag.getTagName().toLowerCase().equals("script")&&currentTag.getTagType()=='H') {
			getScript();
			incrementChar();
			getNextNonBlank();
		}
		switch (currentTag.getTagType()) {
		case 'H':
			//个性化处理
			break;
		case 'F':
			//个性化处理
			break;
		case 'T':
			//个性化处理
			break;
		}
		getTextWithNoScript();
	}
	private void parseAttributes(){
		while(!isEnd){
			if (CharUtil.checkIsSlash(currentChar)) {//<F/> 形式标签
				currentTag.setTagType('F');
				incrementChar();
				getNextNonBlank();
			}
			if (CharUtil.checkIsRightQuota(currentChar)) {
				return ;
			}
			getAttributeName();
			getNextNonBlank();
			if (CharUtil.checkIsEqual(currentChar)) {
				incrementChar();
				getNextNonBlank();
				getAttributeValue();
				getNextNonBlank();
			}else {
				currentTag.getAttributeValueLinkedList().add(null);
			}
		}
	}
	private void getComment(){
		incrementChar();incrementChar();//忽略 --
        int index=0;
		while(isEnd){
			if(index==2){
			    if(currentChar=='>'){
			        break;
                }else{
			        index=kmpCommentNexts[index];
                }
            }else{
			    if(currentChar=="-->".charAt(index)){
			        incrementChar();
			        index++;
                }else {
                    if(index==0)
                        incrementChar();
			        index=kmpCommentNexts[index];
                }
            }
		}
	
	}
	private void getScript(){
	    int index=0;
        while(isEnd){//</script>
            if(index==8){
                if(CharUtil.checkIsRightQuota(currentChar)){
                    break;
                }else{
                    index=kmpScriptNexts[index];
                }
            }else{
                if(Character.toUpperCase(currentChar)==Character.toUpperCase("</SCRIPT>".charAt(index))){
                    incrementChar();
                    index++;
                }else{
                    if(index==0)
                        incrementChar();
                    index=kmpScriptNexts[index];
                }
            }
        }
	}
	private void getTextWithNoScript(){
		StringBuilder textBuilder=new StringBuilder();
		while(!isEnd){
			if (CharUtil.checkIsLeftQuota(currentChar)) {//
				if (textBuilder.toString().equals("")||textBuilder.toString()==null) {
					return ;
				}
				if (currentTag.getTagType()=='H') {
					currentTag.setText(textBuilder.toString());
				}else {
					textLinkedList.add(textBuilder.toString());
				}
				break;
			}else {
				textBuilder.append(currentChar);
				incrementChar();
			}
		}
	}
	private void getAttributeName(){
		StringBuilder attributeNameBuilder=new StringBuilder();
		while(!isEnd){//属性名称通常以下面的形式结束掉： <  attributeName=>等号 <  attributeName1 att2>空格 <  attributeName/> 斜杠 <  attributeName>右尖括号
			if (CharUtil.checkIsBlank(currentChar)||CharUtil.checkIsSlash(currentChar)||CharUtil.checkIsRightQuota(currentChar)||CharUtil.checkIsEqual(currentChar)) {
				currentTag.getAttributeNameLinkedList().add(attributeNameBuilder.toString());
				break;
			}else {
				attributeNameBuilder.append(currentChar);
				incrementChar();
			}
		}
	}
	private void getAttributeValue(){
		StringBuilder attributeValueBuilder=new StringBuilder();
		if (CharUtil.checkIsDot(currentChar)) {
			incrementChar();
			while(!isEnd){//属性值通常以下面的形式结束掉： <  attributeNamevalue >空格  <  attributeValue/> 斜杠 <  attributeValue>右尖括号
				if (CharUtil.checkIsDot(currentChar)||CharUtil.checkIsSlash(currentChar)||CharUtil.checkIsRightQuota(currentChar)) {
					if (attributeValueBuilder.toString()==null||attributeValueBuilder.toString().equals("")) {
						currentTag.getAttributeValueLinkedList().add("");
					}else {
						currentTag.getAttributeValueLinkedList().add(attributeValueBuilder.toString());
					}
					break;
				}else {
					if (CharUtil.checkIsNonSlash(currentChar)) {
						attributeValueBuilder.append(currentChar);
						incrementChar();
						attributeValueBuilder.append(currentChar);
					}else {
						attributeValueBuilder.append(currentChar);
					}
					incrementChar();
				}
			}
			incrementChar();
			getNextNonBlank();
		}else {
			while(!isEnd){
				if (CharUtil.checkIsBlank(currentChar)||CharUtil.checkIsSlash(currentChar)||CharUtil.checkIsRightQuota(currentChar)) {
					if (attributeValueBuilder.toString()==null||attributeValueBuilder.toString().equals("")) {
						currentTag.getAttributeValueLinkedList().add(null);
					}else {
						currentTag.getAttributeValueLinkedList().add(attributeValueBuilder.toString());
					}
					break;
				}else {
					attributeValueBuilder.append(currentChar);
					incrementChar();
				}
			}
		}
	}
	private void setCommentToTag(HtmlTag commentTag){
		if (tagLinkedList.getLast()==null) {
			commentTagLinkedList.add(currentTag);
		}else {
			tagLinkedList.getLast().getCommentTagLinkedList().add(commentTag);
		}
	}
	private void initialDocType(){
		getNextNonBlank();
		incrementChar();
		getNextNonBlank();
		if (CharUtil.checkIsWarn(currentChar)) {//是否为 DOCTYPE标签
			incrementChar();//!
			//doctype
			incrementChar();/*d*/incrementChar();/*o*/incrementChar();/*c*/incrementChar();/*t*/
			incrementChar();/*y*/incrementChar();/*p*/incrementChar();/*e*/
			getNextNonBlank();
			getDocType();
			incrementChar();//忽略 >
			getNextNonBlank();//得到下一个<
			incrementChar();//忽略掉 <
			getNextNonBlank();//得到标签的第一个非空值
		}
	}
	private void getTagName(){
		StringBuilder tagNameBuilder=new StringBuilder();
		while(!isEnd){
			if (CharUtil.checkIsBlank(currentChar)||CharUtil.checkIsRightQuota(currentChar)||CharUtil.checkIsSlash(currentChar)) {// 其他 截止符号 : 
				currentTag.setTagName(tagNameBuilder.toString());
				break;
			}else {
				tagNameBuilder.append(currentChar);
				incrementChar();
			}
		}
	}
	private void getDocType(){
		StringBuilder docTypeBuilder=new StringBuilder();
		while(!isEnd){
			if (CharUtil.checkIsBlank(currentChar)||CharUtil.checkIsRightQuota(currentChar)) {
				docType=docTypeBuilder.toString();
				break;
			}else {
				docTypeBuilder.append(currentChar);
				incrementChar();
			}
		}
	}
	private void getNextNonBlank(){
		while(!isEnd){
			if (!CharUtil.checkIsBlank(currentChar)) {
				break;
			}else {
				incrementChar();
			}
		}
	}
	protected abstract class ParserHelper{
		abstract void increment() throws IOException;
	}
	private class BufferedReaderParseHelper extends ParserHelper{

		private BufferedReader bufferedReader;
		
		 BufferedReaderParseHelper(BufferedReader bufferedReader) {
			this.bufferedReader=bufferedReader;
		}
		@Override
		void increment() throws IOException {
			char[] chars=new char[1];
			if (bufferedReader.read(chars)!=-1) {
				currentChar=chars[0];
			}else {
				isEnd=true;
				bufferedReader.close();
			}
		}
	}
	private class StringHtmlParseHelper extends ParserHelper{

		private char[] html;
		private int currentIndex;
		private int htmlLength;
		
		 StringHtmlParseHelper(String htmlStr) {
			html=htmlStr.toCharArray();
			htmlLength=html.length;
		}
		@Override
		void increment(){//获取游标的下一个标签，如果结束，则设置结束标志。
			currentIndex+=1;
			if (currentIndex>=htmlLength) {
				isEnd=true;
				return;
			}
			currentChar=html[currentIndex];
		}
	}
	private void incrementChar(){
		//increment and set the isEnd flag;
		try {
			pHelper.increment();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void setDocType(String docType) {
		this.docType = docType;
	};
}
