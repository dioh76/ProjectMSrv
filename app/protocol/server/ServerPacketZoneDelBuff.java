package protocol.server;

import protocol.ServerPacket;

public class ServerPacketZoneDelBuff extends ServerPacket{
	
	public int bId;
	public int zId;
	
	public ServerPacketZoneDelBuff(int sender,int buffId,int zoneId)
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_ZONE_DEL_BUFF;
		this.bId = buffId;
		this.zId = zoneId;
	}
}