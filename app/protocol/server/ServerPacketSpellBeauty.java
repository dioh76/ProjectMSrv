package protocol.server;

import java.util.List;

import protocol.ServerPacket;

public class ServerPacketSpellBeauty extends ServerPacket{
	
	public int sId;
	public int zId;
	public int toId;
	public float asset;
	public int fromId;
	public float asset2;
	public List<Integer> ranks;
	
	public ServerPacketSpellBeauty(int sender, int spellId, int zoneId, int toCharId, float asset, int fromCharId, float asset2, List<Integer> ranks)
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_SPELL_BEAUTY;
		this.sId = spellId;
		this.zId = zoneId;
		this.toId = toCharId;
		this.asset = asset;
		this.fromId = fromCharId;
		this.asset2 = asset2;
		this.ranks = ranks;
	}
}