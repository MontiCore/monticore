/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.types;

import de.monticore.symboltable.CommonScopeSpanningSymbol;
import de.monticore.symboltable.types.references.JTypeReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.copyOf;
import static de.monticore.symboltable.Symbols.sortSymbolsByPosition;
import static de.monticore.symboltable.modifiers.BasicAccessModifier.*;
import static de.se_rwth.commons.logging.Log.errorIfNull;
import static java.util.stream.Collectors.toList;

public abstract class CommonJMethodSymbol<U extends JTypeSymbol, T extends JTypeReference<? extends U>, S extends JFieldSymbol>
        extends CommonScopeSpanningSymbol implements JMethodSymbol {

  private boolean isAbstract = false;
  private boolean isStatic = false;
  private boolean isFinal = false;
  private boolean isConstructor = false;
  private boolean isEllipsisParameterMethod = false;

  private T returnType;
  private List<T> exceptions = new ArrayList<>();

  public CommonJMethodSymbol(String name, JMethodSymbolKind kind) {
    super(name, kind);
  }

  @Override
  public T getReturnType() {
    return returnType;
  }

  public void setReturnType(T type) {
    this.returnType = errorIfNull(type);
  }

  @Override
  public List<S> getParameters() {
    final Collection<S> resolvedAttributes = getSpannedScope().resolveLocally(S.KIND);

    return sortSymbolsByPosition(resolvedAttributes.stream().filter(S::isParameter).collect(toList()));
  }

  public void addParameter(S paramType) {
    errorIfNull(paramType);
    checkArgument(paramType.isParameter(), "Only parameters can be added.");

    getSpannedScope().add(paramType);
  }

  public void addFormalTypeParameter(U formalTypeParameter) {
    checkArgument(formalTypeParameter.isFormalTypeParameter());
    getSpannedScope().add(formalTypeParameter);
  }

  @Override
  public List<U> getFormalTypeParameters() {
    final Collection<U> resolvedTypes = getSpannedScope().resolveLocally(U.KIND);
    return resolvedTypes.stream().filter(U::isFormalTypeParameter).collect(toList());
  }

  @Override
  public List<T> getExceptions() {
    return copyOf(exceptions);
  }

  public void setExceptions(List<T> exceptions) {
    this.exceptions = exceptions;
  }

  public void addException(T exception) {
    this.exceptions.add(exception);
  }

  public void setAbstract(boolean isAbstract) {
    this.isAbstract = isAbstract;
  }

  @Override
  public boolean isAbstract() {
    return isAbstract;
  }

  public void setStatic(boolean isStatic) {
    this.isStatic = isStatic;
  }

  @Override
  public boolean isStatic() {
    return isStatic;
  }

  public void setConstructor(boolean isConstructor) {
    this.isConstructor = isConstructor;
  }

  @Override
  public boolean isConstructor() {
    return isConstructor;
  }

  @Override
  public boolean isFinal() {
    return isFinal;
  }

  public void setFinal(boolean isFinal) {
    this.isFinal = isFinal;
  }

  @Override
  public boolean isEllipsisParameterMethod() {
    return isEllipsisParameterMethod;
  }

  public void setEllipsisParameterMethod(boolean isEllipsisParameterMethod) {
    this.isEllipsisParameterMethod = isEllipsisParameterMethod;
  }

  public void setPrivate() {
    setAccessModifier(PRIVATE);
  }

  public void setProtected() {
    setAccessModifier(PROTECTED);
  }

  public void setPublic() {
    setAccessModifier(PUBLIC);
  }

  @Override
  public boolean isPrivate() {
    return getAccessModifier().equals(PRIVATE);
  }

  @Override
  public boolean isProtected() {
    return getAccessModifier().equals(PROTECTED);
  }

  @Override
  public boolean isPublic() {
    return getAccessModifier().equals(PUBLIC);
  }

}
