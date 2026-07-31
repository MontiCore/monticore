/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.combineexpressionswithliterals._cocos;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.javaclassexpressions._ast.ASTClassExpression;
import de.monticore.expressions.javaclassexpressions._ast.ASTJavaClassExpressionsNode;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(CombineExpressionsWithLiteralsMill.class)
public class TestNoClassExpressionForGenerics {

  CombineExpressionsWithLiteralsParser p;

  @BeforeEach
  public void setup(){
    p = CombineExpressionsWithLiteralsMill.parser();
  }

  @Test
  public void testValid() throws IOException {
    Optional<ASTClassExpression> optClass = p.parse_StringClassExpression("Integer.class");

    assertTrue(optClass.isPresent());

    CombineExpressionsWithLiteralsCoCoChecker coCoChecker = new CombineExpressionsWithLiteralsCoCoChecker().getCombineExpressionsWithLiteralsCoCoChecker();
    coCoChecker.checkAll((ASTJavaClassExpressionsNode) optClass.get());
  }

  @Test
  public void testValid2() throws IOException{
    Optional<ASTClassExpression> optClass = p.parse_StringClassExpression("int.class");

    assertTrue(optClass.isPresent());

    CombineExpressionsWithLiteralsCoCoChecker coCoChecker = new CombineExpressionsWithLiteralsCoCoChecker().getCombineExpressionsWithLiteralsCoCoChecker();
    coCoChecker.checkAll((ASTJavaClassExpressionsNode) optClass.get());
  }

  @Test
  public void testInvalidGeneric() throws IOException{
    //MCListType
    Optional<ASTClassExpression> optClass = p.parse_StringClassExpression("List<String>.class");

    assertTrue(optClass.isPresent());

    CombineExpressionsWithLiteralsCoCoChecker coCoChecker = new CombineExpressionsWithLiteralsCoCoChecker().getCombineExpressionsWithLiteralsCoCoChecker();
    coCoChecker.checkAll((ASTJavaClassExpressionsNode) optClass.get());
    
    MCAssertions.assertHasFindingStartingWith(NoClassExpressionForGenerics.ERROR_CODE);
  }

  @Test
  public void testInvalidGeneric2() throws IOException{
    //MCBasicGenericType
    Optional<ASTClassExpression> optClass = p.parse_StringClassExpression("a.b.List<String>.class");

    assertTrue(optClass.isPresent());

    CombineExpressionsWithLiteralsCoCoChecker coCoChecker = new CombineExpressionsWithLiteralsCoCoChecker().getCombineExpressionsWithLiteralsCoCoChecker();
    coCoChecker.checkAll((ASTJavaClassExpressionsNode) optClass.get());
    
    MCAssertions.assertHasFindingStartingWith(NoClassExpressionForGenerics.ERROR_CODE);
  }

}
