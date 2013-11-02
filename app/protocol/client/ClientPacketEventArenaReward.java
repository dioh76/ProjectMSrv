package protocol.client;

import java.util.ArrayList;
import java.util.List;

import protocol.ClientPacket;

public class ClientPacketEventArenaReward extends ClientPacket{
	
	public int startplayer;
	public List<Integer> winners = new ArrayList<Integer>();
	public List<Integer> losers = new ArrayList<Integer>();

	public ClientPacketEventArenaReward()
	{
		proto = ClientPacket.MCP_EVENT_ARENA_REWARD;
	}
}