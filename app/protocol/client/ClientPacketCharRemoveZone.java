package protocol.client;

import protocol.ClientPacket;

public class ClientPacketCharRemoveZone extends ClientPacket{
	
	public int zId;
	public boolean sell;
	
	public ClientPacketCharRemoveZone()
	{
		proto = ClientPacket.MCP_CHAR_REMOVE_ZONE;
	}
}