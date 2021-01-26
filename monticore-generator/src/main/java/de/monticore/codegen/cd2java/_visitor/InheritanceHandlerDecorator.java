/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4code.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PRIVATE;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CoreTemplates.*;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_INTERFACE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.I_SCOPE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.I_SYMBOL;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.*;

/**
 * creates a InheritanceVisitor class from a grammar
 */
public class InheritanceHandlerDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected final VisitorService visitorService;

  protected final SymbolTableService symbolTableService;

  protected final MethodDecorator methodDecorator;
  public InheritanceHandlerDecorator(final GlobalExtensionManagement glex,  final MethodDecorator methodDecorator,
                                     final VisitorService visitorService,
                                     final SymbolTableService symbolTableService) {
    super(glex);
    this.methodDecorator = methodDecorator;
    this.visitorService = visitorService;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit input) {
    ASTCDDefinition cdDefinition = input.deepClone().getCDDefinition();
    ASTCDCompilationUnit compilationUnit = visitorService.calculateCDTypeNamesWithASTPackage(input);
    String languageInterfaceName = visitorService.getLanguageInterfaceName();
    String handlerSimpleName = visitorService.getHandlerSimpleName();

    ASTCDAttribute traverserAttribute = getCDAttributeFacade().createAttribute(PRIVATE, visitorService.getTraverserInterfaceType(), TRAVERSER);
    List<ASTCDMethod> traverserMethods = methodDecorator.decorate(traverserAttribute);

    ASTCDClass cdClass = CD4CodeMill.cDClassBuilder()
        .setName(visitorService.getInheritanceHandlerSimpleName())
        .setModifier(PUBLIC.build())
        .addInterface(visitorService.getHandlerType())
        .addCDAttribute(traverserAttribute)
        .addAllCDMethods(traverserMethods)
        .addAllCDMethods(getASTHandleMethods(compilationUnit.getCDDefinition(), handlerSimpleName, languageInterfaceName))
        .addAllCDMethods(getScopeHandleMethods(cdDefinition, handlerSimpleName))
        .addAllCDMethods(getSymbolHandleMethods(cdDefinition, handlerSimpleName))
        .build();
    
    this.replaceTemplate(ANNOTATIONS, cdClass, createAnnotationsHookPoint(cdClass.getModifier()));
    return cdClass;
  }

  protected List<ASTCDMethod> getASTHandleMethods(ASTCDDefinition astcdDefinition, String handlerSimpleTypeName, String languageInterfaceName) {
    List<ASTCDMethod> handleMethods = new ArrayList<>();

    // generate handle(ASTX node) for all classes X
    handleMethods.addAll(astcdDefinition.getCDClassList()
        .stream()
        .map(c -> getASTHandleMethod(c, languageInterfaceName, handlerSimpleTypeName))
        .collect(Collectors.toList()));

    // generate handle(ASTX node) for all interfaces X
    handleMethods.addAll(astcdDefinition.getCDInterfaceList()
        .stream()
        .map(c -> getHandleASTMethod(c, languageInterfaceName, handlerSimpleTypeName))
        .collect(Collectors.toList()));

    return handleMethods;
  }

  protected ASTCDMethod   getASTHandleMethod(ASTCDClass astcdClass, String languageInterfaceName, String handlerSimpleTypeName) {
    ASTCDMethod handleMethod = visitorService.getVisitorMethod(HANDLE, getMCTypeFacade().createQualifiedType(astcdClass.getName()));
    List<String> superTypeList = new ArrayList<>();
    // super classes
    if (astcdClass.isPresentSuperclass() && !astcdClass.printSuperClass().isEmpty()) {
      superTypeList= visitorService.getAllSuperClassesTransitive(astcdClass);
    }
    // super interfaces
    superTypeList.addAll(visitorService.getAllSuperInterfacesTransitive(astcdClass));
    replaceTemplate(EMPTY_BODY, handleMethod,
        new TemplateHookPoint(HANDLE_AST_INHERITANCE_TEMPLATE,
            languageInterfaceName, handlerSimpleTypeName, superTypeList));
    return handleMethod;
  }

  protected ASTCDMethod getHandleASTMethod(ASTCDInterface astcdInterface, String languageInterfaceName, String handlerSimpleTypeName) {
    ASTCDMethod handleMethod = visitorService.getVisitorMethod(HANDLE, getMCTypeFacade().createQualifiedType(astcdInterface.getName()));
    replaceTemplate(EMPTY_BODY, handleMethod,
        new TemplateHookPoint(HANDLE_AST_INHERITANCE_TEMPLATE,
            languageInterfaceName, handlerSimpleTypeName, new ArrayList<>()));
    return handleMethod;
  }

  /**
   * Adds handle methods for the language specific scope and its artifact scope
   * if available.
   * 
   * @param astcdDefinition The input class diagram of the language
   * @param handlerSimpleTypeName The name of the language's basic visitor type
   * @return The corresponding handle methods for the scope
   */
  protected List<ASTCDMethod> getScopeHandleMethods(ASTCDDefinition astcdDefinition, String handlerSimpleTypeName) {
    List<ASTCDMethod> handleMethods = new ArrayList<ASTCDMethod>();
    List<String> superScopesTransitive = new ArrayList<String>();
    for (CDDefinitionSymbol cd : visitorService.getSuperCDsTransitive()) {
      superScopesTransitive.add(symbolTableService.getScopeInterfaceFullName(cd));
    }
    superScopesTransitive.add(I_SCOPE);
    
    // handle language scope
    ASTCDMethod handleScopeMethod = visitorService.getVisitorMethod(HANDLE, symbolTableService.getScopeInterfaceType());
    handleMethods.add(handleScopeMethod);
    replaceTemplate(EMPTY_BODY, handleScopeMethod,
        new TemplateHookPoint(HANDLE_SYMTAB_INHERITANCE_TEMPLATE,
            handlerSimpleTypeName, superScopesTransitive));
    
    // handle language artifact scope
    List<String> superScopesTransitiveForAS = new ArrayList<String>();
    superScopesTransitiveForAS.add(symbolTableService.getScopeInterfaceFullName());
    superScopesTransitiveForAS.addAll(superScopesTransitive);
    ASTCDMethod handleArtifactScopeMethod = visitorService.getVisitorMethod(HANDLE, symbolTableService.getArtifactScopeInterfaceType());
    handleMethods.add(handleArtifactScopeMethod);
    replaceTemplate(EMPTY_BODY, handleArtifactScopeMethod,
            new TemplateHookPoint(HANDLE_SYMTAB_INHERITANCE_TEMPLATE,
                    handlerSimpleTypeName, superScopesTransitiveForAS));

    return handleMethods;
  }

  /**
   * Adds handle methods for symbols of the language.
   * 
   * @param cdDefinition The input class diagram of the language
   * @param handlerSimpleTypeName The name of the language's basic visitor type
   * @return The corresponding handle methods for the symbols
   */
  protected List<ASTCDMethod> getSymbolHandleMethods(ASTCDDefinition cdDefinition, String handlerSimpleTypeName) {
    List<ASTCDMethod> handleMethods = new ArrayList<ASTCDMethod>();
    for (ASTCDType symbol : symbolTableService.getSymbolDefiningProds(cdDefinition)) {
      ASTCDMethod handleSybolMethod = visitorService.getVisitorMethod(HANDLE, symbolTableService.getSymbolTypeFromAstType(symbol));
      handleMethods.add(handleSybolMethod);
      replaceTemplate(EMPTY_BODY, handleSybolMethod, 
          new TemplateHookPoint(HANDLE_SYMTAB_INHERITANCE_TEMPLATE, 
              handlerSimpleTypeName, Arrays.asList(new String[] {I_SYMBOL})));
    }
    return handleMethods;
  }
}
