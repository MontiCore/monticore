/* (c) https://github.com/MontiCore/monticore */

package mc.feature.followoption;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.followoption.followoption.FollowOptionMill;
import mc.feature.followoption.followoption._parser.FollowOptionParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(FollowOptionMill.class)
public class FollowOptionTest {

  @Test
  public void test1() throws IOException {
    FollowOptionParser simpleAParser = FollowOptionMill.parser();
    simpleAParser.parse_StringA("test ,");
    assertFalse(simpleAParser.hasErrors());
  }
    
  @Test
  public void test2() throws IOException {
    FollowOptionParser simpleBParser = FollowOptionMill.parser();
    simpleBParser.parse_StringB("test ,");
    assertTrue(simpleBParser.hasErrors());
    MCAssertions.assertHasFindingStartingWith("Expected EOF but found token");
  }
  
  /**
   * Test assures that follow option is necessary, as this test fails to produce
   * correct behavior due to missing follow option
   * 
   */
  @Test
  public void test3() throws IOException {
    FollowOptionParser simpleParser = FollowOptionMill.parser();
    simpleParser.parse_StringB(",");
    
    assertTrue(simpleParser.hasErrors());
    MCAssertions.assertHasFindingStartingWith("Expected EOF but found token");
  }

  @Test
  public void test4() throws IOException {
    FollowOptionParser simpleAParser = FollowOptionMill.parser();
    simpleAParser.parse_StringA("test .");
    
    assertTrue(simpleAParser.hasErrors());
    MCAssertions.assertHasFindingStartingWith("mismatched input '.' expecting ','");
  }
}
