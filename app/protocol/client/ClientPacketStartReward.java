package protocol.client;

import protocol.ClientPacket;

public class ClientPacketStartReward extends ClientPacket{
	
	public boolean use;
	public int targetzone;
	
	public ClientPacketStartReward()
	{
		proto = ClientPacket.MCP_START_REWARD;
	}
}