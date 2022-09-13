/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.facade.CDParameterFacade;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
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

  public VisitorService(DiagramSymbol cdSymbol) {
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
  protected VisitorService createService(DiagramSymbol cdSymbol) {
    return createVisitorService(cdSymbol);
  }

  public static VisitorService createVisitorService(DiagramSymbol cdSymbol) {
    return new VisitorService(cdSymbol);
  }

  /**
   * simple visitor name e.g. AutomataVisitor
   */

  public String getVisitorSimpleName() {
    return getVisitorSimpleName(getCDSymbol());
  }

  public String getVisitorSimpleName(DiagramSymbol cdSymbol) {
    return cdSymbol.getName() + VisitorConstants.VISITOR_SUFFIX;
  }
  

  public String getVisitorFullName() {
    return getVisitorFullName(getCDSymbol());
  }

  public String getVisitorFullName(DiagramSymbol cdSymbol) {
    return String.join(".", getPackage(cdSymbol), getVisitorSimpleName(cdSymbol));
  }

  public ASTMCQualifiedType getVisitorType(DiagramSymbol cdSymbol) {
    return getMCTypeFacade().createQualifiedType(getVisitorFullName(cdSymbol));
  }

  public ASTMCQualifiedType getVisitorType() {
    return getVisitorType(getCDSymbol());
  }


  public String getInheritanceHandlerSimpleName() {
    return getInheritanceHandlerSimpleName(getCDSymbol());
  }

  public String getInheritanceHandlerSimpleName(DiagramSymbol cdSymbol) {
    return cdSymbol.getName() + INHERITANCE_SUFFIX + HANDLER_SUFFIX;
  }

  public String getInheritanceHandlerFullName() {
    return getInheritanceHandlerFullName(getCDSymbol());
  }

  public String getInheritanceHandlerFullName(DiagramSymbol cdSymbol) {
    return getPackage(cdSymbol) + "." + getInheritanceHandlerSimpleName(cdSymbol);
  }


  /**
   * traverser (+ interface) name e.g. AutomataTraverser
   */
  
  public String getTraverserSimpleName() {
    return getTraverserSimpleName(getCDSymbol());
  }
  
  public String getTraverserSimpleName(DiagramSymbol cdDefinitionSymbol) {
    return cdDefinitionSymbol.getName() + TRAVERSER_CLASS_SUFFIX;
  }
  
  public String getTraverserFullName() {
    return getTraverserFullName(getCDSymbol());
  }
  
  public String getTraverserFullName(DiagramSymbol cdDefinitionSymbol) {
    return getPackage(cdDefinitionSymbol) + "." + getTraverserSimpleName(cdDefinitionSymbol);
  }
  
  public ASTMCQualifiedType getTraverserType(DiagramSymbol cdSymbol) {
    return getMCTypeFacade().createQualifiedType(getTraverserFullName(cdSymbol));
  }

  public ASTMCQualifiedType getTraverserType() {
    return getTraverserType(getCDSymbol());
  }
  
  public String getTraverserInterfaceSimpleName() {
    return getTraverserInterfaceSimpleName(getCDSymbol());
  }
  
  public String getTraverserInterfaceSimpleName(DiagramSymbol cdDefinitionSymbol) {
    return cdDefinitionSymbol.getName() + TRAVERSER_SUFFIX;
  }
  
  public String getTraverserInterfaceFullName() {
    return getTraverserInterfaceFullName(getCDSymbol());
  }
  
  public String getTraverserInterfaceFullName(DiagramSymbol cdDefinitionSymbol) {
    return getPackage(cdDefinitionSymbol) + "." + getTraverserInterfaceSimpleName(cdDefinitionSymbol);
  }
  
  public ASTMCQualifiedType getTraverserInterfaceType(DiagramSymbol cdSymbol) {
    return getMCTypeFacade().createQualifiedType(getTraverserInterfaceFullName(cdSymbol));
  }

  public ASTMCQualifiedType getTraverserInterfaceType() {
    return getTraverserInterfaceType(getCDSymbol());
  }
  
  /**
   * visitor2 name e.g. AutomataVisitor2
   */
  
  public String getVisitor2SimpleName() {
    return getVisitor2SimpleName(getCDSymbol());
  }
  
  public String getVisitor2SimpleName(DiagramSymbol cdDefinitionSymbol) {
    return cdDefinitionSymbol.getName() + VISITOR2_SUFFIX;
  }
  
  public String getVisitor2FullName() {
    return getVisitor2FullName(getCDSymbol());
  }
  
  public String getVisitor2FullName(DiagramSymbol cdDefinitionSymbol) {
    return getPackage(cdDefinitionSymbol) + "." + getVisitor2SimpleName(cdDefinitionSymbol);
  }
  
  public ASTMCQualifiedType getVisitor2Type(DiagramSymbol cdSymbol) {
    return getMCTypeFacade().createQualifiedType(getVisitor2FullName(cdSymbol));
  }

  public ASTMCQualifiedType getVisitor2Type() {
    return getVisitor2Type(getCDSymbol());
  }
  
  /**
   * handler name e.g. AutomataHandler
   */
  
  public String getHandlerSimpleName() {
    return getHandlerSimpleName(getCDSymbol());
  }
  
  public String getHandlerSimpleName(DiagramSymbol cdDefinitionSymbol) {
    return cdDefinitionSymbol.getName() + HANDLER_SUFFIX;
  }
  
  public String getHandlerFullName() {
    return getHandlerFullName(getCDSymbol());
  }
  
  public String getHandlerFullName(DiagramSymbol cdDefinitionSymbol) {
    return getPackage(cdDefinitionSymbol) + "." + getHandlerSimpleName(cdDefinitionSymbol);
  }
  
  public ASTMCQualifiedType getHandlerType(DiagramSymbol cdSymbol) {
    return getMCTypeFacade().createQualifiedType(getHandlerFullName(cdSymbol));
  }

  public ASTMCQualifiedType getHandlerType() {
    return getHandlerType(getCDSymbol());
  }

  /**
   * other helpful methods
   */


  public List<ASTMCQualifiedType> getAllTraverserInterfacesTypesInHierarchy() {
    return getServicesOfSuperCDs().stream()
        .map(VisitorService::getTraverserInterfaceType)
        .collect(Collectors.toList());
  }

  public ASTCDMethod getVisitorMethod(String methodName, ASTMCType nodeType) {
    ASTCDParameter visitorParameter = CDParameterFacade.getInstance().createParameter(nodeType, "node");
    return CDMethodFacade.getInstance().createMethod(PUBLIC.build(), methodName, visitorParameter);
  }


  /**
   * Retrieves the super traverser interfaces with respect to the type
   * hierarchy.
   * 
   * @return The super traverser interfaces as list of qualified names.
   */
  public List<ASTMCObjectType> getSuperTraverserInterfaces() {
    // only direct super cds, not transitive
    List<DiagramSymbol> superCDs = getSuperCDsDirect();
    return superCDs
        .stream()
        .map(this::getTraverserInterfaceType)
        .collect(Collectors.toList());
  }

}
