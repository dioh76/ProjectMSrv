package models;

import play.Logger;
import play.mvc.*;
import play.libs.*;
import protocol.*;
import protocol.client.*;
import protocol.server.*;
import xml.BattleDiceTable;
import xml.CardTable;
import xml.CharTable;
import xml.GameRule;
import xml.SpellTable;
import xml.ZoneTable;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

import game.*;
import game.spell.Spell;

public class GameRoom {
	
	private long mRoomId;
	private int mMaxUser;
	private boolean mPlaying = false;
	
	private List<User> mUsers = new ArrayList<User>();
	private SortedMap<Integer, SrvCharacter> mCharacters = new TreeMap<Integer, SrvCharacter>();
	private List<Integer> mCharIds = null;
	private List<ZoneInfo> mZones = new ArrayList<ZoneInfo>();
	
	//common
	private long mCreatedTime = 0;
	
	private int mStartCharId = 0;
	private int mLastCharId = 0;
	private int mLastDoubled = 0;
	private int mCurrentTurn = 0;
	private int mCurrentRound = 0;
	
	private SpellUsed mLastSpellUsed;
	private BattleInfo mLastBattle;
	private BattleArena mLastBattleArena;
	
	private List<Integer> maporders;
	private List<Integer> mSpellCards;

	private int mCharIdSeq = 100;
	
