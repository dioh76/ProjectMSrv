package game;

import java.util.ArrayList;
import java.util.List;

public class CardOption {
	
	public int cardId;
	public boolean attack;
	public boolean defense;
	
	public List<Integer> targetCards;
	public List<Integer> targetRegions;
	

	public CardOption()
	{
		targetCards = new ArrayList<Integer>();
		targetRegions = new ArrayList<Integer>();
	}
	
	public boolean getAttackMatch(CardInfo defenseCard, ZoneInfo zoneInfo)
	{
		boolean matched = false;
		
		if(attack == false)
			return matched;
		
		for(int targetId : targetCards)
		{
			if(targetId == defenseCard.getBaseId())
			{
				matched = true;
				break;
			}
		}		
		
		for(int regionId : targetRegions)
		{
			if(regionId == zoneInfo.info)
			{
				matched = true;
				break;
			}			
		}
		
		return matched;
	}
	
	public boolean getDefenseMatch(CardInfo attackCard, ZoneInfo zoneInfo)
	{
		boolean matched = false;
		
		if(defense == false)
			return matched;
		
		for(int targetId : targetCards)
		{
			if(targetId == attackCard.getBaseId())
			{
				matched = true;
				break;
			}
		}		
		
		for(int regionId : targetRegions)
		{
			if(regionId == zoneInfo.info)
			{
				matched = true;
				break;
			}			
		}
		
		return matched;	
	}

}
