/* (c) https://github.com/MontiCore/monticore */

package cdandaut;

import automata7._symboltable.IStimulusSymbolResolver;
import automata7._symboltable.StimulusSymbol;
import basiccd.BasicCDMill;
import basiccd._symboltable.CDClassSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class CDClass2StimulusResolver
    implements IStimulusSymbolResolver {

  @Override
  public List<StimulusSymbol> resolveAdaptedStimulusSymbol(
      boolean foundSymbols, String name, AccessModifier m,
      Predicate<StimulusSymbol> p) {
    List<StimulusSymbol> r = new ArrayList<>();
    Optional<CDClassSymbol> s = BasicCDMill.globalScope()
        .resolveCDClass(name, m);
    if(s.isPresent()){
      CDClass2StimulusAdapter a
          = new CDClass2StimulusAdapter(s.get());
      if(p.test(a)){
        r.add(a);
      }
    }
    return r;
  }
}
