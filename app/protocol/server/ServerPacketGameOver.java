package protocol.server;

import protocol.ServerPacket;

public class ServerPacketGameOver extends ServerPacket{
	
	public ServerPacketGameOver( int sender )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_GAME_OVER;
	}
}
