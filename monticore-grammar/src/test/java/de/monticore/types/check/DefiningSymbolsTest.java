/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsArtifactScope;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsGlobalScope;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsScope;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DefiningSymbolsTest {

  protected ICombineExpressionsWithLiteralsArtifactScope as;
  protected ICombineExpressionsWithLiteralsScope typeScope;
  protected CombineExpressionsWithLiteralsParser p;
  protected FullDeriveFromCombineExpressionsWithLiterals deriver;
  protected FullSynthesizeFromCombineExpressionsWithLiterals synthesizer;
  protected FieldSymbol e;
  protected MethodSymbol add;
  protected TypeSymbol listOfInt;
  
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);

    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    BasicSymbolsMill.initializePrimitives();

    p = CombineExpressionsWithLiteralsMill.parser();

    ICombineExpressionsWithLiteralsGlobalScope gs = CombineExpressionsWithLiteralsMill.globalScope();
    as = CombineExpressionsWithLiteralsMill.artifactScope();
    as.setName("ListOfInt");
    gs.addSubScope(as);

    e = DefsTypeBasic.field("e", SymTypeExpressionFactory.createPrimitive("int"));
    e.setIsStatic(true);
    add = DefsTypeBasic.method("add", new SymTypeVoid());
    add.setIsStatic(true);
    DefsTypeBasic.add(add, e);
    listOfInt = DefsTypeBasic.type("ListOfInt", Lists.newArrayList(add), Lists.newArrayList(e), Lists.newArrayList(), Lists.newArrayList(), as);
    as.add(listOfInt);
    typeScope = (ICombineExpressionsWithLiteralsScope) listOfInt.getSpannedScope();
    deriver = new FullDeriveFromCombineExpressionsWithLiterals();
    synthesizer = new FullSynthesizeFromCombineExpressionsWithLiterals();
  }

  @Test
  public void testQualified() throws IOException {
    Optional<ASTExpression> expr = p.parse_StringExpression("ListOfInt.e");
    Assertions.assertTrue(expr.isPresent());
    Assertions.assertTrue(expr.get() instanceof ASTFieldAccessExpression);
    ASTFieldAccessExpression e = (ASTFieldAccessExpression) expr.get();
    e.accept(getFlatExpressionScopeSetter(as));
    TypeCalculator tc = new TypeCalculator(null, deriver);
    tc.typeOf(e);
    Assertions.assertTrue(e.getDefiningSymbol().isPresent());
    ISymbol definingSymbol = e.getDefiningSymbol().get();
    Assertions.assertTrue(definingSymbol instanceof FieldSymbol);
    Assertions.assertEquals("e", definingSymbol.getName());

    Assertions.assertTrue(e.getExpression() instanceof ASTNameExpression);
    ASTNameExpression listOfInt = (ASTNameExpression) e.getExpression();
    Assertions.assertTrue(listOfInt.getDefiningSymbol().isPresent());
    Assertions.assertEquals(listOfInt.getDefiningSymbol().get(), this.listOfInt);

    expr = p.parse_StringExpression("ListOfInt.add(3)");
    Assertions.assertTrue(expr.isPresent());
    Assertions.assertTrue(expr.get() instanceof ASTCallExpression);
    ASTCallExpression add = (ASTCallExpression) expr.get();
    add.accept(getFlatExpressionScopeSetter(as));
    tc.typeOf(add);
    Assertions.assertTrue(add.getDefiningSymbol().isPresent());
    definingSymbol = add.getDefiningSymbol().get();
    Assertions.assertTrue(definingSymbol instanceof MethodSymbol);
    Assertions.assertEquals("add", definingSymbol.getName());

    Assertions.assertTrue(add.getExpression() instanceof ASTFieldAccessExpression);
    Assertions.assertTrue(((ASTFieldAccessExpression) add.getExpression()).getExpression() instanceof ASTNameExpression);
    listOfInt = (ASTNameExpression) ((ASTFieldAccessExpression) add.getExpression()).getExpression();
    Assertions.assertTrue(listOfInt.getDefiningSymbol().isPresent());
    Assertions.assertEquals(listOfInt.getDefiningSymbol().get(), this.listOfInt);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testUnqualified() throws IOException {
    Optional<ASTExpression> expr = p.parse_StringExpression("e");
    Assertions.assertTrue(expr.isPresent());
    Assertions.assertTrue(expr.get() instanceof ASTNameExpression);
    ASTNameExpression e = (ASTNameExpression) expr.get();
    e.accept(getFlatExpressionScopeSetter(typeScope));
    TypeCalculator tc = new TypeCalculator(null, deriver);
    tc.typeOf(e);
    Assertions.assertTrue(e.getDefiningSymbol().isPresent());
    ISymbol definingSymbol = e.getDefiningSymbol().get();
    Assertions.assertTrue(definingSymbol instanceof FieldSymbol);
    Assertions.assertEquals("e", definingSymbol.getName());

    expr = p.parse_StringExpression("add(3)");
    Assertions.assertTrue(expr.isPresent());
    Assertions.assertTrue(expr.get() instanceof ASTCallExpression);
    ASTCallExpression add = (ASTCallExpression) expr.get();
    add.accept(getFlatExpressionScopeSetter(typeScope));
    tc.typeOf(add);
    Assertions.assertTrue(add.getDefiningSymbol().isPresent());
    definingSymbol = add.getDefiningSymbol().get();
    Assertions.assertTrue(definingSymbol instanceof MethodSymbol);
    Assertions.assertEquals("add", definingSymbol.getName());

    expr = p.parse_StringExpression("ListOfInt");
    Assertions.assertTrue(expr.isPresent());
    Assertions.assertTrue(expr.get() instanceof ASTNameExpression);
    ASTNameExpression listOfInt = (ASTNameExpression) expr.get();
    listOfInt.accept(getFlatExpressionScopeSetter(as));
    tc.typeOf(listOfInt);
    Assertions.assertTrue(listOfInt.getDefiningSymbol().isPresent());
    Assertions.assertEquals(listOfInt.getDefiningSymbol().get(), this.listOfInt);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testTypes() throws IOException {
    Optional<TypeSymbol> type = as.resolveType("int");
    Assertions.assertTrue(type.isPresent());
    Optional<ASTMCType> intType = p.parse_StringMCType("int");
    Assertions.assertTrue(intType.isPresent());
    intType.get().accept(getFlatExpressionScopeSetter(as));
    TypeCalculator tc = new TypeCalculator(synthesizer, null);
    tc.symTypeFromAST(intType.get());
    Assertions.assertTrue(intType.get().getDefiningSymbol().isPresent());
    Assertions.assertEquals(intType.get().getDefiningSymbol().get(), type.get());

    Optional<ASTMCType> listOfIntType = p.parse_StringMCType("ListOfInt");
    Assertions.assertTrue(listOfIntType.isPresent());
    listOfIntType.get().accept(getFlatExpressionScopeSetter(as));
    tc.symTypeFromAST(listOfIntType.get());
    Assertions.assertTrue(listOfIntType.get().getDefiningSymbol().isPresent());
    Assertions.assertEquals(listOfIntType.get().getDefiningSymbol().get(), this.listOfInt);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  protected CombineExpressionsWithLiteralsTraverser getFlatExpressionScopeSetter(IExpressionsBasisScope scope){
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
