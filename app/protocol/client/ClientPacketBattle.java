package protocol.client;

import protocol.ClientPacket;

public class ClientPacketBattle extends ClientPacket{
	
	public int idx;
	public int atId;
	public int zId;
	public int dfId;
	
	public ClientPacketBattle()
	{
		proto = ClientPacket.MCP_PLAYER_BATTLE;
	}
}