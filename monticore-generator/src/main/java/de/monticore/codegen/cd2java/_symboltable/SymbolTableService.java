package de.monticore.codegen.cd2java._symboltable;

import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.symboltable.SymbolTableGeneratorHelper;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.CD4AnalysisHelper;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.se_rwth.commons.Names;

import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PREFIX;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.INTERFACE_PREFIX;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.SYMBOL_TABLE_PACKGE;
import static de.monticore.utils.Names.getSimpleName;
import static de.se_rwth.commons.Names.getQualifier;

public class SymbolTableService extends AbstractService<SymbolTableService> {

  public SymbolTableService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  public SymbolTableService(CDSymbol cdSymbol) {
    super(cdSymbol);
  }

  @Override
  public String getSubPackage() {
    return SYMBOL_TABLE_PACKGE;
  }

  @Override
  protected SymbolTableService createService(CDSymbol cdSymbol) {
    return createSymbolTableService(cdSymbol);
  }

  public static SymbolTableService createSymbolTableService(CDSymbol cdSymbol) {
    return new SymbolTableService(cdSymbol);
  }

  public String getScopeTypeName() {
    return getScopeTypeName(getCDSymbol());
  }

  public String getScopeTypeName(CDSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + cdSymbol.getName() + SymbolTableConstants.SCOPE_SUFFIX;
  }

  public String getScopeInterfaceTypeName() {
    return getScopeInterfaceTypeName(getCDSymbol());
  }

  public String getScopeInterfaceTypeName(CDSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + INTERFACE_PREFIX + cdSymbol.getName() + SymbolTableConstants.SCOPE_SUFFIX;
  }

  public ASTType getScopeType() {
    return getCDTypeFactory().createSimpleReferenceType(getScopeTypeName());
  }

  public ASTType getScopeInterfaceType() {
    return getCDTypeFactory().createSimpleReferenceType(getScopeInterfaceTypeName());
  }

  public ASTType getScopeInterfaceType(CDSymbol cdSymbol) {
    return getCDTypeFactory().createSimpleReferenceType(getScopeInterfaceTypeName(cdSymbol));
  }

  public String getSymbolName(ASTCDType clazz) {
    if (clazz.getName().startsWith(AST_PREFIX)) {
      return clazz.getName().substring(AST_PREFIX.length()) + SymbolTableConstants.SYMBOL_SUFFIX;
    } else {
      return clazz.getName() + SymbolTableConstants.SYMBOL_SUFFIX;
    }
  }

  public String getSymbolTypeName(ASTCDType clazz) {
    return getPackage() + "." + getSymbolName(clazz);
  }

  public ASTType getSymbolType(ASTCDType clazz) {
    return getCDTypeFactory().createSimpleReferenceType(getSymbolTypeName(clazz));
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

  public String getSimpleSymbolNameFromOptional(ASTType type) {
    ASTType referencedSymbolType = TypesHelper.getSimpleReferenceTypeFromOptional(type.deepClone());
    String referencedSymbol = TypesPrinter.printType(referencedSymbolType);
    return getSimpleName(referencedSymbol).substring(0, getSimpleName(referencedSymbol).indexOf(SymbolTableConstants.SYMBOL_SUFFIX));
  }

  public String getSimpleSymbolName(String referencedSymbol) {
    return getSimpleName(referencedSymbol).substring(0, getSimpleName(referencedSymbol).indexOf(SymbolTableConstants.SYMBOL_SUFFIX));
  }

  public boolean isReferencedSymbol(ASTCDAttribute attribute) {
    return attribute.isPresentModifier() && hasStereotype(attribute.getModifier(), MC2CDStereotypes.REFERENCED_SYMBOL);
  }

  public boolean hasSymbolStereotype(ASTModifier modifier) {
    return hasStereotype(modifier, MC2CDStereotypes.SYMBOL);
  }

  public Optional<String> getSymbolTypeValue(ASTModifier modifier) {
    List<String> stereotypeValues = getStereotypeValues(modifier, MC2CDStereotypes.SYMBOL);
    if (!stereotypeValues.isEmpty()) {
      return Optional.ofNullable(stereotypeValues.get(0));
    }
    return Optional.empty();
  }

  public boolean hasScopeStereotype(ASTModifier modifier) {
    return hasStereotype(modifier, MC2CDStereotypes.SCOPE);
  }
}
