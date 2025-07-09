/* (c) https://github.com/MontiCore/monticore */

import de.monticore.runtime.junit.TestWithMCLanguage;
import g1.G1Mill;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test that we can build expressions with a terminal present within the AST
 * without specifying this terminal
 */
@TestWithMCLanguage(g1.G1Mill.class)
public class G1TerminalBuildersTest {

  @Test
  public void testExprMandatory() {
    var elem = G1Mill.testExprMandatoryBuilder().setL("l").setR("r").build();
    Assertions.assertEquals("&&", elem.getOperator());
  }

  @Test
  public void testExprMandatoryEscaped() {
    var elem = G1Mill.testExprMandatoryEscapedBuilder().setL("l").setR("r").build();
    Assertions.assertEquals("\"hellö", elem.getOperator());
  }

  @Test
  public void testExprOpt() {
    var elem = G1Mill.testExprOptBuilder().setL("l").setR("r").build();
    Assertions.assertFalse(elem.isPresentOperator());
  }

  @Test
  public void testExprOptGroup() {
    var elem = G1Mill.testExprOptGroupBuilder().setL("l").setR("r").build();
    Assertions.assertFalse(elem.isPresentOperator());
  }

  @Test
  public void testExprStar() {
    var elem = G1Mill.testExprStarBuilder().setL("l").setR("r").build();
    Assertions.assertEquals(0, elem.getOperatorList().size());
  }

  @Test
  public void testExprStarGroup() {
    var elem = G1Mill.testExprStarGroupBuilder().setL("l").setR("r").build();
    Assertions.assertEquals(0, elem.getOperatorList().size());
  }

  @Test
  public void testExprPlus() {
    // One could consider if the correct answer here should be 1
    var elem = G1Mill.testExprPlusBuilder().setL("l").setR("r").build();
    Assertions.assertEquals(0, elem.getOperatorList().size());
  }

  @Test
  public void testExprPlusGroup() {
    // One could consider if the correct answer here should be 1
    var elem = G1Mill.testExprPlusGroupBuilder().setL("l").setR("r").build();
    Assertions.assertEquals(0, elem.getOperatorList().size());
  }

  @Test
  public void testExprALT() {
    // One could consider if this should lead to an error
    var elem = G1Mill.testExprALTBuilder().setL("l").setR("r").build();
    Assertions.assertFalse(elem.isPresentOperator());
  }
}
