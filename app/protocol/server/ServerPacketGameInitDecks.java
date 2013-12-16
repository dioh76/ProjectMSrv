package protocol.server;

import protocol.ServerPacket;

public class ServerPacketGameInitDecks extends ServerPacket{
	
	public ServerPacketGameInitDecks( int sender )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_GAME_INITDECKS;
	}
}
