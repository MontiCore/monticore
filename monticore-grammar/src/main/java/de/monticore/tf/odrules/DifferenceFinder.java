/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules;

import de.monticore.ast.ASTNode;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.se_rwth.commons.Names;
import de.monticore.tf.odrulegeneration._ast.ASTChangeOperation;
import de.monticore.tf.odrules._ast.ASTODDefinition;
import de.monticore.tf.odrules._ast.ASTODLink;
import de.monticore.tf.odrules._ast.ASTODObject;
import de.monticore.tf.odrules._ast.ASTODRule;
import de.monticore.tf.odrules._parser.ODRulesParser;
import de.monticore.tf.odrules.util.ODRuleStereotypes;
import de.monticore.tf.odrules.util.Util;

import java.io.IOException;
import java.util.*;

public class DifferenceFinder {


  private static class ChangePair<T extends ASTNode> {

    ChangePair(T from, T to) {
      this.from = from;
      this.to = to;
    }

    private T from;
    private T to;
  }


  private List<ASTODObject> toDeleteObjects = new ArrayList<>();
  private List<ASTODObject> toCreateObjects =  new ArrayList<>();
  private List<ASTODObject> unchangedObjects =  new ArrayList<>();
  private List<ChangePair<ASTODObject>> toChangeObjects = new LinkedList<>();

  private List<ASTODLink> toDeleteLinks = new LinkedList<>();
  private List<ASTODLink> toCreateLinks = new LinkedList<>();
  private List<ASTODLink> unchangedLinks = new LinkedList<>();
  private ASTODDefinition lhs;

  private HashMap<ASTODObject, List<ASTODLink>> toCreateObjectsAttr = new HashMap();

  private HierarchyHelper hierarchyHelper;

  public DifferenceFinder(HierarchyHelper hierarchyHelper) {
    this.hierarchyHelper = hierarchyHelper;
  }


  /**
   * @param transformationRulesFilename Filename to the mtod File that contains
   *          both lhs and rhs of the Rules.
   * @return
   *
   */
  public List<ASTChangeOperation> getDifference(String transformationRulesFilename) throws IOException {
    ODRulesParser parser = new ODRulesParser();
    Optional<ASTODRule> rule = parser.parse(transformationRulesFilename);
    return getDifference(rule.get());
  }


  /**
   * @param rule the parsed ODRule
   * @return returns the Composition of all change operations that have to be
   *         performed. Throws IllegalArgumentException, if the there are type
   *         mismatches in lhs rhs.
   */
  public List<ASTChangeOperation> getDifference(ASTODRule rule) {
    this.lhs = rule.getLhs();
    //clear all lists for new calculation
    toDeleteObjects = new ArrayList<>();
    toCreateObjects = new ArrayList<>();
    unchangedObjects = new ArrayList<>();
    toChangeObjects = new LinkedList<>();


    toDeleteLinks = new LinkedList<>();
    toCreateLinks = new LinkedList<>();
    unchangedLinks = new LinkedList<>();

    List<ASTODObject> leftObjects = Util.getAllODObjects(lhs);

    if(rule.isPresentRhs()){
      List<ASTODObject> rightObjects = Util.getAllODObjects(rule.getRhs());

      //calculate the list of deleted and the list of changed objects
      calculateObjectsToChangeOrDelete(leftObjects, rightObjects);

      //calculate the list of created objects
      calculateObjectsToCreate(leftObjects, rightObjects);

      List<ASTODLink> lhsLinks = lhs.getODLinkList();
      List<ASTODLink> rhsLinks = rule.getRhs().getODLinkList();

      // calculate the list of deleted links
      calculateLinksToDelete(lhsLinks, rhsLinks);

      //calculate the list of created links
      calculateLinksToCreate(lhsLinks, rhsLinks);

      //calculate the build order of created objects
      ODBuildOrder buildOrder = new ODBuildOrder(toCreateObjects, toCreateLinks);
      toCreateObjects = buildOrder.getBuildOrder();
      toCreateObjectsAttr = buildOrder.getBuildAttrs();

      //calculate and return the composition of all changes
      return calculateChanges(rule, lhs, rule.getRhs());
    }
    return new ArrayList<ASTChangeOperation>();
  }


