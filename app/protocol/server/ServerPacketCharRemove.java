package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharRemove extends ServerPacket{

	public long 	userId;
	
	public ServerPacketCharRemove( int sender, long userId )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_REMOVE;
		this.userId = userId;
	}
}
