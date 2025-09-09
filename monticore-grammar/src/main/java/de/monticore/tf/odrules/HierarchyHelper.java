/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules;

import de.monticore.tf.odrulegeneration._ast.*;
import de.monticore.tf.odrules._ast.ASTODDefinition;
import de.monticore.tf.odrules._ast.ASTODInnerLink;
import de.monticore.tf.odrules._ast.ASTODObject;
import de.monticore.tf.odrules._ast.ASTODRule;
import de.monticore.tf.odrules.util.ODRuleStereotypes;
import de.monticore.tf.odrules.util.Util;

import java.util.*;
import java.util.stream.Collectors;

public final class HierarchyHelper {
  private final static String optionalType = "de.monticore.tf.ast.IOptional";
  private final static String listType = "de.monticore.tf.ast.IList";

  private ASTODRule rule;
  private ASTODDefinition lhs;
  private Optional<ASTODDefinition> rhs = Optional.empty();

  private Map<String, List<String>> listChildPairs = new HashMap<>();
  private Map<String, List<String>> listChildPairsLhs = new HashMap<>();
  private Map<String, List<String>> listChildPairsWithOptionals = new HashMap<>();

  private List<String> listChildNames = new ArrayList<>();
  private List<String> listChildNamesLhs = new ArrayList<>();
  private List<String> listChildNamesRhs = new ArrayList<>();

  private String packageName = "";
  private final List<String> customImports = new ArrayList<>();

  public HierarchyHelper() {
    rule = ODRulesMill.oDRuleBuilder().uncheckedBuild();
    lhs = ODRulesMill.oDDefinitionBuilder().uncheckedBuild();
  }

  public HierarchyHelper(ASTODRule astodRule) {
    rule = astodRule;
    lhs = rule.getLhs();
    rhs = rule.isPresentRhs() ? Optional.of(rule.getRhs()) : Optional.empty();
    listChildNames = new ArrayList<>();
    listChildNamesLhs = new ArrayList<>();
    listChildNamesRhs = new ArrayList<>();
    // Calculate ListChildPairs for the LHS
    listChildPairs = getListChildPairs(lhs.getODObjectList());
    listChildPairsLhs = getListChildPairs(lhs.getODObjectList());
    for (String key : listChildPairs.keySet()) {
      listChildNamesLhs.addAll(listChildPairs.get(key));
    }

    listChildPairsWithOptionals = getListChildPairsWithOptionals(lhs.getODObjectList());
    Map<String, List<String>> rhsListChildPairs = rhs.isPresent() ?
            getListChildPairs(rhs.get().getODObjectList()) : new HashMap<>();
    for (String key : rhsListChildPairs.keySet()) {
      // Every list on the lhs is also on the rhs
      // If there are objects to create in a list put them to the Map
      if (!listChildPairs.get(key).containsAll(rhsListChildPairs.get(key))) {
        List<String> temporary = rhsListChildPairs.get(key);
        // No duplicates
        temporary.removeAll(listChildPairs.get(key));
        temporary.addAll(listChildPairs.get(key));
        listChildPairs.put(key, temporary);
      }
      listChildNamesRhs.addAll(rhsListChildPairs.get(key));
    }
    // Fill names for quick check
    for (String key : listChildPairs.keySet()) {
      listChildNames.addAll(listChildPairs.get(key));
    }
  }

  /**
   * Checks whether the object with the given name is a list object.
   *
   * @param objectName the name of the object
   * @return whether it is a list
   */
  public boolean isListObject(String objectName) {

    // try to resolve it by the left-hand side first
    ASTODObject obj = Util.getODObject(lhs, objectName);
    if (obj == null) {
      // and then by the right-hand side
      if (rhs.isPresent()) {
        obj = Util.getODObject(rhs.get(), objectName);
        if (obj == null) {
          return false;
        }
      }
    }
    return obj.hasStereotype(ODRuleStereotypes.LIST);
  }

  /**
   * Get package name for code generation
   *
   * @return
   */
  public String getPackageName() {
    return packageName;
  }

  /**
   * Set package name for code generation
   *
   * @param packageName
   */
  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  /**
   * Check if package name for code generation is set
   *
   * @return
   */
  public boolean packageisPresentName() {
    return !"".equals(packageName);
  }

  /**
   * Get all custom imports for code generation
   *
   * @return
   */
  public List<String> getCustomImports() {
    return customImports;
  }

