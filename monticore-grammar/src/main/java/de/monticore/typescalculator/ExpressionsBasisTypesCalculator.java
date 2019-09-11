/* (c) https://github.com/MontiCore/monticore */
package de.monticore.typescalculator;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTLiteralExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTQualifiedNameExpression;
import de.monticore.expressions.expressionsbasis._symboltable.IExpressionsBasisScope;
import de.monticore.expressions.expressionsbasis._visitor.ExpressionsBasisVisitor;
import de.monticore.expressions.prettyprint2.ExpressionsBasisPrettyPrinter;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.MethodSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types2.SymTypeConstant;
import de.monticore.types2.SymTypeExpression;
import de.monticore.types2.SymTypeOfGenerics;
import de.monticore.types2.SymTypeOfObject;
import de.se_rwth.commons.logging.Log;

import java.util.*;

import static de.monticore.typescalculator.TypesCalculatorHelper.unbox;

public class ExpressionsBasisTypesCalculator implements ExpressionsBasisVisitor {

  protected IExpressionsBasisScope scope;

  protected LiteralTypeCalculator literalsVisitor;

  protected SymTypeExpression result;

  protected Map<ASTNode, SymTypeExpression> types;

  private ExpressionsBasisVisitor realThis;

  @Override
  public void setRealThis(ExpressionsBasisVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public ExpressionsBasisVisitor getRealThis() {
    return realThis;
  }

  public ExpressionsBasisTypesCalculator() {
    types = new HashMap<>();
    realThis = this;
  }

  @Override
  public void endVisit(ASTLiteralExpression expr) {
    SymTypeExpression result = null;
    if (types.containsKey(expr.getLiteral())) {
      result = types.get(expr.getLiteral());
    }
    if (result != null) {
      this.result = result;
      types.put(expr, result);
    } else {
      Log.error("0xA0207 The resulting type cannot be calculated");
    }
  }

  @Override
  public void endVisit(ASTLiteral lit) {
    if (!types.containsKey(lit)) {
      SymTypeExpression result = literalsVisitor.calculateType(lit);
      this.result = result;
      types.put(lit, result);
    }
  }

  @Override
  public void endVisit(ASTNameExpression expr) {
    Optional<FieldSymbol> optVar = scope.resolveField(expr.getName());
    Optional<TypeSymbol> optType = scope.resolveType(expr.getName());
    Optional<MethodSymbol> optMethod = scope.resolveMethod(expr.getName());
    if (optVar.isPresent()) {
      FieldSymbol var = optVar.get();
      this.result = var.getType();
      types.put(expr, var.getType());
    } else if (optType.isPresent()) {
      TypeSymbol type = optType.get();
      SymTypeExpression res = TypesCalculatorHelper.fromTypeSymbol(type);
      this.result = res;
      types.put(expr, res);
    } else if (optMethod.isPresent()) {
      MethodSymbol method = optMethod.get();
      if (!"void".equals(method.getReturnType().getName())) {
        SymTypeExpression type = method.getReturnType();
        this.result = type;
        types.put(expr, type);
      } else {
        SymTypeExpression res = new SymTypeConstant();
        res.setName("void");
        this.result = res;
        types.put(expr, res);
      }
    } else {
      Log.info("package suspected", "ExpressionBasisTypesCalculator");
    }
  }

  @Override
  public void endVisit(ASTQualifiedNameExpression expr) {
    String toResolve;
    if (types.containsKey(expr)) {
      result = types.get(expr);
      return;
    } else {
      ExpressionsBasisPrettyPrinter printer = new ExpressionsBasisPrettyPrinter(new IndentPrinter());
      toResolve = printer.prettyprint(expr);
    }

    // (statische innere) Klasse
    Optional<TypeSymbol> typeSymbolopt = scope.resolveType(toResolve);

    // statische variable
    Optional<FieldSymbol> variableSymbolopt = scope.resolveField(toResolve);

    // statische methode
    Optional<MethodSymbol> methodSymbolopt = scope.resolveMethod(toResolve);

    //TODO RE Reihenfolge beachten var vor? Klasse
    if (typeSymbolopt.isPresent()) {
      String fullName = typeSymbolopt.get().getFullName();
      addToTypesMapQName(expr, typeSymbolopt.get().getSuperTypeList(), fullName);
    } else if (variableSymbolopt.isPresent()) {
      ExpressionsBasisPrettyPrinter printer = new ExpressionsBasisPrettyPrinter(new IndentPrinter());
      String exprString = printer.prettyprint(expr);
      String[] stringParts = exprString.split("\\.");
      String beforeName = "";
      if (stringParts.length != 1) {
        for (int i = 0; i < stringParts.length - 1; i++) {
          beforeName += stringParts[i] + ".";
        }
        beforeName = beforeName.substring(0, beforeName.length() - 1);
        if (!scope.resolveType(beforeName).isPresent() && scope.resolveMethodMany(beforeName).isEmpty()) {
          Log.info("package suspected", "ExpressionsBasisTypesCalculator");
        } else {
          if (scope.resolveType(beforeName).isPresent()) {
            Optional<TypeSymbol> typeSymbol = scope.resolveType(beforeName);
            boolean test = false;
            for (int i = 0; i < typeSymbol.get().getFieldList().size(); i++) {
              if (!test && typeSymbol.get().getFieldList().get(i).getFullName().equals(variableSymbolopt.get().getFullName())) {
                test = true;
              }
            }
            if (!test) {
              Log.error("0xA208 the resulting type cannot be calculated");
            }
          } else {
            boolean success = true;
            Collection<MethodSymbol> methodSymbols = scope.resolveMethodMany(beforeName);
            for (MethodSymbol methodSymbol : methodSymbols) {
              if (methodSymbol.getReturnType().getName().equals("void")) {
                success = false;
              } else {
                SymTypeExpression returnType = methodSymbol.getReturnType();
                String[] primitives = new String[]{"int", "double", "char", "float", "long", "short", "byte", "boolean"};
                for (String primitive : primitives) {
                  if (primitive.equals(returnType.getName())) {
                    success = false;
                  }
                  if (success) {
                    if (!methodSymbol.getParameterList().contains(variableSymbolopt.get())) {
                      success = false;
                    }
                  }
                }
                if (!success) {
                  Log.error("0xA0208 the resulting type cannot be calculated");
                }
              }
            }
          }
        }
      }
      String fullName = variableSymbolopt.get().getType().getName();
      addToTypesMapQName(expr, fullName, variableSymbolopt.get().getType().getSuperTypes());
    } else if (methodSymbolopt.isPresent()) {
      String fullName = methodSymbolopt.get().getReturnType().getName();
      addToTypesMapQName(expr, fullName, methodSymbolopt.get().getReturnType().getSuperTypes());
    } else {
      Log.info("package suspected", "ExpressionsBasisTypesCalculator");
    }
  }

  private void addToTypesMapQName(ASTExpression expr, List<TypeSymbol> superTypes, String fullName) {
    String[] parts = fullName.split("\\.");
    ArrayList<String> nameList = new ArrayList<>();
    Collections.addAll(nameList, parts);
    SymTypeExpression res = new SymTypeOfObject();
    res.setName(fullName);

    List<SymTypeExpression> superTypesAsSymTypes = new ArrayList<>();
    for (TypeSymbol ts : superTypes) {
      SymTypeExpression s = new SymTypeOfGenerics();
      s.setName(ts.getName());

    }


    res.setSuperTypes(superTypesAsSymTypes);
    this.result = res;
    types.put(expr, unbox(res));
  }

  private void addToTypesMapQName(ASTExpression expr, String fullName, List<SymTypeExpression> superTypes) {
    String[] parts = fullName.split("\\.");
    ArrayList<String> nameList = new ArrayList<>();
    Collections.addAll(nameList, parts);
    SymTypeExpression res = new SymTypeOfObject();
    res.setName(fullName);

    List<SymTypeExpression> superTypesAsSymTypes = new ArrayList<>();
    for (SymTypeExpression ts : superTypes) {
      SymTypeExpression s = new SymTypeOfGenerics();
      s.setName(ts.getName());
      superTypesAsSymTypes.add(s);
    }


    res.setSuperTypes(superTypesAsSymTypes);
    this.result = res;
    types.put(expr, unbox(res));
  }


  public SymTypeExpression getResult() {
    return result;
  }

  protected IExpressionsBasisScope getScope() {
    return scope;
  }

  public void setScope(IExpressionsBasisScope scope) {
    this.scope = scope;
  }

  protected LiteralTypeCalculator getLiteralsVisitor() {
    return literalsVisitor;
  }

  protected void setLiteralsVisitor(LiteralTypeCalculator literalsVisitor) {
    this.literalsVisitor = literalsVisitor;
  }

  protected Map<ASTNode, SymTypeExpression> getTypes() {
    return types;
  }

  public void setTypes(Map<ASTNode, SymTypeExpression> types) {
    this.types = types;
  }

  public SymTypeExpression calculateType(ASTExpression expr) {
    expr.accept(realThis);
    return types.get(expr);
  }
}
