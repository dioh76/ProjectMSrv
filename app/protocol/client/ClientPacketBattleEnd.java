package protocol.client;

import protocol.ClientPacket;

public class ClientPacketBattleEnd extends ClientPacket{
	
	public boolean attackwin;

	public ClientPacketBattleEnd()
	{
		proto = ClientPacket.MCP_PLAYER_BATTLE_END;
	}
}