	public GameRoom(long roomId, int maxuser)
	{
		mCreatedTime = System.currentTimeMillis();
		
		mRoomId = roomId;
		mMaxUser = maxuser;
		
		maporders = new ArrayList<Integer>();
		maporders.add(ZoneInfo.ZONE_RACE_HUMAN);
		maporders.add(ZoneInfo.ZONE_RACE_DEVIL);
		maporders.add(ZoneInfo.ZONE_RACE_ANGEL);
		//Collections.shuffle(maporders);
		
		initZones();
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
	
	public User getRandomOwner()
	{
		final Random random = new Random();
		synchronized(mUsers)
		{
			if(mUsers.size() == 0)
				return null;
			
			int index = random.nextInt(mUsers.size());
			return mUsers.get(index);
		}
	}
	
	public void addUser( User user )
	{
		synchronized(mUsers)
		{
			mUsers.add(user);
			
			user.SendPacket(new ServerPacketGameJoin(0,user.getUserId(),user.getName(),mMaxUser,mUsers.size()==1 ? true:false).toJson());
			
			ArrayList<Long> userIds = new ArrayList<Long>();
			ArrayList<String> userNames = new ArrayList<String>();
			for(User u : mUsers)
			{
				userIds.add(u.getUserId());
				userNames.add(u.getName());
			}
			
			notifyAll(new ServerPacketUserList(0,userIds,userNames).toJson());
			
	    	//initialize map for this user when user joins
	    	user.SendPacket(new ServerPacketInitZone(0, maporders).toJson());
		}
    	
		addCharacter(user);
	}
	
	public User getUser(long userId)
	{
		synchronized(mUsers)
		{
			for(User user : mUsers)
			{
				if(user.getUserId() == userId)
					return user;
			}
		}
		
		return null;
	}
	
	public void addCharacter(User user)
	{
		boolean isFull = false;
		
		
		int charType = 1;
		CharInfo charInfo = CharTable.getInstance().randomChar();		
		if(charInfo != null)
			charType = charInfo.charType;
		
		//temporarily creating seq ( client same ) 
		charType = mCharacters.size() + 1;
		
		float initSoul = GameRule.getInstance().CHAR_INIT_SOUL;
		SrvCharacter chr = new SrvCharacter(user, user.getUserId(), getNewCharId(), charType, user.getName(), true, initSoul, false );
    	
    	synchronized(mCharacters)
    	{
    		mCharacters.put(chr.charId, chr);
    		if(mCharacters.size() == mMaxUser) isFull = true;
    	}
    		
    	if(isFull)
    	{
    		initGame(false);
    	}
	}
	
	public void addRandomAICharacter(int count)
	{
		
		for( int i = 0; i < count; i++)
		{
			User randomUser = getRandomOwner();
			if(randomUser == null)
				continue;
			
			int charType = 1;
			CharInfo charInfo = CharTable.getInstance().randomChar();		
			if(charInfo != null)
				charType = charInfo.charType;
			
			charType = mCharacters.size() + 1;
			float initSoul = GameRule.getInstance().CHAR_INIT_SOUL;
			SrvCharacter chr = new SrvCharacter(randomUser,randomUser.getUserId(), getNewCharId(), charType, "AIPlayer"+(i+1), false, initSoul, false );
	    	
	    	synchronized(mCharacters)
	    	{
	    		mCharacters.put(chr.charId, chr);
	    	}
		}
	}
    
    public synchronized User removeUser( long userId )
    {
    	User user = null;
    	
    	for( int i = 0; i < mUsers.size(); i++ )
    	{
    		if( mUsers.get(i).getUserId() == userId )
    		{
    			user = mUsers.remove(i); 
    		}
    	}
    	
    	ArrayList<Integer> removes = new ArrayList<Integer>();
    	for(SrvCharacter chr : mCharacters.values())
    	{
    		if(chr.userId == userId)
    		{
    			notifyAll(new ServerPacketCharRemove(chr.charId, chr.userId).toJson());
    			removes.add(chr.charId);
    			
    			for(ZoneInfo zoneInfo : mZones)
    	    	{
    	    		if(zoneInfo.getChar() == chr.charId)
    	    		{
    	    			zoneInfo.setChar(0);
    	    			zoneInfo.setCardInfo(null);
    	    		}
    	    	}
    		}
    	}
    	
    	for(Integer charId : removes)
    	{
    		mCharacters.remove(charId);
    		if( mCharIds != null )
    		{
    			for(int i=0; i< mCharIds.size();i++)
    			{
    				if(mCharIds.get(i) == charId)
    				{
    					mCharIds.remove(i);
    					break;
    				}
    			}    			
    		}
    	}
    	
    	return user;
    }
    
    public ZoneInfo getZone(int zoneId)
    {
    	if(zoneId == -1)
    		return null;
    	
    	synchronized(mZones)
    	{
    		return mZones.get(zoneId);
    	}
    }
    
    public List<Integer> getCharZones(int charId)
    {
    	List<Integer> zones = new ArrayList<Integer>();
    	synchronized(mZones)
		{
			for(ZoneInfo zoneInfo : mZones)
			{
				if(zoneInfo.getChar() == charId)
					zones.add(zoneInfo.id);
			}
		}
		
		return zones;    	
    }
    
    public List<SrvCharacter> getAllCharacters()
    {
    	synchronized(mCharacters)
    	{
    		return new ArrayList<SrvCharacter>(mCharacters.values());
    	}
    	
    	//mCharacters.values();
    }
    
    public int getMostExpensiveZone()
    {
    	float soul = 0;
    	int zoneId = -1;
    	
    	synchronized(mZones)
    	{
    		for(ZoneInfo zoneInfo : mZones)
    		{
    			if(zoneInfo.type == ZoneInfo.ZONE_MAINTYPE_NORMAL && zoneInfo.getCardInfo() != null && zoneInfo.tollSoul() > soul)
    			{
    				soul = zoneInfo.tollSoul();
    				zoneId = zoneInfo.id;
    			}
    		}
    	}
    	
    	return zoneId;
    }
    
    private void initZones()
    {	
    	for(int i = 0; i < ZoneTable.getInstance().getZoneCount(); i++)
    	{
    		ZonePosInfo posInfo = ZoneTable.getInstance().getZonePosInfo(i);
    		
    		ZoneInfo zoneInfo = new ZoneInfo(posInfo.id,this);
    		zoneInfo.type = posInfo.type;
    		zoneInfo.info = posInfo.info;
    		if(posInfo.info != 0)
    		{
    			ZoneBasicInfo basicInfo = ZoneTable.getInstance().getZoneBasicInfo(posInfo.info);
    			zoneInfo.race = basicInfo.race;
    			zoneInfo.enhancable = basicInfo.enhancable;
    			zoneInfo.values = basicInfo.values;
    		}
    		else
    		{
    			zoneInfo.race = ZoneInfo.ZONE_RACE_NONE;
    		}
    		
    		if(zoneInfo.type == ZoneInfo.ZONE_MAINTYPE_NORMAL)
    		{
    			zoneInfo.mLinkedZones = ZoneTable.getInstance().getLinkedZones(zoneInfo.id);
    		}
       		
    		mZones.add(zoneInfo);
    	}
    }
    
    private void initGame(boolean useAI)
    {
    	
    	RoomManager.ready(this.getRoomId());
    	
		if(useAI == true && mCharacters.size() < 4)
    	{
			Logger.info("ai player will be added randomly");
			addRandomAICharacter(4 - mCharacters.size());
    	}
    	
    	//init char
		
		for( SrvCharacter chr : mCharacters.values())
		{
			notifyAll(new ServerPacketCharAdd(chr.charId, chr.userId, chr.charId, chr.charType, chr.userName, chr.userChar,chr.soul).toJson());
		}
		
		mCharIds = new ArrayList<Integer>(mCharacters.keySet());
		mCurrentRound = 1;
		
		//init spell card
		initSpellCards();
		
		notifyAll(new ServerPacketGameReady(0, mCharIds).toJson());
    }
    
    private void initSpellCards()
    {
    	mSpellCards = new ArrayList<Integer>();
    	Iterator<Integer> spellCards = SpellTable.getInstance().getInitSpellCards();
    	while(spellCards.hasNext())
    	{
    		int spellId = spellCards.next();
    		if(spellId < Spell.SYSTEM_SPELL_CARDNUM)
    			mSpellCards.add(spellId);
    	}
    	
    	Collections.shuffle(mSpellCards);
    }
    
    public void sendRanking()
    {
    	List<AssetRank> ranks = new ArrayList<AssetRank>();
    	synchronized(mCharacters)
    	{
    		for(SrvCharacter srvChr : mCharacters.values())
    		{
    			ranks.add(new AssetRank(srvChr.charId,srvChr.getZoneAssets() + srvChr.soul));
    		}
    	}
    	
    	Collections.sort(ranks);
    	
    	List<Integer> sendRanks = new ArrayList<Integer>();
    	for(AssetRank rank : ranks)
    		sendRanks.add(rank.charId);
    	
    	
    	notifyAll( new ServerPacketCharRankAsset(0,sendRanks).toJson());    
    }
    
    public void sendSoulChanged(SrvCharacter chr, boolean notify)
    {
    	boolean bankrupt = chr.soul <= 0 ? true : false;
    	notifyAll(new ServerPacketCharAddSoul(chr.charId, chr.soul, bankrupt,notify).toJson());
    }
    
    private void sendTurnStart(SrvCharacter chr, boolean doubledice)
    {
    	if(chr == null)
    		return;
    	
    	chr.myturn = true;
    	
    	//start turn state to all
    	notifyAll(new ServerPacketCharTurnStart(chr.charId,doubledice).toJson());
    	
    	User user = getUser(chr.userId);
		if(user == null)
		{
			Logger.debug("user is null user id="+chr.userId);
			return;
		}
		
	  	//bankrupt		
		if(chr.soul < 0)
		{
			user.SendPacket(new ServerPacketCharBankruptReq(chr.charId).toJson());
			return;
		}
    	
    	//check turnskip buff
    	for(int i = chr.mBuffs.size() - 1; i >=0; i--)
		{
			Buff buff = chr.mBuffs.get(i);
			if(buff.buffType == Buff.TURN_SKIP)
			{
				buff.turnOver();
				chr.doubledice = 0;
				notifyAll(new ServerPacketCharTurnSkip(chr.charId).toJson());
				return;
			}
		}
    	
    	//check use spell instead of roll dice( e.g. portal)
    	for(int i = chr.mBuffs.size() - 1; i >=0; i--)
		{
			Buff buff = chr.mBuffs.get(i);
			if(buff.buffType == Buff.SPELL_USE)
			{
				chr.doubledice = 0;
				notifyAll(new ServerPacketBuffUseReq(chr.charId,buff.id).toJson());
				return;
			}
		}
    	
    	//roll dice
		user.SendPacket(new ServerPacketRollDiceReq(chr.charId).toJson());
    }
    
    private void sendTurnOver(SrvCharacter chr)
    {
    	if(chr == null)
    		return;
    	
    	chr.myturn = false;
    	
    	//send only charId
    	notifyAll(new ServerPacketCharTurnOver(chr.charId,chr.charId,false,false,0).toJson());
    }
    
    private void sendRoundOver(int chrId, int nextChrId)
    {
    	synchronized(mCharacters)
    	{
    		for( SrvCharacter srvChr : mCharacters.values() )
        	{
        		srvChr.addCard();
        	}    		
    	}
    	
    	notifyAll(new ServerPacketRoundOver(chrId,nextChrId).toJson());
    }
    
    private void sendBattleLose(SrvCharacter attChr, SrvCharacter defChr, ZoneInfo zoneInfo, boolean useSpell, int value)
    {
    	float sumPay = 0;
    	if(value != 100 || useSpell == false)
    	{
	    	//pay for lose
			sumPay = zoneInfo.tollSoul() * (100 - value) / 100;
			defChr.soul += sumPay;
	    	sendSoulChanged(defChr,true);
	    	attChr.soul -= sumPay;
	    	sendSoulChanged(attChr, true);
    	}
    	
		notifyAll(new ServerPacketCharBattleLose(attChr.charId,zoneInfo.getChar(),zoneInfo.id,useSpell,value,(int)sumPay).toJson());    	
    }
    
    public void processPacket( int protocol, JsonNode node )
    {
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
    	case ClientPacket.MCP_ROUND_START: onRoundStart(node); break;
    	case ClientPacket.MCP_CHAR_ADD_BUFF: onCharAddBuff(node); break;
    	case ClientPacket.MCP_CHAR_MOVE_BYSPELL: onCharMoveBySpell(node); break;
    	case ClientPacket.MCP_CHAR_SET_ZONE: onCharSetZone(node); break;
    	case ClientPacket.MCP_CHAR_REMOVE_ZONE: onCharRemoveZone(node); break;
    	case ClientPacket.MCP_CHAR_ADD_ZONE: onCharAddZone(node); break;
    	case ClientPacket.MCP_CHAR_PAY: onCharPay(node); break;
    	case ClientPacket.MCP_CHAR_ADDCARD: onCharAddCard(node); break;
    	case ClientPacket.MCP_CHAR_CHANGE_OWNER: onCharChangeOwner(node); break;
    	case ClientPacket.MCP_CHAR_OCCUPY: onCharOccupy(node); break;
    	case ClientPacket.MCP_ZONE_AMBUSH: onZoneAmbush(node); break;
    	case ClientPacket.MCP_ROLL_DICE: onRollDice(node); break;
    	case ClientPacket.MCP_CARD_CHANGE: onCardChange(node); break;
    	case ClientPacket.MCP_CHAR_BANKRUPT: onCharBankrupt(node); break;
    	case ClientPacket.MCP_SPELL_OPEN: onSpellOpen(node); break;
    	case ClientPacket.MCP_SPELL_REQ_USE: onSpellReqUse(node); break;
    	case ClientPacket.MCP_SPELLUSE: onSpellUse(node); break;
    	case ClientPacket.MCP_SPELLDEFENSE: onSpellDefense(node); break;
    	case ClientPacket.MCP_SPELL_EQUIP: onSpellEquip(node); break;
    	case ClientPacket.MCP_SPELLDEFENSE_REPLY: onSpellDefenseReply(node); break;
    	case ClientPacket.MCP_BUFF_USE: onBuffUse(node); break;
    	case ClientPacket.MCP_PORTAL_USE: onPortalUse(node); break;
    	case ClientPacket.MCP_ZONE_ADD_BUFF: onZoneAddBuff(node); break;
    	case ClientPacket.MCP_ZONE_DEL_BUFF: onZoneDelBuff(node); break;
    	case ClientPacket.MCP_CHAR_CONTROLLED: onCharControlled(node); break;
    	case ClientPacket.MCP_START_REWARD: onStartReward(node); break;
    	case ClientPacket.MCP_EVENT_GAMBLE: onEventGamble(node); break;
    	case ClientPacket.MCP_PLAYER_BATTLE: onBattle(node); break;
    	case ClientPacket.MCP_PLAYER_BATTLE_END: onBattleEnd(node); break;
    	case ClientPacket.MCP_EVENT_ARENA_REQ: onEventArenaReq(node); break;
    	case ClientPacket.MCP_EVENT_ARENA_USE: onEventArenaUse(node); break;
    	case ClientPacket.MCP_EVENT_ARENA_REWARD: onEventArenaReward(node); break;
    	case ClientPacket.MCP_EQUIP_SPELL_USE: onEquipSpellUse(node); break;
    	case ClientPacket.MCP_EQUIP_SPELL_USE_REPLY: onEquipSpellUseReply(node); break;
    	case ClientPacket.MCP_START_ENHANCE: onStartEnhance(node); break;
    	case ClientPacket.MCP_GAME_READY: onGameReady(node); break;
    	case ClientPacket.MCP_GAME_INITDECKS: onGameInitDecks(node); break;
    	}
    }
    
