/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import de.monticore.tf.odrulegeneration._ast.*;
import de.monticore.tf.odrules._ast.ASTODDefinition;
import de.monticore.tf.odrules._ast.ASTODInnerLink;
import de.monticore.tf.odrules._ast.ASTODObject;
import de.monticore.tf.odrules._ast.ASTODRule;
import de.monticore.tf.odrules.util.ODRuleStereotypes;
import de.monticore.tf.odrules.util.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility to analyze containment hierarchies of OD rule objects.
 *
 * <p>The helper precomputes list-child relations for LHS and RHS and offers
 * convenience filters for mandatory/optional/list-related matching and change objects.
 */
public final class HierarchyHelper {
  private final static String optionalType = "de.monticore.tf.ast.IOptional";
  private final static String listType = "de.monticore.tf.ast.IList";

  private ASTODRule rule;
  private ASTODDefinition lhs;
  private Optional<ASTODDefinition> rhs = Optional.empty();

  private Multimap<String, String> listChildPairs = LinkedListMultimap.create();
  private Multimap<String, String> listChildPairsLhs = LinkedListMultimap.create();
  private Multimap<String, String> listChildPairsWithOptionals = LinkedListMultimap.create();

  private List<String> listChildNames = new ArrayList<>();
  private List<String> listChildNamesLhs = new ArrayList<>();
  private List<String> listChildNamesRhs = new ArrayList<>();

  private String packageName = "";
  private final List<String> customImports = new ArrayList<>();

  public HierarchyHelper() {
    rule = ODRulesMill.oDRuleBuilder().uncheckedBuild();
    lhs = ODRulesMill.oDDefinitionBuilder().uncheckedBuild();
  }

  public HierarchyHelper(@Nonnull ASTODRule astodRule) {
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
    Multimap<String, String> rhsListChildPairs =
        rhs.map(astodDefinition -> getListChildPairs(astodDefinition.getODObjectList()))
            .orElseGet(LinkedListMultimap::create);
    for (String key : rhsListChildPairs.keySet()) {
      // Every list on the lhs is also on the rhs
      // If there are objects to create in a list put them to the Map
      if (!new HashSet<>(listChildPairs.get(key)).containsAll(rhsListChildPairs.get(key))) {
        List<String> temporary = new ArrayList<>(rhsListChildPairs.get(key));
        // No duplicates
        temporary.removeAll(listChildPairs.get(key));
        temporary.addAll(listChildPairs.get(key));
        listChildPairs.putAll(key, temporary);
      }
      listChildNamesRhs.addAll(rhsListChildPairs.get(key));
    }
    // Fill names for quick check
    for (String key : listChildPairs.keySet()) {
      listChildNames.addAll(listChildPairs.get(key));
    }
  }

  /**
   * Checks whether the object with the given name is marked as a list object.
   *
   * @param objectName the name of the object
   * @return {@code true} if the object exists and has the list stereotype
   */
  public boolean isListObject(@Nonnull String objectName) {

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
    return obj != null && obj.hasStereotype(ODRuleStereotypes.LIST);
  }

  /**
   * Get package name for code generation
   *
   * @return configured package name, or an empty string if none was configured
   */
  @Nonnull
  public String getPackageName() {
    return packageName;
  }

  /**
   * Set package name for code generation
   *
   * @param packageName package name to use during code generation
   */
  public void setPackageName(@Nonnull String packageName) {
    this.packageName = packageName;
  }

  /**
   * Check if package name for code generation is set
   *
   * @return {@code true} if a non-empty package name is configured
   */
  public boolean packageisPresentName() {
    return !"".equals(packageName);
  }

  /**
   * Get all custom imports for code generation
   *
   * @return mutable list of additional imports for generated code
   */
  @Nonnull
  public List<String> getCustomImports() {
    return customImports;
  }

  /**
   * Add custom import for code generation
   *
   * @param customImport fully qualified import string
   */
  public void addCustomImports(@Nonnull String customImport) {
    customImports.add(customImport);
  }

