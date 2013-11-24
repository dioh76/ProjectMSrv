package protocol.client;

import protocol.ClientPacket;

public class ClientPacketCharAddCard extends ClientPacket{
	
	public int 	cId;

	public ClientPacketCharAddCard()
	{
		proto = ClientPacket.MCP_CHAR_ADDCARD;
	}
}
