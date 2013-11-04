package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharTurnOver extends ServerPacket{
	
	public boolean doubledice;
	public boolean roundover;
	public int		nextChar;
	
	public ServerPacketCharTurnOver( int sender, boolean doubledice, boolean roundover, int nextChar )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_TURN_OVER;
		this.doubledice = doubledice;
		this.roundover = roundover;
		this.nextChar = nextChar;
	}
}