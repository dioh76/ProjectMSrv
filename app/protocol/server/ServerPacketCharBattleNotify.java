package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharBattleNotify extends ServerPacket{
	
	public int attId;
	public int defId;

	public ServerPacketCharBattleNotify( int sender, int attackId, int defenseId )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_BATTLE_NOTIFY;
		this.attId = attackId;
		this.defId = defenseId;
	}
}
