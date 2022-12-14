/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import com.google.common.collect.Lists;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.se_rwth.commons.Joiners;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.ANNOTATIONS;
import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CoreTemplates.createAnnotationsHookPoint;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
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
    String languageInterfaceName = visitorService.getLanguageInterfaceName();
    String handlerSimpleName = visitorService.getHandlerSimpleName();

    ASTCDAttribute traverserAttribute = getCDAttributeFacade().createAttribute(PROTECTED.build(), visitorService.getTraverserInterfaceType(), TRAVERSER);
    List<ASTCDMethod> traverserMethods = methodDecorator.decorate(traverserAttribute);

    ASTCDClass cdClass = CD4CodeMill.cDClassBuilder()
        .setName(visitorService.getInheritanceHandlerSimpleName())
        .setModifier(PUBLIC.build())
        .setCDInterfaceUsage(CD4CodeMill.cDInterfaceUsageBuilder().addInterface(visitorService.getHandlerType()).build())
        .addCDMember(traverserAttribute)
        .addAllCDMembers(traverserMethods)
        .addAllCDMembers(getASTHandleMethods(input.getCDDefinition(), handlerSimpleName, languageInterfaceName))
        .addAllCDMembers(getScopeHandleMethods(input.getCDDefinition(), handlerSimpleName))
        .addAllCDMembers(getSymbolHandleMethods(input.getCDDefinition(), handlerSimpleName))
        .build();
    
    this.replaceTemplate(ANNOTATIONS, cdClass, createAnnotationsHookPoint(cdClass.getModifier()));
    CD4C.getInstance().addImport(cdClass, "de.monticore.ast.ASTNode");
    CD4C.getInstance().addImport(cdClass, "de.monticore.ast.ASTCNode");
    return cdClass;
  }

  protected List<ASTCDMethod> getASTHandleMethods(ASTCDDefinition astcdDefinition, String handlerSimpleTypeName, String languageInterfaceName) {
    List<ASTCDMethod> handleMethods = new ArrayList<>();

    // generate handle(ASTX node) for all classes X
    handleMethods.addAll(astcdDefinition.getCDClassesList()
        .stream()
        .map(c -> getASTHandleMethod(c, languageInterfaceName, handlerSimpleTypeName))
        .collect(Collectors.toList()));

    // generate handle(ASTX node) for all interfaces X
    handleMethods.addAll(astcdDefinition.getCDInterfacesList()
        .stream()
        .map(c -> getHandleASTMethod(c, languageInterfaceName, handlerSimpleTypeName))
        .collect(Collectors.toList()));

    return handleMethods;
  }

  protected ASTCDMethod   getASTHandleMethod(ASTCDClass astcdClass, String languageInterfaceName, String handlerSimpleTypeName) {
    ASTCDMethod handleMethod = visitorService.getVisitorMethod(HANDLE,
            getMCTypeFacade().createQualifiedType(Joiners.DOT.join(visitorService.getASTPackage(), astcdClass.getName())));
    List<String> superTypeList = new ArrayList<>();
    // super classes
    if (astcdClass.isPresentCDExtendUsage() && !astcdClass.printSuperclasses().isEmpty()) {
      superTypeList= visitorService.getAllSuperClassesTransitive(astcdClass);
    }
    // super interfaces
    superTypeList.addAll(visitorService.getAllSuperInterfacesTransitive(astcdClass.getSymbol()));
    replaceTemplate(EMPTY_BODY, handleMethod,
        new TemplateHookPoint(HANDLE_AST_INHERITANCE_TEMPLATE,
            languageInterfaceName, handlerSimpleTypeName, superTypeList));
    return handleMethod;
  }

  protected ASTCDMethod getHandleASTMethod(ASTCDInterface astcdInterface, String languageInterfaceName, String handlerSimpleTypeName) {
    ASTCDMethod handleMethod = visitorService.getVisitorMethod(HANDLE,
            getMCTypeFacade().createQualifiedType(Joiners.DOT.join(visitorService.getASTPackage(), astcdInterface.getName())));
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
    for (DiagramSymbol cd : visitorService.getSuperCDsTransitive()) {
      superScopesTransitive.add(symbolTableService.getScopeInterfaceFullName(cd));
    }
    // remove last element as it is covered by the handle call of the original handler
    superScopesTransitive.remove(superScopesTransitive.size() - 1);
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
      List<String> superList = new ArrayList<>();
      boolean hasScope = symbolTableService.hasScopeStereotype(symbol.getModifier());
      boolean hasInheritedSymbol = symbolTableService.hasInheritedSymbolStereotype(symbol.getModifier());
      boolean hasInheritedScope = symbolTableService.hasInheritedScopeStereotype(symbol.getModifier());
      ASTCDMethod handleSymbolMethod = visitorService.getVisitorMethod(HANDLE, symbolTableService.getSymbolTypeFromAstType(symbol));
      handleMethods.add(handleSymbolMethod);
      if (hasInheritedSymbol) {
        for (String superSymbol: symbolTableService.getInheritedSymbolPropertyTypes(Lists.newArrayList(symbol)).values()) {
          superList.add(superSymbol);
        }
      }
      superList.add(symbolTableService.getCommonSymbolInterfaceFullName());
      if (hasScope || hasInheritedScope) {
        superList.add(I_SCOPE_SPANNING_SYMBOL);
      }
      superList.add(I_SYMBOL);
      replaceTemplate(EMPTY_BODY, handleSymbolMethod,
          new TemplateHookPoint(HANDLE_SYMTAB_INHERITANCE_TEMPLATE, 
              handlerSimpleTypeName, superList));
    }
    return handleMethods;
  }
}
