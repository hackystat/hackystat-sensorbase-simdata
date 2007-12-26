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
    assertEquals("Checking Joe DevTime", 175, joeDevTime.intValue());
    assertEquals("Checking Bob DevTime", 195, bobDevTime.intValue());
    
    // Check size for day 1.
    FileMetricDailyProjectData fileMetric = client.getFileMetric(joe, project, day);
    System.out.println(fileMetric.getTotalSizeMetricValue().intValue());
    
    // Check builds for day 1.
    BuildDailyProjectData builds = client.getBuild(joe, project, day);
    System.out.println("Builds for: " + builds.getOwner() + " " + project);
    for (org.hackystat.dailyprojectdata.resource.build.jaxb.MemberData memberData : builds.getMemberData()) {
      String member = memberData.getMemberUri();
      int failures = memberData.getFailure();
      int success = memberData.getSuccess();
      System.out.println(member + " " + failures + " " + success);
    }
    
    // Check Unit Tests for day 1.
    UnitTestDailyProjectData tests = client.getUnitTest(joe, project, day);
    System.out.println("UnitTests for: " + tests.getOwner() + " " + project);
    for (org.hackystat.dailyprojectdata.resource.unittest.jaxb.MemberData memberData : tests.getMemberData()) {
      String member = memberData.getMemberUri();
      int failures = memberData.getFailure().intValue();
      int success = memberData.getSuccess().intValue();
      System.out.println(member + " " + failures + " " + success);
    }
    
    // Check commits for day 1.
    CommitDailyProjectData commits = client.getCommit(joe, project, day);
    System.out.println("Commits for: " + tests.getOwner() + " " + project);
    for (org.hackystat.dailyprojectdata.resource.commit.jaxb.MemberData memberData : commits.getMemberData()) {
      String member = memberData.getMemberUri();
      int numCommits = memberData.getCommits();
      System.out.println(member + " " + numCommits);
    }

    // Check Coverage for day 1.
    CoverageDailyProjectData coverage = client.getCoverage(joe, project, day, "line");
    System.out.println("Coverage for: " + tests.getOwner() + " " + project);
    for (org.hackystat.dailyprojectdata.resource.coverage.jaxb.ConstructData data : coverage.getConstructData()) {
      String name = data.getName();
      int covered = data.getNumCovered();
      int uncovered = data.getNumUncovered();
      System.out.println(name + " " + covered + " " + uncovered);
    }
  }
}
