package models;

//import org.eclipse.jetty.util.ajax.JSONObjectConvertor;

import play.Logger;
import play.libs.Json;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.mvc.WebSocket;
import protocol.ClientPacket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import protocol.*;
import protocol.client.*;
import protocol.server.ServerPacketGameJoin;

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
			Logger.info(String.format("packet[user=%d] = %s",getUserId(), recvmsg.toString()));
			
			int protocol = recvmsg.get("proto").asInt();
			
			if( protocol == ClientPacket.MCP_GAME_JOIN )
			{
				ClientPacketGameJoin packet = Json.fromJson(recvmsg, ClientPacketGameJoin.class);
				//reply firstly
				SendPacket(new ServerPacketGameJoin(0,getUserId(),packet.maxuser).toJson());
				
				RoomManager.join(this, packet.maxuser);
			}
			else if( mGameRoom != null )
			{
				mGameRoom.processPacket(protocol, recvmsg);
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
