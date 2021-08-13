package fun.fengwk.paxos4j.proposer;

import fun.fengwk.paxos4j.AcceptRequest;
import fun.fengwk.paxos4j.PrepareRequest;
import fun.fengwk.paxos4j.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 提案者
 *
 * @author fengwk
 */
public class Proposer implements PrepareCallback, AcceptCallback {

    private static final Logger LOG = LoggerFactory.getLogger(Proposer.class);

    /**
     * 当前提案id
     */
    private final int id;

    /**
     * acceptors客户端列表
     */
    private final List<AcceptorClient> clients;

    /**
     * 提案列表，列表下标为epoch，proposals.get(epoch)为该轮提案内容
     */
    private final Map<Long, Object> proposals;

    /**
     * 随机等待器
     */
    private final Waiter waiter;

    /**
     * prepare响应列表
     */
    private final LinkedList<Response> responseQueue = new LinkedList<>();

    /**
     * 提案轮次
     */
    private long epoch;

    /**
     * 提案编号
     */
    private long num;

    /**
     * 提案锁
     */
    private final ReentrantLock proposeLock = new ReentrantLock();

    public Proposer(int id, List<AcceptorClient> clients, Map<Long, Object> proposals, Waiter waiter) {
        this.id = id;
        this.clients = clients;
        this.proposals = proposals;
        this.waiter = waiter;
    }

    public void start() throws IOException {
        for (AcceptorClient c : clients) {
            c.start();
        }
    }

    public void stop() throws IOException {
        for (AcceptorClient c : clients) {
            c.stop();
        }
    }

    /**
     * 获取当前提案轮数
     *
     * @return
     */
    public long proposalEpoch() {
        return proposals.size();
    }

    /**
     * 获取i轮提案，i从0开始
     *
     * @param i
     * @return
     */
    public Object getProposal(long i) {
        return proposals.get(i);
    }

    /**
     * 提案直到成功或中断
     *
     * @param value
     * @throws InterruptedException
     */
    public void propose(Object value) throws InterruptedException {
        while (!tryPropose(value)) {}
    }

    /**
     * 尝试进行一轮提案，如果当前提案在本轮被选中返回true，否则返回false
     *
     * @param value
     * @return
     */
    public boolean tryPropose(Object value) throws InterruptedException {
        proposeLock.lockInterruptibly();
        try {
            return doTryPropose(value);
        } finally {
            proposeLock.unlock();
        }
    }

    private boolean doTryPropose(Object value) throws InterruptedException {
        Object inv = value;

        boolean result = true;
        List<AcceptorClient> clients = this.clients;

        while (true) {
            waiter.await();
            num++;

            // 发送prepare请求到所有接收者
            CountDownLatch prepareReqCdl = new CountDownLatch(clients.size());
            PrepareRequest prepareReq = new PrepareRequest(epoch, num);
            clients.forEach(c -> c.prepare(prepareReq, this, prepareReqCdl));
            prepareReqCdl.await();

            // 处理所有接收者返回的prepare响应
            int prepareOkCount = 0;
            long prepareMaxMaxNum = 0;
            long prepareMaxAcceptedNum = 0;
            Object prepareAcceptedValue = null;
            while (!responseQueue.isEmpty()) {
                Response resp = responseQueue.poll();
                if (resp.isOk()) {
                    prepareOkCount++;
                    if (resp.getAcceptedNum() > prepareMaxAcceptedNum && resp.getAcceptedFromId() != id) {
                        prepareMaxAcceptedNum = resp.getAcceptedNum();
                        prepareAcceptedValue = resp.getAcceptedValue();
                    }
                } else {
                    if (resp.getMaxNum() > prepareMaxMaxNum) {
                        prepareMaxMaxNum = resp.getMaxNum();
                    }
                }
            }

            // ok没有超过半数则进行当前epoch的下一次尝试
            if (prepareOkCount <= clients.size() / 2) {
                if (prepareMaxMaxNum > num) {
                    num = prepareMaxMaxNum;
                }
                continue;
            }

            if (prepareMaxAcceptedNum > 0) {
                value = prepareAcceptedValue;
                result = false;
            }

            // 发送accept请求到所有接收者
            CountDownLatch acceptReqCdl = new CountDownLatch(clients.size());
            AcceptRequest acceptReq = new AcceptRequest(epoch, id, num, value);
            clients.forEach(c -> c.accept(acceptReq, this, acceptReqCdl));
            acceptReqCdl.await();

            // 处理所有接收者返回的accept响应
            int acceptOkCount = 0;
            long acceptMaxMaxNum = 0;
            while (!responseQueue.isEmpty()) {
                Response resp = responseQueue.poll();
                if (resp.isOk()) {
                    acceptOkCount++;
                } else {
                    if (resp.getMaxNum() > acceptMaxMaxNum) {
                        acceptMaxMaxNum = resp.getMaxNum();
                    }
                }
            }

            if (acceptOkCount > clients.size() / 2) {
                proposals.put(epoch, value);
                epoch++;
                num = 0;
                LOG.info("({}, {}, {}, {}, {})", epoch, inv, value, result, Thread.currentThread().getName());
                break;
            }

            if (prepareMaxMaxNum > num) {
                num = prepareMaxMaxNum;
            }
        }

        return result;
    }

    @Override
    public void handleAcceptResponse(Response resp, Object ctx) {
        CountDownLatch cdl = (CountDownLatch) ctx;
        synchronized (responseQueue) {
            responseQueue.offer(resp);
        }
        cdl.countDown();
    }

    @Override
    public void handleAcceptException(IOException ex, Object ctx) {
        CountDownLatch cdl = (CountDownLatch) ctx;
        LOG.error("Accept response exception", ex);
        cdl.countDown();
    }

    @Override
    public void handlePrepareResponse(Response resp, Object ctx) {
        CountDownLatch cdl = (CountDownLatch) ctx;
        synchronized (responseQueue) {
            responseQueue.offer(resp);
        }
        cdl.countDown();
    }

    @Override
    public void handlePrepareException(IOException ex, Object ctx) {
        CountDownLatch cdl = (CountDownLatch) ctx;
        LOG.error("Prepare response exception", ex);
        cdl.countDown();
    }

}
