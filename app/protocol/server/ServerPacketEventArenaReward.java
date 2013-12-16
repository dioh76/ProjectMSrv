package protocol.server;

import java.util.List;

import protocol.ServerPacket;

public class ServerPacketEventArenaReward extends ServerPacket{
	
	public int startplayer;
	public List<Integer> winners;
	public List<Integer> losers;
	
	public ServerPacketEventArenaReward( int sender, int startplayer, List<Integer> winners, List<Integer> losers )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_EVENT_ARENA_REWARD;
		this.startplayer = startplayer;
		this.winners = winners;
		this.losers = losers;
	}
}