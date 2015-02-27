package com.kss.xchat.apiresponse;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RoomUsers {
	@JsonProperty("ID")
	public String ID;
	@JsonProperty("User")
	public String User;
	@JsonProperty("NickName")
	public String NickName;
	@JsonProperty("Name")
	public String Name;
}
