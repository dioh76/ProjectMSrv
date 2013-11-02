package protocol.client;

import protocol.ClientPacket;

public class ClientPacketSpellOpen extends ClientPacket{
	
	public ClientPacketSpellOpen()
	{
		proto = ClientPacket.MCP_SPELL_OPEN;
	}
}