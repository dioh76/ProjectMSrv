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
import game.Character;
import game.spell.Spell;

public class GameRoom {
	
	private long mRoomId;
	private int mMaxUser;
	private boolean mPlaying = false;
	
	private List<User> mUsers = new ArrayList<User>();
	private SortedMap<Integer, Character> mCharacters = new TreeMap<Integer, Character>();
	private List<Integer> mCharIds = null;
	private List<ZoneInfo> mZones = new ArrayList<ZoneInfo>();
	
	//common
	private long mCreatedTime = 0;
	
	private int mStartCharId = 0;
	private int mCurrentTurn = 0;
	private int mCurrentRound = 0;
	
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
			
			user.sendPacket(new ServerPacketGameJoin(0,user.getUserId(),user.getName(),mMaxUser,mUsers.size()==1 ? true:false).toJson());
			
			ArrayList<Long> userIds = new ArrayList<Long>();
			ArrayList<String> userNames = new ArrayList<String>();
			for(User u : mUsers)
			{
				userIds.add(u.getUserId());
				userNames.add(u.getName());
			}
			
			notifyAll(new ServerPacketUserList(0,userIds,userNames).toJson());
			
	    	//initialize map for this user when user joins
	    	user.sendPacket(new ServerPacketInitZone(0, maporders).toJson());
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
		
		float initSoul = GameRule.getInstance().CHAR_INIT_MONEY;
		Character chr = new Character(user, user.getUserId(), getNewCharId(), charType, user.getName(), true, initSoul, false );
    	
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
			float initSoul = GameRule.getInstance().CHAR_INIT_MONEY;
			Character chr = new Character(randomUser,randomUser.getUserId(), getNewCharId(), charType, "AIPlayer"+(i+1), false, initSoul, false );
	    	
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
    	for(Character chr : mCharacters.values())
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
    
    public List<Character> getAllCharacters()
    {
    	synchronized(mCharacters)
    	{
    		return new ArrayList<Character>(mCharacters.values());
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
    			if(zoneInfo.type == ZoneInfo.ZONE_MAINTYPE_NORMAL && zoneInfo.getCardInfo() != null && zoneInfo.tollMoney() > soul)
    			{
    				soul = zoneInfo.tollMoney();
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
		
		for( Character chr : mCharacters.values())
		{
			notifyAll(new ServerPacketCharAdd(chr.charId, chr.userId, chr.charId, chr.charType, chr.userName, chr.userChar,chr.money).toJson());
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
    		for(Character srvChr : mCharacters.values())
    		{
    			ranks.add(new AssetRank(srvChr.charId,srvChr.getZoneAssets() + srvChr.money));
    		}
    	}
    	
    	Collections.sort(ranks);
    	
    	List<Integer> sendRanks = new ArrayList<Integer>();
    	for(AssetRank rank : ranks)
    		sendRanks.add(rank.charId);
    	
    	
    	notifyAll( new ServerPacketCharRankAsset(0,sendRanks).toJson());    
    }
    
    public void sendMoneyChanged(Character chr, boolean notify)
    {
    	boolean bankrupt = chr.money < 0 ? true : false;
    	notifyAll(new ServerPacketCharAddSoul(chr.charId, chr.money, bankrupt,notify).toJson());
    }
    
    private void sendTurnStart(Character chr, boolean doubledice)
    {
    	if(chr == null)
    		return;
    	
    	chr.myturn = true;
    	
    	//start turn state to all
    	notifyAll(new ServerPacketCharTurnStart(chr.charId,doubledice).toJson());
    	
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
		chr.sendPacket(new ServerPacketRollDiceReq(chr.charId).toJson());
    }
    
    private void sendTurnOver(Character chr)
    {
    	if(chr == null)
    		return;
    	
    	chr.myturn = false;
    	
    	//send only charId
    	notifyAll(new ServerPacketCharTurnOver(chr.charId,chr.charId,false,false,0).toJson());
    }
    
    private void sendRoundOver(Character chr, boolean chrBankrupt, int nextChrId)
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
			sendTurnOver(chr);
		}		
		
		for( Character srvChr : mCharacters.values() )
    	{
			if(chrBankrupt && srvChr.charId == chr.charId)
				continue;
    		srvChr.addCard();
    	}    		
   	
    	notifyAll(new ServerPacketRoundOver(chr.charId,nextChrId).toJson());
    }
    
