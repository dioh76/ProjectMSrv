package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharTurnOver extends ServerPacket{
	
	public boolean doubledice;
	public boolean roundover;
	public int		startIdx;
	
	public ServerPacketCharTurnOver( int sender, boolean doubledice, boolean roundover, int startIdx )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_TURN_OVER;
		this.doubledice = doubledice;
		this.roundover = roundover;
		this.startIdx = startIdx;
	}
}