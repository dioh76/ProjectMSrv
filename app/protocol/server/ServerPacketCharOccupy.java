package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharOccupy extends ServerPacket{
	
	public int zId;
	public int idx;
	public int cId;
	
	public ServerPacketCharOccupy( int sender, int zoneId, int index, int cardId )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_OCCUPY;
		this.zId = zoneId;
		this.idx = index;
		this.cId = cardId;
	}
}
