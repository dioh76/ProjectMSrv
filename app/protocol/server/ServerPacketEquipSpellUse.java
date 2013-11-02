package protocol.server;

import protocol.ServerPacket;

public class ServerPacketEquipSpellUse extends ServerPacket{
	
	public int spellType;
	
	public ServerPacketEquipSpellUse( int sender, int spellType )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_EQUIP_SPELL_USE;
		this.spellType = spellType;
	}
}