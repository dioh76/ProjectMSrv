package protocol.client;

import protocol.ClientPacket;

public class ClientPacketGameJoin extends ClientPacket{
	
	public int maxuser;
	
	public ClientPacketGameJoin()
	{
		proto = ClientPacket.MCP_GAME_JOIN;
	}

}