    public synchronized void Update(long currentmillisec)
    {
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
    
    //Packet Handle
    
    private void onCharAdd(JsonNode node)
    {
    	//not to be used in server
    	ClientPacketCharAdd pkt = Json.fromJson(node, ClientPacketCharAdd.class);
    	
    	User user = getUser(pkt.userId);
    	
    	if(user == null)
    	{
    		Logger.debug("[E] user not founded..");
    		return;
    	}
    	
    	SrvCharacter chr = new SrvCharacter(user,pkt.userId, pkt.charId, 1, pkt.name, pkt.userChar, GameRule.getInstance().CHAR_INIT_SOUL, false );    	
    	mCharacters.put(pkt.charId, chr);
    	
    	notifyAll(new ServerPacketCharAdd(pkt.charId, pkt.userId, pkt.charId, chr.charType, pkt.name, pkt.userChar,chr.soul).toJson());   	
    }
    
    private void onCharAddSoul(JsonNode node)
    {
    	ClientPacketCharAddSoul pkt = Json.fromJson(node, ClientPacketCharAddSoul.class);
    	
    	SrvCharacter chr = mCharacters.get(pkt.sender);
    	
    	if( chr == null )
    		return;
    	
    	chr.soul += pkt.addsoul;
    	sendSoulChanged(chr,true);
    	
    	sendRanking();
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
    		
			mStartCharId = mCharIds.get(random.nextInt(mCharacters.size())).intValue();
			mLastCharId = mStartCharId;
    		
			notifyAll(new ServerPacketGameStart(pkt.sender, mStartCharId).toJson());
			
			SrvCharacter startChr = mCharacters.get(mStartCharId);
			
			sendTurnStart(startChr, false);
    	}
    }        
    
    private void onRollDice(JsonNode node)
    {
    	ClientPacketRollDice pkt = Json.fromJson(node, ClientPacketRollDice.class);
    	
    	SrvCharacter chr = mCharacters.get(pkt.sender);
    	if(chr == null)
    		return;
    	
    	if(chr.myturn == false)
    		return;
    	
    	final Random random = new Random();
    	
    	if(pkt.rVal != 0 && pkt.bVal != 0)
    	{
        	chr.dice1 = pkt.rVal;
        	chr.dice2 = pkt.bVal;
    	}
    	else
    	{
        	chr.dice1 = random.nextInt(6) + 1;
        	chr.dice2 = random.nextInt(6) + 1;
    	}
    	
    	if(chr.dice1 == chr.dice2 && chr.doubledice < 3)
    		chr.doubledice++;
    	else
    		chr.doubledice = 0;
    	
    	notifyAll(new ServerPacketRollDice(pkt.sender,chr.dice1,chr.dice2,chr.doubledice > 0 ? true : false).toJson());
    }
    
    private void onCardChange(JsonNode node)
    {
    	ClientPacketCardChange pkt = Json.fromJson(node, ClientPacketCardChange.class);
    	
    	SrvCharacter chr = mCharacters.get(pkt.sender);
    	if(chr == null)
    		return;
    	
    	ZoneInfo zoneInfo = mZones.get(pkt.zId);
    	if(zoneInfo == null)
    		return;
    	
    	if(zoneInfo.getCardInfo() == null)
    		return;
    	
    	
    	//no return cost
    	int prevCard = zoneInfo.getCardInfo().cardId;
    	
    	chr.removeCard(pkt.cId);
    	chr.addCard(prevCard);
    	  
    	CardInfo cardInfo = CardTable.getInstance().getCard(pkt.cId);
    	chr.soul -= cardInfo.cost;
    	
    	sendSoulChanged(chr,false);
    	
    	chr.removeZoneAsset(pkt.zId);
		notifyAll( new ServerPacketCharRemoveZone(chr.charId,pkt.zId,false,true).toJson());
    	
		zoneInfo.setCardInfo(cardInfo);
    	chr.addZoneAsset(zoneInfo.id, zoneInfo.tollSoul());
    	notifyAll( new ServerPacketCharAddZone(chr.charId,zoneInfo.id,cardInfo.cardId,chr.charId,false,-1).toJson());    
		
    	notifyAll( new ServerPacketCharZoneAsset(chr.charId,chr.getZoneCount(),chr.getZoneAssets()).toJson());
    	
    	sendRanking();
    	
    	notifyAll(new ServerPacketCardChange(pkt.sender,pkt.zId,pkt.idx,pkt.cId,prevCard).toJson());
    }
    
