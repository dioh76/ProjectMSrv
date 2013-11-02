package protocol.client;

import protocol.ClientPacket;

public class ClientPacketSpellUse extends ClientPacket{
	
	public int spellid;
	public int targetchar;
	public int targetzone;
	public int targetzone2;
	
	public ClientPacketSpellUse()
	{
		proto = ClientPacket.MCP_SPELLUSE;
	}
}