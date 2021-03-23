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
import de.monticore.types.mccollectiontypestest._parser.MCCollectionTypesTestParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class SynthesizeSymTypeFromMCCollectionTypesTest {
  
  /**
   * Focus: Interplay between TypeCheck and the assisting visitors on the
   * extended configuration,
   * i.e. for
   *    types/MCCollectionTypes.mc4
   */
  
  @Before
  public void setup() {
    LogStub.init();
    Log.enableFailQuick(false);
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    BasicSymbolsMill.initializePrimitives();
  }
  
  // Parer used for convenience:
  MCCollectionTypesTestParser parser = new MCCollectionTypesTestParser();
  
  // This is Visitor for Collection types under test:
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

    ICombineExpressionsWithLiteralsArtifactScope dex = CombineExpressionsWithLiteralsMill.artifactScope();
    dex.setPackageName("de.x");
    dex.setEnclosingScope(gs);
    dex.add(DefsTypeBasic.type("Person"));

    ICombineExpressionsWithLiteralsArtifactScope az = CombineExpressionsWithLiteralsMill.artifactScope();
    az.setPackageName("a.z");
    az.setEnclosingScope(gs);
    az.add(DefsTypeBasic.type("Person"));
  }
  
  // ------------------------------------------------------  Tests for Function 1, 1b, 1c

  // reuse some of the tests from MCBasicTypes (to check conformity)
  
  @Test
  public void symTypeFromAST_Test1() throws IOException {
    String s = "double";
    parser = new MCCollectionTypesTestParser();
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

  // new forms of Types coming from MCCollectionType
  
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
  
  
  
}
