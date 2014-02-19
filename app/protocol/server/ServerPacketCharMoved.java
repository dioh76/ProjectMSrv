package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharMoved extends ServerPacket{
	
	public int zId;
	
	public ServerPacketCharMoved( int sender, int zoneId )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_MOVED;
		this.zId = zoneId;
	}
}
