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
    PriorityBlockingQueue<Job> deadlineStuff;
    //LinkedBlockingQueue<Job> DLoutgoing;
    private Semaphore semaphore;

    public MyScheduler(int numJobs, String property) {
        this.numJobs = numJobs;
        this.property = property;
        this.outgoing = new LinkedBlockingQueue<>(1); //was 1
        this.incoming = new LinkedBlockingQueue<>(numJobs / 4);
        this.semaphore = new Semaphore(numJobs / 2);
        //this.DLoutgoing = new LinkedBlockingQueue<>(numJobs/4);
        this.deadlineStuff = new PriorityBlockingQueue<>(numJobs/4, new Comparator<Job>() {
            public int compare(Job job1, Job job2) {
                if (job1.getDeadline() < job2.getDeadline()) {
                    return -1;
                } else if (job1.getDeadline() > job2.getDeadline()){
                    return 1;
                } else {
                    return 0;
                }
            }
        });
    }

    public LinkedBlockingQueue<Job> getOutgoingQueue() {
        // LinkedBlockingQueue<Job> outgoing = new LinkedBlockingQueue(numJobs);
        return outgoing;
    }

    public LinkedBlockingQueue<Job> getIncomingQueue() {
        // LinkedBlockingQueue<Job> incoming = new LinkedBlockingQueue(numJobs);
        return incoming;
    }

    public PriorityBlockingQueue<Job> getPriorityQueue() {
        try {    
            for (int i = 0; i < numJobs; i++){
                Job tempForPBQ = incoming.take();
                deadlineStuff.add(tempForPBQ);
            }
        } catch(Exception e) {
            System.out.println("Fuck");
        }
        return deadlineStuff;
    }

    public void run() {
        int jobsRemaining = numJobs;
        //System.out.println("RUN HAS BEGUN");
        //while(incoming.size() > 0){

        //switch (property) {
            //case "avg wait":
            if (property == "avg wait") {
                while(jobsRemaining != 0){
                    //System.out.print(""); //TF2 Coconut. For some reason this is needed to have code run consistantly
                    try {
                        // semaphore.acquire();
                        Job shortest = incoming.peek();
                        //System.out.println(shortest);
                        if (shortest != null){
                        
                            for(Job job : incoming){
                                if(job.getLength() < shortest.getLength()){
                                    shortest = job;
                                }
                            }
                            //semaphore.acquire();
                            outgoing.put(shortest);
                            incoming.remove(shortest);
                            //incoming.take();
                            //semaphore.release();
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
                    //semaphore.acquire();
                    outgoing.put(incoming.take());
                    //semaphore.release();
                } catch (Exception e) {
                    System.out.println("There was an error");
                    e.printStackTrace();
                }
                jobsRemaining--;
            }
            } //break; 
                
            //case "combined":
            if (property == "combined"){
                //System.out.println("You arent supposed to be here (combined)");
                while(jobsRemaining != 0){
                    //System.out.print(""); //TF2 Coconut. For some reason this is needed to have code run consistantly
                    if (incoming.size() <= numJobs/4){ //Code below is copy/paste from max wait
                        try { 
                            // System.out.println("MAX WAIT ENTERED");
                            //semaphore.acquire();
                            outgoing.put(incoming.take());
                            //semaphore.release();
                        } catch (Exception e) {
                            System.out.println("There was an error");
                            e.printStackTrace();
                        }
                    jobsRemaining--;
                    }
                    else { //Code below is copy/pasted from avg wait
                        try {
                            //semaphore.acquire();
                            Job shortestCombined = incoming.peek();
                            //System.out.println(shortest);
                            if (shortestCombined != null){
                            
                                for(Job job : incoming){
                                    if(job.getLength() < shortestCombined.getLength()){
                                        shortestCombined = job;
                                    }
                                }
                                //semaphore.acquire();
                                outgoing.put(shortestCombined);
                                incoming.remove(shortestCombined);
                                //incoming.take();
                                //semaphore.release();
                            }else {
                                //System.out.println("CODE FAILED: RETRY");
                                //System.out.println(incoming.size());
                                jobsRemaining++;
                            }
                        }catch (Exception e) {
                            System.out.println("There was an error");
                            e.printStackTrace();
                        }
                        jobsRemaining--;
                    }
                }
            } //break;

            //case "deadlines":
            if (property == "deadlines") {
                Thread deadlineExclusiveThread = new Thread(this::getPriorityQueue);
                deadlineExclusiveThread.start();
                //Stuff for deadline tracking
                LinkedBlockingQueue<Job> expiredDeadline = new LinkedBlockingQueue<>(numJobs/4); //buffer for storing expired jobs
                int numExpired = 0;
                long lastJobRuntime = 0; //How long the last job ran for
                
                for(int i = 0; i < numJobs; i++){
                    long elapsedTime = System.currentTimeMillis(); //the (calcluated) total time that has elapsed
                    //System.out.print(""); //TF2 Coconut. For some reason this is needed to have code run consistantly
                    try {
                        Job testJob = deadlineStuff.take();
                        long expectedFinishTime = lastJobRuntime + elapsedTime + testJob.getLength();
                        if (expectedFinishTime < testJob.getDeadline()){ //will finish in time
                            //semaphore.acquire();
                            //Update variables
                            //elapsedTime += testJob.getLength();
                            lastJobRuntime = testJob.getLength();
                            //send to outgoing
                            outgoing.put(testJob);
                            //semaphore.release();
                        } else { //will NOT finish in time
                            //semaphore.acquire();
                            //Update variables
                            numExpired++;
                            //elapsedTime++;
                            lastJobRuntime = 1;
                            //send to expired stack
                            expiredDeadline.put(testJob);
                            //semaphore.release();
                        }
                    } catch (Exception e) {
                        System.out.println("There was an error");
                        e.printStackTrace();
                    }
                    //jobsRemaining--;
                }
                //now to do the expired jobs
                for (int i = 0; i < numExpired; i++){
                    try {
                        //semaphore.acquire();
                        outgoing.put(expiredDeadline.take());
                        //semaphore.release();
                    } catch (Exception e){
                        System.out.println("There was an error");
                        e.printStackTrace();
                    }

                }
            } //break;

            // default:
            //     System.out.println("DEFAULT DANCE");
            //     break;
        //}
        // }
        //System.out.println("RUN HAS LEFT THE BUILDING");
    //}
}
}
