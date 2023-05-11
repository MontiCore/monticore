/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbolTOP;
import de.monticore.symbols.compsymbols._symboltable.ComponentSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represents generic component with filled type parameters. E.g., a {@code TypeExprOfGenericComponent} can
 * represent generic component usages, such as, {@code MyComp<Person>} and {@code OtherComp<List<T>>}.
 */
public class KindOfGenericComponent extends CompKindExpression {

  protected final ImmutableList<SymTypeExpression> typeArguments;

  private ImmutableMap<TypeVarSymbol, SymTypeExpression> typeVarBindingsAsMap;

  public KindOfGenericComponent(@NonNull ComponentSymbol component,
                                @NonNull List<SymTypeExpression> typeArguments) {
    super(component);

    this.typeArguments = ImmutableList.copyOf(Preconditions.checkNotNull(typeArguments));
  }

  public ImmutableMap<TypeVarSymbol, SymTypeExpression> getTypeVarBindings() {
    if (typeVarBindingsAsMap == null) {
      this.typeVarBindingsAsMap = calcTypeVarBindingsAsMap();
    }

    return this.typeVarBindingsAsMap;
  }

  private ImmutableMap<TypeVarSymbol, SymTypeExpression> calcTypeVarBindingsAsMap() {
    ImmutableMap.Builder<TypeVarSymbol, SymTypeExpression> typeVarBindingBuilder = ImmutableMap.builder();

    for (int i = 0; i < typeArguments.size(); i++) {
      // We deal with the wrong number of parameters through cocos
      List<TypeVarSymbol> typeParams = this.getTypeInfo().getTypeParameters();
      if (i < typeParams.size() && typeArguments.get(i) != null) {
        TypeVarSymbol typeParam = this.getTypeInfo().getTypeParameters().get(i);
        typeVarBindingBuilder.put(typeParam, typeArguments.get(i));
      }
    }

    return typeVarBindingBuilder.build();
  }

  @Override
  public String printName() {
    StringBuilder builder = new StringBuilder(this.getTypeInfo().getName()).append('<');
    for (int i = 0; i < this.getTypeBindingsAsList().size(); i++) {
      builder.append(this.getTypeBindingsAsList().get(i).print());
      if (i < this.getTypeBindingsAsList().size() - 1) {
        builder.append(',');
      }
    }
    return builder.append('>').toString();
  }

  @Override
  public String printFullName() {
    StringBuilder builder = new StringBuilder(this.getTypeInfo().getFullName()).append('<');
    for (int i = 0; i < this.getTypeBindingsAsList().size(); i++) {
      builder.append(this.getTypeBindingsAsList().get(i).printFullName());
      if (i < this.getTypeBindingsAsList().size() - 1) {
        builder.append(',');
      }
    }
    return builder.append('>').toString();
  }

  @Override
  public List<CompKindExpression> getSuperComponents() {
    return this.getTypeInfo().getSuperComponentsList();
  }

  @Override
  public Optional<SymTypeExpression> getTypeOfPort(@NonNull String name) {
    Preconditions.checkNotNull(name);

    Optional<PortSymbol> port = this.getTypeInfo().getPort(name, false);

    if (port.isPresent()) {
      Optional<SymTypeExpression> unboundPortType
        = port.filter(PortSymbol::isTypePresent).map(PortSymbol::getType);
      if (unboundPortType.isEmpty()) return Optional.empty();
      return this.createBoundTypeExpression(unboundPortType.get());
    } else {
      for (CompKindExpression superComponent : this.getSuperComponents()) {
        Optional<SymTypeExpression> portOfSuper = superComponent.getTypeOfPort(name);
        if (portOfSuper.isPresent()) return portOfSuper;
      }
      return Optional.empty();
    }
  }

  @Override
  public Optional<SymTypeExpression> getTypeOfParameter(@NonNull String parameterName) {
    Preconditions.checkNotNull(parameterName);

    SymTypeExpression unboundParamType = this.getTypeInfo()
      .getParameter(parameterName)
      .map(VariableSymbolTOP::getType)
      .orElseThrow(NoSuchElementException::new);

    return this.createBoundTypeExpression(unboundParamType);
  }

