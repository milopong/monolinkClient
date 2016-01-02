package com.milopong.monolink.model;

import java.util.ArrayList;

public class FriendGroup  {

	private String Name;
	private ArrayList<Member> Items;

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		this.Name = name;
	}

	public ArrayList<Member> getItems() {
		return Items;
	}

	public void setItems(ArrayList<Member> Items) {
		this.Items = Items;
	}
	

}
