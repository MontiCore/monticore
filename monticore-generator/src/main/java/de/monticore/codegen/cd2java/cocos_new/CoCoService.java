package de.monticore.codegen.cd2java.cocos_new;

import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDType;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;

public class CoCoService extends AbstractService {

  public CoCoService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  @Override
  protected String getSubPackage() {
    return CoCoConstants.COCO_PACKAGE;
  }

  public String getCoCoSimpleTypeName(ASTCDType type) {
    return getCDName() + type.getName() + CoCoConstants.COCO_SUFFIX;
  }

  public String getCoCoFullTypeName(ASTCDType type) {
    return getPackage() + getCoCoSimpleTypeName(type);
  }

  public ASTType getCoCoType(ASTCDType type) {
    return getCDTypeFactory().createSimpleReferenceType(getCoCoFullTypeName(type));
  }

  public ASTType getCoCoType(CDTypeSymbol type) {
    //FIXME: missing CD name in package name, e.g., results in 'de.monticore.codegen._cocos.CDFooCoCo', but should be 'de.monticore.codegen.cocos._cocos.CDFooCoCo'
    return getCDTypeFactory().createSimpleReferenceType(String.join(".", type.getPackageName().toLowerCase(), CoCoConstants.COCO_PACKAGE,
        getCoCoSimpleTypeName((ASTCDType) type.getAstNode().get())));
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

  public String getCheckerSimpleTypeName(CDSymbol cd) {
    return cd.getName() + CoCoConstants.COCO_CHECKER_SUFFIX;
  }

  public String getCheckerFullTypeName(CDSymbol cd) {
    return String.join(".", getPackage(cd), getCheckerSimpleTypeName(cd));
  }

  public ASTType getCheckerType(CDSymbol cd) {
    return getCDTypeFactory().createSimpleReferenceType(getCheckerFullTypeName(cd));
  }
}
