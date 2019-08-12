/* (c) https://github.com/MontiCore/monticore */
package de.monticore.aggregation.blah._symboltable;

import com.google.common.collect.ImmutableSet;
import de.monticore.utils.Names;

import java.util.Collections;
import java.util.Set;

public class BlahLanguage extends BlahLanguageTOP {
  public static final String FILE_ENDING = "blah";

  public BlahLanguage() {
    super("Blah Language",FILE_ENDING);
  }

  @Override
  protected BlahModelLoader provideModelLoader() {
    return BlahSymTabMill.blahModelLoaderBuilder().setModelingLanguage(this).build();
  }

  @Override
  protected Set<String> calculateModelNamesForDummy(String name) {
    // e.g., if p.Automaton.State, return p.Automaton
    if (!Names.getQualifier(name).isEmpty()) {
      return ImmutableSet.of(Names.getQualifier(name));
    }

    return Collections.emptySet();
  }
}
