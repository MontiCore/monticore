package mc.embedding.composite._symboltable;

import de.monticore.symboltable.modifiers.AccessModifier;
import mc.embedding.embedded._symboltable.TextSymbol;
import mc.embedding.host._symboltable.ContentSymbol;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface ICompositeScope extends ICompositeScopeTOP {
  @Override default Collection<ContentSymbol> resolveAdaptedContentLocallyMany(boolean foundSymbols,
      String name, AccessModifier modifier, Predicate<ContentSymbol> predicate) {
    Set<TextSymbol> symbols = resolveTextLocallyMany(foundSymbols, name, modifier, x -> true);
    return symbols.stream().map(s -> new Text2ContentAdapter(s)).collect(Collectors.toList());
  }
  
}
