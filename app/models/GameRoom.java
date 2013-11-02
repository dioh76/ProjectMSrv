package models;

import play.Logger;
import play.mvc.*;
import play.libs.*;
import protocol.*;
import protocol.client.*;
import protocol.server.*;
import xml.CardTable;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

import game.*;

public class GameRoom {
	
	private long mRoomId;
	private int mMaxUser;
	private boolean mPlaying = false;
	
	private List<User> mUsers = new ArrayList<User>();
	private Map<Integer, SrvCharacter> mCharacters = new HashMap<Integer, SrvCharacter>();
	
	//common
	private long mCreatedTime = 0;
	
	private int mStartCharIndex = 0;
	private int mLastCharId = 0;
	private int mCurrentTurn = 0;
	
	private SpellUsed mLastSpellUsed;
	private BattleInfo mLastBattle;
	private BattleArena mLastBattleArena;
	
	private List<Integer> maporders;

	private int mCharIdSeq = 100;
	
	public GameRoom(long roomId, int maxuser)
	{
		mCreatedTime = System.currentTimeMillis();
		
		mRoomId = roomId;
		mMaxUser = maxuser;
		
		maporders = new ArrayList<Integer>();
		maporders.add(Race.HUMAN);
		maporders.add(Race.DEVIL);
		maporders.add(Race.ANGEL);
		Collections.shuffle(maporders);
	}
	
	public long getRoomId()
	{
		return mRoomId;
	}
	
	public synchronized int getNewCharId()
	{
		return mCharIdSeq++;
	}
	
    public boolean isPlaying() {
		return mPlaying;
	}

	public void setPlaying(boolean mPlaying) {
		this.mPlaying = mPlaying;
	}
	
	public synchronized boolean isFull()
	{
		return mUsers.size() == mMaxUser ? true : false;
	}
	
	public synchronized boolean isEmpty()
	{
		return mUsers.size() == 0 ? true : false;
	}
	
	public synchronized int count()
	{
		return mUsers.size();
	}
	
	public long getRandomUserId()
	{
		final Random random = new Random();
		synchronized(mUsers)
		{
			if(mUsers.size() == 0)
				return 0;
			
			int index = random.nextInt(mUsers.size());
			return mUsers.get(index).getUserId();
		}
	}
	
	public void addUser( User user )
	{
		synchronized(mUsers)
		{
			mUsers.add(user);
		}
		
		//initialize map for this user
		user.SendPacket(new ServerPacketInitZone(0, maporders).toJson());
    	
		addCharacter(user);
	}
	
	public void addCharacter(User user)
	{
		SrvCharacter chr = new SrvCharacter(user.getUserId(), getNewCharId(), true, 300, false );
    	
    	synchronized(mCharacters)
    	{
    		mCharacters.put(chr.charId, chr);
    	}
    	
    	//notify addchar for all
    	notifyAll(new ServerPacketCharAdd(chr.charId, user.getUserId(), chr.charId, user.getName(), true).toJson()); 		
	}
	
	public void addRandomAICharacter(int count)
	{
		for( int i = 0; i < count; i++)
		{
			long randomUserId = getRandomUserId();
			SrvCharacter chr = new SrvCharacter(randomUserId, getNewCharId(), false, 300, false );
	    	
	    	synchronized(mCharacters)
	    	{
	    		mCharacters.put(chr.charId, chr);
	    	}
	    	
	    	//notify addchar for all
	    	notifyAll(new ServerPacketCharAdd(chr.charId, randomUserId, chr.charId, "AIPlayer"+(i+1), false).toJson());
		}
	}
    
    public synchronized User removeUser( long userId )
    {
    	for( int i = 0; i < mUsers.size(); i++ )
    	{
    		if( mUsers.get(i).getUserId() == userId )
    		{
    			return mUsers.remove(i); 
    		}
    	}
    	
    	return null;
    }
    
