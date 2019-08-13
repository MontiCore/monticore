/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.literals.mccommonliterals._ast.*;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsVisitor;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommonLiteralsTypesCalculator extends LiteralsBasisTypesCalculator implements MCCommonLiteralsVisitor {

  private ASTMCType result;

  private MCCommonLiteralsVisitor realThis = this;

  private Map<ASTNode, MCTypeSymbol> types;


  @Override
  public ASTMCType calculateType(ASTLiteral lit) {
    lit.accept(this);
    return result;
  }

  public void setTypes(Map<ASTNode, MCTypeSymbol> types) {
    this.types = types;
  }

  public Map<ASTNode, MCTypeSymbol> getTypes() {
    return types;
  }

  @Override
  public MCCommonLiteralsVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(MCCommonLiteralsVisitor realThis){
    this.realThis=realThis;
  }

  @Override
  public void visit(ASTNode node) {

  }

  @Override
  public void endVisit(ASTNode node) {

  }

  @Override
  public void visit(ASTCharLiteral lit){
    result= MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.CHAR).build();
    MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
    sym.setASTMCType(result);
    types.put(lit,sym);
  }

  @Override
  public void visit(ASTBooleanLiteral lit){
    result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
    MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
    sym.setASTMCType(result);
    types.put(lit,sym);
  }

  @Override
  public void visit(ASTNatLiteral lit){
    result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build();
    MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
    sym.setASTMCType(result);
    types.put(lit,sym);
  }

  @Override
  public void visit(ASTBasicDoubleLiteral lit){
    result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build();
    MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
    sym.setASTMCType(result);
    types.put(lit,sym);
  }

  @Override
  public void visit(ASTBasicFloatLiteral lit){
    result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build();
    MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
    sym.setASTMCType(result);
    types.put(lit,sym);
  }

  @Override
  public void visit(ASTBasicLongLiteral lit){
    result = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.LONG).build();
    MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
    sym.setASTMCType(result);
    types.put(lit,sym);
  }

  @Override
  public void visit(ASTStringLiteral lit){
    List<String> name = new ArrayList<>();
    name.add("String");
    ASTMCQualifiedName qualifiedName = MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build();
    result = MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(qualifiedName).build();
    MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
    sym.setASTMCType(result);
    types.put(lit,sym);
  }


}
