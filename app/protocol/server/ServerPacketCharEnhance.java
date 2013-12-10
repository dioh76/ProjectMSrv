package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharEnhance extends ServerPacket{
	
	public int zId;
	public int zLv;
	public float rSoul;
	public int zCnt;
	public float zAsst;
	public boolean msg;
	public boolean turn;
	
	public ServerPacketCharEnhance( int sender, int zoneId, int zoneLevel, float remainSoul, int zoneCnt, float zoneAsset, boolean notifyMsg, boolean turnover )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_ENHANCE;
		this.zId = zoneId;
		this.zLv = zoneLevel;
		this.rSoul = remainSoul;
		this.zCnt = zoneCnt;
		this.zAsst = zoneAsset;
		this.msg = notifyMsg;
		this.turn = turnover;
	}
}
