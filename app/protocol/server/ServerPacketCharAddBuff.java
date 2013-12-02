package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharAddBuff extends ServerPacket{

	public int 	id;
	public int 	bufftype;
	public int 	targetvalue;
	public int 	targetchar;
	public int 	targetzone;
	public int 	remainturn;
	public boolean creature;
	public int	spellid;
	
	public ServerPacketCharAddBuff( int sender, int id, int bufftype, int targetvalue, int targetchar, int targetzone, int remainturn, boolean creature, int spellId )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_ADD_BUFF;
		this.id = id;
		this.bufftype = bufftype;
		this.targetvalue = targetvalue;
		this.targetchar = targetchar;
		this.targetzone = targetzone;
		this.remainturn = remainturn;
		this.creature = creature;
		this.spellid = spellId;
	}
}
