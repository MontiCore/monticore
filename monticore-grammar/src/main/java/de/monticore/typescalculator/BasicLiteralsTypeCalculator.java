package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.mcbasicliterals._ast.*;
import de.monticore.mcbasicliterals._visitor.MCBasicLiteralsVisitor;
import de.monticore.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.mcbasictypes._ast.*;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BasicLiteralsTypeCalculator extends LiteralsBasisTypesCalculator implements MCBasicLiteralsVisitor {

  private ASTMCType result;

  private MCBasicLiteralsVisitor realThis = this;

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
  public MCBasicLiteralsVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(MCBasicLiteralsVisitor realThis){
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
