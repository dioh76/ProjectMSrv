package protocol.server;

import java.util.List;

import protocol.ServerPacket;

public class ServerPacketGameReady extends ServerPacket{
	
	public List<Integer> charIds;
	
	public ServerPacketGameReady( int sender, List<Integer> charIds)
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_GAME_READY;
		this.charIds = charIds;
	}
}
