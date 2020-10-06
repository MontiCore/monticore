/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDDefinition;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.cd4analysis._ast.ASTCDParameter;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.facade.CDParameterFacade;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.*;

public class VisitorService extends AbstractService<VisitorService> {

  public VisitorService(ASTCDCompilationUnit compilationUnit) {
    super(compilationUnit);
  }

  public VisitorService(CDDefinitionSymbol cdSymbol) {
    super(cdSymbol);
  }

  /**
   * overwrite methods of AbstractService to add the correct '_visitor' package for Visitor generation
   */

  @Override
  public String getSubPackage() {
    return VisitorConstants.VISITOR_PACKAGE;
  }

  @Override
  protected VisitorService createService(CDDefinitionSymbol cdSymbol) {
    return createVisitorService(cdSymbol);
  }

  public static VisitorService createVisitorService(CDDefinitionSymbol cdSymbol) {
    return new VisitorService(cdSymbol);
  }

  /**
   * simple visitor name e.g. AutomataVisitor
   */

  public String getVisitorSimpleName() {
    return getVisitorSimpleName(getCDSymbol());
  }

  public String getVisitorSimpleName(CDDefinitionSymbol cdSymbol) {
    return cdSymbol.getName() + VisitorConstants.VISITOR_SUFFIX;
  }

  public String getVisitorFullName() {
    return getVisitorFullName(getCDSymbol());
  }

  public String getVisitorFullName(CDDefinitionSymbol cdSymbol) {
    return String.join(".", getPackage(cdSymbol), getVisitorSimpleName(cdSymbol));
  }

  public ASTMCQualifiedType getVisitorType(CDDefinitionSymbol cdSymbol) {
    return getMCTypeFacade().createQualifiedType(getVisitorFullName(cdSymbol));
  }

  public ASTMCQualifiedType getVisitorType() {
    return getVisitorType(getCDSymbol());
  }

  /**
   * inheritance visitor name e.g. AutomataInheritanceVisitor
   */

  public String getInheritanceVisitorSimpleName() {
    return getInheritanceVisitorSimpleName(getCDSymbol());
  }

  public String getInheritanceVisitorSimpleName(CDDefinitionSymbol cdSymbol) {
    return cdSymbol.getName() + INHERITANCE_SUFFIX + VisitorConstants.VISITOR_SUFFIX;
  }

  public String getInheritanceVisitorFullName(CDDefinitionSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getInheritanceVisitorSimpleName(cdSymbol);
  }

  public String getInheritanceVisitorFullName() {
    return getInheritanceVisitorFullName(getCDSymbol());
  }

  /**
   * parent aware visitor name e.g. AutomataParentAwareVisitor
   */

  public String getParentAwareVisitorSimpleName() {
    return getParentAwareVisitorSimpleName(getCDSymbol());
  }

  public String getParentAwareVisitorSimpleName(CDDefinitionSymbol cdDefinitionSymbol) {
    return cdDefinitionSymbol.getName() + PARENT_AWARE_SUFFIX + VisitorConstants.VISITOR_SUFFIX;
  }

  public String getParentAwareVisitorFullName() {
    return getParentAwareVisitorFullName(getCDSymbol());
  }

  public String getParentAwareVisitorFullName(CDDefinitionSymbol cdDefinitionSymbol) {
    return getPackage(cdDefinitionSymbol) + "." + getParentAwareVisitorSimpleName(cdDefinitionSymbol);
  }

  /**
   * delegator visitor name e.g. AutomataDelegatorVisitor
   */

  public String getDelegatorVisitorSimpleName() {
    return getDelegatorVisitorSimpleName(getCDSymbol());
  }


  public String getDelegatorVisitorSimpleName(CDDefinitionSymbol cdDefinitionSymbol) {
    return cdDefinitionSymbol.getName() + DELEGATOR_SUFFIX + VisitorConstants.VISITOR_SUFFIX;
  }

  public String getDelegatorVisitorFullName() {
    return getDelegatorVisitorFullName(getCDSymbol());
  }

  public String getDelegatorVisitorFullName(CDDefinitionSymbol cdDefinitionSymbol) {
    return getPackage(cdDefinitionSymbol) + "." + getDelegatorVisitorSimpleName(cdDefinitionSymbol);
  }

  /**
   * traverser (+ interface) name e.g. AutomataTraverser
   */
  
  public String getTraverserSimpleName() {
    return getTraverserSimpleName(getCDSymbol());
  }
  
  public String getTraverserSimpleName(CDDefinitionSymbol cdDefinitionSymbol) {
    return cdDefinitionSymbol.getName() + TRAVERSER_SUFFIX;
  }
  
  public String getTraverserFullName() {
    return getTraverserFullName(getCDSymbol());
  }
  
  public String getTraverserFullName(CDDefinitionSymbol cdDefinitionSymbol) {
    return getPackage(cdDefinitionSymbol) + "." + getTraverserSimpleName(cdDefinitionSymbol);
  }
  
  public ASTMCQualifiedType getTraverserType(CDDefinitionSymbol cdSymbol) {
    return getMCTypeFacade().createQualifiedType(getTraverserFullName(cdSymbol));
  }

