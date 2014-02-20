package game.spell;

import models.GameRoom;
import game.SrvCharacter;
import game.ZoneInfo;

public interface SpellHandler {
	
	boolean onBuff();
	boolean onUse(int spellId, GameRoom room, SrvCharacter castChr, SrvCharacter targetChr, ZoneInfo zoneInfo1, ZoneInfo zoneInfo2); 
}
