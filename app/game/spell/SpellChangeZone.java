package game.spell;

import protocol.server.ServerPacketCharAddZone;
import protocol.server.ServerPacketCharRemoveZone;
import protocol.server.ServerPacketCharZoneAsset;
import xml.CardTable;
import game.CardInfo;
import game.SrvCharacter;
import game.ZoneInfo;
import models.GameRoom;

public class SpellChangeZone extends Spell {

	public SpellChangeZone(int id, String strName, int nVal1, int nVal2,
			int useType, int targetUser, int targetType) {
		super(id, strName, nVal1, nVal2, useType, targetUser, targetType);
		spellType = Spell.SPELL_CHANGEZONE;
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

		CardInfo cardInfo = CardTable.getInstance().getCard(value1);
		if(cardInfo == null)
			return true;
		
		castChr.removeZoneAsset(zoneInfo1.id);
		room.notifyAll( new ServerPacketCharRemoveZone(castChr.charId,zoneInfo1.id,false,true).toJson());
    	
    	zoneInfo1.setCardInfo(cardInfo);
    	castChr.addZoneAsset(zoneInfo1.id, zoneInfo1.tollSoul());
    	room.notifyAll( new ServerPacketCharAddZone(castChr.charId,zoneInfo1.id,cardInfo.cardId,castChr.charId,false,-1).toJson());    
		
    	room.notifyAll( new ServerPacketCharZoneAsset(castChr.charId,castChr.getZoneCount(),castChr.getZoneAssets()).toJson());
    	
    	room.sendRanking();
		
		return true;
	}

}
