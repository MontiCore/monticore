/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.basicsymbols._symboltable;

import de.monticore.interpreter.Value;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonElementFactory;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.symboltable.stereotypes.IStereotypeSymbol;
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

/** Tests {@link BasicSymbolsStereoinfoDeSer} */
class BasicSymbolsStereoinfoDeSerTest {

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
  void shouldSerializeStereoInfoWithoutValueInOtherPackage() {
    // Given
    String packageName = "a.b.c";
    String stereoTypeName = "D";

    MCStereotypeSymbol stereoSym = createStereotype(stereoTypeName);
    IBasicSymbolsScope artifactScope = wrapIntoArtifactScope(stereoSym, packageName);
    BasicSymbolsMill.globalScope().addSubScope(artifactScope);

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
    BasicSymbolsMill.globalScope().add(stereoSym);

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
    IBasicSymbolsScope artifactScope = wrapIntoArtifactScope(stereoSym, packageName);
    BasicSymbolsMill.globalScope().addSubScope(artifactScope);

    // When
    Map.Entry<IStereotypeSymbol, Optional<Value>> deserialized =
      StereoinfoDeSer.deserialize(jsonStereoInfo, BasicSymbolsMill.globalScope());

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
    IBasicSymbolsScope commonScope = BasicSymbolsMill.scope();
    commonScope.add(stereoSym);

    // When
    Map.Entry<IStereotypeSymbol, Optional<Value>> deserialized =
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
    return BasicSymbolsMill.mCStereotypeSymbolBuilder()
      .setName(name)
      .build();
  }

  protected IBasicSymbolsScope wrapIntoArtifactScope(MCStereotypeSymbol stereotype,
                                                          String packageName) {
    IBasicSymbolsArtifactScope scope = BasicSymbolsMill.artifactScope();
    scope.setPackageName(packageName);
    scope.add(stereotype);

    return scope;
  }
}