    private void sendBattleLose(Character attChr, Character defChr, ZoneInfo zoneInfo, boolean useSpell, int value)
    {
    	float sumPay = 0;
    	if(value != 100 || useSpell == false)
    	{
	    	//pay for lose
			sumPay = zoneInfo.tollMoney() * (100 - value) / 100;
			if(sumPay > attChr.money)
			{
				float sellSum = attChr.getZoneSellSum();
				if(sumPay > attChr.money + sellSum)
				{
					//bankrupt and hand over all zone
					List<Integer> attZones = attChr.getOwnZones();
					for(int zoneId : attZones)
					{
						ZoneInfo sellZoneInfo = getZone(zoneId);
						float asset = sellZoneInfo.tollMoney();
				    	defChr.addZoneAsset(sellZoneInfo.id, asset, sellZoneInfo.sellMoney());
				    	sellZoneInfo.setChar(defChr.charId);
				    	attChr.removeZoneAsset(sellZoneInfo.id);
				    	notifyAll(new ServerPacketCharChangeOwner(defChr.charId,sellZoneInfo.id,defChr.charId,defChr.getZoneCount(),defChr.getZoneAssets(),attChr.charId,attChr.getZoneCount(),attChr.getZoneAssets()).toJson());
					}
					
					//process bankrupt
					charBankrupt(attChr);
					
			    	sendRanking();						
				}
				else
				{
					attChr.sendPacket(new ServerPacketCharSellZone(attChr.charId,defChr.charId,sumPay).toJson());
				}
				
				return;
				
			}else
			{
				defChr.money += sumPay;
		    	sendMoneyChanged(defChr,true);
		    	attChr.money -= sumPay;
		    	sendMoneyChanged(attChr, true);
			}
    	}
    	
		notifyAll(new ServerPacketCharBattleLose(attChr.charId,zoneInfo.getChar(),zoneInfo.id,useSpell,value,(int)sumPay).toJson());    	
    }
    
    public void processPacket( int protocol, JsonNode node )
    {
    	switch( protocol )
    	{
    	case ClientPacket.MCP_CHAR_ADD: onCharAdd(node); break;
    	case ClientPacket.MCP_CHAR_DIRECTION: onCharDirection(node); break;
    	case ClientPacket.MCP_CHAR_MOVE: onCharMove(node); break;
    	case ClientPacket.MCP_CHAR_PASS: onCharPass(node); break;
    	case ClientPacket.MCP_CHAR_MOVED: onCharMoved(node); break;
    	case ClientPacket.MCP_CHAR_ENHANCE: onCharEnhance(node); break;
    	case ClientPacket.MCP_CHAR_PASSBY_START: onCharPassByStart(node); break;
    	case ClientPacket.MCP_CHAR_TURN_OVER: onCharTurnOver(node); break;
    	case ClientPacket.MCP_CHAR_START_ROUND: onRoundStart(node); break;
    	case ClientPacket.MCP_CHAR_SELL_ZONE: onCharSellZone(node); break;
    	case ClientPacket.MCP_CHAR_ADD_BUFF: onCharAddBuff(node); break;
    	case ClientPacket.MCP_CHAR_PAY: onCharPay(node); break;
    	case ClientPacket.MCP_CHAR_OCCUPY: onCharOccupy(node); break;
    	case ClientPacket.MCP_CHAR_ROLL_DICE: onRollDice(node); break;
    	case ClientPacket.MCP_CARD_CHANGE: onCardChange(node); break;
    	case ClientPacket.MCP_CHAR_BANKRUPT: onCharBankrupt(node); break;
    	case ClientPacket.MCP_SPELL_OPEN: onSpellOpen(node); break;
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
    	case ClientPacket.MCP_EVENT_ARENA_REQ: onEventArenaReq(node); break;
    	case ClientPacket.MCP_EVENT_ARENA_USE: onEventArenaUse(node); break;
    	case ClientPacket.MCP_EVENT_ARENA_REWARD: onEventArenaReward(node); break;
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
    	
    	Character chr = new Character(user,pkt.userId, pkt.charId, 1, pkt.name, pkt.userChar, GameRule.getInstance().CHAR_INIT_MONEY, false );    	
    	mCharacters.put(pkt.charId, chr);
    	
    	notifyAll(new ServerPacketCharAdd(pkt.charId, pkt.userId, pkt.charId, chr.charType, pkt.name, pkt.userChar,chr.money).toJson());   	
    }
    
    private void onCharDirection(JsonNode node)
    {
    	ClientPacketCharDirection pkt = Json.fromJson(node, ClientPacketCharDirection.class);
    	
    	Character chr = mCharacters.get(pkt.sender);
    	
    	if( chr == null )
    		return;
    	
    	chr.checkdirection = true;
    	
    	boolean allready = true;
    	for( Character srvChr : mCharacters.values() )
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
    		
			notifyAll(new ServerPacketGameStart(pkt.sender, mStartCharId).toJson());
			
			Character startChr = mCharacters.get(mStartCharId);
			
			sendTurnStart(startChr, false);
    	}
    }        
    
