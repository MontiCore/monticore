package de.monticore.typescalculator;

import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mccollectiontypes._ast.ASTMCSetType;
import de.monticore.types.mcfullgenerictypes._visitor.MCFullGenericTypesVisitor;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MCTypeVisitor implements MCFullGenericTypesVisitor {

  TypeExpression typeExpression = null;

  public Map<ASTMCType, TypeExpression> mapping = new HashMap<>();

  public void endVisit(ASTMCListType listType) {
    typeExpression = new GenericTypeExpression();
    typeExpression.setName("List");
  }

  public void endVisit(ASTMCSetType setType) {

  }

  public void endVisit(ASTMCObjectType objectType) {


  }

  public void endVisit(ASTMCPrimitiveType primitiveType) {
    TypeConstant typeConstant = new TypeConstant();
    typeConstant.setName(primitiveType.getName());
    mapping.put(primitiveType,typeConstant);
  }

  public TypeExpression getTypeExpression() {
    return typeExpression;
  }


}
