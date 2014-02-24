package protocol.server;

import protocol.ServerPacket;

public class ServerPacketStartEnhance extends ServerPacket{
	
	public int	zId;
	
	public ServerPacketStartEnhance( int sender, int zoneId )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_START_ENHANCE;
		this.zId = zoneId;
	}
}