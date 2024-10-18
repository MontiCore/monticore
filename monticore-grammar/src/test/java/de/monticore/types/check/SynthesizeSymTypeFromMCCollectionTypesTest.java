/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.CombineExpressionsWithLiteralsSymbols2Json;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsArtifactScope;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsGlobalScope;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolSurrogate;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.ASTMCVoidType;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SynthesizeSymTypeFromMCCollectionTypesTest {
  
  /**
   * Focus: Interplay between TypeCheck and the assisting visitors on the
   * extended configuration,
   * i.e. for
   *    types/MCCollectionTypes.mc4
   */

  
  // Parer used for convenience:
  CombineExpressionsWithLiteralsParser parser = new CombineExpressionsWithLiteralsParser();
  
  // This is Visitor for Collection types under test:
  FullSynthesizeFromMCCollectionTypes synt = new FullSynthesizeFromMCCollectionTypes();
  
  // other arguments not used (and therefore deliberately null)
  
  // This is the TypeChecker under Test:
  TypeCalculator tc = new TypeCalculator(synt,null);

  FlatExpressionScopeSetter scopeSetter;
  CombineExpressionsWithLiteralsTraverser traverser;

  @BeforeEach
  public void init(){

    LogStub.init();
    Log.enableFailQuick(false);
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    BasicSymbolsMill.initializePrimitives();

    scopeSetter = new FlatExpressionScopeSetter(CombineExpressionsWithLiteralsMill.globalScope());
    traverser = CombineExpressionsWithLiteralsMill.traverser();
    traverser.add4MCCollectionTypes(scopeSetter);
    traverser.add4MCBasicTypes(scopeSetter);

    ICombineExpressionsWithLiteralsGlobalScope gs = CombineExpressionsWithLiteralsMill.globalScope();
    gs.add(DefsTypeBasic.type("A"));
    gs.add(DefsTypeBasic.type("Person"));
    gs.add(DefsTypeBasic.type("Auto"));
    gs.add(buildGeneric("Map", "K", "V"));
    gs.add(buildGeneric("List", "T"));
    gs.add(buildGeneric("Set", "T"));
    gs.add(buildGeneric("Optional", "T"));

    CombineExpressionsWithLiteralsSymbols2Json symbols2Json = new CombineExpressionsWithLiteralsSymbols2Json();
    ICombineExpressionsWithLiteralsArtifactScope as = symbols2Json.load("src/test/resources/de/monticore/types/check/Persondex.cesym");
    as.setEnclosingScope(gs);

    ICombineExpressionsWithLiteralsArtifactScope as2 = symbols2Json.load("src/test/resources/de/monticore/types/check/Personaz.cesym");
    as2.setEnclosingScope(gs);
  }

  protected static TypeSymbol buildGeneric(String rawName, String... typeParamNames) {
    List<TypeVarSymbol> typeParams = Arrays.stream(typeParamNames)
      .map(DefsTypeBasic::typeVariable)
      .collect(Collectors.toList());

    return DefsTypeBasic.type(rawName, new ArrayList<>(), typeParams);
  }
  
  // ------------------------------------------------------  Tests for Function 1, 1b, 1c

  // reuse some of the tests from MCBasicTypes (to check conformity)
  
  @Test
  public void symTypeFromAST_Test1() throws IOException {
    String s = "double";
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

  // new forms of Types coming from MCCollectionType
  
  @Test
  public void symTypeFromAST_TestListQual() throws IOException {
    // Given
    String s = "List<a.z.Person>";
    ASTMCListType asttype = parser.parse_StringMCListType(s).get();

    // When
    asttype.accept(traverser);
    SymTypeExpression result = tc.symTypeFromAST(asttype);

    // Then
    Assertions.assertEquals(s, result.printFullName());
    Assertions.assertTrue(result instanceof SymTypeOfGenerics);
    Assertions.assertFalse(result.getTypeInfo() instanceof TypeSymbolSurrogate);
    Assertions.assertFalse(result.getTypeInfo() instanceof OOTypeSymbolSurrogate);
    Assertions.assertFalse(((SymTypeOfGenerics) result).getArgument(0).getTypeInfo() instanceof TypeSymbolSurrogate);
    Assertions.assertFalse(((SymTypeOfGenerics) result).getArgument(0).getTypeInfo() instanceof OOTypeSymbolSurrogate);

  }
  
  @Test
  public void symTypeFromAST_TestListQual2() throws IOException {
    // Given
    String s = "Set<Auto>";
    ASTMCType asttype = parser.parse_StringMCType(s).get();

    // When
    asttype.accept(traverser);
    SymTypeExpression result = tc.symTypeFromAST(asttype);

    // Then
    Assertions.assertEquals(s, result.printFullName());
    Assertions.assertTrue(result instanceof SymTypeOfGenerics);
    Assertions.assertFalse(result.getTypeInfo() instanceof TypeSymbolSurrogate);
    Assertions.assertFalse(result.getTypeInfo() instanceof OOTypeSymbolSurrogate);
    Assertions.assertFalse(((SymTypeOfGenerics) result).getArgument(0).getTypeInfo() instanceof TypeSymbolSurrogate);
    Assertions.assertFalse(((SymTypeOfGenerics) result).getArgument(0).getTypeInfo() instanceof OOTypeSymbolSurrogate);
  }
  
  @Test
  public void symTypeFromAST_TestListQual3() throws IOException {
    // Given
    String s = "Map<int,Auto>";
    ASTMCType asttype = parser.parse_StringMCType(s).get();

    // When
    asttype.accept(traverser);
    SymTypeExpression result = tc.symTypeFromAST(asttype);

    // Then
    Assertions.assertEquals(s, result.printFullName());
    Assertions.assertTrue(result instanceof SymTypeOfGenerics);
    Assertions.assertFalse(result.getTypeInfo() instanceof TypeSymbolSurrogate);
    Assertions.assertFalse(result.getTypeInfo() instanceof OOTypeSymbolSurrogate);
    Assertions.assertFalse(((SymTypeOfGenerics) result).getArgument(0).getTypeInfo() instanceof TypeSymbolSurrogate);
    Assertions.assertFalse(((SymTypeOfGenerics) result).getArgument(0).getTypeInfo() instanceof TypeSymbolSurrogate);
    Assertions.assertFalse(((SymTypeOfGenerics) result).getArgument(1).getTypeInfo() instanceof OOTypeSymbolSurrogate);
    Assertions.assertFalse(((SymTypeOfGenerics) result).getArgument(1).getTypeInfo() instanceof OOTypeSymbolSurrogate);
  }
  
  @Test
  public void symTypeFromAST_TestListQual4() throws IOException {
    // Given
    String s = "Set<int>";
    ASTMCType asttype = parser.parse_StringMCType(s).get();

    // When
    asttype.accept(traverser);
    SymTypeExpression result = tc.symTypeFromAST(asttype);

    // Then
    Assertions.assertEquals(s, result.printFullName());
    Assertions.assertTrue(result instanceof SymTypeOfGenerics);
    Assertions.assertFalse(result.getTypeInfo() instanceof TypeSymbolSurrogate);
    Assertions.assertFalse(result.getTypeInfo() instanceof OOTypeSymbolSurrogate);
    Assertions.assertFalse(((SymTypeOfGenerics) result).getArgument(0).getTypeInfo() instanceof TypeSymbolSurrogate);
    Assertions.assertFalse(((SymTypeOfGenerics) result).getArgument(0).getTypeInfo() instanceof OOTypeSymbolSurrogate);
  }

  @Test
  public void symTypeFromAST_TestListQual5() throws IOException {
    // Given
    String s = "Optional<int>";
    ASTMCType asttype = parser.parse_StringMCType(s).get();

    // When
    asttype.accept(traverser);
    SymTypeExpression result = tc.symTypeFromAST(asttype);

    // Then
    Assertions.assertEquals(s, result.printFullName());
    Assertions.assertTrue(result instanceof SymTypeOfGenerics);
    Assertions.assertFalse(result.getTypeInfo() instanceof TypeSymbolSurrogate);
    Assertions.assertFalse(result.getTypeInfo() instanceof OOTypeSymbolSurrogate);
    Assertions.assertFalse(((SymTypeOfGenerics) result).getArgument(0).getTypeInfo() instanceof TypeSymbolSurrogate);
    Assertions.assertFalse(((SymTypeOfGenerics) result).getArgument(0).getTypeInfo() instanceof OOTypeSymbolSurrogate);
  }
  
}
