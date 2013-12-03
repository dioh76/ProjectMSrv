package protocol.client;

import protocol.ClientPacket;

public class ClientPacketCharTurnOver extends ClientPacket{
	
	public boolean force;
	
	public ClientPacketCharTurnOver()
	{
		proto = ClientPacket.MCP_CHAR_TURN_OVER;
	}
}