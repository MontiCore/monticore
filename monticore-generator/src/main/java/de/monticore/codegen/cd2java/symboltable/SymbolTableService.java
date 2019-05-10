package de.monticore.codegen.cd2java.symboltable;

import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.symboltable.SymbolTableGeneratorHelper;
import de.monticore.types.TypesHelper;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.CD4AnalysisHelper;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.se_rwth.commons.Names;

import static de.monticore.codegen.cd2java.ast_new.ASTConstants.AST_PREFIX;
import static de.monticore.codegen.cd2java.symboltable.SymbolTableConstants.INTERFACE_PREFIX;
import static de.monticore.codegen.cd2java.symboltable.SymbolTableConstants.SYMBOL_SUFFIX;
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

  public String getScopeInterfaceTypeName() {
    return getPackage() + "." + INTERFACE_PREFIX + getCDName() + SymbolTableConstants.SCOPE_SUFFIX;
  }

  public ASTType getScopeType() {
    return getCDTypeFactory().createSimpleReferenceType(getScopeTypeName());
  }

  public ASTType getScopeIntefaceType() {
    return getCDTypeFactory().createSimpleReferenceType(getScopeInterfaceTypeName());
  }

  public String getSymbolName(ASTCDClass clazz) {
    if (clazz.getName().startsWith(AST_PREFIX)) {
      return clazz.getName().substring(AST_PREFIX.length()) + SymbolTableConstants.SYMBOL_SUFFIX;
    } else {
      return clazz.getName() + SymbolTableConstants.SYMBOL_SUFFIX;
    }
  }

  public String getSymbolTypeName(ASTCDClass clazz) {
    return getPackage() + "." + getSymbolName(clazz);
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

  public boolean isReferencedSymbol(ASTCDAttribute attribute) {
    return attribute.isPresentModifier() && hasStereotype(attribute.getModifier(), MC2CDStereotypes.REFERENCED_SYMBOL);
  }

  public boolean isSymbolClass(ASTCDClass clazz) {
    return clazz.isPresentModifier() && hasStereotype(clazz.getModifier(), MC2CDStereotypes.SYMBOL);
  }

  public boolean isScopeClass(ASTCDClass clazz) {
    return clazz.isPresentModifier() && hasStereotype(clazz.getModifier(), MC2CDStereotypes.SCOPE);
  }

  public String getResolveMethodNameSuffix(ASTCDAttribute attribute) {
    //get A for resolve A
    ASTType referencedSymbolType = TypesHelper.getSimpleReferenceTypeFromOptional(attribute.getType().deepClone());
    String referencedSymbolName = TypesPrinter.printType(referencedSymbolType);
    //remove package
    String simpleReferencedSymbolName = referencedSymbolName.contains(".")?
        referencedSymbolName.substring(referencedSymbolName.lastIndexOf(".")+1):
        referencedSymbolName;
    //remove Symbol suffix
    return simpleReferencedSymbolName.endsWith(SYMBOL_SUFFIX) ?
        simpleReferencedSymbolName.substring(0, simpleReferencedSymbolName.lastIndexOf(SYMBOL_SUFFIX)) :
        simpleReferencedSymbolName;
  }
}
