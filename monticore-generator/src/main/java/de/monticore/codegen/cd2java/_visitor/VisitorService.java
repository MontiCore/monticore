/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDDefinition;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.cd4analysis._ast.ASTCDParameter;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.factories.CDMethodFacade;
import de.monticore.codegen.cd2java.factories.CDParameterFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.SCOPE_SUFFIX;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.SYMBOL_SUFFIX;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class VisitorService extends AbstractService<VisitorService> {

  public VisitorService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  public VisitorService(CDDefinitionSymbol cdSymbol) {
    super(cdSymbol);
  }

  @Override
  public String getSubPackage() {
    return VisitorConstants.VISITOR_PACKAGE;
  }

  @Override
  protected VisitorService createService(CDDefinitionSymbol cdSymbol) {
    return createVisitorService(cdSymbol);
  }

  public VisitorService createVisitorService(CDDefinitionSymbol cdSymbol) {
    return new VisitorService(cdSymbol);
  }

  public String getVisitorSimpleTypeName() {
    return getVisitorSimpleTypeName(getCDSymbol());
  }

  public String getInheritanceVisitorSimpleTypeName() {
    return getInheritanceVisitorSimpleTypeName(getCDSymbol());
  }

  public String getParentAwareVisitorSimpleTypeName() {
    return getCDName() + PARENT_AWARE_SUFFIX + VisitorConstants.VISITOR_SUFFIX;
  }

  public String getDelegatorVisitorSimpleTypeName() {
    return getCDName() + DELEGATOR_SUFFIX + VisitorConstants.VISITOR_SUFFIX;
  }

  public String getScopeVisitorSimpleTypeName() {
    return getCDName() + SCOPE_SUFFIX + VisitorConstants.VISITOR_SUFFIX;
  }

  public String getScopeVisitorFullTypeName(CDDefinitionSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + cdSymbol.getName() + SCOPE_SUFFIX + VisitorConstants.VISITOR_SUFFIX;
  }

  public String getSymbolVisitorSimpleTypeName() {
    return getCDName() + SYMBOL_SUFFIX + VisitorConstants.VISITOR_SUFFIX;
  }

  public String getSymbolVisitorFullTypeName() {
    return getPackage() + "." + getSymbolVisitorSimpleTypeName();
  }

  public String getVisitorFullTypeName() {
    return getVisitorFullTypeName(getCDSymbol());
  }

  public ASTMCType getVisitorType() {
    return getVisitorType(getCDSymbol());
  }

  public ASTMCQualifiedType getVisitorReferenceType() {
    return getVisitorReferenceType(getCDSymbol());
  }

  public String getVisitorSimpleTypeName(CDDefinitionSymbol cdSymbol) {
    return cdSymbol.getName() + VisitorConstants.VISITOR_SUFFIX;
  }

  public String getVisitorSimpleTypeName(ASTCDDefinition astcdDefinition) {
    return astcdDefinition.getName() + VisitorConstants.VISITOR_SUFFIX;
  }

  public String getInheritanceVisitorSimpleTypeName(CDDefinitionSymbol cdSymbol) {
    return cdSymbol.getName() + INHERITANCE_SUFFIX + VisitorConstants.VISITOR_SUFFIX;
  }

  public String getInheritanceVisitorFullTypeName(CDDefinitionSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getInheritanceVisitorSimpleTypeName(cdSymbol);
  }

  public String getVisitorFullTypeName(CDDefinitionSymbol cdSymbol) {
    return String.join(".", getPackage(cdSymbol), getVisitorSimpleTypeName(cdSymbol));
  }

  public ASTMCType getVisitorType(CDDefinitionSymbol cdSymbol) {
    return getCDTypeFactory().createQualifiedType(getVisitorFullTypeName(cdSymbol));
  }

  public ASTMCQualifiedType getVisitorReferenceType(CDDefinitionSymbol cdSymbol) {
    return getCDTypeFactory().createQualifiedType(getVisitorFullTypeName(cdSymbol));
  }

  public List<ASTMCQualifiedType> getAllVisitorTypesInHierarchy() {
    return getServicesOfSuperCDs().stream()
        .map(VisitorService::getVisitorReferenceType)
        .collect(Collectors.toList());
  }


  public ASTCDMethod getVisitorMethod(String methodName, ASTMCType nodeType) {
    ASTCDParameter visitorParameter = CDParameterFacade.getInstance().createParameter(nodeType, "node");
    return CDMethodFacade.getInstance().createMethod(PUBLIC, methodName, visitorParameter);
  }

  public ASTCDCompilationUnit calculateCDTypeNamesWithPackage(ASTCDCompilationUnit input) {
    // transform own cd
    ASTCDCompilationUnit compilationUnit = input.deepClone();
    //set classname to correct Name with path
    String astPath = getASTPackage();
    compilationUnit.getCDDefinition().getCDClassList().forEach(c -> c.setName(astPath + "." + c.getName()));
    compilationUnit.getCDDefinition().getCDInterfaceList().forEach(i -> i.setName(astPath + "." + i.getName()));
    compilationUnit.getCDDefinition().getCDEnumList().forEach(e -> e.setName(astPath + "." + e.getName()));
    return compilationUnit;
  }

  public ASTCDDefinition calculateCDTypeNamesWithPackage(CDDefinitionSymbol input) {
    // transform inherited cd
    ASTCDDefinition astcdDefinition = input.getAstNode().get().deepClone();
    //set classname to correct Name with path
    String astPath = getASTPackage(input);
    astcdDefinition.getCDClassList().forEach(c -> c.setName(astPath + "." + c.getName()));
    astcdDefinition.getCDInterfaceList().forEach(i -> i.setName(astPath + "." + i.getName()));
    astcdDefinition.getCDEnumList().forEach(e -> e.setName(astPath + "." + e.getName()));
    return astcdDefinition;
  }

  public List<ASTMCQualifiedType> getSuperInheritanceVisitors() {
    //only direct super cds, not transitive
    List<CDDefinitionSymbol> superCDs = getSuperCDsDirect();
    return superCDs
        .stream()
        .map(this::getInheritanceVisitorFullTypeName)
        .map(getCDTypeFactory()::createQualifiedType)
        .collect(Collectors.toList());
  }

  public List<ASTMCQualifiedType> getSuperVisitors() {
    //only direct super cds, not transitive
    List<CDDefinitionSymbol> superCDs = getSuperCDsDirect();
    return superCDs
        .stream()
        .map(this::getVisitorReferenceType)
        .collect(Collectors.toList());
  }
}
