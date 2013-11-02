package protocol.server;

import protocol.ServerPacket;

public class ServerPacketStartReward extends ServerPacket{
	
	public boolean use;
	public int targetzone;
	
	public ServerPacketStartReward( int sender, boolean use, int targetzone )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_START_REWARD;
		this.use = use;
		this.targetzone = targetzone;

	}
}