  public ASTMCQualifiedType getTraverserType() {
    return getTraverserType(getCDSymbol());
  }
  
  public String getTraverserInterfaceSimpleName() {
    return getTraverserInterfaceSimpleName(getCDSymbol());
  }
  
  public String getTraverserInterfaceSimpleName(CDDefinitionSymbol cdDefinitionSymbol) {
    return "I" + getTraverserSimpleName(cdDefinitionSymbol);
  }
  
  public String getTraverserInterfaceFullName() {
    return getTraverserInterfaceFullName(getCDSymbol());
  }
  
  public String getTraverserInterfaceFullName(CDDefinitionSymbol cdDefinitionSymbol) {
    return getPackage(cdDefinitionSymbol) + "." + getTraverserInterfaceSimpleName(cdDefinitionSymbol);
  }
  
  public ASTMCQualifiedType getTraverserInterfaceType(CDDefinitionSymbol cdSymbol) {
    return getMCTypeFacade().createQualifiedType(getTraverserInterfaceFullName(cdSymbol));
  }

  public ASTMCQualifiedType getTraverserInterfaceType() {
    return getTraverserInterfaceType(getCDSymbol());
  }
  
  /**
   * handler name e.g. AutomataHandler
   */
  
  public String getHandlerSimpleName() {
    return getHandlerSimpleName(getCDSymbol());
  }
  
  public String getHandlerSimpleName(CDDefinitionSymbol cdDefinitionSymbol) {
    return "I" + cdDefinitionSymbol.getName() + HANDLER_SUFFIX;
  }
  
  public String getHandlerFullName() {
    return getHandlerFullName(getCDSymbol());
  }
  
  public String getHandlerFullName(CDDefinitionSymbol cdDefinitionSymbol) {
    return getPackage(cdDefinitionSymbol) + "." + getHandlerSimpleName(cdDefinitionSymbol);
  }

  /**
   * other helpful methods
   */

  public List<ASTMCQualifiedType> getAllVisitorTypesInHierarchy() {
    return getServicesOfSuperCDs().stream()
        .map(VisitorService::getVisitorType)
        .collect(Collectors.toList());
  }

  public ASTCDMethod getVisitorMethod(String methodName, ASTMCType nodeType) {
    ASTCDParameter visitorParameter = CDParameterFacade.getInstance().createParameter(nodeType, "node");
    return CDMethodFacade.getInstance().createMethod(PUBLIC, methodName, visitorParameter);
  }

  /**
   * add AST package to all classes, interfaces and enums at the beginning
   * needed because visitor classes are in a different package and so need to fully qualify the ast classes
   */
  public ASTCDCompilationUnit calculateCDTypeNamesWithASTPackage(ASTCDCompilationUnit input) {
    // transform own cd
    ASTCDCompilationUnit compilationUnit = input.deepClone();
    //set classname to correct Name with path
    String astPath = getASTPackage();
    compilationUnit.getCDDefinition().getCDClassList().forEach(c -> c.setName(astPath + "." + c.getName()));
    compilationUnit.getCDDefinition().getCDInterfaceList().forEach(i -> i.setName(astPath + "." + i.getName()));
    compilationUnit.getCDDefinition().getCDEnumList().forEach(e -> e.setName(astPath + "." + e.getName()));
    return compilationUnit;
  }

  /**
   * add AST package to all classes, interfaces and enums at the beginning
   * needed because visitor classes are in a different package and so need to fully qualify the ast classes
   */
  public ASTCDDefinition calculateCDTypeNamesWithASTPackage(CDDefinitionSymbol input) {
    // transform inherited cd
    ASTCDDefinition astcdDefinition = input.getAstNode().deepClone();
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
        .map(this::getInheritanceVisitorFullName)
        .map(getMCTypeFacade()::createQualifiedType)
        .collect(Collectors.toList());
  }

  public List<ASTMCQualifiedType> getSuperVisitors() {
    //only direct super cds, not transitive
    List<CDDefinitionSymbol> superCDs = getSuperCDsDirect();
    return superCDs
        .stream()
        .map(this::getVisitorType)
        .collect(Collectors.toList());
  }
  
  /**
   * Retrieves the super traverser interfaces with respect to the type
   * hierarchy.
   * 
   * @return The super traverser interfaces as list of qualified names.
   */
  public List<ASTMCQualifiedType> getSuperTraverserInterfaces() {
    // only direct super cds, not transitive
    List<CDDefinitionSymbol> superCDs = getSuperCDsDirect();
    return superCDs
        .stream()
        .map(this::getTraverserInterfaceType)
        .collect(Collectors.toList());
  }

  public List<ASTMCQualifiedType> getSuperSymbolVisitors() {
    //only direct super cds, not transitive
    List<CDDefinitionSymbol> superCDs = getSuperCDsDirect();
    return superCDs
            .stream()
            .map(this::getVisitorFullName)
            .map(getMCTypeFacade()::createQualifiedType)
            .collect(Collectors.toList());
  }
}
