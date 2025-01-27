// (c) https://github.com/MontiCore/monticore
package de.monticore.types.check;

import com.google.common.collect.Lists;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * DO NOT USE:
 * Severely broken behavior, including
 * Wrong method filtering,
 * Missing replacing of type variables
 * non-Optimal return type (using Symbols instead of SymTypeExpressions loses information)
 * broken type equality checks (String based)
 * <p>
 * Why does this (still) exist?
 * Too many language tools use this broken behavior
 * in the even more broken resolving-process (ignores AccessModifier, Filter, etc...)
 * -> This is hard to remove from all our tools and will take quite some time.
 * As such, this Class is used as long as the resolving Behavior is still required.
 * Because this is first and foremost used during resolving,
 * it has been extracted from SymTypeExpression.
 * <p>
 * As an alternative, try
 * {@link de.monticore.types3.util.WithinTypeBasicSymbolsResolver},
 * which returns SymTypeExpressions (which in turn refers to the symbol),
 * and handles AccessModifiers and filtering.
 */
@Deprecated
public class DeprecatedSymTypeExpressionSymbolResolver {

  /**
   * returns the list of methods the SymTypeExpression can access and
   * filters these for a method with specific name
   * the last calculated type in the type check was no type
   */
  public static List<FunctionSymbol> getMethodList(SymTypeExpression typeExpr, String methodName, boolean abstractTc) {
    return getMethodList(typeExpr, methodName, abstractTc, AccessModifier.ALL_INCLUSION);
  }

  public static List<FunctionSymbol> getMethodList(SymTypeExpression typeExpr, String methodName, boolean abstractTc, AccessModifier modifier) {
    if (typeExpr.isObscureType())
      return Collections.emptyList();
    List<FunctionSymbol> functionList = new ArrayList<>();
    // Get methods from the type symbol
    List<FunctionSymbol> methods = getCorrectMethods(typeExpr, methodName, false, abstractTc, modifier);
    return transformMethodList(typeExpr, methodName, methods, functionList);
  }

  public static List<FunctionSymbol> getMethodList(SymTypeExpression typeExpr, String methodName, boolean outerIsType, boolean abstractTc) {
    return getMethodList(typeExpr, methodName, outerIsType, abstractTc, AccessModifier.ALL_INCLUSION);
  }

  /**
   * returns the correct methods in both cases:
   * 1) the last result was a type,
   * 2) the last result was an instance
   *
   * @param methodName  name of the method we search for
   * @param outerIsType true if the last result was a type, false
   *                    if it was an instance
   * @return the correct methods for the specific case
   */
  public static List<FunctionSymbol> getMethodList(SymTypeExpression typeExpr, String methodName, boolean outerIsType, boolean abstractTc, AccessModifier modifier) {
    if (typeExpr.isObscureType())
      return Collections.emptyList();
    List<FunctionSymbol> functionList = new ArrayList<>();
    List<FunctionSymbol> methods = getCorrectMethods(typeExpr, methodName, outerIsType, abstractTc, modifier);
    return transformMethodList(typeExpr, methodName, methods, functionList);
  }

  /**
   * return the correct methods for the two situations:
   * 1) the last calculated type in the type check was a type,
   * then filter for non-static methods and
   * add the static methods of this type
   * 2) the last calculated type in the type check was an instance,
   * then just resolve for methods
   *
   * @param methodName  name of the method we search for
   * @param outerIsType true if last result of type check was type,
   *                    false if it was an instance
   * @param abstractTc  true if the tc is not used for object-oriented languages
   * @return the correct methods for the specific case
   */
  public static List<FunctionSymbol> getCorrectMethods(SymTypeExpression typeExpr, String methodName, boolean outerIsType, boolean abstractTc) {
    return getCorrectMethods(typeExpr, methodName, outerIsType, abstractTc, AccessModifier.ALL_INCLUSION);
  }

