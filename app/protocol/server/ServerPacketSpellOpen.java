package protocol.server;

import protocol.ServerPacket;

public class ServerPacketSpellOpen extends ServerPacket{
	
	public int spellId;
	
	public ServerPacketSpellOpen( int sender, int spellId )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_SPELL_OPEN;
		this.spellId = spellId;
	}
}