package game.spell;

import models.GameRoom;
import game.Character;
import game.ZoneInfo;

public interface SpellHandler {
	
	boolean onBuff();
	boolean onUse(int spellId, GameRoom room, Character castChr, Character targetChr, ZoneInfo zoneInfo1, ZoneInfo zoneInfo2); 
}
