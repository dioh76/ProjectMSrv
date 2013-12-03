package protocol.server;

import protocol.ServerPacket;

public class ServerPacketRollDice extends ServerPacket{
	
	public int val;
	public boolean doubled;
	
	public ServerPacketRollDice( int sender,int value,boolean doubled )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_ROLL_DICE;
		this.val = value;
		this.doubled = doubled;

	}
}