    private void onCharBankrupt(JsonNode node)
    {
    	ClientPacketCharBankrupt pkt = Json.fromJson(node, ClientPacketCharBankrupt.class);
    	
    	SrvCharacter chr = mCharacters.get(pkt.sender);
    	if( chr == null )
    		return;
    	
    	if(chr.myturn == false)
    	{
    		Logger.debug("myturn is false chr = " + chr.charId);
    		return;
    	}
    	
    	for(int i = chr.mBuffs.size() - 1; i >=0; i--)
		{
    		Buff buff = chr.mBuffs.get(i);
			chr.mBuffs.remove(i);
				
			notifyAll(new ServerPacketCharDelBuff(pkt.sender,buff.id,buff.targetchar).toJson()); 
		}
    	
    	boolean roundover = false;
		if(mCurrentTurn + 1 >= mCharacters.size())
		{
			mCurrentTurn = 0;
			roundover = true;
		}
		
		//check next char in advance
		int nextIndex = 0;
		for(int i=0; i < mCharIds.size(); i++)
		{
			if( mCharIds.get(i) == chr.charId )
			{
				if( i+1 <= mCharIds.size() - 1 )
					nextIndex = i+1;
				break;
			}
		}
		
		int nextCharId = mCharIds.get(nextIndex);
		
		SrvCharacter nextChr = mCharacters.get(nextCharId);
		if(nextChr == null)
		{
			Logger.debug("next turn char is null " + nextCharId);
			return;
		}
		
		if(mStartCharId == chr.charId)
			mStartCharId = nextCharId;
		
		if(roundover)
		{
			for(ZoneInfo zoneInfo : mZones)
    		{
    			Buff buff = zoneInfo.getBuff();
    			if(buff != null)
    			{
    				buff.turnOver();
    				if(buff.isValid() == false)
    				{
    					Logger.info("remove zone buff..");
    					notifyAll(new ServerPacketZoneDelBuff(pkt.sender,buff.id,buff.targetzone).toJson());
    					zoneInfo.setBuff(null);
    				}
    			}
    		}
    		
    		if( mCurrentRound == GameRule.getInstance().GAMEEND_MAX_TURN)
    		{
    			notifyAll(new ServerPacketGameOver(pkt.sender).toJson());
    		}
    		else
    		{
    			mCurrentRound++;
    			
    			sendRoundOver(pkt.sender, mStartCharId);
    			//notifyAll(new ServerPacketRoundOver(pkt.sender,mStartCharId).toJson());
    			
    			sendTurnOver(chr);
    		}			
		}
		else
		{
			
			sendTurnOver(chr);
			sendTurnStart(nextChr, false);
		}
		
		//Remove Char Info
		for(ZoneInfo zoneInfo : mZones)
    	{
    		if(zoneInfo.getChar() == chr.charId)
    		{
    			zoneInfo.setChar(0);
    			zoneInfo.setCardInfo(null);
    		}
    	}
    	
		mCharacters.remove(chr.charId);
		if( mCharIds != null )
		{
			for(int i=0; i< mCharIds.size();i++)
			{
				if(mCharIds.get(i) == chr.charId)
				{
					mCharIds.remove(i);
					break;
				}
			}
		}
    	
    	notifyAll(new ServerPacketCharRemove(pkt.sender,chr.userId).toJson());		
    	
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
    	
    	SrvCharacter chr = mCharacters.get(pkt.sender);
    	if( chr == null )
    		return;
    	
    	ZoneInfo zoneInfo = mZones.get(pkt.zId);
    	if(zoneInfo == null)
    		return;
    	
    	chr.curzone = pkt.zId;
    	
    	if(zoneInfo.getCardInfo()!= null && zoneInfo.getChar() != pkt.sender)
    	{
    		notifyAll(new ServerPacketCharBattleNotify(pkt.sender,chr.charId,zoneInfo.getChar()).toJson());
    	}
    	
    	User user = getUser(chr.userId);
		if(user != null)
			user.SendPacket(new ServerPacketCharMoved(pkt.sender,pkt.zId).toJson());
    	
    	//notifyAll(new ServerPacketCharMoved(pkt.sender,pkt.zId).toJson());   	
    }  
    
    private void onCharEnhance(JsonNode node)
    {
    	ClientPacketCharEnhance pkt = Json.fromJson(node, ClientPacketCharEnhance.class);
    	
    	SrvCharacter chr = mCharacters.get(pkt.sender);
    	if( chr == null )
    		return;
    	
    	ZoneInfo zoneInfo = mZones.get(pkt.zId);
    	if(zoneInfo == null)
    		return;
    	
    	if(zoneInfo.getChar() != pkt.sender)
    		return;
    	
    	if(zoneInfo.getLevel() >= 2 || zoneInfo.getLevel() < 0)
    		return;
    	
    	zoneInfo.setLevel(zoneInfo.getLevel() + 1);
    	chr.soul -= zoneInfo.buySoul();

    	float asset = zoneInfo.tollSoul();
    	chr.addZoneAsset(zoneInfo.id, asset);
    	
    	notifyAll(new ServerPacketCharEnhance(pkt.sender,pkt.zId,zoneInfo.getLevel(),chr.soul,chr.getZoneCount(),chr.getZoneAssets(),true,true).toJson());
    	
    	sendRanking();
    }   
    
    private void onCharPassByStart(JsonNode node)
    {
    	ClientPacketCharPassByStart pkt = Json.fromJson(node, ClientPacketCharPassByStart.class);
    	
    	SrvCharacter chr = mCharacters.get(pkt.sender);
    	
    	if( chr == null )
    		return;
    	
    	chr.soul += GameRule.getInstance().BOUNS_START_SOUL;
    	sendSoulChanged(chr,false);
    	
    	notifyAll(new ServerPacketCharPassByStart(pkt.sender).toJson());
    }
    
    private void onCharTurnOver(JsonNode node)
    {
    	ClientPacketCharTurnOver pkt = Json.fromJson(node, ClientPacketCharTurnOver.class);
    	
    	SrvCharacter chr = mCharacters.get(pkt.sender);
    	if(chr == null)
    		return;
    	
    	Logger.debug("[srv] turn over char = " + pkt.sender);
    	
    	charTurnOver(chr);
    }
    
    private void charTurnOver(SrvCharacter chr)
    {
       	//char is controlled by spell and turn over
    	if(chr.controlled)
    	{
    		chr.controlled = false;
    		
    		//assign to origin turn owner
    		chr = mCharacters.get(chr.spellcaster);
    	}
    	
    	if(chr.myturn == false)
    	{
    		Logger.debug("myturn is false chr = " + chr.charId);
    		return;
    	}
    	
    	if(chr.doubledice != 0)
    	{
    		sendTurnStart(chr, true);
    		return;
    	}
    	
		for(int i = chr.mBuffs.size() - 1; i >=0; i--)
		{
			Buff buff = chr.mBuffs.get(i);
			
			//turn skip buff is checked in sendTurnStart func
			if(buff.buffType != Buff.TURN_SKIP)
				buff.turnOver();
			
			if(buff.isValid() == false)
			{
				chr.mBuffs.remove(i);				
				notifyAll(new ServerPacketCharDelBuff(chr.charId,buff.id,buff.targetchar).toJson()); 
			}
		}
		
		boolean roundover = false;
		mCurrentTurn++;
		if(mCurrentTurn >= mCharacters.size())
		{
			mCurrentTurn = 0;
			roundover = true;
		}
		
		if(roundover)
		{
			for(ZoneInfo zoneInfo : mZones)
    		{
    			Buff buff = zoneInfo.getBuff();
    			if(buff != null)
    			{
    				buff.turnOver();
    				if(buff.isValid() == false)
    				{
    					Logger.info("remove zone buff..");
    					notifyAll(new ServerPacketZoneDelBuff(chr.charId,buff.id,buff.targetzone).toJson());
    					zoneInfo.setBuff(null);
    				}
    			}
    		}
    		
    		if( mCurrentRound == GameRule.getInstance().GAMEEND_MAX_TURN)
    		{
    			notifyAll(new ServerPacketGameOver(chr.charId).toJson());
    		}
    		else
    		{
    			mCurrentRound++;
    			
    			sendRoundOver(chr.charId,mStartCharId);
    			//notifyAll(new ServerPacketRoundOver(chr.charId,mStartCharId).toJson());
    			
    			sendTurnOver(chr);
    		}			
		}
		else
		{
	    	int nextIndex = 0;
			for(int i=0; i < mCharIds.size(); i++)
			{
				if( mCharIds.get(i) == chr.charId )
				{
					if( i+1 <= mCharIds.size() - 1 )
						nextIndex = i+1;
					break;
				}
			}
			
			int nextCharId = mCharIds.get(nextIndex);
			
			SrvCharacter nextChr = mCharacters.get(nextCharId);
			if(nextChr == null)
			{
				Logger.debug("next turn char is null " + nextCharId);
				return;
			}
			
			sendTurnOver(chr);
			sendTurnStart(nextChr, false);
		}    	
    }
    private void onRoundStart(JsonNode node)
    {
    	ClientPacketRoundStart pkt = Json.fromJson(node, ClientPacketRoundStart.class);
    	
    	//process only starter packet 
    	if(pkt.sender != mStartCharId)
    		return;
    	
    	SrvCharacter nextChr = mCharacters.get(mStartCharId);
		if(nextChr == null)
		{
			Logger.debug("[roundover]next turn char is null " + mStartCharId);
			return;
		}
		
		sendTurnStart(nextChr, false);
    }
    
