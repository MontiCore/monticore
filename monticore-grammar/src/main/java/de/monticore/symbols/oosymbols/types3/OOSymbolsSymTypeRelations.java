// (c) https://github.com/MontiCore/monticore
package de.monticore.symbols.oosymbols.types3;

import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.symboltable.ISymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfFunction;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * relations for SymTypes of wrt. OOSymbols,
 * e.g, isEnumConstant, etc.
 * Mostly used as convenience instead of the required, e.g.,
 * <ol>
 *   <li> checking if a symbol exists</li>
 *   <li> checking if the symbol is a OOSymbol</li>
 *   <li> typeDispatch the symbol to the OOSymbols variant</li>
 *   <li> actually checking a value</li>
 * </ol>
 * Note: this does not contain AccessModifier or similar,
 * as those are related to the resolver and not the SymTypeExpressions.
 */
public class OOSymbolsSymTypeRelations {

  protected static OOSymbolsSymTypeRelations delegate;

  // methods

  public static boolean isClass(SymTypeExpression type) {
    return getDelegate()._isClass(type);
  }

  protected boolean _isClass(SymTypeExpression type) {
    return getOOTypeSymbolIfAvailable(type)
        .map(OOTypeSymbol::isIsClass)
        .orElse(false);
  }

  public static boolean isInterface(SymTypeExpression type) {
    return getDelegate()._isInterface(type);
  }

  protected boolean _isInterface(SymTypeExpression type) {
    return getOOTypeSymbolIfAvailable(type)
        .map(OOTypeSymbol::isIsInterface)
        .orElse(false);
  }

  public static boolean isEnum(SymTypeExpression type) {
    return getDelegate()._isEnum(type);
  }

  protected boolean _isEnum(SymTypeExpression type) {
    return getOOTypeSymbolIfAvailable(type)
        .map(OOTypeSymbol::isIsEnum)
        .orElse(false);
  }

  /**
   * whether the source of the type is an enum constant.
   * s.a. {@link de.monticore.types.check.SymTypeSourceInfo#getSourceSymbol()}
   */
  public static boolean sourceIsEnumConstant(SymTypeExpression type) {
    return getDelegate()._sourceIsEnumConstant(type);
  }

  protected boolean _sourceIsEnumConstant(SymTypeExpression type) {
    return getSourceFieldSymbolIfAvailable(type)
        .map(FieldSymbol::isIsEnumConstant)
        .orElse(false);
  }

  public static boolean isMethod(SymTypeExpression type) {
    return getDelegate()._isMethod(type);
  }

  protected boolean _isMethod(SymTypeExpression type) {
    return getMethodSymbolIfAvailable(type)
        .map(MethodSymbol::isIsMethod)
        .orElse(false);
  }

  public static boolean isConstructor(SymTypeExpression type) {
    return getDelegate()._isConstructor(type);
  }

  protected boolean _isConstructor(SymTypeExpression type) {
    return getMethodSymbolIfAvailable(type)
        .map(MethodSymbol::isIsConstructor)
        .orElse(false);
  }

  // Helper

  protected Optional<OOTypeSymbol> getOOTypeSymbolIfAvailable(
      SymTypeExpression type
  ) {
    Optional<OOTypeSymbol> res;
    if (type.hasTypeInfo()) {
      TypeSymbol typeSymbol = type.getTypeInfo();
      if (OOSymbolsMill.typeDispatcher().isOOSymbolsOOType(typeSymbol)) {
        res = Optional.of(
            OOSymbolsMill.typeDispatcher().asOOSymbolsOOType(typeSymbol)
        );
      }
      else {
        res = Optional.empty();
      }
    }
    else {
      res = Optional.empty();
    }
    return res;
  }

  protected Optional<MethodSymbol> getMethodSymbolIfAvailable(
      SymTypeExpression type
  ) {
    Optional<MethodSymbol> res;
    if (type.isFunctionType()) {
      SymTypeOfFunction func = type.asFunctionType();
      if (func.hasSymbol()) {
        FunctionSymbol funcSym = func.getSymbol();
        if (OOSymbolsMill.typeDispatcher().isOOSymbolsMethod(funcSym)) {
          res = Optional.of(
              OOSymbolsMill.typeDispatcher().asOOSymbolsMethod(funcSym)
          );
        }
        else {
          res = Optional.empty();
        }
      }
      else {
        res = Optional.empty();
      }
    }
    else {
      res = Optional.empty();
    }
    return res;
  }

  protected Optional<FieldSymbol> getSourceFieldSymbolIfAvailable(
      SymTypeExpression type
  ) {
    Optional<FieldSymbol> res;
    if (type.getSourceInfo().getSourceSymbol().isPresent()) {
      ISymbol sourceSymbol = type.getSourceInfo().getSourceSymbol().get();
      if (OOSymbolsMill.typeDispatcher().isOOSymbolsField(sourceSymbol)) {
        res = Optional.of(
            OOSymbolsMill.typeDispatcher().asOOSymbolsField(sourceSymbol)
        );
      }
      else {
        res = Optional.empty();
      }
    }
    else {
      res = Optional.empty();
    }
    return res;
  }

  // static delegate

  public static void init() {
    Log.trace("init default OOSymbolsSymTypeRelations", "TypeCheck setup");
    setDelegate(new OOSymbolsSymTypeRelations());
  }

  public static void reset() {
    OOSymbolsSymTypeRelations.delegate = null;
  }

  protected static void setDelegate(OOSymbolsSymTypeRelations newDelegate) {
    OOSymbolsSymTypeRelations.delegate = Log.errorIfNull(newDelegate);
  }

  protected static OOSymbolsSymTypeRelations getDelegate() {
    if (OOSymbolsSymTypeRelations.delegate == null) {
      init();
    }
    return OOSymbolsSymTypeRelations.delegate;
  }

}
