package com.kss.xchat.apiresponse;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Roster {
	@JsonProperty("ID")
	public String ID;
	@JsonProperty("NickName")
	public String NickName= "";
	@JsonProperty("Name")
	public String Name= "";
	@JsonProperty("Gender")
	public String Gender= "";
	@JsonProperty("City")
	public String City= "";
	@JsonProperty("State")
	public String State= "";
	@JsonProperty("Zip")
	public String Zip= "";
	@JsonProperty("Country")
	public String Country= "";
	@JsonProperty("ProfileImage")
	public String ProfileImage= "";
	@JsonProperty("Status")
	public String Status= "";
	@JsonProperty("Email")
	public String Email= "";
	
	@JsonProperty("Password")
	public String Password= "";
	@JsonProperty("DOB")
	public String DOB= "";
	@JsonProperty("isActive")
	public String isActive= "";
	@JsonProperty("Confirmed")
	public String Confirmed= "";
	@JsonProperty("ConfCode")
	public String ConfCode= "";
	@JsonProperty("DeviceID")
	public String DeviceID= "";
	@JsonProperty("AccessToken")
	public String AccessToken= "";


}