    public void processPacket( int protocol, JsonNode node )
    {
    	Logger.info(node.toString());
    	switch( protocol )
    	{
    	case ClientPacket.MCP_CHAR_ADD: onCharAdd(node); break;
    	case ClientPacket.MCP_CHAR_ADD_SOUL: onCharAddSoul(node); break;
    	case ClientPacket.MCP_CHAR_DIRECTION: onCharDirection(node); break;
    	case ClientPacket.MCP_CHAR_MOVE: onCharMove(node); break;
    	case ClientPacket.MCP_CHAR_PASS: onCharPass(node); break;
    	case ClientPacket.MCP_CHAR_MOVED: onCharMoved(node); break;
    	case ClientPacket.MCP_CHAR_ENHANCE: onCharEnhance(node); break;
    	case ClientPacket.MCP_CHAR_PASSBY_START: onCharPassByStart(node); break;
    	case ClientPacket.MCP_CHAR_TURN_OVER: onCharTurnOver(node); break;
    	case ClientPacket.MCP_CHAR_ADD_BUFF: onCharAddBuff(node); break;
    	case ClientPacket.MCP_SPELL_OPEN: onSpellOpen(node); break;
    	case ClientPacket.MCP_SPELL_REQ_USE: onSpellReqUse(node); break;
    	case ClientPacket.MCP_SPELLUSE: onSpellUse(node); break;
    	case ClientPacket.MCP_SPELLDEFENSE: onSpellDefense(node); break;
    	case ClientPacket.MCP_SPELL_EQUIP: onSpellEquip(node); break;
    	case ClientPacket.MCP_SPELLDEFENSE_REPLY: onSpellDefenseReply(node); break;
    	case ClientPacket.MCP_BUFF_USE: onBuffUse(node); break;
    	case ClientPacket.MCP_PORTAL_USE: onPortalUse(node); break;
    	case ClientPacket.MCP_START_REWARD: onStartReward(node); break;
    	case ClientPacket.MCP_EVENT_GAMBLE: onEventGamble(node); break;
    	case ClientPacket.MCP_PLAYER_BATTLE: onBattle(node); break;
    	case ClientPacket.MCP_PLAYER_BATTLE_END: onBattleEnd(node); break;
    	case ClientPacket.MCP_CARDDECK_USE: onCardDeckUse(node); break;
    	case ClientPacket.MCP_EVENT_ARENA_REQ: onEventArenaReq(node); break;
    	case ClientPacket.MCP_EVENT_ARENA_USE: onEventArenaUse(node); break;
    	case ClientPacket.MCP_EVENT_ARENA_REWARD: onEventArenaReward(node); break;
    	case ClientPacket.MCP_EQUIP_SPELL_USE: onEquipSpellUse(node); break;
    	case ClientPacket.MCP_EQUIP_SPELL_USE_REPLY: onEquipSpellUseReply(node); break;
    	}
    }
    
    public synchronized void Update(long currentmillisec)
    {
    	if(mCreatedTime + 5000 < System.currentTimeMillis())
    	{
    		autoStart();
    	}
    }
    
    // Send a Json event to all members
    public synchronized void notifyAll(JsonNode node) 
    {
        for( int i = 0; i < mUsers.size(); i++)
        {
        	WebSocket.Out<JsonNode> channel = mUsers.get(i).getChannel();
            channel.write(node);
        }
    }
    
    private void autoStart()
    {
    	if(isPlaying() == false )
		{
			setPlaying(true);
			RoomManager.ready(this.getRoomId());
			
			if(mCharacters.size() < 4)
	    	{
				addRandomAICharacter(4 - mCharacters.size());
	    	}	    	
		}
    }
    
    //Packet Handle
    
    private void onCharAdd(JsonNode node)
    {
    	//not to be used in server
    	ClientPacketCharAdd pkt = Json.fromJson(node, ClientPacketCharAdd.class);
    	
    	SrvCharacter chr = new SrvCharacter(pkt.userId, pkt.charId, pkt.userChar, 300, false );    	
    	mCharacters.put(pkt.charId, chr);
    	
    	notifyAll(new ServerPacketCharAdd(pkt.charId, pkt.userId, pkt.charId, pkt.name, pkt.userChar).toJson());   	
    }
    
