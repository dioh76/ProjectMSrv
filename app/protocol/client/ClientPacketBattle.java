package protocol.client;

import protocol.ClientPacket;

public class ClientPacketBattle extends ClientPacket{
	
	public int attackCard;
	public int zoneId;
	public int defenseCard;
	
	public ClientPacketBattle()
	{
		proto = ClientPacket.MCP_PLAYER_BATTLE;
	}
}