/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsArtifactScope;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsGlobalScope;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsScope;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.FlatExpressionScopeSetter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.util.DefsTypesForTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static de.monticore.types3.util.DefsTypesForTests.inScope;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * based on {@link de.monticore.types.check.DefiningSymbolsTest}.
 */
public class SourceSymbolsTest extends AbstractTypeVisitorTest {

  protected ICombineExpressionsWithLiteralsArtifactScope as;
  protected ICombineExpressionsWithLiteralsScope typeScope;
  protected FieldSymbol e;
  protected MethodSymbol add;
  protected TypeSymbol listOfInt;

  @BeforeEach
  public void init() {
    ICombineExpressionsWithLiteralsGlobalScope gs =
        CombineExpressionsWithLiteralsMill.globalScope();
    as = CombineExpressionsWithLiteralsMill.artifactScope();
    as.setName("ListOfInt");
    gs.addSubScope(as);

    // class ListOfInt {
    //   static int e;
    //   static void add(int p);
    // }
    e = DefsTypesForTests.field("e", SymTypeExpressionFactory.createPrimitive("int"));
    e.setIsStatic(true);
    VariableSymbol add_p = DefsTypesForTests.field("p", SymTypeExpressionFactory.createPrimitive("int"));
    add = DefsTypesForTests.method("add", SymTypeExpressionFactory.createTypeVoid());
    add.setIsStatic(true);
    inScope(add.getSpannedScope(), add_p);
    listOfInt = inScope(as, DefsTypesForTests.oOtype("ListOfInt", Collections.emptyList(), Collections.emptyList(), List.of(add), List.of(e)));
    typeScope = (ICombineExpressionsWithLiteralsScope) listOfInt.getSpannedScope();
  }

  @Test
  public void testQualifiedField() throws IOException {
    ASTExpression expr = parseExpr("ListOfInt.e");
    assertInstanceOf(ASTFieldAccessExpression.class, expr);
    ASTFieldAccessExpression e = (ASTFieldAccessExpression) expr;
    generateScopes(e);
    SymTypeExpression eType = TypeCheck3.typeOf(e);

    // e
    assertEquals("int", eType.printFullName());
    assertTrue(eType.getSourceInfo().getSourceSymbol().isPresent());
    ISymbol definingSymbol = eType.getSourceInfo().getSourceSymbol().get();
    assertSame(this.e, definingSymbol);

    // ListOfInt
    assertInstanceOf(ASTNameExpression.class, e.getExpression());
    ASTNameExpression listOfInt = (ASTNameExpression) e.getExpression();
    SymTypeExpression listOfIntType = getType4Ast().getPartialTypeOfTypeIdForName(listOfInt);
    assertTrue(listOfIntType.getSourceInfo().getSourceSymbol().isPresent());
    definingSymbol = listOfIntType.getSourceInfo().getSourceSymbol().get();
    assertSame(this.listOfInt, definingSymbol);

    assertNoFindings();
  }

  @Test
  public void testQualifiedMethod() throws IOException {
    ASTExpression expr = parseExpr("ListOfInt.add(3)");
    assertInstanceOf(ASTCallExpression.class, expr);
    ASTCallExpression add = (ASTCallExpression) expr;
    generateScopes(add);
    TypeCheck3.typeOf(add);

    // add
    SymTypeExpression addType = getType4Ast().getTypeOfExpression(add.getExpression());
    assertEquals("int -> void", addType.printFullName());
    assertTrue(addType.getSourceInfo().getSourceSymbol().isPresent());
    ISymbol definingSymbol = addType.getSourceInfo().getSourceSymbol().get();
    assertSame(this.add, definingSymbol);

    // ListOfInt
    assertInstanceOf(ASTFieldAccessExpression.class, add.getExpression());
    assertInstanceOf(ASTNameExpression.class, ((ASTFieldAccessExpression) add.getExpression()).getExpression());
    ASTNameExpression listOfIntExpr = (ASTNameExpression) ((ASTFieldAccessExpression) add.getExpression()).getExpression();
    SymTypeExpression listOfIntType = getType4Ast().getPartialTypeOfTypeIdForName(listOfIntExpr);
    assertTrue(listOfIntType.getSourceInfo().getSourceSymbol().isPresent());
    definingSymbol = listOfIntType.getSourceInfo().getSourceSymbol().get();
    assertEquals(this.listOfInt, definingSymbol);

    assertNoFindings();
  }

  @Test
  public void testWithinTypeField() throws IOException {
    ASTExpression expr = parseExpr("e");
    assertInstanceOf(ASTNameExpression.class, expr);
    ASTNameExpression eExpr = (ASTNameExpression) expr;
    eExpr.accept(getFlatExpressionScopeSetter(typeScope));
    SymTypeExpression eType = TypeCheck3.typeOf(eExpr);

    assertTrue(eType.getSourceInfo().getSourceSymbol().isPresent());
    ISymbol definingSymbol = eType.getSourceInfo().getSourceSymbol().get();
    assertSame(this.e, definingSymbol);

    assertNoFindings();
  }

  @Test
  public void testWithinTypeMethod() throws IOException {
    ASTExpression expr = parseExpr("add(3)");
    assertInstanceOf(ASTCallExpression.class, expr);
    ASTCallExpression addExpr = (ASTCallExpression) expr;
    addExpr.accept(getFlatExpressionScopeSetter(typeScope));
    TypeCheck3.typeOf(addExpr);

    SymTypeExpression addType = getType4Ast().getTypeOfExpression(addExpr.getExpression());
    assertEquals("int -> void", addType.printFullName());
    assertTrue(addType.getSourceInfo().getSourceSymbol().isPresent());
    ISymbol definingSymbol = addType.getSourceInfo().getSourceSymbol().get();
    assertSame(this.add, definingSymbol);

    assertNoFindings();
  }

  protected CombineExpressionsWithLiteralsTraverser getFlatExpressionScopeSetter(IExpressionsBasisScope scope) {
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
    traverser.add4MCCommonLiterals(flatExpressionScopeSetter);
    return traverser;
  }

}
