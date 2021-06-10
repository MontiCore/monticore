/* (c) https://github.com/MontiCore/monticore */

package javaandaut;

import automata7._symboltable.*;
import basicjava.BasicJavaMill;
import basicjava._symboltable.*;
import de.monticore.io.paths.MCPath;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;


public class AutomataResolver implements IStimulusSymbolResolver {

  public AutomataResolver(MCPath symbolPath){
    for(Path p : symbolPath.getEntries()){
      BasicJavaMill.globalScope().getSymbolPath().addEntry(p);
    }
  }


  @Override public List<StimulusSymbol> resolveAdaptedStimulusSymbol(boolean foundSymbols,
      String name, AccessModifier modifier, Predicate<StimulusSymbol> predicate) {
    List<StimulusSymbol> result = new ArrayList<>();
    Optional<ClassDeclarationSymbol> classDeclSymbol = BasicJavaMill.globalScope()
        .resolveClassDeclaration(name, modifier);
    if(classDeclSymbol.isPresent()){
      result.add(new Class2StimulusAdapter(classDeclSymbol.get()));
    }
    return result;
  }
}