package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharOccupy extends ServerPacket{
	
	public int zId;
	public int cId;
	public boolean rst;
	
	public ServerPacketCharOccupy( int sender, int zoneId, int cardId, boolean result )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_OCCUPY;
		this.zId = zoneId;
		this.cId = cardId;
		this.rst = result;
	}
}
