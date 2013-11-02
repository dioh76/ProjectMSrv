package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharDirection extends ServerPacket{

	public boolean	forward;
	
	public ServerPacketCharDirection( int sender, boolean forward )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_DIRECTION;
		this.forward = forward;
	}
}

