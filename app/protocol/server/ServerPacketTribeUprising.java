package protocol.server;

import protocol.ServerPacket;

public class ServerPacketTribeUprising extends ServerPacket{
	
	public int zId;
	
	public ServerPacketTribeUprising( int sender, int zId)
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_TRIBE_UPRISING;
		this.zId = zId;
	}
}