/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.stereotypesymbols._symboltable;

import de.monticore.interpreter.Value;
import de.monticore.symbols.stereotypesymbols.StereotypeSymbolsMill;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonElementFactory;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.symboltable.stereotypes.ISymbolicStereotype;
import de.monticore.symboltable.stereotypes.StereoinfoDeSer;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Tests {@link StereotypeSymbolsStereoinfoDeSer} */
class StereotypeSymbolsStereoinfoDeSerTest {

  @BeforeEach
  void setUp() {
    LogStub.init();
    StereotypeSymbolsMill.init();
    StereotypeSymbolsStereoinfoDeSer.init();
    JsonElementFactory.setInstance(new JsonElementFactory());
  }

  @AfterEach
  void teardown() {
    StereotypeSymbolsMill.globalScope().clear();
    StereotypeSymbolsMill.reset();
  }

  @Test
  void shouldSerializeStereoInfoWithoutValueInOtherPackage() {
    // Given
    String packageName = "a.b.c";
    String stereoTypeName = "D";

    MCStereotypeSymbol stereoSym = createStereotype(stereoTypeName);
    IStereotypeSymbolsScope artifactScope = wrapIntoArtifactScope(stereoSym, packageName);
    StereotypeSymbolsMill.globalScope().addSubScope(artifactScope);

    // When
    String json = StereoinfoDeSer.printAsJson(stereoSym, Optional.empty());

    // Then
    assertEquals("{\"stereotype\":\"a.b.c.D\"}", json);
  }

  @Test
  void shouldSerializeStereoInfoWithoutValueInGlobalScope() {
    // Given
    String stereoTypeName = "A";
    MCStereotypeSymbol stereoSym = createStereotype(stereoTypeName);
    StereotypeSymbolsMill.globalScope().add(stereoSym);

    // When
    String json = StereoinfoDeSer.printAsJson(stereoSym, Optional.empty());

    // Then
    assertEquals("{\"stereotype\":\"A\"}", json);
  }

  @Test
  void shouldDeserializeStereoInfoWithoutValueInOtherPackage() {
    // Given
    String packageName = "a.b.c";
    String stereoTypeName = "D";
    String fullName = packageName + "." + stereoTypeName;

    JsonElement jsonTypeRef = JsonElementFactory.createJsonString(fullName);
    JsonObject jsonStereoInfo = JsonElementFactory.createJsonObject();
    jsonStereoInfo.putMember("stereotype", jsonTypeRef);

    MCStereotypeSymbol stereoSym = createStereotype(stereoTypeName);
    IStereotypeSymbolsScope artifactScope = wrapIntoArtifactScope(stereoSym, packageName);
    StereotypeSymbolsMill.globalScope().addSubScope(artifactScope);

    // When
    Map.Entry<ISymbolicStereotype, Optional<Value>> deserialized =
      StereoinfoDeSer.deserialize(jsonStereoInfo, StereotypeSymbolsMill.globalScope());

    // Then
    assertAll(
      () -> assertInstanceOf(MCStereotypeSymbolSurrogate.class, deserialized.getKey()),
      () -> assertTrue(deserialized.getValue().isEmpty()),
      () -> assertEquals(0, LogStub.getFindingsCount())
    );

    MCStereotypeSymbolSurrogate refAsSurrogate =
      (MCStereotypeSymbolSurrogate) deserialized.getKey();

    assertTrue(refAsSurrogate.checkLazyLoadDelegate());
    assertEquals(stereoSym, refAsSurrogate.lazyLoadDelegate());
  }

  @Test
  void shouldDeserializeStereoInfoWithoutValueInSamePackage() {
    // Given
    String stereoTypeName = "A";

    JsonElement jsonTypeRef = JsonElementFactory.createJsonString(stereoTypeName);
    JsonObject jsonStereoInfo = JsonElementFactory.createJsonObject();
    jsonStereoInfo.putMember("stereotype", jsonTypeRef);

    MCStereotypeSymbol stereoSym = createStereotype(stereoTypeName);
    IStereotypeSymbolsScope commonScope = StereotypeSymbolsMill.scope();
    commonScope.add(stereoSym);

    // When
    Map.Entry<ISymbolicStereotype, Optional<Value>> deserialized =
      StereoinfoDeSer.deserialize(jsonStereoInfo, commonScope);

    // Then
    assertAll(
      () -> assertInstanceOf(MCStereotypeSymbolSurrogate.class, deserialized.getKey()),
      () -> assertTrue(deserialized.getValue().isEmpty()),
      () -> assertEquals(0, LogStub.getFindingsCount())
    );

    MCStereotypeSymbolSurrogate refAsSurrogate =
      (MCStereotypeSymbolSurrogate) deserialized.getKey();

    assertTrue(refAsSurrogate.checkLazyLoadDelegate());
    assertEquals(stereoSym, refAsSurrogate.lazyLoadDelegate());
  }

  protected MCStereotypeSymbol createStereotype(String name) {
    return StereotypeSymbolsMill.mCStereotypeSymbolBuilder()
      .setName(name)
      .build();
  }

  protected IStereotypeSymbolsScope wrapIntoArtifactScope(MCStereotypeSymbol stereotype,
                                                          String packageName) {
    IStereotypeSymbolsArtifactScope scope = StereotypeSymbolsMill.artifactScope();
    scope.setPackageName(packageName);
    scope.add(stereotype);

    return scope;
  }
}
