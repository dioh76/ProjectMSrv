package game;

import java.util.ArrayList;
import java.util.List;

public class SrvCharacter {

	public long 	userId;
	public int 		charId;
	public boolean	userChar;
	public float	soul;
	public boolean	checkdirection;
	
	public List<Buff> mBuffs;
	
	public SrvCharacter(long userId, int charId, boolean userChar, float soul, boolean checkdirection)
	{
		this.userId = userId;
		this.charId = charId;
		this.userChar = userChar;
		this.soul = soul;
		this.checkdirection = checkdirection;
		
		mBuffs = new ArrayList<Buff>();
	}
}
