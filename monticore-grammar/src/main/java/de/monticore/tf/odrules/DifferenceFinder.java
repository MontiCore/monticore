/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules;

import de.monticore.tf.odrulegeneration._ast.ASTChangeOperation;
import de.monticore.tf.odrules._ast.ASTODDefinition;
import de.monticore.tf.odrules._ast.ASTODLink;
import de.monticore.tf.odrules._ast.ASTODObject;
import de.monticore.tf.odrules._ast.ASTODRule;
import de.monticore.tf.odrules.util.ODRuleStereotypes;
import de.monticore.tf.odrules.util.Util;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.se_rwth.commons.Names;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Calculates the change operations, which transform the lhs of a rule into its rhs.
 * Objects and links are matched by their names.
 */
public class DifferenceFinder {

  protected final HierarchyHelper hierarchyHelper;

  protected record ObjectChange(ASTODObject from, ASTODObject to) {
  }

  public DifferenceFinder(HierarchyHelper hierarchyHelper) {
    this.hierarchyHelper = hierarchyHelper;
  }

  /**
   * @param transformationRulesFilename the mtod file that contains both lhs and rhs of the rule
   * @return the change operations, see {@link #getDifference(ASTODRule)}
   */
  public List<ASTChangeOperation> getDifference(String transformationRulesFilename) throws IOException {
    return getDifference(ODRulesMill.parser().parse(transformationRulesFilename).orElseThrow());
  }

  /**
   * Calculates the change operations in the following order: changes of objects, creation of
   * objects (including the links set during their creation), deletion of links, creation of
   * links, and deletion of objects. Unchanged objects and links do not result in operations.
   *
   * @param rule the parsed ODRule
   * @return the change operations, which have to be performed
   */
  public List<ASTChangeOperation> getDifference(ASTODRule rule) {
    if (!rule.isPresentRhs()) {
      return new ArrayList<>();
    }
    ASTODDefinition lhs = rule.getLhs();
    ASTODDefinition rhs = rule.getRhs();
    List<ASTODObject> leftObjects = Util.getAllODObjects(lhs);
    List<ASTODObject> rightObjects = Util.getAllODObjects(rhs);
    List<ASTODLink> leftLinks = lhs.getODLinkList();
    List<ASTODLink> rightLinks = rhs.getODLinkList();

    // objects only on the lhs are deleted, objects on both sides may be changed
    Map<String, ASTODObject> rightObjectsByName = new HashMap<>();
    rightObjects.forEach(o -> rightObjectsByName.putIfAbsent(o.getName(), o));
    List<ObjectChange> objectsToChange = new ArrayList<>();
    List<ASTODObject> objectsToDelete = new ArrayList<>();
    for (ASTODObject left : leftObjects) {
      if (left.hasStereotype(ODRuleStereotypes.NOT)) {
        continue;
      }
      ASTODObject right = rightObjectsByName.get(left.getName());
      if (right == null) {
        objectsToDelete.add(left.deepClone());
      } else if (!left.deepEquals(right)) {
        objectsToChange.add(new ObjectChange(left.deepClone(), right.deepClone()));
      }
    }

    // objects only on the rhs are created
    Set<String> leftObjectNames = leftObjects.stream()
        .map(ASTODObject::getName)
        .collect(Collectors.toSet());
    List<ASTODObject> objectsToCreate = rightObjects.stream()
        .filter(o -> !leftObjectNames.contains(o.getName()))
        .map(ASTODObject::deepClone)
        .collect(Collectors.toList());

    // links without a counterpart on the other side are deleted or created, respectively
    List<ASTODLink> linksToDelete = leftLinks.stream()
        .filter(left -> rightLinks.stream().noneMatch(right -> isKept(left, right)))
        .filter(left -> !getTarget(left, lhs).hasStereotype(ODRuleStereotypes.NOT))
        .collect(Collectors.toList());
    List<ASTODLink> linksToCreate = rightLinks.stream()
        .filter(right -> leftLinks.stream().noneMatch(right::deepEquals))
        .collect(Collectors.toList());

    // must happen after the links have been compared, as it modifies the rhs
    markCopiedLinks(rightLinks);

    ChangeOperationFactory factory = new ChangeOperationFactory(rule, lhs, rhs, hierarchyHelper);
    List<ASTChangeOperation> changes = new ArrayList<>();
    for (ObjectChange change : objectsToChange) {
      changes.add(factory.createChangeOperation(change.from(), change.to()));
    }
    // objects are created bottom-up, together with the links to their children
    ODBuildOrder buildOrder = new ODBuildOrder(objectsToCreate, linksToCreate);
    Map<ASTODObject, List<ASTODLink>> linksOfCreatedObjects = buildOrder.getBuildAttrs();
    Set<ASTODLink> linksCreatedWithObjects = new HashSet<>();
    for (ASTODObject object : buildOrder.getBuildOrder()) {
      List<ASTODLink> links = linksOfCreatedObjects.get(object);
      linksCreatedWithObjects.addAll(links);
      changes.add(factory.createCreateOperation(object, new ArrayList<>(links)));
    }
    for (ASTODLink link : linksToDelete) {
      changes.add(factory.createDeleteOperation(link));
    }
    for (ASTODLink link : linksToCreate) {
      if (!linksCreatedWithObjects.contains(link)) {
        changes.add(factory.createCreateOperation(link));
      }
    }
    for (ASTODObject object : objectsToDelete) {
      changes.add(factory.createDeleteOperation(object));
    }
    return changes;
  }

  /**
   * A link of the lhs is kept, if a link of the rhs connects the same source using the same
   * role. For iterated attributes, it has to connect the same target, too.
   */
  protected boolean isKept(ASTODLink left, ASTODLink right) {
    return isSameReference(left.getLeftReferenceNameList(), right.getLeftReferenceNameList())
        && hasSameRightRole(left, right)
        && (!left.isAttributeIterated()
            || isSameReference(left.getRightReferenceNameList(), right.getRightReferenceNameList()));
  }

  protected boolean hasSameRightRole(ASTODLink left, ASTODLink right) {
    if (left.isPresentRightRole() != right.isPresentRightRole()) {
      return false;
    }
    return !left.isPresentRightRole() || left.getRightRole().equals(right.getRightRole());
  }

  protected boolean isSameReference(List<ASTMCQualifiedName> left, List<ASTMCQualifiedName> right) {
    return left.getFirst().deepEquals(right.getFirst());
  }

  protected ASTODObject getTarget(ASTODLink link, ASTODDefinition definition) {
    return Util.getODObject(definition,
        Names.constructQualifiedName(link.getRightReferenceName(0).getPartsList()));
  }

  /**
   * Marks the links of the rhs, whose target is also the target of another link,
   * with the stereotype {@value ODRuleStereotypes#COPY}.
   * Note that this replaces any other stereotypes of these links.
   */
  protected void markCopiedLinks(List<ASTODLink> rightLinks) {
    List<ASTODLink> copiedLinks = rightLinks.stream()
        .filter(link -> rightLinks.stream().anyMatch(other -> !link.deepEquals(other)
            && isSameReference(link.getRightReferenceNameList(), other.getRightReferenceNameList())))
        .collect(Collectors.toList());
    for (ASTODLink link : copiedLinks) {
      ASTStereotype stereotype = ODRulesMill.stereotypeBuilder().build();
      stereotype.addValues(ODRulesMill.stereoValueBuilder().setName(ODRuleStereotypes.COPY).build());
      link.setStereotype(stereotype);
    }
  }

}
