package fun.fengwk.paxos4j.proposer;

import java.util.Random;

/**
 * 随机等待器
 *
 * @author fengwk
 */
public class Waiter {

    /**
     * 随机数生成器
     */
    private final Random random;

    /**
     * 最小等待时间，单位毫秒
     */
    private final long minWaitTime;

    /**
     * 最大等待时间，单位毫秒
     */
    private final long maxWaitTime;

    public Waiter(int seed, long minWaitTime, long maxWaitTime) {
        this.random = new Random(seed);
        this.minWaitTime = minWaitTime;
        this.maxWaitTime = maxWaitTime;
    }

    /**
     * 等待一个随机的超时时间
     *
     * @throws InterruptedException
     */
    public void await() throws InterruptedException {
        long waitTime = random.nextInt((int) (maxWaitTime - minWaitTime + 1)) + minWaitTime;
        Thread.sleep(waitTime);
    }

}
