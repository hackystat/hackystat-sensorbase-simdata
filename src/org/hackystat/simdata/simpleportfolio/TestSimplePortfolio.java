package org.hackystat.simdata.simpleportfolio;

import static org.junit.Assert.assertTrue;
import javax.xml.datatype.XMLGregorianCalendar;
import org.hackystat.dailyprojectdata.client.DailyProjectDataClient;
import org.hackystat.dailyprojectdata.resource.devtime.jaxb.DevTimeDailyProjectData;
import org.hackystat.simdata.SimData;
import org.hackystat.simdata.SimDataTestHelper;
import org.hackystat.utilities.tstamp.Tstamp;
import org.junit.Test;

/**
 * Tests the SimplePortfolio scenario.
 * @author Shaoxuan Zhang
 */
public class TestSimplePortfolio extends SimDataTestHelper {

  /** The email of joe, the user in SimplePortfolio. */
  private static final String joe = SimplePortfolio.joe + SimData.getTestDomain();
  /** The project1 in SimplePortfolio. */
  private static final String project1 = SimplePortfolio.project1;
  /** Timestamp of yesterday. */
  private final XMLGregorianCalendar yesterday = Tstamp.makeTimestamp("2008-08-03");
  /** Timestamp of first day. */
  private final XMLGregorianCalendar firstDay = Tstamp.makeTimestamp("2008-07-01");
  
  /**
   * Constructor
   * @throws Exception when error occurs.
   */
  public TestSimplePortfolio() throws Exception {
    //Do nothing
  }
  
  /** 
   * Test the simple portfolio scenario.
   * @throws Exception If problems occur. 
   */
  @Test
  public void testSimplePortfolio() throws Exception {
    new SimplePortfolio(this.getSensorBaseHostName());
    DailyProjectDataClient dpdClient = 
      new DailyProjectDataClient(this.getDailyProjectDataHostName(), joe, joe);

    DevTimeDailyProjectData devTime = dpdClient.getDevTime(joe, project1, firstDay);
    int joeDevTime = devTime.getMemberData().get(0).getDevTime().intValue();
    assertTrue("devtime should no smaller than 3 hours", joeDevTime >= 180);
    assertTrue("devtime should no larger than 4 hours", joeDevTime <= 240);
    
    assertTrue("End LOC has at least 1000 more LOC than the first day.", 
        (dpdClient.getFileMetric(joe, project1, yesterday, "TotalLines").getTotal() - 
        dpdClient.getFileMetric(joe, project1, firstDay, "TotalLines").getTotal()) > 1000);
  }
}
