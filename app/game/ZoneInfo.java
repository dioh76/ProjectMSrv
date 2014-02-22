package game;

import java.util.List;

import models.GameRoom;

import xml.GameRule;

public class ZoneInfo {
	
	public static final int ZONE_RACE_NONE 		= 0;
	public static final int ZONE_RACE_HUMAN 	= 1;
	public static final int ZONE_RACE_DEVIL 	= 2;
	public static final int ZONE_RACE_ANGEL 	= 3;
	public static final int ZONE_RACE_NEUTRAL 	= 4;
	
	public static final int ZONE_MAINTYPE_START = 0;
	public static final int ZONE_MAINTYPE_NORMAL = 1;
	public static final int ZONE_MAINTYPE_SPELL = 2;
	public static final int ZONE_MAINTYPE_EVENT1 = 3;
	public static final int ZONE_MAINTYPE_PORTAL = 4;
	public static final int ZONE_MAINTYPE_EVENT2 = 5;
	public static final int ZONE_MAINTYPE_EVENT3 = 6;
	
	public int 	id;
	public int 	type;
	public int 	race;
	public boolean enhancable;
	public int	info;	//unique zone information id;
	
	private int	mLevel;
	private int	mCharId;
	private float mTollRate;
	private boolean mAmbush;
	private int mAmbushOwner;
	private Buff mBuff;
	private CardInfo mCardInfo;
	private int	mStartEnhance;
	
	public List<Integer> mLinkedZones; 
	public List<ZoneValueInfo> values;
	
	public GameRoom gameroom;
	
	public ZoneInfo(int id, GameRoom room)
	{
		this.id = id;
		this.mLevel = 0;
		this.mCharId = 0;
		this.mTollRate = 100.0f;
		this.mStartEnhance = 0;
		this.gameroom = room;
	}
	
	public void setChar(int charId)
	{
		mCharId = charId;
	}
		
	public int getChar()
	{
		return mCharId;
	}
	
	public void setTollRate(float rate)
	{
		mTollRate = rate;
	}
	
	public float getTollRate()
	{
		return mTollRate;
	}
	
	public void setAmbush(boolean ambush, int owner)
	{
		this.mAmbush = ambush;
		this.mAmbushOwner = owner;
	}
	
	public boolean getAmbush()
	{
		return mAmbush;
	}
	
	public int getAmbushOwner()
	{
		return mAmbushOwner;
	}
	
	public CardInfo getCardInfo()
	{
		return mCardInfo;
	}
	
	public void setCardInfo(CardInfo info)
	{
		mCardInfo = info;
	}
	
	public void addStartEnhance()
	{
		if(mStartEnhance < GameRule.getInstance().START_ENHANCE_ROUND)
			mStartEnhance++;
	}
	
	public void setBuff(Buff buff)
	{
		if(buff != null)
			buff.apply();
		else
			mBuff.unapply();
		
		mBuff = buff;
	}
	
	public Buff getBuff()
	{
		return mBuff;
	}
		
	public void setLevel(int level)
	{
		mLevel = level;
	}
	
	public int getLevel()
	{
		return mLevel;
	}
	
	public float buySoul()
	{
		if(values == null || values.size() == 0)
			return 0;
		else if(mLevel >= values.size())
			return 0;
		else return values.get(mLevel).buy;
	}
	
	public float sellSoul()
	{
		/*if(values == null || values.size() == 0)
			return 0;
		else if(mLevel >= values.size())
			return 0;
		else return values.get(mLevel).sell;*/
		
		if(mCardInfo != null)
			return mCardInfo.cost / 2;
		else 
			return 0;
	}
	
	public float tollSoul()
	{
		if(values == null || values.size() == 0)
			return 0;
		else if(mLevel >= values.size())
			return 0;
		else 
		{
			float zoneToll = values.get(mLevel).toll;
			if(mCardInfo != null)
				zoneToll += mCardInfo.cost;
			
			zoneToll = zoneToll * (1 + GameRule.getInstance().getStartEnhance(mStartEnhance));
			if(allOccupyLinkedZone())
				return zoneToll * 2.0f * mTollRate/100.0f;
			else
				return zoneToll * mTollRate/100.0f;
		}
	}
	
	private boolean allOccupyLinkedZone()
	{
		if(mLinkedZones == null || mLinkedZones.size() == 0)
			return false;
		
    	boolean bAllOccupy = true;
    	for(int zId : mLinkedZones)
    	{
    		ZoneInfo zoneInfo = gameroom.getZone(zId);
    		if(zoneInfo.getChar() != getChar())
    			bAllOccupy = false;
    	}
    	
    	return bAllOccupy;		
	}
}