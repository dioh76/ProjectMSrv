package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharZoneAsset extends ServerPacket{
	
	public int count;
	public float asset;
	
	public ServerPacketCharZoneAsset( int sender, int count, float asset )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_ZONE_ASSET;
		this.count = count;
		this.asset = asset;
	}
}