    private void onRollDice(JsonNode node)
    {
    	ClientPacketRollDice pkt = Json.fromJson(node, ClientPacketRollDice.class);
    	
    	Character chr = mCharacters.get(pkt.sender);
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
    	
    	Character chr = mCharacters.get(pkt.sender);
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
    	chr.money -= cardInfo.cost;
    	
    	sendMoneyChanged(chr,false);
    	
    	chr.removeZoneAsset(pkt.zId);
		notifyAll( new ServerPacketCharRemoveZone(chr.charId,pkt.zId,false,true).toJson());
    	
		zoneInfo.setCardInfo(cardInfo);
    	chr.addZoneAsset(zoneInfo.id, zoneInfo.tollMoney(), zoneInfo.sellMoney());
    	notifyAll( new ServerPacketCharAddZone(chr.charId,zoneInfo.id,cardInfo.cardId,chr.charId,false,-1).toJson());    
		
    	notifyAll( new ServerPacketCharZoneAsset(chr.charId,chr.getZoneCount(),chr.getZoneAssets()).toJson());
    	
    	sendRanking();
    	
    	notifyAll(new ServerPacketCardChange(pkt.sender,pkt.zId,pkt.idx,pkt.cId,prevCard).toJson());
    }
    
    private void onCharBankrupt(JsonNode node)
    {
    	ClientPacketCharBankrupt pkt = Json.fromJson(node, ClientPacketCharBankrupt.class);
    	
    	Character chr = mCharacters.get(pkt.sender);
    	if( chr == null )
    		return;
    	
    	charBankrupt(chr);
    	
    }    
    
