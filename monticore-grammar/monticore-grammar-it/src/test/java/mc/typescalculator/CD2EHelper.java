/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import com.google.common.collect.Lists;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.symbols.oosymbols._symboltable.MethodSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfGenerics;
import mc.testcd4analysis._symboltable.CDFieldSymbol;
import mc.testcd4analysis._symboltable.CDMethOrConstrSymbol;
import mc.testcd4analysis._symboltable.CDTypeSymbol;
import mc.testcd4analysis._symboltable.CDTypeSymbolSurrogate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CD2EHelper {

  private IOOSymbolsScope iOOSymbolsScope;

  private Map<String, SymTypeExpression> symTypeExpressionMap = new HashMap<>();

  private Map<String, OOTypeSymbol> typeSymbolMap = new HashMap<>();

  private Map<String, FieldSymbol> fieldSymbolMap = new HashMap<>();

  private Map<String, MethodSymbol> methodSymbolMap = new HashMap<>();

  public CD2EHelper() {
    this.iOOSymbolsScope = OOSymbolsMill.oOSymbolsScopeBuilder().build();
  }

  public OOTypeSymbol createOOTypeSymbolFormCDTypeSymbol(CDTypeSymbol cdTypeSymbol) {
    if (typeSymbolMap.containsKey(cdTypeSymbol.getName())) {
      return typeSymbolMap.get(cdTypeSymbol.getName());
    } else {
      // add to map
      OOTypeSymbol typeSymbol = OOSymbolsMill.oOTypeSymbolBuilder()
          .setName(cdTypeSymbol.getName())
          .setSpannedScope(OOSymbolsMill.oOSymbolsScopeBuilder().build())
          .build();
      typeSymbolMap.put(cdTypeSymbol.getName(), typeSymbol);

      // super types of type reference
      Optional<SymTypeExpression> superClass = Optional.empty();
      if (cdTypeSymbol.isPresentSuperClass()) {
        superClass = Optional.of(createSymTypeExpressionFormCDTypeSymbolReference(cdTypeSymbol.getSuperClass()));
      }
      List<SymTypeExpression> superInterfaces = cdTypeSymbol.getCdInterfacesList().stream()
          .map(this::createSymTypeExpressionFormCDTypeSymbolReference)
          .collect(Collectors.toList());

      // add field symbols
      List<FieldSymbol> fieldSymbols = cdTypeSymbol.getSpannedScope().getLocalCDFieldSymbols().stream()
          .map(this::createFieldSymbolFormCDFieldSymbol)
          .collect(Collectors.toList());

      // add all methods
      List<MethodSymbol> methodSymbols = cdTypeSymbol.getSpannedScope().getLocalCDMethOrConstrSymbols().stream()
          .map(this::createMethodSymbolFormCDMethOrConstrSymbol)
          .collect(Collectors.toList());


      typeSymbol.setName(cdTypeSymbol.getName());
      typeSymbol.addAllSuperTypes(superInterfaces);
      for (FieldSymbol field : fieldSymbols) {
        typeSymbol.getSpannedScope().add(field);
      }
      fieldSymbols.forEach(f -> typeSymbol.getSpannedScope().add(f));
      for (MethodSymbol method : methodSymbols) {
        typeSymbol.getSpannedScope().add(method);
      }
      methodSymbols.forEach(f -> typeSymbol.getSpannedScope().add(f));
      superClass.ifPresent(typeSymbol::addSuperTypes);
      return typeSymbol;
    }

  }

  public FieldSymbol createFieldSymbolFormCDFieldSymbol(CDFieldSymbol cdFieldSymbol) {
    if (fieldSymbolMap.containsKey(cdFieldSymbol.getName())) {
      return fieldSymbolMap.get(cdFieldSymbol.getName());
    } else {
      // add to map
      FieldSymbol fieldSymbol = OOSymbolsMill.fieldSymbolBuilder()
          .setName(cdFieldSymbol.getName())
          .build();
      fieldSymbolMap.put(cdFieldSymbol.getName(), fieldSymbol);

      // add attribute type
      SymTypeExpression type = createSymTypeExpressionFormCDTypeSymbolReference(cdFieldSymbol.getType());
      fieldSymbol.setIsStatic(cdFieldSymbol.isIsStatic());
      fieldSymbol.setType(type);
      return fieldSymbol;
    }
  }

  public MethodSymbol createMethodSymbolFormCDMethOrConstrSymbol(CDMethOrConstrSymbol cdMethOrConstrSymbol) {
    if (methodSymbolMap.containsKey(cdMethOrConstrSymbol.getName())) {
      return methodSymbolMap.get(cdMethOrConstrSymbol.getName());
    } else {
      // add to map
      MethodSymbol methodSymbol = OOSymbolsMill.methodSymbolBuilder()
          .setName(cdMethOrConstrSymbol.getName())
          .build();
      methodSymbolMap.put(cdMethOrConstrSymbol.getName(), methodSymbol);
      // add return type
      SymTypeExpression returnType = createSymTypeExpressionFormCDTypeSymbolReference(cdMethOrConstrSymbol.getReturnType());
      // add parameters
      List<FieldSymbol> parameters = cdMethOrConstrSymbol.getSpannedScope().getLocalCDFieldSymbols().stream()
          .map(this::createFieldSymbolFormCDFieldSymbol)
          .collect(Collectors.toList());
      methodSymbol.setIsStatic(cdMethOrConstrSymbol.isIsStatic());
      parameters.forEach(symbol -> methodSymbol.getSpannedScope().add(symbol));

      methodSymbol.setReturnType(returnType);
      return methodSymbol;
    }
  }

  public SymTypeExpression createSymTypeExpressionFormCDTypeSymbolReference(CDTypeSymbolSurrogate symbolLoader) {
    if (symTypeExpressionMap.containsKey(symbolLoader.getName())) {
      return symTypeExpressionMap.get(symbolLoader.getName());
    } else {
      SymTypeExpression symTypeExpression;
      try{
        OOTypeSymbol type = createOOTypeSymbolFormCDTypeSymbol(symbolLoader.lazyLoadDelegate());
        iOOSymbolsScope.add(type);
        symTypeExpression = SymTypeExpressionFactory.createTypeExpression(type.getName(), iOOSymbolsScope);
      }catch(Exception e){
        String typeName = symbolLoader.getName();
        OOTypeSymbol typeSymbol = OOSymbolsMill.oOTypeSymbolBuilder()
            .setName(typeName)
            .setSpannedScope(OOSymbolsMill.oOSymbolsScopeBuilder().build())
            .build();
        iOOSymbolsScope.add(typeSymbol);
        symTypeExpression = SymTypeExpressionFactory.createTypeExpression(typeSymbol.getName(), iOOSymbolsScope);
      }
      symTypeExpressionMap.put(symbolLoader.getName(), symTypeExpression);
      return symTypeExpression;
    }
  }

  public SymTypeOfGenerics createSymTypeListFormCDTypeSymbolReference(CDTypeSymbolSurrogate cdTypeSymbolReference) {
    SymTypeExpression symTypeExpression = createSymTypeExpressionFormCDTypeSymbolReference(cdTypeSymbolReference);
    return SymTypeExpressionFactory.createGenerics("List", iOOSymbolsScope, Lists.newArrayList(symTypeExpression));
  }

  public SymTypeOfGenerics createSymTypeOptionalFormCDTypeSymbolReference(CDTypeSymbolSurrogate cdTypeSymbolReference) {
    SymTypeExpression symTypeExpression = createSymTypeExpressionFormCDTypeSymbolReference(cdTypeSymbolReference);
    return SymTypeExpressionFactory.createGenerics("Optional", iOOSymbolsScope, Lists.newArrayList(symTypeExpression));
  }
}
