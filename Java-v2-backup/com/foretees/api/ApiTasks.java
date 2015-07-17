/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.api;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.foretees.common.ProcessConstants;
import com.foretees.common.Connect;
import com.foretees.api.records.ClubInvoicingRule;
import com.foretees.api.cache.CacheBase;

/**
 *
 * @author Owner
 */
public class ApiTasks {
    
    private Timer timer = new Timer();
    
    private long start_time = System.currentTimeMillis();
    private long check_interval = TimeUnit.SECONDS.toMillis(1); // Check if it's time to run a given task every second
    
    private long invoicing_rules_next_run = start_time + TimeUnit.MINUTES.toMillis(1); // First run in 1 minute from app start
    private long invoicing_rules_interval = TimeUnit.HOURS.toMillis(1); // Run every hour 
    
    private long api_cache_check_next_run = start_time + TimeUnit.SECONDS.toMillis(1); // First run in 1 second after app start
    private long api_cache_check_interval = TimeUnit.SECONDS.toMillis(5); // Run every five seconds 

    public static void start() {
        //if(ProcessConstants.isDevServer()){  // replace with ProcessConstants.isTimer2Server()
            Connect.logError("ApiTasks: Starting ApiTaskManager on NODE:" + ProcessConstants.SERVER_ID);
            ApiTasks tasks = new ApiTasks();
            tasks.startTaskManager();
        //} else {
            //Connect.logError("ApiTasks: Not on DEV.  Nothing to do.");
            // Not a timer server... nothing to do?
        //}
    }

    public void startTaskManager() {
        timer.schedule(new ApiTaskManager(), check_interval, check_interval);
    }

    private class ApiTaskManager extends TimerTask {
        @Override
        public void run() {
            //Connect.logError("ApiTasks: checking tasks.");
            
            /* NOTE: This scheduler *intentionally* does not check if previously run tasks are still running.
                     Any tasks implemented here must be able to handle the possibility of running more 
                     than one instance of the task simultaneously.   It _must_ be fully thread safe. */
            
            Long current_time = System.currentTimeMillis();
            
            /* Start invoicing_rules_task  */
            try {
                if(
                        ProcessConstants.isDevServer() // Only run on this server (replace with ProcessConstants.isTimer2Server())
                        && current_time >= invoicing_rules_next_run){
                    // Time to run this task
                    // Set the next time we'll run.
                    invoicing_rules_next_run += invoicing_rules_interval;
                    // Run in a new thread, so it doesn't block execution of any other tasks we may add.
                    Thread task = new Thread(){
                        @Override
                        public void run(){
                            Connect.logError("ApiTasks: Running club invoicing rules");
                            ClubInvoicingRule.runReady();
                            Connect.logError("ApiTasks: Completed club invoicing rules");
                        }
                    };
                    task.start();
                } else {
                    //Connect.logError("ApiTasks: Not yet time for running invoicing_rules_task ");
                }
            } catch(Exception e) {
                Connect.logError("ApiTasks.ApiTaskManager invoicing_rules_task Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            }
            /* End invoicing_rules_task  */
            
            
            /* Start api_cache_clear_check_task */
            try {
                if(current_time >= api_cache_check_next_run){
                    // Time to run this task
                    // Set the next time we'll run.
                    api_cache_check_next_run += api_cache_check_interval;
                    // Run in a new thread, so it doesn't block execution of any other tasks we may add.
                    Thread task = new Thread(){
                        @Override
                        public void run(){
                            CacheBase.checkClearTrigger();
                        }
                    };
                    task.start();
                } else {
                    //Connect.logError("ApiTasks: Not yet time for running api_cache_clear_check_task");
                }
            } catch(Exception e) {
                Connect.logError("ApiTasks.ApiTaskManager api_cache_clear_check_task Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            }
            /* End api_cache_clear_check_task */
            
            
            // Other tasks here...
            
            
            // Re-schedule our timer.  (Using Java's period setting can cause problems.)
            if(!Thread.currentThread().isInterrupted()){
                //timer.schedule(new ApiTaskManager(), check_interval); // Check every minute
            } else {
                Connect.logError("ApiTasks: Shutting down ApiTaskManager");
            }
        }
    }
}
