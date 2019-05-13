package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTQualifiedNameExpression;
import de.monticore.expressions.expressionsbasis._symboltable.ETypeSymbol;
import de.monticore.expressions.expressionsbasis._symboltable.EVariableSymbol;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor;
import de.monticore.expressions.prettyprint2.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.prettyprint2.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;
import de.monticore.types.prettyprint.MCFullGenericTypesPrettyPrinter;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ExpressionsBasisTypesCalculator implements ExpressionsBasisVisitor {

  private final String errorCode = "0xA0144";

  protected ExpressionsBasisScope scope;

  protected LiteralTypeCalculator literalsVisitor;

  protected ASTMCType result;

  protected Map<ASTNode, MCTypeSymbol> types;

  public ExpressionsBasisTypesCalculator(){
    types=new HashMap<>();
  }

  @Override
  public void endVisit(ASTLiteralExpression expr){
    ASTMCType result = null;
    if(types.containsKey(expr.getExtLiteral())){
      result = types.get(expr.getExtLiteral()).getASTMCType();
    }
    if(result!=null) {
      this.result=result;
      MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
      sym.setASTMCType(result);
      types.put(expr, sym);
    }else{
      Log.error(errorCode+"The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTNameExpression expr){
    Optional<EVariableSymbol> optVar = scope.resolveEVariable(expr.getName());
    Optional<ETypeSymbol> optType = scope.resolveEType(expr.getName());
    if(optVar.isPresent()){
      EVariableSymbol var = optVar.get();
      this.result=var.getMCTypeSymbol().getASTMCType();
      types.put(expr,var.getMCTypeSymbol());
    }else if(optType.isPresent()) {
      ASTMCType type = null;
      type = optType.get().getType();
      this.result=type;
      MCTypeSymbol sym = new MCTypeSymbol(type.getBaseName());
      sym.setASTMCType(type);
      types.put(expr,sym);
    }else{
      Log.info("package or method suspected","ExpressionBasisTypesCalculator");
    }
  }

  @Override
  public void endVisit(ASTQualifiedNameExpression expr) {
    ASTMCType result = null;
    if(types.containsKey(expr)){
      MCFullGenericTypesPrettyPrinter printer = new MCFullGenericTypesPrettyPrinter(new IndentPrinter());
      String toResolve = printer.prettyprint(types.get(expr).getASTMCType())+expr.getName();
      Optional<ETypeSymbol> typeSymbolopt = scope.resolveETypeDown(toResolve);//TODO: resolve statt resolveDown
      Optional<EVariableSymbol> variableSymbolopt = scope.resolveEVariableDown(toResolve);//TODO: resolve statt resolveDown
      if(typeSymbolopt.isPresent()){
        String fullName= typeSymbolopt.get().getFullName();
        String[] parts = fullName.split("\\.");
        ArrayList<String> nameList = new ArrayList<>();
        for(String s: parts){
          nameList.add(s);
        }
        result= MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(nameList).build()).build();
        this.result=result;
      }else if(variableSymbolopt.isPresent()){
        String fullName= variableSymbolopt.get().getFullName();
        String[] parts = fullName.split("\\.");
        ArrayList<String> nameList = new ArrayList<>();
        for(String s: parts){
          nameList.add(s);
        }
        result= MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(nameList).build()).build();
        this.result=result;
      }else{
        Log.info("package or method suspected","CommonExpressionTypesCalculator");
      }
    }else{
      ExpressionsBasisPrettyPrinter printer = new ExpressionsBasisPrettyPrinter(new IndentPrinter());
      String toResolve = printer.prettyprint(expr);
      Optional<ETypeSymbol> typeSymbolopt = scope.resolveETypeDown(toResolve);//TODO: resolve statt resolveDown
      Optional<EVariableSymbol> variableSymbolopt = scope.resolveEVariableDown(toResolve);//TODO: resolve statt resolveDown
      if(typeSymbolopt.isPresent()){
        String fullName= typeSymbolopt.get().getFullName();
        String[] parts = fullName.split("\\.");
        ArrayList<String> nameList = new ArrayList<>();
        for(String s: parts){
          nameList.add(s);
        }
        result= MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(nameList).build()).build();
        this.result=result;
      }else if(variableSymbolopt.isPresent()){
        String fullName= variableSymbolopt.get().getFullName();
        String[] parts = fullName.split("\\.");
        ArrayList<String> nameList = new ArrayList<>();
        for(String s: parts){
          nameList.add(s);
        }
        result= MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(nameList).build()).build();
        this.result=result;
      }else{
        Log.info("package or method suspected", "CommonExpressionTypesCalculator");
      }
    }
  }

  public ASTMCType getResult() {
    return result;
  }

  protected ExpressionsBasisScope getScope() {
    return scope;
  }

  protected void setScope(ExpressionsBasisScope scope) {
    this.scope = scope;
  }

  protected LiteralTypeCalculator getLiteralsVisitor() {
    return literalsVisitor;
  }

  protected void setLiteralsVisitor(LiteralTypeCalculator literalsVisitor) {
    this.literalsVisitor = literalsVisitor;
  }

  protected Map<ASTNode, MCTypeSymbol> getTypes() {
    return types;
  }
}
