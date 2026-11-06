/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableInit;
import de.monticore.tf.odrulegeneration.ODRuleGenerationMill;
import de.monticore.tf.odrulegeneration._ast.ASTDependency;
import de.monticore.tf.odrulegeneration._ast.ASTObjectCondition;
import de.monticore.tf.odrules._ast.ASTODAttribute;
import de.monticore.tf.odrules._ast.ASTODDefinition;
import de.monticore.tf.odrules._ast.ASTODLink;
import de.monticore.tf.odrules._ast.ASTODObject;
import de.monticore.tf.odrules._visitor.ODRulesVisitor2;
import de.monticore.tf.odrules.util.ODRuleStereotypes;
import de.monticore.tf.odrules.util.TFExpressionFullPrettyPrinter;
import de.monticore.tf.odrules.util.Util;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static de.se_rwth.commons.StringTransformations.capitalize;
import static de.se_rwth.commons.StringTransformations.uncapitalize;
import static java.lang.String.format;

/**
 * Creates the conditions, which the candidates for the objects of the lhs of a rule have to
 * fulfill. Each condition string describes a violation, i.e., a candidate is rejected,
 * if it evaluates to true.
 */
public class GenerateConditionsVisitor implements ODRulesVisitor2 {

  protected static final String REGEX = "\\.";
  protected static final String CAND = "cand";
  protected static final String CAND_SUFFIX = "_cand";
  protected static final String GET = ".get()";
  protected static final String GET_ATTRIBUTE = ".get%s()";

  protected final List<ASTObjectCondition> objectConditions = new ArrayList<>();

  protected final Map<String, ASTODObject> objectsByName = new HashMap<>();

  /** the object, whose attributes are currently visited */
  protected ASTODObject object;
  protected String objectName = "";

  /** the printed value of the attribute, which is currently visited */
  protected String attrValue = "";

  public List<ASTObjectCondition> getObjectConditions() {
    return objectConditions;
  }

  @Override
  public void visit(ASTODDefinition node) {
    Util.getAllODObjects(node).forEach(o -> objectsByName.putIfAbsent(o.getName(), o));
  }

  @Override
  public void visit(ASTODObject node) {
    object = node;
    if (node.isPresentName()) {
      objectName = node.getName();
    } else {
      Log.error("0xF0001: Object name must be set.");
    }
  }

  @Override
  public void visit(ASTODAttribute node) {
    if (node.isPresentSingleValue()) {
      attrValue = Util.printExpression(node.getSingleValue());
    } else if (node.isPresentList()) {
      attrValue = Util.printListValue(node.getList());
    } else {
      Log.error("0xF0002: Object value must be set.");
    }

    Optional<ASTODObject> targetObject = getODObject(createObjectString(attrValue));
    if ("boolean".equals(node.printType())) {
      createConditionsForBoolean(node, targetObject);
    } else if ("int".equals(node.printType())) {
      createConditionsForInt(node);
    } else if (node.isPresentList()) {
      createConditionsForListWithoutTarget(node);
    } else {
      createConditions(node, object, targetObject);
    }
  }

  /**
   * Adds the conditions, which belong to an object
   */
  protected void addConditions(ASTObjectCondition... conditions) {
    for (ASTObjectCondition condition : conditions) {
      if (condition.getObjectName() != null && !condition.getObjectName().isEmpty()) {
        objectConditions.add(condition);
      }
    }
  }

  protected ASTObjectCondition createCondition(String objectName) {
    ASTObjectCondition condition = ODRuleGenerationMill.objectConditionBuilder().uncheckedBuild();
    condition.setObjectName(objectName);
    return condition;
  }

  protected ASTObjectCondition createCondition(String objectName, String conditionString) {
    ASTObjectCondition condition = createCondition(objectName);
    condition.setConditionString(conditionString);
    return condition;
  }

  /**
   * The condition depends on the candidate of the given object (if any)
   */
  protected void setDependency(ASTObjectCondition condition, String dependsOn) {
    if (!dependsOn.isEmpty()) {
      ASTDependency dependency = ODRuleGenerationMill.dependencyBuilder().uncheckedBuild();
      dependency.setContent(dependsOn);
      condition.setDependency(dependency);
    }
  }

