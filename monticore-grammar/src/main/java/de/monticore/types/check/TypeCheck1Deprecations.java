/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types3.generics.TypeParameterRelations;
import de.monticore.types3.util.SymTypeCollectionVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Legacy SymTypeExpression Functionality of TypeCheck1:
 * Warning: very broken.
 * <p>
 * This has been extracted to assure that all usage points are known:
 * With only few exceptions, the last place this is used in
 * (and needs to be removed/replaced from)
 * is the resolver (SymbolsScopes).
 *
 * @deprecated use TypeCheck 3
 */
@Deprecated(forRemoval = true)
public final class TypeCheck1Deprecations {

  private TypeCheck1Deprecations() {
  }

  public static List<FunctionSymbol> getMethodList(
      SymTypeExpression type, String methodName, boolean abstractTc) {
    return getMethodList(type, methodName, abstractTc,
        AccessModifier.ALL_INCLUSION);
  }

  public static List<FunctionSymbol> getMethodList(
      SymTypeExpression type, String methodName, boolean abstractTc,
      AccessModifier modifier) {
    return transformMethodList(type, methodName,
        getCorrectMethods(type, methodName, false, abstractTc, modifier));
  }

  public static List<FunctionSymbol> getMethodList(
      SymTypeExpression type, String methodName, boolean outerIsType,
      boolean abstractTc) {
    return getMethodList(type, methodName, outerIsType, abstractTc,
        AccessModifier.ALL_INCLUSION);
  }

  public static List<FunctionSymbol> getMethodList(
      SymTypeExpression type, String methodName, boolean outerIsType,
      boolean abstractTc, AccessModifier modifier) {
    return transformMethodList(type, methodName,
        getCorrectMethods(type, methodName, outerIsType, abstractTc,
            modifier));
  }

  public static List<FunctionSymbol> getCorrectMethods(
      SymTypeExpression type, String methodName, boolean outerIsType,
      boolean abstractTc) {
    return getCorrectMethods(type, methodName, outerIsType, abstractTc,
        AccessModifier.ALL_INCLUSION);
  }

  public static List<FunctionSymbol> getCorrectMethods(
      SymTypeExpression type, String methodName, boolean outerIsType,
      boolean abstractTc, AccessModifier modifier) {
    if (type.isObscureType()) {
      return List.of();
    }
    if (abstractTc) {
      return type.getTypeInfo().getSpannedScope()
          .resolveFunctionMany(methodName, modifier);
    }
    List<FunctionSymbol> functions = type.getTypeInfo().getSpannedScope()
        .resolveFunctionMany(methodName, modifier).stream()
        .filter(f -> !(f instanceof MethodSymbol))
        .collect(Collectors.toList());
    List<FunctionSymbol> methods = Lists.newArrayList();
    if (type.getTypeInfo().getSpannedScope() instanceof IOOSymbolsScope) {
      methods.addAll(type.getTypeInfo().getSpannedScope()
          .resolveFunctionMany(methodName, modifier).stream()
          .filter(f -> f instanceof MethodSymbol)
          .toList());
    }
    if (!outerIsType) {
      functions.addAll(methods);
      return functions;
    }
    List<FunctionSymbol> methodsWithoutStatic = methods.stream()
        .filter(Objects::nonNull)
        .map(m -> (MethodSymbol) m)
        .filter(m -> !m.isIsStatic())
        .collect(Collectors.toList());
    methodsWithoutStatic.addAll(functions);
    if (type.getTypeInfo().getSpannedScope() instanceof IOOSymbolsScope scope) {
      methodsWithoutStatic.addAll(scope.getLocalMethodSymbols().stream()
          .filter(MethodSymbol::isIsStatic)
          .toList());
    }
    return methodsWithoutStatic;
  }

