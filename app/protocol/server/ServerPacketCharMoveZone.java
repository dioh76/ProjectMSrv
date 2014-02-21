package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharMoveZone extends ServerPacket{
	
	public int zId;
	
	public ServerPacketCharMoveZone( int sender, int zoneId )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_MOVE_ZONE;
		this.zId = zoneId;
	}
}
