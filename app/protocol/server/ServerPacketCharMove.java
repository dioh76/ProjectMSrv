package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharMove extends ServerPacket{

	public int move;
	
	public ServerPacketCharMove( int sender, int move )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_MOVE;
		this.move = move;
	}
}
