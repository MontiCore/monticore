/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.compsymbols._symboltable;


import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols.CompSymbolsMill;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.CompKindOfComponentType;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;

import static java.nio.file.Files.readString;
import static org.junit.jupiter.api.Assertions.*;

public class ComponentTypeSymbolDeSerTest {

  protected static final String RELATIVE_DIR = "target/resources/test/de/monticore/symbols/compsymbols/_symboltable/";

  protected ComponentTypeSymbolDeSer deSer;
  protected CompSymbolsSymbols2Json comp2json;

  @BeforeEach
  void setup() {
    CompSymbolsMill.reset();
    CompSymbolsMill.init();
    BasicSymbolsMill.initializePrimitives();

    deSer = new ComponentTypeSymbolDeSer();
    CompSymbolsMill.globalScope().putSymbolDeSer(deSer.getSerializedKind(), deSer);
    SubcomponentSymbolDeSer subDeSer = new SubcomponentSymbolDeSer();
    CompSymbolsMill.globalScope().putSymbolDeSer(subDeSer.getSerializedKind(), subDeSer);
    comp2json = new CompSymbolsSymbols2Json();
  }

  @Test
  void shouldSerializeSuperComponentType() throws IOException {
    // Given
    // create a symbol for the super component type
    ComponentTypeSymbol superCType = CompSymbolsMill.componentTypeSymbolBuilder()
        .setName("SuperCType")
        .setSpannedScope(CompSymbolsMill.scope())
        .build();

    // create a reference to the super component type
    CompKindExpression parentType = new CompKindOfComponentType(superCType) {};

    // create a symbol for a component type, reference its super type
    ComponentTypeSymbol cType = CompSymbolsMill.componentTypeSymbolBuilder()
        .setName("CompTypeWithSuper1")
        .setSpannedScope(CompSymbolsMill.scope())
        .addSuperComponents(parentType)
        .build();

    // When
    String actual = deSer.serialize(cType, comp2json);

    // the expected result
    Path json = Path.of(RELATIVE_DIR, "WithSuper1.json");
    String expected = readString(json).replaceAll("\\s+", "");

    // Then
    assertEquals(expected, actual);
  }

  @Test
  void shouldSerializeSuperComponentType2() throws IOException {
    // Given
    // create symbols for the two super component types
    ComponentTypeSymbol superCType1 = CompSymbolsMill.componentTypeSymbolBuilder()
        .setName("SuperCType1")
        .setSpannedScope(CompSymbolsMill.scope())
        .build();
    ComponentTypeSymbol superCType2 = CompSymbolsMill.componentTypeSymbolBuilder()
        .setName("SuperCType2")
        .setSpannedScope(CompSymbolsMill.scope())
        .build();

    // create a reference for to each of the two super component types
    CompKindExpression parentType1 = new CompKindOfComponentType(superCType1);
    CompKindExpression parentType2 = new CompKindOfComponentType(superCType2);

    // symbol for a component type, reference its super types
    ComponentTypeSymbol cType = CompSymbolsMill.componentTypeSymbolBuilder()
        .setName("CompTypeWithSuper2")
        .setSpannedScope(CompSymbolsMill.scope())
        .addSuperComponents(parentType1)
        .addSuperComponents(parentType2)
        .build();

    // When
    String actual = deSer.serialize(cType, comp2json);

    // the expected result
    Path json = Path.of(RELATIVE_DIR, "WithSuper2.json");
    String expected = readString(json).replaceAll("\\s+", "");

    // Then
    assertEquals(expected, actual);
  }

  @Test
  void shouldNotSerializeAbsent() throws IOException {
    // Given
    ComponentTypeSymbol comp = createSimpleComp("Comp");

    // When
    String createdJson = deSer.serialize(comp, comp2json);

    // the expected result
    Path json = Path.of(RELATIVE_DIR, "Simple.json");
    String expected = readString(json).replaceAll("\\s+", "");

    // Then
    assertEquals(expected, createdJson);
  }

  @Test
  void shouldSerializeTypeParameters() throws IOException {
    // Given
    ComponentTypeSymbol comp = createSimpleComp("Comp");
    comp.getSpannedScope().add(
        CompSymbolsMill.typeVarSymbolBuilder()
            .setName("A")
            .setSpannedScope(CompSymbolsMill.scope())
            .build()
    );
    comp.getSpannedScope().add(
        CompSymbolsMill.typeVarSymbolBuilder()
            .setName("B")
            .setSpannedScope(CompSymbolsMill.scope())
            .build()
    );

    // When
    String createdJson = deSer.serialize(comp, comp2json);

    // the expected result
    Path json = Path.of(RELATIVE_DIR, "WithTypeParams.json");
    String expected = readString(json).replaceAll("\\s+", "");

    // Then
    assertEquals(expected, createdJson);
  }

