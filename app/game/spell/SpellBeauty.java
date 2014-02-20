package game.spell;

import protocol.server.ServerPacketCharChangeOwner;
import game.SrvCharacter;
import game.ZoneInfo;
import models.GameRoom;

public class SpellBeauty extends Spell {

	public SpellBeauty(int id, String strName, int nVal1, int nVal2,
			int useType, int targetUser, int targetType) {
		super(id, strName, nVal1, nVal2, useType, targetUser, targetType);
		spellType = Spell.SPELL_BEAUTY;
	}

	@Override
	public boolean onBuff() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onUse(int spellId, GameRoom room, SrvCharacter castChr,
			SrvCharacter targetChr, ZoneInfo zoneInfo1, ZoneInfo zoneInfo2) {
		
		if(castChr == null)
			return true;
		
		if(zoneInfo1 == null)
			return true;

		if(targetChr == null)
    		return true;    	
    	
    	zoneInfo1.setChar(castChr.charId);
    	
    	float asset = zoneInfo1.tollSoul();
    	if(room.isOccpuyLinkedZone(zoneInfo1)) asset = asset * 2.0f;
    	castChr.addZoneAsset(zoneInfo1.id, asset);
    	targetChr.removeZoneAsset(zoneInfo1.id);
    	
    	room.notifyAll(new ServerPacketCharChangeOwner(castChr.charId,zoneInfo1.id,castChr.charId,castChr.getZoneCount(),castChr.getZoneAssets(),targetChr.charId,targetChr.getZoneCount(),targetChr.getZoneAssets()).toJson());
    	room.sendRanking();		
		
		return true;
	}

}
