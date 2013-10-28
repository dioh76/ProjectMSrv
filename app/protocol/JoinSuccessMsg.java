package protocol;

import play.libs.Json;
import com.fasterxml.jackson.databind.JsonNode;

public class JoinSuccessMsg {

	public String protocol = "joinsuccess";
	public long roomId = 0;
	
	public JoinSuccessMsg(long roomId)
	{
		this.roomId = roomId;
	}
	
	public JsonNode toJson()
	{
		return Json.toJson(this);
	}
}