    private void onCharAddBuff(JsonNode node)
    {
    	ClientPacketCharAddBuff pkt = Json.fromJson(node, ClientPacketCharAddBuff.class);
    	
    	charAddBuff(pkt.sender,pkt.bufftype,pkt.targetvalue,pkt.targetchar,pkt.targetzone,pkt.remainturn,pkt.creature,pkt.spellid);   	
    }
    
    public void charAddBuff(int ownerId, int buffType, int targetVal, int targerChr, int targetZone, int remain, boolean creature, int spellId)
    {
    	int objectId = Buff.getNewBuffID();
    	
    	Buff buff = new Buff();
    	buff.id = objectId;
    	buff.owner = ownerId;
    	buff.buffType = buffType;
    	buff.targetchar = targerChr;
    	buff.targetzone = targetZone;
    	buff.remainturn = remain;
    	buff.creature = creature;
    	buff.value1 = targetVal;
    	
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
    	
    	notifyAll(new ServerPacketCharAddBuff(ownerId,objectId,buffType,targetVal,targerChr,targetZone,remain,creature,spellId).toJson());    	
    }
    
    private void onCharMoveBySpell(JsonNode node)
    {
    	ClientPacketCharMoveBySpell pkt = Json.fromJson(node, ClientPacketCharMoveBySpell.class);
    	
    	notifyAll(new ServerPacketCharMoveBySpell(pkt.sender,pkt.move,pkt.reverse,pkt.bonus).toJson());       	
    }
    
    private void onCharSetZone(JsonNode node)
    {
    	ClientPacketCharSetZone pkt = Json.fromJson(node, ClientPacketCharSetZone.class);
    	
    	SrvCharacter chr = mCharacters.get(pkt.sender);
    	
    	if( chr == null )
    		return;
    	
    	chr.addZoneAsset(pkt.zId, pkt.zVal);
    	mZones.get(pkt.zId).setChar(chr.charId);
    	
    	if(pkt.buy)
    	{
    		CardInfo cardInfo = CardTable.getInstance().getCard(pkt.cId);
    		chr.soul -= cardInfo.cost;
    		sendSoulChanged(chr,false);
    	}
    	
    	notifyAll( new ServerPacketCharZoneAsset(pkt.sender,chr.getZoneCount(),chr.getZoneAssets()).toJson());
    	
    	sendRanking();    	
    }
    
    private void onCharRemoveZone(JsonNode node)
    {
    	ClientPacketCharRemoveZone pkt = Json.fromJson(node, ClientPacketCharRemoveZone.class);
    	
    	SrvCharacter chr = mCharacters.get(pkt.sender);
    	
    	if( chr == null )
    		return;
    	
    	if(pkt.sell)
    	{
    		chr.soul += mZones.get(pkt.zId).sellSoul();
    		sendSoulChanged(chr,false);
    	}
    	
    	chr.removeZoneAsset(pkt.zId);
    	mZones.get(pkt.zId).setChar(0);
    	mZones.get(pkt.zId).setCardInfo(null);
    	
    	//check remove buff or not
    	
    	notifyAll( new ServerPacketCharZoneAsset(pkt.sender,chr.getZoneCount(),chr.getZoneAssets()).toJson());
    	
    	sendRanking();
    	
    	notifyAll( new ServerPacketCharRemoveZone(pkt.sender,pkt.zId,pkt.sell,pkt.npconly).toJson());
    }
    
    private void onCharAddZone(JsonNode node)
    {
    	ClientPacketCharAddZone pkt = Json.fromJson(node, ClientPacketCharAddZone.class);
    	
    	SrvCharacter chr = mCharacters.get(pkt.sender);
    	
    	if( chr == null )
    		return;
    	
    	charAddZone(chr,pkt.zId,pkt.cId,pkt.buy,pkt.lIdx);
    }
    
    private void charAddZone(SrvCharacter chr, int zoneId, int cardId, boolean buy, int index )
    {
    	ZoneInfo zoneInfo = mZones.get(zoneId);
    	if(zoneInfo == null)
    		return;

    	CardInfo cardInfo = CardTable.getInstance().getCard(cardId);
    	if(cardInfo == null)
    		return;
    	
    	zoneInfo.setChar(chr.charId);
    	zoneInfo.setCardInfo(cardInfo);
    	
    	chr.addZoneAsset(zoneId, zoneInfo.tollSoul());
    	
    	if(buy)
    	{
			chr.soul -= cardInfo.cost;
			sendSoulChanged(chr,false);
    	}
    	
    	notifyAll( new ServerPacketCharZoneAsset(chr.charId,chr.getZoneCount(),chr.getZoneAssets()).toJson());
    	
    	sendRanking(); 
    	
    	notifyAll( new ServerPacketCharAddZone(chr.charId,zoneId,cardId,chr.charId,buy,index).toJson());    	
    }
    
    private void onCharPay(JsonNode node)
    {
    	ClientPacketCharPay pkt = Json.fromJson(node, ClientPacketCharPay.class);
    	
    	SrvCharacter chr = mCharacters.get(pkt.sender);
    	
    	if( chr == null )
    		return;
    	
    	ZoneInfo zoneInfo = mZones.get(pkt.zId);
    	if(zoneInfo == null)
    		return;
    	
    	int ownerId = zoneInfo.getChar();
    	SrvCharacter owner = mCharacters.get(ownerId);
    	if(owner == null)
    		return;

    	float fToll = zoneInfo.tollSoul();
    	
    	chr.soul -= fToll;
    	sendSoulChanged(chr,false);
    	owner.soul += fToll;
    	sendSoulChanged(owner,false);
    	
    	notifyAll(new ServerPacketCharPay(pkt.sender,pkt.zId).toJson());
    	chr.removeZoneAsset(pkt.zId);
    	mZones.get(pkt.zId).setChar(0);
    	
    	sendRanking();   	
    }
    
    private void onCharAddCard(JsonNode node)
    {
    	ClientPacketCharAddCard pkt = Json.fromJson(node, ClientPacketCharAddCard.class);
    	
    	boolean allready = true;
		
		synchronized(mCharacters)
		{
			SrvCharacter chr = mCharacters.get(pkt.sender);
	    	if( chr == null )
	    		return;
	    	
	    	chr.addcard = true;
	    	for( SrvCharacter srvChr : mCharacters.values() )
	    	{
	    		if(srvChr.addcard == false)
	    		{
	    			allready = false;
	    			break;
	    		}
	    	}
		}
		
		if(allready)
		{		
			synchronized(mCharacters)
			{
				for( SrvCharacter srvChr : mCharacters.values() )
    	    		srvChr.addcard = false;        		
			}
			
			//Check if start char is changed
			mCurrentRound++;
			
			notifyAll(new ServerPacketRoundOver(pkt.sender,mStartCharId).toJson());
		}    
	}
    
