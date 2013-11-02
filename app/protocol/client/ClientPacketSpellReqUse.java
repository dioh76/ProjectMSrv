package protocol.client;

import protocol.ClientPacket;

public class ClientPacketSpellReqUse extends ClientPacket{
	
	public int spellid;
	
	public ClientPacketSpellReqUse()
	{
		proto = ClientPacket.MCP_SPELL_REQ_USE;
	}
}