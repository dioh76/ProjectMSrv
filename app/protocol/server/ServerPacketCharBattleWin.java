package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharBattleWin extends ServerPacket{
	
	public int dfChr;
	public int zId;
	public int atCrd;

	public ServerPacketCharBattleWin( int sender, int defenseId, int zoneId, int attackCard )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_BATTLE_WIN;
		this.dfChr = defenseId;
		this.zId = zoneId;
		this.atCrd = attackCard;
	}
}
