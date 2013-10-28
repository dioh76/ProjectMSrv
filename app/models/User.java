package models;

//import org.eclipse.jetty.util.ajax.JSONObjectConvertor;

import play.Logger;
import play.libs.Json;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.mvc.WebSocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class User implements Callback<JsonNode>,Callback0 {
	
	private long mUserId;
	private String mUserName;
	private GameRoom mGameRoom;
	private WebSocket.Out<JsonNode> mChannel;
	
	public User(long userId, String username, WebSocket.Out<JsonNode> channel)
	{
		mUserId = userId;
		mUserName = username;
		mChannel = channel;
	}
	
	public long getUserId()
	{
		return mUserId;
	}
	
	public String getName()
	{
		return mUserName;
	}
	
	public GameRoom getGameRoom()
	{
		return mGameRoom;
	}
	
	public void setGameRoom(GameRoom room)
	{
		mGameRoom = room;
	}
	
	public WebSocket.Out<JsonNode> getChannel()
	{
		return mChannel;
	}
	
	public void SendPacket(JsonNode node)
	{
		mChannel.write(node);
	}

	@Override
	public void invoke(JsonNode recvmsg) throws Throwable {

		try
		{
			String protocol = recvmsg.get("proto").asText();
			Logger.info("proto="+protocol);
			
			
//			JSONObjectConvertor a = new JSONObjectConvertor();
//			a.toJSON(arg0, arg1)
			
			if(protocol.equalsIgnoreCase("joinroom"))
			{
				RoomManager.join(this, 4);
			}
			else if(protocol.equalsIgnoreCase("leaveroom"))
			{
				RoomManager.leave(this, mGameRoom.getRoomId());
			}
			else
			{
				//switch( type == )
				ObjectNode event = Json.newObject();
		        //event.put("kind", kind);
		        event.put("user", mUserName);
		        event.put("id", mUserId);
		        mChannel.write(event);
			}        
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void invoke() throws Throwable {

		Logger.info("disconnect user="+getUserId());
		//TODO : process disconnect
		UserManager.getInstance().onDisconnect(this);
	}

}
