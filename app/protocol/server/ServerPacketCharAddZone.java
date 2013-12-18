package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharAddZone extends ServerPacket{

	public int	zId;
	public int	cId;
	public int	pId;
	public boolean buy;
	public int	lIdx;
	
	public ServerPacketCharAddZone( int sender,int zoneId,int cardId,int charId,boolean buy,int localIdx )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_ADD_ZONE;
		this.zId = zoneId;
		this.cId = cardId;
		this.pId = charId;
		this.buy = buy;
		this.lIdx = localIdx;
	}
}