  /**
   * Add custom import for code generation
   *
   * @param customImport
   */
  public void addCustomImports(String customImport) {
    customImports.add(customImport);
  }

  /**
   * Calculates all Lists and their children, saves also all children seperately
   *
   * @param objects List of objects to be checked for Lists
   * @return A mapping for each list to their children
   */
  private Map<String, List<String>> getListChildPairs(
          List<ASTODObject> objects) {
    Map<String, List<String>> result = new HashMap<>();
    List<String> childs;
    List<ASTODObject> innerObjects;
    // Search for every List in the given Objects
    for (ASTODObject object : objects) {
      if (object.isPresentType() &&
              Util.printType(object.getType())
                      .equals(listType)) {
        childs = getListChilds(object);
        // If there is a name for the list, save it and the childnames of the
        // list
        if (object.isPresentName()) {
          result.put(object.getName(), childs);
        }
      }
      innerObjects = new ArrayList<>();
      for (ASTODInnerLink link : object.getInnerLinksList()) {
        innerObjects.add(link.getODObject());
      }
      // Do it also for the InnerObjects of the current Object
      result.putAll(getListChildPairs(innerObjects));
    }
    return result;
  }

  private Map<String, List<String>> getListChildPairsWithOptionals(
          List<ASTODObject> objects) {
    Map<String, List<String>> result = new HashMap<>();
    List<String> childs;
    List<ASTODObject> innerObjects;
    // Search for every List in the given Objects
    for (ASTODObject object : objects) {
      if (object.isPresentType() &&
              Util.printType(object.getType())
                      .equals(listType)) {
        childs = getListChildsWithOptionals(object);
        // If there is a name for the list, save it and the childnames of the
        // list
        if (object.isPresentName()) {
          result.put(object.getName(), childs);
        }
      }
      innerObjects = new ArrayList<>();
      for (ASTODInnerLink link : object.getInnerLinksList()) {
        innerObjects.add(link.getODObject());
      }
      // Do it also for the InnerObjects of the current Object
      result.putAll(getListChildPairsWithOptionals(innerObjects));
    }
    return result;
  }

  /**
   * Calculates all inner objects of a given object
   *
   * @param obj        The list from which we want the childs.
   * @param allMatches The list of all LHS match elements.
   * @return a List of the names of all (direct) childs from the list.
   */
  public List<ASTMatchingObject> getInnerLinkObjectsLHS(List<ASTMatchingObject> allMatches,
                                                        ASTMatchingObject obj) {
    ArrayList<ASTMatchingObject> innerObjects = new ArrayList<>();

    for (String innerObjectName : obj.getInnerLinkObjectNamesList()) {

      Optional<ASTMatchingObject> innerLinkObject = allMatches.stream()
              .filter(m -> m.getObjectName().equals(innerObjectName)).findAny();
      if (innerLinkObject.isPresent()) {
        innerObjects.add(innerLinkObject.get());
      }
    }

    return innerObjects;
  }

  /**
   * Calculates all childs from the given list and regards an optional as
   * another list
   *
   * @param list The list from which we want the childs.
   * @return a List of the names of all (direct) childs from the list.
   */
  private List<String> getListChilds(ASTODObject list) {
    List<String> result = new ArrayList<>();
    // For every direct child calculate the name
    for (ASTODInnerLink link : list.getInnerLinksList()) {
      ASTODObject object = link.getODObject();
      // Do it recursively for each Optional or look if the name is present and
      // save it
      if (object.isPresentType() &&
              Util.printType(object.getType())
                      .equals(optionalType)) {
        result.addAll(getListChilds(object));
      } else if (object.isPresentName()) {
        result.add(object.getName());
      }
    }
    return result;
  }

  /**
   * Calculates all childs from the given list and includes optionals
   *
   * @param list The list from which we want the childs.
   * @return a List of the names of all (direct) childs from the list.
   */
  private List<String> getListChildsWithOptionals(ASTODObject list) {
    List<String> result = new ArrayList<>();
    // For every direct child calculate the name
    for (ASTODInnerLink link : list.getInnerLinksList()) {
      ASTODObject object = link.getODObject();
      // Do it recursively for each Optional or look if the name is present and
      // save it
      if (object.isPresentName()) {
        result.add(object.getName());
      }

    }
    return result;
  }

