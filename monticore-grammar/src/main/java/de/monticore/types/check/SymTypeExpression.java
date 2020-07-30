/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.*;
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
  public boolean isTypeConstant() {
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

  /**
   * Am I an array?
   */
  public boolean isArrayType(){
    return false;
  }

  /**
   * Am I of void type?
   */
  public boolean isVoidType(){
    return false;
  }

  /**
   * Am I of null type?
   */
  public boolean isNullType(){
    return false;
  }

  /**
   * Am I an object type? (e.g. "String", "Person")
   */
  public boolean isObjectType(){
    return false;
  }



  public abstract SymTypeExpression deepClone();

  public abstract boolean deepEquals(SymTypeExpression sym);

  protected List<MethodSymbol> methodList = new ArrayList<>();

  /**
   * returns the list of methods the SymTypeExpression can access and filters these for a method with specific name
   * the last calculated type in the type check was no type
   */
  public List<MethodSymbol> getMethodList(String methodname){
    methodList.clear();
    //get methods from the typesymbol
    List<MethodSymbol> methods = getCorrectMethods(methodname,false);
    return transformMethodList(methodname,methods);
  }

  /**
   * return the correct methods for the two situations:
   * 1) the last calculated type in the type check was a type, then filter for non-static methods and
   * add the static methods of this type
   * 2) the last calculated type in the type check was an instance, then just resolve for methods
   * @param methodName name of the method we search for
   * @param outerIsType true if last result of type check was type, false if it was an instance
   * @return the correct methods for the specific case
   */
  protected List<MethodSymbol> getCorrectMethods(String methodName, boolean outerIsType){
    List<MethodSymbol> methods = getTypeInfo().getSpannedScope().resolveMethodMany(methodName);
    if(outerIsType){
      List<MethodSymbol> methodsWithoutStatic = methods.stream().filter(m -> !m.isIsStatic()).collect(Collectors.toList());
      List<MethodSymbol> localStaticMethods = getTypeInfo().getSpannedScope().getLocalMethodSymbols()
          .stream().filter(MethodSymbol::isIsStatic).collect(Collectors.toList());
      methodsWithoutStatic.addAll(localStaticMethods);
      return methodsWithoutStatic;
    }else{
      return methods;
    }
  }

  /**
   * transforms the methods by replacing their type variables with the actual type arguments
   * @param methodName name of the method we search for
   * @param methods methods that need to be transformed
   * @return transformed methods
   */
  protected List<MethodSymbol> transformMethodList(String methodName, List<MethodSymbol> methods){
    List<MethodSymbol> matchingMethods = new ArrayList<>();
    for(MethodSymbol method: methods){
      List<FieldSymbol> fieldSymbols = new ArrayList<>();
      for(FieldSymbol parameter: method.getParameterList()){
        fieldSymbols.add(parameter.deepClone());
      }
      MethodSymbol copiedMethodSymbol = method.deepClone();
      IOOSymbolsScope scope = OOSymbolsMill.oOSymbolsScopeBuilder().build();
      for(FieldSymbol parameter: fieldSymbols){
        scope.add(parameter);
      }
      for(TypeVarSymbol typeVar: method.getTypeVariableList()){
        scope.add(typeVar);
      }
      copiedMethodSymbol.setSpannedScope(scope);
      this.methodList.add(copiedMethodSymbol);
    }
    //filter methods
    for(MethodSymbol method:methodList){
      if(method.getName().equals(methodName)){
        matchingMethods.add(method.deepClone());
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
      for(MethodSymbol method: matchingMethods) {
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
      for(int i = 0;i<matchingMethods.size()-1;i++){
        for(int j = i+1;j<matchingMethods.size();j++){
          if(matchingMethods.get(i).getReturnType().print().equals(matchingMethods.get(j).getReturnType().print())&&
              matchingMethods.get(i).getParameterList().size()==matchingMethods.get(j).getParameterList().size()){
            boolean equal = true;
            for(int k = 0;k<matchingMethods.get(i).getParameterList().size();k++){
              if(!matchingMethods.get(i).getParameterList().get(k).getType().print().equals(
                  matchingMethods.get(j).getParameterList().get(k).getType().print())){
                equal = false;
              }
            }
            if(equal){
              matchingMethods.remove(matchingMethods.get(j));
            }else{
              Log.error("0xA0298 The types of the return type and the parameters of the methods have to be the same");
            }
          }
        }
      }
    }
    return matchingMethods;
  }

  /**
   * returns the correct methods in both cases: 1) the last result was a type, 2) the last result was an instance
   * @param methodName name of the method we search for
   * @param outerIsType true if the last result was a type, false if it was an instance
   * @return the correct methods for the specific case
   */
  public List<MethodSymbol> getMethodList(String methodName, boolean outerIsType){
    methodList.clear();
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

  /**
   * returns the correct fields in both cases: 1) the last result was a type, 2) the last result was an instance
   * @param fieldName name of the field we search for
   * @param outerIsType true if the last result was a type, false if it was an instance
   * @return the correct fields for the specific case
   */
  public List<FieldSymbol> getFieldList(String fieldName, boolean outerIsType){
    List<FieldSymbol> fields = getCorrectFields(fieldName,outerIsType);
    return transformFieldList(fieldName,fields);
  }

  /**
   * return the correct fields for the two situations:
   * 1) the last calculated type in the type check was a type, then filter for non-static fields and
   * add the static fields of this type
   * 2) the last calculated type in the type check was an instance, then just resolve for fields
   * @param fieldName name of the field we search for
   * @param outerIsType true if last result of type check was type, false if it was an instance
   * @return the correct fields for the specific case
   */
  protected List<FieldSymbol> getCorrectFields(String fieldName, boolean outerIsType){
    List<FieldSymbol> fields = getTypeInfo().getSpannedScope().resolveFieldMany(fieldName);
    if(outerIsType){
      List<FieldSymbol> fieldsWithoutStatic = fields.stream().filter(f->!f.isIsStatic()).collect(Collectors.toList());
      List<FieldSymbol> localStaticFields = getTypeInfo().getSpannedScope().getLocalFieldSymbols()
          .stream().filter(FieldSymbol::isIsStatic).collect(Collectors.toList());
      fieldsWithoutStatic.addAll(localStaticFields);
      return fieldsWithoutStatic;
    }else{
      return fields;
    }
  }

  /**
   * transforms the fields by replacing their type variables with the actual type arguments
   * @param fieldName name of the field we search for
   * @param fields fields that need to be transformed
   * @return transformed fields
   */
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
  protected OOTypeSymbolSurrogate typeSymbolSurrogate;

  public OOTypeSymbol getTypeInfo() {
    return typeSymbolSurrogate.lazyLoadDelegate();
  }

  public List<OOTypeSymbol> getInnerTypeList(String name) {
    List<OOTypeSymbol> types = getTypeInfo().getSpannedScope().resolveOOTypeMany(name);
    List<OOTypeSymbol> typeSymbols = Lists.newArrayList();

    for(OOTypeSymbol type:types){
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
