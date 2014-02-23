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

public class SrvCharacter {

	private User	ownuser;
	
	public long 	userId;
	public int 		charId;
	public int		charType;
	public String	userName;
	public boolean	userChar;
	public float	soul;
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
	
	private List<ZoneAsset> mZoneAssets;
	
	public SrvCharacter(User user, long userId, int charId, int charType, String userName, boolean userChar, float soul, boolean checkdirection)
	{
		this.ownuser = user;
		this.userId = userId;
		this.charId = charId;
		this.charType = charType;
		this.userName = userName;
		this.userChar = userChar;
		this.soul = soul;
		this.checkdirection = checkdirection;
		
		this.addcard = false; 
		
		mBuffs = new ArrayList<Buff>();
		mEquipSpells = new ArrayList<Integer>();
		mZoneAssets = new ArrayList<ZoneAsset>();
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
	
	public void addZoneAsset( int zoneId, float value )
	{
		boolean bHasZone = false;
		for(ZoneAsset asset : mZoneAssets)
		{
			if(asset.zoneId == zoneId)
			{
				bHasZone = true;
				asset.value = value;
			}
		}
		
		if(bHasZone == false)
			mZoneAssets.add(new ZoneAsset(zoneId, value));
	}
	
	public void removeZoneAsset(int zoneId)
	{
		for(int i = 0; i < mZoneAssets.size(); i++)
		{
			if(mZoneAssets.get(i).zoneId == zoneId)
			{
				mZoneAssets.remove(i);
				break;
			}
		}
	}
	
	public int getZoneCount()
	{
		return mZoneAssets.size();
	}
	
	public float getZoneAssets()
	{
		float sum = 0;
		for(ZoneAsset asset : mZoneAssets)
			sum += asset.value;
		
		return sum;
	}
	
	public void sendPacket(JsonNode node)
	{
		if(ownuser != null) ownuser.SendPacket(node);
			
	}
}

class ZoneAsset {
	public int 	zoneId;
	public float value;
	
	public ZoneAsset(int zoneId, float value)
	{
		this.zoneId = zoneId;
		this.value = value;
	}
}