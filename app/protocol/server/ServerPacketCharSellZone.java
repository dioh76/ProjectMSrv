package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharSellZone extends ServerPacket{

	public int toId;
	public float sumPay;
	
	public ServerPacketCharSellZone( int sender,int toCharId, float sumPay )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_SELL_ZONE;
		this.toId = toCharId;
		this.sumPay = sumPay;
	}
}
