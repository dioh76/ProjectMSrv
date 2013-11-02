package protocol.client;

import protocol.ClientPacket;

public class ClientPacketEventArenaUse extends ClientPacket{
	
	public int index;
	public int card;

	public ClientPacketEventArenaUse()
	{
		proto = ClientPacket.MCP_EVENT_ARENA_USE;
	}
}