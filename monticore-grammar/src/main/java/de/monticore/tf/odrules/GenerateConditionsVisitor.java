/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcarraystatements._ast.ASTArrayInit;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableInit;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.se_rwth.commons.StringTransformations.capitalize;
import static de.se_rwth.commons.StringTransformations.uncapitalize;
import static java.lang.String.format;

public class GenerateConditionsVisitor implements
        ODRulesVisitor2 {

  private static final String REGEX = "\\.";
  private static final String CAND = "cand";
  private static final String CAND_SUFFIX = "_cand";
  private static final String GET = ".get()";
  private static final String GET_ATTRIBUTE = ".get%s()";

  private List<ASTObjectCondition> objectConditions = new ArrayList<>();

  private String objectName = "";
  private String attrValue = "";
  private boolean targetOptional = false;

  private ASTODObject object;
  private List<ASTODObject> objectList;


  public List<ASTObjectCondition> getObjectConditions() {
    return objectConditions;
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
  public void visit(ASTODDefinition node) {
    objectList = Util.getAllODObjects(node);
  }


  /**
   * Visits the nodes of the given AST of the type ASTODAttribute and generates
   * Strings for the conditions
   *
   * @param node the node to start visiting
   */
  @Override
  public void visit(ASTODAttribute node) {
    if (node.isPresentSingleValue()) {
      attrValue = Util.printExpression(node.getSingleValue());
    } else if (node.isPresentList()) {
      attrValue = new TFExpressionFullPrettyPrinter(new IndentPrinter()).prettyprint(node.getList());
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
    } else if (!targetObject.isPresent() || !targetObject.get().getName().equals(objectName)) {
      createConditions(node, object, targetObject);
    }
  }

  @Override
  public void endVisit(ASTODAttribute node) {
    targetOptional = false;
  }

  private void addConditions(ASTObjectCondition... objectCondition) {
    for (ASTObjectCondition o : objectCondition) {
      if (o.getObjectName() != null && !o.getObjectName().isEmpty()) {
        objectConditions.add(o);
      }
    }
  }

  private void createConditions(ASTODAttribute node, ASTODObject object, Optional<ASTODObject> targetObject) {
    boolean isOptional = node.isOptional();
    boolean isIterated = node.isIterated();

    ASTObjectCondition objectCondition = ODRuleGenerationMill.objectConditionBuilder().uncheckedBuild();
    objectCondition.setObjectName(objectName);
    ASTObjectCondition secondObjectCondition = ODRuleGenerationMill.objectConditionBuilder().uncheckedBuild();
    String getter = createGetterStatement(node);
    String condition;
    if (isOptional) {
      condition = format("(!cand.isPresent%s() || ! cand.get%s().equals(%s", capitalize(node.getName()), capitalize(node.getName()), getter);
    } else if(isIterated) {
      condition = format("(!cand.get%sList().equals(%s", Util.makeSingular(node.getName()), getter);
    }else{
      condition = format("(!cand.get%s().equals(%s", capitalize(node.getName()), getter);
    }
    objectCondition.setTargetOptional(targetOptional);
    objectCondition.setAttribute(node);
    if (targetObject.isPresent())
      objectCondition.setTarget(targetObject.get());
    else
      objectCondition.setTargetAbsent();

    condition += "))";
    if (targetOptional) {
      String presentCheck = createPresentCheckStatement(getter);
      objectCondition.setConditionString(format("%s && %s", presentCheck, condition));
    } else if (getter.endsWith(GET)) {
      objectCondition.setConditionString(getter.replace(GET, ".isPresent()") + " && " + condition);
    } else if (getter.startsWith("$")) {
      if (!getter.contains(".")) {
        objectCondition.setConditionString(format("%s_is_fix && %s", getter, condition));
      } else {
        objectCondition.setConditionString(format("%s != null && %s", getter, condition));
      }
    } else {
      objectCondition.setConditionString(condition);
    }
    // only if value refers to a different object
    if (targetObject.isPresent()) {
      if (!createObjectString(attrValue).isEmpty()) {
        ASTDependency dependency = ODRuleGenerationMill.dependencyBuilder().uncheckedBuild();
        dependency.setContent(createObjectString(attrValue));
        objectCondition.setDependency(dependency);
      }
      if (isObjectWithoutStereoType(object)) {
        String second = format("!%s_cand", objectName);
        if (isOptional) {
          second += format(".get%s()", capitalize(node.getName()));
        } else if(isIterated) {
          second += format(".get%sList()", Util.makeSingular(node.getName()));
        } else {
          second += format(".get%s()", capitalize(node.getName()));
        }
        second = format("%s.equals(%s)", second, createGetterStatementForCand(attrValue));
        secondObjectCondition.setConditionString(second);
        if (!objectName.isEmpty()) {
          ASTDependency dependency = ODRuleGenerationMill.dependencyBuilder().uncheckedBuild();
          dependency.setContent(objectName);
          secondObjectCondition.setDependency(dependency);
        }
        secondObjectCondition.setObjectName(createObjectString(attrValue));
      }
    }
    addConditions(objectCondition, secondObjectCondition);
  }

  private void createConditionsForListWithoutTarget(ASTODAttribute node) {
    ASTObjectCondition objectCondition = ODRuleGenerationMill.objectConditionBuilder().uncheckedBuild();
    objectCondition.setObjectName(objectName);
    StringBuilder conditionString = new StringBuilder(format("(cand.get%sList() == null ",  StringTransformations.capitalize(node.getName())));
    ASTArrayInit value = node.getList();
    int numberOfStrings = value.getVariableInitList().size();
    conditionString.append(format("|| cand.get%sList().size() != %s", StringTransformations.capitalize(node.getName()), numberOfStrings));
    for (int i = 0; i < numberOfStrings; i++) {
      ASTVariableInit nextValue = value.getVariableInit(i);
      TFExpressionFullPrettyPrinter printer = new TFExpressionFullPrettyPrinter(new IndentPrinter());
      String v = printer.prettyprint(nextValue);
      conditionString.append("|| ");
      String check = "";
      // value is a variable
      if (v.startsWith("\"$")) {
        v = v.substring(1, v.length() - 1);
        conditionString.append(v).append("_is_fix && ");
        // value is a reference to another object
      } else if (v.contains(".")) {
        List<String> qName = Splitters.DOT.splitToList(v);
        String cand = qName.get(0) + CAND_SUFFIX;
        StringBuilder getter = new StringBuilder();
        getter.append(format(GET_ATTRIBUTE, capitalize(qName.get(1))));

        if (qName.size() == 3) {
          getter.append(".").append(qName.get(2));
        }
        v = cand + getter;
        check = cand + " != null &&";

      }
      conditionString.append(format("%s! cand.get%sList().get(%s).equals(%s) ", check, StringTransformations.capitalize(node.getName()), i, v));
    }
    conditionString.append(")");
    objectCondition.setConditionString(conditionString.toString());

    addConditions(objectCondition);
  }


  private void createConditionsForBoolean(ASTODAttribute node, Optional<ASTODObject> targetObject) {
    ASTObjectCondition objectCondition = ODRuleGenerationMill.objectConditionBuilder().uncheckedBuild();
    objectCondition.setObjectName(objectName);
    ASTObjectCondition secondObjectCondition = ODRuleGenerationMill.objectConditionBuilder().uncheckedBuild();
    if ("true".equals(attrValue)) {
      objectCondition.setConditionString(format("!cand.is%s()", capitalize(node.getName())));
    } else if ("false".equals(attrValue)) {
      objectCondition.setConditionString(format("cand.is%s()", capitalize(node.getName())));
    } else {
      objectCondition.setConditionString(format("cand.is%s() != %s", capitalize(node.getName()), createIsStatement(attrValue, false)));
      if (targetObject.isPresent() && attrValue.contains(".")) {
        if (!createObjectString(attrValue).isEmpty()) {
          ASTDependency dependency = ODRuleGenerationMill.dependencyBuilder().uncheckedBuild();
          dependency.setContent(createObjectString(attrValue));
          objectCondition.setDependency(dependency);
        }
        secondObjectCondition.setConditionString(format("%s_cand.is%s() != %s", objectName, capitalize(node.getName()), createIsStatement(attrValue, true)));
        if (!objectName.isEmpty()) {
          ASTDependency dependency = ODRuleGenerationMill.dependencyBuilder().uncheckedBuild();
          dependency.setContent(objectName);
          secondObjectCondition.setDependency(dependency);
        }
        secondObjectCondition.setObjectName(createObjectString(attrValue));
      }
    }
    addConditions(objectCondition, secondObjectCondition);
  }

  private void createConditionsForInt(ASTODAttribute node) {
    ASTObjectCondition objectCondition = ODRuleGenerationMill.objectConditionBuilder().uncheckedBuild();
    objectCondition.setObjectName(objectName);
    objectCondition.setConditionString(format("cand.get%s() != %s", capitalize(node.getName()), attrValue));
    addConditions(objectCondition);

  }

  /**
   * Visits the nodes of the given AST of the type ASTODLink and generates
   * Strings for the conditions
   *
   * @param node the node to start visiting
   */
  @Override
  public void visit(ASTODLink node) {
    String leftObjectName = Names.constructQualifiedName(node.getLeftReferenceName(0).getPartsList());
    String rightObjectName = Names.constructQualifiedName(node.getRightReferenceName(0).getPartsList());
    Optional<ASTODObject> rightObject = getODObject(rightObjectName);
    Optional<ASTODObject> leftObject = getODObject(leftObjectName);
    boolean isIterated = node.isAttributeIterated();
    boolean isOptional = node.isAttributeOptional();

    ASTObjectCondition objectCondition = ODRuleGenerationMill.objectConditionBuilder().uncheckedBuild();
    objectCondition.setObjectName(leftObjectName);
    ASTObjectCondition secondObjectCondition = ODRuleGenerationMill.objectConditionBuilder().uncheckedBuild();
    secondObjectCondition.setObjectName(rightObjectName);

    objectCondition.setTargetOptional(targetOptional);
    objectCondition.setLink(node);

    secondObjectCondition.setTargetOptional(targetOptional);
    secondObjectCondition.setLink(node);

    if (!node.isLink()) {
      // if it is a composition the left side is the parent of the right
      objectCondition.setConditionString(format("t.getParent(%s_cand) != cand", rightObjectName));
      secondObjectCondition.setConditionString(format("t.getParent(cand) != %s_cand", leftObjectName));
      if (!rightObjectName.isEmpty()) {
        ASTDependency dependency = ODRuleGenerationMill.dependencyBuilder().uncheckedBuild();
        dependency.setContent(rightObjectName);
        objectCondition.setDependency(dependency);
      }
      if (!leftObjectName.isEmpty()) {
        ASTDependency dependency = ODRuleGenerationMill.dependencyBuilder().uncheckedBuild();
        dependency.setContent(leftObjectName);
        secondObjectCondition.setDependency(dependency);
      }
      if (node.isPresentRightRole()) {
        // if a role name is given, then there is a second condition
        // if an object is a list object or a negative node, it will be
        // checked later 


        if (!rightObject.get().hasStereotype(ODRuleStereotypes.LIST)
            && !(!leftObject.get().hasStereotype(ODRuleStereotypes.NOT) && rightObject.get().hasStereotype(ODRuleStereotypes.NOT))
            && !rightObject.get().hasStereotype(ODRuleStereotypes.OPTIONAL)) {
          ASTObjectCondition roleCondition1 = ODRuleGenerationMill.objectConditionBuilder().uncheckedBuild();
          roleCondition1.setObjectName(leftObjectName);
          roleCondition1.setConditionString(createSourceObjectCondition(node, rightObjectName, isIterated, isOptional));
          if (!rightObjectName.isEmpty()) {
            ASTDependency dependency = ODRuleGenerationMill.dependencyBuilder().uncheckedBuild();
            dependency.setContent(rightObjectName);
            roleCondition1.setDependency(dependency);
          }
          objectConditions.add(roleCondition1);
        }
        if (!leftObject.get().hasStereotype(ODRuleStereotypes.LIST)
            && !leftObject.get().hasStereotype(ODRuleStereotypes.OPTIONAL)) {
          ASTObjectCondition roleCondition2 = ODRuleGenerationMill.objectConditionBuilder().uncheckedBuild();
          roleCondition2.setObjectName(rightObjectName);
          roleCondition2.setConditionString(createTargetObjectCondition(node, leftObjectName, isIterated, isOptional));
          if (!leftObjectName.isEmpty()) {
            ASTDependency dependency = ODRuleGenerationMill.dependencyBuilder().uncheckedBuild();
            dependency.setContent(leftObjectName);
            roleCondition2.setDependency(dependency);
          }
          objectConditions.add(roleCondition2);
        }
      }
    } else {
      // if its not a composition it should be a link
      if (!node.getName().isEmpty()) {
        // name must be given to create conditionString
        objectCondition.setConditionString(format("%s.getRightFromLeft(cand) != null && !%s.getRightFromLeft(cand).contains(%s_cand)",
            uncapitalize(node.getName()),
            uncapitalize(node.getName()),
            rightObjectName));
        if (!rightObjectName.isEmpty()) {
          ASTDependency dependency = ODRuleGenerationMill.dependencyBuilder().uncheckedBuild();
          dependency.setContent(rightObjectName);
          objectCondition.setDependency(dependency);
        }
        secondObjectCondition.setConditionString(format("%s.getRightFromLeft(%s_cand) != null && !%s.getRightFromLeft(%s_cand).contains(cand)",
            uncapitalize(node.getName()),
            leftObjectName,
            uncapitalize(node.getName()),
            leftObjectName));
        if (!leftObjectName.isEmpty()) {
          ASTDependency dependency = ODRuleGenerationMill.dependencyBuilder().uncheckedBuild();
          dependency.setContent(leftObjectName);
          secondObjectCondition.setDependency(dependency);
        }
      }
    }
    if (!leftObject.get().hasStereotype(ODRuleStereotypes.LIST)
        && !leftObject.get().hasStereotype(ODRuleStereotypes.OPTIONAL)) {
      objectConditions.add(secondObjectCondition);
    }
    if (!rightObject.get().hasStereotype(ODRuleStereotypes.LIST)
        && !(!leftObject.get().hasStereotype(ODRuleStereotypes.NOT) && rightObject.get().hasStereotype(ODRuleStereotypes.NOT))
        && !rightObject.get().hasStereotype(ODRuleStereotypes.OPTIONAL)) {
      objectConditions.add(objectCondition);
    }
  }

  private String createSourceObjectCondition(ASTODLink node, String rightObjectName, boolean isIterated, boolean isOptional) {
    if (isIterated) {
      return format("!cand.get%sList().contains(%s_cand)",
          Util.makeSingular(node.getRightRole()),
          rightObjectName);
    } else if (isOptional) {
      return format("((!cand.isPresent%s())||(cand.isPresent%s() && cand.get%s() != %s_cand))",
          capitalize(node.getRightRole()),
          capitalize(node.getRightRole()),
          capitalize(node.getRightRole()),
          rightObjectName);
    } else {
      return format("cand.get%s() != %s_cand",
          capitalize(node.getRightRole()),
          rightObjectName);
    }
  }

  private String createTargetObjectCondition(ASTODLink node, String leftObjectName, boolean isIterated, boolean isOptional) {
    if (isIterated) {
      return format("!%s_cand.get%sList().contains(cand)",
          uncapitalize(leftObjectName),
          Util.makeSingular(node.getRightRole()));
    } else if (isOptional) {
      return format("((!%s_cand.isPresent%s())||(%s_cand.isPresent%s() && %s_cand.get%s() != cand))",
          leftObjectName, capitalize(node.getRightRole()),
          leftObjectName, capitalize(node.getRightRole()),
          leftObjectName, capitalize(node.getRightRole()));
    } else {
      return format("%s_cand.get%s() != cand",
          leftObjectName,
          capitalize(node.getRightRole()));
    }
  }

  private String createGetterStatement(ASTODAttribute attrWithValue) {
    String value = Util.printExpression(attrWithValue.getSingleValue());
    String[] split = value.split(REGEX);

    if (split.length == 1) {
      if (getODObject(split[0]).isPresent()) {
        // object reference
        return split[0] + CAND_SUFFIX;
      } else if (split[0].startsWith("\"$")) {
        // variable
        return split[0].substring(1, split[0].length() - 1);
      } else {
        // fix value
        return split[0];
      }
    } else {
      // attribute reference
      StringBuilder result = new StringBuilder(split[0]).append(CAND_SUFFIX);
      if (split.length > 1) {
        result.append(format(GET_ATTRIBUTE, capitalize(split[1])));
        targetOptional = attrWithValue.isValueOptional();
      }
      for (int i = 2; i < split.length; i++) {
        if(!split[i].equals("get()")) {
          result.append(".").append(split[i]);
          targetOptional = attrWithValue.isValueOptional();
        }
      }
      return result.toString();
    }

  }
  private String createPresentCheckStatement(String getter) {
    List<String> splits = new ArrayList<>(Splitters.DOT.splitToList(getter));
    splits.set(splits.size() - 1, splits.get(splits.size() - 1).replace("get", "isPresent"));

    return Joiners.DOT.join(splits);
  }

  private String createGetterStatementForCand(String statement) {
    String[] split = statement.split(REGEX);
    if (split.length == 1) {
      return CAND;
    } else {
      StringBuilder result = new StringBuilder(CAND);
      if (split.length > 1) {
        result.append(format(GET_ATTRIBUTE, capitalize(split[1])));
      }
      for (int i = 2; i < split.length; i++) {
          if(!split[i].equals("get()")) {
              result.append(".").append(split[i]);
          }
      }
      return result.toString();
    }
  }

  private String createIsStatement(String attrValue, boolean forCand) {
    String[] split = attrValue.split(REGEX);
    StringBuilder result;
    if (forCand) {
      result = new StringBuilder(CAND);
    } else {
      result = new StringBuilder(split[0]).append(CAND_SUFFIX);
    }
    String getter = ".get";
    for (int i = 1; i < split.length; i++) {
      if (i == split.length - 1) {
        getter = ".is";
      }
      result.append(getter).append(capitalize(split[i])).append("()");
    }
    return result.toString();
  }

  private String createObjectString(String statement) {
    String[] split = statement.split(REGEX);
    if (split[0].startsWith("{")) {
      return split[0].substring(1);
    }
    return split[0];
  }

  private Optional<ASTODObject> getODObject(String objName) {
    for (ASTODObject o : objectList) {
      if (objName.equals(o.getName())) {
        return Optional.of(o);
      }
    }
    return Optional.empty();
  }

  private boolean isObjectWithoutStereoType(ASTODObject object) {
    return !object.hasStereotype(ODRuleStereotypes.LIST)
        && !object.hasStereotype(ODRuleStereotypes.NOT)
        && !object.hasStereotype(ODRuleStereotypes.OPTIONAL);
  }

}
