package org.pangaea.agrigrid.service.agriculture.video;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;

import jp.go.nict.langrid.commons.util.CalendarUtil;

import org.pangaea.agrigrid.service.agriculture.dao.entity.Subtitle;

public class SubtitleUtil {
	public static String makeTimedText(Set<Subtitle> set){
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><tt xml:lang=\"en\" xmlns=\"http://www.w3.org/2006/04/ttaf1\"xmlns:tts=\"http://www.w3.org/2006/04/ttaf1#styling\"><head></head><body><div xml:lang=\"en\">");
		String pointTime = "00:00:00.00";
		for(Subtitle s : set){
			sb.append("<p begin=\"");
			sb.append(pointTime);
			sb.append("\"");
			sb.append(" dur=\"");
			pointTime = makeTimeSting(s.getStartMillis());
			sb.append(pointTime);
			sb.append("\">");
			sb.append(s.getText());
			sb.append("</p>");
		}
		
		sb.append("</div></body></tt>");
		return sb.toString();
	}
	
	public static String makeTimeSting(long time){
		Calendar c = CalendarUtil.createBeginningOfDay(Calendar.getInstance());
		c.add(Calendar.SECOND, (int)time);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SS");
		return sdf.format(c.getTime());
	}
}
     
