package protocol.client;

import protocol.ClientPacket;

public class ClientPacketCharBankrupt extends ClientPacket{
	
	public ClientPacketCharBankrupt()
	{
		proto = ClientPacket.MCP_CHAR_BANKRUPT;
	}
}
