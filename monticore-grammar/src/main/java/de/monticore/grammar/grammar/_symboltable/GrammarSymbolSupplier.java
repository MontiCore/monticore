/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammar._symboltable;

import de.se_rwth.commons.logging.Log;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

// TODO: Remove this class after version 7.9.0 (as it is generated)
public class GrammarSymbolSupplier implements Supplier<Optional<MCGrammarSymbol>> {
  protected final String qualifiedName;
  protected final IGrammarScope enclosingScope;

  public GrammarSymbolSupplier(String qualifiedName, IGrammarScope enclosingScope) {
    this.enclosingScope = enclosingScope;
    this.qualifiedName = qualifiedName;
  }

  @Override
  public Optional<MCGrammarSymbol> get() {
    Log.debug("Load full information of '" + qualifiedName + "' (Kind " + "de.monticore.grammar.grammar._symboltable.MCGrammarSymbol" + ").", GrammarSymbolSupplier.class.getSimpleName());
    if(!(enclosingScope instanceof de.monticore.grammar.grammar._symboltable.IGrammarScope)){
      Log.error("0xA4073x84660 The enclosingScope needs to be a subtype of de.monticore.grammar.grammar._symboltable.IGrammarScope.");
      return Optional.empty();
    }
    Optional<MCGrammarSymbol> resolvedSymbol = ((de.monticore.grammar.grammar._symboltable.IGrammarScope) enclosingScope).resolveMCGrammar(qualifiedName);

    if (resolvedSymbol.isPresent()) {
      Log.debug("Loaded full information of '" + qualifiedName + "' successfully.",
                GrammarSymbolSupplier.class.getSimpleName());
    } else {
      Log.error("0xA1037 " + GrammarSymbolSupplier.class.getSimpleName() + " Could not load full information of '" +
                        qualifiedName + "' (Kind " + "de.monticore.grammar.grammar._symboltable.MCGrammarSymbol" + ").");
    }
    return resolvedSymbol;
  }

  public static <T> Supplier<T> memoize(Supplier<T> delegate) {
    AtomicReference<T> value = new AtomicReference<>();
    return () -> {
      T val = value.get();
      if (val == null) {
        val = value.updateAndGet(cur -> cur == null ?
                Objects.requireNonNull(delegate.get()) : cur);
      }
      return val;
    };
  }
}
