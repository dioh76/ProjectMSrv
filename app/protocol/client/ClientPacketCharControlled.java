package protocol.client;

import protocol.ClientPacket;

public class ClientPacketCharControlled extends ClientPacket{
	
	public int caster;
	
	public ClientPacketCharControlled()
	{
		proto = ClientPacket.MCP_CHAR_CONTROLLED;
	}
}
