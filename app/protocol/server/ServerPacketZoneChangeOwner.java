package protocol.server;

import protocol.ServerPacket;

public class ServerPacketZoneChangeOwner extends ServerPacket{
	
	public int cId;
	public int zId;
	
	public ServerPacketZoneChangeOwner( int sender, int cId, int zId)
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_ZONE_CHANGE_OWNER;
		this.cId = cId;
		this.zId = zId;
	}
}