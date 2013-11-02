package protocol.server;

import protocol.ServerPacket;

public class ServerPacketSpellDefense extends ServerPacket{
	
	public int defender;
	
	public ServerPacketSpellDefense( int sender, int defender)
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_SPELLDEFENSE;
		this.defender = defender;
	}
}