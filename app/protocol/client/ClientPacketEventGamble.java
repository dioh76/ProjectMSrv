package protocol.client;

import protocol.ClientPacket;

public class ClientPacketEventGamble extends ClientPacket{
	
	public int race;
	
	public ClientPacketEventGamble()
	{
		proto = ClientPacket.MCP_EVENT_GAMBLE;
	}
}