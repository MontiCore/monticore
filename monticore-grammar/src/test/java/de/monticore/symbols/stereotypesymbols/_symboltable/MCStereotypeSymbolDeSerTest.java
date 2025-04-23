/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.stereotypesymbols._symboltable;

import de.monticore.symbols.stereotypesymbols.StereotypeSymbolsMill;
import de.monticore.symboltable.stereotypes.StereoValueType;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

  protected static final String MULTIPLE_ALLOWED_VALUE_TYPES =
    "{\"kind\":\"de.monticore.symbols.stereotypesymbols._symboltable.MCStereotypeSymbol\"," +
      "\"name\":\"Foo\"," +
      "\"fullName\":\"Foo\"," +
      "\"annotatedElement\":" +
        "\"de.monticore.symbols.stereotypesymbols._symboltable.MCStereotypeSymbol\"," +
      "\"allowedValueTypes\":[\"boolean\",\"none\"]" +
      "}";

  @Test
  void shouldSerializeCorrectlyWithMultipleValueTypes() {
    // Given
    MCStereotypeSymbol stereotype = StereotypeSymbolsMill
      .mCStereotypeSymbolBuilder()
      .setName("Foo")
      .setAnnotatedElement(MCStereotypeSymbol.class)
      .addAllowedValueTypes(StereoValueType.BOOLEAN)
      .addAllowedValueTypes(StereoValueType.NONE)
      .build();

    // When
    String serial = deSer.serialize(stereotype, syms2json);

    // Then
    assertAll(
      () -> assertEquals(MULTIPLE_ALLOWED_VALUE_TYPES, serial),
      () -> assertEquals(0, Log.getFindingsCount())
    );
  }

  @Test
  void shouldSerializeAllowedValueTypes() {
    // Given & When
    MCStereotypeSymbol stereotype = deSer.deserialize(MULTIPLE_ALLOWED_VALUE_TYPES);

    // Then
    assertAll(
      () -> assertEquals(MCStereotypeSymbol.class, stereotype.getAnnotatedElement()),
      () -> assertEquals(2, stereotype.getAllowedValueTypesList().size()),
      () -> assertEquals(0, Log.getFindingsCount())
    );
    assertAll(
      () -> assertTrue(stereotype.containsAllowedValueTypes(StereoValueType.BOOLEAN)),
      () -> assertTrue(stereotype.containsAllowedValueTypes(StereoValueType.NONE))
    );
  }
}
