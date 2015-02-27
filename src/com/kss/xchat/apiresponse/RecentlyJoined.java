package com.kss.xchat.apiresponse;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecentlyJoined {
	@JsonProperty("ID")
	public String ID;
	@JsonProperty("Name")
	public String Name;
	@JsonProperty("NickName")
	public String NickName;
	@JsonProperty("City")
	public String City;
	@JsonProperty("State")
	public String State;
	@JsonProperty("Country")
	public String Country;
	@JsonProperty("ProfileImage")
	public String ProfileImage;
	@JsonProperty("DOJStamp")
	public String DOJStamp;
	@JsonProperty("DOJ")
	public String DOJ;
	@JsonProperty("RoomName")
	public String RoomName;
}