    private void onCharChangeOwner(JsonNode node)
    {
    	ClientPacketCharChangeOwner pkt = Json.fromJson(node, ClientPacketCharChangeOwner.class);
    	
    	SrvCharacter toChar = mCharacters.get(pkt.toId);
    	
    	if( toChar == null )
    		return;
    	
    	SrvCharacter fromChar = mCharacters.get(pkt.fromId);
    	
    	if( fromChar == null )
    		return;    	
    	
    	ZoneInfo zoneInfo = mZones.get(pkt.zId);
    	if(zoneInfo == null)
    		return;
    	
    	zoneInfo.setChar(toChar.charId);
    	
    	float asset = zoneInfo.tollSoul();
    	toChar.addZoneAsset(pkt.zId, asset);
    	fromChar.removeZoneAsset(pkt.zId);
    	
    	notifyAll(new ServerPacketCharChangeOwner(pkt.sender,pkt.zId,pkt.toId,toChar.getZoneCount(),toChar.getZoneAssets(),pkt.fromId,fromChar.getZoneCount(),fromChar.getZoneAssets()).toJson());
    	sendRanking();
	}
    
    private void onCharOccupy(JsonNode node)
    {
    	ClientPacketCharOccupy pkt = Json.fromJson(node, ClientPacketCharOccupy.class);
    	
    	SrvCharacter chr = mCharacters.get(pkt.sender);
    	if(chr == null)
    		return;
    	
    	ZoneInfo zoneInfo = mZones.get(pkt.zId);
    	if(zoneInfo == null)
    		return;
    	
    	chr.removeCard(pkt.cId);
    	
    	if(zoneInfo.getAmbush())
    	{	
    		if(zoneInfo.getAmbushOwner() == pkt.sender)
    		{
    			zoneInfo.setAmbush(false, 0);  			
    			Logger.info("occupy and buff free");
    		}
    		else
    		{
    			CardInfo cardInfo = CardTable.getInstance().getCard(pkt.cId);
   				chr.soul -= cardInfo.cost;
   				sendSoulChanged(chr,false);
    	    	sendRanking(); 
    	    	
    			zoneInfo.setAmbush(false, 0);
    			notifyAll(new ServerPacketCharOccupyAmbush(pkt.sender,pkt.zId,pkt.idx,pkt.cId).toJson());
    			return;
    		}
    	}
    	
    	charAddZone(chr,zoneInfo.id,pkt.cId,true,pkt.idx);   	
    	
    	notifyAll(new ServerPacketCharOccupy(pkt.sender,pkt.zId,pkt.idx,pkt.cId).toJson());
	}
    
    private void onZoneAmbush(JsonNode node)
    {
    	ClientPacketZoneAmbush pkt = Json.fromJson(node, ClientPacketZoneAmbush.class);
    	
    	ZoneInfo zoneInfo = mZones.get(pkt.zId);
    	if(zoneInfo == null)
    		return;
    	
    	zoneInfo.setAmbush(true, pkt.sender);
    	
    	notifyAll(new ServerPacketZoneAmbush(pkt.sender,pkt.zId,true).toJson());    
	}
    
    private void onSpellOpen(JsonNode node)
    {
    	ClientPacketSpellOpen pkt = Json.fromJson(node, ClientPacketSpellOpen.class);
    	
    	if(mSpellCards.size() == 0)
    		initSpellCards();
    	
    	int spellId = mSpellCards.remove(0);
    	if(pkt.spellId != 0)
    		spellId = pkt.spellId;
    	
    	notifyAll(new ServerPacketSpellOpen(pkt.sender,spellId).toJson());   	
    }
    
    private void onSpellReqUse(JsonNode node)
    {
    	ClientPacketSpellReqUse pkt = Json.fromJson(node, ClientPacketSpellReqUse.class);
    	
    	notifyAll(new ServerPacketSpellReqUse(pkt.sender, pkt.spellid).toJson());   	
    }
    
    private void onSpellUse(JsonNode node)
    {
    	ClientPacketSpellUse pkt = Json.fromJson(node, ClientPacketSpellUse.class);
    	
    	SrvCharacter castChr = mCharacters.get(pkt.sender);
    	SrvCharacter targetChr = null;
    	if(pkt.targetchar != -1) targetChr = mCharacters.get(pkt.targetchar);
    	ZoneInfo zoneInfo1 = null;
    	if(pkt.targetzone != -1) zoneInfo1 = mZones.get(pkt.targetzone);
    	ZoneInfo zoneInfo2 = null;
    	if(pkt.targetzone2 != -1) zoneInfo2 = mZones.get(pkt.targetzone2);
    	
    	if(targetChr != null)
    		targetChr.lastspell = new SpellCasted(pkt.sender, pkt.spellid, pkt.targetchar, pkt.targetzone, pkt.targetzone2);
    	
    	Spell spellInfo = SpellTable.getInstance().getSpell(pkt.spellid);
    	
    	if(spellInfo == null)
    		return;
    	
    	
    	//exclusive for grasshopper attack all
    	if(pkt.targetchar != -1 && pkt.sender != pkt.targetchar /*&& spellInfo.spellType != SpellInfo.SPELL_ATTACKALL*/)
    	{
    		SrvCharacter chr = mCharacters.get(pkt.targetchar);
    		if(chr == null)
    			return;
    		
    		if(chr.hasEquipSpell(Spell.SPELL_IMMUNE))
    		{
    			
    			User user = getUser(chr.userId);
    			if(user != null)
    				user.SendPacket(new ServerPacketSpellDefense(chr.charId,chr.charId).toJson());

    		}
    		else
    		{
    			boolean turnover = spellInfo.onUse(spellInfo.spellId, this, castChr, targetChr, zoneInfo1, zoneInfo2);
    			
    			notifyAll(new ServerPacketSpellUse(pkt.sender, pkt.spellid, pkt.targetchar, pkt.targetzone, pkt.targetzone2,turnover).toJson());
    		}
    	}
    	else
    	{
    		boolean turnover = spellInfo.onUse(spellInfo.spellId, this, castChr, targetChr, zoneInfo1, zoneInfo2);
    		
    		notifyAll(new ServerPacketSpellUse(pkt.sender, pkt.spellid, pkt.targetchar, pkt.targetzone, pkt.targetzone2,turnover).toJson());
    	}   	
    }
    
    private void onSpellDefense(JsonNode node)
    {
    	ClientPacketSpellDefense pkt = Json.fromJson(node, ClientPacketSpellDefense.class);
    	
    	notifyAll(new ServerPacketSpellDefense(pkt.sender, pkt.defender).toJson());   	
    }
    
    private void onSpellEquip(JsonNode node)
    {
    	ClientPacketSpellEquip pkt = Json.fromJson(node, ClientPacketSpellEquip.class);
    	
    	SrvCharacter chr = mCharacters.get(pkt.sender);
    	if(chr == null)
    		return;
    	
    	chr.mEquipSpells.add(pkt.spellid);
    	
    	notifyAll(new ServerPacketSpellEquip(pkt.sender, pkt.spellid).toJson());   	
    }
    
