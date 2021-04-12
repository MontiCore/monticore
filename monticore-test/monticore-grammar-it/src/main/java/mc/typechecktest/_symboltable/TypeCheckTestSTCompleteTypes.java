/* (c) https://github.com/MontiCore/monticore */
package mc.typechecktest._symboltable;

import com.google.common.collect.Lists;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;
import mc.typechecktest._ast.ASTTCArtifact;
import mc.typechecktest._ast.ASTTCMethod;
import mc.typechecktest._ast.ASTTCVarDecl;
import mc.typechecktest._visitor.TypeCheckTestVisitor2;

import java.util.Optional;

public class TypeCheckTestSTCompleteTypes implements TypeCheckTestVisitor2 {

  protected TypeCheck tc;

  public TypeCheckTestSTCompleteTypes(TypeCheck tc){
    this.tc = tc;
  }

  @Override
  public void visit(ASTTCMethod node) {
    TCMethodSymbol symbol = node.getSymbol();
    SymTypeExpression symType = tc.symTypeFromAST(node.getMCReturnType());
    TypeSymbol type;
    if(symType.isVoidType()){
      type = symType.getTypeInfo();
    }else{
      type = replaceSurrogate(symType.getTypeInfo());
    }
    symbol.setReturnType(SymTypeExpressionFactory.createTypeExpression(type));
    symbol.setIsStatic(node.isStatic());
  }

  @Override
  public void visit(ASTTCVarDecl node) {
    TCVarDeclSymbol symbol = node.getSymbol();
    SymTypeExpression symType = tc.symTypeFromAST(node.getMCType());
    TypeSymbol type = replaceSurrogate(symType.getTypeInfo());
    symbol.setType(SymTypeExpressionFactory.createTypeExpression(type));
    symbol.setIsStatic(node.isStatic());
  }

  @Override
  public void visit(ASTTCArtifact node) {
    TCArtifactSymbol symbol = node.getSymbol();
    if(node.isPresentSuperType()) {
      SymTypeExpression superType = tc.symTypeFromAST(node.getSuperType());
      TypeSymbol type = replaceSurrogate(superType.getTypeInfo());
      symbol.setSuperTypesList(Lists.newArrayList(SymTypeExpressionFactory.createTypeExpression(type)));
    }
  }

  public TypeSymbol replaceSurrogate(TypeSymbol type){
    Optional<TypeSymbol> ts = type.getEnclosingScope().resolveType(type.getName());
    if(ts.isPresent()){
      return type;
    } else {
      Log.error("Could not find the type " + type.getName());
      return null;
    }
  }
}
