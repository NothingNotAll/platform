package nna.base.util;

import java.util.LinkedList;

public class HtmlTag {
	private char tagType;
	private String tagName;
	private LinkedList<String> attributeNameLinkedList;
	private LinkedList<String> attributeValueLinkedList;
	private LinkedList<String> commentLinkedList=new LinkedList<String>();
	private LinkedList<HtmlTag> commentTagLinkedList=new LinkedList<HtmlTag>();
	private LinkedList<String> scriptLinkedList=new LinkedList<String>();
	private String text;
	
	public HtmlTag(){
		initialTag();
	}
	public char getTagType() {
		return tagType;
	}
	public void setTagType(char tagType) {
		this.tagType = tagType;
	}
	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	public LinkedList<String> getAttributeNameLinkedList() {
		return attributeNameLinkedList;
	}
	public void setAttributeNameLinkedList(LinkedList<String> attributeNameLinkedList) {
		this.attributeNameLinkedList = attributeNameLinkedList;
	}
	public LinkedList<String> getAttributeValueLinkedList() {
		return attributeValueLinkedList;
	}
	public void setAttributeValueLinkedList(LinkedList<String> attributeValueLinkedList) {
		this.attributeValueLinkedList = attributeValueLinkedList;
	}
	public LinkedList<String> getCommentLinkedList() {
		return commentLinkedList;
	}
	public void setCommentLinkedList(LinkedList<String> commentLinkedList) {
		this.commentLinkedList = commentLinkedList;
	}
	public LinkedList<String> getScriptLinkedList() {
		return scriptLinkedList;
	}
	public void setScriptLinkedList(LinkedList<String> scriptLinkedList) {
		this.scriptLinkedList = scriptLinkedList;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	private void initialTag() {
		commentLinkedList=new LinkedList<String>();
		attributeNameLinkedList=new LinkedList<String>();
		attributeValueLinkedList=new LinkedList<String>();
		commentLinkedList=new LinkedList<String>();
		scriptLinkedList=new LinkedList<String>();
	}
	public LinkedList<HtmlTag> getCommentTagLinkedList() {
		return commentTagLinkedList;
	}
	public void setCommentTagLinkedList(LinkedList<HtmlTag> commentTagLinkedList) {
		this.commentTagLinkedList = commentTagLinkedList;
	}
	
}
