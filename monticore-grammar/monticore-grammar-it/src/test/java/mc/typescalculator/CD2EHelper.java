/* (c) https://github.com/MontiCore/monticore */
package mc.typescalculator;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._ast.ASTCDAssociation;
import de.monticore.cd.cd4analysis._symboltable.*;
import de.monticore.expressions.expressionsbasis._symboltable.ExpressionsBasisSymTabMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.MethodSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsSymTabMill;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CD2EHelper {

  private Map<String, SymTypeExpression> symTypeExpressionMap = new HashMap<>();

  private Map<String, TypeSymbol> typeSymbolMap = new HashMap<>();

  private Map<String, FieldSymbol> fieldSymbolMap = new HashMap<>();

  private Map<String, MethodSymbol> methodSymbolMap = new HashMap<>();

  public TypeSymbol createTypeSymbolFormCDTypeSymbol(CDTypeSymbol cdTypeSymbol) {
    if (typeSymbolMap.containsKey(cdTypeSymbol.getName())) {
      return typeSymbolMap.get(cdTypeSymbol.getName());
    } else {
      // add to map
      TypeSymbol typeSymbol = TypeSymbolsSymTabMill.typeSymbolBuilder()
          .setName(cdTypeSymbol.getName())
          .build();
      typeSymbolMap.put(cdTypeSymbol.getName(), typeSymbol);
      typeSymbol.setSpannedScope(ExpressionsBasisSymTabMill.expressionsBasisScopeBuilder().build());

      // super types of type reference
      Optional<SymTypeExpression> superClass = Optional.empty();
      if (cdTypeSymbol.isPresentSuperClass()) {
        superClass = Optional.of(createSymTypeExpressionFormCDTypeSymbolReference(cdTypeSymbol.getSuperClass()));
      }
      List<SymTypeExpression> superInterfaces = cdTypeSymbol.getCdInterfaceList().stream()
          .map(this::createSymTypeExpressionFormCDTypeSymbolReference)
          .collect(Collectors.toList());

      // add field symbols
      List<FieldSymbol> fieldSymbols = cdTypeSymbol.getFields().stream()
          .map(this::createFieldSymbolFormCDFieldSymbol)
          .collect(Collectors.toList());

      // add field symbols form associations
      List<FieldSymbol> fieldSymbolsFormAssoc = cdTypeSymbol.getAllAssociations().stream()
          .map(a -> this.createFieldSymbolFormCDAssociationSymbol(a, cdTypeSymbol))
          .filter(Optional::isPresent)
          .map(Optional::get)
          .collect(Collectors.toList());

      // add all methods
      List<MethodSymbol> methodSymbols = cdTypeSymbol.getMethods().stream()
          .map(this::createMethodSymbolFormCDMethOrConstrSymbol)
          .collect(Collectors.toList());


      typeSymbol.setName(cdTypeSymbol.getName());
      typeSymbol.addAllSuperTypes(superInterfaces);
      typeSymbol.addAllFields(fieldSymbols);
      fieldSymbols.forEach(f -> typeSymbol.getSpannedScope().add(f));
      typeSymbol.addAllFields(fieldSymbolsFormAssoc);
      fieldSymbolsFormAssoc.forEach(f -> typeSymbol.getSpannedScope().add(f));
      typeSymbol.addAllMethods(methodSymbols);
      methodSymbols.forEach(f -> typeSymbol.getSpannedScope().add(f));
      superClass.ifPresent(typeSymbol::addSuperType);
      return typeSymbol;
    }

  }

  public FieldSymbol createFieldSymbolFormCDFieldSymbol(CDFieldSymbol cdFieldSymbol) {
    if (fieldSymbolMap.containsKey(cdFieldSymbol.getName())) {
      return fieldSymbolMap.get(cdFieldSymbol.getName());
    } else {
      // add to map
      FieldSymbol fieldSymbol = TypeSymbolsSymTabMill.fieldSymbolBuilder()
          .setName(cdFieldSymbol.getName())
          .build();
      fieldSymbolMap.put(cdFieldSymbol.getName(), fieldSymbol);

      // add attribute type
      SymTypeExpression type = createSymTypeExpressionFormCDTypeSymbolReference(cdFieldSymbol.getType());

      fieldSymbol.setType(type);
      return fieldSymbol;
    }
  }

  public MethodSymbol createMethodSymbolFormCDMethOrConstrSymbol(CDMethOrConstrSymbol cdMethOrConstrSymbol) {
    if (methodSymbolMap.containsKey(cdMethOrConstrSymbol.getName())) {
      return methodSymbolMap.get(cdMethOrConstrSymbol.getName());
    } else {
      // add to map
      MethodSymbol methodSymbol = TypeSymbolsSymTabMill.methodSymbolBuilder()
          .setName(cdMethOrConstrSymbol.getName())
          .build();
      methodSymbolMap.put(cdMethOrConstrSymbol.getName(), methodSymbol);
      // add return type
      SymTypeExpression returnType = createSymTypeExpressionFormCDTypeSymbolReference(cdMethOrConstrSymbol.getReturnType());
      // add parameters
      List<FieldSymbol> parameters = cdMethOrConstrSymbol.getParameters().stream()
          .map(this::createFieldSymbolFormCDFieldSymbol)
          .collect(Collectors.toList());

      methodSymbol.setReturnType(returnType);
      methodSymbol.setParameterList(parameters);
      return methodSymbol;
    }
  }

  public SymTypeExpression createSymTypeExpressionFormCDTypeSymbolReference(CDTypeSymbolLoader symbolLoader) {
    if (symTypeExpressionMap.containsKey(symbolLoader.getName())) {
      return symTypeExpressionMap.get(symbolLoader.getName());
    } else {
      SymTypeExpression symTypeExpression;
      if (symbolLoader.isSymbolLoaded()) {
        // if typeSymbol is already loaded
        TypeSymbol typeSymbol = createTypeSymbolFormCDTypeSymbol(symbolLoader.getLoadedSymbol());
        symTypeExpression = SymTypeExpressionFactory.createTypeExpression(typeSymbol);
      } else {
        // is typeSymbol can be loaded
        Optional<CDTypeSymbol> cdTypeSymbol = symbolLoader.loadSymbol();
        if (cdTypeSymbol.isPresent()) {
          TypeSymbol typeSymbol = createTypeSymbolFormCDTypeSymbol(symbolLoader.getLoadedSymbol());
          symTypeExpression = SymTypeExpressionFactory.createTypeExpression(typeSymbol);
        } else {
          // if typeSymbol could not be loaded
          String typeName = symbolLoader.getName();
          TypeSymbol typeSymbol = TypeSymbolsSymTabMill.typeSymbolBuilder()
              .setName(typeName)
              .build();
          symTypeExpression = SymTypeExpressionFactory.createTypeExpression(typeSymbol);
        }
      }
      symTypeExpressionMap.put(symbolLoader.getName(), symTypeExpression);
      return symTypeExpression;
    }
  }

  public SymTypeOfGenerics createSymTypeListFormCDTypeSymbolReference(CDTypeSymbolLoader cdTypeSymbolReference) {
    SymTypeExpression symTypeExpression = createSymTypeExpressionFormCDTypeSymbolReference(cdTypeSymbolReference);
    return SymTypeExpressionFactory.createGenerics("List", Lists.newArrayList(symTypeExpression));
  }

  public SymTypeOfGenerics createSymTypeOptionalFormCDTypeSymbolReference(CDTypeSymbolLoader cdTypeSymbolReference) {
    SymTypeExpression symTypeExpression = createSymTypeExpressionFormCDTypeSymbolReference(cdTypeSymbolReference);
    return SymTypeExpressionFactory.createGenerics("Optional", Lists.newArrayList(symTypeExpression));
  }

  public Optional<FieldSymbol> createFieldSymbolFormCDAssociationSymbol(CDAssociationSymbol cdAssociationSymbol, CDTypeSymbol cdTypeSymbol) {
    // attribute name
    if (cdAssociationSymbol.isPresentAstNode()) {
      String name = cdAssociationSymbol.getDerivedName();
      ASTCDAssociation astAssoc = cdAssociationSymbol.getAstNode();
      if (isSourceNode(astAssoc, cdTypeSymbol) && !astAssoc.isRightToLeft()) {
        SymTypeExpression type = createSymTypeExpressionFormCDAssociationSymbolReferenceSource(cdAssociationSymbol);
        return Optional.ofNullable(TypeSymbolsSymTabMill.fieldSymbolBuilder()
            .setName(name)
            .setType(type)
            .build());
      } else if (isTargetNode(astAssoc, cdTypeSymbol) && !astAssoc.isLeftToRight()) {
        SymTypeExpression type = createSymTypeExpressionFormCDAssociationSymbolReferenceTarget(cdAssociationSymbol);
        return Optional.ofNullable(TypeSymbolsSymTabMill.fieldSymbolBuilder()
            .setName(name)
            .setType(type)
            .build());
      }
    }
    return Optional.empty();
  }

  private boolean isSourceNode(ASTCDAssociation association, CDTypeSymbol cdTypeSymbol) {
    return association.getLeftReferenceName().getBaseName().equals(cdTypeSymbol.getName());
  }

  private boolean isTargetNode(ASTCDAssociation association, CDTypeSymbol cdTypeSymbol) {
    return association.getRightReferenceName().getBaseName().equals(cdTypeSymbol.getName());
  }

  private SymTypeExpression createSymTypeExpressionFormCDAssociationSymbolReferenceSource(CDAssociationSymbol cdAssociationSymbol) {
    if (cdAssociationSymbol.isPresentAstNode() && cdAssociationSymbol.getAstNode().isPresentRightCardinality()) {
      ASTCDAssociation association = cdAssociationSymbol.getAstNode();
      if (association.getRightCardinality().isMany() || association.getRightCardinality().isOneToMany()) {
        return createSymTypeListFormCDTypeSymbolReference(cdAssociationSymbol.getTargetType());
      } else if (association.getRightCardinality().isOptional()) {
        return createSymTypeOptionalFormCDTypeSymbolReference(cdAssociationSymbol.getTargetType());
      }
    }
    return createSymTypeExpressionFormCDTypeSymbolReference(cdAssociationSymbol.getTargetType());
  }

  private SymTypeExpression createSymTypeExpressionFormCDAssociationSymbolReferenceTarget(CDAssociationSymbol cdAssociationSymbol) {
    if (cdAssociationSymbol.isPresentAstNode() && cdAssociationSymbol.getAstNode().isPresentLeftCardinality()) {
      ASTCDAssociation association = cdAssociationSymbol.getAstNode();
      if (association.getLeftCardinality().isMany() || association.getLeftCardinality().isOneToMany()) {
        return createSymTypeListFormCDTypeSymbolReference(cdAssociationSymbol.getTargetType());
      } else if (association.getLeftCardinality().isOptional()) {
        return createSymTypeOptionalFormCDTypeSymbolReference(cdAssociationSymbol.getTargetType());
      }
    }
    return createSymTypeExpressionFormCDTypeSymbolReference(cdAssociationSymbol.getTargetType());
  }
}
