package protocol.server;

import protocol.ServerPacket;

public class ServerPacketEquipSpellUseReply extends ServerPacket{
	
	public int spellId;
	public int zoneId;
	public boolean use;
	
	public ServerPacketEquipSpellUseReply( int sender, int spellId, int zoneId, boolean use )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_EQUIP_SPELL_USE_REPLY;
		this.spellId = spellId;
		this.zoneId = zoneId;
		this.use = use;
	}
}