    private void onCharAddSoul(JsonNode node)
    {
    	ClientPacketCharAddSoul pkt = Json.fromJson(node, ClientPacketCharAddSoul.class);
    	
    	SrvCharacter chr = mCharacters.get(pkt.sender);
    	
    	if( chr == null )
    		return;
    	
    	chr.soul += pkt.addsoul;
    	boolean bankrupt = chr.soul <= 0 ? true : false;
    	notifyAll(new ServerPacketCharAddSoul(pkt.sender, chr.soul, bankrupt).toJson());   	
    }    
    
    private void onCharDirection(JsonNode node)
    {
    	ClientPacketCharDirection pkt = Json.fromJson(node, ClientPacketCharDirection.class);
    	
    	SrvCharacter chr = mCharacters.get(pkt.sender);
    	
    	if( chr == null )
    		return;
    	
    	chr.checkdirection = true;
    	
    	boolean allready = true;
    	for( SrvCharacter srvChr : mCharacters.values() )
    	{
    		if(srvChr.checkdirection == false)
    		{
    			allready = false;
    			break;
    		}
    	}
    	
    	notifyAll(new ServerPacketCharDirection(pkt.sender, pkt.forward).toJson());
    	
    	if( allready )
    	{
    		final Random random = new Random();
    		mStartCharIndex = random.nextInt(mCharacters.size());
    		
    		notifyAll(new ServerPacketGameStart(pkt.sender, mStartCharIndex).toJson());
    	}
    }        
    
    private void onCharMove(JsonNode node)
    {
    	ClientPacketCharMove pkt = Json.fromJson(node, ClientPacketCharMove.class);
    	
    	notifyAll(new ServerPacketCharMove(pkt.sender, pkt.move).toJson());   	
    } 
    
    private void onCharPass(JsonNode node)
    {
    	ClientPacketCharPass pkt = Json.fromJson(node, ClientPacketCharPass.class);
    	
    	notifyAll(new ServerPacketCharPass(pkt.sender).toJson());   	
    }
    
    private void onCharMoved(JsonNode node)
    {
    	ClientPacketCharMoved pkt = Json.fromJson(node, ClientPacketCharMoved.class);
    	
    	notifyAll(new ServerPacketCharMoved(pkt.sender).toJson());   	
    }  
    
    private void onCharEnhance(JsonNode node)
    {
    	ClientPacketCharEnhance pkt = Json.fromJson(node, ClientPacketCharEnhance.class);
    	
    	notifyAll(new ServerPacketCharEnhance(pkt.sender).toJson());   	
    }   
    
    private void onCharPassByStart(JsonNode node)
    {
    	ClientPacketCharPassByStart pkt = Json.fromJson(node, ClientPacketCharPassByStart.class);
    	
    	notifyAll(new ServerPacketCharPassByStart(pkt.sender).toJson());   	
    }
    
    private void onCharTurnOver(JsonNode node)
    {
    	ClientPacketCharTurnOver pkt = Json.fromJson(node, ClientPacketCharTurnOver.class);
    	
    	if(pkt.doubledice == false)
    	{
    		SrvCharacter chr = mCharacters.get(pkt.sender);
    		for(int i = chr.mBuffs.size() - 1; i >=0; i--)
    		{
    			Buff buff = chr.mBuffs.get(i);
    			buff.TurnOver();
    			
    			if(buff.isValid() == false)
    			{
    				chr.mBuffs.remove(i);
    				
    				notifyAll(new ServerPacketCharDelBuff(pkt.sender,buff.id,buff.targetchar).toJson()); 
    			}
    		}
    	}
    	
    	mLastCharId = pkt.sender;
    	
    	boolean roundover = false;
    	if(pkt.doubledice == false)
    	{
    		mCurrentTurn++;
    		
    		if(mCurrentTurn == mCharacters.size())
    		{
    			mCurrentTurn = 0;
    			roundover = true;
    		}
    	}
    	
    	notifyAll(new ServerPacketCharTurnOver(pkt.sender,pkt.doubledice,roundover,mStartCharIndex).toJson());   	
    }
    
