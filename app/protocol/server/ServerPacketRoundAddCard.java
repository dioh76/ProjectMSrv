package protocol.server;

import protocol.ServerPacket;

public class ServerPacketRoundAddCard extends ServerPacket{
	
	public ServerPacketRoundAddCard( int sender )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_ROUND_ADDCARD;
	}
}