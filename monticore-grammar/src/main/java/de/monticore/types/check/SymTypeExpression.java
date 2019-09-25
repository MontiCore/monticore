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

  public abstract SymTypeExpression clone();

  public List<MethodSymbol> getMethodList(){
    List<MethodSymbol> methods = typeInfo.get().clone().getMethods();
    if(!isGenericType()){
      return methods;
    }else{
      List<SymTypeExpression> arguments = ((SymTypeOfGenerics)this.clone()).getArgumentList();
      List<TypeVarSymbol> typeVariableArguments = typeInfo.get().clone().getTypeParameters();
      Map<TypeVarSymbol,SymTypeExpression> map = new HashMap<>();
      if(arguments.size()!=typeVariableArguments.size()){
        Log.error("Different number of type arguments in TypeSymbol and SymTypeExpression");
      }
      for(int i=0;i<typeVariableArguments.size();i++){
        map.put(typeVariableArguments.get(i),arguments.get(i));
      }
      for(MethodSymbol method: methods) {
        for (TypeVarSymbol typeVariableArgument : typeVariableArguments) {
          if (method.getReturnType().print().equals(typeVariableArgument.getName())) {
            method.setReturnType(map.get(typeVariableArgument));
          }
        }
        for (FieldSymbol parameter : method.getParameter()) {
          SymTypeExpression parameterType = parameter.getType();
          for (TypeVarSymbol typeVariableArgument : typeVariableArguments) {
            if (parameterType.print().equals(typeVariableArgument.getName())) {
              parameter.setType(map.get(typeVariableArgument));
            }
          }
        }
      }
      return methods;
    }
  }

  public Collection<MethodSymbol> getMethod(String name){
    List<MethodSymbol> methods = getMethodList();
    List<MethodSymbol> resultList = Lists.newArrayList();
    for(MethodSymbol method:methods){
      if(method.getName().equals(name)){
        resultList.add(method);
      }
    }
    return resultList;
  }


  public List<FieldSymbol> getFieldList(){
    List<FieldSymbol> fields = typeInfo.get().clone().getFields();
    if(!isGenericType()){
      return fields;
    }else{
      List<SymTypeExpression> arguments = ((SymTypeOfGenerics)this.clone()).getArgumentList();
      List<TypeVarSymbol> typeVariableArguments = typeInfo.get().clone().getTypeParameters();
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
  protected Optional<TypeSymbol> typeInfo;
  
  public TypeSymbol getTypeInfo(ITypeSymbolsScope symbolTable) {
    if(typeInfo.isPresent()) {
      return typeInfo.get();
    }

    // TODO: in case of failure this is repeated each time ... may be inefficient
    // (but the memorization of a repeated load may be stored before looking at symtab files)
    typeInfo = symbolTable.resolveType(this.getName());
    // TODO: Error may occur? e.g. missing symbol. Was there already an error message?
    return typeInfo.get();
  }
  
  public void setTypeInfo(TypeSymbol typeInfo) {
    this.typeInfo = Optional.of(typeInfo);
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
