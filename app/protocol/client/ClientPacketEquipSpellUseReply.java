package protocol.client;

import protocol.ClientPacket;

public class ClientPacketEquipSpellUseReply extends ClientPacket{
	
	public int spellId;
	public boolean use;

	public ClientPacketEquipSpellUseReply()
	{
		proto = ClientPacket.MCP_EQUIP_SPELL_USE_REPLY;
	}
}