package protocol.client;

import protocol.ClientPacket;

public class ClientPacketCharAddZone extends ClientPacket{
	
	public int zId;
	public int cId;
	public int pId;
	public boolean buy;
	public int lIdx;
	
	public ClientPacketCharAddZone()
	{
		proto = ClientPacket.MCP_CHAR_ADD_ZONE;
	}
}
