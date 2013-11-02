package protocol.server;

import protocol.*;

public class ServerPacketGameJoin  extends ServerPacket{

	public long userId;
	public int maxuser;
	public boolean server;
	
	public ServerPacketGameJoin( int sender, long userId, int maxuser )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_GAME_JOIN;
		this.userId = userId;
		this.maxuser = maxuser;
		this.server = true;
	}
}
