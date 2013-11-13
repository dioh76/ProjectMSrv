package protocol.client;

import protocol.ClientPacket;

public class ClientPacketCharEnhance extends ClientPacket{
	
	public int zId;
	
	public ClientPacketCharEnhance()
	{
		proto = ClientPacket.MCP_CHAR_ENHANCE;
	}
}