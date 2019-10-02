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
          if (method.getReturnType().print().equals(typeVariableArgument.getName())&&method.getReturnType() instanceof SymTypeVariable) {
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
      for(int i = 0;i<methodList.size()-1;i++){
        for(int j = i+1;j<methodList.size();j++){
          if(methodList.get(i).getReturnType().getName().equals(methodList.get(j).getReturnType().getName())&&
              methodList.get(i).getParameterList().size()==methodList.get(j).getParameterList().size()){
            boolean equal = true;
            for(int k = 0;k<methodList.get(i).getParameterList().size();k++){
              if(!methodList.get(i).getParameterList().get(k).getType().print().equals(
                  methodList.get(j).getParameterList().get(k).getType().print())){
                equal = false;
              }
            }
            if(equal){
              methodList.remove(methodList.get(j));
            }
          }
        }
      }
      return methodList;
    }
  }

  public List<FieldSymbol> getFieldList(String fieldName){
    List<FieldSymbol> fields = typeInfo.deepClone().getFieldList();
    List<FieldSymbol> fieldList = new ArrayList<>();
    for(FieldSymbol field: fields){
      if(field.getName().equals(fieldName)){
        fieldList.add(field);
      }
    }
    for(SymTypeExpression superType:typeInfo.getSuperTypeList()){
      fieldList.addAll(superType.getFieldList(fieldName));
    }
    if(!isGenericType()){
      return fieldList;
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
      for(FieldSymbol field: fieldList){
        for(TypeVarSymbol typeVariableArgument:typeVariableArguments) {
          if (field.getType().print().equals(typeVariableArgument.getName())&&field.getType() instanceof SymTypeVariable) {
            field.setType(map.get(typeVariableArgument));
          }
        }
      }
    }
    for(int i = 0;i<fieldList.size()-1;i++){
      for(int j = i+1;j<fieldList.size();j++){
        if(fieldList.get(i).getType().getName().equals(fieldList.get(j).getType().getName())){
          fieldList.remove(fieldList.get(j));
        }
      }
    }
    return fieldList;
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
