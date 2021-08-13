package fun.fengwk.paxos4j;

import fun.fengwk.paxos4j.acceptor.Acceptor;
import fun.fengwk.paxos4j.acceptor.AcceptorServer;
import fun.fengwk.paxos4j.proposer.Proposer;
import fun.fengwk.paxos4j.proposer.Waiter;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author fengwk
 */
public class PaxosTest {

    @Test
    public void test() throws InterruptedException, IOException {
        BlockingQueue<MockRequest> requestQueue1 = new LinkedBlockingQueue<>();
        BlockingQueue<MockResponse> responseQueue1 = new LinkedBlockingQueue<>();
        AcceptorServer server1 = new MockAcceptorServer(requestQueue1, responseQueue1);
        MockAcceptorClient client1= new MockAcceptorClient(requestQueue1, responseQueue1);

        BlockingQueue<MockRequest> requestQueue2 = new LinkedBlockingQueue<>();
        BlockingQueue<MockResponse> responseQueue2 = new LinkedBlockingQueue<>();
        AcceptorServer server2 = new MockAcceptorServer(requestQueue2, responseQueue2);
        MockAcceptorClient client2= new MockAcceptorClient(requestQueue2, responseQueue2);

        BlockingQueue<MockRequest> requestQueue3 = new LinkedBlockingQueue<>();
        BlockingQueue<MockResponse> responseQueue3 = new LinkedBlockingQueue<>();
        AcceptorServer server3 = new MockAcceptorServer(requestQueue3, responseQueue3);
        MockAcceptorClient client3= new MockAcceptorClient(requestQueue3, responseQueue3);

        Proposer proposer1 = new Proposer(1, Arrays.asList(client1, client2, client3), new LinkedHashMap<>(), new Waiter(0, 0L, 300L));
        Proposer proposer2 = new Proposer(2, Arrays.asList(client1, client2, client3), new LinkedHashMap<>(), new Waiter(1, 0L, 300L));
        Proposer proposer3 = new Proposer(3, Arrays.asList(client1, client2, client3), new LinkedHashMap<>(), new Waiter(2, 0L, 300L));

        Acceptor acceptor1 = new Acceptor(server1, new LinkedHashMap<>());
        Acceptor acceptor2 = new Acceptor(server2, new LinkedHashMap<>());
        Acceptor acceptor3 = new Acceptor(server3, new LinkedHashMap<>());

        PaxosNode paxosNode1 = new PaxosNode(proposer1, acceptor1);
        PaxosNode paxosNode2 = new PaxosNode(proposer2, acceptor2);
        PaxosNode paxosNode3 = new PaxosNode(proposer3, acceptor3);

        paxosNode1.start();
        paxosNode2.start();
        paxosNode3.start();

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    paxosNode1.propose("paxosNode1-" + i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    paxosNode2.propose("paxosNode2-" + i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread t3 = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    paxosNode3.propose("paxosNode3-" + i);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        paxosNode1.stop();
        paxosNode2.stop();
        paxosNode3.stop();

        System.out.println("paxosNode1---------");
        System.out.println(paxosNode1.getProposals());
        System.out.println("paxosNode2---------");
        System.out.println(paxosNode2.getProposals());
        System.out.println("paxosNode3---------");
        System.out.println(paxosNode3.getProposals());
    }

}
