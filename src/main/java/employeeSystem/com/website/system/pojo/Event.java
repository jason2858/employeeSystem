package com.yesee.gov.website.pojo;

import org.springframework.stereotype.Component;

//for setting & passing data to fullcalendar
@Component
public class Event {
	
	private String title;
	
	private Integer type;
	
	private String start;
	
	private String end;
	
	private String description;
	
	private String note;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
