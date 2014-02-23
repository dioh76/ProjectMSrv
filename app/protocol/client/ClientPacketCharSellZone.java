package protocol.client;

import protocol.ClientPacket;

public class ClientPacketCharSellZone extends ClientPacket{
	
	public int zId;
	
	public ClientPacketCharSellZone()
	{
		proto = ClientPacket.MCP_CHAR_SELL_ZONE;
	}
}