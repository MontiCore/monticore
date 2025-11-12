/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.mcbasics._visitor.MCBasicsTraverser;
import de.monticore.symbols.compsymbols.CompSymbolsMill;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.ICompSymbolsScope;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCVoidType;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesTraverser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class SynthesizeCompKindFromMCBasicTypesTest {

  @BeforeAll
  public static void beforeAll() {
    // Standard MontiCore test logging setup
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @BeforeEach
  public void setup() {
    Log.clearFindings();

    // reset first
    MCBasicTypesMill.reset();
    CompSymbolsMill.reset();

    // init CompSymbols
    MCBasicTypesMill.init();
    CompSymbolsMill.init();
  }

  @Test
  public void shouldHandleMCQualifiedType_whenSymbolPresent() {
    String normalCompName = "Comp1";
    ComponentTypeSymbol normalComp = CompSymbolsMill.componentTypeSymbolBuilder()
      .setName(normalCompName)
      .setSpannedScope(CompSymbolsMill.scope())
      .build();
    CompSymbolsMill.globalScope().add(normalComp);
    CompSymbolsMill.globalScope().addSubScope(normalComp.getSpannedScope());

    String qualifiedCompName = "Comp2";
    ComponentTypeSymbol qualifiedComp = CompSymbolsMill.componentTypeSymbolBuilder()
      .setName(qualifiedCompName)
      .setSpannedScope(CompSymbolsMill.scope())
      .build();

    String multipleNormalCompName = "Comp3";
    ComponentTypeSymbol multipleNormalComp1 = CompSymbolsMill.componentTypeSymbolBuilder()
      .setName(multipleNormalCompName)
      .setSpannedScope(CompSymbolsMill.scope())
      .build();
    CompSymbolsMill.globalScope().add(multipleNormalComp1);
    CompSymbolsMill.globalScope().addSubScope(multipleNormalComp1.getSpannedScope());
    ComponentTypeSymbol multipleNormalComp2 = CompSymbolsMill.componentTypeSymbolBuilder()
      .setName(multipleNormalCompName)
      .setSpannedScope(CompSymbolsMill.scope())
      .build();
    CompSymbolsMill.globalScope().add(multipleNormalComp2);
    CompSymbolsMill.globalScope().addSubScope(multipleNormalComp2.getSpannedScope());

    String nameOfQualCompScope = "scoop";
    ICompSymbolsScope scopeOfQualComp = CompSymbolsMill.scope();
    scopeOfQualComp.setName(nameOfQualCompScope);
    scopeOfQualComp.add(qualifiedComp);
    scopeOfQualComp.addSubScope(qualifiedComp.getSpannedScope());
    CompSymbolsMill.globalScope().addSubScope(scopeOfQualComp);

    // Build the qualified types via MCBasicTypesMill
    ASTMCQualifiedType astNormalComp = MCBasicTypesMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder()
        .addParts(normalCompName)
        .build())
      .build();
    ASTMCQualifiedType astQualComp = MCBasicTypesMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder()
        .addParts(nameOfQualCompScope)
        .addParts(qualifiedCompName)
        .build())
      .build();
    ASTMCQualifiedType astMultiNormalComp = MCBasicTypesMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder()
        .addParts(multipleNormalCompName)
        .build())
      .build();
    var mcBasicTypesGlobal = MCBasicTypesMill.globalScope();
    astNormalComp.setEnclosingScope(mcBasicTypesGlobal);
    astQualComp.setEnclosingScope(mcBasicTypesGlobal);
    astMultiNormalComp.setEnclosingScope(mcBasicTypesGlobal);

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
      result4multi.getResult().get().getTypeInfo().equals(multipleNormalComp1) ||
        result4multi.getResult().get().getTypeInfo().equals(multipleNormalComp2)
    );
    /*assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(getErrorCodes(MCError.AMBIGUOUS_COMPONENT_REFERENCE));*/


    var srcNormal = result4normal.getResult().get().getSourceNode();
    Assertions.assertTrue(srcNormal.isPresent() && srcNormal.get().equals(astNormalComp));

    var srcQual = result4qual.getResult().get().getSourceNode();
    Assertions.assertTrue(srcQual.isPresent() && srcQual.get().equals(astQualComp));

    var srcMulti = result4multi.getResult().get().getSourceNode();
    Assertions.assertTrue(srcMulti.isPresent() && srcMulti.get().equals(astMultiNormalComp));
  }

  @Test
  public void shouldNotHandleMCQualifiedType() {
    // Given
    ASTMCQualifiedType astNormalComp = MCBasicTypesMill.mCQualifiedTypeBuilder()
      .setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder()
        .addParts("Foo")
        .build())
      .build();
    ASTMCQualifiedType astQualComp =
      MCBasicTypesMill.mCQualifiedTypeBuilder()
        .setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder()
          .addParts("qual")
          .addParts("Foo")
          .build())
        .build();
    astNormalComp.setEnclosingScope(CompSymbolsMill.globalScope());
    astQualComp.setEnclosingScope(CompSymbolsMill.globalScope());

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
   /* assertThat(getLoggedErrorCodes())
      .containsExactlyInAnyOrder(
        getErrorCodes(
          MCError.MISSING_COMPONENT,
          MCError.MISSING_COMPONENT
        )
      );*/
  }

  @Test
  public void shouldNotHandleVoidType() {
    // Given
    ASTMCVoidType voidType = MCBasicTypesMill.mCVoidTypeBuilder().build();
    CompKindCheckResult resultWrapper = new CompKindCheckResult();
    SynthesizeCompKindFromMCBasicTypes synth = new SynthesizeCompKindFromMCBasicTypes(resultWrapper);

    // Attach a traverser to the synth, as we do not override the handle method and thus the synth tries to traverse the
    // AST. In the end this should result in an empty synth result, however, if we do not attach a traverser, this will
    // Result in an error instead.
    MCBasicTypesTraverser traverser = MCBasicTypesMill.traverser();
    traverser.setMCBasicTypesHandler(synth);


    // When
    synth.handle(voidType);

    // Then
    Assertions.assertFalse(resultWrapper.getResult().isPresent());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
