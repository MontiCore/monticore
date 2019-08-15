/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable;

import de.monticore.cd.CD4AnalysisHelper;
import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDType;
import de.monticore.cd.cd4analysis._ast.ASTModifier;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbolReference;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.symboltable.SymbolTableGeneratorHelper;
import de.monticore.types.MCSimpleGenericTypesHelper;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.Names;

import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PREFIX;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.utils.Names.getSimpleName;
import static de.se_rwth.commons.Names.getQualifier;

public class SymbolTableService extends AbstractService<SymbolTableService> {

  public SymbolTableService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  public SymbolTableService(CDDefinitionSymbol cdSymbol) {
    super(cdSymbol);
  }

  @Override
  public String getSubPackage() {
    return SYMBOL_TABLE_PACKGE;
  }

  @Override
  protected SymbolTableService createService(CDDefinitionSymbol cdSymbol) {
    return createSymbolTableService(cdSymbol);
  }

  public static SymbolTableService createSymbolTableService(CDDefinitionSymbol cdSymbol) {
    return new SymbolTableService(cdSymbol);
  }

  public String getScopeTypeName() {
    return getScopeTypeName(getCDSymbol());
  }

  public String getScopeTypeName(CDDefinitionSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + cdSymbol.getName() + SymbolTableConstants.SCOPE_SUFFIX;
  }

  public String getScopeInterfaceTypeName() {
    return getScopeInterfaceTypeName(getCDSymbol());
  }

  public String getArtifactScopeTypeName(CDDefinitionSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + cdSymbol.getName() + ARTIFACT_PREFIX + SymbolTableConstants.SCOPE_SUFFIX;
  }

  public String getArtifactScopeTypeName() {
    return getArtifactScopeTypeName(getCDSymbol());
  }

  public ASTMCQualifiedType getArtifactScopeType() {
    return getCDTypeFactory().createQualifiedType(getArtifactScopeTypeName(getCDSymbol()));
  }

  public String getScopeInterfaceTypeName(CDDefinitionSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + INTERFACE_PREFIX + cdSymbol.getName() + SymbolTableConstants.SCOPE_SUFFIX;
  }

  public ASTMCType getScopeType() {
    return getCDTypeFactory().createQualifiedType(getScopeTypeName());
  }

  public ASTMCType getScopeInterfaceType() {
    return getCDTypeFactory().createQualifiedType(getScopeInterfaceTypeName());
  }

  public ASTMCType getScopeInterfaceType(CDDefinitionSymbol cdSymbol) {
    return getCDTypeFactory().createQualifiedType(getScopeInterfaceTypeName(cdSymbol));
  }

  public String getSymbolName(ASTCDType clazz) {
    // normal symbol name calculation from
    if (clazz.getName().startsWith(AST_PREFIX)) {
      return clazz.getName().substring(AST_PREFIX.length()) + SymbolTableConstants.SYMBOL_SUFFIX;
    } else {
      return clazz.getName() + SymbolTableConstants.SYMBOL_SUFFIX;
    }
  }

  public String getSymbolTypeName(ASTCDType clazz) {
    //if in grammar other symbol Name is defined e.g. 'symbol (MCType) MCQualifiedType implements MCObjectType = MCQualifiedName;'
    if (clazz.getModifierOpt().isPresent()) {
      Optional<String> symbolTypeValue = getSymbolTypeValue(clazz.getModifierOpt().get());
      if (symbolTypeValue.isPresent()) {
        return symbolTypeValue.get();
      }
    }
    return getPackage() + "." + getSymbolName(clazz);
  }

  public ASTMCType getSymbolType(ASTCDType clazz) {
    return getCDTypeFactory().createQualifiedType(getSymbolTypeName(clazz));
  }

  public String getReferencedSymbolTypeName(ASTCDAttribute attribute) {
    String referencedSymbol = CD4AnalysisHelper.getStereotypeValues(attribute,
        MC2CDStereotypes.REFERENCED_SYMBOL.toString()).get(0);

    if (!getQualifier(referencedSymbol).isEmpty() && !referencedSymbol.contains(SYMBOL_TABLE_PACKGE)) {
      referencedSymbol = SymbolTableGeneratorHelper
          .getQualifiedSymbolType(getQualifier(referencedSymbol)
              .toLowerCase(), Names.getSimpleName(referencedSymbol));
    }
    return referencedSymbol;
  }

  public String getSimpleSymbolNameFromOptional(ASTMCType type) {
    ASTMCType referencedSymbolType = MCSimpleGenericTypesHelper.getReferenceTypeFromOptional(type).getMCTypeOpt().get();
    String referencedSymbol = referencedSymbolType.printType();
    return getSimpleName(referencedSymbol).substring(0, getSimpleName(referencedSymbol).indexOf(SymbolTableConstants.SYMBOL_SUFFIX));
  }

  public String getSimpleSymbolName(String referencedSymbol) {
    return getSimpleName(referencedSymbol).substring(0, getSimpleName(referencedSymbol).indexOf(SymbolTableConstants.SYMBOL_SUFFIX));
  }

  public boolean isReferencedSymbol(ASTCDAttribute attribute) {
    return attribute.isPresentModifier() && hasStereotype(attribute.getModifier(), MC2CDStereotypes.REFERENCED_SYMBOL);
  }

  public Optional<String> getSymbolTypeValue(ASTModifier modifier) {
    List<String> stereotypeValues = getStereotypeValues(modifier, MC2CDStereotypes.SYMBOL);
    if (!stereotypeValues.isEmpty()) {
      return Optional.ofNullable(stereotypeValues.get(0));
    }
    return Optional.empty();
  }

  public Optional<ASTCDType> getTypeWithSymbolInfo(ASTCDType type) {
    if (type.getModifierOpt().isPresent() && hasSymbolStereotype(type.getModifierOpt().get())) {
      return Optional.of(type);
    }
    if (!type.getCDTypeSymbolOpt().isPresent()) {
      return Optional.empty();
    }
    for (CDTypeSymbolReference superType : type.getCDTypeSymbol().getCdInterfaces()) {
      if (superType.existsReferencedSymbol() && superType.getReferencedSymbol().getAstNode().isPresent()) {
        Optional<ASTCDType> result = getTypeWithSymbolInfo(superType.getReferencedSymbol().getAstNode().get());
        if (result.isPresent()) {
          return result;
        }
      }
    }
    return Optional.empty();
  }

  public Optional<ASTCDType> getTypeWithScopeInfo(ASTCDType type) {
    if (type.getModifierOpt().isPresent() && hasScopeStereotype(type.getModifierOpt().get())) {
      return Optional.of(type);
    }
    if (!type.getCDTypeSymbolOpt().isPresent()) {
      return Optional.empty();
    }
    for (CDTypeSymbolReference superType : type.getCDTypeSymbol().getCdInterfaces()) {
      if (superType.existsReferencedSymbol() && superType.getReferencedSymbol().getAstNode().isPresent()) {
        Optional<ASTCDType> result = getTypeWithScopeInfo(superType.getReferencedSymbol().getAstNode().get());
        if (result.isPresent()) {
          return result;
        }
      }
    }
    return Optional.empty();
  }

}