  protected void createConditions(ASTODAttribute node, ASTODObject object, Optional<ASTODObject> targetObject) {
    boolean isSameObject = targetObject.isPresent() && targetObject.get().getName().equals(objectName);
    boolean targetOptional = isTargetOptional(node);
    String getter = createGetterStatement(node);
    if (isSameObject) {
      // replace <objectName>_cand.getAttr() with cand.getAttr()
      getter = getter.replace(objectName + CAND_SUFFIX, CAND);
    }

    String attributeAccess = getAttributeAccess(node);
    String condition = format("(!cand.%s.equals(%s))", attributeAccess, getter);
    if (node.isOptional()) {
      condition = format("(!cand.isPresent%s() || ! cand.%s.equals(%s))",
          capitalize(node.getName()), attributeAccess, getter);
    }

    ASTObjectCondition objectCondition = createCondition(objectName);
    objectCondition.setTargetOptional(targetOptional);
    objectCondition.setAttribute(node);
    if (targetObject.isPresent() && !isSameObject) {
      objectCondition.setTarget(targetObject.get());
    } else {
      objectCondition.setTargetAbsent();
    }

    if (targetOptional) {
      objectCondition.setConditionString(format("%s && %s", createPresentCheckStatement(getter), condition));
    } else if (getter.endsWith(GET)) {
      objectCondition.setConditionString(getter.replace(GET, ".isPresent()") + " && " + condition);
    } else if (getter.startsWith("$") && !getter.contains(".")) {
      objectCondition.setConditionString(format("%s_is_fix && %s", getter, condition));
    } else if (getter.startsWith("$")) {
      objectCondition.setConditionString(format("%s != null && %s", getter, condition));
    } else {
      objectCondition.setConditionString(condition);
    }

    // the same check from the perspective of the referenced object,
    // only if the value refers to a different object
    ASTObjectCondition secondObjectCondition = null;
    if (targetObject.isPresent() && !isSameObject) {
      String targetName = createObjectString(attrValue);
      setDependency(objectCondition, targetName);
      if (isObjectWithoutStereoType(object)) {
        secondObjectCondition = createCondition(targetName, format("!%s_cand.%s.equals(%s)",
            objectName, attributeAccess, createGetterStatementForCand(attrValue)));
        setDependency(secondObjectCondition, objectName);
      }
    }
    addConditions(objectCondition);
    if (secondObjectCondition != null) {
      addConditions(secondObjectCondition);
    }
  }

  /**
   * @return the call of the getter for the attribute, which is compared with the value
   */
  protected String getAttributeAccess(ASTODAttribute node) {
    if (node.isIterated() && !node.isOptional()) {
      return format("get%sList()", Util.getListAccessorName(node));
    }
    return format("get%s()", capitalize(node.getName()));
  }

  protected void createConditionsForListWithoutTarget(ASTODAttribute node) {
    String listGetter = format("cand.get%sList()", Util.getListAccessorName(node));
    List<ASTVariableInit> values = node.getList().getVariableInitList();
    StringBuilder conditionString = new StringBuilder(format("(%s == null || %s.size() != %s",
        listGetter, listGetter, values.size()));
    TFExpressionFullPrettyPrinter printer = new TFExpressionFullPrettyPrinter(new IndentPrinter());
    for (int i = 0; i < values.size(); i++) {
      String value = printer.prettyprint(values.get(i));
      String check = "";
      conditionString.append("|| ");
      if (value.startsWith("\"$")) {
        // variable
        value = value.substring(1, value.length() - 1);
        conditionString.append(value).append("_is_fix && ");
      } else if (Util.isAttributeReference(value)) {
        // attribute of another object
        List<String> qName = Splitters.DOT.splitToList(value);
        String cand = qName.get(0) + CAND_SUFFIX;
        value = cand + format(GET_ATTRIBUTE, capitalize(qName.get(1))) + (qName.size() == 3 ? "." + qName.get(2) : "");
        check = cand + " != null &&";
      }
      conditionString.append(format("%s! %s.get(%s).equals(%s) ", check, listGetter, i, value));
    }
    conditionString.append(")");
    addConditions(createCondition(objectName, conditionString.toString()));
  }

  protected void createConditionsForBoolean(ASTODAttribute node, Optional<ASTODObject> targetObject) {
    String name = capitalize(node.getName());
    if ("true".equals(attrValue)) {
      addConditions(createCondition(objectName, format("!cand.is%s()", name)));
    } else if ("false".equals(attrValue)) {
      addConditions(createCondition(objectName, format("cand.is%s()", name)));
    } else {
      ASTObjectCondition objectCondition = createCondition(objectName,
          format("cand.is%s() != %s", name, createIsStatement(attrValue, false)));
      addConditions(objectCondition);
      if (targetObject.isPresent() && attrValue.contains(".")) {
        String targetName = createObjectString(attrValue);
        setDependency(objectCondition, targetName);
        ASTObjectCondition secondObjectCondition = createCondition(targetName,
            format("%s_cand.is%s() != %s", objectName, name, createIsStatement(attrValue, true)));
        setDependency(secondObjectCondition, objectName);
        addConditions(secondObjectCondition);
      }
    }
  }

