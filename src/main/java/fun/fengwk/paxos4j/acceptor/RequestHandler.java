package fun.fengwk.paxos4j.acceptor;

import fun.fengwk.paxos4j.AcceptRequest;
import fun.fengwk.paxos4j.PrepareRequest;
import fun.fengwk.paxos4j.Response;

/**
 * @author fengwk
 */
public interface RequestHandler {

    Response handlePrepareRequest(PrepareRequest req);

    Response handleAcceptRequest(AcceptRequest req);

}
