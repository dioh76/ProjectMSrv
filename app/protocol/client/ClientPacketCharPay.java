package protocol.client;

import protocol.ClientPacket;

public class ClientPacketCharPay extends ClientPacket{
	
	public int zId;
	
	public ClientPacketCharPay()
	{
		proto = ClientPacket.MCP_CHAR_PAY;
	}
}
