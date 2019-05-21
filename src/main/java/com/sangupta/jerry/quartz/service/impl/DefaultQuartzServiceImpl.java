/**
 *
 * jerry - Common Java Functionality
 * Copyright (c) 2012, Sandeep Gupta
 *
 * http://www.sangupta/projects/jerry
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.sangupta.jerry.quartz.service.impl;

import com.sangupta.jerry.quartz.domain.QuartzJobInfo;
import com.sangupta.jerry.quartz.domain.QuartzJobTriggerInfo;
import com.sangupta.jerry.quartz.service.QuartzService;
import com.sangupta.jerry.util.AssertUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Default implementation for the {@link QuartzService}.
 *
 * @author sangupta
 */
public class DefaultQuartzServiceImpl implements QuartzService {

    private static final Log logger = LogFactory.getLog(DefaultQuartzServiceImpl.class);

    @Autowired
    private Scheduler scheduler;

    /**
	 * @return a {@link List} of all {@link QuartzJobInfo} objects available
	 * 
	 * @see QuartzService#getJobs()
	 */
    public List<QuartzJobInfo> getJobs() {
        List<QuartzJobInfo> jobs = new ArrayList<QuartzJobInfo>();
        try {
            List<String> jobGroupNames = this.scheduler.getJobGroupNames();

            List<JobExecutionContext> runningJobs = this.scheduler.getCurrentlyExecutingJobs();

            for (String jobGroupName : jobGroupNames) {
                final Set<JobKey> jobKeys = this.scheduler.getJobKeys(GroupMatcher.jobGroupEquals(jobGroupName));

                for (JobKey jobKey : jobKeys) {
                    // TODO: Find a better solution to return other details of the jobs.
                    // Because getJobDetail tries to instantiate the class of the type that is specified in the
                    // data store. Hence, we only return a list of job names and groups.
                    // JobDetail jobDetail = scheduler.getJobDetail(jobName, jobGroupName);

                    QuartzJobInfo jobInfo = new QuartzJobInfo();
                    jobInfo.setJobName(jobKey.getName());
                    jobInfo.setJobGroupName(jobGroupName);

                    @SuppressWarnings("unchecked")
                    List<Trigger> triggers = (List<Trigger>) this.scheduler.getTriggersOfJob(jobKey);

                    if (AssertUtils.isNotEmpty(triggers)) {
                        for (Trigger trigger : triggers) {
                            QuartzJobTriggerInfo triggerInfo = new QuartzJobTriggerInfo();

                            String triggerName = trigger.getKey().getName();
                            String triggerGroup = trigger.getKey().getGroup();
                            triggerInfo.setTriggerName(triggerName);
                            triggerInfo.setTriggerGroup(triggerGroup);
                            triggerInfo.setLastFireTime(trigger.getPreviousFireTime());
                            triggerInfo.setNextFireTime(trigger.getNextFireTime());

                            triggerInfo.setStatus(this.scheduler.getTriggerState(trigger.getKey()));

                            // check if the trigger is already running
                            for (JobExecutionContext jec : runningJobs) {
                                if (jec.getTrigger().equals(trigger)) {
                                    triggerInfo.setRunning(true);
                                    triggerInfo.setRunTime(jec.getJobRunTime());
                                    break;
                                }
                            }

                            jobInfo.addTriggerInfo(triggerInfo);
                        }
                    }

                    // jobInfo.setDescription(jobDetail.getDescription());
                    jobs.add(jobInfo);
                }
            }
            return jobs;
        } catch (SchedulerException e) {
            logger.error("error getting scheduled job info", e);
        }
        return null;
    }

    /**
	 * Pause the job with given name in given group
	 * 
	 * @param jobName
	 *            the job name
	 * 
	 * @param jobGroupName
	 *            the job group name
	 * 
	 * @return <code>true</code> if job was paused, <code>false</code> otherwise
	 * 
	 * @see QuartzService#pauseQuartzJob(String, String)
	 */
    public boolean pauseQuartzJob(String jobName, String jobGroupName) {
        try {
            this.scheduler.pauseJob(new JobKey(jobName, jobGroupName));
            return true;
        } catch (SchedulerException e) {
            logger.error("error pausing job: " + jobName + " in group: " + jobGroupName, e);
        }
        return false;
    }

    public boolean resumeQuartzJob(String jobName, String jobGroupName) {
        try {
            this.scheduler.resumeJob(new JobKey(jobName, jobGroupName));
            return true;
        } catch (SchedulerException e) {
            logger.error("error resuming job: " + jobName + " in group: " + jobGroupName, e);
        }
        return false;
    }


    /**
	 * Pause the Quartz scheduler.
	 * 
	 * @return <code>true</code> if paused, <code>false</code> otherwise
	 * 
	 * @see QuartzService#pauseQuartzScheduler()
	 */
    public boolean pauseQuartzScheduler() {
        try {
            this.scheduler.standby();
            return true;
        } catch (SchedulerException e) {
            logger.error("error pausing quartz scheduler", e);
        }
        return false;
    }

    /**
	 * Check if scheduler is paused, or not.
	 * 
	 * @return {@link Boolean} <code>true</code> if paused, <code>false</code>
	 *         otherwise, or <code>null</code> if we cannot fetch state
	 * 
	 * @see QuartzService#isSchedulerPaused()
	 */
    public Boolean isSchedulerPaused() {
        try {
            return this.scheduler.isInStandbyMode();
        } catch (SchedulerException e) {
            logger.error("error retrieveing scheduler condition", e);
        }
        return null;
    }

    /**
	 * Fire the given job in given group.
	 * 
	 * @param jobName
	 *            the name of the job
	 * 
	 * @param jobGroupName
	 *            the group name of the job
	 * 
	 * @return <code>true</code> if job was fired, <code>false</code> otherwise
	 * 
	 * @see QuartzService#executeJob(String, String)
	 */
    public boolean executeJob(String jobName, String jobGroupName) {
        try {
            this.scheduler.triggerJob(new JobKey(jobName, jobGroupName));
            return true;
        } catch (SchedulerException e) {
            logger.error("error executing job: " + jobName + " in group: " + jobGroupName, e);
        }
        return false;
    }

    @Override
    public boolean deleteQuartzJob(String jobName, String jobGroupName) {
        try {
            this.scheduler.deleteJob(new JobKey(jobName, jobGroupName));
            return true;
        } catch (SchedulerException e) {
            logger.error("error deleting job: " + jobName + " in group: " + jobGroupName, e);
        }
        return false;
    }

    @Override
    public boolean scheduleQuartzJob(JobDetail jobDetail, Trigger trigger) {
        try {
            this.scheduler.scheduleJob(jobDetail, trigger);
            return true;
        } catch (SchedulerException e) {
            logger.error("error scheduling job: " + jobDetail.getKey().getName(), e);
        }
        return false;
    }

    @Override
    public JobDetail getJobDetail(String jobName, String jobGroupName) {
        try {
            return this.scheduler.getJobDetail(new JobKey(jobName, jobGroupName));
        } catch (SchedulerException e) {
            logger.error("error getting job detail: " + jobName + " in group: " + jobGroupName, e);
        }
        return null;
    }

    // Usual getter/setter's follow

    /**
     * Returns the scheduler.
     *
     * @return the scheduler.
     */
    public Scheduler getScheduler() {
        return this.scheduler;
    }

    /**
     * Sets the scheduler to the specified value.
     *
     * @param scheduler scheduler to set.
     */
    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

}
