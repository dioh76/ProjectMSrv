package protocol.server;

import protocol.ServerPacket;

public class ServerPacketZoneAmbush extends ServerPacket{
	
	public int zId;
	public boolean enable;
	
	public ServerPacketZoneAmbush( int sender, int zoneId, boolean enable)
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_ZONE_AMBUSH;
		this.zId = zoneId;
		this.enable = enable;
	}
}