package org.pangaea.agrigrid.service.agriculture.video.servlet;

import java.util.List;

public class TranslateRequestJson {
	public void setTimes(List<Long[]> times) {
		this.times = times;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public List<Long[]> getTimes() {
		return times;
	}
	public String getToken() {
		return token;
	}
	private String token;
	private List<Long[]> times;
}
