/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight.cocos;

import de.monticore.javalight._cocos.JavaLightCoCoChecker;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.symbols.basicsymbols._ast.ASTBasicSymbolsNode;
import de.monticore.symbols.oosymbols._ast.ASTMethod;
import de.monticore.testjavalight.TestJavaLightMill;
import de.monticore.testjavalight._parser.TestJavaLightParser;
import de.monticore.testjavalight._visitor.TestJavaLightTraverser;
import de.monticore.types.check.FlatExpressionScopeSetter;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(TestJavaLightMill.class)
public class ReturnTypeAssignmentIsValidTest extends JavaLightCocoTest {
  
  @BeforeEach
  public void initCoco(){
    checker = new JavaLightCoCoChecker();
    checker.addCoCo(new ReturnTypeAssignmentIsValid());
  }
  
  @ParameterizedTest
  @ValueSource(strings = {
      "public void test(){return;}",
      "public int test(){return 3;}",
      "public char test(){return 'c';}",
      "public double test(){return 1.2;}",
      "public boolean test(){return false;}",
      "public double test(){return 2;}"
  })
  public void checkValid(String expressionString) throws IOException {
    TestJavaLightParser parser = TestJavaLightMill.parser();
    Optional<ASTMethod> optAST = parser.parse_StringMethod(expressionString);
    assertTrue(optAST.isPresent());

    TestJavaLightTraverser traverser = getFlatExpressionScopeSetter();
    optAST.get().accept(traverser);

    checker.checkAll((ASTBasicSymbolsNode) optAST.get());
  }
  
  static Stream<Arguments> checkInvalidArgs() {
    return Stream.of(
        Arguments.of("public void test(){return 3;}", ReturnTypeAssignmentIsValid.ERROR_CODE),
        Arguments.of("public int test(){return 3.0;}", ReturnTypeAssignmentIsValid.ERROR_CODE_3),
        Arguments.of("public char test(){return;}", ReturnTypeAssignmentIsValid.ERROR_CODE_2),
        Arguments.of("public double test(){return true;}", ReturnTypeAssignmentIsValid.ERROR_CODE_3),
        Arguments.of("public boolean test(){return 'f';}", ReturnTypeAssignmentIsValid.ERROR_CODE_3)
    );
  }

  @ParameterizedTest
  @MethodSource("checkInvalidArgs")
  public void checkInvalid(String expressionString, String expectedError) throws IOException {
    TestJavaLightParser parser = TestJavaLightMill.parser();
    Optional<ASTMethod> optAST = parser.parse_StringMethod(expressionString);
    assertTrue(optAST.isPresent());

    TestJavaLightTraverser traverser = getFlatExpressionScopeSetter();
    optAST.get().accept(traverser);
    checker.checkAll((ASTBasicSymbolsNode) optAST.get());

    Log.getFindings().remove(
        MCAssertions.assertHasFindingStartingWith(expectedError));

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