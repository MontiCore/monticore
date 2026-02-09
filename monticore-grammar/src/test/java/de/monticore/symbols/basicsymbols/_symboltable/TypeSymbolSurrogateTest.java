/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.basicsymbols._symboltable;

import de.monticore.ast.ASTNode;
import de.monticore.interpreter.MIValue;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.ISymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.symboltable.stereotypes.IStereotypeReference;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.LogStub;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/** Tests {@link TypeSymbolSurrogate} */
public class TypeSymbolSurrogateTest {

  @BeforeEach
  void setUp() {
    LogStub.init();
    BasicSymbolsMill.init();
  }

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
    assertSame(scopeToSet, type.getSpannedScope());
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
    assertSame(type.getSpannedScope(), scope);
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
    assertSame(superClassExpr, superClassCalculated);
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
    assertSame(superClassExpr, type.getSuperClass());
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
    assertArrayEquals(new TypeVarSymbol[]{typeParam}, typeParams.toArray());
  }

  @Test @SuppressWarnings({"EqualsWithItself", "ConstantConditions"})
  void equalsShouldEqualSame() {
    // Given
    TypeSymbolSurrogate surrogate = BasicSymbolsMill.typeSymbolSurrogateBuilder()
            .setName("Type")
            .setEnclosingScope(BasicSymbolsMill.scope())
            .build();

    // When
    boolean result = surrogate.equals(surrogate);

    // Then
    assertTrue(result);
  }

  @Test
  void equalsShouldNotEqualDifferent1() {
    // Given
    TypeSymbolSurrogate surrogate1 = BasicSymbolsMill.typeSymbolSurrogateBuilder()
            .setName("Type1")
            .setEnclosingScope(BasicSymbolsMill.scope())
            .build();

    TypeSymbolSurrogate surrogate2 = BasicSymbolsMill.typeSymbolSurrogateBuilder()
            .setName("Type2")
            .setEnclosingScope(BasicSymbolsMill.scope())
            .build();

    // When
    boolean result = surrogate1.equals(surrogate2);

    // Then
    assertFalse(result);
  }

  @Test
  void equalsShouldNotEqualDifferent2() {
    // Given
    IBasicSymbolsScope scope = BasicSymbolsMill.scope();

    TypeSymbol symbol1 = BasicSymbolsMill.typeSymbolBuilder()
            .setName("Type1")
            .setSpannedScope(BasicSymbolsMill.scope())
            .build();

    scope.add(symbol1);

    TypeSymbolSurrogate surrogate1 = BasicSymbolsMill.typeSymbolSurrogateBuilder()
            .setName("Type1")
            .setEnclosingScope(scope)
            .build();

    TypeSymbol symbol2 = BasicSymbolsMill.typeSymbolBuilder()
            .setName("Type2")
            .setSpannedScope(BasicSymbolsMill.scope())
            .build();

    scope.add(symbol2);

    TypeSymbolSurrogate surrogate2 = BasicSymbolsMill.typeSymbolSurrogateBuilder()
            .setName("Type2")
            .setEnclosingScope(scope)
            .build();

    // When
    boolean result = surrogate1.equals(surrogate2);

    // Then
    assertFalse(result);
  }

  @Test
  void equalsShouldEqualSymbol() {
    // Given
    IBasicSymbolsScope scope = BasicSymbolsMill.scope();

    TypeSymbol symbol = BasicSymbolsMill.typeSymbolBuilder()
            .setName("Type")
            .setSpannedScope(BasicSymbolsMill.scope())
            .build();

    scope.add(symbol);

    TypeSymbolSurrogate surrogate = BasicSymbolsMill.typeSymbolSurrogateBuilder()
            .setName("Type")
            .setEnclosingScope(scope)
            .build();

    // When
    boolean result = surrogate.equals(symbol);

    // Then
    assertTrue(result);
  }

  @Test
  void equalsShouldNotEqualSymbol() {
    // Given
    IBasicSymbolsScope scope = BasicSymbolsMill.scope();

    TypeSymbol symbol = BasicSymbolsMill.typeSymbolBuilder()
            .setName("Type1")
            .setSpannedScope(BasicSymbolsMill.scope())
            .build();

    scope.add(symbol);

    TypeSymbolSurrogate surrogate = BasicSymbolsMill.typeSymbolSurrogateBuilder()
            .setName("Type2")
            .setEnclosingScope(scope)
            .build();

    // When
    boolean result = surrogate.equals(symbol);

    // Then
    assertFalse(result);

    // When
    boolean resultSymmetric = symbol.equals(surrogate);

    // Then
    assertFalse(resultSymmetric);
  }

  @Test
  public void equalsShouldNotEqualAdapted() {
    SymbolMock mock1 = new SymbolMock("Type1");
    SymbolMock mock2 = new SymbolMock("Type2");

    IBasicSymbolsScope scope = new BasicSymbolsScopeWithAdapted(List.of(mock1, mock2));

    // we don't use the builder because it automatically loads the delegate
    TypeSymbolSurrogate surrogate1 = new TypeSymbolSurrogate("Type1");
    surrogate1.setName("Type1");
    surrogate1.setFullName("Type1");
    surrogate1.setEnclosingScope(scope);

    TypeSymbolSurrogate surrogate2 = new TypeSymbolSurrogate("Type2");
    surrogate2.setName("Type2");
    surrogate2.setFullName("Type2");
    surrogate2.setEnclosingScope(scope);


    var result = surrogate1.equals(surrogate2);

    assertFalse(result);
  }

  @Test
  public void equalsShouldEqualAdapted() {
    SymbolMock mock = new SymbolMock("Type1");

    IBasicSymbolsScope scope = new BasicSymbolsScopeWithAdapted(List.of(mock));

    // we don't use the builder because it automatically loads the delegate
    TypeSymbolSurrogate surrogate1 = new TypeSymbolSurrogate("Type1");
    surrogate1.setName("Type1");
    surrogate1.setFullName("Type1");
    surrogate1.setEnclosingScope(scope);

    TypeSymbolSurrogate surrogate2 = new TypeSymbolSurrogate("Type1");
    surrogate2.setName("Type1");
    surrogate2.setFullName("Type1");
    surrogate2.setEnclosingScope(scope);


    var result = surrogate1.equals(surrogate2);

    assertTrue(result);
  }

  private static class SymbolMock implements ISymbol {
    private final String name;

    SymbolMock(String name) {
      this.name = name;
    }
    public SymbolMock getThis() {
      return this;
    }
    public boolean equals (Object obj) {
      if(!(obj instanceof SymbolMock)) {
        return false;
      }
      SymbolMock s1 = getThis();
      SymbolMock s2 = ((SymbolMock) obj).getThis();

      return s1 == s2;
    }

    @Override
    public String getName() {
      return name;
    }
    // Boilerplate
    @Override
    public String getPackageName() {
      return null;
    }
    @Override
    public String getFullName() {
      return null;
    }
    @Override
    public IScope getEnclosingScope() {
      return null;
    }
    @Override
    public void setAccessModifier(AccessModifier accessModifier) {
    }
    @Override
    public Map<IStereotypeReference, Optional<MIValue>> getStereoinfo() {
      return null;
    }
    @Override
    public boolean isPresentAstNode() {
      return false;
    }
    @Override
    public ASTNode getAstNode() {
      return null;
    }
    @Override
    public SourcePosition getSourcePosition() {
      return null;
    }
    @Override
    public void accept(ITraverser visitor) {
    }
  }

  private static class SymbolMock2TypeSymbolAdapter extends TypeSymbol {
    private final SymbolMock adaptee;
    SymbolMock2TypeSymbolAdapter(SymbolMock adaptee) {
      super(adaptee.getName());
      this.adaptee = adaptee;
    }

    public SymbolMock getAdaptee() {
      return adaptee;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof SymbolMock2TypeSymbolAdapter) {
        return getAdaptee().equals(((SymbolMock2TypeSymbolAdapter) obj).getAdaptee());
      }
      else {
        return super.equals(obj);
      }
    }
  }

  private static class BasicSymbolsScopeWithAdapted extends BasicSymbolsScope {
    public BasicSymbolsScopeWithAdapted(List<SymbolMock> adaptees) {
      this.adaptees = adaptees;
    }
    private final List<SymbolMock> adaptees;
    @Override
    public List<TypeSymbol> resolveAdaptedTypeLocallyMany(boolean foundSymbols, String name, AccessModifier modifier, Predicate<TypeSymbol> predicate) {
      return adaptees.stream().filter(symbol -> symbol.getName().equals(name)).map(SymbolMock2TypeSymbolAdapter::new).collect(Collectors.toList());
    }
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
