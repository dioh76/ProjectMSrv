package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharBankruptReq extends ServerPacket{

	public ServerPacketCharBankruptReq( int sender )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_BANKRUPT_REQ;
	}
}
