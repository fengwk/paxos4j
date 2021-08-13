package fun.fengwk.paxos4j;

import fun.fengwk.paxos4j.acceptor.Acceptor;

/**
 * {@link Acceptor}返回的响应。
 *
 * @author fengwk
 */
public class Response {

    /**
     * 本次请求的响应结果
     */
    private final boolean ok;

    /**
     * 当前接收者在epoch轮次许可的最大提案号
     */
    private final Long maxNum;

    /**
     * 当前接收者在epoch轮次批准的提案来自提案者的id
     */
    private final Integer acceptedFromId;

    /**
     * 当前接收者在epoch轮次批准的提案编号
     */
    private final Long acceptedNum;

    /**
     * 当前接收者在epoch轮次批准的提案内容
     */
    private final Object acceptedValue;

    public Response(boolean ok, Long maxNum, Integer acceptedFromId, Long acceptedNum, Object acceptedValue) {
        this.ok = ok;
        this.maxNum = maxNum;
        this.acceptedFromId = acceptedFromId;
        this.acceptedNum = acceptedNum;
        this.acceptedValue = acceptedValue;
    }

    public boolean isOk() {
        return ok;
    }

    public Long getMaxNum() {
        return maxNum;
    }

    public Integer getAcceptedFromId() {
        return acceptedFromId;
    }

    public Long getAcceptedNum() {
        return acceptedNum;
    }

    public Object getAcceptedValue() {
        return acceptedValue;
    }

    @Override
    public String toString() {
        return String.format("Response(%b, %d, %d, %d, %s)", ok, maxNum, acceptedFromId, acceptedNum, acceptedValue);
    }

}
