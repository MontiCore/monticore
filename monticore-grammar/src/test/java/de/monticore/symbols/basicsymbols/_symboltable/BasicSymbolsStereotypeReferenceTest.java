/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.basicsymbols._symboltable;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symboltable.serialization.json.JsonElementFactory;
import de.monticore.symboltable.stereotypes.IStereotypeReference;
import de.monticore.symboltable.stereotypes.IStereotypeSymbol;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests {@link BasicSymbolsStereotypeReference} */
class BasicSymbolsStereotypeReferenceTest {

  @BeforeEach
  void setUp() {
    LogStub.init();
    BasicSymbolsMill.init();
    BasicSymbolsStereoinfoDeSer.init();
    JsonElementFactory.setInstance(new JsonElementFactory());
  }

  @AfterEach
  void teardown() {
    BasicSymbolsMill.globalScope().clear();
    BasicSymbolsMill.reset();
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
    assertEquals(0, LogStub.getFindingsCount());
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
    assertEquals(1, LogStub.getFindingsCount());
    assertEquals(1, LogStub.getErrorCount());
    assertTrue(LogStub.getFindings().get(0).getMsg().startsWith("0x82406"));
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
    assertEquals(1, LogStub.getFindingsCount());
    assertEquals(1, LogStub.getErrorCount());
    assertTrue(LogStub.getFindings().get(0).getMsg().startsWith("0xA4095"));
  }

  protected MCStereotypeSymbol createStereotype(String name) {
    return BasicSymbolsMill.mCStereotypeSymbolBuilder()
      .setName(name)
      .build();
  }

}
