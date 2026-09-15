/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
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
import javax.annotation.Nonnull;

import java.io.IOException;
import java.util.*;

public class DifferenceFinder {
  
  private record ChangePair<T extends ASTNode>(@Nonnull T from, @Nonnull T to) {
  
  }


  private List<ASTODObject> toDeleteObjects = new ArrayList<>();
  private List<ASTODObject> toCreateObjects =  new ArrayList<>();
  private List<ASTODObject> unchangedObjects =  new ArrayList<>();
  private List<ChangePair<ASTODObject>> toChangeObjects = new LinkedList<>();

  private List<ASTODLink> toDeleteLinks = new LinkedList<>();
  private List<ASTODLink> toCreateLinks = new LinkedList<>();
  private List<ASTODLink> unchangedLinks = new LinkedList<>();
  private ASTODDefinition lhs;

  private Multimap<ASTODObject, ASTODLink> toCreateObjectsAttr = LinkedListMultimap.create();

  private HierarchyHelper hierarchyHelper;

  public DifferenceFinder(@Nonnull HierarchyHelper hierarchyHelper) {
    this.hierarchyHelper = hierarchyHelper;
  }


  /**
   * Parses a transformation rule file and calculates the resulting change operations.
   *
   * @param transformationRulesFilename the path to the *.mtod file containing both LHS and RHS
   * @return the ordered list of change operations; never {@code null}
   * @throws IOException if the file cannot be read
   */
  public @Nonnull List<ASTChangeOperation> getDifference(@Nonnull String transformationRulesFilename) throws IOException {
    ODRulesParser parser = ODRulesMill.parser();
    Optional<ASTODRule> rule = parser.parse(transformationRulesFilename);
    return getDifference(rule.get());
  }


  /**
   * Calculates the difference between the left-hand side and right-hand side of one rule.
   *
   * @param rule the parsed rule
   * @return the ordered composition of change operations; never {@code null}
   * @throws IllegalArgumentException if there are type mismatches between LHS and RHS
   */
  public @Nonnull List<ASTChangeOperation> getDifference(@Nonnull ASTODRule rule) {
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
    return new ArrayList<>();
  }


  /**
   * Composes all collected object and link updates into executable change operations.
   * This method uses the previously computed state in {@code toDeleteObjects},
   * {@code toCreateObjects}, {@code toChangeObjects}, {@code toDeleteLinks}, and
   * {@code toCreateLinks}.
   *
   * @param rule the full transformation rule
   * @param lhs the left-hand side object diagram
   * @param rhs the right-hand side object diagram
   * @return the ordered change operations; never {@code null}
   */
  private @Nonnull List<ASTChangeOperation> calculateChanges(@Nonnull ASTODRule rule,
      @Nonnull ASTODDefinition lhs,
      @Nonnull ASTODDefinition rhs) {
    ChangeOperationFactory operationFactory = new ChangeOperationFactory(rule, lhs, rhs, hierarchyHelper);

    List<ASTChangeOperation> changeOpList = new ArrayList<>();
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
   * Calculates deleted and changed objects by comparing LHS objects with RHS objects.
   * Results are stored in {@code toDeleteObjects} and {@code toChangeObjects}.
   *
   * @param leftObjects objects from the LHS
   * @param rightObjects objects from the RHS
   */
  private void calculateObjectsToChangeOrDelete(@Nonnull List<ASTODObject> leftObjects,
      @Nonnull List<ASTODObject> rightObjects) {
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
   * Calculates objects that are present on the RHS but missing on the LHS.
   * Results are stored in {@code toCreateObjects}.
   *
   * @param leftObjects objects from the LHS
   * @param rightObjects objects from the RHS
   */
  private void calculateObjectsToCreate(@Nonnull List<ASTODObject> leftObjects,
      @Nonnull List<ASTODObject> rightObjects) {
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
   * Calculates links that are present on the LHS but missing on the RHS.
   * Results are stored in {@code toDeleteLinks} and {@code unchangedLinks}.
   *
   * @param lhsLinks links from the LHS
   * @param rhsLinks links from the RHS
   */
  private void calculateLinksToDelete(@Nonnull List<ASTODLink> lhsLinks,
      @Nonnull List<ASTODLink> rhsLinks) {
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

  private boolean referencesAreEqual(@Nonnull List<ASTMCQualifiedName> rightReferenceNames,
      @Nonnull List<ASTMCQualifiedName> rightReferenceNames1) {
    return rightReferenceNames.getFirst().deepEquals(rightReferenceNames1.getFirst());
  }

  private boolean isMatchForSetValuedLink(@Nonnull ASTODLink left, @Nonnull ASTODLink right) {
    return (referencesAreEqual(left.getLeftReferenceNameList(),right.getLeftReferenceNameList())
        && referencesAreEqual(left.getRightReferenceNameList(),right.getRightReferenceNameList()))
        && areRolesEqual(left, right);
  }

  private boolean areRolesEqual(@Nonnull ASTODLink left, @Nonnull ASTODLink right) {
    return ((left.getRightRole() == null && right.getRightRole() == null)
        || (left.getRightRole() != null && left.getRightRole().equals(right.getRightRole())));
  }

  private boolean isMatchForLink(@Nonnull ASTODLink left, @Nonnull ASTODLink right) {
    return referencesAreEqual(left.getLeftReferenceNameList(),right.getLeftReferenceNameList())
        && areRolesEqual(left, right);
  }

  /**
   * Calculates links that are present on the RHS but missing on the LHS.
   * Results are stored in {@code toCreateLinks}.
   *
   * @param lhsLinks links from the LHS
   * @param rhsLinks links from the RHS
   */
  private void calculateLinksToCreate(@Nonnull List<ASTODLink> lhsLinks,
      @Nonnull List<ASTODLink> rhsLinks) {
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
          ASTStereoValue value = ODRulesMill.stereoValueBuilder().setName("copy").build();
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
