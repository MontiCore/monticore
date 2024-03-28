/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.modifiers;

import de.se_rwth.commons.logging.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CompoundAccessModifier implements AccessModifier {

  protected Map<String, AccessModifier> dimensionToModifier;

  public CompoundAccessModifier(List<AccessModifier> modifiers) {
    this.dimensionToModifier = new HashMap<>();
    for(AccessModifier modifier: modifiers){
      Map<String, AccessModifier> modifierMap = modifier.getDimensionToModifierMap();
      for(Map.Entry<String, AccessModifier> entry: modifierMap.entrySet()){
        if(dimensionToModifier.get(entry.getKey()) != null){
          Log.warn("0xA0143 Multiple entries for dimension " + entry.getKey());
        }
        dimensionToModifier.put(entry.getKey(), entry.getValue());
      }
    }
  }

  public CompoundAccessModifier(AccessModifier... modifiers){
    this(Arrays.stream(modifiers).collect(Collectors.toList()));
  }

  @Override
  public boolean includes(AccessModifier modifier) {
    for(Map.Entry<String, AccessModifier> entry: modifier.getDimensionToModifierMap().entrySet()){
      String key = entry.getKey();
      AccessModifier value = entry.getValue();
      if(this.dimensionToModifier.containsKey(key)){
        if(!this.dimensionToModifier.get(key).includes(value)){
          return false;
        }
      }
    }
    return true;
  }


  @Override
  public Map<String, AccessModifier> getDimensionToModifierMap() {
    return dimensionToModifier;
  }
}