  /**
   * Calculates the change between the LHS and the RHS
   * based on the fields toDeleteObjects, toCreateObjects,
   * toChangeObjects, toDeleteLinks, toCreateLinks
   *
   * @param lhs left hand side of the Rules
   * @param rhs right hand side of the Rules
   * @return the changeComposition containing all changes.
   */
  private List<ASTChangeOperation> calculateChanges(ASTODRule rule, ASTODDefinition lhs, ASTODDefinition rhs) {
    ChangeOperationFactory operationFactory = new ChangeOperationFactory(rule, lhs, rhs, hierarchyHelper);

    List<ASTChangeOperation> changeOpList = new ArrayList<ASTChangeOperation>();
    List<ASTODLink> toCreateLinksCopy = new LinkedList<>(toCreateLinks);

    for (ChangePair<ASTODObject> pair : toChangeObjects) {
      changeOpList.add(operationFactory.createChangeOperation(pair.from, pair.to));
    }
    for (ASTODObject obj : toCreateObjects) {
      List<ASTODLink> relLinks = new LinkedList<>();
      for (ASTODLink link : toCreateObjectsAttr.get(obj)) {
        toCreateLinksCopy.remove(link);
        relLinks.add(link);
      }
      changeOpList.add(operationFactory.createCreateOperation(obj, relLinks));
    }
    for (ASTODLink link : toDeleteLinks) {
      String targetName = Names.constructQualifiedName(link.getRightReferenceName(0).getPartsList());
      ASTODObject targetObject = Util.getODObject(lhs, targetName);
      if(!targetObject.hasStereotype(ODRuleStereotypes.NOT)){
        changeOpList.add(operationFactory.createDeleteOperation(link));
      }
    }
    for (ASTODLink link : toCreateLinksCopy) {
      changeOpList.add(operationFactory.createCreateOperation(link));
    }
    for (ASTODObject obj : toDeleteObjects) {
      changeOpList.add(operationFactory.createDeleteOperation(obj));
    }
    // unchanged objects do not need to be listed anywhere.
    return changeOpList;
  }

  /**
   * Calculates the list of deleted objects stored in the field toDeleteObjects
   * and the list of changed objects stored in the field toChangeObjects
   *
   * @param leftObjects List of objects from the LHS
   * @param rightObjects List of objects from the RHS
   */
  private void calculateObjectsToChangeOrDelete(List<ASTODObject> leftObjects, List<ASTODObject> rightObjects){
    for (ASTODObject left : leftObjects) {
      // says, weather the variable from the left side is found on the
      // right side.
      boolean found = false;
      // for all rules on the left side, find the matching rule on the
      // right side.
      if (!left.hasStereotype(ODRuleStereotypes.NOT)) {
        for (ASTODObject right : rightObjects) {
          if (left.getName().equals(right.getName())) {
            // deepEquals is too strong but if there is nothing to
            // change the factory doesn't add any changes
            // needs to be replaced by another equals, if you need a
            // correct list of unchanged Objects.
            if (left.deepEquals(right)) {
              unchangedObjects.add(left.deepClone());
            } else {
              toChangeObjects.add(new ChangePair<>(left.deepClone(), right.deepClone()));
            }
            found = true;
          }
        }
        // Objects only on left side should be deleted
        if (!found) {
          toDeleteObjects.add(left.deepClone());
        }
      }
    }
  }

