package protocol.client;

import protocol.ClientPacket;

public class ClientPacketCharChangeOwner extends ClientPacket{
	
	public int	zId;
	public int	fromId;
	public int	toId;
	
	public ClientPacketCharChangeOwner()
	{
		proto = ClientPacket.MCP_CHAR_CHANGE_OWNER;
	}
}