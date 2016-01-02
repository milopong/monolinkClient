package com.milopong.monolink.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Event implements Serializable {
	private static final long serialVersionUID = 1L;
	private int no;
	private String name;
	private String startTime;
	private String endTime;
	private String place;
	private String detailPlace;
	private String longitude;
	private String latitude;
	private ArrayList<Member> participants;
	private String tag;
	private String memo;
	private String photo;
	private String company;
	private String bookmark;
	
	
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getDetailPlace() {
		return detailPlace;
	}
	public void setDetailPlace(String detailPlace) {
		this.detailPlace = detailPlace;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public ArrayList<Member> getParticipants() {
		return participants;
	}
	public void setParticipants(ArrayList<Member> participants) {
		this.participants = participants;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getBookmark() {
		return bookmark;
	}
	public void setBookmark(String bookmark) {
		this.bookmark = bookmark;
	}
	
}