    private void onCharAddBuff(JsonNode node)
    {
    	ClientPacketCharAddBuff pkt = Json.fromJson(node, ClientPacketCharAddBuff.class);
    	
    	int objectId = Buff.getNewBuffID();
    	
    	Buff buff = new Buff();
    	buff.id = objectId;
    	buff.buffType = pkt.bufftype;
    	buff.targetchar = pkt.targetchar;
    	buff.targetzone = pkt.targetzone;
    	buff.remainturn = pkt.remainturn;
    	buff.creature = pkt.creature;
    	
    	if(mCharacters.containsKey(buff.targetchar) == false)
    		return;
    	
    	SrvCharacter chr = mCharacters.get(buff.targetchar);
    	
    	if( buff.buffType == Buff.SPELL_USE )
    	{
    		for( int i = chr.mBuffs.size() - 1; i >=0; i-- )
    		{
    			Buff prev = chr.mBuffs.get(i);
    			if( prev.buffType == Buff.SPELL_USE )
    			{
    				chr.mBuffs.remove(i);
    				notifyAll(new ServerPacketCharDelBuff(prev.targetchar,prev.id,buff.targetchar).toJson()); 
    			}
    		}
    	}
    	
    	chr.mBuffs.add(buff);
    	
    	notifyAll(new ServerPacketCharAddBuff(pkt.sender,objectId,pkt.bufftype,pkt.targetvalue,pkt.targetchar,pkt.targetzone,pkt.remainturn,pkt.creature,pkt.spellid).toJson());   	
    }
    
    private void onSpellOpen(JsonNode node)
    {
    	ClientPacketSpellOpen pkt = Json.fromJson(node, ClientPacketSpellOpen.class);
    	
    	notifyAll(new ServerPacketSpellOpen(pkt.sender).toJson());   	
    }
    
    private void onSpellReqUse(JsonNode node)
    {
    	ClientPacketSpellReqUse pkt = Json.fromJson(node, ClientPacketSpellReqUse.class);
    	
    	notifyAll(new ServerPacketSpellReqUse(pkt.sender, pkt.spellid).toJson());   	
    }
    
    private void onSpellUse(JsonNode node)
    {
    	ClientPacketSpellUse pkt = Json.fromJson(node, ClientPacketSpellUse.class);
    	
    	mLastSpellUsed = new SpellUsed(pkt.sender, pkt.spellid, pkt.targetchar, pkt.targetzone, pkt.targetzone2);
    	
    	notifyAll(new ServerPacketSpellUse(pkt.sender, pkt.spellid, pkt.targetchar, pkt.targetzone, pkt.targetzone2).toJson());   	
    }
    
    private void onSpellDefense(JsonNode node)
    {
    	ClientPacketSpellDefense pkt = Json.fromJson(node, ClientPacketSpellDefense.class);
    	
    	notifyAll(new ServerPacketSpellDefense(pkt.sender, pkt.defender).toJson());   	
    }
    
    private void onSpellEquip(JsonNode node)
    {
    	ClientPacketSpellEquip pkt = Json.fromJson(node, ClientPacketSpellEquip.class);
    	
    	notifyAll(new ServerPacketSpellEquip(pkt.sender, pkt.spellid).toJson());   	
    }
    
    private void onSpellDefenseReply(JsonNode node)
    {
    	ClientPacketSpellDefenseReply pkt = Json.fromJson(node, ClientPacketSpellDefenseReply.class);
    	
    	if(mLastSpellUsed != null)
    	{
    		notifyAll(new ServerPacketSpellDefenseReply(pkt.sender, pkt.defender, mLastSpellUsed.spellId, mLastSpellUsed.targetchar, mLastSpellUsed.targetzone, mLastSpellUsed.targetzone2, pkt.use).toJson());	
    	}   	
    }
    
