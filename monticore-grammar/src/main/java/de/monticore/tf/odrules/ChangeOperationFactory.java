/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.tf.odrulegeneration.ODRuleGenerationMill;
import de.monticore.tf.odrulegeneration._ast.*;
import de.monticore.tf.odrules._ast.*;
import de.monticore.tf.odrules.util.ODRuleStereotypes;
import de.monticore.tf.odrules.util.TFExpressionFullPrettyPrinter;
import de.monticore.tf.odrules.util.Util;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.StringTransformations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Creates the change operations of a rule, see {@link DifferenceFinder}.
 */
public class ChangeOperationFactory {

  protected final ASTODRule rule;
  protected final ASTODDefinition lhs;
  protected final ASTODDefinition rhs;
  protected final HierarchyHelper hierarchyHelper;

  public ChangeOperationFactory(ASTODRule rule, ASTODDefinition lhs, ASTODDefinition rhs, HierarchyHelper hierarchyHelper) {
    this.rule = rule;
    this.lhs = lhs;
    this.rhs = rhs;
    this.hierarchyHelper = hierarchyHelper;
  }

  /**
   * @return a ChangeOperation that sets all attributes of {@code to}, which are not present in
   * {@code from} or have another value there.
   * @throws IllegalArgumentException if an attribute has different types in both objects
   */
  public ASTChangeOperation createChangeOperation(ASTODObject from, ASTODObject to) {
    Map<String, ASTODAttribute> fromAttributes = new HashMap<>();
    from.getAttributesList().forEach(a -> fromAttributes.putIfAbsent(a.getName(), a));

    ASTChangeOperationBuilder builder = ODRuleGenerationMill.changeOperationBuilder();
    for (ASTODAttribute right : to.getAttributesList()) {
      ASTODAttribute left = fromAttributes.get(right.getName());
      if (left != null) {
        // the AST classes of the types must match
        String leftType = left.getMCType().printType();
        String rightType = right.getMCType().printType();
        if (!leftType.equals(rightType)) {
          throw new IllegalArgumentException(leftType + " does not match " + rightType);
        }
      }
      if (left == null || !getValueStringFromAttribute(from, left).equals(getValueStringFromAttribute(to, right))) {
        builder.addSetAttributeOperations(createSetAttribute(to, right));
      }
    }
    return builder.build();
  }

  public ASTChangeOperation createDeleteOperation(ASTODObject obj) {
    return ODRuleGenerationMill.changeOperationBuilder()
        .addDeleteOperations(createDelete(obj.getType(), obj.getName(), obj.hasStereotype(ODRuleStereotypes.LIST)))
        .build();
  }

  public ASTChangeOperation createCreateOperation(ASTODObject obj) {
    ASTChangeOperationBuilder builder = ODRuleGenerationMill.changeOperationBuilder()
        .addCreateOperations(createCreate(obj.getType(), obj.getName(), !obj.getStereotypeValue("noBuilder").isEmpty()));
    for (ASTODAttribute attribute : obj.getAttributesList()) {
      builder.addSetAttributeOperations(createSetAttribute(obj, attribute));
    }
    return builder.build();
  }

  public ASTChangeOperation createCreateOperation(ASTODLink link) {
    return ODRuleGenerationMill.changeOperationBuilder()
        .addSetAttributeOperations(createLinkCreation(link))
        .build();
  }

  public ASTChangeOperation createCreateOperation(ASTODObject obj, List<ASTODLink> links) {
    ASTChangeOperation changeOperation = createCreateOperation(obj);
    for (ASTODLink link : links) {
      changeOperation.addSetAttributeOperations(createLinkCreation(link));
    }
    return changeOperation;
  }

  public ASTChangeOperation createDeleteOperation(ASTODLink link) {
    return ODRuleGenerationMill.changeOperationBuilder()
        .addSetAttributeOperations(createLinkDeletion(link))
        .build();
  }

  protected String getValueStringFromAttribute(ASTODObject object, ASTODAttribute attribute) {
    TFExpressionFullPrettyPrinter printer = new TFExpressionFullPrettyPrinter(new IndentPrinter());
    if (!attribute.isPresentList()) {
      return printer.prettyprint(attribute.getSingleValue());
    }
    boolean withinOpt = hierarchyHelper.isWithinOptionalStructure(object.getName());
    return attribute.getList().getVariableInitList().stream()
        .map(printer::prettyprint)
        .map(value -> getListElementValue(value, withinOpt))
        .collect(Collectors.joining(", ", "Lists.newArrayList(", ")"));
  }