  public static List<FunctionSymbol> transformMethodList(
      SymTypeExpression type, String methodName,
      List<FunctionSymbol> functions) {
    List<FunctionSymbol> matchingMethods = new ArrayList<>();
    for (FunctionSymbol method : functions) {
      List<VariableSymbol> parameters = new ArrayList<>();
      for (VariableSymbol parameter : method.getParameterList()) {
        parameters.add(parameter.deepClone());
      }
      FunctionSymbol copiedMethod = method.deepClone();
      IOOSymbolsScope scope = OOSymbolsMill.scope();
      parameters.forEach(scope::add);
      method.getTypeVariableList().forEach(scope::add);
      copiedMethod.setSpannedScope(scope);
      if (copiedMethod.getName().equals(methodName)) {
        matchingMethods.add(copiedMethod.deepClone());
      }
    }
    if (type.isGenericType()) {
      Map<TypeVarSymbol, SymTypeExpression> replacements =
          getTypeVariableReplacements(type, "0xA1300");
      for (FunctionSymbol method : matchingMethods) {
        method.setType(replaceMemberTypeVariables(
            method.getType(), replacements
        ));
        for (VariableSymbol parameter : method.getParameterList()) {
          parameter.setType(replaceMemberTypeVariables(
              parameter.getType(), replacements
          ));
        }
      }
      removeDuplicateMethods(matchingMethods);
    }
    return matchingMethods;
  }

  public static List<FunctionSymbol> resolveSuperTypeFunctions(
      SymTypeExpression superType,
      String name,
      AccessModifier modifier
  ) {
    List<FunctionSymbol> functions = superType.getTypeInfo()
        .getSpannedScope().resolveFunctionMany(name, modifier);
    return transformMethodList(superType, name, functions);
  }

  public static List<VariableSymbol> getFieldList(
      SymTypeExpression type, String fieldName, boolean abstractTc) {
    return getFieldList(type, fieldName, abstractTc,
        AccessModifier.ALL_INCLUSION);
  }

  public static List<VariableSymbol> getFieldList(
      SymTypeExpression type, String fieldName, boolean abstractTc,
      AccessModifier modifier) {
    return transformFieldList(type, fieldName,
        getCorrectFields(type, fieldName, false, abstractTc, modifier));
  }

  public static List<VariableSymbol> getFieldList(
      SymTypeExpression type, String fieldName, boolean outerIsType,
      boolean abstractTc) {
    return getFieldList(type, fieldName, outerIsType, abstractTc,
        AccessModifier.ALL_INCLUSION);
  }

  public static List<VariableSymbol> getFieldList(
      SymTypeExpression type, String fieldName, boolean outerIsType,
      boolean abstractTc, AccessModifier modifier) {
    return transformFieldList(type, fieldName,
        getCorrectFields(type, fieldName, outerIsType, abstractTc,
            modifier));
  }

  public static List<VariableSymbol> getCorrectFields(
      SymTypeExpression type, String fieldName, boolean outerIsType,
      boolean abstractTc) {
    return getCorrectFields(type, fieldName, outerIsType, abstractTc,
        AccessModifier.ALL_INCLUSION);
  }

  public static List<VariableSymbol> getCorrectFields(
      SymTypeExpression type, String fieldName, boolean outerIsType,
      boolean abstractTc, AccessModifier modifier) {
    if (type.isObscureType()) {
      return List.of();
    }
    if (abstractTc) {
      return type.getTypeInfo().getSpannedScope()
          .resolveVariableMany(fieldName, modifier);
    }
    List<VariableSymbol> variables = type.getTypeInfo().getSpannedScope()
        .resolveVariableMany(fieldName, modifier).stream()
        .filter(v -> !(v instanceof FieldSymbol))
        .collect(Collectors.toList());
    List<VariableSymbol> fields = Lists.newArrayList();
    if (type.getTypeInfo().getSpannedScope() instanceof IOOSymbolsScope) {
      fields.addAll(type.getTypeInfo().getSpannedScope()
          .resolveVariableMany(fieldName, modifier).stream()
          .filter(v -> v instanceof FieldSymbol)
          .toList());
    }
    if (!outerIsType) {
      variables.addAll(fields);
      return variables;
    }
    List<VariableSymbol> fieldsWithoutStatic = fields.stream()
        .map(f -> (FieldSymbol) f)
        .filter(f -> !f.isIsStatic())
        .collect(Collectors.toList());
    fieldsWithoutStatic.addAll(variables);
    if (type.getTypeInfo().getSpannedScope() instanceof IOOSymbolsScope scope) {
      fieldsWithoutStatic.addAll(scope.getLocalFieldSymbols().stream()
          .filter(FieldSymbol::isIsStatic)
          .toList());
    }
    return fieldsWithoutStatic;
  }

