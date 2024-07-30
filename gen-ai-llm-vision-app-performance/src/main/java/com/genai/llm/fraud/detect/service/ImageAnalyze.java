package com.genai.llm.fraud.detect.service;

public class ImageAnalyze 
{

	String systemMsg = " You are a helpful assistant. ";
	String text;
	String imgSrc;
	Float temperature = 0.4f;
	
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getImgSrc() {
		return imgSrc;
	}
	public void setImgSrc(String imgSrc) {
		this.imgSrc = imgSrc;
	}
	
	public Float getTemperature() {
		return temperature;
	}
	public void setTemperature(Float temperature) {
		this.temperature = temperature;
	}
	
	public String getSystemMsg() {
		return systemMsg;
	}
	public void setSystemMsg(String systemMsg) {
		this.systemMsg = systemMsg;
	}		
}