  protected static List<FunctionSymbol> getCorrectMethods(SymTypeExpression typeExpr, String methodName,
      boolean outerIsType, boolean abstractTc, AccessModifier modifier) {
    if (typeExpr.isObscureType())
      return Collections.emptyList();
    if (!abstractTc) {
      List<FunctionSymbol> functions = typeExpr.getTypeInfo().getSpannedScope()
          .resolveFunctionMany(methodName, modifier).stream()
          .filter(f -> !(f instanceof MethodSymbol))
          .collect(Collectors.toList());
      List<FunctionSymbol> methods = Lists.newArrayList();
      if (typeExpr.getTypeInfo().getSpannedScope() instanceof IOOSymbolsScope) {
        methods.addAll(((IOOSymbolsScope) typeExpr.getTypeInfo()
            .getSpannedScope()).resolveFunctionMany(methodName, modifier)
            .stream().filter(f -> f instanceof MethodSymbol)
            .collect(Collectors.toList()));
      }
      if (outerIsType) {
        List<FunctionSymbol> methodsWithoutStatic =
            methods.stream().filter(Objects::nonNull)
                .map(m -> (MethodSymbol) m)
                .filter(m -> !m.isIsStatic())
                .collect(Collectors.toList());
        methodsWithoutStatic.addAll(functions);
        if (typeExpr.getTypeInfo().getSpannedScope() instanceof IOOSymbolsScope) {
          List<MethodSymbol> localStaticMethods =
              ((IOOSymbolsScope) typeExpr.getTypeInfo().getSpannedScope())
                  .getLocalMethodSymbols().stream()
                  .filter(MethodSymbol::isIsStatic)
                  .collect(Collectors.toList());
          methodsWithoutStatic.addAll(localStaticMethods);
        }
        return methodsWithoutStatic;
      }
      else {
        functions.addAll(methods);
        return functions;
      }
    }
    else {
      return typeExpr.getTypeInfo().getSpannedScope().resolveFunctionMany(methodName, modifier);
    }
  }

  /**
   * transforms the methods by replacing their type variables with
   * the actual type arguments
   *
   * @param methodName name of the method we search for
   * @param functions  methods that need to be transformed
   * @return transformed methods
   */
  protected static List<FunctionSymbol> transformMethodList(
      SymTypeExpression typeExpr,
      String methodName,
      List<FunctionSymbol> functions,
      List<FunctionSymbol> functionList) {
    List<FunctionSymbol> matchingMethods = new ArrayList<>();
    for (FunctionSymbol method : functions) {
      List<VariableSymbol> fieldSymbols = new ArrayList<>();
      for (VariableSymbol parameter : method.getParameterList()) {
        fieldSymbols.add(parameter.deepClone());
      }
      FunctionSymbol copiedMethodSymbol = method.deepClone();
      IOOSymbolsScope scope = OOSymbolsMill.scope();
      for (VariableSymbol parameter : fieldSymbols) {
        scope.add(parameter);
      }
      for (TypeVarSymbol typeVar : method.getTypeVariableList()) {
        scope.add(typeVar);
      }
      copiedMethodSymbol.setSpannedScope(scope);
      functionList.add(copiedMethodSymbol);
    }
    // Filter methods
    for (FunctionSymbol method : functionList) {
      if (method.getName().equals(methodName)) {
        matchingMethods.add(method.deepClone());
      }
    }
    if (typeExpr.isGenericType()) {
      // Compare type arguments of SymTypeExpression (actual type)
      // and its TypeVarSymbol (type definition)
      List<SymTypeExpression> arguments =
          ((SymTypeOfGenerics) typeExpr.deepClone()).getArgumentList();
      List<TypeVarSymbol> typeVariableArguments =
          typeExpr.getTypeInfo().getTypeParameterList();
      Map<TypeVarSymbol, SymTypeExpression> map = new HashMap<>();
      if (arguments.size() != typeVariableArguments.size()) {
        Log.error("0xA1300 Different number of type arguments in TypeSymbol and SymTypeExpression");
      }
      for (int i = 0; i < typeVariableArguments.size(); i++) {
        // Put the type arguments in a map TypeVarSymbol -> SymTypeExpression
        map.put(typeVariableArguments.get(i), arguments.get(i));
      }
      // For every method in methodList: replace type variables in
      // parameters or return type with its actual symtype expression
      for (FunctionSymbol method : matchingMethods) {
        // Return type
        method.replaceTypeVariables(map);
        // Type parameters
        for (VariableSymbol parameter : method.getParameterList()) {
          parameter.replaceTypeVariables(map);
        }
      }
      // Note: the following is not correct (not fixed as deprecated):
      // if there are two methods with the same parameters and return
      // type remove the second method from the list because it is a method from a super type and is
      // overridden by the first method
      for (int i = 0; i < matchingMethods.size() - 1; i++) {
        for (int j = i + 1; j < matchingMethods.size(); j++) {
          if (matchingMethods.get(i).getType().print()
              .equals(matchingMethods.get(j).getType().print()) &&
              matchingMethods.get(i).getParameterList().size()
                  == matchingMethods.get(j).getParameterList().size()) {
            boolean equal = true;
            for (int k = 0; k < matchingMethods.get(i).getParameterList().size(); k++) {
              if (!matchingMethods.get(i).getParameterList().get(k).getType().print().equals(
                  matchingMethods.get(j).getParameterList().get(k).getType().print())) {
                equal = false;
              }
            }
            if (equal) {
              matchingMethods.remove(matchingMethods.get(j));
            }
            else {
              Log.error("0xA2298 The types of the return type and the parameters of the methods have to be the same");
            }
          }
        }
      }
    }
    return matchingMethods;
  }

