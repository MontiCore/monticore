/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardTypeArgument;
import de.monticore.types.mcfullgenerictypes._visitor.MCFullGenericTypesVisitor;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCCustomTypeArgument;
import de.monticore.types2.SymGenericTypeExpression;
import de.monticore.types2.SymObjectType;
import de.monticore.types2.SymTypeConstant;
import de.monticore.types2.SymTypeExpression;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MCTypeVisitor implements MCFullGenericTypesVisitor {

  SymTypeExpression symTypeExpression = null;

  public Map<ASTMCBasicTypesNode, SymTypeExpression> mapping = new HashMap<>();
  public Map<ASTMCTypeArgument, SymTypeExpression> typeArgumentMapping = new HashMap<>();



  public void endVisit(ASTMCListType listType) {
    SymGenericTypeExpression listGenericType = new SymGenericTypeExpression();
    listGenericType.setName("List");
    List<SymTypeExpression> argumentList = new LinkedList<SymTypeExpression>();
    argumentList.add(mapping.get(listType.getMCTypeArgument().getMCTypeOpt().get()));
    listGenericType.setArguments(argumentList);
    mapping.put(listType,listGenericType);
  }

  public void endVisit(ASTMCSetType setType) {
    SymGenericTypeExpression listGenericType = new SymGenericTypeExpression();
    listGenericType.setName("Set");
    List<SymTypeExpression> argumentList = new LinkedList<SymTypeExpression>();
    argumentList.add(mapping.get(setType.getMCTypeArgument().getMCTypeOpt().get()));
    listGenericType.setArguments(argumentList);
    mapping.put(setType,listGenericType);
  }

  public void endVisit(ASTMCOptionalType optType) {
    SymGenericTypeExpression listGenericType = new SymGenericTypeExpression();
    listGenericType.setName("Optional");
    List<SymTypeExpression> argumentList = new LinkedList<SymTypeExpression>();
    argumentList.add(mapping.get(optType.getMCTypeArgument().getMCTypeOpt().get()));
    listGenericType.setArguments(argumentList);
    mapping.put(optType,listGenericType);
  }

  public void endVisit(ASTMCMapType mapType) {
    SymGenericTypeExpression listGenericType = new SymGenericTypeExpression();
    listGenericType.setName("Map");
    List<SymTypeExpression> argumentList = new LinkedList<SymTypeExpression>();
    argumentList.add(mapping.get(mapType.getKey().getMCTypeOpt().get()));
    argumentList.add(mapping.get(mapType.getValue().getMCTypeOpt().get()));
    listGenericType.setArguments(argumentList);
    mapping.put(mapType,listGenericType);
  }


  public void endVisit(ASTMCBasicGenericType genType) {
    SymGenericTypeExpression genericTypeExpression = new SymGenericTypeExpression();
    genericTypeExpression.setName(genType.getName());
    List<SymTypeExpression> argumentList = new LinkedList<SymTypeExpression>();
    for(ASTMCTypeArgument typeArg : genType.getMCTypeArgumentList()) {
      argumentList.add(typeArgumentMapping.get(typeArg));
    }
    genericTypeExpression.setArguments(argumentList);
    mapping.put(genType,genericTypeExpression);
  }

  public void endVisit(ASTMCQualifiedType qType) {
    SymObjectType oType = new SymObjectType();
    oType.setName(qType.getName());
    mapping.put(qType,oType);
  }

  public void endVisit(ASTMCQualifiedName qName) {
    SymObjectType oType = new SymObjectType();
    oType.setName(qName.toString());
    mapping.put(qName,oType);
  }

  public void endVisit(ASTMCPrimitiveType primitiveType) {
    SymTypeConstant typeConstant = new SymTypeConstant();
    typeConstant.setName(primitiveType.getName());
    mapping.put(primitiveType,typeConstant);
  }

  public void endVisit(ASTMCVoidType voidType) {
    SymTypeConstant typeConstant = new SymTypeConstant();
    typeConstant.setName("void");
    mapping.put(voidType,typeConstant);
  }

  public void endVisit(ASTMCBasicTypeArgument basicTypeArgument) {
    SymObjectType o = new SymObjectType();
    o.setName(basicTypeArgument.getMCQualifiedType().getName());
    //TODO RE rekursiv fehlt!
    typeArgumentMapping.put(basicTypeArgument,o);
  }

  public void endVisit(ASTMCTypeArgument arg) {
    SymObjectType o = new SymObjectType();
    o.setName(arg.getMCTypeOpt().get().getName());
    typeArgumentMapping.put(arg,o);
  }

  public void endVisit(ASTMCPrimitiveTypeArgument basicTypeArgument) {
    SymObjectType o = new SymObjectType();
    //o.setName(basicTypeArgument.getMCQualifiedType().getName());
    //TODO RE rekursiv fehlt!
    typeArgumentMapping.put(basicTypeArgument,o);
  }
  public void endVisit(ASTMCCustomTypeArgument basicTypeArgument) {
    SymObjectType o = new SymObjectType();
    o.setName(basicTypeArgument.getMCType().getName());
    //TODO RE rekursiv fehlt!
    typeArgumentMapping.put(basicTypeArgument,o);
  }

  public void endVisit(ASTMCWildcardTypeArgument basicTypeArgument) {
    SymObjectType o = new SymObjectType();
    o.setName(basicTypeArgument.getMCTypeOpt().get().getName());
    //TODO RE rekursiv fehlt!
    typeArgumentMapping.put(basicTypeArgument,o);
  }

}