    private void charBankrupt(Character chr)
    {
    	boolean roundover = false;
    	int nextCharId = 0;
    	Character nextChr = null;
    	//If my turn, check next player to start
    	if(chr.myturn)
    	{
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
    		
    		nextCharId = mCharIds.get(nextIndex);
    		
    		nextChr = mCharacters.get(nextCharId);
    		if(nextChr == null)
    		{
    			Logger.debug("next turn char is null " + nextCharId);
    			return;
    		}
    		
    		if(mStartCharId == nextCharId)
    			roundover = true;
    		
    		if(mStartCharId == chr.charId)
    			mStartCharId = nextCharId;     
    		
    		if(roundover)
    		{
    			sendRoundOver(chr, true, mStartCharId);	
    		}
    		else if(chr.myturn)
    		{
    			sendTurnOver(chr);
    			sendTurnStart(nextChr, false);
    		}    		
    	}

		//Remove Char Info    	
    	for(int i = chr.mBuffs.size() - 1; i >=0; i--)
		{
    		Buff buff = chr.mBuffs.get(i);
			chr.mBuffs.remove(i);
				
			notifyAll(new ServerPacketCharDelBuff(chr.charId,buff.id,buff.targetchar).toJson()); 
		}
		
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
    	
    	notifyAll(new ServerPacketCharRemove(chr.charId,chr.userId).toJson());		
    	
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
    	
    	Character chr = mCharacters.get(pkt.sender);
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
    	
		chr.sendPacket(new ServerPacketCharMoved(pkt.sender,pkt.zId).toJson());
    }  
    
    private void onCharEnhance(JsonNode node)
    {
    	ClientPacketCharEnhance pkt = Json.fromJson(node, ClientPacketCharEnhance.class);
    	
    	Character chr = mCharacters.get(pkt.sender);
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
    	chr.money -= zoneInfo.buyMoney();

    	float asset = zoneInfo.tollMoney();
    	chr.addZoneAsset(zoneInfo.id, asset, zoneInfo.sellMoney());
    	
    	notifyAll(new ServerPacketCharEnhance(pkt.sender,pkt.zId,zoneInfo.getLevel(),chr.money,chr.getZoneCount(),chr.getZoneAssets(),true,true).toJson());
    	
    	sendRanking();
    }   
    
    private void onCharPassByStart(JsonNode node)
    {
    	ClientPacketCharPassByStart pkt = Json.fromJson(node, ClientPacketCharPassByStart.class);
    	
    	Character chr = mCharacters.get(pkt.sender);
    	
    	if( chr == null )
    		return;
    	
    	chr.money += GameRule.getInstance().START_BONUS_MONEY;
    	sendMoneyChanged(chr,false);
    	
    	notifyAll(new ServerPacketCharPassByStart(pkt.sender).toJson());
    }
    
    private void onCharTurnOver(JsonNode node)
    {
    	ClientPacketCharTurnOver pkt = Json.fromJson(node, ClientPacketCharTurnOver.class);
    	
    	Character chr = mCharacters.get(pkt.sender);
    	if(chr == null)
    		return;
    	
    	Logger.debug("[srv] turn over char = " + pkt.sender);
    	
    	charTurnOver(chr);
    }
    
    private void charTurnOver(Character chr)
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
    	
