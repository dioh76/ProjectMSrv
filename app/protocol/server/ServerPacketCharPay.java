package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharPay extends ServerPacket{
	
	public int zId;
	
	public ServerPacketCharPay( int sender, int zoneId )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_PAY;
		this.zId = zoneId;
	}
}
