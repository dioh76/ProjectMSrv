package protocol.client;

import protocol.ClientPacket;

public class ClientPacketSpellDefense extends ClientPacket{
	
	public int defender;
	
	public ClientPacketSpellDefense()
	{
		proto = ClientPacket.MCP_SPELLDEFENSE;
	}
}