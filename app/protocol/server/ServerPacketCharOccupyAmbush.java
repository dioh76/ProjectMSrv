package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharOccupyAmbush extends ServerPacket{
	
	public int zId;
	public int idx;
	public int cId;
	
	public ServerPacketCharOccupyAmbush( int sender, int zoneId, int index, int cardId )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_OCCUPY_AMBUSH;
		this.zId = zoneId;
		this.idx = index;
		this.cId = cardId;
	}
}