  /**
   * Removes every mandatory object.
   *
   * @param allObjects list of all objects
   * @return a list of all found list objects
   */
  public List<ASTMatchingObject> getListObjects(List<ASTMatchingObject> allObjects) {
    ArrayList<ASTMatchingObject> mandatoryObjects = allObjects.stream()
            .filter(c -> c.isListObject()).collect(Collectors.toCollection(ArrayList::new));
    return mandatoryObjects;
  }

  /**
   * Checks if the given object is in a list.
   *
   * @param object The object to be checked
   * @return If the object is in a list
   */
  public boolean isListChild(ASTMatchingObject object) {
    return listChildNames.contains(object.getObjectName());
  }

  public boolean isListChild(String objectname) {
    return listChildNames.contains(objectname);
  }

  public boolean isLhsListChild(String objectname) {
    return listChildNamesLhs.contains(objectname);
  }

  public boolean isLhsListChild(ASTMatchingObject object) {
    return listChildNamesLhs.contains(object.getObjectName());
  }

  public boolean isRhsListChild(String objectname) {
    return listChildNamesRhs.contains(objectname);
  }

  public boolean isRhsListChild(ASTMatchingObject object) {
    return listChildNamesRhs.contains(object.getObjectName());
  }

  /**
   * Removes every optional and listChild match-object.
   *
   * @param allMatches list of all match-objects
   * @return a list of match-objects without optional and listChild objects
   */
  public List<ASTMatchingObject> getMandatoryObjectsWithoutOptAndListChilds(
          List<ASTMatchingObject> allMatches) {
    ArrayList<ASTMatchingObject> mandatoryObjects = allMatches.stream()
            .filter(c -> !c.isOptObject() && !isWithinListStructure(c.getObjectName()))
            .collect(Collectors.toCollection(ArrayList::new));
    for (ASTMatchingObject object : allMatches) {
      if (object.isListObject() || object.getType().endsWith("IList")) {
        for (int i = 0; i <= object.getInnerLinkObjectNamesList().size(); i++) {
          int index = allMatches.indexOf(object);
          if (allMatches.get(index + i).isOptObject() || allMatches.get(index + i).getType().endsWith("IOptional")) {
            for (String innerLinkName : allMatches.get(index + i).getInnerLinkObjectNamesList()) {
              for (Iterator<ASTMatchingObject> it = mandatoryObjects.iterator(); it.hasNext(); ) {
                ASTMatchingObject mandatoryObject = it.next();
                if (mandatoryObject.getObjectName().equals(innerLinkName)) {
                  it.remove();
                }
              }
            }
          }
        }
      }
      if (object.isOptObject() || object.getType().endsWith("IOptional")) {
        for (int i = 0; i <= object.getInnerLinkObjectNamesList().size(); i++) {
          int index = allMatches.indexOf(object);
          if (allMatches.get(index + i).isListObject() || allMatches.get(index + i).getType().endsWith("IList")) {
            for (String innerLinkName : allMatches.get(index + i).getInnerLinkObjectNamesList()) {
              for (Iterator<ASTMatchingObject> it = mandatoryObjects.iterator(); it.hasNext(); ) {
                ASTMatchingObject mandatoryObject = it.next();
                if (mandatoryObject.getObjectName().equals(innerLinkName)) {
                  it.remove();
                }
              }
            }
          }
        }
      }
    }
    return mandatoryObjects;
  }

  /**
   * Removes every listChild match-object.
   *
   * @param allMatches list of all match-objects
   * @return a list of match-objects without listChild objects
   */
  public List<ASTMatchingObject> getMandatoryObjectsWithoutListChilds(
          List<ASTMatchingObject> allMatches) {
    ArrayList<ASTMatchingObject> mandatoryObjects = allMatches.stream()
            .filter(c -> !isWithinListStructure(c.getObjectName()))
            .collect(Collectors.toCollection(ArrayList<ASTMatchingObject>::new));
    return mandatoryObjects;
  }

  /**
   * Removes every optional and listChild match-object.
   *
   * @param allObjects list of all match-objects
   * @return a list of match-objects without optional and listChild objects
   */
  public List<ASTMatchingObject> getListChilds(List<ASTMatchingObject> allObjects,
                                               ASTMatchingObject list) {
    ArrayList<ASTMatchingObject> mandatoryObjects = allObjects.stream()
            .filter(c -> listChildPairsLhs.get(list.getObjectName()).contains(c.getObjectName()))
            .collect(Collectors.toCollection(ArrayList::new));
    return mandatoryObjects;
  }

