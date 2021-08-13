package fun.fengwk.paxos4j.acceptor;

import fun.fengwk.paxos4j.AcceptRequest;
import fun.fengwk.paxos4j.PrepareRequest;
import fun.fengwk.paxos4j.Response;

import java.io.IOException;
import java.util.Map;

/**
 * 接受者
 *
 * @author fengwk
 */
public class Acceptor implements RequestHandler {

    private AcceptorServer server;

    /**
     * 轮次列表
     */
    private Map<Long, Epoch> epochs;

    public Acceptor(AcceptorServer server, Map<Long, Epoch> epochs) {
        this.server = server;
        this.epochs = epochs;
    }

    public void start() throws IOException {
        server.start(this);
    }

    public void stop() throws IOException {
        server.stop();
    }

    private Epoch getEpoch(long i) {
        return epochs.computeIfAbsent(i, k -> new Epoch());
    }

    @Override
    public synchronized Response handlePrepareRequest(PrepareRequest req) {
        Epoch epoch = getEpoch(req.getEpoch());

        if (req.getNum() > epoch.getMaxNum()) {
            epoch.setMaxNum(req.getNum());
            return new Response(true, null, epoch.getAcceptedFromId(), epoch.getAcceptedNum(), epoch.getAcceptedValue());
        } else {
            return new Response(false, epoch.getMaxNum(), null, null, null);
        }
    }

    @Override
    public synchronized Response handleAcceptRequest(AcceptRequest req) {
        Epoch epoch = getEpoch(req.getEpoch());

        if (req.getNum() == epoch.getMaxNum()) {
            epoch.setAcceptedFromId(req.getFromId());
            epoch.setAcceptedNum(req.getNum());
            epoch.setAcceptedValue(req.getValue());
            return new Response(true, null, null, null, null);
        } else {
            return new Response(false, epoch.getMaxNum(), null, null, null);
        }
    }

}
