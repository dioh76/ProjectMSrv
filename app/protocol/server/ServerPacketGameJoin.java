package protocol.server;

import protocol.*;

public class ServerPacketGameJoin  extends ServerPacket{

	public long userId;
	public String userName;
	public int maxuser;
	public boolean server;
	public boolean host;
	
	public ServerPacketGameJoin( int sender, long userId, String userName, int maxuser, boolean host )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_GAME_JOIN;
		this.userId = userId;
		this.userName = userName;
		this.maxuser = maxuser;
		this.server = true;
		this.host = host;
	}
}
