/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.types;

import com.google.common.collect.ImmutableList;
import de.monticore.symboltable.CommonScopeSpanningSymbol;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.symboltable.types.references.JTypeReference;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static de.monticore.symboltable.Symbols.sortSymbolsByPosition;

/**
 * @author Pedram Mir Seyed Nazari
 */
public abstract class CommonJMethodSymbol <U extends JTypeSymbol, T extends JTypeReference<? extends U>, S extends JFieldSymbol>
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
    this.returnType = Log.errorIfNull(type);
  }

  @Override
  public List<S> getParameters() {
    final Collection<S> resolvedAttributes = getSpannedScope().resolveLocally(S.KIND);

    final List<S> parameters = sortSymbolsByPosition(resolvedAttributes.stream().filter(S::isParameter).collect(Collectors.toList()));

    return parameters;
  }

  public void addParameter(S paramType) {
    Log.errorIfNull(paramType);
    checkArgument(paramType.isParameter(), "Only parameters can be added.");

    getMutableSpannedScope().add(paramType);
  }

  public void addFormalTypeParameter(U formalTypeParameter) {
    checkArgument(formalTypeParameter.isFormalTypeParameter());
    getMutableSpannedScope().add(formalTypeParameter);
  }

  @Override
  public List<U> getFormalTypeParameters() {
    final Collection<U> resolvedTypes = getSpannedScope().resolveLocally(U.KIND);
    return resolvedTypes.stream().filter(U::isFormalTypeParameter).collect(Collectors.toList());
  }

  @Override
  public List<T> getExceptions() {
    return ImmutableList.copyOf(exceptions);
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

}
