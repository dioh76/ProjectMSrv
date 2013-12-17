package protocol.client;

import protocol.ClientPacket;

public class ClientPacketCardChange extends ClientPacket{
	
	public int zId;
	public int idx;
	public int cId;
	
	public ClientPacketCardChange()
	{
		proto = ClientPacket.MCP_CARD_CHANGE;
	}
}
