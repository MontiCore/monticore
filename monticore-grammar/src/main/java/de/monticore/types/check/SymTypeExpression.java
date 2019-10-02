/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.types.typesymbols._symboltable.*;
import de.se_rwth.commons.logging.Log;

import java.util.*;

/**
 * SymTypeExpression is the superclass for all typeexpressions, such as
 * TypeConstants, TypeVariables and applications of Type-Constructors.
 * It shares common functionality
 * (such as comparison, printing)
 */
public abstract class SymTypeExpression {

  /**
   * print: Umwandlung in einen kompakten String
   */
  public abstract String print();
  
  /**
   * printAsJson: Umwandlung in einen kompakten Json String
   */
  protected abstract String printAsJson();
  
  /**
   * Am I primitive? (such as "int")
   * (is this needed?)
   */
  public boolean isPrimitiveType() {
    return false;
  }

  /**
   * Am I a generic type? (such as "List<Integer>")
   */
  public boolean isGenericType() {
    return false;
  }

  public abstract SymTypeExpression deepClone();

  public List<MethodSymbol> getMethodList(String methodname){
    List<MethodSymbol> methods = typeInfo.deepClone().getMethodList();
    List<MethodSymbol> methodList = new ArrayList<>();
    for(MethodSymbol method:methods){
      if(method.getName().equals(methodname)){
        methodList.add(method);
      }
    }
    for(SymTypeExpression superType:typeInfo.getSuperTypeList()){
      methodList.addAll(superType.getTypeInfo().getMethodList(methodname));
    }
    if(!isGenericType()){
      return methodList;
    }else{
      List<SymTypeExpression> arguments = ((SymTypeOfGenerics)this.deepClone()).getArgumentList();
      List<TypeVarSymbol> typeVariableArguments = typeInfo.deepClone().getTypeParameterList();
      Map<TypeVarSymbol,SymTypeExpression> map = new HashMap<>();
      if(arguments.size()!=typeVariableArguments.size()){
        Log.error("Different number of type arguments in TypeSymbol and SymTypeExpression");
      }
      for(int i=0;i<typeVariableArguments.size();i++){
        map.put(typeVariableArguments.get(i),arguments.get(i));
      }
      for(MethodSymbol method: methodList) {
        for (TypeVarSymbol typeVariableArgument : typeVariableArguments) {
          if (method.getReturnType().print().equals(typeVariableArgument.getName())) {
            method.setReturnType(map.get(typeVariableArgument));
          }
        }
        for (FieldSymbol parameter : method.getParameterList()) {
          SymTypeExpression parameterType = parameter.getType();
          for (TypeVarSymbol typeVariableArgument : typeVariableArguments) {
            if (parameterType.print().equals(typeVariableArgument.getName())&& parameterType instanceof SymTypeVariable) {
              parameter.setType(map.get(typeVariableArgument));
            }
          }
        }
      }
      return methodList;
    }
  }

  public List<MethodSymbol> getMethod(String name){
    List<MethodSymbol> methods = getMethodList(name);
    List<MethodSymbol> resultList = Lists.newArrayList();
    for(MethodSymbol method:methods){
      if(method.getName().equals(name)){
        resultList.add(method);
      }
    }
    for(SymTypeExpression superType:typeInfo.getSuperTypeList()){
      resultList.addAll(superType.getMethod(name));
    }
    return resultList;
  }


  public List<FieldSymbol> getFieldList(){
    List<FieldSymbol> fields = typeInfo.deepClone().getFieldList();
    if(!isGenericType()){
      return fields;
    }else{
      List<SymTypeExpression> arguments = ((SymTypeOfGenerics)this.deepClone()).getArgumentList();
      List<TypeVarSymbol> typeVariableArguments = typeInfo.deepClone().getTypeParameterList();
      Map<TypeVarSymbol,SymTypeExpression> map = new HashMap<>();
      if(arguments.size()!=typeVariableArguments.size()){
        Log.error("Different number of type arguments in TypeSymbol and SymTypeExpression");
      }
      for(int i=0;i<typeVariableArguments.size();i++){
        map.put(typeVariableArguments.get(i),arguments.get(i));
      }
      for(FieldSymbol field: fields){
        SymTypeExpression fieldType = field.getType();
        for(TypeVarSymbol typeVariableArgument: typeVariableArguments){
          if(fieldType.print().equals(typeVariableArgument.getName())){
            field.setType(map.get(typeVariableArgument));
          }
        }
      }
      return fields;
    }
  }


  /**
   * Assumption:
   * We assume that each(!) and really each SymTypeExpression has
   * an associated TypeSymbol, where all available Fields, Methods, etc. can be found.
   *
   * These may, however, be empty, e.g. for primitive Types.
   *
   * Furthermore, each SymTypeExpression knows this TypeSymbol (i.e. the
   * TypeSymbols are loaded (or created) upon creation of the SymType.
   */
  protected TypeSymbol typeInfo;
  
  public TypeSymbol getTypeInfo() {
      return typeInfo;
  }
  
  public void setTypeInfo(TypeSymbol typeInfo) {
    this.typeInfo = typeInfo;
  }
  
  // --------------------------------------------------------------------------

  /**
   * A type has a name (XXX BR Exceptions may apply?)
   * anonymous types only in List<?> in FullGenericTypes.mc4, not yet supported
   */
  protected String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
