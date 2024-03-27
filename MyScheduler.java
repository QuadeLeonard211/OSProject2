// Josh Rowe and Quade Leonard

import java.util.*;
import java.util.concurrent.*;

public class MyScheduler {
    private int numJobs;
    private String property; // four possible options 'avg wait', 'max wait', 'combined', 'deadlines'
    // 'avg wait' use shortest job first
    LinkedBlcokingQueue<Job> outgoing;
    LinkedBlcokingQueue<Job> incoming;

    public MyScheduler(int numJobs, String property) {
        this.numJobs = numJobs;
        this.property = property;
        this.outgoing = getOutgoingQueue();
        this.incoming = getIncomingQueue();
        
    }

    public LinkedBlockingQueue<Job> getOutgoingQueue() {
        LinkedBlockingQueue<Job> outgoing = new LinkedBlockingQueue(numJobs);
        return outgoing;
    }

    public LinkedBlockingQueue<Job> getIncomingQueue() {
        LinkedBlcokingQueue<Job> incoming = new LinkedBlockingQueue(numJobs);
        return incoming;
    }

    public void run() {
        switch(property){
            case "avg wait":
            break;
            case "max wait":
            break;
            case "combined":
            break;
            case "deadlines":
            break;
            default:
            break;
        }
    }
}
