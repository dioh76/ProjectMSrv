package protocol.server;

import protocol.ServerPacket;

public class ServerPacketSpellUse extends ServerPacket{
	
	public int spellid;
	public int targetchar;
	public int targetzone;
	public int targetzone2;
	public boolean turnover;
	
	public ServerPacketSpellUse( int sender, int spellid, int targetchar, int targetzone, int targetzone2, boolean turnover )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_SPELLUSE;
		this.spellid = spellid;
		this.targetchar = targetchar;
		this.targetzone = targetzone;
		this.targetzone2 = targetzone2;
		this.turnover = turnover;
	}
}