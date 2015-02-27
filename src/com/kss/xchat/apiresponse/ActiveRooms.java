package com.kss.xchat.apiresponse;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ActiveRooms {
	@JsonProperty("RoomId")
	public String RoomId;
	@JsonProperty("RoomName")
	public String RoomName= "";
	@JsonProperty("RoomUsers")
	public String RoomUsers= "";
}
