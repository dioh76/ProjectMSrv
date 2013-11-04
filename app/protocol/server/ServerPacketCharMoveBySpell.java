package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharMoveBySpell extends ServerPacket{

	public int move;
	public boolean reverse;
	public boolean bonus;
	
	public ServerPacketCharMoveBySpell( int sender, int move, boolean reverse, boolean bonus )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_MOVE_BYSPELL;
		this.move = move;
		this.reverse = reverse;
		this.bonus = bonus;
	}
}
