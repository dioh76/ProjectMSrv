package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCardChange extends ServerPacket{
	
	public int zId;
	public int idx;
	public int cId;
	public int prevCId;
	
	public ServerPacketCardChange( int sender, int zoneId, int index, int cardId, int prevCardId )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CARD_CHANGE;
		this.zId = zoneId;
		this.idx = index;
		this.cId = cardId;
		this.prevCId = prevCardId;
	}
}
