package com.kss.xchat.apiresponse;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TopRooms {
	@JsonProperty("ID")
	public String ID;
	@JsonProperty("RoomID")
	public String RoomID;
	@JsonProperty("RoomName")
	public String RoomName= "";
	@JsonProperty("UsersCount")
	public String UsersCount= "";

}