    private void onSpellDefenseReply(JsonNode node)
    {
    	ClientPacketSpellDefenseReply pkt = Json.fromJson(node, ClientPacketSpellDefenseReply.class);
    	
    	SrvCharacter chr = mCharacters.get(pkt.sender);
    	if(chr == null)
    		return;
    	
    	if(chr.lastspell != null)
    	{
    		boolean turnover = true;
    		if(pkt.use == true)
    		{
    			int removeSpellId = chr.removeEquipSpell(Spell.SPELL_IMMUNE);
    			if(removeSpellId != -1)
    			{
    				notifyAll(new ServerPacketEquipSpellRemove(pkt.sender,removeSpellId).toJson());
    			}
    		}
    		else
    		{
    			Spell spellInfo = SpellTable.getInstance().getSpell(chr.lastspell.spellId);
    	    	if(spellInfo == null)
    	    		return;
    	    	
    	    	SrvCharacter castChr = mCharacters.get(chr.lastspell.caster);
    	    	ZoneInfo zoneInfo1 = null;
    	    	if(chr.lastspell.targetzone != -1) zoneInfo1 = mZones.get(chr.lastspell.targetzone);
    	    	ZoneInfo zoneInfo2 = null;
    	    	if(chr.lastspell.targetzone2 != -1) zoneInfo2 = mZones.get(chr.lastspell.targetzone2);    	    	
    	    	
    	    	turnover = spellInfo.onUse(spellInfo.spellId, this, castChr, chr, zoneInfo1, zoneInfo2);
    		}
    		
    		notifyAll(new ServerPacketSpellDefenseReply(chr.lastspell.caster, pkt.defender, chr.lastspell.spellId, chr.lastspell.targetchar, chr.lastspell.targetzone, chr.lastspell.targetzone2, pkt.use,turnover).toJson());	
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
    
    private void onZoneAddBuff(JsonNode node)
    {
    	ClientPacketZoneAddBuff pkt = Json.fromJson(node, ClientPacketZoneAddBuff.class);
    	
    	zoneAddBuff(pkt.sender,pkt.bType,pkt.zId,pkt.val,pkt.remain,pkt.sId);
    }
    
    public void zoneAddBuff(int ownerId, int buffType, int zoneId, int val,int remain, int spellId)
    {
    	int objectId = Buff.getNewBuffID();
    	
    	Buff buff = new Buff();
    	buff.id = objectId;
    	buff.owner = ownerId;
    	buff.buffType = buffType;
    	buff.targetchar = -1;
    	buff.targetzone = zoneId;
    	buff.remainturn = remain;
    	buff.creature = true;
    	buff.value1 = val;
    	
    	ZoneInfo zoneInfo = mZones.get(zoneId);
    	if(zoneInfo == null)
    		return;
    	
    	buff.setZoneInfo(zoneInfo);
    	
    	Buff prevBuff = zoneInfo.getBuff();
    	if(prevBuff != null)
    	{
    		notifyAll(new ServerPacketZoneDelBuff(ownerId,prevBuff.id,prevBuff.targetzone).toJson());
    		zoneInfo.setBuff(null);
    	}
    	
    	zoneInfo.setBuff(buff);
    	    	
    	notifyAll(new ServerPacketZoneAddBuff(ownerId,objectId,buffType,val,zoneId,remain,spellId).toJson());      	
    }
    
    private void onZoneDelBuff(JsonNode node)
    {
    	ClientPacketZoneDelBuff pkt = Json.fromJson(node, ClientPacketZoneDelBuff.class);
    	
    	ZoneInfo zoneInfo = mZones.get(pkt.zId);
    	if(zoneInfo == null)
    		return;
    	
    	Buff prevBuff = zoneInfo.getBuff();
    	if(prevBuff != null)
    	{
    		notifyAll(new ServerPacketZoneDelBuff(pkt.sender,prevBuff.id,prevBuff.targetzone).toJson());
    		zoneInfo.setBuff(null);
    	} 	
    }
    
    private void onCharControlled(JsonNode node)
    {
    	ClientPacketCharControlled pkt = Json.fromJson(node, ClientPacketCharControlled.class);
    	
    	SrvCharacter chr = mCharacters.get(pkt.sender);
    	if(chr == null)
    		return;
    	
    	chr.controlled = true;
    	chr.spellcaster = pkt.caster;
    }
    
    public void onStartReward(JsonNode node)
    {
    	ClientPacketStartReward pkt = Json.fromJson(node, ClientPacketStartReward.class);
    	
    	if(pkt.use)
    	{
    	   	SrvCharacter chr = mCharacters.get(pkt.sender);
        	if( chr == null )
        		return;
        	
        	ZoneInfo zoneInfo = mZones.get(pkt.targetzone);
        	if(zoneInfo == null)
        		return;
        	
        	if(zoneInfo.getChar() != pkt.sender)
        		return;
        	
        	if(zoneInfo.getLevel() >= 2 || zoneInfo.getLevel() < 0)
        		return;
        	
        	zoneInfo.setLevel(zoneInfo.getLevel() + 1);
        	chr.soul -= zoneInfo.buySoul();
        	float asset = zoneInfo.tollSoul();
        	chr.addZoneAsset(zoneInfo.id, asset);
        	
        	notifyAll(new ServerPacketCharEnhance(pkt.sender,pkt.targetzone,zoneInfo.getLevel(),chr.soul,chr.getZoneCount(),chr.getZoneAssets(),false,false).toJson());
        	
        	sendRanking();
    	}
    	
    	notifyAll(new ServerPacketStartReward(pkt.sender, pkt.use, pkt.targetzone).toJson());
    }
    
    public void onEventGamble(JsonNode node)
    {
    	ClientPacketEventGamble pkt = Json.fromJson(node, ClientPacketEventGamble.class);
    	
    	SrvCharacter chr = mCharacters.get(pkt.sender);
    	if( chr == null )
    		return;
    	
    	chr.soul -= 30;
    	sendSoulChanged(chr,false);
    	
    	int card = CardTable.getInstance().getEventCard();
    	CardInfo info = CardTable.getInstance().getCard(card);
    	if(info != null)
    	{
    		if(info.grade != CardInfo.CARD_GRADE_S && info.grade != CardInfo.CARD_GRADE_A)
    			card = -1;
    	}
    	else
    		card = -1;
    	
    	if(card != -1)
    		chr.addCard(card);
    	
    	notifyAll(new ServerPacketEventGamble(pkt.sender, pkt.index, card).toJson());    	
    }
    
    public void onBattle(JsonNode node)
    {
    	ClientPacketBattle pkt = Json.fromJson(node, ClientPacketBattle.class);
    	
    	//attack character
    	SrvCharacter chr = mCharacters.get(pkt.sender);
    	if(chr == null)
    		return;
    	
    	if(pkt.idx == -1)
    	{
    		Logger.debug("attack card is not valid.");
    		return;
    	}
    	
    	ZoneInfo zoneInfo = mZones.get(pkt.zId);
    	
    	//defense character
    	SrvCharacter chrDef = mCharacters.get(zoneInfo.getChar());
    	if(chrDef == null)
    		return;
    	
    	chr.removeCard(pkt.atId);
    	
    	CardInfo cardInfo = CardTable.getInstance().getCard(pkt.atId);
    	CharInfo charInfo = CharTable.getInstance().getChar(chr.charType);
    	float totalSt = (charInfo.st + cardInfo.st) * GameRule.getInstance().getZoneBuff(zoneInfo.race, cardInfo.race);
    	
    	CardInfo cardInfo2 = zoneInfo.getCardInfo();
    	CharInfo charInfo2 = CharTable.getInstance().getChar(chrDef.charType);
    	float totalHp = (charInfo2.hp + cardInfo2.hp) * GameRule.getInstance().getZoneBuff(zoneInfo.race, cardInfo2.race);
    	
    	//check option
    	boolean attackOptionWin = false;
    	boolean defenseOptionWin = false;
    	CardOption option = CardTable.getInstance().getCardOption(pkt.atId);
    	if(option != null)
    	{
    		attackOptionWin = option.getAttackMatch(cardInfo2, zoneInfo);
    	}
    	
    	CardOption option2 = CardTable.getInstance().getCardOption(pkt.dfId);
    	if(option2 != null)
    	{
    		defenseOptionWin = option2.getDefenseMatch(cardInfo, zoneInfo);
    	}
    	
    	boolean attackWin = false;
    	boolean activatedOption = true;
    	if((attackOptionWin == true && defenseOptionWin == true) || (attackOptionWin == false && defenseOptionWin == false))
    	{
    		attackWin = totalHp - totalSt > 0 ? false : true;
    		activatedOption = false;
    	}
    	else if(attackOptionWin == true)
    		attackWin = true;
    	else if(defenseOptionWin == true)
    		attackWin = false;
    	
    	mLastBattle = new BattleInfo();
    	mLastBattle.zoneId = pkt.zId;
    	mLastBattle.charId = pkt.sender; 
    	mLastBattle.attackCard = pkt.atId;
    	mLastBattle.defenseCard = pkt.dfId;
    	mLastBattle.totalHp = totalHp;
    	mLastBattle.totalSt = totalSt;
    	mLastBattle.attackWin = attackWin;
    	
    	notifyAll(new ServerPacketBattle(pkt.sender,pkt.idx,chrDef.charId,cardInfo.cardId,cardInfo2.cardId,zoneInfo.id,totalHp,totalSt,attackWin,activatedOption).toJson());   	
    }    

    public void onBattleEnd(JsonNode node)
    {
    	ClientPacketBattleEnd pkt = Json.fromJson(node, ClientPacketBattleEnd.class);
    	
    	if(mLastBattle == null || mLastBattle.charId != pkt.sender)
    	{
    		Logger.debug("last battle is not found.");
    		return;
    	}
    	
    	SrvCharacter attChr = mCharacters.get(mLastBattle.charId);
    	if(attChr == null)
    		return;
    	
    	ZoneInfo zoneInfo = mZones.get(mLastBattle.zoneId);
    	if(zoneInfo == null)
    		return;
    	
    	int defenseChrId = zoneInfo.getChar();
    	
		SrvCharacter prevChr = mCharacters.get(zoneInfo.getChar());
		if(prevChr == null)
			return;
		
    	if(mLastBattle.attackWin)
    	{
    		//occupy
    		
    		//change zone owner
        	prevChr.removeZoneAsset(zoneInfo.id);
        	notifyAll( new ServerPacketCharRemoveZone(prevChr.charId,zoneInfo.id,false,true).toJson());
        	notifyAll( new ServerPacketCharZoneAsset(prevChr.charId,prevChr.getZoneCount(),prevChr.getZoneAssets()).toJson());
        	
        	zoneInfo.setChar(mLastBattle.charId);
        	CardInfo cardInfo = CardTable.getInstance().getCard(mLastBattle.attackCard);
        	zoneInfo.setCardInfo(cardInfo);
        	
        	attChr.addZoneAsset(zoneInfo.id, zoneInfo.tollSoul());        	
        	notifyAll( new ServerPacketCharAddZone(attChr.charId,mLastBattle.zoneId,mLastBattle.attackCard,attChr.charId,false,-1).toJson());
        	notifyAll( new ServerPacketCharZoneAsset(attChr.charId,attChr.getZoneCount(),attChr.getZoneAssets()).toJson());
        	
        	sendRanking();
    		
    		notifyAll( new ServerPacketCharBattleWin(mLastBattle.charId,defenseChrId,zoneInfo.id,mLastBattle.attackCard).toJson());
    		
    		mLastBattle = null;
    	}
    	else
    	{
    		if(attChr.hasEquipSpell(Spell.SPELL_SAFEGUARD))
    		{
    			//check for equip spell
    			User user = getUser(attChr.userId);
    			if(user != null)
    			user.SendPacket(new ServerPacketEquipSpellUse(pkt.sender,Spell.SPELL_SAFEGUARD).toJson());  
    		}
    		else
    		{
    			sendBattleLose(attChr,prevChr,zoneInfo,false,0);
    			
    			mLastBattle = null;
    		}
    	}
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
    	int dice = 1;//random.nextInt(3) + 1;
    	CardInfo info = CardTable.getInstance().getCard(pkt.card);
    	int total = 0;
    	
    	if(info != null)
    	{
    		total = (int)info.st * dice;
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
    
    //equip spell for battle toll 
    public void onEquipSpellUseReply(JsonNode node)
    {
    	ClientPacketEquipSpellUseReply pkt = Json.fromJson(node, ClientPacketEquipSpellUseReply.class);
    	
    	if(mLastBattle == null || mLastBattle.charId != pkt.sender)
    	{
    		Logger.debug("last battle is not found.");
    		return;
    	}
    	
    	SrvCharacter attChr = mCharacters.get(mLastBattle.charId);
    	if(attChr == null)
    		return;
    	
    	ZoneInfo zoneInfo = mZones.get(mLastBattle.zoneId);
    	if(zoneInfo == null)
    		return;
    	
		SrvCharacter prevChr = mCharacters.get(zoneInfo.getChar());
		if(prevChr == null)
			return;    	
    	
    	if(pkt.use == true)
    	{
    		SrvCharacter chr = mCharacters.get(pkt.sender);
			if(chr == null)
				return;
			
			Spell spell = SpellTable.getInstance().getSpell(pkt.spellId);
			sendBattleLose(attChr,prevChr,zoneInfo,true,spell.value1);
			
			chr.removeEquipSpellId(pkt.spellId);
			notifyAll(new ServerPacketEquipSpellRemove(chr.charId,pkt.spellId).toJson());
    	}
    	else
    	{
    		sendBattleLose(attChr,prevChr,zoneInfo,false,0); 		
    	}
    	
    	mLastBattle = null;
   	   	
    }
    
    private void onStartEnhance(JsonNode node)
    {
    	ClientPacketStartEnhance pkt = Json.fromJson(node, ClientPacketStartEnhance.class);
    	
    	for(ZoneInfo zoneInfo : mZones)
    	{
    		if(zoneInfo.getChar() == pkt.sender)
    		{
    			zoneInfo.addStartEnhance();
    		}
    	}
    	
    	notifyAll(new ServerPacketStartEnhance(pkt.sender).toJson());
    }    
        
    
    public void onGameReady(JsonNode node)
    {
    	ClientPacketGameReady pkt = Json.fromJson(node, ClientPacketGameReady.class);
    	
    	if(isPlaying() == false )
		{
			setPlaying(true);
			initGame(pkt.useAI);
		}
    }
    
    public void onGameInitDecks(JsonNode node)
    {
    	ClientPacketGameInitDecks pkt = Json.fromJson(node, ClientPacketGameInitDecks.class);
    	
    	SrvCharacter chr = mCharacters.get(pkt.sender);
    	if( chr == null )
    		return;
    	
    	final Random random = new Random();
    	int deckType = random.nextInt(6);
    	
    	chr.mAllCards = CardTable.getInstance().getSystemDeck(deckType);
    	chr.mRemainCards = new ArrayList<Integer>(chr.mAllCards);
    	
    	for(int i = 0; i < GameRule.INITIAL_CARDDECK_SIZE; i++)
    	{
    		int idx = random.nextInt(chr.mRemainCards.size());
    		chr.mPlayCards.add(chr.mRemainCards.get(idx));
    		chr.mRemainCards.remove(idx);
    	}
    	
    	notifyAll(new ServerPacketGameInitDecks(chr.charId, deckType, chr.mRemainCards, chr.mPlayCards).toJson());
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
    	public int attackCard;
    	public int defenseCard;
    	public float totalSt;
    	public float totalHp;
    	public boolean attackWin;
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
    
    class AssetRank implements Comparable<AssetRank>{
    	public int charId;
    	public float asset;
    	
    	public AssetRank(int charId, float asset)
    	{
    		this.charId = charId;
    		this.asset = asset;
    	}
		
    	@Override
		public int compareTo(AssetRank o) {
			if( this.asset == o.asset ) return 0;
			else if( this.asset > o.asset ) return -1;
			else return 1;
		}  	
    	
    }
    
}
