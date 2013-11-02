package protocol.server;

import protocol.ServerPacket;

public class ServerPacketPortalUse extends ServerPacket{
	
	public int targetzone;
	
	public ServerPacketPortalUse( int sender, int targetzone )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_PORTAL_USE;
		this.targetzone = targetzone;

	}
}