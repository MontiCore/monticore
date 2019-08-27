/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable;

import de.monticore.cd.CD4AnalysisHelper;
import de.monticore.cd.cd4analysis._ast.*;
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
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PREFIX;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILDER_SUFFIX;
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
    return SYMBOL_TABLE_PACKAGE;
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
    return getPackage(cdSymbol) + "." + cdSymbol.getName() + SCOPE_SUFFIX;
  }

  public String getScopeInterfaceTypeName() {
    return getScopeInterfaceTypeName(getCDSymbol());
  }

  public String getArtifactScopeTypeName(CDDefinitionSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + cdSymbol.getName() + ARTIFACT_PREFIX + SCOPE_SUFFIX;
  }

  public String getArtifactScopeTypeName() {
    return getArtifactScopeTypeName(getCDSymbol());
  }

  public ASTMCQualifiedType getArtifactScopeType() {
    return getCDTypeFactory().createQualifiedType(getArtifactScopeTypeName(getCDSymbol()));
  }

  public String getGlobalScopeTypeName(CDDefinitionSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + cdSymbol.getName() + GLOBAL_PREFIX + SCOPE_SUFFIX;
  }

  public String getGlobalScopeTypeName() {
    return getGlobalScopeTypeName(getCDSymbol());
  }

  public ASTMCQualifiedType getGlobalScopeType() {
    return getCDTypeFactory().createQualifiedType(getGlobalScopeTypeName(getCDSymbol()));
  }

  public String getGlobalScopeInterfaceTypeName(CDDefinitionSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getGlobalScopeInterfaceSimpleName(cdSymbol);
  }

  public String getGlobalScopeInterfaceTypeName() {
    return getGlobalScopeInterfaceTypeName(getCDSymbol());
  }

  public String getGlobalScopeInterfaceSimpleName(CDDefinitionSymbol cdSymbol) {
    return INTERFACE_PREFIX + cdSymbol.getName() + GLOBAL_PREFIX + SCOPE_SUFFIX;
  }

  public String getGlobalScopeInterfaceSimpleName() {
    return getGlobalScopeInterfaceSimpleName(getCDSymbol());
  }

  public ASTMCQualifiedType getGlobalcopeInterfaceType() {
    return getCDTypeFactory().createQualifiedType(getGlobalScopeInterfaceTypeName(getCDSymbol()));
  }

  public String getCommonSymbolInterfaceName(CDDefinitionSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + INTERFACE_PREFIX + COMMON_PREFIX + cdSymbol.getName() + SYMBOL_SUFFIX;
  }

  public String getCommonSymbolInterfaceName() {
    return getCommonSymbolInterfaceName(getCDSymbol());
  }


  public String getScopeInterfaceTypeName(CDDefinitionSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + INTERFACE_PREFIX + cdSymbol.getName() + SCOPE_SUFFIX;
  }

  public ASTMCType getScopeType() {
    return getCDTypeFactory().createQualifiedType(getScopeTypeName());
  }

  public ASTMCQualifiedType getScopeInterfaceType() {
    return getCDTypeFactory().createQualifiedType(getScopeInterfaceTypeName());
  }

  public ASTMCQualifiedType getScopeInterfaceType(CDDefinitionSymbol cdSymbol) {
    return getCDTypeFactory().createQualifiedType(getScopeInterfaceTypeName(cdSymbol));
  }

  public String getSymbolName(ASTCDType clazz) {
    // normal symbol name calculation from
    return removeASTPrefix(clazz) + SYMBOL_SUFFIX;
  }

  public String removeASTPrefix(ASTCDType clazz) {
    // normal symbol name calculation from
    if (clazz.getName().startsWith(AST_PREFIX)) {
      return clazz.getName().substring(AST_PREFIX.length());
    } else {
      return clazz.getName();
    }
  }


  public String getSymbolFullTypeName(ASTCDType clazz) {
    //if in grammar other symbol Name is defined e.g. 'symbol (MCType) MCQualifiedType implements MCObjectType = MCQualifiedName;'
    return getSymbolFullTypeName(clazz, getCDSymbol());
  }

  public String getSymbolFullTypeName(ASTCDType clazz, CDDefinitionSymbol cdDefinitionSymbol) {
    //if in grammar other symbol Name is defined e.g. 'symbol (MCType) MCQualifiedType implements MCObjectType = MCQualifiedName;'
    if (clazz.getModifierOpt().isPresent()) {
      Optional<String> symbolTypeValue = getSymbolTypeValue(clazz.getModifierOpt().get());
      if (symbolTypeValue.isPresent()) {
        return symbolTypeValue.get();
      }
    }
    return getPackage(cdDefinitionSymbol) + "." + getSymbolName(clazz);
  }

  public String getSymbolSimpleTypeName(ASTCDType clazz) {
    //if in grammar other symbol Name is defined e.g. 'symbol (MCType) MCQualifiedType implements MCObjectType = MCQualifiedName;'
    if (clazz.getModifierOpt().isPresent()) {
      Optional<String> symbolTypeValue = getSymbolTypeValue(clazz.getModifierOpt().get());
      if (symbolTypeValue.isPresent()) {
        return symbolTypeValue.get();
      }
    }
    return getSymbolName(clazz);
  }

  public Optional<String> getDefiningSymbolTypeName(ASTCDType clazz) {
    // does only return symbol defining parts not parts with e.g. symbol (MCType)
    return getDefiningSymbolTypeName(clazz, getCDSymbol());
  }

  public Optional<String> getDefiningSymbolTypeName(ASTCDType clazz, CDDefinitionSymbol cdDefinitionSymbol) {
    //if in grammar other symbol Name is defined e.g. 'symbol (MCType) MCQualifiedType implements MCObjectType = MCQualifiedName;'
    if (clazz.getModifierOpt().isPresent()) {
      Optional<String> symbolTypeValue = getSymbolTypeValue(clazz.getModifierOpt().get());
      if (symbolTypeValue.isPresent()) {
        return Optional.empty();
      }
    }
    return Optional.of(getPackage(cdDefinitionSymbol) + "." + getSymbolName(clazz));
  }

  public ASTMCType getSymbolType(ASTCDType clazz) {
    return getCDTypeFactory().createQualifiedType(getSymbolFullTypeName(clazz));
  }

  public String getReferencedSymbolTypeName(ASTCDAttribute attribute) {
    String referencedSymbol = CD4AnalysisHelper.getStereotypeValues(attribute,
        MC2CDStereotypes.REFERENCED_SYMBOL.toString()).get(0);

    if (!getQualifier(referencedSymbol).isEmpty() && !referencedSymbol.contains(SYMBOL_TABLE_PACKAGE)) {
      referencedSymbol = SymbolTableGeneratorHelper
          .getQualifiedSymbolType(getQualifier(referencedSymbol)
              .toLowerCase(), Names.getSimpleName(referencedSymbol));
    }
    return referencedSymbol;
  }

  public String getSymbolBuilderName(ASTCDType astcdType) {
    return getSymbolName(astcdType) + BUILDER_SUFFIX;
  }

  public String getSimpleSymbolNameFromOptional(ASTMCType type) {
    ASTMCType referencedSymbolType = MCSimpleGenericTypesHelper.getReferenceTypeFromOptional(type).getMCTypeOpt().get();
    String referencedSymbol = referencedSymbolType.printType();
    return getSimpleName(referencedSymbol).substring(0, getSimpleName(referencedSymbol).indexOf(SYMBOL_SUFFIX));
  }

  public String getSimpleSymbolName(String referencedSymbol) {
    return getSimpleName(referencedSymbol).substring(0, getSimpleName(referencedSymbol).indexOf(SYMBOL_SUFFIX));
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

  public List<ASTCDClass> getSymbolClasses(List<ASTCDClass> classList) {
    return classList.stream()
        .filter(ASTCDClassTOP::isPresentModifier)
        .filter(c -> hasSymbolStereotype(c.getModifier()))
        .collect(Collectors.toList());
  }

  public List<ASTCDInterface> getSymbolInterfaces(List<ASTCDInterface> astcdInterfaces) {
    return astcdInterfaces.stream()
        .filter(ASTCDInterface::isPresentModifier)
        .filter(c -> hasSymbolStereotype(c.getModifier()))
        .collect(Collectors.toList());
  }

  public List<ASTCDClass> getScopeClasses(List<ASTCDClass> classList) {
    return classList.stream()
        .filter(ASTCDClassTOP::isPresentModifier)
        .filter(c -> hasScopeStereotype(c.getModifier()))
        .collect(Collectors.toList());
  }

  public List<ASTCDInterface> getScopeInterfaces(List<ASTCDInterface> astcdInterfaces) {
    return astcdInterfaces.stream()
        .filter(ASTCDInterface::isPresentModifier)
        .filter(c -> hasScopeStereotype(c.getModifier()))
        .collect(Collectors.toList());
  }

  public boolean hasProd(ASTCDDefinition astcdDefinition) {
    // is true if it has any class productions or any interface productions that are not the language interface
    return !astcdDefinition.isEmptyCDClasss() ||
        (!astcdDefinition.isEmptyCDInterfaces() &&
            !(astcdDefinition.sizeCDInterfaces() == 1
                && astcdDefinition.getCDInterface(0).getName().equals(getSimleLanguageInterfaceName())));
  }
}
