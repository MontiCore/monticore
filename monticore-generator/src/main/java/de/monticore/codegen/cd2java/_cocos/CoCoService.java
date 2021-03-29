/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._cocos;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

public class CoCoService extends AbstractService<CoCoService> {

  public CoCoService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  public CoCoService(DiagramSymbol cdSymbol) {
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
  protected CoCoService createService(DiagramSymbol cdSymbol) {
    return createCoCoService(cdSymbol);
  }

  public static CoCoService createCoCoService(DiagramSymbol cdSymbol) {
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

  public String getCheckerSimpleTypeName(DiagramSymbol cdSymbol) {
    return cdSymbol.getName() + CoCoConstants.COCO_CHECKER_SUFFIX;
  }

  public String getCheckerFullTypeName() {
    return String.join(".", getPackage(), getCheckerSimpleTypeName());
  }

  public String getCheckerFullTypeName(DiagramSymbol cdSymbol) {
    return String.join(".", getPackage(cdSymbol), getCheckerSimpleTypeName(cdSymbol));
  }

  public ASTMCType getCheckerType() {
    return getMCTypeFacade().createQualifiedType(getCheckerFullTypeName());
  }
}
