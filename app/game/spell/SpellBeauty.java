package game.spell;

import protocol.server.ServerPacketSpellBeauty;
import game.Character;
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
	public boolean onUse(int spellId, GameRoom room, Character castChr,
			Character targetChr, ZoneInfo zoneInfo1, ZoneInfo zoneInfo2) {
		
		if(castChr == null)
			return true;
		
		if(zoneInfo1 == null)
			return true;

		if(targetChr == null)
    		return true;    	
    	
    	zoneInfo1.setChar(castChr.charId);
    	
    	float asset = zoneInfo1.tollMoney();
    	castChr.addZoneAsset(zoneInfo1.id, asset, zoneInfo1.sellMoney());
    	zoneInfo1.setChar(castChr.charId);
    	targetChr.removeZoneAsset(zoneInfo1.id);
    	
    	room.notifyAll(new ServerPacketSpellBeauty(castChr.charId,spellId,zoneInfo1.id,castChr.charId,castChr.getZoneAssets(),targetChr.charId,targetChr.getZoneAssets(),room.getRanks()).toJson());
    	//room.sendRanking();		
		
		return true;
	}
}
