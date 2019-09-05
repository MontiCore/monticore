// (c) https://github.com/MontiCore/monticore

package de.monticore.expressions.expressionsbasis._symboltable;

import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.Collection;
import java.util.Optional;


/** #2385
 * TOP to mark relevant resolve Methods as deprecated
 *
 * TO be removed when #2385 or #2383 solved
 *
 */
public interface IExpressionsBasisScope extends IExpressionsBasisScopeTOP {

  @Deprecated
  default public Optional<EMethodSymbol> resolveEMethod(String name) {
    return getResolvedOrThrowException(resolveEMethodMany(name));
  }

  @Deprecated
  default public Collection<EMethodSymbol> resolveEMethodMany(String name) {
    return resolveEMethodMany(name, AccessModifier.ALL_INCLUSION);
  }


  @Deprecated
  default public Optional<ETypeSymbol> resolveEType(String name) {
    return getResolvedOrThrowException(resolveETypeMany(name));
  }

  @Deprecated
  default public Collection<ETypeSymbol> resolveETypeMany(String name) {
    return resolveETypeMany(name, AccessModifier.ALL_INCLUSION);
  }

  @Deprecated
  default public Optional<EVariableSymbol> resolveEVariable(String name) {
    return getResolvedOrThrowException(resolveEVariableMany(name));
  }

  @Deprecated
  default public Collection<EVariableSymbol> resolveEVariableMany(String name) {
    return resolveEVariableMany(name, AccessModifier.ALL_INCLUSION);
  }

}
