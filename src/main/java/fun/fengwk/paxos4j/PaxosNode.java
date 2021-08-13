package fun.fengwk.paxos4j;

import fun.fengwk.paxos4j.acceptor.Acceptor;
import fun.fengwk.paxos4j.proposer.Proposer;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author fengwk
 */
public class PaxosNode {

    /**
     * 提案者
     */
    private final Proposer proposer;

    /**
     * 接收者
     */
    private final Acceptor acceptor;

    public PaxosNode(Proposer proposer, Acceptor acceptor) {
        this.proposer = proposer;
        this.acceptor = acceptor;
    }

    public void start() throws IOException {
        proposer.start();
        acceptor.start();

    }

    public void stop() throws IOException {
        proposer.stop();
        acceptor.stop();
    }

    public boolean tryPropose(Object value) throws InterruptedException {
        return proposer.tryPropose(value);
    }

    public void propose(Object value) throws InterruptedException {
        proposer.propose(value);
    }

    public Map<Long, Object> getProposals() {
        Map<Long, Object> proposals = new LinkedHashMap<>();
        for (long i = 0; i < proposer.proposalEpoch(); i++) {
            proposals.put(i, proposer.getProposal(i));
        }
        return proposals;
    }

}
