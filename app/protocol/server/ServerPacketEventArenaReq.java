package protocol.server;

import protocol.ServerPacket;

public class ServerPacketEventArenaReq extends ServerPacket{
	
	public int startPlayer;
	public int membercount;
	public int atype;
	
	public ServerPacketEventArenaReq( int sender, int startPlayer, int membercount, int arenaType)
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_EVENT_ARENA_REQ;
		this.startPlayer = startPlayer;
		this.membercount = membercount;
		this.atype = arenaType;
	}
}