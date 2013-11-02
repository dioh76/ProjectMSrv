package protocol.client;

import protocol.ClientPacket;

public class ClientPacketPortalUse extends ClientPacket{
	
	public int targetzone;
	
	public ClientPacketPortalUse()
	{
		proto = ClientPacket.MCP_PORTAL_USE;
	}
}