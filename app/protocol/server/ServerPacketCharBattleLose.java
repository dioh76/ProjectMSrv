package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharBattleLose extends ServerPacket{
	
	public int dfChr;
	public int zId;
	public boolean useSpell;
	public int val;
	public int sum;

	public ServerPacketCharBattleLose( int sender, int defenseId, int zoneId, boolean useSpell, int value, int sum )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_BATTLE_LOSE;
		this.dfChr = defenseId;
		this.zId = zoneId;
		this.useSpell = useSpell;
		this.val = value;
		this.sum = sum;
	}
}
