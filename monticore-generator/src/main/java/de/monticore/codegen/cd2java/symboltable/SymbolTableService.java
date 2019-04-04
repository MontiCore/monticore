package de.monticore.codegen.cd2java.symboltable;

import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.symboltable.SymbolTableGeneratorHelper;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.CD4AnalysisHelper;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.se_rwth.commons.Names;

import java.util.Optional;

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
  protected String getSubPackage() {
    return SymbolTableConstants.SYMBOL_TABLE_PACKGE;
  }

  @Override
  protected SymbolTableService createService(CDSymbol cdSymbol) {
    return createSymbolTableService(cdSymbol);
  }

  public static SymbolTableService createSymbolTableService(CDSymbol cdSymbol) {
    return new SymbolTableService(cdSymbol);
  }

  public String getScopeTypeName() {
    return getPackage() + "." + getCDName() + SymbolTableConstants.SCOPE_SUFFIX;
  }

  public ASTType getScopeType() {
    return getCDTypeFactory().createSimpleReferenceType(getScopeTypeName());
  }

  public String getSymbolTypeName(ASTCDClass clazz) {
    return getPackage() + "." + clazz.getName() + SymbolTableConstants.SYMBOL_SUFFIX;
  }

  public ASTType getSymbolType(ASTCDClass clazz) {
    return getCDTypeFactory().createSimpleReferenceType(getSymbolTypeName(clazz));
  }

  public String getReferencedSymbolTypeName(ASTCDAttribute attribute) {
    String referencedSymbol = CD4AnalysisHelper.getStereotypeValues(attribute,
        MC2CDStereotypes.REFERENCED_SYMBOL.toString()).get(0);

    if (!getQualifier(referencedSymbol).isEmpty()) {
      referencedSymbol = SymbolTableGeneratorHelper
          .getQualifiedSymbolType(getQualifier(referencedSymbol)
              .toLowerCase(), Names.getSimpleName(referencedSymbol));
    }
    return referencedSymbol;
  }

  public String getSimpleSymbolName(String referencedSymbol) {
    return getSimpleName(referencedSymbol).substring(0, getSimpleName(referencedSymbol).indexOf(SymbolTableConstants.SYMBOL_SUFFIX));
  }

  public boolean isReferencedSymbolAttribute(ASTCDAttribute attribute) {
    return hasStereotype(attribute.getModifierOpt(), MC2CDStereotypes.REFERENCED_SYMBOL);
  }

  public boolean isSymbolClass(ASTCDClass clazz) {
    return hasStereotype(clazz.getModifierOpt(), MC2CDStereotypes.SYMBOL);
  }

  public boolean isScopeClass(ASTCDClass clazz) {
    return hasStereotype(clazz.getModifierOpt(), MC2CDStereotypes.SCOPE);
  }

  private boolean hasStereotype(Optional<ASTModifier> modifier, MC2CDStereotypes stereotype) {
    if (modifier.isPresent() && modifier.get().isPresentStereotype()) {
      return modifier.get().getStereotype().getValueList().stream().anyMatch(v -> v.getName().equals(stereotype.toString()));
    }
    return false;
  }
}
