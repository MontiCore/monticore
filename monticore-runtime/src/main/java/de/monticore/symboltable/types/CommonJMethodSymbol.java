/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.symboltable.types;

import static com.google.common.base.Preconditions.checkArgument;
import static de.monticore.symboltable.Symbols.sortSymbolsByPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import de.monticore.symboltable.CommonScopeSpanningSymbol;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.symboltable.types.references.JTypeReference;
import de.se_rwth.commons.logging.Log;

/**
 * @author Pedram Mir Seyed Nazari
 */
public class CommonJMethodSymbol <U extends JTypeSymbol, T extends JTypeReference<? extends U>, S extends JAttributeSymbol>
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
    final Collection<S> resolvedAttributes = getSpannedScope().resolveLocally(CommonJAttributeSymbol.KIND);

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
