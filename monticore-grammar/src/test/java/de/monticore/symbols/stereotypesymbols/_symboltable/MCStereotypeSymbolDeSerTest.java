/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.stereotypesymbols._symboltable;

import de.monticore.symbols.stereotypesymbols.StereotypeSymbolsMill;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/** Tests {@link MCStereotypeSymbolDeSer} */
class MCStereotypeSymbolDeSerTest {

  protected MCStereotypeSymbolDeSer deSer;
  protected StereotypeSymbolsSymbols2Json syms2json;

  @BeforeEach
  void setup() {
    LogStub.init();
    StereotypeSymbolsMill.reset();
    StereotypeSymbolsMill.init();

    deSer = new MCStereotypeSymbolDeSer();
    StereotypeSymbolsMill.globalScope().putSymbolDeSer(deSer.getSerializedKind(), deSer);
    syms2json = new StereotypeSymbolsSymbols2Json();
  }

  protected static final String SERIALIZED =
    "{\"kind\":\"de.monticore.symbols.stereotypesymbols._symboltable.MCStereotypeSymbol\"," +
      "\"name\":\"Foo\"," +
      "\"fullName\":\"Foo\"," +
      "\"annotatedElement\":" +
        "\"foo.bar.Symbol\"" +
      "}";

  @Test
  void shouldSerializeCorrectlyWithMultipleValueTypes() {
    // Given
    MCStereotypeSymbol stereotype = StereotypeSymbolsMill
      .mCStereotypeSymbolBuilder()
      .setName("Foo")
      .setAnnotatedElement("foo.bar.Symbol")
      .build();

    // When
    String serial = deSer.serialize(stereotype, syms2json);

    // Then
    assertAll(
      () -> assertEquals(SERIALIZED, serial),
      () -> assertEquals(0, Log.getFindingsCount())
    );
  }

  @Test
  void shouldSerializeAllowedValueTypes() {
    // Given & When
    MCStereotypeSymbol stereotype = deSer.deserialize(SERIALIZED);

    // Then
    assertAll(
      () -> assertEquals("foo.bar.Symbol", stereotype.getAnnotatedElement()),
      () -> assertEquals(0, Log.getFindingsCount())
    );
  }
}
