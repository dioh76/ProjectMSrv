package protocol.client;

import protocol.ClientPacket;

public class ClientPacketSpellDefenseReply extends ClientPacket{
	
	public int defender;
	public boolean use;
	
	public ClientPacketSpellDefenseReply()
	{
		proto = ClientPacket.MCP_SPELLDEFENSE_REPLY;
	}
}