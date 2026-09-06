/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.basicsymbols._symboltable;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonElementFactory;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.symboltable.stereotypes.IStereotypeReference;
import de.monticore.symboltable.stereotypes.StereoinfoDeSer;
import de.monticore.symboltable.stereotypes.SymbolBackedStereotypeReference;
import de.monticore.values.MCValue;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/** Tests {@link BasicSymbolsStereoinfoDeSer} */
@TestWithMCLanguage(BasicSymbolsMill.class)
class BasicSymbolsStereoinfoDeSerTest {

  @BeforeEach
  void setUp() {
    BasicSymbolsStereoinfoDeSer.init();
    JsonElementFactory.setInstance(new JsonElementFactory());
  }

  @Test
  void shouldSerializeStereoInfoWithoutValueInOtherPackage() {
    // Given
    String packageName = "a.b.c";
    String stereoTypeName = "D";

    MCStereotypeSymbol stereoSym = createStereotype(stereoTypeName);
    IBasicSymbolsScope artifactScope = wrapIntoArtifactScope(stereoSym, packageName);
    BasicSymbolsMill.globalScope().addSubScope(artifactScope);
    IStereotypeReference stereoRef = new SymbolBackedStereotypeReference(stereoSym);

    // When
    String json = StereoinfoDeSer.printAsJson(stereoRef, Optional.empty());

    // Then
    assertEquals("{\"stereotype\":\"a.b.c.D\"}", json);
  }

  @Test
  void shouldSerializeStereoInfoWithoutValueInGlobalScope() {
    // Given
    String stereoTypeName = "A";
    MCStereotypeSymbol stereoSym = createStereotype(stereoTypeName);
    BasicSymbolsMill.globalScope().add(stereoSym);
    IStereotypeReference stereoRef = new SymbolBackedStereotypeReference(stereoSym);

    // When
    String json = StereoinfoDeSer.printAsJson(stereoRef, Optional.empty());

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
    Map.Entry<IStereotypeReference, Optional<MCValue>> deserialized =
      StereoinfoDeSer.deserialize(jsonStereoInfo, BasicSymbolsMill.globalScope());

    // Then
    assertAll(
      () -> assertInstanceOf(BasicSymbolsStereotypeReference.class, deserialized.getKey()),
      () -> assertTrue(deserialized.getValue().isEmpty()),
      () -> assertEquals(0, LogStub.getFindingsCount())
    );

    BasicSymbolsStereotypeReference refAsRef =
      (BasicSymbolsStereotypeReference) deserialized.getKey();

    assertTrue(refAsRef.getResolved().isPresent());
    assertEquals(stereoSym, refAsRef.getResolved().get());
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
    Map.Entry<IStereotypeReference, Optional<MCValue>> deserialized =
      StereoinfoDeSer.deserialize(jsonStereoInfo, commonScope);

    // Then
    assertAll(
      () -> assertInstanceOf(BasicSymbolsStereotypeReference.class, deserialized.getKey()),
      () -> assertTrue(deserialized.getValue().isEmpty()),
      () -> assertEquals(0, LogStub.getFindingsCount())
    );

    BasicSymbolsStereotypeReference refAsRef =
      (BasicSymbolsStereotypeReference) deserialized.getKey();

    assertTrue(refAsRef.getResolved().isPresent());
    assertEquals(stereoSym, refAsRef.getResolved().get());
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
