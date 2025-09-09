/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcarraystatements._ast.ASTArrayInit;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableInit;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.StringTransformations;
import de.monticore.tf.odrulegeneration.ODRuleGenerationMill;
import de.monticore.tf.odrulegeneration._ast.*;
import de.monticore.tf.odrules._ast.*;
import de.monticore.tf.odrules.util.ODRuleStereotypes;
import de.monticore.tf.odrules.util.TFExpressionFullPrettyPrinter;
import de.monticore.tf.odrules.util.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class ChangeOperationFactory {

  private ASTODRule rule;
  private ASTODDefinition lhs;
  private ASTODDefinition rhs;
  private HierarchyHelper hierarchyHelper;

  /**
   * @param from
   * @param to
   * @return returns a ChangeOperation, that contains all the steps, to convert
   *         one ASTODObject to another.
   */

  public ASTChangeOperation createChangeOperation(ASTODObject from, ASTODObject to) {
    // this method does all the steps necessary to convert one Object to
    // another
    ASTChangeOperationBuilder builder = ODRuleGenerationMill.changeOperationBuilder();
    List<ASTChange> setAttributeOperations = new ArrayList<ASTChange>();

    for (ASTODAttribute right : to.getAttributesList()) {
      // search for matching types on the left
      ASTODAttribute found = null;
      for (ASTODAttribute left : from.getAttributesList()) {
        if (left.getName().equals(right.getName())) {
          found = left;
          // if the data type mismatches, an exception is thrown, this
          // may not happen
          String leftType = left.getMCType().toString();
          String rightType = right.getMCType().toString();
          if (leftType.indexOf('@') >= 0) {
            leftType = leftType.split("@")[0];
          }
          if (rightType.indexOf('@') >= 0) {
            rightType = rightType.split("@")[0];
          }

          if (!leftType.equals(rightType)){
            throw new IllegalArgumentException(leftType + " does not match " + rightType);
          }
          break;
        }
      }
      if (found != null) {
        // Compare the found attribute with the attribute on the right
        // side
        String l = getValueStringFromAttribute(from, found);
        String r = getValueStringFromAttribute(to, right);

        if (!l.equals(r)){
          setAttributeOperations.add(createSetAttribute(to, right));
        }
      } else {
        // attribute is only on the right side, so set this attribute
        setAttributeOperations.add(createSetAttribute(to, right));
      }
    }

    builder.setSetAttributeOperationsList(setAttributeOperations);
    return builder.build();
  }

  public ASTChangeOperation createDeleteOperation(ASTODObject obj) {
    String variableName = obj.getName();
    ASTMCType type = obj.getType();

    ASTChangeOperationBuilder builder = ODRuleGenerationMill.changeOperationBuilder();

    List<ASTDeleteOperation> deleteOperations = new ArrayList<ASTDeleteOperation>();
    deleteOperations.add(createDelete(type, variableName, obj.hasStereotype(ODRuleStereotypes.LIST)));

    builder.setDeleteOperationsList(deleteOperations);
    return builder.build();
  }

  public ASTChangeOperation createCreateOperation(ASTODObject obj) {
    ASTMCType type = obj.getType();
    String objectName = obj.getName();

    ASTChangeOperationBuilder builder = ODRuleGenerationMill.changeOperationBuilder();

    List<ASTCreateOperation> createOperations = new ArrayList<>();
    List<ASTChange> setOperations = new ArrayList<>();
    createOperations.add(createCreate(type, objectName, !obj.getStereotypeValue("noBuilder").isEmpty()));

    List<ASTODAttribute> attributes = obj.getAttributesList();
    for (ASTODAttribute att : attributes) {
      setOperations.add(createSetAttribute( obj, att));
    }

    builder.setSetAttributeOperationsList(setOperations);
    builder.setCreateOperationsList(createOperations);
    return builder.build();
  }

  public ASTChangeOperation createCreateOperation(ASTODLink link) {
    ASTChangeOperationBuilder builder = ODRuleGenerationMill.changeOperationBuilder();

    List<ASTChange> setOperations = new ArrayList<ASTChange>();
    setOperations.add(createLinkCreation(link));

    builder.setSetAttributeOperationsList(setOperations);
    return builder.build();
  }

  public ASTChangeOperation createCreateOperation(ASTODObject obj, List<ASTODLink> links) {
    ASTChangeOperation changeOperation = createCreateOperation(obj);

    for(ASTODLink link : links) {
      changeOperation.addSetAttributeOperations(createLinkCreation(link));
    }

    return changeOperation;
  }

  public ASTChangeOperation createDeleteOperation(ASTODLink link) {
    ASTChangeOperationBuilder builder = ODRuleGenerationMill.changeOperationBuilder();

    List<ASTChange> setOperations = new ArrayList<ASTChange>();
    setOperations.add(createLinkDeletion(link));

    builder.setSetAttributeOperationsList(setOperations);
    return builder.build();
  }

  public ChangeOperationFactory(ASTODRule rule, ASTODDefinition lhs, ASTODDefinition rhs, HierarchyHelper hierarchyHelper) {
    this.rule = rule;
    this.lhs = lhs;
    this.rhs = rhs;
    this.hierarchyHelper = hierarchyHelper;
  }

  protected String getValueStringFromAttribute(ASTODObject object, ASTODAttribute attribute) {
    if (attribute.isPresentList() ) {
      ASTArrayInit listValue = attribute.getList();
      StringBuilder valuesList = new StringBuilder();
      Iterator<ASTVariableInit> i = listValue.getVariableInitList().iterator();
      while (i.hasNext()) {
        ASTVariableInit next = i.next();
        TFExpressionFullPrettyPrinter printer = new TFExpressionFullPrettyPrinter(new IndentPrinter());
        String n = printer.prettyprint(next);
        if(n.contains(".")) {
          List<String> parts = Lists.newArrayList(Splitter.on(".").split(n));
          if(hierarchyHelper.isWithinOptionalStructure(object.getName())) {
            valuesList.append("m." + parts.get(0) + ".isPresent()?");
            valuesList.append("m." + parts.get(0) + ".get()");
            valuesList.append(".get").append(StringTransformations.capitalize(parts.get(1))).append("()");
            if(parts.size()==3){
              valuesList.append(".").append(parts.get(2));
            }
            valuesList.append(":\"undef\"");
          } else {
            valuesList.append("m." + parts.get(0));
            valuesList.append(".get").append(StringTransformations.capitalize(parts.get(1))).append("()");
            if(parts.size()==3){
              valuesList.append(".").append(parts.get(2));
            }
          }
        }else if(n.startsWith("\"$") && n.endsWith("\"")){
          valuesList.append(n.substring(1,n.length()-1));
        } else {
          valuesList.append(n);
        }
        if (i.hasNext()) {
          valuesList.append(", ");
        }
      }

      return "Lists.newArrayList(" + valuesList.toString() + ")";
    } else {
      TFExpressionFullPrettyPrinter printer = new TFExpressionFullPrettyPrinter(new IndentPrinter());
      return printer.prettyprint(attribute.getSingleValue());
    }
  }


  private ASTChange createSetAttribute(ASTODObject object, ASTODAttribute attribute) {
    ASTChangeBuilder builder = ODRuleGenerationMill.changeBuilder();



    // composite

    builder.setObjectName(object.getName());
    builder.setAttributeName(attribute.getName());

    builder.setObjectType(object.printType());
    String type = attribute.printType();
    builder.setType(type);
    builder.setGenericType(type);
    builder.setSimpleType(Names.getSimpleName(type));
    if (type.equals("int")) {
      builder.setBoxingType("Integer");
    }
    else if(type.equals("boolean")) {
      builder.setBoxingType("Boolean");
    }

    String attributeName = attribute.getName();

    boolean isAttributeOptional = attribute.isOptional();
    builder.setAttributeOptional(isAttributeOptional);
    boolean isAttributeIterated = attribute.isIterated();
    // copy
    builder.setObjectInList(object.hasStereotype(ODRuleStereotypes.LIST));
    // valueListObject
    builder.setValueType(type);

    if (isAttributeIterated) {
      builder.setSetter("get" + StringTransformations.capitalize(attributeName) + "List().addAll");
      builder.setUnsetter("get" + StringTransformations.capitalize(attributeName) + "List().remove");
    } else {
      builder.setSetter("set" + StringTransformations.capitalize(attributeName));
      builder.setUnsetter("set" + StringTransformations.capitalize(attributeName));
    }

    if (type.equals("boolean")) {
      builder.setGetter("is" + StringTransformations.capitalize(attributeName));
    } else if(isAttributeIterated) {
      builder.setGetter("get" +  StringTransformations.capitalize(attributeName) + "List");
      if(isAttributeOptional) {
        builder.setGetIsPresent("isPresent" + Util.makeSingular(attributeName) + "List()");
      }
    } else {
      builder.setGetter("get" + StringTransformations.capitalize(attributeName));
      if(isAttributeOptional) {
        builder.setGetIsPresent("isPresent" + StringTransformations.capitalize(attributeName) + "()");
      }
    }

    builder.setValueStringList(attribute.isPresentList());
    builder.setPrimitiveType(Util.isBuiltInType(attribute));

    builder.setValue(getValueStringFromAttribute(object, attribute));
    // oldValue

    boolean objectWithinOpt = hierarchyHelper.isWithinOptionalStructure(object.getName());
    boolean objectWithinList = hierarchyHelper.isWithinListStructure(object.getName());
    builder.setObjectWithinOpt(objectWithinOpt);
    builder.setObjectWithinList(objectWithinList);
    String objectGetter = "";
    if(objectWithinOpt && objectWithinList) {
      objectGetter = "get_" + object.getName() + "().get()";
    } else if(objectWithinOpt) {
      objectGetter = "m." + object.getName() + ".get()";
    } else if(objectWithinList) {
      objectGetter = "get_" + object.getName() + "()";
    } else {
      objectGetter = "m." + object.getName();
    }
    builder.setObjectGetter(objectGetter);

    if(!builder.isPrimitiveType() && !builder.isValueStringList() && builder.isPresentValue()) {
      boolean valueWithinOpt = hierarchyHelper.isWithinOptionalStructure(builder.getValue());
      boolean valueWithinList = hierarchyHelper.isWithinListStructure(builder.getValue());
      builder.setValueWithinOpt(valueWithinOpt);
      builder.setValueWithinList(valueWithinList);
      String valueGetter;
      if (valueWithinOpt && valueWithinList) {
        valueGetter = "get_" + builder.getValue() + "().get()";
      } else if (valueWithinOpt) {
        valueGetter = "m." + builder.getValue() + ".get()";
      } else if (valueWithinList) {
        valueGetter = "get_" + builder.getValue() + "()";
      } else {
        valueGetter = "m." + builder.getValue();
      }
      builder.setValueGetter(valueGetter);
    }
    if(!builder.isPrimitiveType() && !builder.isValueStringList() && builder.isPresentOldValue()) {
      boolean oldValueWithinOpt = hierarchyHelper.isWithinOptionalStructure(builder.getOldValue());
      boolean oldValueWithinList = hierarchyHelper.isWithinListStructure(builder.getOldValue());
      builder.setOldValueWithinOpt(oldValueWithinOpt);
      builder.setOldValueWithinList(oldValueWithinList);
      String oldValueGetter;
      if (oldValueWithinOpt && oldValueWithinList) {
        oldValueGetter = "get_" + builder.getOldValue() + "().get()";
      } else if (oldValueWithinOpt) {
        oldValueGetter = "m." + builder.getOldValue() + ".get()";
      } else if (oldValueWithinList) {
        oldValueGetter = "get_" + builder.getOldValue() + "()";
      } else {
        oldValueGetter = "m." + builder.getOldValue();
      }
      builder.setOldValueGetter(oldValueGetter);
    }

    return builder.build();
  }

  private ASTChange createLinkCreation(ASTODLink link) {
    String targetName = Names.constructQualifiedName(link.getRightReferenceName(0).getPartsList());
    ASTODObject targetObject = Util.getODObject(rhs, targetName);


    ASTChangeBuilder builder = ODRuleGenerationMill.changeBuilder();

    builder.setComposite(!link.isLink());
    String objectName = Names.constructQualifiedName(link.getLeftReferenceName(0).getPartsList());
    builder.setObjectName(objectName);
    String attributeName = link.getRightRole();
    builder.setAttributeName(attributeName);

    // objectType
    String type = Util.getRightRoleType(link, rhs);
    String objectType = Util.getLeftRoleType(link, lhs);
    builder.setType(type);
    builder.setObjectType(objectType);
    builder.setSimpleType(Names.getSimpleName(type));
    if (type.equals("int")) {
      builder.setBoxingType("Integer");
    }
    else if(type.equals("boolean")) {
      builder.setBoxingType("Boolean");
    }
    if(!link.getStereotypeValue("genericType").isEmpty()) {
      builder.setGenericType(link.getStereotypeValue("genericType"));
    } else {
      builder.setGenericType(type);
    }

    boolean isAttributeIterated = link.isAttributeIterated();
    builder.setAttributeIterated(isAttributeIterated);
    boolean isAttributeOptional = link.isAttributeOptional();
    builder.setAttributeOptional(isAttributeOptional);
    builder.setValueListObject(targetObject.hasStereotype(ODRuleStereotypes.LIST));
    builder.setValueType(type);
    builder.setCopy(link.hasStereotype("copy"));

    if (isAttributeIterated) {
      builder.setSetter("get" + Util.makeSingular(attributeName) + "List().add");
      builder.setUnsetter("get" + Util.makeSingular(attributeName) + "List().remove");
    } else {
      builder.setSetter("set" + StringTransformations.capitalize(attributeName));
      builder.setUnsetter("set" + StringTransformations.capitalize(attributeName));
    }

    if (type.equals("boolean")) {
      builder.setGetter("is" + StringTransformations.capitalize(attributeName));
    } else if(isAttributeIterated) {
      builder.setGetter("get" + Util.makeSingular(attributeName) + "List");
      if(isAttributeOptional) {
        builder.setGetIsPresent("isPresent" + Util.makeSingular(attributeName) + "List()");
      }
    } else {
      builder.setGetter("get" + StringTransformations.capitalize(attributeName));
      if(isAttributeOptional) {
        builder.setGetIsPresent("isPresent" + StringTransformations.capitalize(attributeName) + "()");
      }
    }


    // valueStringList
    // primitiveType

    builder.setValue(targetName);
    // oldValue


    boolean objectWithinOpt = hierarchyHelper.isWithinOptionalStructure(objectName);
    boolean objectWithinList = hierarchyHelper.isWithinListStructure(objectName);
    builder.setObjectWithinOpt(objectWithinOpt);
    builder.setObjectWithinList(objectWithinList);
    String objectGetter = "";
    if(objectWithinOpt && objectWithinList) {
      objectGetter = "get_" + objectName + "().get()";
    } else if(objectWithinOpt) {
      objectGetter = "m." + objectName + ".get()";
    } else if(objectWithinList) {
      objectGetter = "get_" + objectName + "()";
    } else {
      objectGetter = "m." + objectName;
    }
    builder.setObjectGetter(objectGetter);

    if(!builder.isPrimitiveType() && !builder.isValueStringList() && builder.isPresentValue()) {
      boolean valueWithinOpt = hierarchyHelper.isWithinOptionalStructure(builder.getValue());
      boolean valueWithinList = hierarchyHelper.isWithinListStructure(builder.getValue());
      builder.setValueWithinOpt(valueWithinOpt);
      builder.setValueWithinList(valueWithinList);
      String valueGetter;
      if (valueWithinOpt && valueWithinList) {
        valueGetter = "get_" + builder.getValue() + "().get()";
      } else if (valueWithinOpt) {
        valueGetter = "m." + builder.getValue() + ".get()";
      } else if (valueWithinList) {
        valueGetter = "get_" + builder.getValue() + "()";
      } else {
        valueGetter = "m." + builder.getValue();
      }
      builder.setValueGetter(valueGetter);
    }
    if(!builder.isPrimitiveType() && !builder.isValueStringList() && builder.isPresentOldValue()) {
      boolean oldValueWithinOpt = hierarchyHelper.isWithinOptionalStructure(builder.getOldValue());
      boolean oldValueWithinList = hierarchyHelper.isWithinListStructure(builder.getOldValue());
      builder.setOldValueWithinOpt(oldValueWithinOpt);
      builder.setOldValueWithinList(oldValueWithinList);
      String oldValueGetter;
      if (oldValueWithinOpt && oldValueWithinList) {
        oldValueGetter = "get_" + builder.getOldValue() + "().get()";
      } else if (oldValueWithinOpt) {
        oldValueGetter = "m." + builder.getOldValue() + ".get()";
      } else if (oldValueWithinList) {
        oldValueGetter = "get_" + builder.getOldValue() + "()";
      } else {
        oldValueGetter = "m." + builder.getOldValue();
      }
      builder.setOldValueGetter(oldValueGetter);
    }

    if(link.hasStereotype("insertType")) {
      builder.setInsertPosition(createInsertPosition(link, builder));
    }

    return builder.build();
  }

  private String createInsertPosition(ASTODLink link, ASTChangeBuilder builder) {
    String insertType = link.getStereotypeValue("insertType");
    String insertAt = link.getStereotypeValue("insertAt");
    String insertPos = "";

    if(insertType.equals("first")) {
      insertPos = "0";
    } else if(insertType.equals("last")) {
      insertPos = "m."+ builder.getObjectName() + "." + builder.getGetter() + "().size()";
    } else if(insertType.equals("inplace")) {
      insertPos = "m."+ builder.getObjectName() + "_" + insertAt + "_before_pos";
    } else if(insertType.equals("relative")) {
      insertPos = "m."+ builder.getObjectName() + "." + builder.getGetter() + "().indexOf(" + "m." + insertAt + ") + 1";
    }

    return insertPos;
  }

    private ASTChange createLinkDeletion(ASTODLink link) {
    String attributeName = "";
    if(link.isPresentRightRole()) {
      attributeName = link.getRightRole();
    }
    String targetName = Names.constructQualifiedName(link.getRightReferenceName(0).getPartsList());
    String objectName = Names.constructQualifiedName(link.getLeftReferenceName(0).getPartsList());
    ASTODObject targetObject = Util.getODObject(lhs, targetName);
    String objectType = Util.getLeftRoleType(link, lhs);

    ASTChangeBuilder builder = ODRuleGenerationMill.changeBuilder();

    builder.setComposite(!link.isLink());

    builder.setObjectName(objectName);
    builder.setAttributeName(attributeName);

    builder.setObjectType(objectType);
    String type = Util.getRightRoleType(link, lhs);
    builder.setType(type);
    builder.setSimpleType(Names.getSimpleName(type));
    if (type.equals("int")) {
      builder.setBoxingType("Integer");
    }
    else if(type.equals("boolean")) {
      builder.setBoxingType("Boolean");
    }
    if(!link.getStereotypeValue("genericType").isEmpty()) {
      builder.setGenericType(link.getStereotypeValue("genericType"));
    } else {
      builder.setGenericType(type);
    }


    boolean isAttributeIterated = link.isAttributeIterated();
    builder.setAttributeIterated(isAttributeIterated);
    boolean isAttributeOptional = link.isAttributeOptional();
    builder.setAttributeOptional(isAttributeOptional);
    builder.setValueListObject(targetObject.hasStereotype(ODRuleStereotypes.LIST));
    builder.setValueType(type);

    if (isAttributeIterated) {
      builder.setSetter("get" + Util.makeSingular(attributeName) + "List().add");
      builder.setUnsetter("get" + Util.makeSingular(attributeName) + "List().remove");
    } else {
      builder.setSetter("set" + StringTransformations.capitalize(attributeName));
      builder.setUnsetter("set" + StringTransformations.capitalize(attributeName));
    }

    if (type.equals("boolean")) {
      builder.setGetter("is" + StringTransformations.capitalize(attributeName));
    } else if(isAttributeIterated) {
      builder.setGetter("get" + Util.makeSingular(attributeName) + "List");
      if(isAttributeOptional) {
        builder.setGetIsPresent("isPresent" + Util.makeSingular(attributeName) + "List()");
      }
    } else {
      builder.setGetter("get" + StringTransformations.capitalize(attributeName));
      if(isAttributeOptional) {
        builder.setGetIsPresent("isPresent" + StringTransformations.capitalize(attributeName) + "()");
      }
    }

    // valueStringList
    // primitiveType

    // value
    builder.setOldValue(targetName);


    boolean objectWithinOpt = hierarchyHelper.isWithinOptionalStructure(objectName);
    boolean objectWithinList = hierarchyHelper.isWithinListStructure(objectName);
    builder.setObjectWithinOpt(objectWithinOpt);
    builder.setObjectWithinList(objectWithinList);
    String objectGetter = "";
    if(objectWithinOpt && objectWithinList) {
      objectGetter = "get_" + objectName + "().get()";
    } else if(objectWithinOpt) {
      objectGetter = "m." + objectName + ".get()";
    } else if(objectWithinList) {
      objectGetter = "get_" + objectName + "()";
    } else {
      objectGetter = "m." + objectName;
    }
    builder.setObjectGetter(objectGetter);

    if(!builder.isPrimitiveType() && !builder.isValueStringList() && builder.isPresentValue()) {
      boolean valueWithinOpt = hierarchyHelper.isWithinOptionalStructure(builder.getValue());
      boolean valueWithinList = hierarchyHelper.isWithinListStructure(builder.getValue());
      builder.setValueWithinOpt(valueWithinOpt);
      builder.setValueWithinList(valueWithinList);
      String valueGetter;
      if (valueWithinOpt && valueWithinList) {
        valueGetter = "get_" + builder.getValue() + "().get()";
      } else if (valueWithinOpt) {
        valueGetter = "m." + builder.getValue() + ".get()";
      } else if (valueWithinList) {
        valueGetter = "get_" + builder.getValue() + "()";
      } else {
        valueGetter = "m." + builder.getValue();
      }
      builder.setValueGetter(valueGetter);
    }
    if(!builder.isPrimitiveType() && !builder.isValueStringList() && builder.isPresentOldValue()) {
      boolean oldValueWithinOpt = hierarchyHelper.isWithinOptionalStructure(builder.getOldValue());
      boolean oldValueWithinList = hierarchyHelper.isWithinListStructure(builder.getOldValue());
      builder.setOldValueWithinOpt(oldValueWithinOpt);
      builder.setOldValueWithinList(oldValueWithinList);
      String oldValueGetter;
      if (oldValueWithinOpt && oldValueWithinList && objectWithinList) {
        // Special case for nested Opt inside List
        oldValueGetter = "get_" + builder.getOldValue() + "()";
      } else if (oldValueWithinOpt && oldValueWithinList) {
        oldValueGetter = "get_" + builder.getOldValue() + "().get()";
      } else if (oldValueWithinOpt) {
        oldValueGetter = "m." + builder.getOldValue() + ".get()";
      } else if (oldValueWithinList) {
        oldValueGetter = "get_" + builder.getOldValue() + "()";
      } else {
        oldValueGetter = "m." + builder.getOldValue();
      }
      builder.setOldValueGetter(oldValueGetter);
    }

    return builder.build();
  }

  private ASTDeleteOperation createDelete(ASTMCType type, String variableName, boolean isListObject) {
    String typeName = Util.printType(type);
    List<String> complexname = Splitters.DOT.splitToList(typeName);
    String simpleType = complexname.get(complexname.size()-1);

    ASTDeleteOperationBuilder builder = ODRuleGenerationMill.deleteOperationBuilder();
    builder.setName(variableName);
    builder.setType(typeName);
    builder.setList(isListObject);
    builder.setSimpleType(simpleType);
    builder.setGrammarType(simpleType.substring(3));

    builder.setTypepackage(rule.getGrammarPackageName() +
        "." + rule.getGrammarName().toLowerCase() + "._ast");

    return builder.build();
  }

  private ASTCreateOperation createCreate(ASTMCType type, String variable, boolean isInterface) {
    String typeName = Util.printType(type);
    List<String> complexname = Splitters.DOT.splitToList(typeName);
    String simpleType = complexname.get(complexname.size()-1);

    ASTCreateOperationBuilder builder = ODRuleGenerationMill.createOperationBuilder();
    builder.setName(variable);
    builder.setType(typeName);
    builder.setSimpleType(simpleType);

    builder.setFactoryName(rule.getGrammarPackageName()
            + "." + rule.getGrammarName().toLowerCase()
            + "." + rule.getGrammarName() + "Mill");


    if (isInterface)
      builder.setFactoryName("__missing");

    return builder.build();
  }

}
