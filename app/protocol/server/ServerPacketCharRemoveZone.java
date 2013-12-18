package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharRemoveZone extends ServerPacket{

	public int zId;
	public boolean sell;
	public boolean npconly;
	
	public ServerPacketCharRemoveZone( int sender,int zoneId,boolean sell,boolean npconly )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_REMOVE_ZONE;
		this.zId = zoneId;
		this.sell = sell;
		this.npconly = npconly;
	}
}