    	//check bankrupt
    	if(chr.money < 0)
		{
			chr.sendPacket(new ServerPacketCharBankruptReq(chr.charId).toJson());
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
		
		boolean roundover = false;
		if(mStartCharId == nextCharId)
			roundover = true;
		
		if(roundover)
		{
			sendRoundOver(chr, false, mStartCharId);	
		}
		else
		{
			Character nextChr = mCharacters.get(nextCharId);
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
    	
    	Character nextChr = mCharacters.get(mStartCharId);
		if(nextChr == null)
		{
			Logger.debug("[roundover]next turn char is null " + mStartCharId);
			return;
		}
		
		sendTurnStart(nextChr, false);
    }
    
    private void onCharSellZone(JsonNode node)
    {
    	ClientPacketCharSellZone pkt = Json.fromJson(node, ClientPacketCharSellZone.class);
    	
    	Character chr = mCharacters.get(pkt.sender);
    	
    	if(chr == null)
    		return;
    	
    	Character targetChr = mCharacters.get(pkt.toId);
    	
    	ZoneInfo zoneInfo = mZones.get(pkt.zId);
    	
    	if(zoneInfo == null)
    		return;
    	
    	if(targetChr != null)
    	{
    		chr.removeZoneAsset(zoneInfo.id);
    		targetChr.addZoneAsset(zoneInfo.id, zoneInfo.tollMoney(), zoneInfo.sellMoney());
    		zoneInfo.setChar(targetChr.charId);
    		notifyAll(new ServerPacketCharChangeOwner(targetChr.charId,zoneInfo.id,targetChr.charId,targetChr.getZoneCount(),targetChr.getZoneAssets(),chr.charId,chr.getZoneCount(),chr.getZoneAssets()).toJson());
    	}
    	else
    	{
    		chr.money += mZones.get(pkt.zId).sellMoney();
       		sendMoneyChanged(chr,false);
        	
        	chr.removeZoneAsset(pkt.zId);
        	zoneInfo.setChar(0);
        	zoneInfo.setCardInfo(null);
        	
        	//check remove buff or not
    		Buff prevBuff = zoneInfo.getBuff();
    		if(prevBuff != null)
    		{
    			zoneInfo.setBuff(null);
    			notifyAll(new ServerPacketZoneDelBuff(chr.charId,prevBuff.id,prevBuff.targetzone).toJson());
    		}    	
        	
        	notifyAll( new ServerPacketCharZoneAsset(pkt.sender,chr.getZoneCount(),chr.getZoneAssets()).toJson());
        	notifyAll( new ServerPacketCharRemoveZone(pkt.sender,pkt.zId,true,false).toJson());    	
    	}
    	
    	sendRanking();
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
    	
    	Character chr = mCharacters.get(buff.targetchar);
    	
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
    
    private void charAddZone(Character chr, int zoneId, int cardId, boolean buy, int index )
    {
    	ZoneInfo zoneInfo = mZones.get(zoneId);
    	if(zoneInfo == null)
    		return;

    	CardInfo cardInfo = CardTable.getInstance().getCard(cardId);
    	if(cardInfo == null)
    		return;
    	
    	zoneInfo.setChar(chr.charId);
    	zoneInfo.setCardInfo(cardInfo);
    	
    	chr.addZoneAsset(zoneId, zoneInfo.tollMoney(), zoneInfo.sellMoney());
    	
    	if(buy)
    	{
			chr.money -= cardInfo.cost;
			sendMoneyChanged(chr,false);
    	}
    	
    	notifyAll( new ServerPacketCharZoneAsset(chr.charId,chr.getZoneCount(),chr.getZoneAssets()).toJson());
    	
    	sendRanking(); 
    	
    	notifyAll( new ServerPacketCharAddZone(chr.charId,zoneId,cardId,chr.charId,buy,index).toJson());    	
    }
    
    private void onCharPay(JsonNode node)
    {
    	ClientPacketCharPay pkt = Json.fromJson(node, ClientPacketCharPay.class);
    	
    	Character chr = mCharacters.get(pkt.sender);
    	
    	if( chr == null )
    		return;
    	
    	ZoneInfo zoneInfo = mZones.get(pkt.zId);
    	if(zoneInfo == null)
    		return;
    	
    	int ownerId = zoneInfo.getChar();
    	Character owner = mCharacters.get(ownerId);
    	if(owner == null)
    		return;

    	float fToll = zoneInfo.tollMoney();
    	
    	chr.money -= fToll;
    	sendMoneyChanged(chr,false);
    	owner.money += fToll;
    	sendMoneyChanged(owner,false);
    	
    	notifyAll(new ServerPacketCharPay(pkt.sender,pkt.zId).toJson());
    	chr.removeZoneAsset(pkt.zId);
    	mZones.get(pkt.zId).setChar(0);
    	
    	sendRanking();   	
    }
    
    private void onCharOccupy(JsonNode node)
    {
    	ClientPacketCharOccupy pkt = Json.fromJson(node, ClientPacketCharOccupy.class);
    	
    	Character chr = mCharacters.get(pkt.sender);
    	if(chr == null)
    		return;
    	
    	ZoneInfo zoneInfo = mZones.get(pkt.zId);
    	if(zoneInfo == null)
    		return;
    	
    	CardInfo cardInfo = CardTable.getInstance().getCard(pkt.cId);
    	if(cardInfo == null)
    		return;

    	if(chr.money < cardInfo.cost)
    	{
    		notifyAll(new ServerPacketCharOccupy(pkt.sender,pkt.zId,pkt.cId,false).toJson());
    		return;
    	}
    	
    	chr.removeCard(pkt.cId);
    	
    	if(zoneInfo.getAmbush())
    	{
    		//Erase the ambush buff.
    		Character ambushChr = mCharacters.get(zoneInfo.getAmbushOwner());
    		if(ambushChr != null)
    		{    		
				Buff prevBuff = zoneInfo.getBuff();
		    	if(prevBuff != null)
		    	{
		    		ambushChr.sendPacket(new ServerPacketZoneDelBuff(zoneInfo.getAmbushOwner(),prevBuff.id,prevBuff.targetzone).toJson());
		    		zoneInfo.setBuff(null);
		    	}
		    	
		    	if(zoneInfo.getAmbushOwner() == pkt.sender)
	    		{
	    			zoneInfo.setAmbush(false, 0);  			
	    			Logger.info("occupy and buff free");
	    		}
	    		else
	    		{
	    			
	   				chr.money -= cardInfo.cost;
	   				sendMoneyChanged(chr,false);
	    	    	sendRanking(); 
	    	    	
	    			zoneInfo.setAmbush(false, 0);
	    			notifyAll(new ServerPacketCharOccupyAmbush(pkt.sender,pkt.zId,pkt.idx,pkt.cId).toJson());
	    			
	    			return;
	    		}		    	
    		}
    		else
    		{
    			zoneInfo.setAmbush(false, 0);
    			zoneInfo.setBuff(null);
    		}
    	}
    	
    	charAddZone(chr,zoneInfo.id,pkt.cId,true,pkt.idx);   	
    	
    	notifyAll(new ServerPacketCharOccupy(pkt.sender,pkt.zId,pkt.cId,true).toJson());
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
    
    private void onSpellUse(JsonNode node)
    {
    	ClientPacketSpellUse pkt = Json.fromJson(node, ClientPacketSpellUse.class);
    	
    	Character castChr = mCharacters.get(pkt.sender);
    	Character targetChr = null;
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
    		Character chr = mCharacters.get(pkt.targetchar);
    		if(chr == null)
    			return;
    		
    		if(chr.hasEquipSpell(Spell.SPELL_IMMUNE))
    		{
   				chr.sendPacket(new ServerPacketSpellDefense(chr.charId,chr.charId).toJson());
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
    	
    	Character chr = mCharacters.get(pkt.sender);
    	if(chr == null)
    		return;
    	
    	chr.mEquipSpells.add(pkt.spellid);
    	
    	notifyAll(new ServerPacketSpellEquip(pkt.sender, pkt.spellid).toJson());   	
    }
    
    private void onSpellDefenseReply(JsonNode node)
    {
    	ClientPacketSpellDefenseReply pkt = Json.fromJson(node, ClientPacketSpellDefenseReply.class);
    	
    	Character chr = mCharacters.get(pkt.sender);
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
    	    	
    	    	Character castChr = mCharacters.get(chr.lastspell.caster);
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
    	
    	Character chr = mCharacters.get(pkt.sender);
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
    
    public void onStartReward(JsonNode node)
    {
    	ClientPacketStartReward pkt = Json.fromJson(node, ClientPacketStartReward.class);
    	
    	if(pkt.use)
    	{
    	   	Character chr = mCharacters.get(pkt.sender);
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
        	chr.money -= zoneInfo.buyMoney();
        	float asset = zoneInfo.tollMoney();
        	chr.addZoneAsset(zoneInfo.id, asset, zoneInfo.sellMoney());
        	
        	notifyAll(new ServerPacketCharEnhance(pkt.sender,pkt.targetzone,zoneInfo.getLevel(),chr.money,chr.getZoneCount(),chr.getZoneAssets(),false,false).toJson());
        	
        	sendRanking();
    	}
    	
    	notifyAll(new ServerPacketStartReward(pkt.sender, pkt.use, pkt.targetzone).toJson());
    }
    
    public void onEventGamble(JsonNode node)
    {
    	ClientPacketEventGamble pkt = Json.fromJson(node, ClientPacketEventGamble.class);
    	
    	Character chr = mCharacters.get(pkt.sender);
    	if( chr == null )
    		return;
    	
    	chr.money -= 30;
    	sendMoneyChanged(chr,false);
    	
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
    	Character chr = mCharacters.get(pkt.sender);
    	if(chr == null)
    		return;
    	
    	if(pkt.idx == -1)
    	{
    		Logger.debug("attack card is not valid.");
    		return;
    	}
    	
    	ZoneInfo zoneInfo = mZones.get(pkt.zId);
    	
    	//defense character
    	Character chrDef = mCharacters.get(zoneInfo.getChar());
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
    	
    	Character attChr = mCharacters.get(mLastBattle.charId);
    	if(attChr == null)
    		return;
    	
    	ZoneInfo zoneInfo = mZones.get(mLastBattle.zoneId);
    	if(zoneInfo == null)
    		return;
    	
    	int defenseChrId = zoneInfo.getChar();
    	
		Character prevChr = mCharacters.get(zoneInfo.getChar());
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
        	
        	attChr.addZoneAsset(zoneInfo.id, zoneInfo.tollMoney(), zoneInfo.sellMoney());        	
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
   				attChr.sendPacket(new ServerPacketEquipSpellUse(pkt.sender,Spell.SPELL_SAFEGUARD).toJson());  
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
    	
    	for(int chrId : pkt.winners)
		{
			Character chr = mCharacters.get(chrId);
			if(chr != null)
			{
				Spell spell = SpellTable.getInstance().getSpell(GameRule.SPELL_ID_BATTLE_ARENA_WIN);
				float fSoul = spell.value1 * pkt.losers.size();
				chr.money += fSoul;
				sendMoneyChanged(chr, true);
			}
		}
		
    	for(int chrId : pkt.losers)
		{
    		Character chr = mCharacters.get(chrId);
			if(chr != null)
			{
				Spell spell = SpellTable.getInstance().getSpell(GameRule.SPELL_ID_BATTLE_ARENA_LOSE);
				float fSoul = spell.value1;
				if(chr.money < fSoul)
					chr.money = 0;
				else
					chr.money -= fSoul;
				
				sendMoneyChanged(chr, true);
			}			
		}
    	
    	sendRanking();
    	
    	notifyAll(new ServerPacketEventArenaReward(pkt.sender,pkt.startplayer,pkt.winners,pkt.losers).toJson());    	   	
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
    	
    	Character attChr = mCharacters.get(mLastBattle.charId);
    	if(attChr == null)
    		return;
    	
    	ZoneInfo zoneInfo = mZones.get(mLastBattle.zoneId);
    	if(zoneInfo == null)
    		return;
    	
		Character prevChr = mCharacters.get(zoneInfo.getChar());
		if(prevChr == null)
			return;    	
    	
    	if(pkt.use == true)
    	{
    		Character chr = mCharacters.get(pkt.sender);
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
    	
    	Character chr = mCharacters.get(pkt.sender);
    	if(chr == null)
    		return;
    	
    	boolean enhanced = false;
    	for(ZoneInfo zoneInfo : mZones)
    	{
    		if(zoneInfo.getChar() == pkt.sender)
    		{
    			enhanced = zoneInfo.addStartEnhance();
    			notifyAll(new ServerPacketStartEnhance(pkt.sender,zoneInfo.id).toJson());
    		}
    	}
    	
    	if(enhanced)
    	{
    		notifyAll( new ServerPacketCharZoneAsset(chr.charId,chr.getZoneCount(),chr.getZoneAssets()).toJson());
    		sendRanking();
    	}	
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
    	
    	Character chr = mCharacters.get(pkt.sender);
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
