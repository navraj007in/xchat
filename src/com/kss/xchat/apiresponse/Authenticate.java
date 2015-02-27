package com.kss.xchat.apiresponse;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Authenticate {
	@JsonProperty("userExists")
	public String userExists;
	@JsonProperty("Email")
	public String Email;
	@JsonProperty("NickName")
	public String NickName;
	@JsonProperty("Name")
	public String Name;
	@JsonProperty("Gender")
	public String Gender;
	@JsonProperty("DOB")
	public String DOB;
	@JsonProperty("City")
	public String City;
	@JsonProperty("State")
	public String State;
	@JsonProperty("Country")
	public String Country;
	@JsonProperty("Token")
	public String Token;
	@JsonProperty("ProfileImage")
	public String ProfileImage;
	@JsonProperty("Status")
	public String Status;

}
