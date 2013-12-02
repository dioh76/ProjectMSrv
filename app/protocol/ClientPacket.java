package protocol;

public class ClientPacket
{
	public static final int MCP_GAME_JOIN = 100;
	public static final int MCP_CHAR_ADD = 101;
	public static final int MCP_CHAR_ADD_SOUL = 102;
	public static final int MCP_CHAR_DIRECTION = 103;
	public static final int MCP_CHAR_MOVE = 104;
	public static final int MCP_CHAR_PASS = 105;
	public static final int MCP_CHAR_MOVED = 106;
	public static final int MCP_CHAR_ENHANCE = 107;
	public static final int MCP_CHAR_PASSBY_START = 108;
	public static final int MCP_CHAR_TURN_OVER = 109;
	public static final int MCP_CHAR_ADD_BUFF = 110;
	public static final int MCP_CHAR_MOVE_BYSPELL = 111;
	public static final int MCP_CHAR_SET_ZONE = 112;
	public static final int MCP_CHAR_REMOVE_ZONE = 113;
	public static final int MCP_CHAR_PAY = 114;
	public static final int MCP_CHAR_ADDCARD = 115;
	public static final int MCP_CHAR_CHANGE_OWNER = 116;
	public static final int MCP_CHAR_OCCUPY = 117;
	public static final int MCP_ZONE_AMBUSH = 118;
	public static final int MCP_SPELL_OPEN = 200;
	public static final int MCP_SPELL_REQ_USE = 201;
	public static final int MCP_SPELLUSE = 202;
	public static final int MCP_SPELLDEFENSE = 203;
	public static final int MCP_SPELL_EQUIP = 204;
	public static final int MCP_SPELLDEFENSE_REPLY = 205;
	public static final int MCP_BUFF_USE = 206;
	public static final int MCP_PORTAL_USE = 207;
	public static final int MCP_START_REWARD = 300;
	public static final int MCP_EVENT_GAMBLE = 301;
	public static final int MCP_PLAYER_BATTLE = 302;
	public static final int MCP_PLAYER_BATTLE_END = 303;
	public static final int MCP_CARDDECK_USE = 304;
	public static final int MCP_EVENT_ARENA_REQ = 305;
	public static final int MCP_EVENT_ARENA_USE = 306;
	public static final int MCP_EVENT_ARENA_REWARD = 307;
	public static final int MCP_EQUIP_SPELL_USE = 308; // Specially, Defense Toll
	public static final int MCP_EQUIP_SPELL_USE_REPLY = 309;
	public static final int MCP_GAME_READY = 400;
	
	public int proto;
	public int sender;
}	