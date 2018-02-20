/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.resolving.ResolvingFilter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Super type of all scopes in the symbol table. A scope defines/contains a collection of symbols
 * and can have an enclosing and several sub scopes. An example is the method scope in Java. A
 * method scope can define variables and can have sub scopes, such as if-blocks. The enclosing
 * scope is the scope of the class which defines the method.
 *
 * @author  Pedram Mir Seyed Nazari
 *
 */
public interface Scope {

  /**
   * @return the name of the scope, if it is a named scope. If a symbol spans this scope, the name usually is the same
   * as the spanning symbol's name.
   *
   * @see Scope#getSpanningSymbol()
   */
  Optional<String> getName();

  /**
   * @return the enclosing scope. In Java, for example, the enclosing scope of a method scope is
   * the class scope that defines the method.
   */
  Optional<? extends Scope> getEnclosingScope();

  /**
   * @return the sub scopes. In Java, for example, method scopes are the sub scopes of a class
   * scope.
   */
  List<? extends Scope> getSubScopes();

  /**
   * Resolves the symbol with the given <code>name</code> and <code>kind</code> starting from
   * this scope. If no symbols are found, the resolving is continued in the enclosing scope. Note
   * that hiding has also an impact on whether the resolving should continue in the enclosing scope.
   *
   * @param name the symbol name to be resolved
   * @param kind the symbol kind to be resolved
   * @param <T> the type of the resolved symbol
   *
   * @return the symbol with the given <code>name</code> and <code>kind</code> starting from
   * this scope.
   *
   * @throws de.monticore.symboltable.resolving.ResolvedSeveralEntriesException Thrown if more than one
   * symbol is resolved.
   */
  <T extends Symbol> Optional<T> resolve(String name, SymbolKind kind);

  /**
   *
   * Resolves the symbol with the given <code>name</code> and <code>kind</code> starting from
   * this scope. Additionally, the symbol must have a proper access <code>modifier</code>. For
   * example, in Java, if <code>modifier</code> was <code>protected</code>, the symbol would have
   * to be declared as <code>protected</code> or <code>public</code>. If no symbols are found,
   * the resolving is continued in the enclosing scope. Note that hiding has also an impact on whether the
   * resolving should continue in the enclosing scope.
   *
   * @param name the symbol name to be resolved
   * @param kind the symbol kind to be resolved
   * @param modifier the access modifier of the symbol
   * @param <T> the type of the resolved symbol
   *
   * @return the symbol with the given <code>name</code> and <code>kind</code> starting from
   * this scope.
   *
   * @throws de.monticore.symboltable.resolving.ResolvedSeveralEntriesException Thrown if more than one
   * symbol is resolved.
   */
  <T extends Symbol> Optional<T> resolve(String name, SymbolKind kind, AccessModifier modifier);

  <T extends Symbol> Optional<T> resolve(String name, SymbolKind kind, AccessModifier modifier, Predicate<Symbol> predicate);

  /**
   * Resolves only explicitly imported symbols (i.e., symbols from imported scopes) and locally defined symbols.
   * For example, consider the Java classes <code>A</code>, <code>B</code>, and <code>C</code>, where
   * <code>A</code> subclasses <code>B</code> which itself subclasses <code>C</code>.
   * Then, <code>A</code> imports all (non-private) symbols defined in <code>B</code>. Further, <code>A</code>
   * imports all symbols <b>explicitly</b> imported by <code>B</code>, that means, all (non-private) symbols
   * defined in <code>C</code>.
   * <br /><br />
   * In contrast, <code>A</code> does not import symbols that are <b>implicitly</b> imported by <code>B</code>.
   * That means, symbols defined in the enclosing scope of <code>B</code>.
   *
   *
   *
   * @param name the symbol name to be resolved
   * @param kind the symbol kind to be resolved
   * @param modifier the access modifier of the symbol
   * @param <T> the type of the resolved symbol
   *
   * @return the symbol with the given <code>name</code> and <code>kind</code> starting from
   * this scope.
   *
   * @throws de.monticore.symboltable.resolving.ResolvedSeveralEntriesException Thrown if more than one
   * symbol is resolved.
   */
  <T extends Symbol> Optional<T> resolveImported(String name, SymbolKind kind, AccessModifier modifier);

