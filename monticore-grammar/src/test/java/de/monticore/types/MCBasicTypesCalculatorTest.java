package de.monticore.types;

import de.monticore.ast.ASTNodeBuilder;
import de.monticore.mcbasictypes1._ast.ASTBooleanTypeBuilder;
import de.monticore.mcexpressions._ast.ASTExpression;
import de.monticore.testmcexpressions._parser.TestMCExpressionsParser;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTType;
import de.monticore.types.mcbasictypestest._parser.MCBasicTypesTestParser;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class MCBasicTypesCalculatorTest {

  Map<String,Integer> lookUp;

  @Before
  public void init() {
    lookUp = new HashMap<>();

    Integer t = ASTConstantsMCBasicTypes.BOOLEAN;

    lookUp.put("var",ASTConstantsMCBasicTypes.BOOLEAN);
  }

  @Test
  public void test() {


    MCBasicTypesTestParser mcBasicTypesParser = new MCBasicTypesTestParser();
    // .parseType(primitive);

    try {
      Optional<ASTPrimitiveType> type = mcBasicTypesParser.parse_StringPrimitiveType("boolean");

      MCBasicTypesCalculator calc = new MCBasicTypesCalculator(lookUp);

      assertTrue(calc.isBool(type.get()));



    } catch (IOException e) {
      e.printStackTrace();
    }


  }
  @Test
  public void test2() {
    TestMCExpressionsParser mcExpressionsParser = new TestMCExpressionsParser();



    MCBasicTypesCalculator calc = new MCBasicTypesCalculator(lookUp);
    Boolean b = null;
    try {
      Optional<ASTExpression> exprOpt = mcExpressionsParser.parse_String("true || var");

      System.out.println(exprOpt.get().getClass());


      b = calc.isBool(exprOpt.get());
      System.out.println("Ergebnis: "+b);
    } catch (IOException e) {
      e.printStackTrace();
    }

    assertTrue(b);
  }

}
