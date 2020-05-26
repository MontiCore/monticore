/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.I_SCOPE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.I_SYMBOL;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.HANDLE;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.HANDLE_AST_INHERITANCE_TEMPLATE;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.HANDLE_SYMTAB_INHERITANCE_TEMPLATE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDDefinition;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.cd4analysis._ast.ASTCDType;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4code.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;

/**
 * creates a InheritanceVisitor class from a grammar
 */
public class InheritanceVisitorDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {

  protected final VisitorService visitorService;
  
  protected final SymbolTableService symbolTableService;

  public InheritanceVisitorDecorator(final GlobalExtensionManagement glex,
                                     final VisitorService visitorService,
                                     final SymbolTableService symbolTableService) {
    super(glex);
    this.visitorService = visitorService;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit input) {
    ASTCDDefinition cdDefinition = input.deepClone().getCDDefinition();
    ASTCDCompilationUnit compilationUnit = visitorService.calculateCDTypeNamesWithASTPackage(input);
    String languageInterfaceName = visitorService.getLanguageInterfaceName();
    String visitorSimpleTypeName = visitorService.getVisitorSimpleName();

    return CD4CodeMill.cDInterfaceBuilder()
        .setName(visitorService.getInheritanceVisitorSimpleName())
        .setModifier(PUBLIC.build())
        .addInterface(visitorService.getVisitorType())
        .addAllInterfaces(visitorService.getSuperInheritanceVisitors())
        .addAllCDMethods(getASTHandleMethods(compilationUnit.getCDDefinition(), visitorSimpleTypeName, languageInterfaceName))
        .addAllCDMethods(getScopeHandleMethods(cdDefinition, visitorSimpleTypeName))
        .addAllCDMethods(getSymbolHandleMethods(cdDefinition, visitorSimpleTypeName))
        .build();
  }

  protected List<ASTCDMethod> getASTHandleMethods(ASTCDDefinition astcdDefinition, String visitorSimpleTypeName, String languageInterfaceName) {
    List<ASTCDMethod> handleMethods = new ArrayList<>();
    handleMethods.addAll(astcdDefinition.getCDClassList()
        .stream()
        .map(c -> getASTHandleMethod(c, languageInterfaceName, visitorSimpleTypeName))
        .collect(Collectors.toList()));

    handleMethods.addAll(astcdDefinition.getCDInterfaceList()
        .stream()
        .map(c -> getHandleASTMethod(c, languageInterfaceName, visitorSimpleTypeName))
        .collect(Collectors.toList()));

    return handleMethods;
  }

  protected ASTCDMethod getASTHandleMethod(ASTCDClass astcdClass, String languageInterfaceName, String visitorSimpleTypeName) {
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
            languageInterfaceName, visitorSimpleTypeName, superTypeList));
    return handleMethod;
  }

  protected ASTCDMethod getHandleASTMethod(ASTCDInterface astcdInterface, String languageInterfaceName, String visitorSimpleTypeName) {
    ASTCDMethod handleMethod = visitorService.getVisitorMethod(HANDLE, getMCTypeFacade().createQualifiedType(astcdInterface.getName()));
    replaceTemplate(EMPTY_BODY, handleMethod,
        new TemplateHookPoint(HANDLE_AST_INHERITANCE_TEMPLATE,
            languageInterfaceName, visitorSimpleTypeName, new ArrayList<>()));
    return handleMethod;
  }

  /**
   * Adds handle methods for the language specific scope and its artifact scope
   * if available.
   * 
   * @param astcdDefinition The input class diagram of the language
   * @param visitorSimpleTypeName The name of the language's basic visitor type
   * @return The corresponding handle methods for the scope
   */
  protected List<ASTCDMethod> getScopeHandleMethods(ASTCDDefinition astcdDefinition, String visitorSimpleTypeName) {
    List<ASTCDMethod> handleMethods = new ArrayList<ASTCDMethod>();
    List<String> superScopesTransitive = new ArrayList<String>();
    for (CDDefinitionSymbol cd : visitorService.getSuperCDsTransitive()) {
      if (symbolTableService.hasProd(cd.getAstNode()) || symbolTableService.hasStartProd(cd.getAstNode())) {
        superScopesTransitive.add(symbolTableService.getScopeInterfaceFullName(cd));
      }
    }
    superScopesTransitive.add(I_SCOPE);
    
    // handle language scope
    ASTCDMethod handleScopeMethod = visitorService.getVisitorMethod(HANDLE, symbolTableService.getScopeType());
    handleMethods.add(handleScopeMethod);
    replaceTemplate(EMPTY_BODY, handleScopeMethod,
        new TemplateHookPoint(HANDLE_SYMTAB_INHERITANCE_TEMPLATE,
            visitorSimpleTypeName, superScopesTransitive));
    
    // handle language artifact scope
    if (symbolTableService.hasProd(astcdDefinition) || symbolTableService.hasStartProd()) {
      List<String> superScopesTransitiveForAS = new ArrayList<String>();
      superScopesTransitiveForAS.add(symbolTableService.getScopeInterfaceFullName());
      superScopesTransitiveForAS.addAll(superScopesTransitive);
      ASTCDMethod handleArtifactScopeMethod = visitorService.getVisitorMethod(HANDLE, symbolTableService.getArtifactScopeType());
      handleMethods.add(handleArtifactScopeMethod);
      replaceTemplate(EMPTY_BODY, handleArtifactScopeMethod, 
          new TemplateHookPoint(HANDLE_SYMTAB_INHERITANCE_TEMPLATE, 
              visitorSimpleTypeName, superScopesTransitiveForAS));
    }
    
    return handleMethods;
  }

  /**
   * Adds handle methods for symbols of the language.
   * 
   * @param astcdDefinition The input class diagram of the language
   * @param visitorSimpleTypeName The name of the language's basic visitor type
   * @return The corresponding handle methods for the symbols
   */
  protected List<ASTCDMethod> getSymbolHandleMethods(ASTCDDefinition cdDefinition, String visitorSimpleTypeName) {
    List<ASTCDMethod> handleMethods = new ArrayList<ASTCDMethod>();
    for (ASTCDType symbol : symbolTableService.getSymbolDefiningProds(cdDefinition)) {
      ASTCDMethod handleSybolMethod = visitorService.getVisitorMethod(HANDLE, symbolTableService.getSymbolTypeFromAstType(symbol));
      handleMethods.add(handleSybolMethod);
      replaceTemplate(EMPTY_BODY, handleSybolMethod, 
          new TemplateHookPoint(HANDLE_SYMTAB_INHERITANCE_TEMPLATE, 
              visitorSimpleTypeName, Arrays.asList(new String[] {I_SYMBOL})));
    }
    return handleMethods;
  }
}
