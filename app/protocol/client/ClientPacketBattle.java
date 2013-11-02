package protocol.client;

import protocol.ClientPacket;

public class ClientPacketBattle extends ClientPacket{
	
	public int attackId;
	public int attackCard;
	public int zoneId;
	
	public ClientPacketBattle()
	{
		proto = ClientPacket.MCP_PLAYER_BATTLE;
	}
}