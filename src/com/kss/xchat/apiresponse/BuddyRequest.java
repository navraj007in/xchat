package com.kss.xchat.apiresponse;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BuddyRequest {


	@JsonProperty("NickName")
	public String NickName;
	@JsonProperty("ID")
	public String ID;
	@JsonProperty("Name")
	public String Name;
	@JsonProperty("Gender")
	public String Gender;
	@JsonProperty("City")
	public String City;
	@JsonProperty("Country")
	public String Country;

	@JsonProperty("Message")
	public String Message;

	@JsonProperty("ProfileImage")
	public String ProfileImage;

}
