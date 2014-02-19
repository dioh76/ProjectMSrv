package protocol.server;

import protocol.ServerPacket;

public class ServerPacketBattle extends ServerPacket{
	
	public int idx;
	public int dfChr;
	public int atCrd;
	public int dfCrd;
	public int zId;
	public float tHp;
	public float tSt;
	public boolean atWin;
	
	public ServerPacketBattle( int sender, int index, int defenseChr, int attackCard, int defenseCard, int zoneId, float totalHp, float totalSt, boolean attackWin )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_BATTLE;
		this.idx = index;
		this.dfChr = defenseChr;
		this.atCrd = attackCard;
		this.dfCrd = defenseCard;
		this.zId = zoneId;
		this.tHp = totalHp;
		this.tSt = totalSt;
		this.atWin = attackWin;
	}
}