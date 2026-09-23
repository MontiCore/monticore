/* (c) https://github.com/MontiCore/monticore */
package de.monticore.suppliedtypes;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.suppliedtypes.suppliedtypes.SuppliedTypesMill;
import de.monticore.suppliedtypes.suppliedtypes._symboltable.HolderSymbol;
import de.monticore.suppliedtypes.suppliedtypes._symboltable.HolderSymbolBuilder;
import de.monticore.suppliedtypes.suppliedtypes._symboltable.ISuppliedTypesScope;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(SuppliedTypesMill.class)
public class SuppliedTypesTest {

  private SymTypeExpression intType;

  private SymTypeExpression booleanType;

  @BeforeEach
  public void init() {
    BasicSymbolsMill.initializePrimitives();
    intType = SymTypeExpressionFactory.createPrimitive("int");
    booleanType = SymTypeExpressionFactory.createPrimitive("boolean");
  }

  @Test
  public void symbolMandatoryType() {
    HolderSymbol symbol = new HolderSymbol("holder");

    // default: no value set yet
    assertNull(symbol.getMandatoryType());

    symbol.setMandatoryType(intType);
    assertSame(intType, symbol.getMandatoryType());
    assertSame(intType, symbol.getMandatoryTypeSupplier().get());

    symbol.setMandatoryTypeSupplier(() -> booleanType);
    assertSame(booleanType, symbol.getMandatoryType());

    // getXSupplier() must never leak the internal wrapper: it returns exactly what was set
    Supplier<SymTypeExpression> plainSupplier = () -> intType;
    symbol.setMandatoryTypeSupplier(plainSupplier);
    assertSame(plainSupplier, symbol.getMandatoryTypeSupplier());
  }

  @Test
  public void symbolListType() {
    HolderSymbol symbol = new HolderSymbol("holder");

    // default: an empty, non-null list
    assertNotNull(symbol.getListTypeList());
    assertTrue(symbol.getListTypeList().isEmpty());

    List<SymTypeExpression> values = List.of(intType, booleanType);
    symbol.setListTypeList(values);
    assertEquals(values, symbol.getListTypeList());
    assertEquals(values, symbol.getListTypeListSupplier().get());

    symbol.setListTypeListSupplier(() -> values);
    assertEquals(values, symbol.getListTypeList());

    // getXSupplier() it returns exactly what was set
    Supplier<List<SymTypeExpression>> plainSupplier = () -> values;
    symbol.setListTypeListSupplier(plainSupplier);
    assertSame(plainSupplier, symbol.getListTypeListSupplier());
  }

  @Test
  public void symbolOptType() {
    HolderSymbol symbol = new HolderSymbol("holder");

    // default: absent
    assertFalse(symbol.isPresentOptType());
    assertNull(symbol.getOptType());

    symbol.setOptType(booleanType);
    assertTrue(symbol.isPresentOptType());
    assertSame(booleanType, symbol.getOptType());
    assertEquals(java.util.Optional.of(booleanType), symbol.getOptTypeSupplier().get());

    // removing value
    symbol.setOptTypeAbsent();
    assertFalse(symbol.isPresentOptType());
    assertNull(symbol.getOptType());


    symbol.setOptTypeSupplier(() -> Optional.of(intType));
    assertTrue(symbol.isPresentOptType());
    assertSame(intType, symbol.getOptType());

      // getXSupplier() it returns exactly what was set
    Supplier<Optional<SymTypeExpression>> plainSupplier = () -> Optional.of(booleanType);
    symbol.setOptTypeSupplier(plainSupplier);
    assertSame(plainSupplier, symbol.getOptTypeSupplier());
  }

  @Test
  public void scopeMandatoryType() {
    ISuppliedTypesScope scope = SuppliedTypesMill.scope();

    // default: no value set yet
    assertNull(scope.getMandatoryType());

    scope.setMandatoryType(intType);
    assertSame(intType, scope.getMandatoryType());
    assertSame(intType, scope.getMandatoryTypeSupplier().get());

    scope.setMandatoryTypeSupplier(() -> booleanType);
    assertSame(booleanType, scope.getMandatoryType());

    // getXSupplier() must never leak the internal wrapper: it returns exactly what was set
    Supplier<SymTypeExpression> plainSupplier = () -> intType;
    scope.setMandatoryTypeSupplier(plainSupplier);
    assertSame(plainSupplier, scope.getMandatoryTypeSupplier());
  }

