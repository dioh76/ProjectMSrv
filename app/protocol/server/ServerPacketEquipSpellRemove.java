package protocol.server;

import protocol.ServerPacket;

public class ServerPacketEquipSpellRemove extends ServerPacket{
	
	public int spellId;
	
	public ServerPacketEquipSpellRemove( int sender, int spellId )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_EQUIP_SPELL_REMOVE;
		this.spellId = spellId;
	}
}