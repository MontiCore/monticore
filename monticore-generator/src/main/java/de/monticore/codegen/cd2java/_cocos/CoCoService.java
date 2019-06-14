package de.monticore.codegen.cd2java._cocos;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDType;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java._ast.ast_class.ASTConstants;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

public class CoCoService extends AbstractService<CoCoService> {

  public CoCoService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  public CoCoService(CDDefinitionSymbol cdSymbol) {
    super(cdSymbol);
  }

  @Override
  public String getSubPackage() {
    return CoCoConstants.COCO_PACKAGE;
  }

  @Override
  protected CoCoService createService(CDDefinitionSymbol cdSymbol) {
    return createCoCoService(cdSymbol);
  }

  public static CoCoService createCoCoService(CDDefinitionSymbol cdSymbol) {
    return new CoCoService(cdSymbol);
  }

  public String getCoCoSimpleTypeName(ASTCDType type) {
    return getCDName() + ASTConstants.AST_PREFIX +  type.getName() + CoCoConstants.COCO_SUFFIX;
  }

  public String getCoCoFullTypeName(ASTCDType type) {
    return String.join(".", getPackage(), getCoCoSimpleTypeName(type));
  }

  public ASTMCType getCoCoType(ASTCDType type) {
    return getCDTypeFactory().createSimpleReferenceType(getCoCoFullTypeName(type));
  }

  public String getCoCoSimpleTypeName() {
    return getCDName() + ASTConstants.AST_PREFIX +  getCDName() + CoCoConstants.NODE_INFIX + CoCoConstants.COCO_SUFFIX;
  }

  public String getCoCoFullTypeName() {
    return String.join(".", getPackage(), getCoCoSimpleTypeName());
  }

  public ASTMCType getCoCoType() {
    return getCDTypeFactory().createSimpleReferenceType(getCoCoFullTypeName());
  }

  public String getCheckerSimpleTypeName() {
    return getCDName() + CoCoConstants.COCO_CHECKER_SUFFIX;
  }

  public String getCheckerFullTypeName() {
    return String.join(".", getPackage(), getCheckerSimpleTypeName());
  }

  public ASTMCType getCheckerType() {
    return getCDTypeFactory().createSimpleReferenceType(getCheckerFullTypeName());
  }
}
