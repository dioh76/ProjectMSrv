package protocol;

public class LeaveMsg extends SrvMsg {

	public long roomId = 0;
	
	public LeaveMsg(long roomId)
	{
		this.proto = "leaveroom";
		this.roomId = roomId;
	}

}