  protected void createConditionsForInt(ASTODAttribute node) {
    addConditions(createCondition(objectName, format("cand.get%s() != %s", capitalize(node.getName()), attrValue)));
  }

  @Override
  public void visit(ASTODLink node) {
    if (node.isLink() && !node.isPresentName()) {
      // conditions can only be created for named links
      return;
    }
    String leftObjectName = Names.constructQualifiedName(node.getLeftReferenceName(0).getPartsList());
    String rightObjectName = Names.constructQualifiedName(node.getRightReferenceName(0).getPartsList());
    ASTODObject leftObject = getODObject(leftObjectName).get();
    ASTODObject rightObject = getODObject(rightObjectName).get();
    boolean checkedForSource = isCheckedForSource(leftObject, rightObject);
    boolean checkedForTarget = isCheckedForTarget(leftObject);

    // the condition for the candidates of the source object
    ASTObjectCondition objectCondition = createCondition(leftObjectName);
    objectCondition.setTargetOptional(false);
    objectCondition.setLink(node);
    // the condition for the candidates of the target object
    ASTObjectCondition secondObjectCondition = createCondition(rightObjectName);
    secondObjectCondition.setTargetOptional(false);
    secondObjectCondition.setLink(node);

    if (!node.isLink()) {
      // for compositions, the source is the parent of the target
      objectCondition.setConditionString(format("t.getParent(%s_cand) != cand", rightObjectName));
      setDependency(objectCondition, rightObjectName);
      secondObjectCondition.setConditionString(format("t.getParent(cand) != %s_cand", leftObjectName));
      setDependency(secondObjectCondition, leftObjectName);

      if (node.isPresentRightRole()) {
        // the target is also reachable using the role, list and negative objects are checked later
        if (checkedForSource) {
          ASTObjectCondition roleCondition = createCondition(leftObjectName,
              createSourceObjectCondition(node, rightObjectName, node.isAttributeIterated(), node.isAttributeOptional()));
          setDependency(roleCondition, rightObjectName);
          objectConditions.add(roleCondition);
        }
        if (checkedForTarget) {
          ASTObjectCondition roleCondition = createCondition(rightObjectName,
              createTargetObjectCondition(node, leftObjectName, node.isAttributeIterated(), node.isAttributeOptional()));
          setDependency(roleCondition, leftObjectName);
          if (node.isAttributeIterated()) {
            // for large lists, getParent(child) == parent is checked instead of
            // parent.getChildren().contains(child)
            secondObjectCondition.setConditionString(format("(%s_cand.get%sList().size() > 10 &&%s)",
                leftObjectName, getListAccessorName(node),
                secondObjectCondition.getConditionString()));
          }
          objectConditions.add(roleCondition);
        }
      }
    } else {
      String association = uncapitalize(node.getName());
      objectCondition.setConditionString(format("%s.getRightFromLeft(cand) != null && !%s.getRightFromLeft(cand).contains(%s_cand)",
          association, association, rightObjectName));
      setDependency(objectCondition, rightObjectName);
      secondObjectCondition.setConditionString(format("%s.getRightFromLeft(%s_cand) != null && !%s.getRightFromLeft(%s_cand).contains(cand)",
          association, leftObjectName, association, leftObjectName));
      setDependency(secondObjectCondition, leftObjectName);
    }

    if (checkedForTarget) {
      objectConditions.add(secondObjectCondition);
    }
    if (checkedForSource) {
      objectConditions.add(objectCondition);
    }
  }

  /**
   * Whether a link is checked for the candidates of its source object. Links to list or
   * optional objects as well as links from positive to negative objects are checked later.
   */
  protected boolean isCheckedForSource(ASTODObject source, ASTODObject target) {
    return !target.hasStereotype(ODRuleStereotypes.LIST)
        && !target.hasStereotype(ODRuleStereotypes.OPTIONAL)
        && (source.hasStereotype(ODRuleStereotypes.NOT) || !target.hasStereotype(ODRuleStereotypes.NOT));
  }

  /**
   * Whether a link is checked for the candidates of its target object.
   * Links from list or optional objects are checked later.
   */
  protected boolean isCheckedForTarget(ASTODObject source) {
    return !source.hasStereotype(ODRuleStereotypes.LIST)
        && !source.hasStereotype(ODRuleStereotypes.OPTIONAL);
  }

