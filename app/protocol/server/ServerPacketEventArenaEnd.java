package protocol.server;

import java.util.List;

import protocol.ServerPacket;

public class ServerPacketEventArenaEnd extends ServerPacket{
	
	public int startPlayer;
	public List<Integer> winners;
	public List<Integer> losers;
	
	public ServerPacketEventArenaEnd( int sender, int startPlayer, List<Integer> winners, List<Integer> losers)
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_EVENT_ARENA_END;
		this.startPlayer = startPlayer;
		this.winners = winners;
		this.losers = losers;
	}
}