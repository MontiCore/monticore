package de.monticore.codegen.cd2java.cocos_new;

import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.ast_new.ASTConstants;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDType;
import de.monticore.umlcd4a.symboltable.CDSymbol;

public class CoCoService extends AbstractService<CoCoService> {

  public CoCoService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  public CoCoService(CDSymbol cdSymbol) {
    super(cdSymbol);
  }

  @Override
  protected String getSubPackage() {
    return CoCoConstants.COCO_PACKAGE;
  }

  @Override
  protected CoCoService createService(CDSymbol cdSymbol) {
    return createCoCoService(cdSymbol);
  }

  public static CoCoService createCoCoService(CDSymbol cdSymbol) {
    return new CoCoService(cdSymbol);
  }

  public String getCoCoSimpleTypeName(ASTCDType type) {
    return getCDName() + ASTConstants.AST_PREFIX +  type.getName() + CoCoConstants.COCO_SUFFIX;
  }

  public String getCoCoFullTypeName(ASTCDType type) {
    return String.join(".", getPackage(), getCoCoSimpleTypeName(type));
  }

  public ASTType getCoCoType(ASTCDType type) {
    return getCDTypeFactory().createSimpleReferenceType(getCoCoFullTypeName(type));
  }

  public String getCoCoSimpleTypeName() {
    return getCDName() + ASTConstants.AST_PREFIX +  getCDName() + CoCoConstants.NODE_INFIX + CoCoConstants.COCO_SUFFIX;
  }

  public String getCoCoFullTypeName() {
    return String.join(".", getPackage(), getCoCoSimpleTypeName());
  }

  public ASTType getCoCoType() {
    return getCDTypeFactory().createSimpleReferenceType(getCoCoFullTypeName());
  }

  public String getCheckerSimpleTypeName() {
    return getCDName() + CoCoConstants.COCO_CHECKER_SUFFIX;
  }

  public String getCheckerFullTypeName() {
    return String.join(".", getPackage(), getCheckerSimpleTypeName());
  }

  public ASTType getCheckerType() {
    return getCDTypeFactory().createSimpleReferenceType(getCheckerFullTypeName());
  }
}
