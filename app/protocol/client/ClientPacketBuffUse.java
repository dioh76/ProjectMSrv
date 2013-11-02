package protocol.client;

import protocol.ClientPacket;

public class ClientPacketBuffUse extends ClientPacket{
	
	public int buffId;
	public int spellid;
	
	public ClientPacketBuffUse()
	{
		proto = ClientPacket.MCP_BUFF_USE;
	}
}