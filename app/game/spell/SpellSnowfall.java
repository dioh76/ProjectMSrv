package game.spell;

import game.Buff;
import game.Character;
import game.ZoneInfo;
import models.GameRoom;

public class SpellSnowfall extends Spell {

	public SpellSnowfall(int id, String strName, int nVal1, int nVal2,
			int useType, int targetUser, int targetType) {
		super(id, strName, nVal1, nVal2, useType, targetUser, targetType);
		spellType = Spell.SPELL_SNOWFALL;
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
		
		if(zoneInfo1.type == ZoneInfo.ZONE_MAINTYPE_TRIBE)
			return true;		

		room.zoneAddBuff(castChr.charId, Buff.PLUS_TOLL, zoneInfo1.id, value1, value2, spellId);
		
		return true;
	}

}
