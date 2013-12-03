package protocol.server;

import protocol.ServerPacket;

public class ServerPacketZoneAddBuff extends ServerPacket{
	
	public int bId;
	public int bType;
	public int val;
	public int zId;
	public int remain;
	public int sId;
	
	public ServerPacketZoneAddBuff(int sender,int buffId,int buffType,int value,int zoneId,int remain,int spellId)
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_ZONE_ADD_BUFF;
		this.bId = buffId;
		this.bType = buffType;
		this.val = value;
		this.zId = zoneId;
		this.remain = remain;
		this.sId = spellId;
	}
}