  @Test
  void shouldSerializeParameters() throws IOException {
    // Given
    ComponentTypeSymbol comp = createSimpleComp("Comp");
    VariableSymbol paramA = CompSymbolsMill.variableSymbolBuilder()
        .setName("a")
        .setType(SymTypeExpressionFactory.createPrimitive("int"))
        .build();
    VariableSymbol paramB = CompSymbolsMill.variableSymbolBuilder()
        .setName("b")
        .setType(SymTypeExpressionFactory.createPrimitive("int"))
        .build();

    comp.getSpannedScope().add(paramA);
    comp.getSpannedScope().add(paramB);
    comp.addParameter(paramA);
    comp.addParameter(paramB);

    // When
    String createdJson = deSer.serialize(comp, comp2json);

    // the expected result
    Path json = Path.of(RELATIVE_DIR, "WithParams.json");
    String expected = readString(json).replaceAll("\\s+", "");

    // Then
    assertEquals(expected, createdJson);
  }

  @Test
  void shouldSerializePorts() throws IOException {
    // Given
    ComponentTypeSymbol comp = createSimpleComp("Comp");
    PortSymbol portIncoming = CompSymbolsMill.portSymbolBuilder()
        .setName("inc")
        .setIncoming(true)
        .setType(SymTypeExpressionFactory.createPrimitive("int"))
        .setTiming(Timing.TIMED)
        .setStronglyCausal(false)
        .build();
    PortSymbol portOutgoing = CompSymbolsMill.portSymbolBuilder()
        .setName("outg")
        .setOutgoing(true)
        .setType(SymTypeExpressionFactory.createPrimitive("int"))
        .setTiming(Timing.TIMED)
        .setStronglyCausal(false)
        .build();

    comp.getSpannedScope().add(portIncoming);
    comp.getSpannedScope().add(portOutgoing);


    // When
    String createdJson = deSer.serialize(comp, comp2json);

    // the expected result
    Path json = Path.of(RELATIVE_DIR, "WithPorts.json");
    String expected = readString(json).replaceAll("\\s+", "");

    // Then
    assertEquals(expected, createdJson);
  }

  @Test
  void shouldNotDeserializeAbsentParent() throws IOException {
    // When
    Path json = Path.of(RELATIVE_DIR, "Simple.json");
    String jsonString = readString(json).replaceAll("\\s+", "");
    ComponentTypeSymbol comp = deSer.deserialize(CompSymbolsMill.globalScope(), jsonString);

    // Then
    assertTrue(comp.isEmptySuperComponents(), "Parent is present");
  }

  @Test
  void shouldDeserializeTypeParameters() throws IOException {
    // When
    Path json = Path.of(RELATIVE_DIR, "WithTypeParams.json");
    String jsonString = readString(json).replaceAll("\\s+", "");
    ComponentTypeSymbol comp = deSer.deserialize(CompSymbolsMill.globalScope(), jsonString);

    // Then
    assertEquals(2, comp.getTypeParameters().size());
    assertAll(
        () -> assertEquals("A", comp.getTypeParameters().get(0).getName()),
        () -> assertEquals("B", comp.getTypeParameters().get(1).getName())
    );
  }

  @Test
  void shouldDeserializeParameters() throws IOException {
    // When
    Path json = Path.of(RELATIVE_DIR, "WithParams.json");
    String jsonString = readString(json).replaceAll("\\s+", "");
    ComponentTypeSymbol comp = deSer.deserialize(CompSymbolsMill.globalScope(), jsonString);

    // Then
    assertEquals(2, comp.getParameterList().size());
    assertEquals(2, comp.getSpannedScope().getLocalVariableSymbols().size());
    assertAll(
        () -> assertEquals("a", comp.getParameterList().get(0).getName()),
        () -> assertEquals("b", comp.getParameterList().get(1).getName()),
        () -> assertTrue(comp.getSpannedScope().resolveVariable("a").isPresent()),
        () -> assertTrue(comp.getSpannedScope().resolveVariable("b").isPresent()),
        () -> assertEquals(comp.getSpannedScope().resolveVariable("a").get(), comp.getParameterList().get(0)),
        () -> assertEquals(comp.getSpannedScope().resolveVariable("b").get(), comp.getParameterList().get(1))
    );
  }

  @Test
  void shouldDeserializePorts() throws IOException {
    // When
    Path json = Path.of(RELATIVE_DIR, "WithPorts.json");
    String jsonString = readString(json).replaceAll("\\s+", "");
    ComponentTypeSymbol comp = deSer.deserialize(CompSymbolsMill.globalScope(), jsonString);

    // Then
    assertEquals(2, comp.getPorts().size());
    assertEquals(2, comp.getSpannedScope().getLocalPortSymbols().size());
    assertAll(
        () -> assertEquals("inc", comp.getPorts().get(0).getName()),
        () -> assertEquals("outg", comp.getPorts().get(1).getName()),
        () -> assertTrue(comp.getSpannedScope().resolvePort("inc").isPresent()),
        () -> assertTrue(comp.getSpannedScope().resolvePort("outg").isPresent()),
        () -> assertEquals(comp.getSpannedScope().resolvePort("inc").get(), comp.getPorts().get(0)),
        () -> assertEquals(comp.getSpannedScope().resolvePort("outg").get(), comp.getPorts().get(1))
    );
  }

