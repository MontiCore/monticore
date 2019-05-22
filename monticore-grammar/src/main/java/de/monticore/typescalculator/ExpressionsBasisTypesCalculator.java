package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.*;
import de.monticore.expressions.expressionsbasis._symboltable.EMethodSymbol;
import de.monticore.expressions.expressionsbasis._symboltable.ETypeSymbol;
import de.monticore.expressions.expressionsbasis._symboltable.EVariableSymbol;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisScope;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor;
import de.monticore.expressions.prettyprint2.ExpressionsBasisPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._symboltable.MCTypeSymbol;
import de.monticore.types.prettyprint.MCFullGenericTypesPrettyPrinter;
import de.se_rwth.commons.logging.Log;
import sun.security.krb5.internal.crypto.EType;

import java.util.*;

public class ExpressionsBasisTypesCalculator implements ExpressionsBasisVisitor {

  protected ExpressionsBasisScope scope;

  protected LiteralTypeCalculator literalsVisitor;

  protected ASTMCType result;

  protected Map<ASTNode, MCTypeSymbol> types;

  private ExpressionsBasisVisitor realThis;

  @Override
  public void setRealThis(ExpressionsBasisVisitor realThis){
    this.realThis=realThis;
  }

  @Override
  public ExpressionsBasisVisitor getRealThis(){
    return realThis;
  }

  public ExpressionsBasisTypesCalculator(){
    types=new HashMap<>();
    realThis=this;
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
      Log.error("0xA0207 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTNameExpression expr){
    Optional<EVariableSymbol> optVar = scope.resolveEVariable(expr.getName());
    Optional<ETypeSymbol> optType = scope.resolveEType(expr.getName());
    Optional<EMethodSymbol> optMethod = scope.resolveEMethod(expr.getName());
    if(optVar.isPresent()){
      EVariableSymbol var = optVar.get();
      this.result=var.getMCTypeSymbol().getASTMCType();
      types.put(expr,var.getMCTypeSymbol());
    }else if(optType.isPresent()) {
      ASTMCType type = optType.get().getType();
      this.result=type;
      MCTypeSymbol sym = new MCTypeSymbol(type.getBaseName());
      sym.setASTMCType(type);
      types.put(expr,sym);
    }else if(optMethod.isPresent()) {
      EMethodSymbol method = optMethod.get();
      if(method.getReturnType().isPresentMCType()){
        ASTMCType type=method.getReturnType().getMCType();
        this.result=type;
        MCTypeSymbol sym = new MCTypeSymbol(type.getBaseName());
        sym.setASTMCType(type);
        types.put(expr,sym);
      }else{
        List<String> name = new ArrayList<>();
        name.add("void");
        ASTMCType type = MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(name).build()).build();
        this.result=type;
        MCTypeSymbol sym = new MCTypeSymbol(type.getBaseName());
        sym.setASTMCType(type);
        types.put(expr,sym);
      }
    }else{
      Log.info("package suspected","ExpressionBasisTypesCalculator");
    }
  }

  @Override
  public void endVisit(ASTQualifiedNameExpression expr) {
    String toResolve;
    if(types.containsKey(expr)) {
      MCFullGenericTypesPrettyPrinter printer = new MCFullGenericTypesPrettyPrinter(new IndentPrinter());
      toResolve = printer.prettyprint(types.get(expr).getASTMCType()) + expr.getName();
    }else{
      ExpressionsBasisPrettyPrinter printer = new ExpressionsBasisPrettyPrinter(new IndentPrinter());
      toResolve = printer.prettyprint(expr);
    }
      Optional<ETypeSymbol> typeSymbolopt = scope.resolveETypeDown(toResolve);//TODO: resolve statt resolveDown
      Optional<EVariableSymbol> variableSymbolopt = scope.resolveEVariableDown(toResolve);//TODO: resolve statt resolveDown
      Optional<EMethodSymbol> methodSymbolopt = scope.resolveEMethodDown(toResolve);
      if(typeSymbolopt.isPresent()){
        String fullName= typeSymbolopt.get().getFullName();
        String[] parts = fullName.split("\\.");
        ArrayList<String> nameList = new ArrayList<>();
        Collections.addAll(nameList,parts);
        ASTMCType result= MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(nameList).build()).build();
        this.result=result;
        MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
        sym.setASTMCType(result);
        types.put(expr,sym);
      }else if(variableSymbolopt.isPresent()){
        ExpressionsBasisPrettyPrinter printer = new ExpressionsBasisPrettyPrinter(new IndentPrinter());
        String exprString = printer.prettyprint(expr);
        String[] stringParts = exprString.split("\\.");
        String beforeName="";
        if(stringParts.length!=1){
          for(int i=0;i<stringParts.length-1;i++){
            beforeName+=stringParts[i]+".";
          }
          beforeName=beforeName.substring(0,beforeName.length()-1);
          if(!scope.resolveETypeDown(beforeName).isPresent()){ //TODO: replace resolveDown with resolve
            Log.info("package suspected","ExpressionsBasisTypesCalculator");
          }else{
            Optional<ETypeSymbol> typeSymbol = scope.resolveETypeDown(beforeName); //TODO: replace resolveDown with resolve
            if(!typeSymbol.get().getVariableSymbols().contains(variableSymbolopt.get())){
              Log.error("0xA208 the resulting type cannot be calculated");
            }
          }
        }

        String fullName= variableSymbolopt.get().getFullName();
        String[] parts = fullName.split("\\.");
        ArrayList<String> nameList = new ArrayList<>();
        Collections.addAll(nameList,parts);
        ASTMCType result= MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(nameList).build()).build();
        this.result=result;
        MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
        sym.setASTMCType(result);
        types.put(expr,sym);
      }else if(methodSymbolopt.isPresent()) {
        String fullName = methodSymbolopt.get().getFullName();
        String[] parts = fullName.split("\\.");
        ArrayList<String> nameList = new ArrayList<>();
        Collections.addAll(nameList,parts);
        ASTMCType result= MCBasicTypesMill.mCQualifiedTypeBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(nameList).build()).build();
        this.result=result;
        MCTypeSymbol sym = new MCTypeSymbol(result.getBaseName());
        sym.setASTMCType(result);
        types.put(expr,sym);
      }else{
        Log.info("package suspected","ExpressionsBasisTypesCalculator");
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

  public void setTypes(Map<ASTNode,MCTypeSymbol> types){
    this.types=types;
  }

  public ASTMCType calculateType(ASTExpression expr){
    expr.accept(realThis);
    return types.get(expr).getASTMCType();
  }
}
