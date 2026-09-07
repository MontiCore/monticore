// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.util;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.ISymbol;

import java.util.Optional;

import static de.monticore.types3.util.TypeContextCalculator.getEnclosingType;

/**
 * Checks if a symbol represents a native JavaType.
 * As the current Symbols (7.8.0) don't contain this information,
 * we have to essentially guess.
 */
public class TypeSymbolNativityChecker {

  public static boolean isNativeJavaType(TypeSymbol typeSymbol) {
    Preconditions.checkNotNull(typeSymbol);
    // check against legacy relationship
    Preconditions.checkState(!(typeSymbol instanceof TypeVarSymbol));
    boolean isNative;

    // could be improved:
    // how do we find if a symbol is of native Java
    // or of our own languages?
    // Currently using hacky workaround

    // This does not catch everything
    if (isClass2MCTypeAdapter(typeSymbol)) {
      isNative = true;
    }
    // This should catch everything, but may be too much
    else {
      isNative = getNativeJavaClassIfExists(typeSymbol).isPresent();
    }
    return isNative;
  }

  public static boolean isNativeJavaVariable(VariableSymbol varSymbol) {
    // assumption: we don't add our own symbols to native Java Classes
    // this may not be true in the future
    return isEnclosedByNativeJavaType(varSymbol).isPresent();
  }

  public static boolean isNativeJavaFunction(FunctionSymbol funcSymbol) {
    // assumption: we don't add our own symbols to native Java Classes
    // this may not be true in the future
    return isEnclosedByNativeJavaType(funcSymbol).isPresent();
  }

  public static Class<?> getNativeJavaClass(TypeSymbol typeSymbol) {
    Preconditions.checkState(isNativeJavaType(typeSymbol));
    return getNativeJavaClassIfExists(typeSymbol).get();
  }

  // helper

  protected static Optional<TypeSymbol> isEnclosedByNativeJavaType(
      ISymbol symbol
  ) {
    Optional<TypeSymbol> enclosingTypeSym =
        getEnclosingType(symbol.getEnclosingScope());
    if (enclosingTypeSym.isPresent() &&
        isNativeJavaType(enclosingTypeSym.get())) {
      return enclosingTypeSym;
    }
    else {
      return Optional.empty();
    }
  }

  protected static Optional<Class<?>> getNativeJavaClassIfExists(
      TypeSymbol typeSymbol
  ) {
    try {
      String fqName = typeSymbol.getFullName();
      return Optional.of(Class.forName(fqName));
    }
    catch (ClassNotFoundException e) {
      return Optional.empty();
    }
  }

  // to be removed as soon as we have a decent way to
  // check the nativity of type symbols.
  protected static boolean isClass2MCTypeAdapter(TypeSymbol typeSymbol) {
    try {
      Class<?> typeAdapterClass = Class.forName(
          "de.monticore.class2mc.adapters.JClass2TypeSymbolAdapter"
      );
      Class<?> ooTypeAdapterClass = Class.forName(
          "de.monticore.class2mc.adapters.JClass2OOTypeSymbolAdapter"
      );
      return typeAdapterClass.isInstance(typeSymbol)
          || ooTypeAdapterClass.isInstance(typeSymbol);
    }
    catch (ClassNotFoundException e) {
      return false;
    }
  }

}
