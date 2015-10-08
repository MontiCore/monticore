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

import static com.google.common.base.Strings.nullToEmpty;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

/**
 * @author Pedram Mir Seyed Nazari
 */
public abstract class CommonSymbol implements Symbol {

  private final String name;
  private String packageName;
  // TODO PN create setter
  private String fullName;

  private Scope enclosingScope;
  
  private ASTNode node;

  private SymbolKind kind;

  private AccessModifier accessModifier = BasicAccessModifier.ABSENT;

  public CommonSymbol(String name, SymbolKind kind) {
    // TODO PN if name is qualified: this.name = simple(name) and this.packageName = qualifier (name)?
    this.name = Log.errorIfNull(name);
    this.kind = Log.errorIfNull(kind);
  }

  // TODO PN pull-up?
  public void setPackageName(String packageName) {
    this.packageName = Log.errorIfNull(packageName);
  }
  
  /**
   * @see Symbol#getName()
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * @see Symbol#getPackageName()
   */
  @Override
  public String getPackageName() {
    if (packageName == null) {
      Optional<ArtifactScope> artifactScope = Scopes.getArtifactScope(getEnclosingScope());

      if (artifactScope.isPresent()) {
        packageName = artifactScope.get().getPackageName();
      }
    }
    return nullToEmpty(packageName);
  }

  // TODO PN pull-up?
  public void setFullName(String fullName) {
    this.fullName = Log.errorIfNull(fullName);
  }

  @Override
  public String getFullName() {
    if (fullName == null) {
      if (enclosingScope == null) {
        // There should not be a symbol that is not defined in any scope. This case should only
        // occur while the symbol is build (by the symbol table creator). So, here the fullName
        // should not be cached yet.
        return name;
      }

      final Deque<String> nameParts = new ArrayDeque<>();
      nameParts.addFirst(name);

      Scope currentScope = enclosingScope;

      // TODO PN FQN = parent.FQN + "." simpleName

      while (currentScope.getEnclosingScope().isPresent()) {
        if (!(currentScope instanceof GlobalScope)) {
		  // TODO PN should we really only consider shadowing scopes?
          if (currentScope.isShadowingScope()) {
            if (currentScope instanceof ArtifactScope) {
              ArtifactScope artifactScope = (ArtifactScope) currentScope;
              if (!artifactScope.getPackageName().isEmpty()) {
			    // TODO PN use this.getPackageName() instead.
                nameParts.addFirst(artifactScope.getPackageName());
              }
            }
            else {
              if (currentScope.getName().isPresent()) {
                nameParts.addFirst(currentScope.getName().get());
              }
              // TODO PN else stop? If one of the enclosing scopes is unnamed,
              //         the full name is same as the simple name.
            }
          }
        }
        currentScope = currentScope.getEnclosingScope().get();
      }

      fullName = Names.getQualifiedName(nameParts);
    }

    return fullName;
  }

  @Override
  public SymbolKind getKind() {
    return kind;
  }
  
  /**
   * @param kind the kind to set
   */
  protected void setKind(SymbolKind kind) {
    this.kind = Log.errorIfNull(kind);
  }

  /**
   * @see Symbol#setAstNode(de.monticore.ast.ASTNode)
   */
  @Override
  public void setAstNode(ASTNode node) {
    this.node = node;
  }
  
  /**
   * @see Symbol#getAstNode()
   */
  @Override
  public Optional<ASTNode> getAstNode() {
    return Optional.ofNullable(node);
  }

  @Override
  public Scope getEnclosingScope() {
    return enclosingScope;
  }

  @Override
  public void setEnclosingScope(MutableScope scope) {
    this.enclosingScope = scope;
  }

  /**
   * @see Symbol#getAccessModifier()
   */
  @Override
  public AccessModifier getAccessModifier() {
    return accessModifier;
  }

  @Override
  public void setAccessModifier(AccessModifier accessModifier) {
    this.accessModifier = accessModifier;
  }

  @Override
  public String toString() {
    // TODO PN do not return full name, since it needs the full information about a symbol.
    return getFullName();
  }

}
