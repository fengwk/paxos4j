package fun.fengwk.paxos4j.proposer;

import fun.fengwk.paxos4j.Response;

import java.io.IOException;

/**
 * @author fengwk
 */
public interface AcceptCallback {

    /**
     * 处理响应内容
     *
     * @param resp
     * @param ctx
     */
    void handleAcceptResponse(Response resp, Object ctx);

    /**
     * 如果连接失败则回调该方法
     *
     * @param ex
     * @param ctx
     */
    void handleAcceptException(IOException ex, Object ctx);

}
