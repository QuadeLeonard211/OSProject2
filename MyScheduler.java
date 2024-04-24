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
        this.outgoing = new LinkedBlockingQueue<>(1);
        this.incoming = new LinkedBlockingQueue<>(numJobs / 4);
        this.semaphore = new Semaphore(numJobs / 2);
    }

    public LinkedBlockingQueue<Job> getOutgoingQueue() {
        // LinkedBlockingQueue<Job> outgoing = new LinkedBlockingQueue(numJobs);
        return outgoing;
    }

    public LinkedBlockingQueue<Job> getIncomingQueue() {
        // LinkedBlockingQueue<Job> incoming = new LinkedBlockingQueue(numJobs);
        return incoming;
    }

    public void run() {
        int jobsRemaining = numJobs;
        System.out.println("RUN HAS BEGUN");
        //while(incoming.size() > 0){

        //switch (property) {
            //case "avg wait":
            if (property == "avg wait") {
                while(jobsRemaining != 0){
                    System.out.print(""); //TF2 Coconut. For some reason this is needed to have code run consistantly
                    try {
                        semaphore.acquire();
                        Job shortest = incoming.peek();
                        //System.out.println(shortest);
                        if (shortest != null){
                        
                            for(Job job : incoming){
                                if(job.getLength() < shortest.getLength()){
                                    shortest = job;
                                }
                            }
                            outgoing.put(shortest);
                            incoming.remove(shortest);
                            //incoming.take();
                            semaphore.release();
                        } else{
                            //System.out.println("CODE FAILED: RETRY");
                            //System.out.println(incoming.size());
                            jobsRemaining++;
                        }
                    } catch (Exception e) {
                        System.out.println("There was an error");
                        e.printStackTrace();
                    }
                    jobsRemaining--;
                }
            } //break; 

            //case "max wait":
            if (property == "max wait") {
                while (jobsRemaining != 0) {
                    
                try {
                    // System.out.println("MAX WAIT ENTERED");
                    semaphore.acquire();
                    outgoing.put(incoming.take());
                    semaphore.release();
                } catch (Exception e) {
                    System.out.println("There was an error");
                    e.printStackTrace();
                }
                jobsRemaining--;
            }
            } //break; 
                
            //case "combined":
            if (property == "combined"){
                System.out.println("You arent supposed to be here (combined)");
            } //break;

            //case "deadlines":
            if (property == "deadlines") {
                System.out.println("You arent supposed to be here (deadlines)");
            } //break;

            // default:
            //     System.out.println("DEFAULT DANCE");
            //     break;
        //}
        // }
        System.out.println("RUN HAS LEFT THE BUILDING");
    //}
}
}
