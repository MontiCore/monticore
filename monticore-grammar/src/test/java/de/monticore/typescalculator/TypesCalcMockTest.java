/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.testcommonexpressions._parser.TestCommonExpressionsParser;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;
import de.monticore.types.mcbasictypestest._parser.MCBasicTypesTestParser;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static junit.framework.TestCase.assertTrue;

public class TypesCalcMockTest {
  private ExpressionAndLiteralsTypeCalculatorVisitorMock mock;

  @Before
  public void setUp() {

    mock = new ExpressionAndLiteralsTypeCalculatorVisitorMock();
    TypesCalculator.setExpressionAndLiteralsTypeCalculator(mock);

  }

  @Test
  public void isBooleanTest() throws IOException {

    TestCommonExpressionsParser p = new TestCommonExpressionsParser();
    Optional<ASTExpression> expr = p.parse_StringExpression("9 + 8");
    MCBasicTypesTestParser typesParser = new MCBasicTypesTestParser();
    ASTMCPrimitiveType type = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();

    mock.addLookUp(expr.get(), type);


    Boolean testExpression = TypesCalculator.isBoolean(expr.get());
    assertTrue(testExpression);


  }
}
