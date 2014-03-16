package protocol.client;

import protocol.ClientPacket;

public class ClientPacketCharSellZone extends ClientPacket{
	
	public int zId;
	public int toId;
	public float sumpay;
	
	public ClientPacketCharSellZone()
	{
		proto = ClientPacket.MCP_CHAR_SELL_ZONE;
	}
}