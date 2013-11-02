package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharPassByStart extends ServerPacket{
	
	public ServerPacketCharPassByStart( int sender )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_PASSBY_START;
	}
}
