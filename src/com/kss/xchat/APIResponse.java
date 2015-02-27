package com.kss.xchat;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kss.xchat.apiresponse.RoomUsers;

public class APIResponse {
	@JsonProperty("UserName")
	public String UserName;
	@JsonProperty("Password")
	public String PASSWORD = "";
	@JsonProperty("Token")
	public String Token = "";
	@JsonProperty("HTTPResponse")
	public String HttpResponse = "";
	@JsonProperty("subResponse")
	public String subResponse = "";
	@JsonProperty("RoomUsers")
	public ArrayList<RoomUsers> roomUsers;

}
