package game;

import game.spell.Spell;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import models.User;

import protocol.server.ServerPacketCharAddCard;
import protocol.server.ServerPacketCharDelBuff;
import protocol.server.ServerPacketCharRemoveCard;

import xml.GameRule;
import xml.SpellTable;

public class Character {

	private User	ownuser;
	
	public long 	userId;
	public int 		charId;
	public int		charType;
	public String	userName;
	public boolean	userChar;
	public float	money;
	public boolean	checkdirection;
	public boolean	addcard;
	
	//position
	public int		curzone;
	
	public boolean	myturn;
	public int		dice1;
	public int		dice2;
	public int		doubledice;
	
	//character is controlled by spell
	public boolean	controlled;
	public int		spellcaster;
	
	//casted spell to me
	public SpellCasted lastspell;
	
	public List<Buff> mBuffs;
	public List<Integer> mEquipSpells;
	public List<Integer> mAllCards;
	public List<Integer> mPlayCards;
	public List<Integer> mRemainCards;
	
	private List<ZoneValue> mZoneValues;
	
	public Character(User user, long userId, int charId, int charType, String userName, boolean userChar, float money, boolean checkdirection)
	{
		this.ownuser = user;
		this.userId = userId;
		this.charId = charId;
		this.charType = charType;
		this.userName = userName;
		this.userChar = userChar;
		this.money = money;
		this.checkdirection = checkdirection;
		
		this.addcard = false; 
		
		mBuffs = new ArrayList<Buff>();
		mEquipSpells = new ArrayList<Integer>();
		mZoneValues = new ArrayList<ZoneValue>();
		mAllCards = new ArrayList<Integer>();
		mPlayCards = new ArrayList<Integer>();
	}
	
	private boolean hasPlayCard(int cardId)
	{
		boolean hasInPlayCard = false;
		for(int playCardId : mPlayCards)
		{
			if(cardId == playCardId)
				hasInPlayCard = true;
		}	
		
		return hasInPlayCard;
	}
	
	public void addCard(int cardId)
	{
		if(mPlayCards.size() < GameRule.INITIAL_CARDDECK_SIZE)
		{
			mPlayCards.add(cardId);
			sendPacket(new ServerPacketCharAddCard(charId,cardId,false).toJson());
		}
		else
		{
			//Into the first deck
			mRemainCards.add(0, cardId);
		}
	}
	
	public void addCard()
	{
		if(mPlayCards.size() < GameRule.INITIAL_CARDDECK_SIZE)
		{
			//If the remain of the card is empty, initialize
			if(mRemainCards.size() == 0)
			{
				for(int cardId : mAllCards)
				{
					if(hasPlayCard(cardId) == false)
						mRemainCards.add(cardId);
				}
			}
			
			int cardId = mRemainCards.get(0);
			mPlayCards.add(cardId);
			mRemainCards.remove(0);
			
			sendPacket(new ServerPacketCharAddCard(charId,cardId,true).toJson());
		}
	}
	
	public void removeCard(int cardId)
	{
		for(int i = 0; i < mPlayCards.size(); i++)
		{
			if(mPlayCards.get(i) == cardId)
			{
				mPlayCards.remove(i);
				break;
			}
		}
		
		sendPacket(new ServerPacketCharRemoveCard(charId,cardId).toJson());
	}
	
	public boolean hasEquipSpell(int spellType)
	{
		for(int spellId : mEquipSpells)
		{
			Spell info = SpellTable.getInstance().getSpell(spellId);
			if(info.spellType == spellType)
				return true;
		}
		
		return false;
	}
	
	public int removeEquipSpell(int spellType)
	{
		for(int i=0; i< mEquipSpells.size(); i++)
		{
			int spellId = mEquipSpells.get(i);
			Spell info = SpellTable.getInstance().getSpell(spellId);
			if(info.spellType == spellType)
			{
				mEquipSpells.remove(i);
				return spellId;
			}
		}
		
		return -1;
	}
	
	public boolean removeEquipSpellId(int spellId)
	{
		return mEquipSpells.remove(new Integer(spellId));
	}
	
	public void removeBuff(int buffId)
	{
		for(int i = mBuffs.size() - 1; i >=0; i--)
		{
			Buff buff = mBuffs.get(i);
			if( buff.id == buffId )
			{
				mBuffs.remove(i);
				break;
				//notifyAll(new ServerPacketCharDelBuff(charId,buff.id,buff.targetchar).toJson()); 
			}
		}		
	}
	
	public void addZoneAsset( int zoneId, float value, float sell )
	{
		boolean bHasZone = false;
		for(ZoneValue asset : mZoneValues)
		{
			if(asset.zoneId == zoneId)
			{
				bHasZone = true;
				asset.asset = value;
				asset.sell = sell;
			}
		}
		
		if(bHasZone == false)
			mZoneValues.add(new ZoneValue(zoneId, value, sell));
	}
	
	public void removeZoneAsset(int zoneId)
	{
		for(int i = 0; i < mZoneValues.size(); i++)
		{
			if(mZoneValues.get(i).zoneId == zoneId)
			{
				mZoneValues.remove(i);
				break;
			}
		}
	}
	
	public int getZoneCount()
	{
		return mZoneValues.size();
	}
	
	public List<Integer> getOwnZones()
	{
		List<Integer> zoneIds = new ArrayList<Integer>();
		
		for(ZoneValue asset : mZoneValues)
			zoneIds.add(asset.zoneId);
		
		return zoneIds;
	}
	
	public float getZoneAssets()
	{
		float sum = 0;
		for(ZoneValue asset : mZoneValues)
			sum += asset.asset;
		
		return sum;
	}
	
	public float getZoneSellSum()
	{
		float sum = 0;
		for(ZoneValue asset : mZoneValues)
			sum += asset.sell;
		
		return sum;		
	}
	
	public void sendPacket(JsonNode node)
	{
		if(ownuser != null) ownuser.sendPacket(node);
			
	}
}

class ZoneValue {
	public int 	zoneId;
	public float asset;
	public float sell;
	
	public ZoneValue(int zoneId, float asset, float sell)
	{
		this.zoneId = zoneId;
		this.asset = asset;
		this.sell = sell;
	}
}