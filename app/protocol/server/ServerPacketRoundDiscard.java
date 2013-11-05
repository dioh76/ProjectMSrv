package protocol.server;

import protocol.ServerPacket;

public class ServerPacketRoundDiscard extends ServerPacket{
	
	public ServerPacketRoundDiscard( int sender )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_ROUND_DISCARD;
	}
}