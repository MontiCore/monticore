package de.monticore.codegen.cd2java.visitor_new;

import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.factories.CDMethodFactory;
import de.monticore.codegen.cd2java.factories.CDParameterFactory;
import de.monticore.codegen.cd2java.factories.SuperSymbolHelper;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;
import de.monticore.umlcd4a.symboltable.CDSymbol;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class VisitorService extends AbstractService {

  public VisitorService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  @Override
  protected String getSubPackage() {
    return VisitorConstants.VISITOR_PACKAGE;
  }

  public String getVisitorSimpleTypeName() {
    return getCDName() + VisitorConstants.VISITOR_SUFFIX;
  }

  public String getVisitorFullTypeName() {
    return getPackage() + "." + getVisitorSimpleTypeName();
  }

  public ASTType getVisitorType() {
    return getCDTypeFactory().createSimpleReferenceType(getVisitorFullTypeName());
  }

  public ASTReferenceType getVisitorType(CDSymbol cd) {
    return getCDTypeFactory().createSimpleReferenceType(String.join(".", getPackage(cd), cd.getName() + VisitorConstants.VISITOR_SUFFIX));
  }

  public List<ASTReferenceType> getAllVisitorTypesInHierarchy() {
    return SuperSymbolHelper.getSuperCDs(getCD()).stream()
        .map(this::getVisitorType)
        .collect(Collectors.toList());
  }

  public ASTCDMethod getVisitorMethod(String methodType, ASTType nodeType) {
    ASTCDParameter visitorParameter = CDParameterFactory.getInstance().createParameter(nodeType, "node");
    return CDMethodFactory.getInstance().createMethod(PUBLIC, methodType, visitorParameter);
  }
}
