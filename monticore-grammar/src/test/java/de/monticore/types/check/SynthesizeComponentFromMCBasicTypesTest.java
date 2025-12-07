/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.types.MCBasicTypesTest;
import de.monticore.types.componentsymbolswithmcbasictypestest.ComponentSymbolsWithMCBasicTypesTestMill;
import de.monticore.types.componentsymbolswithmcbasictypestest._visitor.ComponentSymbolsWithMCBasicTypesTestTraverser;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.ASTMCVoidType;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

public class SynthesizeComponentFromMCBasicTypesTest extends MCBasicTypesTest {

  private CombineExpressionsWithLiteralsParser parser;

  @BeforeAll
  public static void beforeAll() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @BeforeEach
  public void setup() {
    Log.clearFindings();
    parser = CombineExpressionsWithLiteralsMill.parser();

    ComponentSymbolsWithMCBasicTypesTestMill.reset();
    ComponentSymbolsWithMCBasicTypesTestMill.init();
  }

  @Test
  public void synthesizesCompKind_forResolvableComponentTypeSymbol() throws Exception {
    ComponentTypeSymbol typeA = ComponentSymbolsWithMCBasicTypesTestMill.componentTypeSymbolBuilder()
      .setName("A")
      .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
      .build();
    ComponentSymbolsWithMCBasicTypesTestMill.globalScope().add(typeA);
    typeA.setEnclosingScope(ComponentSymbolsWithMCBasicTypesTestMill.globalScope());

    ASTMCType ast = parser.parse_StringMCType("A").orElseThrow();
    ast.setEnclosingScope(ComponentSymbolsWithMCBasicTypesTestMill.globalScope());

    FullSynthesizeCompKindFromMCBasicTypes synth = new FullSynthesizeCompKindFromMCBasicTypes();

    Optional<CompKindExpression> res = synth.synthesize(ast);
    Assertions.assertTrue(res.isPresent());
    MCAssertions.assertNoFindings("Did not expect central error 0xD0104");
  }

  @Test
  public void shouldLogCentralError_whenPrimitiveType() throws Exception {
    ASTMCType astDouble = parser.parse_StringMCType("double").orElseThrow();

    FullSynthesizeCompKindFromMCBasicTypes synth = new FullSynthesizeCompKindFromMCBasicTypes();

    Optional<CompKindExpression> result = synth.synthesize(astDouble);

    Assertions.assertTrue(result.isEmpty(), "Expected no CompKindExpression for primitive 'double'");
    MCAssertions.assertHasFindings(
      f -> {
        String m = f.getMsg();
        return m != null && m.contains("0xD0104");
      },
      "Expected a central finding containing 0xD0104"
    );
  }

