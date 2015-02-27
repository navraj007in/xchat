package com.kss.xchat.apiresponse;

import com.fasterxml.jackson.annotation.JsonProperty;

public class States {
	@JsonProperty("ID")
	public String ID;
	@JsonProperty("Country")
	public String Country= "";
	@JsonProperty("State1")
	public String State1= "";
	@JsonProperty("Name")
	public String Name= "";
	
}
