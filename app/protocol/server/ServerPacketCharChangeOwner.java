package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharChangeOwner extends ServerPacket{
	
	public int zId;
	public int toId;
	public int zCnt;
	public float asset;
	public int fromId;
	public int zCnt2;
	public float asset2;
	
	public ServerPacketCharChangeOwner(int sender, int zoneId, int toCharId,int zoneCnt, float asset, int fromCharId, int zoneCnt2, float asset2)
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_CHANGE_OWNER;
		this.zId = zoneId;
		this.toId = toCharId;
		this.zCnt = zoneCnt;
		this.asset = asset;
		this.fromId = fromCharId;
		this.zCnt2 = zoneCnt2;
		this.asset2 = asset2;
	}
}