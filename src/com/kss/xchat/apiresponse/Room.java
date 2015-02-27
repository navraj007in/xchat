package com.kss.xchat.apiresponse;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Room {
	@JsonProperty("ID")
	public String ID;
	@JsonProperty("RoomName")
	public String RoomName;
	@JsonProperty("UsersCount")
	public String UsersCount;

}
