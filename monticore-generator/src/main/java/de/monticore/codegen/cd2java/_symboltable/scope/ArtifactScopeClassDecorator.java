/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.scope;

import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.monticore.cd.facade.CDModifier.PRIVATE;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.ACCEPT_METHOD;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISITOR_PREFIX;

/**
 * creates a artifactScope class from a grammar
 */
public class ArtifactScopeClassDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected final SymbolTableService symbolTableService;

  protected final MethodDecorator methodDecorator;

  protected final VisitorService visitorService;

  /**
   * flag added to define if the ArtifactScope class was overwritten with the TOP mechanism
   * if top mechanism was used, must use setter to set flag true, before the decoration
   * is needed for different accept method implementations
   */
  protected boolean isArtifactScopeTop;

  protected static final String TEMPLATE_PATH = "_symboltable.artifactscope.";

  public ArtifactScopeClassDecorator(final GlobalExtensionManagement glex,
                                final SymbolTableService symbolTableService,
                                final VisitorService visitorService,
                                final MethodDecorator methodDecorator) {
    super(glex);
    this.symbolTableService = symbolTableService;
    this.visitorService = visitorService;
    this.methodDecorator = methodDecorator;
  }

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit input) {
    String artifactScopeSimpleName = symbolTableService.getArtifactScopeSimpleName();
    String scopeClassFullName = symbolTableService.getScopeClassFullName();

    ASTCDAttribute packageNameAttribute = createPackageNameAttribute();
    ASTCDAttribute importsAttribute = createImportsAttribute();

    return CD4AnalysisMill.cDClassBuilder()
        .setName(artifactScopeSimpleName)
        .setModifier(PUBLIC.build())
        .setSuperclass(getMCTypeFacade().createQualifiedType(scopeClassFullName))
        .addInterface(symbolTableService.getArtifactScopeInterfaceType())
        .addAllCDConstructors(createConstructors(artifactScopeSimpleName))
        .addCDAttributes(packageNameAttribute)
        .addAllCDMethods(createPackageNameAttributeMethods(packageNameAttribute))
        .addCDAttributes(importsAttribute)
        .addAllCDMethods(createImportsAttributeMethods(importsAttribute))
        .addCDMethods(createIsPresentNameMethod())
        .addCDMethods(createGetNameMethod())
        .addCDMethods(createAcceptMethod(artifactScopeSimpleName))
        .build();
  }

  protected List<ASTCDConstructor> createConstructors(String artifactScopeName) {
    ASTCDParameter packageNameParam = getCDParameterFacade().createParameter(String.class, PACKAGE_NAME_VAR);
    ASTCDParameter importsParam = getCDParameterFacade().createParameter(getMCTypeFacade().createListTypeOf(IMPORT_STATEMENT), "imports");

    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), artifactScopeName, packageNameParam, importsParam);
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("this(Optional.empty(), packageName, imports);"));

    ASTCDParameter enclosingScopeParam = getCDParameterFacade().createParameter(
        getMCTypeFacade().createOptionalTypeOf(symbolTableService.getScopeInterfaceFullName()), ENCLOSING_SCOPE_VAR);
    ASTCDConstructor constructorWithEnclosingScope = getCDConstructorFacade().createConstructor(PUBLIC.build(),
        artifactScopeName, enclosingScopeParam, packageNameParam, importsParam);
    this.replaceTemplate(EMPTY_BODY, constructorWithEnclosingScope, new TemplateHookPoint(TEMPLATE_PATH + "ConstructorArtifactScope"));
    return new ArrayList<>(Arrays.asList(constructor, constructorWithEnclosingScope));
  }

  protected ASTCDAttribute createPackageNameAttribute() {
    return getCDAttributeFacade().createAttribute(PRIVATE, String.class, PACKAGE_NAME_VAR);
  }

  protected ASTCDAttribute createImportsAttribute() {
    return getCDAttributeFacade().createAttribute(PRIVATE, getMCTypeFacade().createListTypeOf(IMPORT_STATEMENT), "imports");
  }

  protected List<ASTCDMethod> createPackageNameAttributeMethods(ASTCDAttribute attr) {
    return  methodDecorator.decorate(attr);
  }

  protected List<ASTCDMethod> createImportsAttributeMethods(ASTCDAttribute attr) {
    return  methodDecorator.decorate(attr).stream()
        .filter(m -> (m.getName().equals("getImportsList") || m.getName().equals("setImportsList")))
        .collect(Collectors.toList());
  }


  protected ASTCDMethod createGetNameMethod() {
    ASTCDMethod getNameMethod = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createStringType(), "getName");
    this.replaceTemplate(EMPTY_BODY, getNameMethod, new TemplateHookPoint(TEMPLATE_PATH + "GetName"));
    return getNameMethod;
  }

  /**
   * Creates the isPresentName method for artifact scopes.
   *
   * @return The isPresentName method.
   */
  protected ASTCDMethod createIsPresentNameMethod() {
    ASTCDMethod getNameMethod = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createBooleanType(), "isPresentName");
    this.replaceTemplate(EMPTY_BODY, getNameMethod, new TemplateHookPoint(TEMPLATE_PATH + "IsPresentName"));
    return getNameMethod;
  }

  protected ASTCDMethod createAcceptMethod(String artifactScopeName) {
    String visitor = visitorService.getVisitorFullName();
    ASTCDParameter parameter = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(visitor), VISITOR_PREFIX);
    ASTCDMethod acceptMethod = getCDMethodFacade().createMethod(PUBLIC, ACCEPT_METHOD, parameter);
    if (!isArtifactScopeTop()) {
      this.replaceTemplate(EMPTY_BODY, acceptMethod, new StringHookPoint("visitor.handle(this);"));
    } else {
      String errorCode = symbolTableService.getGeneratedErrorCode(artifactScopeName);
      this.replaceTemplate(EMPTY_BODY, acceptMethod, new TemplateHookPoint(
          "_symboltable.AcceptTop", artifactScopeName, errorCode));
    }
    return acceptMethod;
  }

  public boolean isArtifactScopeTop() {
    return isArtifactScopeTop;
  }

  public void setArtifactScopeTop(boolean artifactScopeTop) {
    isArtifactScopeTop = artifactScopeTop;
  }

}