  public static List<VariableSymbol> transformFieldList(
      SymTypeExpression type, String fieldName, List<VariableSymbol> fields) {
    List<VariableSymbol> fieldList = new ArrayList<>();
    for (VariableSymbol field : fields) {
      if (field.getName().equals(fieldName)) {
        fieldList.add(field.deepClone());
      }
    }
    if (!type.isGenericType()) {
      return fieldList;
    }
    Map<TypeVarSymbol, SymTypeExpression> replacements =
        getTypeVariableReplacements(type, "0xA1301");
    fieldList.forEach(field -> field.setType(replaceMemberTypeVariables(
        field.getType(), replacements
    )));
    removeDuplicateFields(fieldList);
    return fieldList;
  }

  public static List<VariableSymbol> resolveSuperTypeVariables(
      SymTypeExpression superType,
      String name,
      AccessModifier modifier
  ) {
    List<VariableSymbol> variables = superType.getTypeInfo()
        .getSpannedScope().resolveVariableMany(name, modifier);
    return transformFieldList(superType, name, variables);
  }

  private static SymTypeExpression replaceMemberTypeVariables(
      SymTypeExpression type,
      Map<TypeVarSymbol, SymTypeExpression> replacements
  ) {
    Map<SymTypeVariable, SymTypeExpression> relationReplacements =
        new LinkedHashMap<>();
    for (SymTypeExpression variable : new SymTypeCollectionVisitor().calculate(
        type, SymTypeExpression::isTypeVariable
    )) {
      SymTypeVariable typeVariable = variable.asTypeVariable();
      replacements.entrySet().stream()
          .filter(entry -> entry.getKey().getName().equals(
              typeVariable.getTypeVarSymbol().getName()
          ))
          .findFirst()
          .ifPresent(entry -> relationReplacements.put(
              typeVariable, entry.getValue()
          ));
    }
    return TypeParameterRelations.replaceTypeVariables(
        type, relationReplacements
    );
  }

  private static Map<TypeVarSymbol, SymTypeExpression>
  getTypeVariableReplacements(SymTypeExpression type, String errorCode) {
    List<SymTypeExpression> arguments =
        type.asGenericType().deepClone().asGenericType().getArgumentList();
    List<TypeVarSymbol> typeVariables =
        type.getTypeInfo().getTypeParameterList();
    if (arguments.size() != typeVariables.size()) {
      Log.error(errorCode + " Different number of type arguments in "
          + "TypeSymbol and SymTypeExpression");
    }
    Map<TypeVarSymbol, SymTypeExpression> replacements = new LinkedHashMap<>();
    for (int i = 0; i < typeVariables.size(); i++) {
      replacements.put(typeVariables.get(i), arguments.get(i));
    }
    return replacements;
  }

  private static void removeDuplicateMethods(List<FunctionSymbol> methods) {
    for (int i = 0; i < methods.size() - 1; i++) {
      for (int j = i + 1; j < methods.size(); j++) {
        FunctionSymbol first = methods.get(i);
        FunctionSymbol second = methods.get(j);
        if (!first.getType().print().equals(second.getType().print())
            || first.getParameterList().size()
            != second.getParameterList().size()) {
          continue;
        }
        boolean equal = true;
        for (int k = 0; k < first.getParameterList().size(); k++) {
          if (!first.getParameterList().get(k).getType().print().equals(
              second.getParameterList().get(k).getType().print())) {
            equal = false;
          }
        }
        if (equal) {
          methods.remove(j);
        }
        else {
          Log.error("0xA2298 The types of the return type and the "
              + "parameters of the methods have to be the same");
        }
      }
    }
  }

  private static void removeDuplicateFields(List<VariableSymbol> fields) {
    for (int i = 0; i < fields.size() - 1; i++) {
      for (int j = i + 1; j < fields.size(); j++) {
        if (fields.get(i).getType().print().equals(
            fields.get(j).getType().print())) {
          fields.remove(j);
        }
        else {
          Log.error("0xA2299 The types of the fields have to be same");
        }
      }
    }
  }
}
