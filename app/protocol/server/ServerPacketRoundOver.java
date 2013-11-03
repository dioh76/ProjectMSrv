package protocol.server;

import protocol.ServerPacket;

public class ServerPacketRoundOver extends ServerPacket{
	
	public int nextChar;
	
	public ServerPacketRoundOver( int sender, int nextChar )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_ROUND_OVER;
		this.nextChar = nextChar;

	}
}