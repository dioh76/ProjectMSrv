package protocol.client;

import protocol.ClientPacket;

public class ClientPacketEventGamble extends ClientPacket{
	
	public int index;
	
	public ClientPacketEventGamble()
	{
		proto = ClientPacket.MCP_EVENT_GAMBLE;
	}
}