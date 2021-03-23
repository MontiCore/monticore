/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsArtifactScope;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsGlobalScope;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsScope;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.ASTMCVoidType;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypestest._parser.MCSimpleGenericTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SynthesizeSymTypeFromMCSimpleGenericTypesTest {
  
  /**
   * Focus: Interplay between TypeCheck and the assisting visitors on the
   * extended configuration,
   * i.e. for
   *    types/MCSimpleGenericTypes.mc4
   */
  
  @Before
  public void setup() {
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
    gs.add(DefsTypeBasic.type("Auto"));
    gs.add(DefsTypeBasic.type("Map"));
    gs.add(DefsTypeBasic.type("List"));
    gs.add(DefsTypeBasic.type("Set"));
    gs.add(DefsTypeBasic.type("Iterator"));
    gs.add(DefsTypeBasic.type("Collection"));
    gs.add(DefsTypeBasic.type("Void"));

    ICombineExpressionsWithLiteralsArtifactScope dex = CombineExpressionsWithLiteralsMill.artifactScope();
    dex.setPackageName("de.x");
    dex.setEnclosingScope(gs);
    dex.add(DefsTypeBasic.type("Person"));

    ICombineExpressionsWithLiteralsArtifactScope az = CombineExpressionsWithLiteralsMill.artifactScope();
    az.setPackageName("a.z");
    az.setEnclosingScope(gs);
    az.add(DefsTypeBasic.type("Person"));

    ICombineExpressionsWithLiteralsArtifactScope javautil = CombineExpressionsWithLiteralsMill.artifactScope();
    javautil.setPackageName("java.util");
    javautil.setEnclosingScope(gs);
    javautil.add(DefsTypeBasic.type("Iterator"));

    ICombineExpressionsWithLiteralsArtifactScope javalang = CombineExpressionsWithLiteralsMill.artifactScope();
    javalang.setPackageName("java.lang");
    javalang.setEnclosingScope(gs);
    javalang.add(DefsTypeBasic.type("String"));
    javalang.add(DefsTypeBasic.type("Person"));
  }
  
  // Parer used for convenience:
  MCSimpleGenericTypesTestParser parser = new MCSimpleGenericTypesTestParser();
  
  // This is Visitor for SimpleGeneric types under test:
  SynthesizeSymTypeFromCombineExpressionsWithLiteralsDelegator synt = new SynthesizeSymTypeFromCombineExpressionsWithLiteralsDelegator();
  
  // other arguments not used (and therefore deliberately null)
  
  // This is the TypeChecker under Test:
  TypeCheck tc = new TypeCheck(synt,null);

  FlatExpressionScopeSetter scopeSetter;
  CombineExpressionsWithLiteralsTraverser traverser;

  @Before
  public void initScope(){
    scopeSetter = new FlatExpressionScopeSetter(CombineExpressionsWithLiteralsMill.globalScope());
    traverser = CombineExpressionsWithLiteralsMill.traverser();
    traverser.add4MCSimpleGenericTypes(scopeSetter);
    traverser.add4MCCollectionTypes(scopeSetter);
    traverser.add4MCBasicTypes(scopeSetter);
  }
  
  // ------------------------------------------------------  Tests for Function 1, 1b, 1c

  // reuse some of the tests from MCBasicTypes (to check conformity)
  
  @Test
  public void symTypeFromAST_Test1() throws IOException {
    String s = "double";
    parser = new MCSimpleGenericTypesTestParser();
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
    asttype.setEnclosingScope(CombineExpressionsWithLiteralsMill.globalScope().getSubScopes().get(0));
    assertEquals(s, tc.symTypeFromAST(asttype).printFullName());
  }
  
  @Test
  public void symTypeFromAST_ReturnTest() throws IOException {
    ASTMCVoidType v = MCBasicTypesMill.mCVoidTypeBuilder().build();
    ASTMCReturnType r = MCBasicTypesMill.mCReturnTypeBuilder()
                                  .setMCVoidType(v).build();
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

  // reuse some of the tests from MCCollectionType
  
  @Test
  public void symTypeFromAST_TestListQual() throws IOException {
    ICombineExpressionsWithLiteralsScope az =
        CombineExpressionsWithLiteralsMill.globalScope().getSubScopes().get(1);
    String s = "List<a.z.Person>";
    ASTMCListType asttype = parser.parse_StringMCListType(s).get();
    asttype.getMCTypeArgument().setEnclosingScope(az);
    asttype.getMCTypeArgument().getMCTypeOpt().get().setEnclosingScope(az);
    assertEquals(s, tc.symTypeFromAST(asttype).printFullName());
  }
  
  @Test
  public void symTypeFromAST_TestListQual2() throws IOException {
    String s = "Set<Auto>";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    asttype.accept(traverser);
    assertEquals(s, tc.symTypeFromAST(asttype).printFullName());
  }
  
  @Test
  public void symTypeFromAST_TestListQual3() throws IOException {
    String s = "Map<int,Auto>";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    asttype.accept(traverser);
    assertEquals(s, tc.symTypeFromAST(asttype).printFullName());
  }
  
  @Test
  public void symTypeFromAST_TestListQual4() throws IOException {
    String s = "Set<int>";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    asttype.accept(traverser);
    assertEquals(s, tc.symTypeFromAST(asttype).printFullName());
  }
  
  //new tests coming from MCSimpleGenericTypes

  @Test
  public void symTypeFromAST_TestGeneric() throws IOException {
    String s = "Iterator<Person>";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    asttype.accept(traverser);
    assertEquals(s, tc.symTypeFromAST(asttype).printFullName());
  }

  @Test
  public void symTypeFromAST_TestGeneric2() throws IOException {
    ICombineExpressionsWithLiteralsScope javautil = CombineExpressionsWithLiteralsMill.globalScope().getSubScopes().get(2);
    ICombineExpressionsWithLiteralsScope javalang = CombineExpressionsWithLiteralsMill.globalScope().getSubScopes().get(3);
    String s = "java.util.Iterator<java.lang.String>";
    ASTMCBasicGenericType asttype = parser.parse_StringMCBasicGenericType(s).get();
    asttype.setEnclosingScope(javautil);
    asttype.getMCTypeArgument(0).setEnclosingScope(javalang);
    asttype.getMCTypeArgument(0).getMCTypeOpt().get().setEnclosingScope(javalang);
    assertEquals(s, tc.symTypeFromAST(asttype).printFullName());
  }

  @Test
  public void symTypeFromAST_TestGeneric3() throws IOException {
    String s = "Collection<int>";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    asttype.accept(traverser);
    assertEquals(s, tc.symTypeFromAST(asttype).printFullName());
  }

  @Test
  public void symTypeFromAST_TestGeneric4() throws IOException {
    ICombineExpressionsWithLiteralsScope gs = CombineExpressionsWithLiteralsMill.globalScope();
    ICombineExpressionsWithLiteralsScope javautil = gs.getSubScopes().get(2);
    String s = "java.util.Iterator<Void>";
    ASTMCBasicGenericType asttype = parser.parse_StringMCBasicGenericType(s).get();
    asttype.setEnclosingScope(javautil);
    asttype.getMCTypeArgument(0).setEnclosingScope(gs);
    asttype.getMCTypeArgument(0).getMCTypeOpt().get().setEnclosingScope(gs);
    assertEquals(s, tc.symTypeFromAST(asttype).printFullName());
  }

  @Test
  public void symTypeFromAST_TestGeneric5() throws IOException {
    ICombineExpressionsWithLiteralsScope gs = CombineExpressionsWithLiteralsMill.globalScope();
    ICombineExpressionsWithLiteralsScope javautil = gs.getSubScopes().get(2);
    ICombineExpressionsWithLiteralsScope javalang = gs.getSubScopes().get(3);
    String s = "java.util.Iterator<java.lang.String,java.lang.Person,java.lang.String,int>";
    ASTMCBasicGenericType asttype = parser.parse_StringMCBasicGenericType(s).get();
    asttype.setEnclosingScope(javautil);
    asttype.getMCTypeArgument(0).setEnclosingScope(javalang);
    asttype.getMCTypeArgument(0).getMCTypeOpt().get().setEnclosingScope(javalang);
    asttype.getMCTypeArgument(1).setEnclosingScope(javalang);
    asttype.getMCTypeArgument(1).getMCTypeOpt().get().setEnclosingScope(javalang);
    asttype.getMCTypeArgument(2).setEnclosingScope(javalang);
    asttype.getMCTypeArgument(2).getMCTypeOpt().get().setEnclosingScope(javalang);
    asttype.getMCTypeArgument(3).setEnclosingScope(gs);
    asttype.getMCTypeArgument(3).getMCTypeOpt().get().setEnclosingScope(gs);
    assertEquals(s, tc.symTypeFromAST(asttype).printFullName());
  }

  @Test
  public void symTypeFromAST_TestGeneric6() throws IOException {
    ICombineExpressionsWithLiteralsScope gs = CombineExpressionsWithLiteralsMill.globalScope();
    ICombineExpressionsWithLiteralsScope javautil = gs.getSubScopes().get(2);
    String s = "java.util.Iterator<java.util.Iterator<java.util.Iterator<int>>>";
    ASTMCBasicGenericType asttype = parser.parse_StringMCBasicGenericType(s).get();
    asttype.setEnclosingScope(javautil);
    asttype.getMCTypeArgument(0).setEnclosingScope(javautil);
    asttype.getMCTypeArgument(0).getMCTypeOpt().get().setEnclosingScope(javautil);
    assertTrue(asttype.getMCTypeArgument(0).getMCTypeOpt().get() instanceof ASTMCBasicGenericType);
    ASTMCBasicGenericType first = (ASTMCBasicGenericType) asttype.getMCTypeArgument(0).getMCTypeOpt().get();
    first.getMCTypeArgument(0).setEnclosingScope(javautil);
    first.getMCTypeArgument(0).getMCTypeOpt().get().setEnclosingScope(javautil);
    assertTrue(first.getMCTypeArgument(0).getMCTypeOpt().get() instanceof ASTMCBasicGenericType);
    ASTMCBasicGenericType second = (ASTMCBasicGenericType) first.getMCTypeArgument(0).getMCTypeOpt().get();
    second.getMCTypeArgument(0).setEnclosingScope(gs);
    second.getMCTypeArgument(0).getMCTypeOpt().get().setEnclosingScope(gs);
    assertEquals(s, tc.symTypeFromAST(asttype).printFullName());
  }

  @Test
  public void symTypeFromAST_TestGeneric7() throws IOException {
    ICombineExpressionsWithLiteralsScope gs = CombineExpressionsWithLiteralsMill.globalScope();
    ICombineExpressionsWithLiteralsScope javautil = gs.getSubScopes().get(2);
    String s = "java.util.Iterator<List<java.util.Iterator<int>>>";
    ASTMCBasicGenericType asttype = parser.parse_StringMCBasicGenericType(s).get();
    asttype.setEnclosingScope(javautil);
    asttype.getMCTypeArgument(0).setEnclosingScope(gs);
    asttype.getMCTypeArgument(0).getMCTypeOpt().get().setEnclosingScope(gs);
    assertTrue(asttype.getMCTypeArgument(0).getMCTypeOpt().get() instanceof ASTMCBasicGenericType);
    ASTMCBasicGenericType first = (ASTMCBasicGenericType) asttype.getMCTypeArgument(0).getMCTypeOpt().get();
    first.getMCTypeArgument(0).setEnclosingScope(javautil);
    first.getMCTypeArgument(0).getMCTypeOpt().get().setEnclosingScope(javautil);
    assertTrue(first.getMCTypeArgument(0).getMCTypeOpt().get() instanceof ASTMCBasicGenericType);
    ASTMCBasicGenericType second = (ASTMCBasicGenericType) first.getMCTypeArgument(0).getMCTypeOpt().get();
    second.getMCTypeArgument(0).setEnclosingScope(gs);
    second.getMCTypeArgument(0).getMCTypeOpt().get().setEnclosingScope(gs);
    assertEquals(s, tc.symTypeFromAST(asttype).printFullName());
  }

}
