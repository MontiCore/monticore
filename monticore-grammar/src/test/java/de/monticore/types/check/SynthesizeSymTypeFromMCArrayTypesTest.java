/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.CombineExpressionsWithLiteralsSymbols2Json;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsArtifactScope;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsGlobalScope;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.mcarraytypes.MCArrayTypesMill;
import de.monticore.types.mcarraytypes._ast.ASTMCArrayType;
import de.monticore.types.mcarraytypes._visitor.MCArrayTypesTraverser;
import de.monticore.types.mcarraytypestest._parser.MCArrayTypesTestParser;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.ASTMCVoidType;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class SynthesizeSymTypeFromMCArrayTypesTest {

  /**
   * Focus: Interplay between TypeCheck and the assisting visitors on the
   * Basic configuration, i.e.
   * i.e. for
   *    expressions/ExpressionsBasis.mc4
   *    literals/MCLiteralsBasis.mc4
   *    types/MCBasicTypes.mc4
   *    types/MCArrayTypes.mc4
   */

  @BeforeClass
  public static void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    BasicSymbolsMill.initializePrimitives();
    init();
  }

  public static void init(){
    ICombineExpressionsWithLiteralsGlobalScope gs = CombineExpressionsWithLiteralsMill.globalScope();
    gs.add(DefsTypeBasic.type("A"));
    gs.add(DefsTypeBasic.type("Person"));

    CombineExpressionsWithLiteralsSymbols2Json symbols2Json = new CombineExpressionsWithLiteralsSymbols2Json();
    ICombineExpressionsWithLiteralsArtifactScope as = symbols2Json.load("src/test/resources/de/monticore/types/check/Persondex.cesym");
    as.setEnclosingScope(gs);
  }

  // Parer used for convenience:
  CombineExpressionsWithLiteralsParser parser = new CombineExpressionsWithLiteralsParser();
  // This is the TypeChecker under Test:
  TypeCalculator tc = new TypeCalculator(new FullSynthesizeFromMCArrayTypes(),null);

  FlatExpressionScopeSetter scopeSetter;
  CombineExpressionsWithLiteralsTraverser traverser;

  @Before
  public void initScope(){
    scopeSetter = new FlatExpressionScopeSetter(CombineExpressionsWithLiteralsMill.globalScope());
    traverser = CombineExpressionsWithLiteralsMill.traverser();
    traverser.add4MCBasicTypes(scopeSetter);
    traverser.add4MCArrayTypes(scopeSetter);
  }

  // ------------------------------------------------------  Tests for Function 1, 1b, 1c

  @Test
  public void symTypeFromAST_Test1() throws IOException {
    String s = "double";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    asttype.accept(traverser);
    assertEquals(s, tc.symTypeFromAST(asttype).printFullName());
  }

  @Test
  public void symTypeFromAST_Test2() throws IOException {
    String s = "int";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    asttype.accept(traverser);
    assertEquals(s, tc.symTypeFromAST(asttype).printFullName());
  }

  @Test
  public void symTypeFromAST_Test3() throws IOException {
    String s = "A";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    asttype.accept(traverser);
    assertEquals(s, tc.symTypeFromAST(asttype).printFullName());
  }

  @Test
  public void symTypeFromAST_Test4() throws IOException {
    String s = "Person";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    asttype.accept(traverser);
    assertEquals(s, tc.symTypeFromAST(asttype).printFullName());
  }

  @Test
  public void symTypeFromAST_Test5() throws IOException {
    String s = "de.x.Person";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    asttype.accept(traverser);
    assertEquals(s, tc.symTypeFromAST(asttype).printFullName());
  }

  @Test
  public void symTypeFromAST_VoidTest() throws IOException {
    ASTMCVoidType v = MCBasicTypesMill.mCVoidTypeBuilder().build();
    assertEquals("void", tc.symTypeFromAST(v).printFullName());
  }

  @Test
  public void symTypeFromAST_ReturnTest() throws IOException {
    ASTMCVoidType v = MCBasicTypesMill.mCVoidTypeBuilder().build();
    ASTMCReturnType r = MCBasicTypesMill.mCReturnTypeBuilder().setMCVoidType(v).build();
    assertEquals("void", tc.symTypeFromAST(r).printFullName());
  }

  @Test
  public void symTypeFromAST_ReturnTest2() throws IOException {
    // im Prinzip dassselbe via Parser:
    ASTMCReturnType r = parser.parse_StringMCReturnType("void").get();
    assertEquals("void", tc.symTypeFromAST(r).printFullName());
  }

  @Test
  public void symTypeFromAST_ReturnTest3() throws IOException {
    // und nochmal einen normalen Typ:
    String s = "Person";
    ASTMCReturnType r = parser.parse_StringMCReturnType(s).get();
    r.accept(traverser);
    assertEquals(s, tc.symTypeFromAST(r).printFullName());
  }

  @Test
  public void symTypeFrom_AST_ArrayTest() throws IOException {
    ASTMCType prim = parser.parse_StringMCType("int").get();
    ASTMCArrayType asttype = MCArrayTypesMill.mCArrayTypeBuilder().setMCType(prim).setDimensions(2).build();
    asttype.accept(traverser);
    assertEquals("int[][]", tc.symTypeFromAST(asttype).printFullName());
  }

  @Test
  public void symTypeFrom_AST_ArrayTest2() throws IOException {
    ASTMCType person = parser.parse_StringMCType("Person").get();
    ASTMCArrayType asttype = MCArrayTypesMill.mCArrayTypeBuilder().setMCType(person).setDimensions(1).build();
    asttype.accept(traverser);
    assertEquals("Person[]", tc.symTypeFromAST(asttype).printFullName());
  }

}