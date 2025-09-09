/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.rule2od;


import com.google.common.collect.Maps;
import de.monticore.tf.ast.ITFObject;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class Variable2AttributeMap {

  public Map<String, AttributeEntry> getV2a() {
    return Maps.newHashMap(v2a);
  }

  public void setV2a(
      Map<String, AttributeEntry> v2a) {
    this.v2a = v2a;
  }

  private Map<String, AttributeEntry> v2a = new HashMap<>();

  public void addDeclaration(String variableName, ITFObject object, String attributeName) {
    v2a.put(variableName, new AttributeEntry(object, attributeName));
  }

  public void addDeclaration(String variableName, ITFObject object, String attributeName, boolean listValued) {
    v2a.put(variableName, new AttributeEntry(object, attributeName, listValued));
  }

  public boolean contains(String variableName) {
    return v2a.containsKey(variableName);
  }

  /**
   * @return the object for the referenced attribute
   */
  public ITFObject getObject(String variableName) {
      return v2a.get(variableName).getObject();
  }

  /**
   * @return the attribute name for the referenced attribute, null if the
   *         variable is unknown
   */
  public String getAttributeName(String variableName) {
      return v2a.get(variableName).getAttributeName();
  }

  public boolean isList(String variableName){
    return v2a.get(variableName).isListValued();
  }

  class AttributeEntry {
    AttributeEntry(ITFObject object, String attributeName) {
      super();
      checkNotNull(object);
      checkNotNull(attributeName);
      this.object = object;
      this.attributeName = attributeName;
    }

    AttributeEntry(ITFObject object, String attributeName, boolean listValued) {
      super();
      checkNotNull(object);
      checkNotNull(attributeName);
      this.object = object;
      this.attributeName = attributeName;
      this.listValued = listValued;
    }

    public ITFObject getObject() {
      return object;
    }

    public String getAttributeName() {
      return attributeName;
    }

    private ITFObject object;
    private String attributeName;

    public boolean isListValued() {
      return listValued;
    }

    private boolean listValued = false;
  }

  @Override
  public Variable2AttributeMap clone()  {
    Variable2AttributeMap map = new Variable2AttributeMap();
    map.setV2a(getV2a());
    return map;
  }
}
