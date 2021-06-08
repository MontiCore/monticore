/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization.json;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class is a subtype of a JsonObject that traces, which members have been visited. This can be
 * used, e.g., to track members that are stored but not yet taken into account for building up Java
 * objects.
 *
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
   * @see de.monticore.symboltable.serialization.json.JsonObject#setMembers(java.util.Map)
   */
  @Override
  public void setMembers(Map<String, JsonElement> members) {
    visitedMembers = new HashSet<>();
    super.setMembers(members);
  }
  
  /**
   * @see de.monticore.symboltable.serialization.json.JsonObject#getMember(java.lang.String)
   */
  @Override
  public JsonElement getMember(String name) {
    JsonElement result = super.getMember(name);
    if (null != result) {
      visitedMembers.add(name);
    }
    return result;
  }

  /**
   * This returns a collection of keys of members for which no getter method has been invoked yet.
   * 
   * @return
   */
  public Collection<String> getUnvisitedMembers() {
    //first create a copy of the set of member names
    Set<String> keySet = new HashSet<>(this.getMemberNames());
    //and then remove all members that have been visited
    keySet.removeAll(visitedMembers);
    return keySet;
  }
  
}
