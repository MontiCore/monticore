/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.basicsymbols._symboltable;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/** Tests {@link TypeSymbolSurrogate} */
public class TypeSymbolSurrogateTest {

  @Test
  public void setSpannedScopeShouldSkipSurrogate() {
    // Given
    Map.Entry<TypeSymbol, TypeSymbolSurrogate> pair = createTypeWithSurrogate("Type");
    TypeSymbol type = pair.getKey();
    TypeSymbolSurrogate surrogate = pair.getValue();

    IBasicSymbolsScope scopeToSet = BasicSymbolsMill.scope();

    // When
    surrogate.setSpannedScope(scopeToSet);

    // Then
    Assertions.assertSame(scopeToSet, type.getSpannedScope());
  }

  @Test
  public void getSpannedScopeShouldSkipSurrogate() {
    // Given
    Map.Entry<TypeSymbol, TypeSymbolSurrogate> pair = createTypeWithSurrogate("Type");
    TypeSymbol type = pair.getKey();
    TypeSymbolSurrogate surrogate = pair.getValue();

    // When
    IBasicSymbolsScope scope = surrogate.getSpannedScope();

    // Then
    Assertions.assertSame(type.getSpannedScope(), scope);
  }

  @Test
  void getSuperClassShouldSkipSurrogate() {
    // Given
    Map.Entry<TypeSymbol, TypeSymbolSurrogate> pair = createTypeWithSurrogate("Type");
    TypeSymbol type = pair.getKey();
    TypeSymbolSurrogate surrogate = pair.getValue();

    TypeSymbol superClass = createTypeWithSurrogate("SuperClass").getKey();
    SymTypeExpression superClassExpr = SymTypeExpressionFactory.createFromSymbol(superClass);
    type.setSuperTypesList(Collections.singletonList(superClassExpr));

    // When
    SymTypeExpression superClassCalculated = surrogate.getSuperClass();

    // Then
    Assertions.assertSame(superClassExpr, superClassCalculated);
  }


  @Test
  void setSuperClassShouldSkipSurrogate() {
    // Given
    Map.Entry<TypeSymbol, TypeSymbolSurrogate> pair = createTypeWithSurrogate("Type");
    TypeSymbol type = pair.getKey();
    TypeSymbolSurrogate surrogate = pair.getValue();

    TypeSymbol superClass = createTypeWithSurrogate("SuperClass").getKey();
    SymTypeExpression superClassExpr = SymTypeExpressionFactory.createFromSymbol(superClass);

    // When
    surrogate.setSuperTypesList(Collections.singletonList(superClassExpr));

    // Then
    Assertions.assertSame(superClassExpr, type.getSuperClass());
  }

  @Test
  void getTypeParameterListShouldSkipSurrogate() {
    // Given
    Map.Entry<TypeSymbol, TypeSymbolSurrogate> pair = createTypeWithSurrogate("Type");
    TypeSymbol type = pair.getKey();
    TypeSymbolSurrogate surrogate = pair.getValue();

    TypeVarSymbol typeParam = addTypeParameterTo(type, "T");

    // When
    List<TypeVarSymbol> typeParams = surrogate.getTypeParameterList();

    // Then
    Assertions.assertArrayEquals(new TypeVarSymbol[]{typeParam}, typeParams.toArray());
  }

  /**
   * Adds a type parameter to the type.
   *
   * @return the created type parameter
   */
  protected TypeVarSymbol addTypeParameterTo(@NonNull TypeSymbol type,
                                             @NonNull String typeParamName) {

    TypeVarSymbol typeVar = BasicSymbolsMill
        .typeVarSymbolBuilder()
        .setName(typeParamName)
        .setAccessModifier(BasicAccessModifier.PUBLIC)
        .build();

    type.getSpannedScope().add(typeVar);

    return typeVar;
  }

  protected static Map.Entry<TypeSymbol, TypeSymbolSurrogate> createTypeWithSurrogate(
      @NonNull String compName) {

    IBasicSymbolsScope commonScope = BasicSymbolsMill.scope();

    TypeSymbol symbol = BasicSymbolsMill.typeSymbolBuilder()
        .setName(compName)
        .setSpannedScope(BasicSymbolsMill.scope())
        .build();

    commonScope.add(symbol);

    TypeSymbolSurrogate surrogate = BasicSymbolsMill.typeSymbolSurrogateBuilder()
        .setName(compName)
        .setEnclosingScope(commonScope)
        .build();

    return Map.entry(symbol, surrogate);
  }

}
