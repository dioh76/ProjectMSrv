package protocol.server;

import protocol.ServerPacket;

public class ServerPacketBattle extends ServerPacket{
	
	public ServerPacketBattle( int sender )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_PLAYER_BATTLE;
	}
}