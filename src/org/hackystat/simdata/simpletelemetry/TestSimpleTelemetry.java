package org.hackystat.simdata.simpletelemetry;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import javax.xml.datatype.XMLGregorianCalendar;

import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.dailyprojectdata.resource.build.jaxb.BuildDailyProjectData;
import org.hackystat.dailyprojectdata.resource.commit.jaxb.CommitDailyProjectData;
import org.hackystat.dailyprojectdata.resource.coverage.jaxb.CoverageDailyProjectData;
import org.hackystat.dailyprojectdata.resource.devtime.jaxb.DevTimeDailyProjectData;
import org.hackystat.dailyprojectdata.resource.devtime.jaxb.MemberData;
import org.hackystat.dailyprojectdata.resource.filemetric.jaxb.FileMetricDailyProjectData;
import org.hackystat.dailyprojectdata.resource.unittest.jaxb.UnitTestDailyProjectData;
import org.hackystat.simdata.SimData;
import org.hackystat.simdata.SimDataTestHelper;
import org.hackystat.utilities.tstamp.Tstamp;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the SimpleTelemetry scenario by retrieving DPDs for the first day and ensuring their
 * values are correct.
 * @author Philip Johnson
 */
public class TestSimpleTelemetry extends SimDataTestHelper {
  
  private boolean invokedSimpleTelemetry = false;

  /**
   * Runs the SimpleTelemetry scenario to set up the data on the SensorBase.
   * You can disable the data sending by setting the System property 
   * "org.hackystat.simdata.TestSimpleTelemetry.sendData" to "false", which
   * speeds up testing when you've already sent the data to your test sensorbase.
   * Can't use @BeforeClass since we can't get the sensorbase hostname.
   * @throws Exception If problems occur. 
   */
  @Before
  public void setupData() throws Exception {
    String sendData = System.getProperty("org.hackystat.simdata.TestSimpleTelemetry.sendData");
    // Only set up data if not disabled and we haven't done it previously.
    if (!"false".equals(sendData) && (!invokedSimpleTelemetry)) { 
      new SimpleTelemetry(this.getSensorBaseHostName());
      invokedSimpleTelemetry = true;
    }
  }
  
  /**
   * Tests the DailyProjectData instances associated with SimpleTelemetry.
   * @throws Exception If problems occur. 
   */
  @Test public void testDPD() throws Exception {
    String dpdHost = this.getDailyProjectDataHostName();
    String joe = SimpleTelemetry.joe + SimData.getTestDomain();
    String bob = SimpleTelemetry.bob + SimData.getTestDomain();
    String project = SimpleTelemetry.project;
    XMLGregorianCalendar day = Tstamp.makeTimestamp(SimpleTelemetry.startString);
    DailyProjectDataClient client = new DailyProjectDataClient(dpdHost, joe, joe);
    // Check DevTime for Day 1.
    DevTimeDailyProjectData devTime = client.getDevTime(joe, project, day);
    BigInteger joeDevTime = null;
    BigInteger bobDevTime = null;
    // Find Joe's devTime.
    for (MemberData data : devTime.getMemberData()) {
      if (data.getMemberUri().contains(joe)) {
        joeDevTime = data.getDevTime();
      }
      if (data.getMemberUri().contains(bob)) {
        bobDevTime = data.getDevTime();
      }
    }
    assertEquals("Checking Joe DevTime", 180, joeDevTime.intValue());
    assertEquals("Checking Bob DevTime", 200, bobDevTime.intValue());
  
    // Check builds for day 1.
    BuildDailyProjectData builds = client.getBuild(joe, project, day);
    int joeBuilds = 0;
    int bobBuilds = 0;
    for (org.hackystat.dailyprojectdata.resource.build.jaxb.MemberData memberData : 
      builds.getMemberData()) {
      if (memberData.getMemberUri().contains(joe)) {
        joeBuilds = memberData.getSuccess() + memberData.getFailure();
      }
      if (memberData.getMemberUri().contains(bob)) {
        bobBuilds = memberData.getSuccess() + memberData.getFailure();
      }
    }
    assertEquals("Checking Joe Builds", 2, joeBuilds);
    assertEquals("Checking Bob Builds", 5, bobBuilds);
    
    // Check size for day 1.
    FileMetricDailyProjectData fileMetric = client.getFileMetric(bob, project, day);
    assertEquals("Checking totalSize", 216, fileMetric.getTotalSizeMetricValue().intValue()); 

    
    // Check Unit Tests for day 1.
    UnitTestDailyProjectData tests = client.getUnitTest(joe, project, day);
    int joeTests = 0;
    int bobTests = 0;
    for (org.hackystat.dailyprojectdata.resource.unittest.jaxb.MemberData memberData : 
      tests.getMemberData()) {
      if (memberData.getMemberUri().contains(joe)) {
        joeTests = memberData.getSuccess().intValue() + memberData.getFailure().intValue();
      }
      if (memberData.getMemberUri().contains(bob)) {
        bobTests = memberData.getSuccess().intValue() + memberData.getFailure().intValue();
      }
    }
    assertEquals("Checking Joe Tests", 3, joeTests);
    assertEquals("Checking Bob Tests", 3, bobTests);
    
    // Check commits for day 1.
    CommitDailyProjectData commits = client.getCommit(joe, project, day);
    int joeChurn = 0;
    int bobChurn = 0;
    for (org.hackystat.dailyprojectdata.resource.commit.jaxb.MemberData memberData : 
      commits.getMemberData()) {
      if (memberData.getMemberUri().contains(joe)) {
        joeChurn = 
          memberData.getLinesAdded() + memberData.getLinesChanged() + memberData.getLinesDeleted();
      }
      if (memberData.getMemberUri().contains(bob)) {
        bobChurn = 
          memberData.getLinesAdded() + memberData.getLinesChanged() + memberData.getLinesDeleted();
      }
    }
    assertEquals("Checking Joe Churn", 13, joeChurn);
    assertEquals("Checking Bob Churn", 5, bobChurn);


    // Check Coverage for day 1.
    CoverageDailyProjectData coverage = client.getCoverage(joe, project, day, "line");
    for (org.hackystat.dailyprojectdata.resource.coverage.jaxb.ConstructData data : 
      coverage.getConstructData()) {
      if (data.getName().contains("Joe.java")) {
        assertEquals("Checking Joe.java coverage", 90, data.getNumCovered().intValue());
      }
      if (data.getName().contains("Bob.java")) {
        assertEquals("Checking Bob.java coverage", 93, data.getNumCovered().intValue());
      }
    }
  }
}
