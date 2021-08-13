package fun.fengwk.paxos4j;

import fun.fengwk.paxos4j.proposer.AcceptCallback;
import fun.fengwk.paxos4j.proposer.AcceptorClient;
import fun.fengwk.paxos4j.proposer.PrepareCallback;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * @author fengwk
 */
public class MockAcceptorClient implements AcceptorClient {

    private final BlockingQueue<MockRequest> requestQueue;
    private final BlockingQueue<MockResponse> responseQueue;
    private CountDownLatch cdl;
    private volatile Thread runner;
    private final Random random = new Random();

    public MockAcceptorClient(BlockingQueue<MockRequest> requestQueue, BlockingQueue<MockResponse> responseQueue) {
        this.requestQueue = requestQueue;
        this.responseQueue = responseQueue;
    }

    @Override
    public void start() throws IOException {
        cdl = new CountDownLatch(1);
        runner = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    MockResponse resp = responseQueue.take();
                    if (resp.getCallback() instanceof PrepareCallback) {
                        if (random.nextInt(100) >= 20) {
                            ((PrepareCallback) resp.getCallback()).handlePrepareResponse(resp.getResp(), resp.getCtx());
                        } else {
                            ((PrepareCallback) resp.getCallback()).handlePrepareException(new IOException("Network error"), resp.getCtx());
                        }
                    } else {
                        if (random.nextInt(100) >= 20) {
                            ((AcceptCallback) resp.getCallback()).handleAcceptResponse(resp.getResp(), resp.getCtx());
                        } else {
                            ((AcceptCallback) resp.getCallback()).handleAcceptException(new IOException("Network error"), resp.getCtx());
                        }
                    }
                } catch (InterruptedException ignore) {
                    Thread.currentThread().interrupt();
                }
            }

            cdl.countDown();
        });
        runner.start();
    }

    @Override
    public void stop() throws IOException {
        runner.interrupt();
        for (;;) {
            try {
                cdl.await();
                break;
            } catch (InterruptedException ignore) {}
        }
    }

    @Override
    public void prepare(PrepareRequest req, PrepareCallback callback, Object ctx) {
        requestQueue.offer(new MockRequest(req, callback, ctx));
    }

    @Override
    public void accept(AcceptRequest req, AcceptCallback callback, Object ctx) {
        requestQueue.offer(new MockRequest(req, callback, ctx));
    }

}
