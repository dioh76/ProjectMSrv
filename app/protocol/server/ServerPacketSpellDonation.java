package protocol.server;

import java.util.List;

import protocol.ServerPacket;

public class ServerPacketSpellDonation extends ServerPacket{
	
	public int sId;
	public int zId;
	public float asset;
	public int toId;
	public float tasset;
	public List<Integer> ranks;
	
	public ServerPacketSpellDonation(int sender, int spellId, int zoneId, float asset, int toCharId, float tasset, List<Integer> ranks)
	{
		this.sender = sender;
		this.proto = ServerPacket.MSP_SPELL_BEAUTY;
		this.sId = spellId;
		this.zId = zoneId;
		this.asset = asset;
		this.toId = toCharId;		
		this.tasset = tasset;
		this.ranks = ranks;
	}
}