  /**
   * Calculates all Lists and their children, saves also all children seperately
   *
   * @param objects List of objects to be checked for Lists
   * @return A mapping for each list to their children
   */
  private Multimap<String, String> getListChildPairs(
          List<ASTODObject> objects) {
    Multimap<String, String> result = HashMultimap.create();
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
          result.putAll(object.getName(), childs);
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

  private Multimap<String, String> getListChildPairsWithOptionals(
          List<ASTODObject> objects) {
    Multimap<String, String> result = LinkedListMultimap.create();
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
          result.putAll(object.getName(), childs);
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
   * Resolves all named inner-link objects of a given LHS match object.
   *
   * @param obj object whose inner link names should be resolved
   * @param allMatches all available LHS match objects
   * @return all resolved inner-link objects in declaration order
   */
  @Nonnull
  public List<ASTMatchingObject> getInnerLinkObjectsLHS(@Nonnull List<ASTMatchingObject> allMatches,
                                                        @Nonnull ASTMatchingObject obj) {
    ArrayList<ASTMatchingObject> innerObjects = new ArrayList<>();

    for (String innerObjectName : obj.getInnerLinkObjectNamesList()) {

      Optional<ASTMatchingObject> innerLinkObject = allMatches.stream()
              .filter(m -> m.getObjectName().equals(innerObjectName)).findAny();
      innerLinkObject.ifPresent(innerObjects::add);
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
   * Returns all match objects that represent list objects.
   *
   * @param allObjects list of all objects
   * @return all objects that are marked as list objects
   */
  public List<ASTMatchingObject> getListObjects(List<ASTMatchingObject> allObjects) {
    return allObjects.stream()
            .filter(ASTMatchingObject::isListObject).collect(Collectors.toCollection(ArrayList::new));
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
              mandatoryObjects.removeIf(
                  mandatoryObject -> mandatoryObject.getObjectName().equals(innerLinkName));
            }
          }
        }
      }
      if (object.isOptObject() || object.getType().endsWith("IOptional")) {
        for (int i = 0; i <= object.getInnerLinkObjectNamesList().size(); i++) {
          int index = allMatches.indexOf(object);
          if (allMatches.get(index + i).isListObject() || allMatches.get(index + i).getType().endsWith("IList")) {
            for (String innerLinkName : allMatches.get(index + i).getInnerLinkObjectNamesList()) {
              mandatoryObjects.removeIf(
                  mandatoryObject -> mandatoryObject.getObjectName().equals(innerLinkName));
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
    return allMatches.stream()
            .filter(c -> !isWithinListStructure(c.getObjectName()))
            .collect(Collectors.toCollection(ArrayList<ASTMatchingObject>::new));
  }

  /**
   * Returns all direct children of a specific list object (based on LHS precomputation).
   *
   * @param allObjects all available match objects
   * @param list target list object
   * @return all match objects that are children of {@code list}
   */
  public List<ASTMatchingObject> getListChilds(@Nonnull List<ASTMatchingObject> allObjects,
                                               @Nonnull ASTMatchingObject list) {
    ArrayList<ASTMatchingObject> mandatoryObjects = allObjects.stream()
            .filter(c -> listChildPairsLhs.get(list.getObjectName()).contains(c.getObjectName()))
            .collect(Collectors.toCollection(ArrayList::new));
    return mandatoryObjects;
  }

  /**
   * Returns all direct children of a specific list object, including optional wrapper objects.
   *
   * @param allobjects all available match objects
   * @param list target list object
   * @return all child objects of {@code list}, including optionals
   */
  public List<ASTMatchingObject> getListChildsWithOptionals(@Nonnull List<ASTMatchingObject> allobjects,
                                                            @Nonnull ASTMatchingObject list) {
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
   * Returns the nearest list parent name for the given object name.
   *
   * @param object name of the child object
   * @return containing list name, or {@code null} if the object is not inside any list structure
   */
  @Nullable
  public String getListParent(@Nonnull String object) {
    for (String key : listChildPairs.keySet()) {
      if (listChildPairs.get(key).contains(object)) {
        return key;
      }
    }
    return null;
  }

  /**
   * Computes the list hierarchy path for an object.
   *
   * @param object The Object for which we want to find a path in the
   *               List-Structure Tree
   * @return A List starting with the root-list and ending with the list
   * containing the given Object.
   */
  @Nonnull
  public List<String> getListTree(@Nonnull String object) {
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
   * Resolves an object by name from a list.
   *
   * @param allObjects candidate objects
   * @param name object name to search
   * @return matching object, or {@code null} if no object with this name exists
   */
  @Nullable
  public ASTMatchingObject getObjectByName(@Nonnull List<ASTMatchingObject> allObjects, @Nonnull String name) {
    for (ASTMatchingObject obj : allObjects) {
      if (obj.getObjectName().equals(name)) {
        return obj;
      }
    }
    return null;
  }

  /**
   * Filters out optional and list wrapper change objects.
   *
   * @param allChanges list of all Change objects
   * @return all changes excluding optional/list wrapper types
   */
  public List<ASTChange> getMandatoryChangeObjects(List<ASTChange> allChanges) {
    return allChanges.stream()
            .filter(c -> !c.getType().equals(optionalType) && !c.getType().equals(listType))
            .collect(Collectors.toCollection(ArrayList::new));
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

  /**
   * Returns all changes except those that are also declared as create operations.
   *
   * @param replacements replacement block containing creates and changes
   * @return changes that reference already existing objects
   */
  public List<ASTChange> getChangeObjectsWhithoutCreate(ASTReplacement replacements) {
    Set<String> createStrings = replacements.getCreateObjectsList().stream().map(ASTCreateOperation::getName).collect(Collectors.toSet());
    return replacements.getChangesList().stream().filter(m -> !createStrings.contains(m.getObjectName())).collect(Collectors.toCollection(ArrayList::new));
  }
  
  /**
   * Checks whether the given object is created by the replacement.
   *
   * @param replacement replacement block
   * @param objectName object name to check
   * @return {@code true} if a create operation exists for {@code objectName}
   */
  public boolean isCreatedObject(ASTReplacement replacement, String objectName) {
    return replacement.getCreateObjectsList().stream().anyMatch(c -> c.getName().equals(objectName));
  }

    /**
     * Returns changes for objects inside list structures, excluding optional/list wrapper nodes.
     *
     * @param allChanges list of all Change objects
     * @return changes whose target object is nested in a list structure
     */
  public List<ASTChange> getMandatoryChangeObjectsOnlyList(List<ASTChange> allChanges) {
    ArrayList<ASTChange> mandatoryObjects = allChanges.stream()
            .filter(c -> !c.getType().equals(optionalType) && !c.getType().equals(listType))
            .filter(a -> isWithinListStructure(a.getObjectName()))
            .collect(Collectors.toCollection(ArrayList::new));
    return mandatoryObjects;
  }

  /**
   * Filters out optional and list wrapper delete objects.
   *
   * @param allDeletes list of all Delete objects
   * @return all deletes excluding optional/list wrapper types
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
   * @return {@code true} if the object with the given name is not optional
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
   * @return whether the object is a descendant of {@code parent}
   */
  private boolean isChild(ASTODObject parent, String childName) {
    // getODObject performs a depth-first search if necessary
    ASTODObject result = Util.getODObject(parent, childName);
    return (result != null && !result.deepEquals(parent));
  }

  /**
   * Resolves the first matching object with the given name from a list.
   *
   * @param allObjects candidate objects
   * @param name searched object name
   * @return matching object, or {@code null} if no object has the given name
   */
  @Nullable
  public static ASTMatchingObject getMatchingObject(@Nonnull List<ASTMatchingObject> allObjects, @Nonnull String name) {
    return allObjects.stream().filter(o -> o.getObjectName().equals(name)).findFirst().orElse(null);
  }

}
