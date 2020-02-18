/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.types.typesymbols._symboltable.*;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
   * returns the list of methods the SymTypeExpression can access and filters these for a method with specific name
   */
  public List<MethodSymbol> getMethodList(String methodname){
    //get methods from the typesymbol
    List<MethodSymbol> methods = getCorrectMethods(methodname,false);
    return transformMethodList(methodname,methods);
  }

  protected List<MethodSymbol> getCorrectMethods(String methodName, boolean outerIsType){
    List<MethodSymbol> methods = getTypeInfo().getSpannedScope().resolveMethodMany(methodName);
    if(outerIsType){
      List<MethodSymbol> methodsWithoutStatic = methods.stream().filter(m -> !m.isIsStatic()).collect(Collectors.toList());
      List<MethodSymbol> localStaticMethods = getTypeInfo().getSpannedScope().getLocalMethodSymbols()
          .stream().filter(MethodSymbolTOP::isIsStatic).collect(Collectors.toList());
      methodsWithoutStatic.addAll(localStaticMethods);
      return methodsWithoutStatic;
    }else{
      return methods;
    }
  }

  protected List<MethodSymbol> transformMethodList(String methodName, List<MethodSymbol> methods){
    List<MethodSymbol> methodList = new ArrayList<>();
    //filter methods
    for(MethodSymbol method:methods){
      if(method.getName().equals(methodName)){
        methodList.add(method.deepClone());
      }
    }
    if(isGenericType()){
      //compare type arguments of SymTypeExpression(actual type) and its TypeSymbol(type definition)
      List<SymTypeExpression> arguments = ((SymTypeOfGenerics)this.deepClone()).getArgumentList();
      List<TypeVarSymbol> typeVariableArguments = getTypeInfo().getTypeParameterList();
      Map<TypeVarSymbol,SymTypeExpression> map = new HashMap<>();
      if(arguments.size()!=typeVariableArguments.size()){
        Log.error("0xA0300 Different number of type arguments in TypeSymbol and SymTypeExpression");
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
            }else{
              Log.error("0xA0298 The types of the return type and the parameters of the methods have to be the same");
            }
          }
        }
      }
    }
    return methodList;
  }

  public List<MethodSymbol> getMethodList(String methodName, boolean outerIsType){
    List<MethodSymbol> methods = getCorrectMethods(methodName,outerIsType);
    return transformMethodList(methodName,methods);
  }

  /**
   * returns the list of fields the SymTypeExpression can access and filters these for a field with specific name
   */
  public List<FieldSymbol> getFieldList(String fieldName){
    //get methods from the typesymbol
    List<FieldSymbol> fields = getCorrectFields(fieldName,false);
    return transformFieldList(fieldName,fields);
  }

  public List<FieldSymbol> getFieldList(String fieldName, boolean outerIsType){
    List<FieldSymbol> fields = getCorrectFields(fieldName,outerIsType);
    return transformFieldList(fieldName,fields);
  }

  protected List<FieldSymbol> getCorrectFields(String fieldName, boolean outerIsType){
    List<FieldSymbol> fields = getTypeInfo().getSpannedScope().resolveFieldMany(fieldName);
    if(outerIsType){
      List<FieldSymbol> fieldsWithoutStatic = fields.stream().filter(f->!f.isIsStatic()).collect(Collectors.toList());
      List<FieldSymbol> localStaticFields = getTypeInfo().getSpannedScope().getLocalFieldSymbols()
          .stream().filter(FieldSymbolTOP::isIsStatic).collect(Collectors.toList());
      fieldsWithoutStatic.addAll(localStaticFields);
      return fieldsWithoutStatic;
    }else{
      return fields;
    }
  }

  protected List<FieldSymbol> transformFieldList(String fieldName, List<FieldSymbol> fields){
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
      List<TypeVarSymbol> typeVariableArguments = getTypeInfo().getTypeParameterList();
      Map<TypeVarSymbol,SymTypeExpression> map = new HashMap<>();
      if(arguments.size()!=typeVariableArguments.size()){
        Log.error("0xA0301 Different number of type arguments in TypeSymbol and SymTypeExpression");
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
        }else{
          Log.error("0xA0299 The types of the fields have to be same");
        }
      }
    }
    return fieldList;
  }

  /**
   * Constraint:
   * We assume that each(!) and really each SymTypeExpression has
   * an associated TypeSymbol, where all available Fields, Methods, etc. can be found.
   * <p>
   * These may, however, be empty, e.g. for primitive Types.
   * <p>
   * Furthermore, each SymTypeExpression knows this TypeSymbol (i.e. the
   * TypeSymbols are loaded (or created) upon creation of the SymType.
   */
  protected TypeSymbolLoader typeSymbolLoader;

  public TypeSymbol getTypeInfo() {
    return typeSymbolLoader.getLoadedSymbol();
  }

  public List<TypeSymbol> getInnerTypeList(String name) {
    List<TypeSymbol> types = getTypeInfo().getSpannedScope().resolveTypeMany(name);
    List<TypeSymbol> typeSymbols = Lists.newArrayList();

    for(TypeSymbol type:types){
      if(name!=null && name.equals(type.getName())){
        typeSymbols.add(type);
      }
    }
    if(!isGenericType()){
      //check recursively for more inner types, replace every type variable in methods and fields
      //watch for new type variables declared in inner types and their uses
    }
    return typeSymbols;
  }

  // --------------------------------------------------------------------------
}
