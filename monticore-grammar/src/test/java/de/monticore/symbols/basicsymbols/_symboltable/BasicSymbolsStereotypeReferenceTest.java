/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.basicsymbols._symboltable;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symboltable.serialization.json.JsonElementFactory;
import de.monticore.symboltable.stereotypes.IStereotypeReference;
import de.monticore.symboltable.stereotypes.IStereotypeSymbol;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests {@link BasicSymbolsStereotypeReference} */
@TestWithMCLanguage(BasicSymbolsMill.class)
class BasicSymbolsStereotypeReferenceTest {

  @BeforeEach
  void setUp() {
    BasicSymbolsStereoinfoDeSer.init();
    JsonElementFactory.setInstance(new JsonElementFactory());
  }

  @Test
  void shouldFindStereotype() {
    // Given
    String stereotypeName = "Stereo";
    IBasicSymbolsScope enclScope = BasicSymbolsMill.scope();
    MCStereotypeSymbol stereotype = createStereotype(stereotypeName);
    enclScope.add(stereotype);

    IStereotypeReference ref = new BasicSymbolsStereotypeReference(stereotypeName, enclScope);

    // When
    Optional<? extends IStereotypeSymbol> resolvedStereotype = ref.getResolved();

    // Then
    assertTrue(resolvedStereotype.isPresent());
    assertSame(stereotype, resolvedStereotype.get());
  }

  @Test
  void shouldNotFindUnavailableStereotype() {
    // Given
    IBasicSymbolsScope enclScope = BasicSymbolsMill.scope();
    IStereotypeReference ref = new BasicSymbolsStereotypeReference("NoStereo", enclScope);

    // When
    Optional<? extends IStereotypeSymbol> resolvedStereotype = ref.getResolved();

    // Then
    assertTrue(resolvedStereotype.isEmpty());
    
    Log.getFindings().remove(
        MCAssertions.assertHasFindingStartingWith("0x82406"));
  }

  @Test
  void shouldLogErrorForAmbiguousStereotype() {
    // Given
    String stereotypeName = "Ambiguous";
    IBasicSymbolsScope enclScope = BasicSymbolsMill.scope();
    MCStereotypeSymbol stereotype1 = createStereotype(stereotypeName);
    MCStereotypeSymbol stereotype2 = createStereotype(stereotypeName);
    enclScope.add(stereotype1);
    enclScope.add(stereotype2);

    IStereotypeReference ref = new BasicSymbolsStereotypeReference(stereotypeName, enclScope);

    // When
    Optional<? extends IStereotypeSymbol> resolvedStereotype = ref.getResolved();

    // Then
    assertTrue(resolvedStereotype.isEmpty());
    Log.getFindings().remove(
        MCAssertions.assertHasFindingStartingWith("0xA4095"));
  }

  protected MCStereotypeSymbol createStereotype(String name) {
    return BasicSymbolsMill.mCStereotypeSymbolBuilder()
      .setName(name)
      .build();
  }

}
