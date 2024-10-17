/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.combineexpressionswithliterals._cocos;

import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.javaclassexpressions._ast.ASTClassExpression;
import de.monticore.expressions.javaclassexpressions._ast.ASTJavaClassExpressionsNode;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

public class TestNoClassExpressionForGenerics {

  CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();

  @BeforeEach
  public void setup(){
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testValid() throws IOException {
    Optional<ASTClassExpression> optClass = p.parse_StringClassExpression("Integer.class");

    Assertions.assertTrue(optClass.isPresent());

    CombineExpressionsWithLiteralsCoCoChecker coCoChecker = new CombineExpressionsWithLiteralsCoCoChecker().getCombineExpressionsWithLiteralsCoCoChecker();
    coCoChecker.checkAll((ASTJavaClassExpressionsNode) optClass.get());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testValid2() throws IOException{
    Optional<ASTClassExpression> optClass = p.parse_StringClassExpression("int.class");

    Assertions.assertTrue(optClass.isPresent());

    CombineExpressionsWithLiteralsCoCoChecker coCoChecker = new CombineExpressionsWithLiteralsCoCoChecker().getCombineExpressionsWithLiteralsCoCoChecker();
    coCoChecker.checkAll((ASTJavaClassExpressionsNode) optClass.get());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInvalidGeneric() throws IOException{
    //MCListType
    Optional<ASTClassExpression> optClass = p.parse_StringClassExpression("List<String>.class");

    Assertions.assertTrue(optClass.isPresent());

    CombineExpressionsWithLiteralsCoCoChecker coCoChecker = new CombineExpressionsWithLiteralsCoCoChecker().getCombineExpressionsWithLiteralsCoCoChecker();
    coCoChecker.checkAll((ASTJavaClassExpressionsNode) optClass.get());
    Assertions.assertFalse(Log.getFindings().isEmpty());
    Assertions.assertTrue(Log.getFindings().get(Log.getFindings().size()-1).getMsg().startsWith(NoClassExpressionForGenerics.ERROR_CODE));
  }

  @Test
  public void testInvalidGeneric2() throws IOException{
    //MCBasicGenericType
    Optional<ASTClassExpression> optClass = p.parse_StringClassExpression("a.b.List<String>.class");

    Assertions.assertTrue(optClass.isPresent());

    CombineExpressionsWithLiteralsCoCoChecker coCoChecker = new CombineExpressionsWithLiteralsCoCoChecker().getCombineExpressionsWithLiteralsCoCoChecker();
    coCoChecker.checkAll((ASTJavaClassExpressionsNode) optClass.get());
    Assertions.assertFalse(Log.getFindings().isEmpty());
    Assertions.assertTrue(Log.getFindings().get(Log.getFindings().size()-1).getMsg().startsWith(NoClassExpressionForGenerics.ERROR_CODE));
  }

}
