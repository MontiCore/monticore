/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsArtifactScope;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsGlobalScope;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsScope;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTQualifiedNameExpression;
import de.monticore.expressions.commonexpressions._ast.ASTStaticFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.FlatExpressionScopeSetter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types3.util.DefsTypesForTests;
import de.monticore.types3.util.PostTypeCheckNodeReplacer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static de.monticore.types3.util.DefsTypesForTests.inScope;
import static org.junit.jupiter.api.Assertions.*;

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
    assertInstanceOf(ASTQualifiedNameExpression.class, expr);
    generateScopes(expr);
    SymTypeExpression eType = TypeCheck3.typeOf(expr);

    // e
    assertEquals("int", eType.printFullName());
    assertTrue(eType.getSourceInfo().getSourceSymbol().isPresent());
    ISymbol definingSymbol = eType.getSourceInfo().getSourceSymbol().get();
    assertSame(this.e, definingSymbol);

    // ListOfInt
    ASTNode replacedExpr = replaceNodes(expr);
    assertInstanceOf(ASTStaticFieldAccessExpression.class, replacedExpr);
    ASTMCQualifiedType listOfInt =
        ((ASTStaticFieldAccessExpression) replacedExpr).getType();
    SymTypeExpression listOfIntType = getType4Ast().getPartialTypeOfTypeId(listOfInt);
    assertTrue(listOfIntType.getSourceInfo().getSourceSymbol().isPresent());
    definingSymbol = listOfIntType.getSourceInfo().getSourceSymbol().get();
    assertSame(this.listOfInt, definingSymbol);
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
    replaceNodes(add);
    assertInstanceOf(ASTStaticFieldAccessExpression.class, add.getExpression());
    ASTMCQualifiedType listOfIntType4Ast =
        ((ASTStaticFieldAccessExpression) add.getExpression()).getType();
    SymTypeExpression listOfIntType = getType4Ast().getPartialTypeOfTypeId(listOfIntType4Ast);
    assertTrue(listOfIntType.getSourceInfo().getSourceSymbol().isPresent());
    definingSymbol = listOfIntType.getSourceInfo().getSourceSymbol().get();
    assertEquals(this.listOfInt, definingSymbol);
  }

  @Test
  public void testWithinTypeField() throws IOException {
    ASTExpression expr = parseExpr("e");
    assertInstanceOf(ASTQualifiedNameExpression.class, expr);
    ASTQualifiedNameExpression eExpr = (ASTQualifiedNameExpression) expr;
    eExpr.accept(getFlatExpressionScopeSetter(typeScope));
    SymTypeExpression eType = TypeCheck3.typeOf(eExpr);

    assertTrue(eType.getSourceInfo().getSourceSymbol().isPresent());
    ISymbol definingSymbol = eType.getSourceInfo().getSourceSymbol().get();
    assertSame(this.e, definingSymbol);
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

  // Helper

  protected ASTNode replaceNodes(ASTExpression rootNode) {
    return PostTypeCheckNodeReplacer.replace(
        CombineExpressionsWithLiteralsMill::inheritanceTraverser,
        rootNode
    );
  }

}
