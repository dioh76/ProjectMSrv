package protocol.server;

import protocol.ServerPacket;

public class ServerPacketRollDice extends ServerPacket{
	
	public int rVal;
	public int bVal;
	public boolean doubled;
	
	public ServerPacketRollDice( int sender,int redvalue,int blackvalue,boolean doubled )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_ROLL_DICE;
		this.rVal = redvalue;
		this.bVal = blackvalue;
		this.doubled = doubled;

	}
}