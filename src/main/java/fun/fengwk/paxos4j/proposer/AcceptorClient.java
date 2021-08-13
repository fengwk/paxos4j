package fun.fengwk.paxos4j.proposer;

import fun.fengwk.paxos4j.AcceptRequest;
import fun.fengwk.paxos4j.PrepareRequest;

import java.io.IOException;

/**
 * 接收者客户端
 *
 * @author fengwk
 */
public interface AcceptorClient {

     /**
      * 启动当前客户端
      *
      * @throws IOException
      */
     void start() throws IOException;

     /**
      * 停止当前客户端
      *
      * @throws IOException
      */
     void stop() throws IOException;

     /**
      * 阶段1.准备请求
      *
      * @param req
      * @param callback
      * @param ctx
      */
     void prepare(PrepareRequest req, PrepareCallback callback, Object ctx);

     /**
      * 阶段2，提案请求
      *
      * @param req
      * @param callback
      * @param ctx
      */
     void accept(AcceptRequest req, AcceptCallback callback, Object ctx);

}