    private void onBuffUse(JsonNode node)
    {
    	ClientPacketBuffUse pkt = Json.fromJson(node, ClientPacketBuffUse.class);
    	
    	SrvCharacter chr = mCharacters.get(pkt.sender);
    	if(chr == null)
    		return;
    	
    	for( int i = chr.mBuffs.size() - 1; i >= 0 ; i-- )
    	{
    		Buff buff = chr.mBuffs.get(i);
    		if( buff.id == pkt.buffId )
    		{
    			chr.mBuffs.remove(i);
    			
    			notifyAll(new ServerPacketCharDelBuff(buff.targetchar, buff.id, buff.targetchar).toJson());
    			notifyAll(new ServerPacketBuffUse(pkt.sender, pkt.spellid).toJson());
    			
    			break;
    		}
    	}   	
    }
    
    private void onPortalUse(JsonNode node)
    {
    	ClientPacketPortalUse pkt = Json.fromJson(node, ClientPacketPortalUse.class);
    	
    	notifyAll(new ServerPacketPortalUse(pkt.sender, pkt.targetzone).toJson());
    }
    
    public void onStartReward(JsonNode node)
    {
    	ClientPacketStartReward pkt = Json.fromJson(node, ClientPacketStartReward.class);
    	
    	notifyAll(new ServerPacketStartReward(pkt.sender, pkt.use, pkt.targetzone).toJson());
    }
    
    public void onEventGamble(JsonNode node)
    {
    	ClientPacketEventGamble pkt = Json.fromJson(node, ClientPacketEventGamble.class);
    	
    	int card = CardTable.getInstance().getEventCard();
    	
    	//TODO : exclusive S, A card
    	
    	
    	notifyAll(new ServerPacketEventGamble(pkt.sender, pkt.index, card).toJson());    	
    }
    
    public void onBattle(JsonNode node)
    {
    	ClientPacketBattle pkt = Json.fromJson(node, ClientPacketBattle.class);
    	
    	mLastBattle = new BattleInfo();
    	mLastBattle.zoneId = pkt.zoneId;
    	mLastBattle.charId = pkt.attackId;
    	mLastBattle.cardId = pkt.attackCard;
    	
    	//notifyAll(new ServerPacketBattle(pkt.sender, pkt.index, card).toJson());    	
    }    

    public void onBattleEnd(JsonNode node)
    {
    	ClientPacketBattleEnd pkt = Json.fromJson(node, ClientPacketBattleEnd.class);
    	
    	notifyAll(new ServerPacketBattleEnd(pkt.sender,mLastBattle.charId,mLastBattle.cardId,pkt.attackwin,0,mLastBattle.zoneId).toJson());    	
    }
    
    public void onCardDeckUse(JsonNode node)
    {
    	ClientPacketCardDeckUse pkt = Json.fromJson(node, ClientPacketCardDeckUse.class);
    	
    	notifyAll(new ServerPacketCardDeckUse(pkt.sender,pkt.acttype,pkt.index,pkt.card).toJson());    	
    }
    
    public void onEventArenaReq(JsonNode node)
    {
    	ClientPacketEventArenaReq pkt = Json.fromJson(node, ClientPacketEventArenaReq.class);
    	
    	mLastBattleArena = new BattleArena();
    	mLastBattleArena.arenaCount = pkt.membercount;
    	mLastBattleArena.arenaStarter = pkt.startPlayer;
    	
    	notifyAll(new ServerPacketEventArenaReq(pkt.sender,pkt.startPlayer,pkt.membercount).toJson());    	   	
    }
    
