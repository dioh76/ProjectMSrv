package protocol.client;

import protocol.ClientPacket;

public class ClientPacketStartEnhance extends ClientPacket{
	
	public ClientPacketStartEnhance()
	{
		proto = ClientPacket.MCP_START_ENHANCE;
	}
}