  /**
   * Calculates the list of created objects stored in the field toCreateObjects
   *
   * @param leftObjects List of objects from the LHS
   * @param rightObjects List of objects from the RHS
   */
  private void calculateObjectsToCreate(List<ASTODObject> leftObjects, List<ASTODObject> rightObjects){
    for (ASTODObject right : rightObjects) {
      // says, weather the variable from the right side is found on the
      // left side.
      boolean found = false;
      // there must be a better way than iterating the whole object list
      // on the right
      for (ASTODObject left : leftObjects) {
        if (left.getName().equals(right.getName())) {
          found = true;
        }
      }
      if (!found) {
        toCreateObjects.add(right.deepClone());
      }
    }

  }

  /**
   * Calculates the list of deleted links stored in the field toDeleteLinks
   * and the list of changed links stored in the field toChangeLinks
   *
   * @param lhsLinks List of links from the LHS
   * @param rhsLinks List of links from the RHS
   */
  private void calculateLinksToDelete(List<ASTODLink> lhsLinks, List<ASTODLink> rhsLinks) {
    for (ASTODLink left : lhsLinks) {
      // says, whether the variable from the left side is found on the
      // right side.
      boolean found = false;
      boolean isSetValued = left.isAttributeIterated();
  
      // for all rules on the left side, find the matching rule on the
      // right side. If the multiplicity at the right end is larger than 1,
      // then any link with the same source object and the same role is
      // a match
        for (ASTODLink right : rhsLinks) {
          boolean match;
          boolean unchanged;
          if (isSetValued) {
            match = isMatchForSetValuedLink(left, right);
            unchanged = match;
          }
          else {
            match = isMatchForLink(left, right);
            unchanged = match && referencesAreEqual(left.getRightReferenceNameList(),
                right.getRightReferenceNameList());
          }
          if (match) {
            if (unchanged) {
              unchangedLinks.add(left);
            }
            found = true;
          }
        }
        // Objects only on left side should be deleted
        if (!found) {
          toDeleteLinks.add(left);
        }
  
    }
  }

  private boolean referencesAreEqual(List<ASTMCQualifiedName> rightReferenceNames,
      List<ASTMCQualifiedName> rightReferenceNames1) {
    return rightReferenceNames.get(0).deepEquals(rightReferenceNames1.get(0));
  }

  private boolean isMatchForSetValuedLink(ASTODLink left, ASTODLink right){
    return (referencesAreEqual(left.getLeftReferenceNameList(),right.getLeftReferenceNameList())
        && referencesAreEqual(left.getRightReferenceNameList(),right.getRightReferenceNameList()))
        && areRolesEqual(left, right);
  }

  private boolean areRolesEqual(ASTODLink left, ASTODLink right){
    return ((left.getRightRole() == null && right.getRightRole() == null)
        || (left.getRightRole() != null && left.getRightRole().equals(right.getRightRole())));
  }

  private boolean isMatchForLink(ASTODLink left, ASTODLink right){
    return referencesAreEqual(left.getLeftReferenceNameList(),right.getLeftReferenceNameList())
        && areRolesEqual(left, right);
  }

  /**
   * Calculates the list of created links stored in the field toCreateLinks
   *
   * @param lhsLinks List of links from the LHS
   * @param rhsLinks List of links from the RHS
   */
  private void calculateLinksToCreate(List<ASTODLink> lhsLinks, List<ASTODLink> rhsLinks){
    for (ASTODLink right : rhsLinks) {
      // says, weather the variable from the right side is found on the
      // left side.
      boolean found = false;
      // there must be a better way than iterating the whole object list
      // on the right
      for (ASTODLink left : lhsLinks) {
        if (left.deepEquals(right)) {
            found = true;
        }

      }

      for(ASTODLink former : rhsLinks){
        if(!right.deepEquals(former) &&  referencesAreEqual(former.getRightReferenceNameList(),right.getRightReferenceNameList())){
          ASTStereotype stereotype = ODRulesMill.stereotypeBuilder().build();
          ASTStereoValue value = ODRulesMill.stereoValueBuilder().setName("copy").setContent("").build();
          stereotype.getValuesList().add(value);
          right.setStereotype(stereotype);
        }
      }
      if (!found) {
        toCreateLinks.add(right);
      }
    }
  }

}
