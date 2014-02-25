package game.spell;

import game.Character;
import game.ZoneInfo;
import models.GameRoom;

public class SpellSoul extends Spell {

	public SpellSoul(int id, String strName, int nVal1, int nVal2,
			int useType, int targetUser, int targetType) {
		super(id, strName, nVal1, nVal2, useType, targetUser, targetType);
		spellType = Spell.SPELL_SOUL;
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
		
		if(targetChr == null)
			return true;
		
		castChr.money += value1;
		if(targetChr.money < value1)
			targetChr.money = 0;
		else
			targetChr.money -= value1;
		
		room.sendMoneyChanged(castChr, true);
		room.sendMoneyChanged(targetChr, true);
		
		room.sendRanking();
		
		return true;
	}

}
