package fun.fengwk.paxos4j;

import fun.fengwk.paxos4j.proposer.PrepareCallback;

/**
 * @author fengwk
 */
public class MockResponse {

    private final Response resp;
    private final Object callback;
    private final Object ctx;

    public MockResponse(Response resp, Object callback, Object ctx) {
        this.resp = resp;
        this.callback = callback;
        this.ctx = ctx;
    }

    public Response getResp() {
        return resp;
    }

    public Object getCallback() {
        return callback;
    }

    public Object getCtx() {
        return ctx;
    }

}
