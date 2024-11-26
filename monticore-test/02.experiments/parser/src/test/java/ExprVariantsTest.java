/* (c) https://github.com/MontiCore/monticore */

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import exprvar1.ExprVar1Mill;
import exprvar2.ExprVar2Mill;
import exprvars.ExprVarsMill;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ExprVariantsTest {
  
  @BeforeEach
  public void before() {
    LogStub.initPlusLog();
    Log.enableFailQuick(false);
  }

  @Test
  public void testVariant1() throws IOException {
    ExprVar1Mill.init();
    var parser = ExprVar1Mill.parser();
    var opt = parser.parse_StringExpression("new ABC");

    Assertions.assertTrue(opt.isPresent());
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testVariant2() throws IOException {
    // TODO: This might be solved by the common startRule+EOF idea?
    ExprVar2Mill.init();
    var parser = ExprVar2Mill.parser();
    var opt = parser.parse_StringExpression("new ABC");

    Assertions.assertTrue(opt.isPresent());
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testVariantC() throws IOException {
    ExprVarsMill.init();
    var parser = ExprVarsMill.parser();
    var opt = parser.parse_StringExpression("new ABC");

    Assertions.assertTrue(opt.isPresent());
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


}