  /**
   * Gives all child objects from a given list of objects, including optionals
   *
   * @param allobjects the objects to search in
   * @return all objects in lists in the given objects
   */
  public List<ASTMatchingObject> getListChildsWithOptionals(List<ASTMatchingObject> allobjects,
                                                            ASTMatchingObject list) {
    ArrayList<ASTMatchingObject> mandatoryObjects = allobjects.stream()
            .filter(
                    c -> listChildPairsWithOptionals.get(list.getObjectName()).contains(c.getObjectName()))
            .collect(Collectors.toCollection(ArrayList<ASTMatchingObject>::new));
    return mandatoryObjects;
  }

  /**
   * Gives all objects in a List in the given list of objects, besides optionals
   *
   * @param allobjects the objects to search in
   * @return all objects in lists in the given objects
   */
  public List<ASTMatchingObject> getListChilds(List<ASTMatchingObject> allobjects) {
    ArrayList<ASTMatchingObject> mandatoryObjects = allobjects.stream()
            .filter(c -> isWithinListStructure(c.getObjectName()) && !c.getType().equals(optionalType)
                    && !c.getType().equals(listType))
            .collect(Collectors.toCollection(ArrayList<ASTMatchingObject>::new));
    return mandatoryObjects;
  }

  /**
   * Gives all objects in a List in the given list of objects
   *
   * @param allobjects the objects to search in
   * @return all objects in lists in the given objects
   */
  public List<ASTMatchingObject> getListChildsWithOptionals(List<ASTMatchingObject> allobjects) {
    ArrayList<ASTMatchingObject> mandatoryObjects = allobjects.stream()
            .filter(c -> isWithinListStructure(c.getObjectName()) && !c.getType().equals(listType))
            .collect(Collectors.toCollection(ArrayList<ASTMatchingObject>::new));
    return mandatoryObjects;
  }

  /**
   * Gives the ListStructure Name the given objectName lies in
   *
   * @param object The name of the object
   * @return The name of the list Structure which contains the object
   */
  public String getListParent(String object) {
    for (String key : listChildPairs.keySet()) {
      if (listChildPairs.get(key).contains(object)) {
        return key;
      }
    }
    return null;
  }

  /**
   * Method to get the Treepath the object lies in.
   *
   * @param object The Object for which we want to find a path in the
   *               List-Structure Tree
   * @return A List starting with the root-list and ending with the list
   * containing the given Object.
   */
  public List<String> getListTree(String object) {
    List<String> result = new ArrayList<>();
    if (this.isListChild(object)) {
      for (String key : listChildPairs.keySet()) {
        if (listChildPairs.get(key).contains(object)) {
          result.add(key);
          result.addAll(getListTree(key));
          return result;
        }
      }
    }
    return result;
  }

  /**
   * Gives the Object to a Name in a given List of Objects
   *
   * @param allObjects The List of objects where the object should be found in.
   * @param name       The given objectname
   * @return The object which has the given name, null if no object was found
   */
  public ASTMatchingObject getObjectByName(List<ASTMatchingObject> allObjects, String name) {
    for (ASTMatchingObject obj : allObjects) {
      if (obj.getObjectName().equals(name)) {
        return obj;
      }
    }
    return null;
  }

  /**
   * Removes every Change object with the Optional type.
   *
   * @param allChanges list of all Change objects
   * @return a list of Changes without optional objects
   */
  public List<ASTChange> getMandatoryChangeObjects(List<ASTChange> allChanges) {
    ArrayList<ASTChange> mandatoryObjects = allChanges.stream()
            .filter(c -> !c.getType().equals(optionalType) && !c.getType().equals(listType))
            .collect(Collectors.toCollection(ArrayList::new));
    return mandatoryObjects;
  }

  /**
   * Removes every Change object with the Optional type and the list type.
   *
   * @param allChanges list of all Change objects
   * @return a list of Changes without optional objects and list objects
   */
  public List<ASTChange> getMandatoryChangeObjectsNoList(List<ASTChange> allChanges) {
    ArrayList<ASTChange> mandatoryObjects = allChanges.stream()
            .filter(c -> !c.getType().equals(optionalType) && !c.getType().equals(listType))
            .collect(Collectors.toCollection(ArrayList::new));
    return mandatoryObjects.stream().filter(a -> !isWithinListStructure(a.getObjectName())).collect(Collectors.toCollection(ArrayList::new));
  }

