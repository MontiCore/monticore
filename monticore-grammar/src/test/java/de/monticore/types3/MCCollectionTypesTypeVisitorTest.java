/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsArtifactScope;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsGlobalScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbolSurrogate;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static de.monticore.types3.util.DefsTypesForTests.inScope;
import static de.monticore.types3.util.DefsTypesForTests.type;

public class MCCollectionTypesTypeVisitorTest
    extends AbstractTypeVisitorTest {

  @BeforeEach
  public void initFurtherTypes() {
    ICombineExpressionsWithLiteralsGlobalScope gs =
        CombineExpressionsWithLiteralsMill.globalScope();
    // add two further Person Types within different packages
    ICombineExpressionsWithLiteralsArtifactScope azScope =
        inScope(gs, CombineExpressionsWithLiteralsMill.artifactScope());
    azScope.setPackageName("a.z");
    inScope(azScope, type("Person"));
    ICombineExpressionsWithLiteralsArtifactScope dexScope =
        inScope(gs, CombineExpressionsWithLiteralsMill.artifactScope());
    dexScope.setPackageName("de.x");
    inScope(dexScope, type("Person"));
  }

  @Test
  public void symTypeFromAST_TestList1() throws IOException {
    // Given
    String s = "List<a.z.Person>";
    ASTMCListType asttype = parser.parse_StringMCListType(s).get();
    generateScopes(asttype);

    // When
    SymTypeExpression result = TypeCheck3.symTypeFromAST(asttype);

    // Then
    assertNoFindings();
    Assertions.assertEquals(s, result.printFullName());
    Assertions.assertTrue(result instanceof SymTypeOfGenerics);
    Assertions.assertFalse(result.getTypeInfo() instanceof TypeSymbolSurrogate);
    Assertions.assertFalse(result.getTypeInfo() instanceof OOTypeSymbolSurrogate);
    Assertions.assertFalse(((SymTypeOfGenerics) result).getArgument(0)
        .getTypeInfo() instanceof TypeSymbolSurrogate);
    Assertions.assertFalse(((SymTypeOfGenerics) result).getArgument(0)
        .getTypeInfo() instanceof OOTypeSymbolSurrogate);

  }

  @Test
  public void symTypeFromAST_TestSet1() throws IOException {
    // Given
    String s = "Set<Student>";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    generateScopes(asttype);

    // When
    SymTypeExpression result = TypeCheck3.symTypeFromAST(asttype);

    // Then
    assertNoFindings();
    Assertions.assertEquals(s, result.printFullName());
    Assertions.assertTrue(result instanceof SymTypeOfGenerics);
    Assertions.assertFalse(result.getTypeInfo() instanceof TypeSymbolSurrogate);
    Assertions.assertFalse(result.getTypeInfo() instanceof OOTypeSymbolSurrogate);
    Assertions.assertFalse(((SymTypeOfGenerics) result).getArgument(0)
        .getTypeInfo() instanceof TypeSymbolSurrogate);
    Assertions.assertFalse(((SymTypeOfGenerics) result).getArgument(0)
        .getTypeInfo() instanceof OOTypeSymbolSurrogate);
  }

  @Test
  public void symTypeFromAST_TestMap1() throws IOException {
    // Given
    String s = "Map<int,Student>";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    generateScopes(asttype);

    // When
    SymTypeExpression result = TypeCheck3.symTypeFromAST(asttype);

    // Then
    assertNoFindings();
    Assertions.assertEquals(s, result.printFullName());
    Assertions.assertTrue(result instanceof SymTypeOfGenerics);
    Assertions.assertFalse(result.getTypeInfo() instanceof TypeSymbolSurrogate);
    Assertions.assertFalse(result.getTypeInfo() instanceof OOTypeSymbolSurrogate);
    Assertions.assertFalse(((SymTypeOfGenerics) result).getArgument(0)
        .getTypeInfo() instanceof TypeSymbolSurrogate);
    Assertions.assertFalse(((SymTypeOfGenerics) result).getArgument(0)
        .getTypeInfo() instanceof TypeSymbolSurrogate);
    Assertions.assertFalse(((SymTypeOfGenerics) result).getArgument(1)
        .getTypeInfo() instanceof OOTypeSymbolSurrogate);
    Assertions.assertFalse(((SymTypeOfGenerics) result).getArgument(1)
        .getTypeInfo() instanceof OOTypeSymbolSurrogate);
  }

  @Test
  public void symTypeFromAST_TestSet2() throws IOException {
    // Given
    String s = "Set<int>";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    generateScopes(asttype);

    // When
    SymTypeExpression result = TypeCheck3.symTypeFromAST(asttype);

    // Then
    assertNoFindings();
    Assertions.assertEquals(s, result.printFullName());
    Assertions.assertTrue(result instanceof SymTypeOfGenerics);
    Assertions.assertFalse(result.getTypeInfo() instanceof TypeSymbolSurrogate);
    Assertions.assertFalse(result.getTypeInfo() instanceof OOTypeSymbolSurrogate);
    Assertions.assertFalse(((SymTypeOfGenerics) result).getArgument(0)
        .getTypeInfo() instanceof TypeSymbolSurrogate);
    Assertions.assertFalse(((SymTypeOfGenerics) result).getArgument(0)
        .getTypeInfo() instanceof OOTypeSymbolSurrogate);
  }

  @Test
  public void symTypeFromAST_TestOptional1() throws IOException {
    // Given
    String s = "Optional<int>";
    ASTMCType asttype = parser.parse_StringMCType(s).get();
    generateScopes(asttype);

    // When
    SymTypeExpression result = TypeCheck3.symTypeFromAST(asttype);

    // Then
    assertNoFindings();
    Assertions.assertEquals(s, result.printFullName());
    Assertions.assertTrue(result instanceof SymTypeOfGenerics);
    Assertions.assertFalse(result.getTypeInfo() instanceof TypeSymbolSurrogate);
    Assertions.assertFalse(result.getTypeInfo() instanceof OOTypeSymbolSurrogate);
    Assertions.assertFalse(((SymTypeOfGenerics) result).getArgument(0)
        .getTypeInfo() instanceof TypeSymbolSurrogate);
    Assertions.assertFalse(((SymTypeOfGenerics) result).getArgument(0)
        .getTypeInfo() instanceof OOTypeSymbolSurrogate);
  }

}
