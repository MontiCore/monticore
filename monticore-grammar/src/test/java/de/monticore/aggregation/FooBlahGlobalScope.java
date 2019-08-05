package de.monticore.aggregation;

import de.monticore.aggregation.blah.Bar2DummySymbol;
import de.monticore.aggregation.blah._symboltable.BlahGlobalScope;
import de.monticore.aggregation.blah._symboltable.BlahLanguage;
import de.monticore.aggregation.blah._symboltable.DummySymbol;
import de.monticore.aggregation.blah._symboltable.IBlahGlobalScope;
import de.monticore.aggregation.foo._symboltable.BarSymbol;
import de.monticore.aggregation.foo._symboltable.FooGlobalScope;
import de.monticore.aggregation.foo._symboltable.FooLanguage;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class FooBlahGlobalScope extends FooGlobalScope {

  public FooBlahGlobalScope(ModelPath modelPath, FooLanguage fooLanguage){
    super(modelPath,fooLanguage);
    iBlahGS = new BlahGlobalScope(modelPath, new BlahLanguage());
  }

  IBlahGlobalScope iBlahGS;

  @Override
  public Collection<BarSymbol> resolveAdaptedBar(boolean foundSymbols,
                                                 String symbolName, AccessModifier modifier, Predicate<BarSymbol> predicate) {
    Collection<DummySymbol> vardefs = iBlahGS.resolveDummyMany(foundSymbols, symbolName, modifier);
    List<BarSymbol> list = new ArrayList<>();
    for (DummySymbol x : vardefs) {
      Bar2DummySymbol bar2DummySymbol = new Bar2DummySymbol(x);
      list.add(bar2DummySymbol);
    }
    return list;
  }

  public IBlahGlobalScope getIBlahGS() {
    return iBlahGS;
  }

  public void setiBlahGS(IBlahGlobalScope iBlahGS) {
    this.iBlahGS = iBlahGS;
  }

}
