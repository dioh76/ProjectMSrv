package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCardChange extends ServerPacket{
	
	public int zId;
	public int idx;
	public int cId;
	
	public ServerPacketCardChange( int sender, int zoneId, int index, int cardId )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CARD_CHANGE;
		this.zId = zoneId;
		this.idx = index;
		this.cId = cardId;
	}
}
