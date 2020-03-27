/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator.myownlanguage._cocos;

import de.monticore.expressions.commonexpressions._ast.ASTCommonExpressionsNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.typescalculator.myownlanguage._parser.MyOwnLanguageParser;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class TestPlusExpressionReturnsInt {

  @Before
  public void setup(){
    LogStub.init();
    Log.enableFailQuick(false);
  }

  protected MyOwnLanguageParser parser = new MyOwnLanguageParser();

  @Test
  public void testPlusExpressionValid() throws IOException {
    Optional<ASTExpression> p1 = parser.parse_StringExpression("3+4");
    assertTrue(p1.isPresent());
    MyOwnLanguageCoCoChecker coCoChecker = new MyOwnLanguageCoCoChecker().getMyOwnLanguageCoCoChecker();
    coCoChecker.checkAll(((ASTCommonExpressionsNode) p1.get()));
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testPlusExpressionInvalid() throws IOException{
    Optional<ASTExpression> p1 = parser.parse_StringExpression("3+4.5");
    assertTrue(p1.isPresent());
    MyOwnLanguageCoCoChecker coCoChecker = new MyOwnLanguageCoCoChecker().getMyOwnLanguageCoCoChecker();
    coCoChecker.checkAll((ASTCommonExpressionsNode) p1.get());
    assertFalse(Log.getFindings().isEmpty());
    assertTrue(Log.getFindings().get(0).getMsg().startsWith(PlusExpressionReturnsInt.ERROR_CODE));
  }

}
