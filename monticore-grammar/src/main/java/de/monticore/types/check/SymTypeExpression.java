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
   * print: Conversion to a compact string, such as "int", "Person", "List< A >"
   */
  public abstract String print();
  
  /**
   * printAsJson: Umwandlung in einen kompakten Json String
   */
  protected abstract String printAsJson();
  
  /**
   * Am I primitive? (such as "int")
   * (default: no)
   */
  public boolean isPrimitive() {
    return false;
  }

  /**
   * Am I a generic type? (such as "List<Integer>")
   */
  public boolean isGenericType() {
    return false;
  }

  /**
   * Am I a type variable?
   */
  public boolean isTypeVariable(){
    return false;
  }

  public abstract SymTypeExpression deepClone();

  /**
   * gets the list of methods the SymTypeExpression can access and filters these for a method with specific name
   */
  public List<MethodSymbol> getMethodList(String methodname){
    //get methods from the typesymbol
    List<MethodSymbol> methods = typeInfo.getSpannedScope().resolveMethodMany(methodname);
    List<MethodSymbol> methodList = new ArrayList<>();
    //filter methods
    for(MethodSymbol method:methods){
      if(method.getName().equals(methodname)){
        methodList.add(method.deepClone());
      }
    }
    if(!isGenericType()){
      return methodList;
    }else{
      //compare type arguments of SymTypeExpression(actual type) and its TypeSymbol(type definition)
      List<SymTypeExpression> arguments = ((SymTypeOfGenerics)this.deepClone()).getArgumentList();
      List<TypeVarSymbol> typeVariableArguments = typeInfo.deepClone().getTypeParameterList();
      Map<TypeVarSymbol,SymTypeExpression> map = new HashMap<>();
      if(arguments.size()!=typeVariableArguments.size()){
        Log.error("Different number of type arguments in TypeSymbol and SymTypeExpression");
      }
      for(int i=0;i<typeVariableArguments.size();i++){
        //put the type arguments in a map TypeVarSymbol -> SymTypeExpression
        map.put(typeVariableArguments.get(i),arguments.get(i));
      }
      //every method in methodList: replace typevariables in parameters or return type with its
      // actual symtypeexpression
      for(MethodSymbol method: methodList) {
        //return type
        for (TypeVarSymbol typeVariableArgument : typeVariableArguments) {
          if (method.getReturnType().print().equals(typeVariableArgument.getName())&&method.getReturnType().isTypeVariable()) {
            method.setReturnType(map.get(typeVariableArgument));
          }
        }
        //type parameters
        for (FieldSymbol parameter : method.getParameterList()) {
          SymTypeExpression parameterType = parameter.getType();
          for (TypeVarSymbol typeVariableArgument : typeVariableArguments) {
            if (parameterType.print().equals(typeVariableArgument.getName())&& parameterType.isTypeVariable()) {
              parameter.setType(map.get(typeVariableArgument));
            }
          }
        }
      }
      //if there are two methods with the same parameters and return type remove the second method
      // in the list because it is a method from a super type and is overridden by the first method
      for(int i = 0;i<methodList.size()-1;i++){
        for(int j = i+1;j<methodList.size();j++){
          if(methodList.get(i).getReturnType().print().equals(methodList.get(j).getReturnType().print())&&
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

  /**
   * gets the list of fields the SymTypeExpression can access and filters these for a field with specific name
   */
  public List<FieldSymbol> getFieldList(String fieldName){
    //get methods from the typesymbol
    List<FieldSymbol> fields = typeInfo.getSpannedScope().resolveFieldMany(fieldName);
    List<FieldSymbol> fieldList = new ArrayList<>();
    //filter fields
    for(FieldSymbol field: fields){
      if(field.getName().equals(fieldName)){
        fieldList.add(field.deepClone());
      }
    }
    if(!isGenericType()){
      return fieldList;
    }else{
      //compare type arguments of SymTypeExpression(actual type) and its TypeSymbol(type definition)
      List<SymTypeExpression> arguments = ((SymTypeOfGenerics)this.deepClone()).getArgumentList();
      List<TypeVarSymbol> typeVariableArguments = typeInfo.deepClone().getTypeParameterList();
      Map<TypeVarSymbol,SymTypeExpression> map = new HashMap<>();
      if(arguments.size()!=typeVariableArguments.size()){
        Log.error("Different number of type arguments in TypeSymbol and SymTypeExpression");
      }
      for(int i=0;i<typeVariableArguments.size();i++){
        //put the type arguments in a map TypeVarSymbol -> SymTypeExpression
        map.put(typeVariableArguments.get(i),arguments.get(i));
      }
      //every field in fieldList: replace typevariables in type with its actual symtypeexpression
      for(FieldSymbol field: fieldList){
        for(TypeVarSymbol typeVariableArgument:typeVariableArguments) {
          if (field.getType().print().equals(typeVariableArgument.getName())&&field.getType().isTypeVariable()) {
            field.setType(map.get(typeVariableArgument));
          }
        }
      }
    }
    //if there are two fields with the same type remove the second field in the list because it is a
    // field from a super type and is overridden by the first field
    for(int i = 0;i<fieldList.size()-1;i++){
      for(int j = i+1;j<fieldList.size();j++){
        if(fieldList.get(i).getType().print().equals(fieldList.get(j).getType().print())){
          fieldList.remove(fieldList.get(j));
        }
      }
    }
    return fieldList;
  }

  /**
   * Constraint:
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
}
