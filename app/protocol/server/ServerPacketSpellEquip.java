package protocol.server;

import protocol.ServerPacket;

public class ServerPacketSpellEquip extends ServerPacket{
	
	public int spellid;
	
	public ServerPacketSpellEquip( int sender, int spellid )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_SPELL_EQUIP;
		this.spellid = spellid;
	}
}