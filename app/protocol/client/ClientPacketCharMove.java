package protocol.client;

import protocol.ClientPacket;

public class ClientPacketCharMove extends ClientPacket{
	
	public int move;
	
	public ClientPacketCharMove()
	{
		proto = ClientPacket.MCP_CHAR_MOVE;
	}
}