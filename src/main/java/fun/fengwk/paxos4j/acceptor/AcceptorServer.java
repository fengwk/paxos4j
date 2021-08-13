package fun.fengwk.paxos4j.acceptor;

import java.io.IOException;

/**
 * 接受者服务器
 *
 * @author fengwk
 */
public interface AcceptorServer {

    void start(RequestHandler handler) throws IOException;

    void stop() throws IOException;

}
