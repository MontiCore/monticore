/* (c) https://github.com/MontiCore/monticore */

package mc.feature.followoption;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.followoption.followoption._parser.FollowOptionParser;

public class FollowOptionTest extends GeneratorIntegrationsTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void test1() throws IOException {
    
    //-- extractfile gen/FollowOptionTest.x
    FollowOptionParser simpleAParser = new FollowOptionParser();
    simpleAParser.parseA(new StringReader("test ,"));
    assertEquals(false, simpleAParser.hasErrors());
    //-- endfile gen/FollowOptionTest.x
    assertTrue(Log.getFindings().isEmpty());
  }
    
  @Test
  public void test2() throws IOException {    
    //-- extractfile gen/FollowOptionTest.x

    FollowOptionParser simpleBParser = new FollowOptionParser();
    simpleBParser.parseB(new StringReader("test ,"));
    assertEquals(true, simpleBParser.hasErrors());
    //-- endfile gen/FollowOptionTest.x
  }
  
  /**
   * Test assures that follow option is necessary, as this test fails to produce
   * correct behavior due to missing follow option
   * 
   */
  @Test
  public void test3() throws IOException {
    
    FollowOptionParser simpleParser = new FollowOptionParser();
    simpleParser.parseB(new StringReader(","));
    
    assertEquals(true, simpleParser.hasErrors());
  }

  @Test
  public void test4() throws IOException {
    
    FollowOptionParser simpleAParser = new FollowOptionParser();
    simpleAParser.parseA(new StringReader("test ."));

    assertEquals(true, simpleAParser.hasErrors());
  }
}
