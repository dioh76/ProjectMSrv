package protocol.client;

import protocol.ClientPacket;

public class ClientPacketZoneAmbush extends ClientPacket{
	
	public int 		zId;
	
	public ClientPacketZoneAmbush()
	{
		proto = ClientPacket.MCP_ZONE_AMBUSH;
	}
}
