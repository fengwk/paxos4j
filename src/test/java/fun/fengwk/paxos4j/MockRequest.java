package fun.fengwk.paxos4j;

import fun.fengwk.paxos4j.proposer.PrepareCallback;

/**
 * @author fengwk
 */
public class MockRequest {

    private final Object req;
    private final Object callback;
    private final Object ctx;

    public MockRequest(Object req, Object callback, Object ctx) {
        this.req = req;
        this.callback = callback;
        this.ctx = ctx;
    }

    public Object getReq() {
        return req;
    }

    public Object getCallback() {
        return callback;
    }

    public Object getCtx() {
        return ctx;
    }

}