  public Optional<SymTypeExpression> getTypeBindingFor(@NonNull TypeVarSymbol typeVar) {
    Preconditions.checkNotNull(typeVar);
    return Optional.ofNullable(this.getTypeVarBindings().get(typeVar));
  }

  public Optional<SymTypeExpression> getTypeBindingFor(@NonNull String typeVarName) {
    Preconditions.checkNotNull(typeVarName);
    Optional<TypeVarSymbol> searchedTypeVar = this.getTypeVarBindings().keySet().stream()
      .filter(tvar -> tvar.getName().equals(typeVarName))
      .findFirst();
    return searchedTypeVar.map(typeVarSymbol -> this.getTypeVarBindings().get(typeVarSymbol));
  }

  public ImmutableList<SymTypeExpression> getTypeBindingsAsList() {
    return this.typeArguments;
  }

  /**
   * If this {@code TypeExprOfGenericComponent} references type variables (e.g. the type {@code Comp<List<T>>}) and you
   * provide a {@link SymTypeExpression} mapping for that type variable, this method returns a {@code
   * TypeExprOfGenericComponent} where that type variable has been reset by the SymTypeExpression you provided. E.g., if
   * you provide the mapping {@code T -> Person} for the above given example component, then this method would return
   * {@code Comp<List<Person>>}. If you provide mappings for type variables that do not appear in the component type
   * expression, then these will be ignored.
   */
  public KindOfGenericComponent bindTypeParameter(
    @NonNull Map<TypeVarSymbol, SymTypeExpression> newTypeVarBindings) {
    Preconditions.checkNotNull(newTypeVarBindings);

    List<SymTypeExpression> newBindings = new ArrayList<>();
    for(SymTypeExpression typeArg : this.typeArguments) {
      SymTypeExpression newTypeArg;
      if(typeArg.isTypeVariable() && newTypeVarBindings.containsKey(((TypeVarSymbol) typeArg.getTypeInfo()))) {
        newTypeArg = newTypeVarBindings.get((TypeVarSymbol) typeArg.getTypeInfo());
      } else {
        newTypeArg = typeArg.deepClone();
        newTypeArg.replaceTypeVariables(newTypeVarBindings);
      }
      newBindings.add(newTypeArg);
    }
    return new KindOfGenericComponent(this.getTypeInfo(), newBindings);
  }

  @Override
  public KindOfGenericComponent deepClone(@NonNull ComponentSymbol compTypeSymbol) {
    List<SymTypeExpression> clonedBindings = this.getTypeBindingsAsList().stream()
      .map(SymTypeExpression::deepClone)
      .collect(Collectors.toList());
    return new KindOfGenericComponent(compTypeSymbol, clonedBindings);
  }

  @Override
  public boolean deepEquals(@NonNull CompKindExpression compSymType) {
    Preconditions.checkNotNull(compSymType);

    if(!(compSymType instanceof KindOfGenericComponent)) {
      return false;
    }
    KindOfGenericComponent otherCompExpr = (KindOfGenericComponent) compSymType;

    boolean equal = this.getTypeInfo().equals(compSymType.getTypeInfo());
    equal &= this.getTypeBindingsAsList().size() == otherCompExpr.getTypeBindingsAsList().size();
    for(int i = 0; i < this.getTypeBindingsAsList().size(); i++) {
      equal &= this.getTypeBindingsAsList().get(i).deepEquals(otherCompExpr.getTypeBindingsAsList().get(i));
    }

    return equal;
  }

  protected Optional<SymTypeExpression> createBoundTypeExpression(@NonNull SymTypeExpression unboundTypeExpr) {
    if (unboundTypeExpr.isPrimitive() || unboundTypeExpr.isObjectType() || unboundTypeExpr.isNullType()) {
      return Optional.of(unboundTypeExpr);
    } else if (unboundTypeExpr.isTypeVariable()) {
      return this.getTypeBindingFor(unboundTypeExpr.getTypeInfo().getName());
    } else {
      SymTypeExpression boundSymType = unboundTypeExpr.deepClone();
      boundSymType.replaceTypeVariables(this.getTypeVarBindings());
      return Optional.of(boundSymType);
    }
  }
}
