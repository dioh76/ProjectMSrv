package game;

import java.util.List;

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
	
	public ZoneInfo(int id)
	{
		this.id = id;
		this.mLevel = 0;
		this.mCharId = 0;
		this.mTollRate = 100.0f;
		this.mStartEnhance = 0;
	}
	
	public void setChar(int charId)
	{
		mCharId = charId;
	}
		
	public int getChar()
	{
		return mCharId;
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
		if(mStartEnhance < 5)
			mStartEnhance++;
	}
	
	public void setBuff(Buff buff)
	{
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
			
			zoneToll = zoneToll * (1 + 0.1f * mStartEnhance);
			
			return values.get(mLevel).toll * mTollRate/100.0f;
		}
	}
}