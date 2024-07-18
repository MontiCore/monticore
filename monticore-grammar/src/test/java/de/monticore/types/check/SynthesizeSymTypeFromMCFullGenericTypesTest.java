/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.CombineExpressionsWithLiteralsSymbols2Json;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsArtifactScope;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsGlobalScope;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.ASTMCVoidType;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class SynthesizeSymTypeFromMCFullGenericTypesTest {

  /**
   * Focus: Interplay between TypeCheck and the assisting visitors on the
   * extended configuration,
   * i.e. for
   *    types/MCFullGenericTypes.mc4
   */

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    BasicSymbolsMill.initializePrimitives();

    ICombineExpressionsWithLiteralsGlobalScope gs = CombineExpressionsWithLiteralsMill.globalScope();
    OOTypeSymbol a2 = DefsTypeBasic.type("A2", Lists.newArrayList(), Lists.newArrayList(), Lists.newArrayList(), Lists.newArrayList(DefsTypeBasic.typeVariable("T")), gs);
    gs.add(a2);
    a2.getSpannedScope().add(DefsTypeBasic.type("B", Lists.newArrayList(), Lists.newArrayList(), Lists.newArrayList(), Lists.newArrayList(), a2.getSpannedScope()));
    gs.add(DefsTypeBasic.type("A"));
    gs.add(DefsTypeBasic.type("Person"));
    gs.add(DefsTypeBasic.type("Auto"));
    gs.add(DefsTypeBasic.type("Map"));
    gs.add(DefsTypeBasic.type("List"));
    gs.add(DefsTypeBasic.type("Set"));
    gs.add(DefsTypeBasic.type("Iterator"));
    gs.add(DefsTypeBasic.type("Collection"));
    gs.add(DefsTypeBasic.type("Void"));

    CombineExpressionsWithLiteralsSymbols2Json symbols2Json = new CombineExpressionsWithLiteralsSymbols2Json();
    ICombineExpressionsWithLiteralsArtifactScope as = symbols2Json.load("src/test/resources/de/monticore/types/check/Persondex.cesym");
    as.setEnclosingScope(gs);

    ICombineExpressionsWithLiteralsArtifactScope as2 = symbols2Json.load("src/test/resources/de/monticore/types/check/Personaz.cesym");
    as2.setEnclosingScope(gs);

    ICombineExpressionsWithLiteralsArtifactScope as3 = symbols2Json.load("src/test/resources/de/monticore/types/check/Iterator.cesym");
    as3.setEnclosingScope(gs);

    ICombineExpressionsWithLiteralsArtifactScope as4 = symbols2Json.load("src/test/resources/de/monticore/types/check/String.cesym");
    as4.setEnclosingScope(gs);

    ICombineExpressionsWithLiteralsArtifactScope as5 = symbols2Json.load("src/test/resources/de/monticore/types/check/Personjl.cesym");
    as5.setEnclosingScope(gs);
  }

  // Parer used for convenience:
  CombineExpressionsWithLiteralsParser parser = new CombineExpressionsWithLiteralsParser();

  // This is Visitor for SimpleGeneric types under test:
  ISynthesize synt = new FullSynthesizeFromMCFullGenericTypes();

  // other arguments not used (and therefore deliberately null)

  // This is the TypeChecker under Test:
  TypeCalculator tc = new TypeCalculator(synt,null);

  FlatExpressionScopeSetter scopeSetter;
  CombineExpressionsWithLiteralsTraverser traverser;

  @BeforeEach
  public void initScope(){
    scopeSetter = new FlatExpressionScopeSetter(CombineExpressionsWithLiteralsMill.globalScope());
    traverser = CombineExpressionsWithLiteralsMill.traverser();
    traverser.add4MCFullGenericTypes(scopeSetter);
    traverser.add4MCSimpleGenericTypes(scopeSetter);
    traverser.add4MCCollectionTypes(scopeSetter);
    traverser.add4MCBasicTypes(scopeSetter);
  }

  // ------------------------------------------------------  Tests for Function 1, 1b, 1c

  // reuse some of the tests from MCBasicTypes (to check conformity)

  @Test
  public void symTypeFromAST_Test1() throws IOException {
    String s = "double";
    parser = new CombineExpressionsWithLiteralsParser();
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    asttype.accept(traverser);
    Assertions.assertEquals(s, tc.symTypeFromAST(asttype).printFullName());
  }

  @Test
  public void symTypeFromAST_Test4() throws IOException {
    String s = "Person";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    asttype.accept(traverser);
    Assertions.assertEquals(s, tc.symTypeFromAST(asttype).printFullName());
  }

  @Test
  public void symTypeFromAST_Test5() throws IOException {
    String s = "de.x.Person";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    asttype.accept(traverser);
    Assertions.assertEquals(s, tc.symTypeFromAST(asttype).printFullName());
  }

  @Test
  public void symTypeFromAST_ReturnTest() throws IOException {
    ASTMCVoidType v = MCBasicTypesMill.mCVoidTypeBuilder().build();
    ASTMCReturnType r = MCBasicTypesMill.mCReturnTypeBuilder()
      .setMCVoidType(v).build();
    Assertions.assertEquals("void", tc.symTypeFromAST(r).printFullName());
  }

  @Test
  public void symTypeFromAST_ReturnTest3() throws IOException {
    // und nochmal einen normalen Typ:
    String s = "Person";
    ASTMCReturnType r = parser.parse_StringMCReturnType(s).get();
    r.accept(traverser);
    Assertions.assertEquals(s, tc.symTypeFromAST(r).printFullName());
  }

  // reuse some of the tests from MCCollectionType

  @Test
  public void symTypeFromAST_TestListQual() throws IOException {
    String s = "List<a.z.Person>";
    ASTMCListType asttype = parser.parse_StringMCListType(s).get();
    asttype.accept(traverser);
    Assertions.assertEquals(s, tc.symTypeFromAST(asttype).printFullName());
  }

  @Test
  public void symTypeFromAST_TestListQual2() throws IOException {
    String s = "Set<Auto>";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    asttype.accept(traverser);
    Assertions.assertEquals(s, tc.symTypeFromAST(asttype).printFullName());
  }

  @Test
  public void symTypeFromAST_TestListQual3() throws IOException {
    String s = "Map<int,Auto>";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    asttype.accept(traverser);
    Assertions.assertEquals(s, tc.symTypeFromAST(asttype).printFullName());
  }

  @Test
  public void symTypeFromAST_TestListQual4() throws IOException {
    String s = "Set<int>";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    asttype.accept(traverser);
    Assertions.assertEquals(s, tc.symTypeFromAST(asttype).printFullName());
  }

  //new tests coming from MCSimpleGenericTypes

  @Test
  public void symTypeFromAST_TestGeneric() throws IOException {
    String s = "Iterator<Person>";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    asttype.accept(traverser);
    Assertions.assertEquals(s, tc.symTypeFromAST(asttype).printFullName());
  }

  @Test
  public void symTypeFromAST_TestGeneric2() throws IOException {
    String s = "java.util.Iterator<java.lang.String>";
    ASTMCBasicGenericType asttype = parser.parse_StringMCBasicGenericType(s).get();
    asttype.accept(traverser);
    Assertions.assertEquals(s, tc.symTypeFromAST(asttype).printFullName());
  }

  @Test
  public void symTypeFromAST_TestGeneric3() throws IOException {
    String s = "Collection<int>";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    asttype.accept(traverser);
    Assertions.assertEquals(s, tc.symTypeFromAST(asttype).printFullName());
  }

  @Test
  public void symTypeFromAST_TestGeneric4() throws IOException {
    String s = "java.util.Iterator<Void>";
    ASTMCBasicGenericType asttype = parser.parse_StringMCBasicGenericType(s).get();
    asttype.accept(traverser);
    Assertions.assertEquals(s, tc.symTypeFromAST(asttype).printFullName());
  }

  @Test
  public void symTypeFromAST_TestGeneric5() throws IOException {
    String s = "java.util.Iterator<java.lang.String,java.lang.Person,java.lang.String,int>";
    ASTMCBasicGenericType asttype = parser.parse_StringMCBasicGenericType(s).get();
    asttype.accept(traverser);
    Assertions.assertEquals(s, tc.symTypeFromAST(asttype).printFullName());
  }

  @Test
  public void symTypeFromAST_TestGeneric6() throws IOException {
    String s = "java.util.Iterator<java.util.Iterator<java.util.Iterator<int>>>";
    ASTMCBasicGenericType asttype = parser.parse_StringMCBasicGenericType(s).get();
    asttype.accept(traverser);
    Assertions.assertEquals(s, tc.symTypeFromAST(asttype).printFullName());
  }

  @Test
  public void symTypeFromAST_TestGeneric7() throws IOException {
    String s = "java.util.Iterator<List<java.util.Iterator<int>>>";
    ASTMCBasicGenericType asttype = parser.parse_StringMCBasicGenericType(s).get();
    asttype.accept(traverser);
    Assertions.assertEquals(s, tc.symTypeFromAST(asttype).printFullName());
  }

  @Test
  public void symTypeFromAST_TestGeneric8() throws IOException {
    String s = "List<? extends java.lang.String>";
    ASTMCBasicGenericType asttype = parser.parse_StringMCBasicGenericType(s).get();
    asttype.accept(traverser);
    Assertions.assertEquals(s, tc.symTypeFromAST(asttype).printFullName());
  }


}
