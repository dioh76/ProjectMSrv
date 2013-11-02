package protocol.client;

import protocol.ClientPacket;

public class ClientPacketCharMoved extends ClientPacket{
	
	public ClientPacketCharMoved()
	{
		proto = ClientPacket.MCP_CHAR_MOVED;
	}
}
