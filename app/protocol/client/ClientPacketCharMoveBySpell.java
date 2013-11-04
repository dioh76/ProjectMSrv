package protocol.client;

import protocol.ClientPacket;

public class ClientPacketCharMoveBySpell extends ClientPacket{
	
	public int move;
	public boolean reverse;
	public boolean bonus;	
	
	public ClientPacketCharMoveBySpell()
	{
		proto = ClientPacket.MCP_CHAR_MOVE_BYSPELL;
	}
}