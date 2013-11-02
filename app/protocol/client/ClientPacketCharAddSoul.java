package protocol.client;

import protocol.ClientPacket;

public class ClientPacketCharAddSoul extends ClientPacket{
	
	public float	addsoul;
	
	public ClientPacketCharAddSoul()
	{
		proto = ClientPacket.MCP_CHAR_ADD_SOUL;
	}
}

