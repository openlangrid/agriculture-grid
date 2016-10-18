package org.pangaea.agrigrid.service.agriculture.video.servlet;

public class TranslateResultJson {
	public String[] getTexts() {
		return texts;
	}
	public void setTexts(String[] texts) {
		this.texts = texts;
	}
	public boolean isFinish() {
		return isFinish;
	}
	public void setFinish(boolean isFinish) {
		this.isFinish = isFinish;
	}
	String[] texts;
	boolean isFinish = false;
}
