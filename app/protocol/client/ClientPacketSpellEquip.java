package protocol.client;

import protocol.ClientPacket;

public class ClientPacketSpellEquip extends ClientPacket{
	
	public int spellid;
	
	public ClientPacketSpellEquip()
	{
		proto = ClientPacket.MCP_SPELL_EQUIP;
	}
}