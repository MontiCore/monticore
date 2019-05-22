package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.mcbasicliterals._ast.*;
import de.monticore.mcbasicliterals._visitor.MCBasicLiteralsVisitor;
import de.monticore.types.mcbasictypes._ast.*;

import java.util.ArrayList;
import java.util.List;

public class BasicLiteralsTypeCalculator implements LiteralTypeCalculator {

  private ASTMCType type;

  private MCBasicLiteralsVisitor realThis = this;


  @Override
  public ASTMCType calculateType(ASTLiteral lit) {
    lit.accept(this);
    return type;
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
    type= MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.CHAR).build();
  }

  @Override
  public void visit(ASTBooleanLiteral lit){
    type = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.BOOLEAN).build();
  }

  @Override
  public void visit(ASTNatLiteral lit){
    type = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.INT).build();
  }

  @Override
  public void visit(ASTBasicDoubleLiteral lit){
    type = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.DOUBLE).build();
  }

  @Override
  public void visit(ASTBasicFloatLiteral lit){
    type = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.FLOAT).build();
  }

  @Override
  public void visit(ASTBasicLongLiteral lit){
    type = MCBasicTypesMill.mCPrimitiveTypeBuilder().setPrimitive(ASTConstantsMCBasicTypes.LONG).build();
  }

  @Override
  public void visit(ASTStringLiteral lit){
    List<String> name = new ArrayList<>();
    name.add("String");
    ASTMCQualifiedName qualifiedName = MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build();
    type = MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(qualifiedName).build();
  }


}
