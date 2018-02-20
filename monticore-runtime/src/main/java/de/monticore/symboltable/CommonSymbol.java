/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

/**
 * @author Pedram Mir Seyed Nazari
 */
public abstract class CommonSymbol implements Symbol {

  private final String name;
  private String packageName;
  private String fullName;

  private Scope enclosingScope;
  
  private ASTNode node;

  private SymbolKind kind;

  private AccessModifier accessModifier = AccessModifier.ALL_INCLUSION;

  public CommonSymbol(String name, SymbolKind kind) {
    this.name = Log.errorIfNull(name);
    this.kind = Log.errorIfNull(kind);
  }

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
   * Returns the package of this symbol. All symbols within  an artifact usually
   * have the same package name. For example, the state chart <code>p.q.SC</code>
   * and its containing states all have the package <code>p.q</code>.
   *
   * By default, this method determines the package name dynamically via
   * {@link #determinePackageName()} } and caches the value. If the package name
   * should not be cached, and hence, calculated every time this method is invoked,
   * override this method and directly delegate to {@link #determinePackageName()}.
   *
   * @see #getFullName()
   *
   */
  @Override
  public String getPackageName() {
    if (packageName == null) {
      packageName = determinePackageName();
    }

    return packageName;
  }

  /**
   * Determines <b>dynamically</b> the package name of the symbol.
   * @return the package name of the symbol determined dynamically
   */
  protected String determinePackageName() {
    Optional<? extends Scope> optCurrentScope = Optional.ofNullable(enclosingScope);

    while (optCurrentScope.isPresent()) {
      final Scope currentScope = optCurrentScope.get();
      if (currentScope.isSpannedBySymbol()) {
        // If one of the enclosing scope(s) is spanned by a symbol, take its
        // package name. This check is important, since the package name of the
        // enclosing symbol might be set manually.
        return currentScope.getSpanningSymbol().get().getPackageName();
      }
      else if (currentScope instanceof ArtifactScope) {
          return ((ArtifactScope)currentScope).getPackageName();
        }

      optCurrentScope = currentScope.getEnclosingScope();
    }

    return "";
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  @Override
  /**
   * @return the full name of a symbol. For example, the full name of a state symbol <code>s</code>
   * in a state chart <code>p.q.SC</code> is <code>p.q.SC.s</code>.
   *
   * By default, this method determines the full name dynamically via
   * {@link #determineFullName()} and caches the value. If the full name should not
   * be cached, and hence, calculated every time this method is invoked, override
   * this method and directly delegate to {@link #determineFullName()}.
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
   * Determines <b>dynamically</b> the full name of the symbol.
   * @return the full name of the symbol determined dynamically
   */
  protected String determineFullName() {
    if (enclosingScope == null) {
      // There should not be a symbol that is not defined in any scope. This case should only
      // occur while the symbol is built (by the symbol table creator). So, here the full name
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
            // ...else stop? If one of the enclosing scopes is unnamed,
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
