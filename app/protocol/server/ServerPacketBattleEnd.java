package protocol.server;

import protocol.ServerPacket;

public class ServerPacketBattleEnd extends ServerPacket{
	
	public int attackId;
	public int attackCard;
	public boolean attackwin;
	public int	defenderId;
	public int	zoneId;
	
	public ServerPacketBattleEnd( int sender, int attackId, int attackCard, boolean attackwin, int defenderId, int zoneId )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_PLAYER_BATTLE_END;
		this.attackId = attackId;
		this.attackCard = attackCard;
		this.attackwin = attackwin;
		this.defenderId = defenderId;
		this.zoneId = zoneId;
	}
}