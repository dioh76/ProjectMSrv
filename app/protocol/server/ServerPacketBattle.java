package protocol.server;

import protocol.ServerPacket;

public class ServerPacketBattle extends ServerPacket{
	
	public int attackCard;
	public int zoneId;
	public int attackDice;
	public int defenseDice;
	
	public ServerPacketBattle( int sender, int attackCard, int zoneId, int attackDice, int defenseDice )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_PLAYER_BATTLE;
		this.attackCard = attackCard;
		this.zoneId = zoneId;
		this.attackDice = attackDice;
		this.defenseDice = defenseDice;
	}
}