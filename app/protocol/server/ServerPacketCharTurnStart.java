package protocol.server;

import protocol.ServerPacket;

public class ServerPacketCharTurnStart extends ServerPacket{
	
	public boolean 	doubledice;
	
	public ServerPacketCharTurnStart( int sender, boolean doubledice )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_CHAR_TURN_START;
		this.doubledice = doubledice;
	}
}