  /**
   * @param value     a printed element of a list value
   * @param withinOpt whether the object of the attribute is within an optional structure
   */
  protected String getListElementValue(String value, boolean withinOpt) {
    if (value.contains(".")) {
      // reference to an attribute of another object: object.attribute[.member]
      List<String> parts = Splitters.DOT.splitToList(value);
      String access = ".get" + StringTransformations.capitalize(parts.get(1)) + "()"
          + (parts.size() == 3 ? "." + parts.get(2) : "");
      if (withinOpt) {
        return "m." + parts.get(0) + ".isPresent()?m." + parts.get(0) + ".get()" + access + ":\"undef\"";
      }
      return "m." + parts.get(0) + access;
    }
    if (value.startsWith("\"$") && value.endsWith("\"")) {
      // variable
      return value.substring(1, value.length() - 1);
    }
    return value;
  }

  protected ASTChange createSetAttribute(ASTODObject object, ASTODAttribute attribute) {
    ASTChangeBuilder builder = ODRuleGenerationMill.changeBuilder();
    String type = attribute.printType();
    builder.setObjectName(object.getName());
    builder.setAttributeName(attribute.getName());
    builder.setObjectType(object.printType());
    setTypes(builder, type, type);
    builder.setAttributeOptional(attribute.isOptional());
    builder.setObjectInList(object.hasStereotype(ODRuleStereotypes.LIST));
    setAccessors(builder, attribute.getName(), type, attribute.isOptional(),
        attribute.isIterated() ? Optional.of(Util.getListAccessorName(attribute)) : Optional.empty(), "addAll");
    builder.setValueStringList(attribute.isPresentList());
    builder.setPrimitiveType(Util.isBuiltInType(attribute));
    builder.setValue(getValueStringFromAttribute(object, attribute));
    setObjectGetter(builder, object.getName());
    if (!builder.isPrimitiveType() && !builder.isValueStringList()) {
      setValueGetter(builder);
    }
    return builder.build();
  }

  protected ASTChange createLinkCreation(ASTODLink link) {
    ASTChangeBuilder builder = createLinkChange(link, rhs, link.getRightRole());
    builder.setCopy(link.hasStereotype(ODRuleStereotypes.COPY));
    builder.setValue(getTargetName(link));
    setValueGetter(builder);
    if (link.hasStereotype("insertType")) {
      builder.setInsertPosition(createInsertPosition(link, builder));
    }
    return builder.build();
  }

  protected ASTChange createLinkDeletion(ASTODLink link) {
    ASTChangeBuilder builder = createLinkChange(link, lhs, link.isPresentRightRole() ? link.getRightRole() : "");
    String oldValue = getTargetName(link);
    builder.setOldValue(oldValue);
    boolean withinOpt = hierarchyHelper.isWithinOptionalStructure(oldValue);
    boolean withinList = hierarchyHelper.isWithinListStructure(oldValue);
    builder.setOldValueWithinOpt(withinOpt);
    builder.setOldValueWithinList(withinList);
    if (withinOpt && withinList && builder.isObjectWithinList()) {
      // special case for nested optionals inside of lists
      builder.setOldValueGetter("get_" + oldValue + "()");
    } else {
      builder.setOldValueGetter(getObjectAccess(oldValue, withinOpt, withinList));
    }
    return builder.build();
  }

  /**
   * Creates the parts of a change, which are common to the creation and deletion of a link.
   *
   * @param definition    the side of the rule the target of the link belongs to
   * @param attributeName the attribute set by the link
   */
  protected ASTChangeBuilder createLinkChange(ASTODLink link, ASTODDefinition definition, String attributeName) {
    ASTODObject targetObject = Util.getODObject(definition, getTargetName(link));
    String objectName = Names.constructQualifiedName(link.getLeftReferenceName(0).getPartsList());
    String type = Util.getRightRoleType(link, definition);
    String genericType = link.getStereotypeValue("genericType");

    ASTChangeBuilder builder = ODRuleGenerationMill.changeBuilder();
    builder.setComposite(!link.isLink());
    builder.setObjectName(objectName);
    builder.setAttributeName(attributeName);
    builder.setObjectType(Util.getLeftRoleType(link, lhs));
    setTypes(builder, type, genericType.isEmpty() ? type : genericType);
    builder.setAttributeIterated(link.isAttributeIterated());
    builder.setAttributeOptional(link.isAttributeOptional());
    builder.setValueListObject(targetObject.hasStereotype(ODRuleStereotypes.LIST));
    setAccessors(builder, attributeName, type, link.isAttributeOptional(),
        link.isAttributeIterated() ? Optional.of(Util.getListAccessorName(link, targetObject)) : Optional.empty(), "add");
    setObjectGetter(builder, objectName);
    return builder;
  }

  protected String getTargetName(ASTODLink link) {
    return Names.constructQualifiedName(link.getRightReferenceName(0).getPartsList());
  }

