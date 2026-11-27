/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules;

import de.monticore.tf.odrules._ast.ASTODLink;
import de.monticore.tf.odrules._ast.ASTODObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Calculates the order, in which new objects are created: Children are created before their
 * parents, so that a parent can be created together with the links to its (new) children.
 */
public class ODBuildOrder {

  /** the new objects by their names */
  protected final Map<String, ASTODObject> objects = new LinkedHashMap<>();

  /** the links, which are set when creating an object, by the name of their source */
  protected final Map<String, List<ASTODLink>> links = new HashMap<>();

  protected final Map<String, List<String>> children = new HashMap<>();

  protected final Map<String, String> parents = new HashMap<>();

  protected final Set<String> visited = new HashSet<>();

  protected final List<ASTODObject> buildOrder = new ArrayList<>();

  /**
   * @param objects the new objects
   * @param links   the new links
   */
  public ODBuildOrder(List<ASTODObject> objects, List<ASTODLink> links) {
    for (ASTODObject object : objects) {
      if (this.objects.putIfAbsent(object.getName(), object) == null) {
        this.links.put(object.getName(), new ArrayList<>());
        this.children.put(object.getName(), new ArrayList<>());
      }
    }
    for (ASTODLink link : links) {
      String parent = link.getLeftReferenceName(0).toString();
      String child = link.getRightReferenceName(0).toString();
      boolean isNewParent = this.objects.containsKey(parent);
      if (isNewParent) {
        this.links.get(parent).add(link);
      }
      if (this.objects.containsKey(child)) {
        if (isNewParent) {
          children.get(parent).add(child);
          parents.put(child, parent);
        } else {
          parents.remove(child);
        }
      }
    }
    calculateBuildOrder();
  }

  /**
   * @return the new objects in the build order together with the links set during their creation
   */
  public LinkedHashMap<ASTODObject, List<ASTODLink>> getBuildAttrs() {
    LinkedHashMap<ASTODObject, List<ASTODLink>> buildAttrs = new LinkedHashMap<>();
    for (ASTODObject object : buildOrder) {
      buildAttrs.put(object, links.get(object.getName()));
    }
    return buildAttrs;
  }

  public List<ASTODObject> getBuildOrder() {
    return buildOrder;
  }

  /**
   * Adds each tree of new objects to the build order, starting from its root
   */
  public void calculateBuildOrder() {
    for (String name : objects.keySet()) {
      if (!visited.contains(name)) {
        String root = name;
        Set<String> ancestors = new HashSet<>();
        while (parents.containsKey(root) && ancestors.add(root)) {
          root = parents.get(root);
        }
        addBottomUp(root);
      }
    }
  }

  protected void addBottomUp(String name) {
    for (String child : children.get(name)) {
      addBottomUp(child);
    }
    buildOrder.add(objects.get(name));
    visited.add(name);
  }

}
