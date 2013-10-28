package protocol;

public class JoinMsg extends SrvMsg {

	public long roomId = 0;
	
	public JoinMsg(long roomId)
	{
		this.proto = "joinroom";
		this.roomId = roomId;
	}

}
