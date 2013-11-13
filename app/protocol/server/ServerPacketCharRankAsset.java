package protocol.server;

import java.util.List;

import protocol.ServerPacket;

public class ServerPacketCharRankAsset extends ServerPacket{
	
	public List<Integer> ranks;
	
	public ServerPacketCharRankAsset( int sender, List<Integer> ranks )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_RANK_ASSET;
		this.ranks = ranks;
	}
}