package game;

import java.util.List;

public class ZoneInfo {
	
	public static final int ZONE_RACE_NONE 		= 0;
	public static final int ZONE_RACE_HUMAN 	= 1;
	public static final int ZONE_RACE_DEVIL 	= 2;
	public static final int ZONE_RACE_ANGEL 	= 3;
	public static final int ZONE_RACE_NEUTRAL 	= 4;
	
	public static final int NEUTRAL_ZONE_INDEX 	= 3;
	
	public int 	id;
	public int 	type;
	public int 	race;
	
	private int	mLevel;
	private int	mCharId;
	private float mTollRate;
	
	public List<ZoneValueInfo> values;
	
	public ZoneInfo(int id)
	{
		this.id = id;
		this.mLevel = 0;
		this.mCharId = 0;
		this.mTollRate = 100.0f;
	}
	
	public void setChar(int charId)
	{
		mCharId = charId;
	}
		
	public int getChar()
	{
		return mCharId;
	}
	
	public void setLevel(int level)
	{
		mLevel = level;
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
		if(values == null || values.size() == 0)
			return 0;
		else if(mLevel >= values.size())
			return 0;
		else return values.get(mLevel).sell;
	}
	
	public float tollSoul()
	{
		if(values == null || values.size() == 0)
			return 0;
		else if(mLevel >= values.size())
			return 0;
		else return values.get(mLevel).toll * mTollRate;
	}
}