  @Test
  void shouldSerializeSubComponents() throws IOException {
    // Given
    ComponentTypeSymbol comp = createSimpleComp("Parent");
    comp.getSpannedScope().add(
        CompSymbolsMill.subcomponentSymbolBuilder()
            .setName("inst")
            .setType(new CompKindOfComponentType(createSimpleComp("Comp")))
            .build()
    );

    // When
    String createdJson = deSer.serialize(comp, comp2json);

    // the expected result
    Path json = Path.of(RELATIVE_DIR, "WithSub.json");
    String expected = readString(json).replaceAll("\\s+", "");

    // Then
    assertEquals(expected, createdJson);
  }

  @Test
  void shouldDeserializeSubComponents() throws IOException {
    // When
    Path json = Path.of(RELATIVE_DIR, "WithSub.json");
    String jsonString = readString(json).replaceAll("\\s+", "");
    ComponentTypeSymbol comp = deSer.deserialize(CompSymbolsMill.globalScope(), jsonString);

    // Then
    assertEquals(1, comp.getSubcomponents().size());
    assertEquals(1, comp.getSpannedScope().getLocalSubcomponentSymbols().size());
    assertAll(
        () -> assertEquals("inst", comp.getSubcomponents().get(0).getName()),
        () -> assertTrue(comp.getSpannedScope().resolveSubcomponent("inst").isPresent()),
        () -> assertEquals(comp.getSpannedScope().resolveSubcomponent("inst").get(), comp.getSubcomponents().get(0))
    );
  }

  protected static ComponentTypeSymbol createSimpleComp(String name) {
    return CompSymbolsMill.componentTypeSymbolBuilder()
        .setName(name)
        .setSpannedScope(CompSymbolsMill.scope())
        .build();
  }

  @Test
  void shouldSerializeSpec() throws IOException {
    // Given
    ComponentTypeSymbol comp = createSimpleComp("Comp");
    ComponentTypeSymbol refinement = createSimpleComp("RefinementCType");
    CompKindExpression refinementType = new CompKindOfComponentType(refinement);
    comp.setRefinementsList(Collections.singletonList(refinementType));

    // When
    String createdJson = deSer.serialize(comp, comp2json);

    // the expected result
    Path json = Path.of(RELATIVE_DIR, "WithRefinement.json");
    String expected = readString(json).replaceAll("\\s+", "");

    // Then
    assertEquals(expected, createdJson);
  }

  @Test
  void shouldDeserializeSpec() throws IOException {
    // When
    Path json = Path.of(RELATIVE_DIR, "WithRefinement.json");
    String jsonString = readString(json).replaceAll("\\s+", "");
    ComponentTypeSymbol comp = deSer.deserialize(CompSymbolsMill.globalScope(), jsonString);

    // Then
    assertFalse(comp.isEmptyRefinements(), "Refined component not present");
    assertEquals("RefinementCType", comp.getRefinements(0).printName());
  }


  @Test
  void shouldSerializeInnerComponents() throws IOException {
    // Given
    ComponentTypeSymbol comp = createSimpleComp("Comp");
    comp.getSpannedScope().add(
        CompSymbolsMill.componentTypeSymbolBuilder()
            .setName("inst")
            .setSpannedScope(CompSymbolsMill.scope())
            .build()
    );

    // When
    String createdJson = deSer.serialize(comp, comp2json);

    // the expected result
    Path json = Path.of(RELATIVE_DIR, "WithInner.json");
    String expected = readString(json).replaceAll("\\s+", "");

    // Then
    assertEquals(expected, createdJson);
  }

  @Test
  void shouldDeserializeInnerComponents() throws IOException {
    // When
    Path json = Path.of(RELATIVE_DIR, "WithInner.json");
    String jsonString = readString(json).replaceAll("\\s+", "");
    ComponentTypeSymbol comp = deSer.deserialize(CompSymbolsMill.globalScope(), jsonString);

    // Then
    assertEquals(1, comp.getSpannedScope().getLocalComponentTypeSymbols().size());
    assertAll(
        () -> assertEquals("inst", comp.getSpannedScope().getLocalComponentTypeSymbols().get(0).getName())
    );
  }

  @Test
  void shouldSerializeFields() throws IOException {
    // Given
    ComponentTypeSymbol comp = createSimpleComp("Comp");
    comp.getSpannedScope().add(
        CompSymbolsMill.variableSymbolBuilder()
            .setName("inst")
            .setType(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT))
            .build()
    );

    // When
    String createdJson = deSer.serialize(comp, comp2json);

    // the expected result
    Path json = Path.of(RELATIVE_DIR, "WithField.json");
    String expected = readString(json).replaceAll("\\s+", "");

    // Then
    assertEquals(expected, createdJson);
  }

  @Test
  void shouldDeserializeFields() throws IOException {
    // When
    Path json = Path.of(RELATIVE_DIR, "WithField.json");
    String jsonString = readString(json).replaceAll("\\s+", "");
    ComponentTypeSymbol comp = deSer.deserialize(CompSymbolsMill.globalScope(), jsonString);

    // Then
    assertEquals(1, comp.getFields().size());
    assertAll(
        () -> assertEquals("inst", comp.getFields().get(0).getName())
    );
  }
}