  <T extends Symbol> Collection<T> resolveMany(String name, SymbolKind kind);

  <T extends Symbol> Collection<T> resolveMany(String name, SymbolKind kind, AccessModifier modifier);

  <T extends Symbol> Collection<T> resolveMany(String name, SymbolKind kind, Predicate<Symbol> predicate);

  <T extends Symbol> Collection<T> resolveMany(String name, SymbolKind kind, AccessModifier modifier, Predicate<Symbol> predicate);

  /**
   * Resolves the symbol with the given <code>name</code> and <code>kind</code> only within scope.
   *
   * @param name the symbol name to be resolved
   * @param kind the symbol kind to be resolved
   * @param <T> the type of the resolved symbol
   *
   * @return the symbol with the given <code>name</code> and <code>kind</code> only within scope.
   *
   * @throws de.monticore.symboltable.resolving.ResolvedSeveralEntriesException Thrown if more than one
   * symbol is resolved.
   */
  <T extends Symbol> Optional<T> resolveLocally(String name, SymbolKind kind);

  /**
   * Resolves all symbols with the given <code>kind</code> only within this scope.
   *
   * @param kind the symbol kind to be resolved
   * @param <T> the type of the resolved symbols
   *
   * @return the symbols with the given <code>name</code> and <code>kind</code> only within scope.
   */
  <T extends Symbol> Collection<T> resolveLocally(SymbolKind kind);



  <T extends Symbol> Optional<T> resolveDown(String name, SymbolKind kind);

  <T extends Symbol> Optional<T> resolveDown(String name, SymbolKind kind, AccessModifier modifier);

  <T extends Symbol> Optional<T> resolveDown(String name, SymbolKind kind, AccessModifier modifier, Predicate<Symbol> predicate);

  <T extends Symbol> Collection<T> resolveDownMany(String name, SymbolKind kind);

  <T extends Symbol> Collection<T> resolveDownMany(String name, SymbolKind kind, AccessModifier modifier);

  <T extends Symbol> Collection<T> resolveDownMany(String name, SymbolKind kind, AccessModifier modifier, Predicate<Symbol> predicate);

  /**
   *
   * @return all symbols directly defined/contained in this scope (not in enclosing scope).
   */
  Map<String, Collection<Symbol>> getLocalSymbols();



  /**
   * @return number of symbols directly defined/contained in this scope (not in enclosing scope).
   */
  int getSymbolsSize();

  /**
    * @return true, if this scope shadows symbols of the enclosing scope. By default, named scopes
   * (see #getName()) are shadowing scopes.
   */
  boolean isShadowingScope();

  /**
   * @return true, if this scope is spanned by a symbol. For example, a Java method spans a
   * method scope.
   */
  boolean isSpannedBySymbol();

  /**
   * @return the symbol that spans this scope. For example, a Java method spans a
   * method scope.
   */
  Optional<? extends ScopeSpanningSymbol> getSpanningSymbol();

  /**
   * States whether this scope exports symbols that can be used from outside the scope.
   * For example, a Java class exports symbols. In contrast, a Java if-block does not
   * export any symbols, hence, its locally defined variables cannot be referenced
   * from outside. By default, a scope with a name exports its symbols (although
   * this does not apply for Java methods).
   *
   * @return true, if this scope exports symbols that can be used from outside the scope.
   */
  boolean exportsSymbols();

  /**
   * Returns the resolvers that are available in this scope. Within a simple grammarlanguage, these
   * resolvers are usually the same for all scopes of the grammarlanguage. The composing languages this
   * may vary.
   *
   * @return the resolvers available in this scope.
   */
  Set<ResolvingFilter<? extends Symbol>> getResolvingFilters();

  /**
   * @return the corresponding ast node
   */
  Optional<? extends ASTNode> getAstNode();

  /**
   * Returns this scope as a {@link MutableScope}. Note that each scope must
   * implement {@link MutableScope}.
   *
   * @return this scope as a {@link MutableScope}.
   */
  MutableScope getAsMutableScope();
}
