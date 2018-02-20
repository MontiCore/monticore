/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.types;

import com.google.common.collect.ImmutableList;
import de.monticore.symboltable.CommonScopeSpanningSymbol;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.symboltable.types.references.JTypeReference;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static de.monticore.symboltable.Symbols.sortSymbolsByPosition;

/**
 *
 * @author Pedram Mir Seyed Nazari
 * @param <T>
 * @param <S>
 */
public abstract class CommonJTypeSymbol <T extends JTypeSymbol, S extends JFieldSymbol, U extends JMethodSymbol, V extends JTypeReference<T>>
    extends CommonScopeSpanningSymbol implements JTypeSymbol {

  private final JAttributeSymbolKind attributeKind;
  private final JMethodSymbolKind methodKind;

  private V superClass;
  private final List<V> interfaces = new ArrayList<>();

  private boolean isAbstract = false;
  private boolean isFinal = false;
  private boolean isInterface = false;
  private boolean isEnum = false;
  private boolean isFormalTypeParameter = false;
  // e.g., inner interface or inner class
  private boolean isInnerType = false;

  protected CommonJTypeSymbol(String name, JTypeSymbolKind typeKind,
      JAttributeSymbolKind attributeKind, JMethodSymbolKind methodKind) {
    super(name, typeKind);

    this.attributeKind = attributeKind;
    this.methodKind = methodKind;
  }

  protected CommonJTypeSymbol(String name) {
    this(name, JTypeSymbol.KIND, JFieldSymbol.KIND, JMethodSymbol.KIND);
  }

  @Override
  protected MutableScope createSpannedScope() {
    return new CommonJTypeScope(Optional.empty());
  }

  @Override
  public boolean isGeneric() {
    return !getFormalTypeParameters().isEmpty();
  }

  public void addFormalTypeParameter(T formalTypeParameter) {
    checkArgument(formalTypeParameter.isFormalTypeParameter());
    getMutableSpannedScope().add(formalTypeParameter);
  }

  @Override
  public List<T> getFormalTypeParameters() {
    final Collection<T> resolvedTypes = getSpannedScope().resolveLocally(T.KIND);
    return resolvedTypes.stream().filter(T::isFormalTypeParameter).collect(Collectors.toList());
  }

  @Override
  public Optional<V> getSuperClass() {
    return Optional.ofNullable(superClass);
  }

  public void setSuperClass(V superClass) {
    this.superClass = superClass;
  }

  @Override
  public List<V> getInterfaces() {
    return ImmutableList.copyOf(interfaces);
  }

  public void addInterface(V superInterface) {
    this.interfaces.add(Log.errorIfNull(superInterface));
  }

  @Override
  public List<V> getSuperTypes() {
    final List<V> superTypes = new ArrayList<>();
    if (getSuperClass().isPresent()) {
      superTypes.add(getSuperClass().get());
    }
    superTypes.addAll(getInterfaces());
    return superTypes;
  }

  public void addField(S attribute) {
    getMutableSpannedScope().add(Log.errorIfNull(attribute));
  }

  @Override
  public List<S> getFields() {
    return sortSymbolsByPosition(getSpannedScope().resolveLocally(attributeKind));
  }

  @Override
  public Optional<S> getField(String attributeName) {
    checkArgument(!isNullOrEmpty(attributeName));

    return getSpannedScope().resolveLocally(attributeName, attributeKind);
  }

  public void addMethod(U method) {
    Log.errorIfNull(method);
    checkArgument(!method.isConstructor());

    getMutableSpannedScope().add(method);
  }

  @Override
  public List<U> getMethods() {
    final Collection<U> resolvedMethods = getSpannedScope().resolveLocally(methodKind);

    final List<U> methods = sortSymbolsByPosition(resolvedMethods.stream().filter(method -> !method.isConstructor()).collect(Collectors.toList()));

    return methods;
  }

  @Override
  public Optional<U> getMethod(String methodName) {
    checkArgument(!isNullOrEmpty(methodName));

    Optional<U> method = getSpannedScope().resolveLocally(methodName, methodKind);
    if (method.isPresent() && !method.get().isConstructor()) {
      return method;
    }
    return Optional.empty();
  }

  public void addConstructor(U constructor) {
    Log.errorIfNull(constructor);
    checkArgument(constructor.isConstructor());

    getMutableSpannedScope().add(constructor);
  }

  @Override
  public List<U> getConstructors() {
    final Collection<U> resolvedMethods = getSpannedScope().resolveLocally(methodKind);

    final List<U> constructors = sortSymbolsByPosition(resolvedMethods.stream().filter(U::isConstructor).collect(Collectors.toList()));

    return constructors;
  }

  public void addInnerType(T innerType) {
    Log.errorIfNull(innerType);

    getMutableSpannedScope().add(innerType);
  }

  @Override
  public List<T> getInnerTypes() {
    return sortSymbolsByPosition(getSpannedScope().resolveLocally(getKind()));
  }

  @Override
  public Optional<T> getInnerType(String innerTypeName) {
    checkArgument(!isNullOrEmpty(innerTypeName));

    return getSpannedScope().resolveLocally(innerTypeName, getKind());
  }



  public void setAbstract(boolean isAbstract) {
    this.isAbstract = isAbstract;
  }

  @Override
  public boolean isAbstract() {
    return isAbstract;
  }

  public void setFinal(boolean isFinal) {
    this.isFinal = isFinal;
  }

  @Override
  public boolean isFinal() {
    return isFinal;
  }

  public void setInterface(boolean isInterface) {
    this.isInterface = isInterface;
  }

  @Override
  public boolean isInterface() {
    return isInterface;
  }

  public void setEnum(boolean isEnum) {
    this.isEnum = isEnum;
  }

  @Override
  public boolean isEnum() {
    return isEnum;
  }

  @Override
  public boolean isClass() {
    return !isInterface() && !isEnum();
  }

  public void setPrivate() {
    setAccessModifier(BasicAccessModifier.PRIVATE);
  }

  public void setProtected() {
    setAccessModifier(BasicAccessModifier.PROTECTED);
  }

  public void setPublic() {
    setAccessModifier(BasicAccessModifier.PUBLIC);
  }

  @Override
  public boolean isPrivate() {
    return getAccessModifier().equals(BasicAccessModifier.PRIVATE);
  }

  @Override
  public boolean isProtected() {
    return getAccessModifier().equals(BasicAccessModifier.PROTECTED);
  }

  @Override
  public boolean isPublic() {
    return getAccessModifier().equals(BasicAccessModifier.PUBLIC);
  }

  public void setInnerType(boolean innerType) {
    isInnerType = innerType;
  }

  @Override
  public boolean isInnerType() {
    return isInnerType;
  }

  /**
   * @param formalTypeParameter true, if this type itself is a formal type parameter
   */
  public void setFormalTypeParameter(boolean formalTypeParameter) {
    this.isFormalTypeParameter = formalTypeParameter;
  }

  @Override
  public boolean isFormalTypeParameter() {
    return isFormalTypeParameter;
  }
}
