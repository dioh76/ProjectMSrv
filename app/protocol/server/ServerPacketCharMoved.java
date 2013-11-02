package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharMoved extends ServerPacket{
	
	public ServerPacketCharMoved( int sender )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_MOVED;
	}
}