  protected void setTypes(ASTChangeBuilder builder, String type, String genericType) {
    builder.setType(type);
    builder.setGenericType(genericType);
    builder.setValueType(type);
    builder.setSimpleType(Names.getSimpleName(type));
    if (type.equals("int")) {
      builder.setBoxingType("Integer");
    } else if (type.equals("boolean")) {
      builder.setBoxingType("Boolean");
    }
  }

  /**
   * Sets the names of the methods used to access the attribute.
   *
   * @param listAccessorName the name used by the list accessors, if the attribute is iterated
   * @param addMethod        the method of the list used for adding values
   */
  protected void setAccessors(ASTChangeBuilder builder, String attributeName, String type,
      boolean isOptional, Optional<String> listAccessorName, String addMethod) {
    String name = StringTransformations.capitalize(attributeName);
    if (listAccessorName.isPresent()) {
      builder.setSetter("get" + listAccessorName.get() + "List()." + addMethod);
      builder.setUnsetter("get" + listAccessorName.get() + "List().remove");
    } else {
      builder.setSetter("set" + name);
      builder.setUnsetter("set" + name);
    }

    if (type.equals("boolean")) {
      builder.setGetter("is" + name);
    } else if (listAccessorName.isPresent()) {
      builder.setGetter("get" + listAccessorName.get() + "List");
      if (isOptional) {
        builder.setGetIsPresent("isPresent" + listAccessorName.get() + "List()");
      }
    } else {
      builder.setGetter("get" + name);
      if (isOptional) {
        builder.setGetIsPresent("isPresent" + name + "()");
      }
    }
  }

  protected void setObjectGetter(ASTChangeBuilder builder, String objectName) {
    boolean withinOpt = hierarchyHelper.isWithinOptionalStructure(objectName);
    boolean withinList = hierarchyHelper.isWithinListStructure(objectName);
    builder.setObjectWithinOpt(withinOpt);
    builder.setObjectWithinList(withinList);
    builder.setObjectGetter(getObjectAccess(objectName, withinOpt, withinList));
  }

  protected void setValueGetter(ASTChangeBuilder builder) {
    String value = builder.getValue();
    boolean withinOpt = hierarchyHelper.isWithinOptionalStructure(value);
    boolean withinList = hierarchyHelper.isWithinListStructure(value);
    builder.setValueWithinOpt(withinOpt);
    builder.setValueWithinList(withinList);
    builder.setValueGetter(getObjectAccess(value, withinOpt, withinList));
  }

  /**
   * @return the expression accessing the matched object with the given name
   */
  protected String getObjectAccess(String objectName, boolean withinOpt, boolean withinList) {
    if (withinList) {
      return "get_" + objectName + "()" + (withinOpt ? ".get()" : "");
    }
    return "m." + objectName + (withinOpt ? ".get()" : "");
  }

  protected String createInsertPosition(ASTODLink link, ASTChangeBuilder builder) {
    String insertType = link.getStereotypeValue("insertType");
    String insertAt = link.getStereotypeValue("insertAt");

    return switch (insertType) {
      case "first" -> "0";
      case "last" -> "m." + builder.getObjectName() + "." + builder.getGetter() + "().size()";
      case "inplace" -> "m." + builder.getObjectName() + "_" + insertAt + "_before_pos";
      case "relative" ->
          "m." + builder.getObjectName() + "." + builder.getGetter() + "().indexOf(" + "m."
              + insertAt + ") + 1";
      default -> "";
    };
  }

  protected ASTDeleteOperation createDelete(ASTMCType type, String variableName, boolean isListObject) {
    String typeName = Util.printType(type);
    String simpleType = Names.getSimpleName(typeName);
    return ODRuleGenerationMill.deleteOperationBuilder()
        .setName(variableName)
        .setType(typeName)
        .setList(isListObject)
        .setSimpleType(simpleType)
        .setGrammarType(simpleType.substring("AST".length()))
        .setTypepackage(getGrammarPackage() + "._ast")
        .build();
  }

  protected ASTCreateOperation createCreate(ASTMCType type, String variable, boolean isInterface) {
    String typeName = Util.printType(type);
    return ODRuleGenerationMill.createOperationBuilder()
        .setName(variable)
        .setType(typeName)
        .setSimpleType(Names.getSimpleName(typeName))
        .setFactoryName(isInterface ? "__missing" : getGrammarPackage() + "." + rule.getGrammarName() + "Mill")
        .build();
  }

  /**
   * @return the package of the language, the rule is defined for
   */
  protected String getGrammarPackage() {
    String grammarPackage = rule.getGrammarPackageName();
    return (grammarPackage.isEmpty() ? "" : grammarPackage + ".") + rule.getGrammarName().toLowerCase();
  }

}
