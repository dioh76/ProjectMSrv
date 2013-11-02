package protocol.server;

import protocol.ServerPacket;

public class ServerPacketGameStart extends ServerPacket{

	public int startIdx;
	
	public ServerPacketGameStart( int sender, int startIdx )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_GAME_START;
		this.startIdx = startIdx;
	}
}
