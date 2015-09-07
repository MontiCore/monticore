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

package de.monticore.symboltable;

import java.util.Optional;

import de.se_rwth.commons.logging.Log;

/**
 * Provides helper methods for {@link de.monticore.symboltable.Scope}.
 *
 * @author Pedram Mir Seyed Nazari
 */
public final class Scopes {

  private Scopes(){
  }

  public static Scope getTopScope(final Scope scope) {
    Log.errorIfNull(scope);

    Scope currentScope = scope;

    while (currentScope.getEnclosingScope().isPresent()) {
      currentScope = currentScope.getEnclosingScope().get();
    }

    return currentScope;
  }

  public static Optional<ArtifactScope> getArtifactScope(final Scope startScope) {
    Log.errorIfNull(startScope);

    if (startScope instanceof ArtifactScope) {
      return Optional.of((ArtifactScope) startScope);
    }

    Scope currentScope = startScope;

    while (currentScope.getEnclosingScope().isPresent()) {
      currentScope = currentScope.getEnclosingScope().get();

      if (currentScope instanceof ArtifactScope) {
        return Optional.of((ArtifactScope)currentScope);
      }
    }

    return Optional.empty();

  }
  
  public static boolean isDescendant(final Scope descendant, final Scope ancestor) {
    Log.errorIfNull(descendant);
    Log.errorIfNull(ancestor);
    
    if (descendant == ancestor) {
      return false;
    }
    
    Optional<? extends Scope> parent = descendant.getEnclosingScope();
    while (parent.isPresent()) {
      if (parent.get() == ancestor) {
        return true;
      }
      parent = parent.get().getEnclosingScope();
    }
    
    return false;
  }
  
  public static Optional<? extends Scope> getFirstShadowingScope(final Scope scope) {
    Log.errorIfNull(scope);

    Optional<? extends Scope> currentScope = Optional.of(scope);
    
    while (currentScope.isPresent()) {
      if (currentScope.get().definesNameSpace()) {
        return currentScope;
      }
      currentScope = currentScope.get().getEnclosingScope();
    }
    
    return Optional.empty();
    
  }

  // TODO PN print whole scope hierarchy
  
}
