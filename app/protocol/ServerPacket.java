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
	public static final int MSP_ROUND_OVER = 1113;
	public static final int MSP_SPELL_OPEN = 1200;
	public static final int MSP_SPELL_REQ_USE = 1201;
	public static final int MSP_SPELLUSE = 1202;
	public static final int MSP_SPELLDEFENSE = 1203;
	public static final int MSP_SPELL_EQUIP = 1204;
	public static final int MSP_SPELLDEFENSE_REPLY = 1205;
	public static final int MSP_BUFF_USE = 1206;
	public static final int MSP_PORTAL_USE = 1207;
	public static final int MSP_START_REWARD = 1300;
	public static final int MSP_EVENT_GAMBLE = 1301;
	public static final int MSP_PLAYER_BATTLE = 1302;
	public static final int MSP_PLAYER_BATTLE_END = 1303;
	public static final int MSP_CARDDECK_USE = 1304;
	public static final int MSP_EVENT_ARENA_REQ = 1305;
	public static final int MSP_EVENT_ARENA_USE = 1306;
	public static final int MSP_EVENT_ARENA_END = 1307;
	public static final int MSP_EVENT_ARENA_REWARD = 1308;
	public static final int MSP_EQUIP_SPELL_USE = 1309;
	public static final int MSP_EQUIP_SPELL_USE_REPLY = 1310;
	//for server only
	public static final int MSP_INIT_ZONE = 1400;
	public static final int MSP_CHAR_REMOVE = 1401;
	public static final int MSP_GAME_READY = 1402;
	
	public int proto;
	public int sender;
	
	public JsonNode toJson()
	{
		return Json.toJson(this);
	}
}