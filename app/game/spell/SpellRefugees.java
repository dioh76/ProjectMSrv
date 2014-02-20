package game.spell;

import game.SrvCharacter;
import game.ZoneInfo;
import models.GameRoom;

public class SpellRefugees extends Spell {

	public SpellRefugees(int id, String strName, int nVal1, int nVal2,
			int useType, int targetUser, int targetType) {
		super(id, strName, nVal1, nVal2, useType, targetUser, targetType);
		spellType = Spell.SPELL_REFUGEES;
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
		
		castChr.soul -= value1;
		
		room.sendSoulChanged(castChr, true);
		room.sendRanking();
		
		return true;
	}

}
