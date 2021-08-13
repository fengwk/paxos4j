package fun.fengwk.paxos4j.acceptor;

/**
 * @author fengwk
 */
public class Epoch {

    /**
     * 当前接收者在epoch轮次许可的最大提案号
     */
    private long maxNum;

    /**
     * 当前接收者在epoch轮次批准的提案来自提案者的id
     */
    private int acceptedFromId;

    /**
     * 当前接收者在epoch轮次批准的提案编号
     */
    private long acceptedNum;

    /**
     * 当前接收者在epoch轮次批准的提案内容
     */
    private Object acceptedValue;

    public long getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(long maxNum) {
        this.maxNum = maxNum;
    }

    public int getAcceptedFromId() {
        return acceptedFromId;
    }

    public void setAcceptedFromId(int acceptedFromId) {
        this.acceptedFromId = acceptedFromId;
    }

    public long getAcceptedNum() {
        return acceptedNum;
    }

    public void setAcceptedNum(long acceptedNum) {
        this.acceptedNum = acceptedNum;
    }

    public Object getAcceptedValue() {
        return acceptedValue;
    }

    public void setAcceptedValue(Object acceptedValue) {
        this.acceptedValue = acceptedValue;
    }

}
