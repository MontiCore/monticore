package de.monticore.codegen.cd2java._visitor;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.HANDLE;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class InheritanceVisitorDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {


  private final VisitorService visitorService;

  public InheritanceVisitorDecorator(final GlobalExtensionManagement glex,
                                     final VisitorService visitorService) {
    super(glex);
    this.visitorService = visitorService;
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit input) {
    ASTCDCompilationUnit compilationUnit = visitorService.calculateCDTypeNamesWithPackage(input);


    return CD4AnalysisMill.cDInterfaceBuilder()
        .setName(visitorService.getInheritanceVisitorSimpleTypeName())
        .setModifier(PUBLIC.build())
        .addInterface(visitorService.getVisitorReferenceType())
        .addAllInterfaces(getSuperVisitor())
        .addAllCDMethods(getHandleMethods(compilationUnit.getCDDefinition()))
        .build();
  }

  protected List<ASTMCQualifiedType> getSuperVisitor() {
    //only direct super cds, not transitive
    List<CDDefinitionSymbol> superCDs = visitorService.getSuperCDsDirect();
    return superCDs
        .stream()
        .map(visitorService::getVisitorReferenceType)
        .collect(Collectors.toList());
  }

  protected List<ASTCDMethod> getHandleMethods(ASTCDDefinition astcdDefinition) {
    List<ASTCDMethod> handleMethods = new ArrayList<>();
    String visitorSimpleTypeName = visitorService.getVisitorSimpleTypeName();
    handleMethods.addAll(astcdDefinition.getCDClassList()
        .stream()
        .map(c -> visitorService.getVisitorMethod(HANDLE, getCDTypeFacade().createQualifiedType(c.getName())))
        .collect(Collectors.toList()));

    handleMethods.addAll(astcdDefinition.getCDInterfaceList()
        .stream()
        .map(c -> visitorService.getVisitorMethod(HANDLE, getCDTypeFacade().createQualifiedType(c.getName())))
        .collect(Collectors.toList()));

    // add template
    handleMethods.forEach(m -> replaceTemplate(EMPTY_BODY, m,
        new TemplateHookPoint("_visitor.HandleInheritance",
            visitorService.getLanguageInterfaceName(astcdDefinition.getName()), visitorSimpleTypeName)));

    return handleMethods;
  }
}
