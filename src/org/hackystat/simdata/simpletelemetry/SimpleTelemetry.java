package org.hackystat.simdata.simpletelemetry;

import java.util.Random;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hackystat.sensorbase.client.SensorBaseClientException;
import org.hackystat.simdata.SimData;
import org.hackystat.utilities.tstamp.Tstamp;

/**
 * Illustrates simple use of Telemetry to understand development.  There are two developers, 
 * Joe and Bob, who are working on a Project in the "simpletelemetry" directory. Joe always works on
 * the file "Joe.java", and Bob always works on the file "Bob.java".   
 * We look at a number of Scrum "sprints", each lasting 1 week (5 days).
 * <ul>
 * <li> Sprint 1 (06/01 - 06/05): "Healthy" project.
 * <li> Sprint 2 2 (06-08 - 06/12): "Late start" week.   
 * </ul>
 * 
 * @author Philip Johnson
 *
 */
public class SimpleTelemetry {
  private SimData simData;
  private String joe = "joe.simpletelemetry";
  private String bob = "bob.simpletelemetry";
  private String project = "simpletelemetry";
  private String projectUriPattern = "*/" + project + "/*";
  private String joeDir = "/users/joe/" + project + "/";
  private String bobDir = "/users/bob/" + project + "/";
  private String joeFile = joeDir + "Joe.java";
  private String bobFile = bobDir + "Bob.java";
  private XMLGregorianCalendar start = Tstamp.makeTimestamp("2007-06-01");
  private XMLGregorianCalendar end = Tstamp.makeTimestamp("2007-08-01");
  private Random random = new Random(0);
  
  private static final String SUCCESS = "Success";
  private static final String PASS = "pass";
  
  /**
   * Sets up the simpletelemetry scenario.  Creates the developers and the project.
   * @param host The SensorBase host that will receive the data. 
   * @throws Exception If problems occur.
   */
  public SimpleTelemetry(String host) throws Exception {
    this.simData = new SimData(host);
    this.simData.getLogger().info("Initializing SimpleTelemetry scenario");
    this.simData.makeUser(joe);
    this.simData.makeUser(bob);
    this.simData.makeProject(project, joe, start, end, projectUriPattern);
    this.simData.addMember(project, joe, bob);
  }
  
  /**
   * Create the first Sprint. It's a "healthy" sprint:
   * <ul>
   * <li> Effort is constant, between an hour and an hour and a half per day.
   * <li> Size increases steadily, starting at about 100 and increasing by 10 or so lines per day.
   * <li> Builds and unit tests between 2-6 times a day.
   * <li> Coverage is always at least 80%.
   * <li> They each commit once a day, with relatively low churn (less than 20%).
   * @throws SensorBaseClientException If problems occur.
   */
  public void makeSprint1() throws SensorBaseClientException {
    for (int i = 0; i < 5; i++) {
      XMLGregorianCalendar day = Tstamp.incrementDays(start, i);
      this.simData.getLogger().info("SimpleTelemetry: Making data for day: " + day);
      
      // Effort is constant, between an hour and an hour and a half per day.
      this.simData.addDevEvents(joe, day, 12 + random.nextInt(7), joeFile);
      this.simData.addDevEvents(bob, day, 12 + random.nextInt(7), bobFile);
      
      // Size increases steadily, starting at about 100 and increasing by 10 or so lines per day.
      int joeFileSize = 100 + (i * 10) + random.nextInt(10); 
      int bobFileSize = 100 + (i * 10) + random.nextInt(10); 
      simData.addFileMetric(joe, start, joeFile, joeFileSize);
      simData.addFileMetric(bob, start, bobFile, bobFileSize);
      
      // Builds and unit tests between 2-6 times a day.
      simData.addBuilds(joe, start, joeDir, SUCCESS, 2 + random.nextInt(5));
      simData.addBuilds(bob, start, bobDir, SUCCESS, 2 + random.nextInt(5));
      simData.addUnitTests(joe, start, joeFile, PASS, 2 + random.nextInt(5));
      simData.addUnitTests(bob, start, bobFile, PASS, 2 + random.nextInt(5));
      
      // Coverage is always at least 80%.
      int joeUncovered = random.nextInt(20);
      int bobUncovered = random.nextInt(20);
      simData.addCoverage(joe, start, joeFile, (joeFileSize - joeUncovered), joeUncovered);
      simData.addCoverage(bob, start, bobFile, (bobFileSize - bobUncovered), bobUncovered);
      
      // They each commit once a day, with relatively low churn (less than 20%).
      simData.addCommit(joe, start, joeFile, joeFileSize, random.nextInt(10), random.nextInt(10));
      simData.addCommit(bob, start, bobFile, bobFileSize, random.nextInt(10), random.nextInt(10));
    }
  }
  
