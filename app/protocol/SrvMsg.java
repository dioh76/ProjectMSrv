package protocol;

import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;

public class SrvMsg {

	public String proto;
	
	public JsonNode toJson()
	{
		return Json.toJson(this);
	}	
}
