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
    if ((packageName == null)) {
      Optional<? extends Scope> optCurrentScope = Optional.of(enclosingScope);

      while (optCurrentScope.isPresent()) {
        final Scope currentScope = optCurrentScope.get();
        if (currentScope.isSpannedBySymbol()) {
          // If one of the enclosing scope(s) is spanned by a symbol, take its
          // package name. This check is important, since the package name of the
          // enclosing symbol might be set manually.
          packageName = currentScope.getSpanningSymbol().get().getPackageName();
          break;
        }
        else if (currentScope instanceof ArtifactScope) {
            packageName = ((ArtifactScope)currentScope).getPackageName();
          }

        optCurrentScope = currentScope.getEnclosingScope();
      }
    }

    return nullToEmpty(packageName);
  }

  // TODO PN pull-up?
  public void setFullName(String fullName) {
    this.fullName = Log.errorIfNull(fullName);
  }

  @Override
  /**
   * @return the full name of a symbol. For example, the full name of a state symbol <code>s</code>
   * in a state chart <code>p.q.SC</code> is <code>p.q.SC.s</code>.
   *
   * By default, this method determines the full name dynamically via
   * {@link #determineFullName()} and caches the value. If the name should not
   * be cached, and hence, calculated everytime this method is invoked, override
   * it and directly delegate to {@link #determineFullName()}.
   *
   * @see #getPackageName()
   */
  public String getFullName() {
    if (fullName == null) {
      fullName = determineFullName();
    }

    return fullName;
  }

  /**
   * Determines <b>dynamically</b>t he full name of the symbol.
   * @return the full name of the symbol determined dynamically
   */
  protected String determineFullName() {
    if (enclosingScope == null) {
      // There should not be a symbol that is not defined in any scope. This case should only
      // occur while the symbol is build (by the symbol table creator). So, here the fullName
      // should not be cached yet.
      return name;
    }

    final Deque<String> nameParts = new ArrayDeque<>();
    nameParts.addFirst(name);

    Optional<? extends Scope> optCurrentScope = Optional.of(enclosingScope);

    while (optCurrentScope.isPresent()) {
      final Scope currentScope = optCurrentScope.get();
      if (currentScope.isSpannedBySymbol()) {
        // If one of the enclosing scope(s) is spanned by a symbol, the full name
        // of that symbol is the missing prefix, and hence, the calculation
        // ends here. This check is important, since the full name of the enclosing
        // symbol might be set manually.
        nameParts.addFirst(currentScope.getSpanningSymbol().get().getFullName());
        break;
      }

      if (!(currentScope instanceof GlobalScope)) {
          if (currentScope instanceof ArtifactScope) {
            // We have reached the artifact scope. Get the package name from the
            // symbol itself, since it might be set manually.
            if (!getPackageName().isEmpty()) {
              nameParts.addFirst(getPackageName());
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
      optCurrentScope = currentScope.getEnclosingScope();
    }

    return Names.getQualifiedName(nameParts);
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
    return getName();
  }

}
