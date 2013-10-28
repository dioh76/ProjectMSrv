package protocol;

public enum ClientPacket
{
	MCP_CONNECT,
	MCP_CREATE_GAME,
	MCP_JOIN_GAME,
	MCP_CHAR_ADD,
	MCP_CHAR_ADD_SOUL,
	MCP_CHAR_ROUND_OVER,
	MCP_CHAR_TURN_OVER,
	MCP_CHAR_ADD_BUFF,
	MCP_SPELL_OPEN,
	MCP_SPELL_REQ_USE,
	MCP_SPELLUSE,
	MCP_SPELLDEFENSE,
	MCP_SPELL_EQUIP,
	MCP_SPELLDEFENSE_REPLY,
	MCP_BUFF_USE,
	MCP_PORTAL_USE,
	MCP_START_REWARD,
	MCP_EVENT_GAMBLE,
	MCP_PLAYER_BATTLE,
	MCP_PLAYER_BATTLE_END,
	MCP_CARDDECK_USE,
	MCP_EVENT_ARENA_REQ,
	MCP_EVENT_ARENA_USE,
	MCP_EVENT_ARENA_REWARD,
	MCP_EQUIP_SPELL_USE, // Specially, Defense Toll
	MCP_EQUIP_SPELL_USE_REPLY,	
}