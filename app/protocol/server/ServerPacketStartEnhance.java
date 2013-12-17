package protocol.server;

import protocol.ServerPacket;

public class ServerPacketStartEnhance extends ServerPacket{
	
	public ServerPacketStartEnhance( int sender )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_START_ENHANCE;
	}
}