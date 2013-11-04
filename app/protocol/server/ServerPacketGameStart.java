package protocol.server;

import protocol.ServerPacket;

public class ServerPacketGameStart extends ServerPacket{

	public int starter;
	
	public ServerPacketGameStart( int sender, int starter )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_GAME_START;
		this.starter = starter;
	}
}
