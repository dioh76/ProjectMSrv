package game.spell;

import protocol.server.ServerPacketCharMoveZone;
import xml.ZoneTable;
import game.Character;
import game.ZoneInfo;
import models.GameRoom;

public class SpellProvocation extends Spell {

	public SpellProvocation(int id, String strName, int nVal1, int nVal2,
			int useType, int targetUser, int targetType) {
		super(id, strName, nVal1, nVal2, useType, targetUser, targetType);
		spellType = Spell.SPELL_PROVOCATION;
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
		
		if(room.getCharZones(castChr.charId).size() == 0)
			return true;
		
		if(targetChr == null)
    		return true;
		
		int pos = targetChr.curzone;
		int i = pos + 1;
		while(i != pos)
		{
			if(i >= ZoneTable.getInstance().getZoneCount())
				i = 0;

			ZoneInfo zoneInfo = room.getZone(i);
			if(zoneInfo != null)
			{
				if(zoneInfo.getChar() == castChr.charId)
					break;
			}
			
			i++;
			
	    	targetChr.controlled = true;
	    	targetChr.spellcaster = castChr.charId;
		}
		
		room.notifyAll(new ServerPacketCharMoveZone(targetChr.charId,i).toJson());
		
		return false;
	}

}
