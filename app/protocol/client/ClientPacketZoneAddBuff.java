package protocol.client;

import protocol.ClientPacket;

public class ClientPacketZoneAddBuff extends ClientPacket{
	
	public int bType;
	public int val;
	public int zId;
	public int remain;
	public int sId;
	
	public ClientPacketZoneAddBuff()
	{
		proto = ClientPacket.MCP_ZONE_ADD_BUFF;
	}
}