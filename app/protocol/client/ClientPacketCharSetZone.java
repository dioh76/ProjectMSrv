package protocol.client;

import protocol.ClientPacket;

public class ClientPacketCharSetZone extends ClientPacket{
	
	public int zId;
	public float zVal;
	public boolean buy;
	
	public ClientPacketCharSetZone()
	{
		proto = ClientPacket.MCP_CHAR_SET_ZONE;
	}
}