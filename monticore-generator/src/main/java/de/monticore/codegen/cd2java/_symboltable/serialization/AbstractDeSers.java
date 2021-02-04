/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.cd2java._symboltable.serialization;

import java.util.ArrayList;
import java.util.List;

/**
 * This class manages a list of all DeSers that are generated as abstract classes.
 * It only yields correct results if called *after* the decoration has been realized for
 * all DeSers. This is required by the global scop class generator to avoid instantiating abstract
 * DeSers in the map of known DeSers.
 * @return
 */
public class AbstractDeSers {

  protected static List<String> abstractDeSers = new ArrayList<>();

  public static void add(String abstractDeSerName){
    abstractDeSers.add(abstractDeSerName);
  }

  public static void reset(){
    abstractDeSers.clear();
  }

  public static boolean contains(String abstractDeSerName){
    return abstractDeSers.contains(abstractDeSerName);
  }

}
