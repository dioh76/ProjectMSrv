package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharTurnSkip extends ServerPacket{
	
	public ServerPacketCharTurnSkip( int sender )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_TURN_SKIP;
	}
}