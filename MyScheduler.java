// Josh Rowe and Quade Leonard

import java.util.*;
import java.util.concurrent.*;

public class MyScheduler {
    private int numJobs;
    private String property; // four possible options 'avg wait', 'max wait', 'combined', 'deadlines'
    // 'avg wait' - shortest job first
    // 'max wait' - first come first serve
    // 'combined' - combined FCFS and SJF
    // 'deadlines' - earliest deadline first
    LinkedBlockingQueue<Job> outgoing;
    LinkedBlockingQueue<Job> incoming;
    private Semaphore semaphore;

    public MyScheduler(int numJobs, String property) {
        this.numJobs = numJobs;
        this.property = property;
        this.outgoing = getOutgoingQueue();
        this.incoming = getIncomingQueue();
        this.semaphore = new Semaphore(numJobs / 2);
    }

    public LinkedBlockingQueue<Job> getOutgoingQueue() {
        LinkedBlockingQueue<Job> outgoing = new LinkedBlockingQueue(numJobs);
        return outgoing;
    }

    public LinkedBlockingQueue<Job> getIncomingQueue() {
        LinkedBlockingQueue<Job> incoming = new LinkedBlockingQueue(numJobs);
        return incoming;
    }

    public void run() {
        switch(property){
            case "avg wait":
            break;
            case "max wait":
                try{
                    semaphore.acquire();
                    outgoing.add(incoming.take());
                    semaphore.release();
                }
                catch (Exception e) {
                    System.out.println("There was an error");
                }
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
