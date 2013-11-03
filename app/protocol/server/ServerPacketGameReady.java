package protocol.server;

import protocol.ServerPacket;

public class ServerPacketGameReady extends ServerPacket{
	
	public ServerPacketGameReady( int sender )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_GAME_READY;
	}
}