  @Test
  public void scopeListType() {
    ISuppliedTypesScope scope = SuppliedTypesMill.scope();

    // default: an empty, non-null list
    assertNotNull(scope.getListTypeList());
    assertTrue(scope.getListTypeList().isEmpty());

    List<SymTypeExpression> values = List.of(intType, booleanType);
    scope.setListTypeList(values);
    assertEquals(values, scope.getListTypeList());
    assertEquals(values, scope.getListTypeListSupplier().get());

    scope.setListTypeListSupplier(() -> values);
    assertEquals(values, scope.getListTypeList());

      // getXSupplier() it returns exactly what was set
    Supplier<List<SymTypeExpression>> plainSupplier = () -> values;
    scope.setListTypeListSupplier(plainSupplier);
    assertSame(plainSupplier, scope.getListTypeListSupplier());
  }

  @Test
  public void scopeOptType() {
    ISuppliedTypesScope scope = SuppliedTypesMill.scope();

    // default: absent
    assertFalse(scope.isPresentOptType());
    assertNull(scope.getOptType());

    scope.setOptType(booleanType);
    assertTrue(scope.isPresentOptType());
    assertSame(booleanType, scope.getOptType());
    assertEquals(java.util.Optional.of(booleanType), scope.getOptTypeSupplier().get());

    // removing value
    scope.setOptTypeAbsent();
    assertFalse(scope.isPresentOptType());
    assertNull(scope.getOptType());


    scope.setOptTypeSupplier(() -> Optional.of(intType));
    assertTrue(scope.isPresentOptType());
    assertSame(intType, scope.getOptType());

    // getXSupplier() it returns exactly what was set
    Supplier<Optional<SymTypeExpression>> plainSupplier = () -> Optional.of(booleanType);
    scope.setOptTypeSupplier(plainSupplier);
    assertSame(plainSupplier, scope.getOptTypeSupplier());
  }

  @Test
  public void builderMandatoryType() {
    HolderSymbolBuilder builder = SuppliedTypesMill.holderSymbolBuilder();

    // default: no value set yet
    assertNull(builder.getMandatoryType());

    builder.setMandatoryType(intType);
    assertSame(intType, builder.getMandatoryType());
    assertSame(intType, builder.getMandatoryTypeSupplier().get());

    builder.setMandatoryTypeSupplier(() -> booleanType);
    assertSame(booleanType, builder.getMandatoryType());

    // getXSupplier() it returns exactly what was set
    Supplier<SymTypeExpression> plainSupplier = () -> intType;
    builder.setMandatoryTypeSupplier(plainSupplier);
    assertSame(plainSupplier, builder.getMandatoryTypeSupplier());
  }

  @Test
  public void builderListType() {
    HolderSymbolBuilder builder = SuppliedTypesMill.holderSymbolBuilder();

    // default: an empty, non-null list
    assertNotNull(builder.getListTypeList());
    assertTrue(builder.getListTypeList().isEmpty());

    List<SymTypeExpression> values = List.of(intType, booleanType);
    builder.setListTypeList(values);
    assertEquals(values, builder.getListTypeList());
    assertEquals(values, builder.getListTypeListSupplier().get());

    builder.setListTypeListSupplier(() -> values);
    assertEquals(values, builder.getListTypeList());

    // getXSupplier() it returns exactly what was set
    Supplier<List<SymTypeExpression>> plainSupplier = () -> values;
    builder.setListTypeListSupplier(plainSupplier);
    assertSame(plainSupplier, builder.getListTypeListSupplier());
  }

  @Test
  public void builderOptType() {
    HolderSymbolBuilder builder = SuppliedTypesMill.holderSymbolBuilder();

    // default: absent
    assertFalse(builder.isPresentOptType());
    assertNull(builder.getOptType());

    builder.setOptType(booleanType);
    assertTrue(builder.isPresentOptType());
    assertSame(booleanType, builder.getOptType());
    assertEquals(Optional.of(booleanType), builder.getOptTypeSupplier().get());

    // removing value
    builder.setOptTypeAbsent();
    assertFalse(builder.isPresentOptType());
    assertNull(builder.getOptType());

    builder.setOptTypeSupplier(() -> Optional.of(intType));
    assertTrue(builder.isPresentOptType());
    assertSame(intType, builder.getOptType());

    // getXSupplier() it returns exactly what was set
    Supplier<Optional<SymTypeExpression>> plainSupplier = () -> Optional.of(booleanType);
    builder.setOptTypeSupplier(plainSupplier);
    assertSame(plainSupplier, builder.getOptTypeSupplier());
  }

  @Test
  public void builderDefaultsPropagateToTheBuiltSymbol() {
    HolderSymbol symbol = SuppliedTypesMill.holderSymbolBuilder()
        .setName("holder")
        .build();

    assertNull(symbol.getMandatoryType());
    assertNotNull(symbol.getListTypeList());
    assertTrue(symbol.getListTypeList().isEmpty());
    assertFalse(symbol.isPresentOptType());
  }
}
