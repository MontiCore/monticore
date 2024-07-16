/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight._cocos.JavaLightCoCoChecker;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._ast.ASTBasicSymbolsNode;
import de.monticore.symbols.oosymbols._ast.ASTMethod;
import de.monticore.testjavalight.TestJavaLightMill;
import de.monticore.testjavalight._parser.TestJavaLightParser;
import de.monticore.testjavalight._visitor.TestJavaLightTraverser;
import de.monticore.types.check.FlatExpressionScopeSetter;
import de.monticore.types.check.FullDeriveFromCombineExpressionsWithLiterals;
import de.monticore.types.check.FullSynthesizeFromCombineExpressionsWithLiterals;
import de.monticore.types.check.TypeCalculator;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReturnTypeAssignmentIsValidTest extends JavaLightCocoTest {
  
  @BeforeEach
  public void initCoco(){
    checker = new JavaLightCoCoChecker();
    checker.addCoCo(new ReturnTypeAssignmentIsValid(new TypeCalculator(new FullSynthesizeFromCombineExpressionsWithLiterals(), new FullDeriveFromCombineExpressionsWithLiterals())));
  }
  
  public void checkValid(String expressionString) throws IOException {
  
    TestJavaLightParser parser = new TestJavaLightParser();
    Optional<ASTMethod> optAST = parser.parse_StringMethod(expressionString);
    Assertions.assertTrue(optAST.isPresent());
    Log.getFindings().clear();
    TestJavaLightTraverser traverser = getFlatExpressionScopeSetter();
    optAST.get().accept(traverser);
    checker.checkAll((ASTBasicSymbolsNode) optAST.get());
    Assertions.assertTrue(Log.getFindings().isEmpty());
    
  }

  public void checkInvalid(String expressionString) throws IOException {

    TestJavaLightParser parser = new TestJavaLightParser();
    Optional<ASTMethod> optAST = parser.parse_StringMethod(expressionString);
    Assertions.assertTrue(optAST.isPresent());
    Log.getFindings().clear();
    TestJavaLightTraverser traverser = getFlatExpressionScopeSetter();
    optAST.get().accept(traverser);
    checker.checkAll((ASTBasicSymbolsNode) optAST.get());
    Assertions.assertFalse(Log.getFindings().isEmpty());

  }

  @Test
  public void testValid() throws IOException {

    checkValid("public void test(){return;}");
    checkValid("public int test(){return 3;}");
    checkValid("public char test(){return 'c';}");
    checkValid("public double test(){return 1.2;}");
    checkValid("public boolean test(){return false;}");

  }

  @Test
  public void testInvalid() throws IOException {

    checkInvalid("public void test(){return 3;}");
    checkInvalid("public int test(){return 3.0;}");
    checkInvalid("public char test(){return;}");
    checkInvalid("public double test(){return true;}");
    checkInvalid("public boolean test(){return 'f';}");

  }

  protected TestJavaLightTraverser getFlatExpressionScopeSetter() {
    FlatExpressionScopeSetter flatExpressionScopeSetter = new FlatExpressionScopeSetter(TestJavaLightMill.globalScope());
    TestJavaLightTraverser traverser = TestJavaLightMill.traverser();
    traverser.add4ExpressionsBasis(flatExpressionScopeSetter);
    traverser.add4AssignmentExpressions(flatExpressionScopeSetter);
    traverser.add4CommonExpressions(flatExpressionScopeSetter);
    traverser.add4JavaClassExpressions(flatExpressionScopeSetter);
    traverser.add4MCBasicTypes(flatExpressionScopeSetter);
    traverser.add4MCCollectionTypes(flatExpressionScopeSetter);
    traverser.add4MCCommonLiterals(flatExpressionScopeSetter);
    return traverser;
  }

}