/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types;

import de.monticore.types.check.*;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mccollectiontypes._ast.*;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardTypeArgument;
import de.monticore.types.mcfullgenerictypes._visitor.MCFullGenericTypesVisitor;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCCustomTypeArgument;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static de.monticore.types.check.SymTypeExpressionFactory.*;

/**
 * Visitor to convert from MCTypes (Typen Grammatiken, Basic, Collection,
 * BasicGeneric,FullGeneric)
 *
 * TODO 4: Umbauen von Types-Map auf Traverse und single result
 * TODO 4: remove deprecated method , weil da das ebenfalls benötigte Symbol fehlt
 */
public class DeriveSymTypeOfMCType implements MCFullGenericTypesVisitor {

  SymTypeExpression symTypeExpression = null;

  public Map<ASTMCBasicTypesNode, SymTypeExpression> mapping = new HashMap<>();
  public Map<ASTMCTypeArgument, SymTypeExpression> typeArgumentMapping = new HashMap<>();



  public void endVisit(ASTMCListType listType) {
    List<SymTypeExpression> argumentList = new LinkedList<SymTypeExpression>();
    argumentList.add(mapping.get(listType.getMCTypeArgument().getMCTypeOpt().get()));
    SymTypeOfGenerics listGenericType = createGenerics("List",argumentList);
    mapping.put(listType,listGenericType);
  }

  public void endVisit(ASTMCSetType setType) {
    List<SymTypeExpression> argumentList = new LinkedList<SymTypeExpression>();
    argumentList.add(mapping.get(setType.getMCTypeArgument().getMCTypeOpt().get()));
    SymTypeOfGenerics listGenericType = createGenerics("Set",argumentList);
    mapping.put(setType,listGenericType);
  }

  public void endVisit(ASTMCOptionalType optType) {
    List<SymTypeExpression> argumentList = new LinkedList<SymTypeExpression>();
    argumentList.add(mapping.get(optType.getMCTypeArgument().getMCTypeOpt().get()));
    SymTypeOfGenerics listGenericType = createGenerics("Optional",argumentList);
    mapping.put(optType,listGenericType);
  }

  public void endVisit(ASTMCMapType mapType) {
    List<SymTypeExpression> argumentList = new LinkedList<SymTypeExpression>();
    argumentList.add(mapping.get(mapType.getKey().getMCTypeOpt().get()));
    argumentList.add(mapping.get(mapType.getValue().getMCTypeOpt().get()));
    SymTypeOfGenerics listGenericType = createGenerics("Map",argumentList);
    mapping.put(mapType,listGenericType);
  }


  public void endVisit(ASTMCBasicGenericType genType) {
    List<SymTypeExpression> argumentList = new LinkedList<SymTypeExpression>();
    for(ASTMCTypeArgument typeArg : genType.getMCTypeArgumentList()) {
      argumentList.add(typeArgumentMapping.get(typeArg));
    }
    SymTypeOfGenerics genericTypeExpression = createGenerics(genType.printWithoutTypeArguments(), argumentList);
    mapping.put(genType,genericTypeExpression);
  }

  public void endVisit(ASTMCQualifiedType qType) {
    SymTypeOfObject oType = createTypeObject(qType.printType());
    mapping.put(qType,oType);
  }

  public void endVisit(ASTMCQualifiedName qName) {
    SymTypeOfObject oType = createTypeObject(qName.toString());
    mapping.put(qName,oType);
  }

  public void endVisit(ASTMCPrimitiveType primitiveType) {
    SymTypeConstant typeConstant = createTypeConstant(primitiveType.printType());
    mapping.put(primitiveType,typeConstant);
  }

  public void endVisit(ASTMCVoidType voidType) {
    SymTypeVoid typeConstant = createTypeVoid();
    mapping.put(voidType,typeConstant);
  }

  public void endVisit(ASTMCBasicTypeArgument basicTypeArgument) {
    SymTypeOfObject o = createTypeObject(basicTypeArgument.getMCQualifiedType().printType());
    //TODO RE rekursiv fehlt!
    typeArgumentMapping.put(basicTypeArgument,o);
  }

  public void endVisit(ASTMCTypeArgument arg) {
    SymTypeOfObject o = createTypeObject(arg.getMCTypeOpt().get().printType());
    typeArgumentMapping.put(arg,o);
  }

  public void endVisit(ASTMCPrimitiveTypeArgument basicTypeArgument) {
    SymTypeConstant o = createTypeConstant(basicTypeArgument.getMCPrimitiveType().printType());
    //TODO RE rekursiv fehlt!
    typeArgumentMapping.put(basicTypeArgument,o);
  }
  public void endVisit(ASTMCCustomTypeArgument basicTypeArgument) {
    if (basicTypeArgument.getMCType() instanceof ASTMCGenericType) {
      List<SymTypeExpression> argumentList = new LinkedList<>();
      for (ASTMCTypeArgument typeArg : ((ASTMCGenericType) basicTypeArgument.getMCType()).getMCTypeArgumentList()) {
        argumentList.add(typeArgumentMapping.get(typeArg));
      }
      SymTypeOfGenerics o = createGenerics(basicTypeArgument.getMCType().getName(), argumentList);
      //TODO RE rekursiv fehlt!
      typeArgumentMapping.put(basicTypeArgument, o);
    } else {
      SymTypeOfObject o = createTypeObject(basicTypeArgument.getMCType().getName());
      //TODO RE rekursiv fehlt!
      typeArgumentMapping.put(basicTypeArgument, o);
    }
  }

  public void endVisit(ASTMCWildcardTypeArgument basicTypeArgument) {
    SymTypeOfObject o = createTypeObject(basicTypeArgument.getMCTypeOpt().get().printType());
    //TODO RE rekursiv fehlt!
    typeArgumentMapping.put(basicTypeArgument,o);
  }

}