  @Test
  public void shouldHandleMCQualifiedType() {
    // Given
    // First build some component type symbols which we refer to with the qualified type
    String normalCompName = "Comp1";
    ComponentTypeSymbol normalComp = ComponentSymbolsWithMCBasicTypesTestMill.componentTypeSymbolBuilder()
      .setName(normalCompName)
      .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
      .build();
    ComponentSymbolsWithMCBasicTypesTestMill.globalScope().add(normalComp);
    ComponentSymbolsWithMCBasicTypesTestMill.globalScope().addSubScope(normalComp.getSpannedScope());

    String qualifiedCompName = "Comp2";
    ComponentTypeSymbol qualifiedComp = ComponentSymbolsWithMCBasicTypesTestMill.componentTypeSymbolBuilder()
      .setName(qualifiedCompName)
      .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
      .build();

    String multipleNormalCompName = "Comp3";
    ComponentTypeSymbol multipleNormalComp1 = ComponentSymbolsWithMCBasicTypesTestMill.componentTypeSymbolBuilder()
      .setName(multipleNormalCompName)
      .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
      .build();
    ComponentSymbolsWithMCBasicTypesTestMill.globalScope().add(multipleNormalComp1);
    ComponentSymbolsWithMCBasicTypesTestMill.globalScope().addSubScope(multipleNormalComp1.getSpannedScope());
    ComponentTypeSymbol multipleNormalComp2 = ComponentSymbolsWithMCBasicTypesTestMill.componentTypeSymbolBuilder()
      .setName(multipleNormalCompName)
      .setSpannedScope(ComponentSymbolsWithMCBasicTypesTestMill.scope())
      .build();
    ComponentSymbolsWithMCBasicTypesTestMill.globalScope().add(multipleNormalComp2);
    ComponentSymbolsWithMCBasicTypesTestMill.globalScope().addSubScope(multipleNormalComp2.getSpannedScope());

    String nameOfQualCompScope = "scoop";
    var scopeOfQualComp = ComponentSymbolsWithMCBasicTypesTestMill.scope();
    scopeOfQualComp.setName(nameOfQualCompScope);
    scopeOfQualComp.add(qualifiedComp);
    scopeOfQualComp.addSubScope(qualifiedComp.getSpannedScope());
    ComponentSymbolsWithMCBasicTypesTestMill.globalScope().addSubScope(scopeOfQualComp);

    // Now build the qualified type
    ASTMCQualifiedType astNormalComp = ComponentSymbolsWithMCBasicTypesTestMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(ComponentSymbolsWithMCBasicTypesTestMill.mCQualifiedNameBuilder()
        .addParts(normalCompName)
        .build())
      .build();
    ASTMCQualifiedType astQualComp = ComponentSymbolsWithMCBasicTypesTestMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(ComponentSymbolsWithMCBasicTypesTestMill.mCQualifiedNameBuilder()
        .addParts(nameOfQualCompScope)
        .addParts(qualifiedCompName)
        .build())
      .build();
    ASTMCQualifiedType astMultiNormalComp = ComponentSymbolsWithMCBasicTypesTestMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(ComponentSymbolsWithMCBasicTypesTestMill.mCQualifiedNameBuilder()
        .addParts(multipleNormalCompName)
        .build())
      .build();
    astNormalComp.setEnclosingScope(ComponentSymbolsWithMCBasicTypesTestMill.globalScope());
    astQualComp.setEnclosingScope(ComponentSymbolsWithMCBasicTypesTestMill.globalScope());
    astMultiNormalComp.setEnclosingScope(ComponentSymbolsWithMCBasicTypesTestMill.globalScope());

    CompKindCheckResult result4normal = new CompKindCheckResult();
    CompKindCheckResult result4qual = new CompKindCheckResult();
    CompKindCheckResult result4multi = new CompKindCheckResult();
    SynthesizeCompKindFromMCBasicTypes synth4normal = new SynthesizeCompKindFromMCBasicTypes(result4normal);
    SynthesizeCompKindFromMCBasicTypes synth4qual = new SynthesizeCompKindFromMCBasicTypes(result4qual);
    SynthesizeCompKindFromMCBasicTypes synth4multi = new SynthesizeCompKindFromMCBasicTypes(result4multi);

    // When
    synth4normal.handle(astNormalComp);
    synth4qual.handle(astQualComp);
    synth4multi.handle(astMultiNormalComp);

    // Then
    Assertions.assertTrue(result4normal.getResult().isPresent());
    Assertions.assertTrue(result4qual.getResult().isPresent());
    Assertions.assertTrue(result4multi.getResult().isPresent());

    Assertions.assertEquals(normalComp, result4normal.getResult().get().getTypeInfo());
    Assertions.assertEquals(qualifiedComp, result4qual.getResult().get().getTypeInfo());
    Assertions.assertTrue(
      result4multi.getResult().get().getTypeInfo().equals(multipleNormalComp1)
        || result4multi.getResult().get().getTypeInfo().equals(multipleNormalComp2));
  }

  @Test
  public void shouldNotHandleMCQualifiedType() {
    // Given
    ASTMCQualifiedType astNormalComp = ComponentSymbolsWithMCBasicTypesTestMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(ComponentSymbolsWithMCBasicTypesTestMill.mCQualifiedNameBuilder()
        .addParts("Foo")
        .build())
      .build();
    ASTMCQualifiedType astQualComp =
      ComponentSymbolsWithMCBasicTypesTestMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(ComponentSymbolsWithMCBasicTypesTestMill.mCQualifiedNameBuilder()
          .addParts("qual")
          .addParts("Foo")
          .build())
        .build();
    astNormalComp.setEnclosingScope(ComponentSymbolsWithMCBasicTypesTestMill.globalScope());
    astQualComp.setEnclosingScope(ComponentSymbolsWithMCBasicTypesTestMill.globalScope());

    CompKindCheckResult result4normal = new CompKindCheckResult();
    CompKindCheckResult result4qual = new CompKindCheckResult();
    SynthesizeCompKindFromMCBasicTypes synth4normal = new SynthesizeCompKindFromMCBasicTypes(result4normal);
    SynthesizeCompKindFromMCBasicTypes synth4qual = new SynthesizeCompKindFromMCBasicTypes(result4qual);

    // When
    synth4normal.handle(astNormalComp);
    synth4qual.handle(astQualComp);

    // Then
    Assertions.assertFalse(result4normal.getResult().isPresent());
    Assertions.assertFalse(result4qual.getResult().isPresent());
  }

  @Test
  public void shouldNotHandleVoidType() {
    // Given
    ASTMCVoidType voidType = ComponentSymbolsWithMCBasicTypesTestMill.mCVoidTypeBuilder().build();
    CompKindCheckResult resultWrapper = new CompKindCheckResult();
    SynthesizeCompKindFromMCBasicTypes synth = new SynthesizeCompKindFromMCBasicTypes(resultWrapper);

    // Attach a traverser to the synth, as we do not override the handle method and thus the synth tries to traverse the
    // AST. In the end this should result in an empty synth result, however, if we do not attach a traverser, this will
    // Result in an error instead.
    ComponentSymbolsWithMCBasicTypesTestTraverser traverser = ComponentSymbolsWithMCBasicTypesTestMill.traverser();
    traverser.setMCBasicTypesHandler(synth);

    // When
    synth.handle(voidType);

    // Then
    Assertions.assertFalse(resultWrapper.getResult().isPresent());
    MCAssertions.assertNoFindings();
  }
}
