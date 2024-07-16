/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator.myownlanguage._cocos;

import de.monticore.expressions.commonexpressions._ast.ASTCommonExpressionsNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.typescalculator.myownlanguage.MyOwnLanguageMill;
import mc.typescalculator.myownlanguage._parser.MyOwnLanguageParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

public class PlusExpressionReturnsIntTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeEach
  public void setup(){
    MyOwnLanguageMill.reset();
    MyOwnLanguageMill.init();
    BasicSymbolsMill.initializePrimitives();
  }

  protected MyOwnLanguageParser parser = new MyOwnLanguageParser();

  @Test
  public void testPlusExpressionValid() throws IOException {
    Optional<ASTExpression> p1 = parser.parse_StringExpression("3+4");
    Assertions.assertTrue(p1.isPresent());
    MyOwnLanguageCoCoChecker coCoChecker = new MyOwnLanguageCoCoChecker().getMyOwnLanguageCoCoChecker();
    coCoChecker.checkAll(((ASTCommonExpressionsNode) p1.get()));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testPlusExpressionInvalid() throws IOException{
    Optional<ASTExpression> p1 = parser.parse_StringExpression("3+4.5");
    Assertions.assertTrue(p1.isPresent());
    MyOwnLanguageCoCoChecker coCoChecker = new MyOwnLanguageCoCoChecker().getMyOwnLanguageCoCoChecker();
    coCoChecker.checkAll((ASTCommonExpressionsNode) p1.get());
    Assertions.assertFalse(Log.getFindings().isEmpty());
    Assertions.assertTrue(Log.getFindings().get(0).getMsg().startsWith(PlusExpressionReturnsInt.ERROR_CODE));
  }

}