  public List<ASTChange> getChangeObjectsWhithoutCreate(ASTReplacement replacements) {
    Set<String> createStrings = replacements.getCreateObjectsList().stream().map(ASTCreateOperation::getName).collect(Collectors.toSet());
    return replacements.getChangesList().stream().filter(m -> !createStrings.contains(m.getObjectName())).collect(Collectors.toCollection(ArrayList::new));
  }

    /**
     * Only change objects with the list type.
     *
     * @param allChanges list of all Change objects
     * @return a list of Changes, which are list objects
     */
  public List<ASTChange> getMandatoryChangeObjectsOnlyList(List<ASTChange> allChanges) {
    ArrayList<ASTChange> mandatoryObjects = allChanges.stream()
            .filter(c -> !c.getType().equals(optionalType) && !c.getType().equals(listType))
            .filter(a -> isWithinListStructure(a.getObjectName()))
            .collect(Collectors.toCollection(ArrayList::new));
    return mandatoryObjects;
  }

  /**
   * Removes every Delete object with the Optional type.
   *
   * @param allDeletes list of all Delete objects
   * @return a list of Deletes without optional objects
   */
  public List<ASTDeleteOperation> getMandatoryDeleteObjects(List<ASTDeleteOperation> allDeletes) {
    ArrayList<ASTDeleteOperation> mandatoryObjects = allDeletes.stream()
            .filter(d -> !d.getType().equals(optionalType) && !d.getType().equals(listType))
            .collect(Collectors.toCollection(ArrayList::new));
    return mandatoryObjects;
  }

  /**
   * Removes every optional Match object.
   *
   * @param allMatches list of all Match objects
   * @return a list of Matches without optional objects
   */
  public List<ASTMatchingObject> getMandatoryMatchObjects(List<ASTMatchingObject> allMatches) {
    ArrayList<ASTMatchingObject> mandatoryObjects = allMatches.stream()
            .filter(c -> !c.isOptObject()).collect(Collectors.toCollection(ArrayList::new));
    return mandatoryObjects;
  }

  /**
   * Removes every optional and list match object.
   *
   * @param allMatches list of all Match objects
   * @return a list of Matches without optional and list objects
   */
  public List<ASTMatchingObject> getMandatoryObjectsWithoutOptList(
          List<ASTMatchingObject> allMatches) {
    ArrayList<ASTMatchingObject> mandatoryObjects = allMatches.stream()
            .filter(c -> !c.isListObject() && !c.isOptObject())
            .collect(Collectors.toCollection(ArrayList<ASTMatchingObject>::new));
    return mandatoryObjects;
  }

  /**
   * Removes every mandatory Match object.
   *
   * @param allMatches list of all Match objects
   * @return a list of optional Match objects
   */
  public List<ASTMatchingObject> getOptionalMatchObjects(List<ASTMatchingObject> allMatches) {
    ArrayList<ASTMatchingObject> mandatoryObjects =
            allMatches.stream().filter(c -> (c.isOptObject() || c.getType().endsWith("IOptional")))
                    .collect(Collectors.toCollection(ArrayList::new));
    return mandatoryObjects;
  }

  /**
   * Checks if one of the InnerLinkObjects is an Optional object
   *
   * @param matches  list of matches to check
   * @param linkName the name of the object to look for
   * @return <code>true</code>, if the object with the given name is no optional
   */
  public boolean isNoOptionalName(List<ASTMatchingObject> matches, String linkName) {
    for (ASTMatchingObject linkObject : matches) {
      if (linkObject.getObjectName().equals(linkName) && linkObject.isOptObject()) {
        return false;
      }
    }
    return true;
  }

  /**
   * Removes every normal object.
   *
   * @param allObjects list of all Match-objects
   * @return a list of optional Match objects
   */
  public List<ASTMatchingObject> getOptListObjects(List<ASTMatchingObject> allObjects) {
    ArrayList<ASTMatchingObject> mandatoryObjects = allObjects.stream()
            .filter(c -> c.isOptObject() || c.isListObject())
            .collect(Collectors.toCollection(ArrayList::new));
    return mandatoryObjects;
  }

