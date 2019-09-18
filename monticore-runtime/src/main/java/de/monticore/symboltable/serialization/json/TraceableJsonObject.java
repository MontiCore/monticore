/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization.json;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * This class is a subtype of a JsonObject that traces, which members have been visited. 
 * This can be used, e.g., to track members that are stored but not yet taken into account 
 * for building up Java objects.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class TraceableJsonObject extends JsonObject {
  
  protected Set<String> visitedMembers;
  
  /**
   * Constructor for de.monticore.symboltable.serialization.json.TraceableJsonObject
   */
  public TraceableJsonObject() {
    visitedMembers = new HashSet<>();
  }
  
  /**
   * @see de.monticore.symboltable.serialization.json.JsonObject#setAttributes(java.util.Map)
   */
  @Override
  public void setMembers(Map<String, JsonElement> members) {
    visitedMembers = new HashSet<>();
    super.setMembers(members);
  }
  
  /**
   * @see de.monticore.symboltable.serialization.json.JsonObject#get(java.lang.String)
   */
  @Override
  public JsonElement get(String key) {
    JsonElement jsonElement = super.get(key);
    if (null != jsonElement) {
      visitedMembers.add(key);
    }
    return jsonElement;
  }
  
  /**
   * @see de.monticore.symboltable.serialization.json.JsonObject#getStringOpt(java.lang.String)
   */
  @Override
  public Optional<String> getStringOpt(String key) {
    Optional<String> res = super.getStringOpt(key);
    if (null != res && res.isPresent()) {
      visitedMembers.add(key);
    }
    return res;
  }
  
  /**
   * @see de.monticore.symboltable.serialization.json.JsonObject#getBooleanOpt(java.lang.String)
   */
  @Override
  public Optional<Boolean> getBooleanOpt(String key) {
    Optional<Boolean> res = super.getBooleanOpt(key);
    if (null != res && res.isPresent()) {
      visitedMembers.add(key);
    }
    return res;
  }
  
  public Collection<String> getUnvisitedMembers(){
    Set<String> keySet = keySet();
    keySet.removeAll(visitedMembers);
    return keySet;
  }

}
