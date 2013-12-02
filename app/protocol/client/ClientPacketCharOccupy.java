package protocol.client;

import protocol.ClientPacket;

public class ClientPacketCharOccupy extends ClientPacket{
	
	public int zId;
	public int idx;
	public int cId;
	
	public ClientPacketCharOccupy()
	{
		proto = ClientPacket.MCP_CHAR_OCCUPY;
	}
}
