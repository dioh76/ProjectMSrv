package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharEnhance extends ServerPacket{
	
	public ServerPacketCharEnhance( int sender )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_ENHANCE;
	}
}
