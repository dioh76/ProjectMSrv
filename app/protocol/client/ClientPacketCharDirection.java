package protocol.client;

import protocol.ClientPacket;

public class ClientPacketCharDirection extends ClientPacket{
	
	public boolean forward;
	
	public ClientPacketCharDirection()
	{
		proto = ClientPacket.MCP_CHAR_DIRECTION;
	}
}

