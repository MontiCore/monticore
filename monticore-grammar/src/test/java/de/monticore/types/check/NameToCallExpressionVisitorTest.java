/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsArtifactScope;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsGlobalScope;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsScope;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.commonexpressions._visitor.CommonExpressionsTraverser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NameToCallExpressionVisitorTest {

  private CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();

  @Before
  public void setupLog() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  /**
   * test if the visitor transforms a call expression with inner name expression correctly
   */
  @Test
  public void nameTest() throws IOException {
    CombineExpressionsWithLiteralsTraverser traverser = CombineExpressionsWithLiteralsMill.traverser();
    NameToCallExpressionVisitor visitor = new NameToCallExpressionVisitor();
    traverser.setCommonExpressionsHandler(visitor);
    traverser.add4CommonExpressions(visitor);
    traverser.setExpressionsBasisHandler(visitor);
    traverser.add4ExpressionsBasis(visitor);
    Optional<ASTExpression> astex = p.parse_StringExpression("test()");
    astex.get().accept(traverser);
    assertEquals("test",visitor.getLastName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * test if the visitor transforms a call expression with inner field access expression correctly
   */
  @Test
  public void fieldAccessTest() throws IOException{
    FullDeriveFromCombineExpressionsWithLiterals derive = new FullDeriveFromCombineExpressionsWithLiterals();
    CombineExpressionsWithLiteralsTraverser traverser = CombineExpressionsWithLiteralsMill.traverser();
    NameToCallExpressionVisitor visitor = new NameToCallExpressionVisitor();
    visitor.setTypeCheckTraverser((CommonExpressionsTraverser) derive.getTraverser());
    visitor.setTypeCheckResult(derive.getTypeCheckResult());
    traverser.setCommonExpressionsHandler(visitor);
    traverser.add4CommonExpressions(visitor);
    traverser.setExpressionsBasisHandler(visitor);
    traverser.add4ExpressionsBasis(visitor);
    Optional<ASTExpression> astex = p.parse_StringExpression("a.b.test()");
    ASTExpression expr = ((ASTCallExpression) astex.get()).getExpression();
    expr.accept(getFlatExpressionScopeSetter(getFlatExpressionScope()));
    astex.get().accept(traverser);
    assertEquals("test", visitor.getLastName());
    assertEquals(((ASTFieldAccessExpression) expr).getExpression(), visitor.getLastExpression());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  protected ICombineExpressionsWithLiteralsScope getFlatExpressionScope() {
    // empty scope used to avoid NullPointerExceptions in the traverser
    ICombineExpressionsWithLiteralsGlobalScope gs = CombineExpressionsWithLiteralsMill.globalScope();
    ICombineExpressionsWithLiteralsArtifactScope as = CombineExpressionsWithLiteralsMill.artifactScope();
    as.setName("artifactScope");
    gs.addSubScope(as);
    return as;
  }

  protected CombineExpressionsWithLiteralsTraverser getFlatExpressionScopeSetter(
      IExpressionsBasisScope scope) {
    CombineExpressionsWithLiteralsTraverser traverser = CombineExpressionsWithLiteralsMill.traverser();
    FlatExpressionScopeSetter flatExpressionScopeSetter = new FlatExpressionScopeSetter(scope);
    traverser.add4ExpressionsBasis(flatExpressionScopeSetter);
    traverser.add4AssignmentExpressions(flatExpressionScopeSetter);
    traverser.add4CommonExpressions(flatExpressionScopeSetter);
    traverser.add4JavaClassExpressions(flatExpressionScopeSetter);
    traverser.add4BitExpressions(flatExpressionScopeSetter);
    traverser.add4MCBasicTypes(flatExpressionScopeSetter);
    traverser.add4MCCollectionTypes(flatExpressionScopeSetter);
    traverser.add4MCSimpleGenericTypes(flatExpressionScopeSetter);
    return traverser;
  }

}
