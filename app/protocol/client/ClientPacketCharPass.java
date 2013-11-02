package protocol.client;

import protocol.ClientPacket;

public class ClientPacketCharPass extends ClientPacket{
	
	public ClientPacketCharPass()
	{
		proto = ClientPacket.MCP_CHAR_PASS;
	}
}
