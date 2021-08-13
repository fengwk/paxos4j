package fun.fengwk.paxos4j;

import fun.fengwk.paxos4j.acceptor.AcceptorServer;
import fun.fengwk.paxos4j.acceptor.RequestHandler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * @author fengwk
 */
public class MockAcceptorServer implements AcceptorServer {

    private final BlockingQueue<MockRequest> requestQueue;
    private final BlockingQueue<MockResponse> responseQueue;
    private CountDownLatch cdl;
    private volatile Thread runner;

    public MockAcceptorServer(BlockingQueue<MockRequest> requestQueue, BlockingQueue<MockResponse> responseQueue) {
        this.requestQueue = requestQueue;
        this.responseQueue = responseQueue;
    }

    @Override
    public void start(RequestHandler handler) {
        cdl = new CountDownLatch(1);
        runner = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    MockRequest req = requestQueue.take();
                    Response resp;
                    if (req.getReq() instanceof PrepareRequest) {
                        resp = handler.handlePrepareRequest((PrepareRequest) req.getReq());
                    } else {
                        resp = handler.handleAcceptRequest((AcceptRequest) req.getReq());
                    }
                    responseQueue.offer(new MockResponse(resp, req.getCallback(), req.getCtx()));
                } catch (InterruptedException ignore) {
                    Thread.currentThread().interrupt();
                }
            }

            cdl.countDown();
        });
        runner.start();
    }

    @Override
    public void stop() {
        runner.interrupt();
        for (;;) {
            try {
                cdl.await();
                break;
            } catch (InterruptedException ignore) {}
        }
    }

}