  /**
   * Create the second Sprint. Work suffers from not getting started until late in the sprint.
   * <ul>
   * <li> Effort is very low for first three days, then very high.
   * <li> Size is very low for first three days, then jumps.
   * <li> Builds and unit tests very low for first three days, then high.
   * <li> Coverage is very low for first three days, never gets high.
   * <li> No commits for first three days, then many in last two. High churn each time.
   * @throws SensorBaseClientException If problems occur.
   */
  public void makeSprint2() throws SensorBaseClientException {
    // Move forward 7 days to start the second sprint.
    int dayOffset = 7;
    // Do first three days of sprint in one loop.
    for (int i = dayOffset + 0; i < dayOffset + 3; i++) {
      XMLGregorianCalendar day = Tstamp.incrementDays(start, i);
      this.simData.getLogger().info("SimpleTelemetry: Making data for day: " + day);
      
      // Effort is low, between zero minutes and 20 minutes.
      this.simData.addDevEvents(joe, day, 0 + random.nextInt(4), joeFile);
      this.simData.addDevEvents(bob, day, 0 + random.nextInt(4), bobFile);
      
      // Size is very low, between 20 and 30 LOC.
      int joeFileSize = 20 + random.nextInt(10); 
      int bobFileSize = 20 + random.nextInt(10); 
      simData.addFileMetric(joe, start, joeFile, joeFileSize);
      simData.addFileMetric(bob, start, bobFile, bobFileSize);
      
      // Builds and unit tests between 0-2 times a day.
      simData.addBuilds(joe, start, joeDir, SUCCESS, 0 + random.nextInt(1));
      simData.addBuilds(bob, start, bobDir, SUCCESS, 0 + random.nextInt(1));
      simData.addUnitTests(joe, start, joeFile, PASS, 0 + random.nextInt(1));
      simData.addUnitTests(bob, start, bobFile, PASS, 0 + random.nextInt(1));
      
      // Coverage is 0 - 10%
      int joeCovered = random.nextInt(10);
      int bobCovered = random.nextInt(10);
      simData.addCoverage(joe, start, joeFile, joeCovered, (joeFileSize - joeCovered));
      simData.addCoverage(bob, start, bobFile, bobCovered, (bobFileSize - bobCovered));
      
      // No commits for first three days.
    }
    
    // Now do last two days.
    for (int i = dayOffset + 3; i < dayOffset + 5; i++) {
      XMLGregorianCalendar day = Tstamp.incrementDays(start, i);
      this.simData.getLogger().info("SimpleTelemetry: Making data for day: " + day);
      
      // Effort is high, between 8 and 10 hours per day.
      this.simData.addDevEvents(joe, day, (12 * 8) + random.nextInt(12 * 2), joeFile);
      this.simData.addDevEvents(bob, day, (12 * 8) + random.nextInt(12 * 2), bobFile);
      
      // Size increases dramatically, by 200 or so lines per day.
      int joeFileSize = (i * 100) +  random.nextInt(200); 
      int bobFileSize = (i * 100) + random.nextInt(200); 
      simData.addFileMetric(joe, start, joeFile, joeFileSize);
      simData.addFileMetric(bob, start, bobFile, bobFileSize);
      
      // Builds and unit tests between 20 and 40 per day
      simData.addBuilds(joe, start, joeDir, SUCCESS, 20 + random.nextInt(20));
      simData.addBuilds(bob, start, bobDir, SUCCESS, 20 + random.nextInt(20));
      simData.addUnitTests(joe, start, joeFile, PASS, 20 + random.nextInt(20));
      simData.addUnitTests(bob, start, bobFile, PASS, 20 + random.nextInt(20));
      
      // Coverage is around 40%
      int joeCovered = 30 + random.nextInt(20);
      int bobCovered = 30 + random.nextInt(20);
      simData.addCoverage(joe, start, joeFile, joeCovered, (joeFileSize - joeCovered));
      simData.addCoverage(bob, start, bobFile, bobCovered, (bobFileSize - bobCovered));
      
      // Lots of commits for last two days, with high churn.
      simData.addCommit(joe, start, joeFile, joeFileSize, 
          random.nextInt(joeFileSize), random.nextInt(joeFileSize));
      simData.addCommit(bob, start, bobFile, bobFileSize, 
          random.nextInt(bobFileSize), random.nextInt(bobFileSize));
    }
  }
}
