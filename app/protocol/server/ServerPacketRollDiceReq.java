package protocol.server;

import protocol.ServerPacket;

public class ServerPacketRollDiceReq extends ServerPacket{
	
	public ServerPacketRollDiceReq( int sender )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_ROLL_DICE_REQ;
	}
}