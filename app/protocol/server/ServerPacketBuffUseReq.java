package protocol.server;

import protocol.ServerPacket;

public class ServerPacketBuffUseReq extends ServerPacket{
	
	public int buffId;
	
	public ServerPacketBuffUseReq( int sender, int buffId )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_BUFF_USE_REQ;
		this.buffId = buffId;

	}
}