    public void onEventArenaUse(JsonNode node)
    {
    	ClientPacketEventArenaUse pkt = Json.fromJson(node, ClientPacketEventArenaUse.class);
    	
    	final Random random = new Random();
    	int dice = random.nextInt(3) + 1;
    	CardInfo info = CardTable.getInstance().getCard(pkt.card);
    	int total = 0;
    	
    	if(info != null)
    	{
    		total = info.ap * dice;
    	}
    	
    	notifyAll(new ServerPacketEventArenaUse(pkt.sender,pkt.index,pkt.card,dice).toJson());
    	
    	synchronized(mLastBattleArena)
    	{
    		mLastBattleArena.arenaCount--;
    		mLastBattleArena.mArenaScores.add(new BattleArenaScore(pkt.sender, total));
    		
    		if(mLastBattleArena.arenaCount == 0)
    		{
    			Collections.sort(mLastBattleArena.mArenaScores);
    			
    			List<Integer> winners = new ArrayList<Integer>();
    			List<Integer> losers = new ArrayList<Integer>();
    			
    			int prevScore = 0;
    			for(BattleArenaScore bArena : mLastBattleArena.mArenaScores)
    			{
    				if(prevScore == 0)
    				{
    					prevScore = bArena.score;
    					winners.add(bArena.charId);
    				}
    				else if( prevScore == bArena.score)
    				{
    					winners.add(bArena.charId);
    				}
    				else
    					losers.add(bArena.charId);
    			}
    			
    			notifyAll(new ServerPacketEventArenaEnd(pkt.sender,mLastBattleArena.arenaStarter,winners, losers).toJson());
    			
    			mLastBattleArena = null;
    		}
    		
    	}
    	
    }
    
    public void onEventArenaReward(JsonNode node)
    {
    	ClientPacketEventArenaReward pkt = Json.fromJson(node, ClientPacketEventArenaReward.class);
    	
    	notifyAll(new ServerPacketEventArenaReward(pkt.sender,pkt.startplayer,pkt.winners,pkt.losers).toJson());    	   	
    }
    
    public void onEquipSpellUse(JsonNode node)
    {
    	ClientPacketEquipSpellUse pkt = Json.fromJson(node, ClientPacketEquipSpellUse.class);
    	
    	notifyAll(new ServerPacketEquipSpellUse(pkt.sender,pkt.spellType).toJson());    	   	
    }
    
    public void onEquipSpellUseReply(JsonNode node)
    {
    	ClientPacketEquipSpellUseReply pkt = Json.fromJson(node, ClientPacketEquipSpellUseReply.class);
    	
    	notifyAll(new ServerPacketEquipSpellUseReply(pkt.sender,pkt.spellId,mLastBattle.zoneId,pkt.use).toJson());    	   	
    }
    
    // -- Messages
    


	public class Join {
        
        final String username;
        final User user;
        
        public Join(String username, User user) {
            this.username = username;
            this.user = user;
        }
        
    }
    
    public class Talk {
        
        final String username;
        final String text;
        
        public Talk(String username, String text) {
            this.username = username;
            this.text = text;
        }
        
    }
    
    public class Quit {
        
        final long userId;
        
        public Quit(long userId) {
            this.userId = userId;
        }
        
    }
    
    class SpellUsed {

    	public int caster;
    	public int spellId;
    	public int targetchar;
    	public int targetzone;
    	public int targetzone2;
    	
    	public SpellUsed( int caster, int spellId, int targetchar, int targetzone, int targetzone2)
    	{
    		this.caster = caster;
    		this.spellId = spellId;
    		this.targetchar = targetchar;
    		this.targetzone = targetzone;
    		this.targetzone2 = targetzone2;
    	}
    }
    
    class BattleInfo {
    	public int zoneId;
    	public int charId;
    	public int cardId;
    }
    
    class BattleArena {
    	public int arenaCount;
    	public int arenaStarter;
    	public List<BattleArenaScore> mArenaScores = new ArrayList<BattleArenaScore>();
    }
    
    class BattleArenaScore implements Comparable<BattleArenaScore>{
    	public int charId;
    	public int score;
    	
    	public BattleArenaScore(int charId, int score)
    	{
    		this.charId = charId;
    		this.score = score;
    	}

		@Override
		public int compareTo(BattleArenaScore o) {
			if( this.score == o.score ) return 0;
			else if( this.score > o.score ) return -1;
			else return 1;
		}
    }
    
}
