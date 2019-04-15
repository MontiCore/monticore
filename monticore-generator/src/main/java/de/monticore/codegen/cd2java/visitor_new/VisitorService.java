package de.monticore.codegen.cd2java.visitor_new;

import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.factories.CDMethodFacade;
import de.monticore.codegen.cd2java.factories.CDParameterFacade;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;
import de.monticore.umlcd4a.symboltable.CDSymbol;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class VisitorService extends AbstractService<VisitorService> {

  public VisitorService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  public VisitorService(CDSymbol cdSymbol) {
    super(cdSymbol);
  }

  @Override
  protected String getSubPackage() {
    return VisitorConstants.VISITOR_PACKAGE;
  }

  @Override
  protected VisitorService createService(CDSymbol cdSymbol) {
    return createVisitorService(cdSymbol);
  }

  public VisitorService createVisitorService(CDSymbol cdSymbol) {
    return new VisitorService(cdSymbol);
  }

  public String getVisitorSimpleTypeName() {
    return getCDName() + VisitorConstants.VISITOR_SUFFIX;
  }

  public String getVisitorFullTypeName() {
    return String.join(".",getPackage(), getVisitorSimpleTypeName());
  }

  public ASTType getVisitorType() {
    return getCDTypeFactory().createSimpleReferenceType(getVisitorFullTypeName());
  }

  public ASTReferenceType getVisitorReferenceType() {
    return getCDTypeFactory().createSimpleReferenceType(getVisitorFullTypeName());
  }

  public List<ASTReferenceType> getAllVisitorTypesInHierarchy() {
    return getServicesOfSuperCDs().stream()
        .map(VisitorService::getVisitorReferenceType)
        .collect(Collectors.toList());
  }


  ASTCDMethod getVisitorMethod(String methodName, ASTType nodeType) {
    ASTCDParameter visitorParameter = CDParameterFacade.getInstance().createParameter(nodeType, "node");
    return CDMethodFacade.getInstance().createMethod(PUBLIC, methodName, visitorParameter);
  }
}