  public static List<VariableSymbol> getFieldList(SymTypeExpression typeExpr, String fieldName, boolean abstractTc) {
    return getFieldList(typeExpr, fieldName, abstractTc, AccessModifier.ALL_INCLUSION);
  }

  /**
   * returns the list of fields the SymTypeExpression can access
   * and filters these for a field with specific name
   */
  public static List<VariableSymbol> getFieldList(SymTypeExpression typeExpr, String fieldName, boolean abstractTc, AccessModifier modifier) {
    if (typeExpr.isObscureType())
      return Collections.emptyList();
    // Get fields from the type symbol
    List<VariableSymbol> fields = getCorrectFields(typeExpr, fieldName, false, abstractTc, modifier);
    return transformFieldList(typeExpr, fieldName, fields);
  }

  public static List<VariableSymbol> getFieldList(SymTypeExpression typeExpr, String fieldName, boolean outerIsType, boolean abstractTc) {
    return getFieldList(typeExpr, fieldName, outerIsType, abstractTc, AccessModifier.ALL_INCLUSION);
  }

  /**
   * returns the correct fields in both cases:
   * 1) the last result was a type,
   * 2) the last result was an instance
   *
   * @param fieldName   name of the field we search for
   * @param outerIsType true if the last result was a type,
   *                    false if it was an instance
   * @return the correct fields for the specific case
   */
  public static List<VariableSymbol> getFieldList(SymTypeExpression typeExpr, String fieldName, boolean outerIsType, boolean abstractTc, AccessModifier modifier) {
    if (typeExpr.isObscureType())
      return Collections.emptyList();
    List<VariableSymbol> fields = getCorrectFields(typeExpr, fieldName, outerIsType, abstractTc, modifier);
    return transformFieldList(typeExpr, fieldName, fields);
  }

  public static List<VariableSymbol> getCorrectFields(SymTypeExpression typeExpr, String fieldName, boolean outerIsType, boolean abstractTc) {
    return getCorrectFields(typeExpr, fieldName, outerIsType, abstractTc, AccessModifier.ALL_INCLUSION);
  }

  /**
   * return the correct fields for the two situations:
   * 1) the last calculated type in the type check was a type,
   * then filter for non-static fields and
   * add the static fields of this type
   * 2) the last calculated type in the type check was an instance,
   * then just resolve for fields
   *
   * @param fieldName   name of the field we search for
   * @param outerIsType true if last result of type check was type,
   *                    false if it was an instance
   * @return the correct fields for the specific case
   */
  protected static List<VariableSymbol> getCorrectFields(SymTypeExpression typeExpr, String fieldName,
      boolean outerIsType, boolean abstractTc, AccessModifier modifier) {
    if (typeExpr.isObscureType())
      return Collections.emptyList();
    if (!abstractTc) {
      List<VariableSymbol> variables = typeExpr.getTypeInfo().getSpannedScope()
          .resolveVariableMany(fieldName, modifier).stream()
          .filter(v -> !(v instanceof FieldSymbol))
          .collect(Collectors.toList());
      List<VariableSymbol> fields = Lists.newArrayList();
      if (typeExpr.getTypeInfo().getSpannedScope() instanceof IOOSymbolsScope) {
        fields.addAll((typeExpr.getTypeInfo().getSpannedScope())
            .resolveVariableMany(fieldName, modifier).stream()
            .filter(v -> v instanceof FieldSymbol)
            .collect(Collectors.toList()));
      }
      if (outerIsType) {
        List<VariableSymbol> fieldsWithoutStatic =
            fields.stream().map(f -> (FieldSymbol) f)
                .filter(f -> !f.isIsStatic())
                .collect(Collectors.toList());
        fieldsWithoutStatic.addAll(variables);
        if (typeExpr.getTypeInfo().getSpannedScope() instanceof IOOSymbolsScope) {
          List<FieldSymbol> localStaticFields =
              ((IOOSymbolsScope) typeExpr.getTypeInfo().getSpannedScope())
                  .getLocalFieldSymbols().stream()
                  .filter(FieldSymbol::isIsStatic)
                  .collect(Collectors.toList());
          fieldsWithoutStatic.addAll(localStaticFields);
        }
        return fieldsWithoutStatic;
      }
      else {
        variables.addAll(fields);
        return variables;
      }
    }
    else {
      return typeExpr.getTypeInfo().getSpannedScope().resolveVariableMany(fieldName, modifier);
    }
  }

