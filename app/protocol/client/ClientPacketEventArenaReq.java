package protocol.client;

import protocol.ClientPacket;

public class ClientPacketEventArenaReq extends ClientPacket{
	
	public int startPlayer;
	public int membercount;

	public ClientPacketEventArenaReq()
	{
		proto = ClientPacket.MCP_EVENT_ARENA_REQ;
	}
}