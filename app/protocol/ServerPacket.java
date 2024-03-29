package protocol;

import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;

public class ServerPacket
{
	public static final int MSP_GAME_JOIN = 1100;
	public static final int MSP_CHAR_ADD = 1101;
	public static final int MSP_CHAR_ADD_SOUL = 1102;
	public static final int MSP_CHAR_DIRECTION = 1103;
	public static final int MSP_GAME_START = 1104;	//if all char set direction
	public static final int MSP_CHAR_MOVE = 1105;
	public static final int MSP_CHAR_PASS = 1106;
	public static final int MSP_CHAR_MOVED = 1107;
	public static final int MSP_CHAR_ENHANCE = 1108;
	public static final int MSP_CHAR_PASSBY_START = 1109;
	public static final int MSP_CHAR_TURN_OVER = 1110;
	public static final int MSP_CHAR_ADD_BUFF = 1111;
	public static final int MSP_CHAR_DEL_BUFF = 1112;
	public static final int MSP_CHAR_MOVE_BYSPELL = 1113;
	//public static final int MSP_ROUND_ADDCARD = 1114;		
	public static final int MSP_ROUND_OVER = 1114;
	public static final int MSP_CHAR_ZONE_ASSET = 1115;
	public static final int MSP_CHAR_RANK_ASSET = 1116;
	public static final int MSP_CHAR_PAY = 1117;
	public static final int MSP_CHAR_CHANGE_OWNER = 1118;
	public static final int MSP_CHAR_OCCUPY = 1119;
	public static final int MSP_CHAR_OCCUPY_AMBUSH = 1120;
	public static final int MSP_ZONE_AMBUSH = 1121;
	public static final int MSP_ROLL_DICE = 1122;
	public static final int MSP_CARD_CHANGE = 1123;
	public static final int MSP_CHAR_REMOVE_ZONE = 1124;
	public static final int MSP_CHAR_ADD_ZONE = 1125;
	//related char turn over 
	public static final int MSP_CHAR_TURN_START = 1126;
	public static final int MSP_ROLL_DICE_REQ = 1127;
	public static final int MSP_CHAR_TURN_SKIP = 1128;
	public static final int MSP_CHAR_BANKRUPT_REQ = 1129;
	
	public static final int MSP_CHAR_MOVE_ZONE = 1130;
	public static final int MSP_CHAR_ADD_CARD = 1131;
	public static final int MSP_CHAR_REMOVE_CARD = 1132;
	public static final int MSP_CHAR_SELL_ZONE = 1133;
	
	public static final int MSP_SPELL_OPEN = 1200;
	public static final int MSP_SPELL_REQ_USE = 1201;
	public static final int MSP_SPELLUSE = 1202;
	public static final int MSP_SPELLDEFENSE = 1203;
	public static final int MSP_SPELL_EQUIP = 1204;
	public static final int MSP_SPELLDEFENSE_REPLY = 1205;
	public static final int MSP_BUFF_USE = 1206;
	public static final int MSP_PORTAL_USE = 1207;
	public static final int MSP_ZONE_ADD_BUFF = 1208;
	public static final int MSP_ZONE_DEL_BUFF = 1209;

	//related char turn over
	public static final int MSP_BUFF_USE_REQ = 1210;
	
	public static final int MSP_ZONE_CHANGE_OWNER = 1211;
	public static final int MSP_NOTIFY_TRIBUTE = 1212;
	public static final int MSP_TRIBE_UPRISING = 1213;
	public static final int MSP_SYSTEM_CHAR_ADD	= 1214;
	
	//spell use result
	public static final int MSP_SPELL_BEAUTY = 1215;
	public static final int MSP_SPELL_CHANGEZONE = 1216;
	public static final int MSP_SPELL_DONATION = 1217;
	public static final int MSP_SPELL_THUNDER = 1218;
	
	public static final int MSP_START_REWARD = 1300;
	public static final int MSP_EVENT_GAMBLE = 1301;
	public static final int MSP_CHAR_BATTLE = 1302;
	public static final int MSP_CARDDECK_USE = 1303;
	public static final int MSP_EVENT_ARENA_REQ = 1304;
	public static final int MSP_EVENT_ARENA_USE = 1305;
	public static final int MSP_EVENT_ARENA_END = 1306;
	public static final int MSP_EVENT_ARENA_REWARD = 1307;
	public static final int MSP_EQUIP_SPELL_USE = 1308;
	public static final int MSP_SERVER_DISCONNECT = 1309; // not used in server
	public static final int MSP_GAME_OVER = 1310;
	public static final int MSP_EQUIP_SPELL_REMOVE = 1311;
	public static final int MSP_START_ENHANCE = 1312;
	public static final int MSP_CHAR_BATTLE_NOTIFY = 1313;
	public static final int MSP_CHAR_BATTLE_WIN = 1314;
	public static final int MSP_CHAR_BATTLE_LOSE = 1315;
	
	//for server only
	public static final int MSP_INIT_ZONE = 1400;
	public static final int MSP_CHAR_REMOVE = 1401;
	public static final int MSP_GAME_READY = 1402;
	public static final int MSP_USER_LIST = 1403;
	public static final int MSP_GAME_INITDECKS = 1404;
	
	public static final int MSP_SIMULATOR_ON = 1501;
	
	public int proto;
	public int sender;
	
	public JsonNode toJson()
	{
		return Json.toJson(this);
	}
}