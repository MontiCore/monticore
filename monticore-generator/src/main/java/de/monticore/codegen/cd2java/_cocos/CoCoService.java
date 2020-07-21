/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._cocos;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDType;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

public class CoCoService extends AbstractService<CoCoService> {

  public CoCoService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  public CoCoService(CDDefinitionSymbol cdSymbol) {
    super(cdSymbol);
  }

  /**
   * overwrite methods of AbstractService to add the correct '_coco' package for CoCo generation
   */

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

  /**
   * coco interface name
   */
  public String getCoCoSimpleTypeName(ASTCDType type) {
    return getCDName() + type.getName() + CoCoConstants.COCO_SUFFIX;
  }

  public String getCoCoSimpleTypeName(String type) {
    return getCDName() + type + CoCoConstants.COCO_SUFFIX;
  }

  public String getCoCoFullTypeName(ASTCDType type) {
    return String.join(".", getPackage(), getCoCoSimpleTypeName(type));
  }

  /**
   * coco type
   */
  public ASTMCType getCoCoType(ASTCDType type) {
    return getMCTypeFacade().createQualifiedType(getCoCoFullTypeName(type));
  }

  /**
   * coco checker name
   */
  public String getCheckerSimpleTypeName() {
    return getCDName() + CoCoConstants.COCO_CHECKER_SUFFIX;
  }

  public String getCheckerFullTypeName() {
    return String.join(".", getPackage(), getCheckerSimpleTypeName());
  }

  public ASTMCType getCheckerType() {
    return getMCTypeFacade().createQualifiedType(getCheckerFullTypeName());
  }
}