  protected String createSourceObjectCondition(ASTODLink node, String rightObjectName, boolean isIterated, boolean isOptional) {
    if (isIterated) {
      return format("!cand.get%sList().contains(%s_cand)", getListAccessorName(node), rightObjectName);
    }
    String role = capitalize(node.getRightRole());
    if (isOptional) {
      return format("((!cand.isPresent%s())||(cand.isPresent%s() && cand.get%s() != %s_cand))",
          role, role, role, rightObjectName);
    }
    return format("cand.get%s() != %s_cand", role, rightObjectName);
  }

  protected String createTargetObjectCondition(ASTODLink node, String leftObjectName, boolean isIterated, boolean isOptional) {
    if (isIterated) {
      String list = format("%s_cand.get%sList()", leftObjectName, getListAccessorName(node));
      return format("(%s.size() <= 10 && !%s.contains(cand))", list, list);
    }
    String role = capitalize(node.getRightRole());
    if (isOptional) {
      return format("((!%s_cand.isPresent%s())||(%s_cand.isPresent%s() && %s_cand.get%s() != cand))",
          leftObjectName, role, leftObjectName, role, leftObjectName, role);
    }
    return format("%s_cand.get%s() != cand", leftObjectName, role);
  }

  /**
   * Whether the value of the attribute is an attribute of another object, which may be absent
   */
  protected boolean isTargetOptional(ASTODAttribute attrWithValue) {
    return Util.isAttributeReference(Util.printExpression(attrWithValue.getSingleValue()))
        && attrWithValue.isValueOptional();
  }

  /**
   * @return the expression for the value of the attribute
   */
  protected String createGetterStatement(ASTODAttribute attrWithValue) {
    String value = Util.printExpression(attrWithValue.getSingleValue());
    if (Util.isAttributeReference(value)) {
      // attribute of another object
      String[] split = value.split(REGEX);
      return appendAttributeAccess(split[0] + CAND_SUFFIX, split);
    } else if (getODObject(value).isPresent()) {
      // another object
      return value + CAND_SUFFIX;
    } else if (value.startsWith("\"$")) {
      // variable
      return value.substring(1, value.length() - 1);
    }
    // fixed value
    return value;
  }

  protected String createPresentCheckStatement(String getter) {
    // replaces the getter of the last attribute, e.g., getX() with isPresentX()
    List<String> splits = new ArrayList<>(Splitters.DOT.splitToList(getter));
    String last = splits.getLast();
    if (last.startsWith("get")) {
      splits.set(splits.size() - 1, "isPresent" + last.substring("get".length()));
    }
    return Joiners.DOT.join(splits);
  }

  /**
   * @return the expression for the value of the attribute, where the referenced object
   * is the candidate
   */
  protected String createGetterStatementForCand(String statement) {
    String[] split = statement.split(REGEX);
    return split.length == 1 ? CAND : appendAttributeAccess(CAND, split);
  }

  /**
   * @param split the parts of a qualified attribute reference (object.attribute[.member]*)
   * @return the access of the attribute (and its members) of the given object
   */
  protected String appendAttributeAccess(String object, String[] split) {
    StringBuilder result = new StringBuilder(object).append(format(GET_ATTRIBUTE, capitalize(split[1])));
    for (int i = 2; i < split.length; i++) {
      if (!split[i].equals("get()")) {
        result.append(".").append(split[i]);
      }
    }
    return result.toString();
  }

  protected String createIsStatement(String attrValue, boolean forCand) {
    String[] split = attrValue.split(REGEX);
    StringBuilder result = new StringBuilder(forCand ? CAND : split[0] + CAND_SUFFIX);
    for (int i = 1; i < split.length; i++) {
      result.append(i == split.length - 1 ? ".is" : ".get").append(capitalize(split[i])).append("()");
    }
    return result.toString();
  }

  protected String createObjectString(String statement) {
    String objectName = statement.split(REGEX)[0];
    return objectName.startsWith("{") ? objectName.substring(1) : objectName;
  }

  /**
   * @return the name of the list accessors of the attribute set by the given link
   */
  protected String getListAccessorName(ASTODLink link) {
    String rightObjectName = Names.constructQualifiedName(link.getRightReferenceName(0).getPartsList());
    return Util.getListAccessorName(link, getODObject(rightObjectName).orElse(null));
  }

  protected Optional<ASTODObject> getODObject(String objName) {
    return Optional.ofNullable(objectsByName.get(objName));
  }

  protected boolean isObjectWithoutStereoType(ASTODObject object) {
    return !object.hasStereotype(ODRuleStereotypes.LIST)
        && !object.hasStereotype(ODRuleStereotypes.NOT)
        && !object.hasStereotype(ODRuleStereotypes.OPTIONAL);
  }

}
