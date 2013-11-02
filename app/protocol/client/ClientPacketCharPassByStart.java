package protocol.client;

import protocol.ClientPacket;

public class ClientPacketCharPassByStart extends ClientPacket{
	
	public ClientPacketCharPassByStart()
	{
		proto = ClientPacket.MCP_CHAR_PASSBY_START;
	}
}
