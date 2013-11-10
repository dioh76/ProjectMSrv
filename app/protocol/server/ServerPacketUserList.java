package protocol.server;

import java.util.List;

import protocol.ServerPacket;

public class ServerPacketUserList extends ServerPacket{
	
	public List<Long> userIds;
	public List<String> userNames;
	
	public ServerPacketUserList( int sender, List<Long> userIds, List<String> userNames )
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_USER_LIST;
		this.userIds = userIds;
		this.userNames = userNames;

	}
}