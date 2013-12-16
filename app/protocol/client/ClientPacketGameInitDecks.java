package protocol.client;

import protocol.ClientPacket;

public class ClientPacketGameInitDecks extends ClientPacket{
	
	public long uId;
	
	public ClientPacketGameInitDecks()
	{
		proto = ClientPacket.MCP_GAME_INITDECKS;
	}

}
