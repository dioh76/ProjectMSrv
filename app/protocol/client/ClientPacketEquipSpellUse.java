package protocol.client;

import protocol.ClientPacket;

public class ClientPacketEquipSpellUse extends ClientPacket{
	
	public int spellType;

	public ClientPacketEquipSpellUse()
	{
		proto = ClientPacket.MCP_EQUIP_SPELL_USE;
	}
}