  /**
   * Checks if the object with the given name is within an optional structure.
   *
   * @param objectName the object name
   * @return whether the object is within an optional structure
   */
  public boolean isWithinOptionalStructure(String objectName) {
    if (isWithinStructure(lhs, objectName, ODRuleStereotypes.OPTIONAL)) {
      return true;
    } else if (rhs.isPresent() && isWithinStructure(rhs.get(), objectName, ODRuleStereotypes.OPTIONAL)) {
      return true;
    }
    return false;
  }

  /**
   * Checks if the object with the given name is within an negative structure
   * (not[[..]]-element).
   *
   * @param objectName the object name
   * @return whether the object is within a negative structure
   */
  public boolean isWithinNegativeStructure(String objectName) {
    if (isWithinNegativeStructure(lhs, objectName)) {
      return true;
    } else if (rhs.isPresent() && isWithinNegativeStructure(rhs.get(), objectName)) {
      return true;
    }
    return false;
  }

  public boolean isWithinListStructure(String objectName) {
    return isWithinStructure(lhs, objectName, ODRuleStereotypes.LIST);
  }

  /**
   * Checks if the object with the given name is within an structure with the
   * given stereotype.
   *
   * @param definition the ASTODDefinition (lhs or rhs)
   * @param objectName the object name
   * @param stereotype the structure type
   * @return whether the object is within a structure
   */
  private boolean isWithinStructure(ASTODDefinition definition, String objectName,
                                    String stereotype) {
    // check if the object is a direct child of the definition
    // because that means it is not within any hierarchical structure
    if (isDirectChild(definition, objectName)) {
      return false;
    } else {
      // get every structure with the given stereotype
      ArrayList<ASTODObject> structures = Util.getAllODObjects(definition).stream()
              .filter(odObj -> odObj.hasStereotype(stereotype))
              .collect(Collectors.toCollection(ArrayList::new));
      // get every structure without the given stereotype
      ArrayList<ASTODObject> notStructures = Util.getAllODObjects(definition).stream()
              .filter(odObj -> !odObj.hasStereotype(stereotype))
              .collect(Collectors.toCollection(ArrayList::new));

      // check if the object is a child of any relevant structure
      for (ASTODObject structure : structures) {
        if (isChild(structure, objectName)) {
/*          if (stereotype.equals(ODRuleStereotypes.LIST)) {
            for (ASTODObject notStructure : notStructures) {
              if (isChild(notStructure, objectName)) {
                return false;
              }
            }
          }*/
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Checks if the object with the given name is within a structure with the
   * stereotype not. Implementation is slightly different than for other
   * stereotypes as no abstract parent node is created for not.
   *
   * @param definition the ASTODDefinition (lhs or rhs)
   * @param objectName the object name
   * @return whether the object is within a structure
   */
  private boolean isWithinNegativeStructure(ASTODDefinition definition, String objectName) {
    // get every structure with the given stereotype
    ArrayList<ASTODObject> structures = Util.getAllODObjects(definition).stream()
            .filter(odObj -> odObj.hasStereotype(ODRuleStereotypes.NOT))
            .collect(Collectors.toCollection(ArrayList::new));

    // check if the object is a child of any relevant structure
    for (ASTODObject structure : structures) {
      if (isChild(structure, objectName)) {
        return true;
      } else if (structure.isPresentName()) {
        if (structure.getName().equals(objectName)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Checks if the ODDefinition has a direct child with the given name.
   *
   * @param def       the ODDefinition
   * @param childName the ODObject name
   * @return whether the object is a direct child of the definition
   */
  private boolean isDirectChild(ASTODDefinition def, String childName) {
    for (ASTODObject directChild : def.getODObjectList()) {
      if (directChild.isPresentName() && directChild.getName().equals(childName)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if the given object is a child of the given parent object.
   *
   * @param parent    the parent ODObject
   * @param childName the child ODObject
   * @return whether the object is a child of the definition
   */
  private boolean isChild(ASTODObject parent, String childName) {
    // getODObject performs a depth-first search if necessary
    ASTODObject result = Util.getODObject(parent, childName);
    return (result != null && !result.deepEquals(parent));
  }

  public static ASTMatchingObject getMatchingObject(List<ASTMatchingObject> allObjects, String name) {
    return allObjects.stream().filter(o -> o.getObjectName().equals(name)).findFirst().orElse(null);
  }

}