  /**
   * transforms the fields by replacing their type variables with
   * the actual type arguments
   *
   * @param fieldName name of the field we search for
   * @param fields    fields that need to be transformed
   * @return transformed fields
   */
  protected static List<VariableSymbol> transformFieldList(SymTypeExpression typeExpr, String fieldName,
      List<VariableSymbol> fields) {
    List<VariableSymbol> fieldList = new ArrayList<>();
    // Filter fields
    for (VariableSymbol field : fields) {
      if (field.getName().equals(fieldName)) {
        fieldList.add(field.deepClone());
      }
    }
    if (!typeExpr.isGenericType()) {
      return fieldList;
    }
    else {
      // Compare type arguments of SymTypeExpression (actual type)
      // and its TypeSymbol (type definition)
      List<SymTypeExpression> arguments =
          ((SymTypeOfGenerics) typeExpr.deepClone()).getArgumentList();
      List<TypeVarSymbol> typeVariableArguments =
          typeExpr.getTypeInfo().getTypeParameterList();
      Map<TypeVarSymbol, SymTypeExpression> map = new HashMap<>();
      if (arguments.size() != typeVariableArguments.size()) {
        Log.error("0xA1301 Different number of type arguments in TypeSymbol and SymTypeExpression");
      }
      for (int i = 0; i < typeVariableArguments.size(); i++) {
        // Put the type arguments in a map TypeVarSymbol -> SymTypeExpression
        map.put(typeVariableArguments.get(i), arguments.get(i));
      }
      // For every field in fieldList: replace type variables in
      // type with its actual symtype expression
      for (VariableSymbol field : fieldList) {
        field.replaceTypeVariables(map);
      }
    }
    // If there are two fields with the same type, remove the second field
    // in the list because it is a field from a super type and is overridden by the first field
    for (int i = 0; i < fieldList.size() - 1; i++) {
      for (int j = i + 1; j < fieldList.size(); j++) {
        if (fieldList.get(i).getType().print().equals(fieldList.get(j).getType().print())) {
          fieldList.remove(fieldList.get(j));
        }
        else {
          Log.error("0xA2299 The types of the fields have to be same");
        }
      }
    }
    return fieldList;
  }

  public static void replaceTypeVariables(SymTypeExpression typeExpr,
      Map<TypeVarSymbol, SymTypeExpression> replaceMap) {
    if (typeExpr.isGenericType()) {
      SymTypeOfGenerics genType = typeExpr.asGenericType();
      for (int i = 0; i < genType.getArgumentList().size(); i++) {
        SymTypeExpression type = genType.getArgument(i);
        TypeSymbol realTypeInfo;
        TypeSymbol typeInfo = type.getTypeInfo();
        if (typeInfo instanceof TypeSymbolSurrogate) {
          realTypeInfo = ((TypeSymbolSurrogate) type.getTypeInfo()).lazyLoadDelegate();
        }
        else {
          realTypeInfo = typeInfo;
        }
        if (type.isTypeVariable() && realTypeInfo instanceof TypeVarSymbol) {
          Optional<TypeVarSymbol> typeVar = replaceMap.keySet().stream().filter(t -> t.getName().equals(realTypeInfo.getName())).findAny();
          if (typeVar.isPresent()) {
            List<SymTypeExpression> args = new ArrayList<>(genType.getArgumentList());
            args.remove(type);
            args.add(i, replaceMap.get(typeVar.get()));
            genType.setArgumentList(args);
          }
        }
        else {
          type.replaceTypeVariables(replaceMap);
        }
      }
    }
  }

}
