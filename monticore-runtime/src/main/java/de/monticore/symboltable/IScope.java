/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import de.monticore.ast.ASTNode;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.modifiers.IncludesAccessModifierSymbolPredicate;
import de.monticore.symboltable.resolving.ResolvedSeveralEntriesForSymbolException;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.Splitters;

import java.util.*;

import static com.google.common.collect.FluentIterable.from;
import static de.se_rwth.commons.Joiners.DOT;
import static java.util.stream.Collectors.toSet;

public interface IScope {

  IScope getEnclosingScope();

  /**
   * @return number of symbols directly defined/contained in this scope (not in enclosing scope).
   */
  int getSymbolsSize();

  /**
   * @return true, if this scope shadows symbols of the enclosing scope. By default, named scopes
   * (see #getName()) are shadowing scopes.
   */
  boolean isShadowing();

  /**
   * @return true, if this scope is spanned by a symbol. For example, a Java method spans a
   * method scope.
   */
  boolean isPresentSpanningSymbol();

  /**
   * States whether this scope exports symbols that can be used from outside the scope.
   * For example, a Java class exports symbols. In contrast, a Java if-block does notisym
   * export any symbols, hence, its locally defined variables cannot be referenced
   * from outside. By default, a scope with a name exports its symbols (although
   * this does not apply for Java methods).
   *
   * @return true, if this scope exports symbols that can be used from outside the scope.
   */
  boolean isExportingSymbols();

  boolean isOrdered();

  void setExportingSymbols(boolean b);

  void setShadowing(boolean b);

  void setOrdered(boolean b);

  /**
   * @param node the corresponding ast node
   */
  void setAstNode(ASTNode node);

  void setAstNodeAbsent();

  ASTNode getAstNode();

  boolean isPresentAstNode();

  void setSpanningSymbol(IScopeSpanningSymbol symbol);

  void setSpanningSymbolAbsent();


  IScopeSpanningSymbol getSpanningSymbol();

  void setName(String name);

  void setNameAbsent();

  String getName();

  boolean isPresentName();

  default List<String> getRemainingNameForResolveDown(String symbolName) {
    return Lists.newArrayList(isPresentName() && !getName().isEmpty() && symbolName.startsWith(getName()) ? symbolName.substring(getName().length() + 1) : symbolName);
  }

  default FluentIterable<String> getNameParts(String symbolName) {
    return from(Splitters.DOT.split(symbolName));
  }

  default boolean checkIfContinueWithEnclosingScope(boolean foundSymbols) {
    // If this scope shadows its enclosing scope and already some symbols are found,
    // there is no need to continue searching.
    return !(foundSymbols && isShadowing());
  }

  default boolean checkIfContinueAsSubScope(String symbolName) {
    if (this.isExportingSymbols()) {
      final List<String> nameParts = getNameParts(symbolName).toList();

      if (nameParts.size() > 1) {
        final String firstNamePart = nameParts.get(0);
        // A scope that exports symbols usually has a name.
        if (this.isPresentName()) {
          return symbolName.startsWith(getName());
        }
        else {
          return firstNamePart.equals("");
        }
      }
    }

    return false;
  }

  default <T extends ISymbol> Optional<T> getResolvedOrThrowException(final Collection<T> resolved) {
    Set<T> resolvedSet = new HashSet<>(resolved);

    if (resolvedSet.size() == 1) {
      return Optional.of(resolvedSet.iterator().next());
    } else if (resolvedSet.size() > 1) {
      throw new ResolvedSeveralEntriesForSymbolException("0xA4095 Found " + resolvedSet.size()
          + " symbols: " + resolvedSet.iterator().next().getFullName(),
          resolvedSet);
    }

    return Optional.empty();
  }

  default <T extends ISymbol> List<T> filterSymbolsByAccessModifier(AccessModifier modifier, Collection<T> resolvedUnfiltered) {
    return new ArrayList<>(resolvedUnfiltered.stream().filter(new IncludesAccessModifierSymbolPredicate(modifier)).collect(toSet()));
  }

  default LinkedListMultimap<String, SymbolWithScopeOfUnknownKind> getUnknownSymbols() {
    return LinkedListMultimap.create();
  }

  default List<SymbolWithScopeOfUnknownKind> getLocalUnknownSymbols() {
    return getUnknownSymbols().values();
  }

  default void add(SymbolWithScopeOfUnknownKind symbol) {
    throw new UnsupportedOperationException("This operation is not implemented.");
  }

  default void remove(SymbolWithScopeOfUnknownKind symbol) {
    throw new UnsupportedOperationException("This operation is not implemented.");
  }

  default void accept(ITraverser visitor)  {
    visitor.handle(this);
  }

  /**
   * whether this is a subscope of the other scope (transitive)
   */
  default boolean isSubScopeOf(IScope superScope) {
    if (this == superScope) {
      return true;
    }
    else if (this.getEnclosingScope() != null) {
      return this.getEnclosingScope().isSubScopeOf(superScope);
    }
    else {
      return false;
    }
  }

  /**
   * whether this is a proper subscope of the other scope (transitive)
   */
  default boolean isProperSubScopeOf(IScope superScope) {
    if (this.getEnclosingScope() == null) {
      return false;
    }
    else {
      return this.getEnclosingScope().isSubScopeOf(superScope);
    }
  }

}
