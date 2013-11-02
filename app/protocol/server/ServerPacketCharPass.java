package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharPass extends ServerPacket{
	
	public ServerPacketCharPass( int sender )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_PASS;
	}
}