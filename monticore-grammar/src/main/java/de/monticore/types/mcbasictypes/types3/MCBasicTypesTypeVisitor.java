/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcbasictypes.types3;

import de.monticore.expressions.expressionsbasis.types3.util.NameExpressionTypeCalculator;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypePrimitive;
import de.monticore.types.mcbasictypes._ast.ASTMCPrimitiveType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._visitor.MCBasicTypesVisitor2;
import de.monticore.types3.AbstractTypeVisitor;
import de.monticore.types3.util.TypeContextCalculator;
import de.monticore.types3.util.WithinTypeBasicSymbolsResolver;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;
import java.util.stream.Collectors;

public class MCBasicTypesTypeVisitor extends AbstractTypeVisitor
    implements MCBasicTypesVisitor2 {

  protected NameExpressionTypeCalculator nameExprTypeCalc;

  protected WithinTypeBasicSymbolsResolver withinTypeResolver;

  protected TypeContextCalculator typeCtxCalc;

  public MCBasicTypesTypeVisitor() {
    // defaultValues
    nameExprTypeCalc = new NameExpressionTypeCalculator();
    withinTypeResolver = new WithinTypeBasicSymbolsResolver();
    typeCtxCalc = new TypeContextCalculator();
  }

  protected NameExpressionTypeCalculator getNameExprTypeCalc() {
    return nameExprTypeCalc;
  }

  protected WithinTypeBasicSymbolsResolver getWithinTypeResolver() {
    return withinTypeResolver;
  }

  protected TypeContextCalculator getTypeCtxCalc() {
    return typeCtxCalc;
  }

  @Override
  public void endVisit(ASTMCPrimitiveType primitiveType) {
    String primName = primitiveType.printType();
    Optional<TypeSymbol> prim =
        getAsBasicSymbolsScope(primitiveType.getEnclosingScope())
            .resolveType(primName);
    if (prim.isPresent()) {
      SymTypePrimitive typeConstant =
          SymTypeExpressionFactory.createPrimitive(prim.get());
      getType4Ast().setTypeOfTypeIdentifier(primitiveType, typeConstant);
      primitiveType.setDefiningSymbol(typeConstant.getTypeInfo());
    }
    else {
      getType4Ast().setTypeOfTypeIdentifier(primitiveType,
          SymTypeExpressionFactory.createObscureType());
      Log.error("0xA0111 Cannot find symbol " + primName,
          primitiveType.get_SourcePositionStart(),
          primitiveType.get_SourcePositionEnd()
      );
    }
  }

  // Note: ASTMCVoidType does not get its type stored in type4ast,
  //       as the type is known to be void.
  //       However, it is stored for the ASTMCReturnType

  @Override
  public void endVisit(ASTMCReturnType rType) {
    if (rType.isPresentMCType()) {
      getType4Ast().setTypeOfTypeIdentifier(
          rType,
          getType4Ast().getTypeOfTypeIdentifier(rType.getMCType())
      );
    }
    else if (rType.isPresentMCVoidType()) {
      getType4Ast().setTypeOfTypeIdentifier(
          rType,
          SymTypeExpressionFactory.createTypeVoid()
      );
    }
  }

  @Override
  public void endVisit(ASTMCQualifiedName qName) {
    IBasicSymbolsScope enclosingScope =
        getAsBasicSymbolsScope(qName.getEnclosingScope());
    // Note: As the qualified name is a List of Names,
    // There is no ASTNode representing each prefix with a type,
    // e.g., Assume a.b.c with a being a qualifier, a.b being a type
    // and c being an inner type in a.b
    // then there is no AST node representing a.b.
    // As types are usually stored in Type4AST,
    // this cannot be done for a.b in this case.
    // The result from a.b will be discarded after this method

    // find the type with the smallest prefix,
    // e.g., for a.b.c.d it is a.b, if a.b and a.b.c are types,
    // with a and a.b being their qualifiers respectively.
    // Afterwards, use the prefix to search for inner types
    int numberOfPartsUsedForFirstType = 0;
    Optional<SymTypeExpression> type = Optional.empty();
    do {
      numberOfPartsUsedForFirstType++;
      if (type.isEmpty()) {
        String prefix = qName.getPartsList().stream()
            .limit(numberOfPartsUsedForFirstType)
            .collect(Collectors.joining("."));
        type = getNameExprTypeCalc()
            .typeOfNameAsTypeId(enclosingScope, prefix);
      }
      else {
        SymTypeExpression prefixType = type.get();
        String name = qName.getParts(numberOfPartsUsedForFirstType - 1);
        if (prefixType.isObjectType() || prefixType.isGenericType()) {
          type = getWithinTypeResolver().resolveType(
              prefixType,
              name,
              getTypeCtxCalc().getAccessModifier(
                  prefixType.getTypeInfo(),
                  enclosingScope,
                  true
              ),
              t -> true
          );
          if (type.isEmpty()) {
            Log.error("0xFDAE3 unable to find type "
                    + name
                    + " within type "
                    + prefixType.printFullName(),
                qName.get_SourcePositionStart(),
                qName.get_SourcePositionEnd()
            );
          }
        }
        else {
          Log.error("0xFDA3E unexpected access \"."
                  + name
                  + "\" for type "
                  + prefixType.printFullName(),
              qName.get_SourcePositionStart(),
              qName.get_SourcePositionEnd()
          );
        }
      }

    } while (numberOfPartsUsedForFirstType < qName.sizeParts());

    if (type.isEmpty()) {
      Log.error("0xA0324 Cannot find symbol " + qName.getQName(),
          qName.get_SourcePositionStart(),
          qName.get_SourcePositionEnd()
      );
      type = Optional.of(SymTypeExpressionFactory.createObscureType());
    }

    getType4Ast().setTypeOfTypeIdentifier(qName, type.get());
  }

  @Override
  public void endVisit(ASTMCQualifiedType mcQType) {
    getType4Ast().setTypeOfTypeIdentifier(
        mcQType,
        getType4Ast().getPartialTypeOfTypeId(mcQType.getMCQualifiedName())
    );
  }
}
