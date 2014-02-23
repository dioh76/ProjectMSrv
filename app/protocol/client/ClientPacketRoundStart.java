package protocol.client;

import protocol.ClientPacket;

public class ClientPacketRoundStart extends ClientPacket{
	
	public ClientPacketRoundStart()
	{
		proto = ClientPacket.MCP_CHAR_START_ROUND;
	}
}