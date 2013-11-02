package protocol.server;

import protocol.ServerPacket;

public class ServerPacketSpellReqUse extends ServerPacket{
	
	public int spellid;
	
	public ServerPacketSpellReqUse( int sender, int spellid )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_SPELL_REQ_USE;
		this.spellid = spellid;
	}
}