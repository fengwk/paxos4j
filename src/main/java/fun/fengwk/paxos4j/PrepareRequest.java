package fun.fengwk.paxos4j;

/**
 * 准备请求
 *
 * <p>
 * 该请求用以获取请求内携带提案编号的许可，针对该请求接收者可能做出以下三种响应：
 * <ul>
 * <li>允许使用当前提案编号并且自定义提案内容</li>
 * <li>允许使用当前提案编号并返回允许提案</li>
 * <li>不允许使用当前提案编号并返回当前许可的最大提案编号及允许的提案</li>
 * </ul>
 * </p>
 *
 * @author fengwk
 */
public class PrepareRequest {

    /**
     * 提案轮次
     */
    private final long epoch;

    /**
     * epoch轮次的提案编号
     */
    private final long num;

    public PrepareRequest(long epoch, long num) {
        this.epoch = epoch;
        this.num = num;
    }

    public long getEpoch() {
        return epoch;
    }

    public long getNum() {
        return num;
    }

    @Override
    public String toString() {
        return String.format("PrepareRequest(%d, %d)", epoch, num);
    }

}
