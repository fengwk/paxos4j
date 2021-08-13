package fun.fengwk.paxos4j;

/**
 * 提案请求
 *
 * <p>
 * 该请求向接收者提交提案，接收者可能做出以下三种响应：
 * <ul>
 * <li>批准提案</li>
 * <li>拒绝提案并返回当前许可的最大提案编号及允许的提案</li>
 * </ul>
 * </p>
 *
 * @author fengwk
 */
public class AcceptRequest {

    /**
     * 提案轮次
     */
    private final long epoch;

    /**
     * 当前请求来自提案者的id
     */
    private final int fromId;

    /**
     * 当前轮次的逻辑时钟
     */
    private final long num;

    /**
     * 当前轮次的提案内容
     */
    private final Object value;

    public AcceptRequest(long epoch, int fromId, long num, Object value) {
        this.epoch = epoch;
        this.fromId = fromId;
        this.num = num;
        this.value = value;
    }

    public long getEpoch() {
        return epoch;
    }

    public int getFromId() {
        return fromId;
    }

    public long getNum() {
        return num;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("AcceptRequest(%d, %d, %d, %s)", epoch, fromId, num, value);
    }

}
