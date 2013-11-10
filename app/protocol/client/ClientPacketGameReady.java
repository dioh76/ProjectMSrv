package protocol.client;

import protocol.ClientPacket;

public class ClientPacketGameReady extends ClientPacket{
	
	public boolean useAI;
	
	public ClientPacketGameReady()
	{
		proto = ClientPacket.MCP_GAME_READY;
	}

}
