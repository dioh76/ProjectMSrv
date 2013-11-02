package protocol.client;

import protocol.ClientPacket;

public class ClientPacketCardDeckUse extends ClientPacket{
	
	public int acttype;
	public int index;
	public int card;

	public ClientPacketCardDeckUse()
	{
		proto = ClientPacket.MCP_CARDDECK_USE;
	}
}