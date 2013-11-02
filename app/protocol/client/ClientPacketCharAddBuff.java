package protocol.client;

import protocol.ClientPacket;

public class ClientPacketCharAddBuff extends ClientPacket{
	
	public int 		id;
	public int 		bufftype;
	public int 		targetvalue;
	public int 		targetchar;
	public int 		targetzone;
	public int 		remainturn;
	public boolean 	creature;
	public int		spellid;
	
	public ClientPacketCharAddBuff()
	{
		proto = ClientPacket.MCP_CHAR_ADD_BUFF;
	}
}
