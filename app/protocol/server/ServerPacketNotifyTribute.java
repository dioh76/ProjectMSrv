package protocol.server;

import protocol.ServerPacket;

public class ServerPacketNotifyTribute extends ServerPacket{
	
	public int cId;
	public int zId;
	
	public ServerPacketNotifyTribute( int sender, int cId, int zId)
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_NOTIFY_TRIBUTE;
		this.cId = cId;
		this.zId = zId;
	}
}