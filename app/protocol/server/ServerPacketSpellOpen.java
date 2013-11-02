package protocol.server;

import protocol.ServerPacket;

public class ServerPacketSpellOpen extends ServerPacket{
	
	public ServerPacketSpellOpen( int sender )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_SPELL_OPEN;
	}
}