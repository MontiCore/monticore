// (c) https://github.com/MontiCore/monticore

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.aggregation.foo._symboltable;

import de.monticore.aggregation.blah.Bar2DummySymbol;
import de.monticore.aggregation.blah.BlahMill;
import de.monticore.aggregation.blah._symboltable.DummySymbol;
import de.monticore.aggregation.blah._symboltable.IBlahGlobalScope;
import de.monticore.io.paths.MCPath;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class FooGlobalScope extends de.monticore.aggregation.foo._symboltable.FooGlobalScopeTOP {
  public FooGlobalScope() {
    super();
    iBlahGS = BlahMill
            .globalScope();
    iBlahGS.setSymbolPath(symbolPath);
    iBlahGS.setFileExt("blah");
  }

  public FooGlobalScope(MCPath symbolPath, String ext){
    super(symbolPath, "blah");
    iBlahGS = BlahMill
        .globalScope();
    iBlahGS.setSymbolPath(symbolPath);
    iBlahGS.setFileExt("blah");
  }

  IBlahGlobalScope iBlahGS;

  @Override
  public List<BarSymbol> resolveAdaptedBar(boolean foundSymbols,
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

  @Override
  public FooGlobalScope getRealThis() {
    return this;
  }
}
