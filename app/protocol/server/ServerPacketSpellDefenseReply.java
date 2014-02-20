package protocol.server;

import protocol.ServerPacket;

public class ServerPacketSpellDefenseReply extends ServerPacket{
	
	public int defender;
	public int spellid;
	public int targetchar;
	public int targetzone;
	public int targetzone2;
	public boolean use;
	public boolean turnover;
	
	public ServerPacketSpellDefenseReply( int sender, int defender, int spellid, int targetchar, int targetzone, int targetzone2, boolean use, boolean turnover)
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_SPELLDEFENSE_REPLY;
		this.defender = defender;
		this.spellid = spellid;
		this.targetchar = targetchar;
		this.targetzone = targetzone;
		this.targetzone2 = targetzone2;
		this.use = use;
		this.turnover = turnover;
	}
}