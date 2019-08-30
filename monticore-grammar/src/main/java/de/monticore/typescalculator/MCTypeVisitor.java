/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardTypeArgument;
import de.monticore.types.mcfullgenerictypes._visitor.MCFullGenericTypesVisitor;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCCustomTypeArgument;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MCTypeVisitor implements MCFullGenericTypesVisitor {

  TypeExpression typeExpression = null;

  public Map<ASTMCBasicTypesNode, TypeExpression> mapping = new HashMap<>();
  public Map<ASTMCTypeArgument, TypeExpression> typeArgumentMapping = new HashMap<>();



  public void endVisit(ASTMCListType listType) {
    GenericTypeExpression listGenericType = new GenericTypeExpression();
    listGenericType.setName("List");
    List<TypeExpression> argumentList = new LinkedList<TypeExpression>();
    argumentList.add(mapping.get(listType.getMCTypeArgument().getMCTypeOpt().get()));
    listGenericType.setArguments(argumentList);
    mapping.put(listType,listGenericType);
  }

  public void endVisit(ASTMCSetType setType) {
    GenericTypeExpression listGenericType = new GenericTypeExpression();
    listGenericType.setName("Set");
    List<TypeExpression> argumentList = new LinkedList<TypeExpression>();
    argumentList.add(mapping.get(setType.getMCTypeArgument().getMCTypeOpt().get()));
    listGenericType.setArguments(argumentList);
    mapping.put(setType,listGenericType);
  }

  public void endVisit(ASTMCOptionalType optType) {
    GenericTypeExpression listGenericType = new GenericTypeExpression();
    listGenericType.setName("Optional");
    List<TypeExpression> argumentList = new LinkedList<TypeExpression>();
    argumentList.add(mapping.get(optType.getMCTypeArgument().getMCTypeOpt().get()));
    listGenericType.setArguments(argumentList);
    mapping.put(optType,listGenericType);
  }

  public void endVisit(ASTMCMapType mapType) {
    GenericTypeExpression listGenericType = new GenericTypeExpression();
    listGenericType.setName("Map");
    List<TypeExpression> argumentList = new LinkedList<TypeExpression>();
    argumentList.add(mapping.get(mapType.getKey().getMCTypeOpt().get()));
    argumentList.add(mapping.get(mapType.getValue().getMCTypeOpt().get()));
    listGenericType.setArguments(argumentList);
    mapping.put(mapType,listGenericType);
  }


  public void endVisit(ASTMCBasicGenericType genType) {
    GenericTypeExpression genericTypeExpression = new GenericTypeExpression();
    genericTypeExpression.setName(genType.getName());
    List<TypeExpression> argumentList = new LinkedList<TypeExpression>();
    for(ASTMCTypeArgument typeArg : genType.getMCTypeArgumentList()) {
      argumentList.add(typeArgumentMapping.get(typeArg));
    }
    genericTypeExpression.setArguments(argumentList);
    mapping.put(genType,genericTypeExpression);
  }

  public void endVisit(ASTMCQualifiedType qType) {
    ObjectType oType = new ObjectType();
    oType.setName(qType.getName());
    mapping.put(qType,oType);
  }

  public void endVisit(ASTMCQualifiedName qName) {
    ObjectType oType = new ObjectType();
    oType.setName(qName.toString());
    mapping.put(qName,oType);
  }

  public void endVisit(ASTMCPrimitiveType primitiveType) {
    TypeConstant typeConstant = new TypeConstant();
    typeConstant.setName(primitiveType.getName());
    mapping.put(primitiveType,typeConstant);
  }

  public void endVisit(ASTMCVoidType voidType) {
    TypeConstant typeConstant = new TypeConstant();
    typeConstant.setName("void");
    mapping.put(voidType,typeConstant);
  }

  public void endVisit(ASTMCBasicTypeArgument basicTypeArgument) {
    ObjectType o = new ObjectType();
    o.setName(basicTypeArgument.getMCQualifiedType().getName());
    //TODO RE rekursiv fehlt!
    typeArgumentMapping.put(basicTypeArgument,o);
  }

  public void endVisit(ASTMCTypeArgument arg) {
    ObjectType o = new ObjectType();
    o.setName(arg.getMCTypeOpt().get().getName());
    typeArgumentMapping.put(arg,o);
  }

  public void endVisit(ASTMCPrimitiveTypeArgument basicTypeArgument) {
    ObjectType o = new ObjectType();
    //o.setName(basicTypeArgument.getMCQualifiedType().getName());
    //TODO RE rekursiv fehlt!
    typeArgumentMapping.put(basicTypeArgument,o);
  }
  public void endVisit(ASTMCCustomTypeArgument basicTypeArgument) {
    ObjectType o = new ObjectType();
    o.setName(basicTypeArgument.getMCType().getName());
    //TODO RE rekursiv fehlt!
    typeArgumentMapping.put(basicTypeArgument,o);
  }

  public void endVisit(ASTMCWildcardTypeArgument basicTypeArgument) {
    ObjectType o = new ObjectType();
    o.setName(basicTypeArgument.getMCTypeOpt().get().getName());
    //TODO RE rekursiv fehlt!
    typeArgumentMapping.put(basicTypeArgument,o);
  }

}
