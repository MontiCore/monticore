/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_INTERFACE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.I_SCOPE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.I_SYMBOL;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.END_VISIT;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.GET_REAL_THIS;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.HANDLE;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.HANDLE_TEMPLATE;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.REAL_THIS;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.SET_REAL_THIS;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.TRAVERSE;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.TRAVERSE_SCOPE_TEMPLATE;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.TRAVERSE_TEMPLATE;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISIT;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDDefinition;
import de.monticore.cd.cd4analysis._ast.ASTCDEnum;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.cd4analysis._ast.ASTCDParameter;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4code.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.prettyprint.MCSimpleGenericTypesPrettyPrinter;

/**
 * creates a Visitor interface from a grammar
 */
public class Visitor2Decorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {
  
  protected final VisitorService visitorService;
  
  protected final SymbolTableService symbolTableService;
  
  protected boolean isTop;
  
  public Visitor2Decorator(final GlobalExtensionManagement glex,
                             final VisitorService visitorService,
                             final SymbolTableService symbolTableService) {
    super(glex);
    this.visitorService = visitorService;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit ast) {
    ASTCDCompilationUnit compilationUnit = visitorService.calculateCDTypeNamesWithASTPackage(ast);
    ASTMCType visitorType = this.visitorService.getVisitorType();
    ASTMCType astNodeType = getMCTypeFacade().createQualifiedType(AST_INTERFACE);
    Set<String> symbolNames = symbolTableService.retrieveSymbolNamesFromCD(visitorService.getCDSymbol());

    ASTCDInterface visitorInterface = CD4CodeMill.cDInterfaceBuilder()
        .setName(this.visitorService.getVisitor2SimpleName())
        .setModifier(PUBLIC.build())
//        .addCDMethod(addGetRealThisMethods(visitorType))
//        .addCDMethod(addSetRealThisMethods(visitorType))
        .addCDMethod(addEndVisitASTNodeMethods(astNodeType))
        .addCDMethod(addVisitASTNodeMethods(astNodeType))
        .addAllCDMethods(addASTNodeVisitorMethods(compilationUnit.getCDDefinition()))
        .addAllCDMethods(addISymbolVisitorMethods())
        .addAllCDMethods(addSymbolVisitorMethods(symbolNames))
        .addAllCDMethods(addIScopeVisitorMethods())
        .addAllCDMethods(addScopeVisitorMethods(getSymbolsTransitive(), ast.getCDDefinition()))
        .build();

    // add visitor methods, but no double signatures
//    List<ASTCDMethod> classMethods = addClassVisitorMethods(compilationUnit.getCDDefinition().getCDClassList());
//    for (ASTCDMethod classMethod : classMethods) {
//      if (!visitorService.isMethodAlreadyDefined(classMethod, visitorInterface.getCDMethodList())) {
//        visitorInterface.addCDMethod(classMethod);
//      }
//    }
//    List<ASTCDMethod> interfaceMethods = addInterfaceVisitorMethods(compilationUnit.getCDDefinition().getCDInterfaceList());
//    for (ASTCDMethod interfaceMethod : interfaceMethods) {
//      if (!visitorService.isMethodAlreadyDefined(interfaceMethod, visitorInterface.getCDMethodList())) {
//        visitorInterface.addCDMethod(interfaceMethod);
//      }
//    }
//    List<ASTCDMethod> enumMethods = addEnumVisitorMethods(compilationUnit.getCDDefinition().getCDEnumList(),
//        compilationUnit.getCDDefinition().getName());
//    for (ASTCDMethod enumMethod : enumMethods) {
//      if (!visitorService.isMethodAlreadyDefined(enumMethod, visitorInterface.getCDMethodList())) {
//        visitorInterface.addCDMethod(enumMethod);
//      }
//    }

    return visitorInterface;
  }

  protected ASTCDMethod addVisitASTNodeMethods(ASTMCType astNodeType) {
    return visitorService.getVisitorMethod(VisitorConstants.VISIT, astNodeType);
  }

  protected ASTCDMethod addEndVisitASTNodeMethods(ASTMCType astNodeType) {
    return visitorService.getVisitorMethod(VisitorConstants.END_VISIT, astNodeType);
  }
  
  /**
   * Adds visit and endVisit methods for a set of AST nodes.
   * 
   * @param cdDefinition The input class diagram
   * @return Created visitor methods to visit the AST nodes
   */
  protected List<ASTCDMethod> addASTNodeVisitorMethods(ASTCDDefinition cdDefinition) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    visitorMethods.addAll(addClassVisitorMethods(cdDefinition.getCDClassList()));
    visitorMethods.addAll(addInterfaceVisitorMethods(cdDefinition.getCDInterfaceList()));
    visitorMethods.addAll(addEnumVisitorMethods(cdDefinition.getCDEnumList(), cdDefinition.getName()));
    return visitorMethods;
  }

  protected ASTCDMethod addGetRealThisMethods(ASTMCType visitorType) {
    String hookPoint;
    if (!isTop()) {
      hookPoint = "return this;";
    } else {
      hookPoint = "return (" + visitorService.getVisitorSimpleName() + ")this;";
    }
    ASTCDMethod getRealThisMethod = this.getCDMethodFacade().createMethod(PUBLIC, visitorType, GET_REAL_THIS);
    this.replaceTemplate(EMPTY_BODY, getRealThisMethod, new StringHookPoint(hookPoint));
    return getRealThisMethod;
  }

  protected ASTCDMethod addSetRealThisMethods(ASTMCType visitorType) {
    ASTCDParameter visitorParameter = getCDParameterFacade().createParameter(visitorType, REAL_THIS);
    ASTCDMethod setRealThis = this.getCDMethodFacade().createMethod(PUBLIC, SET_REAL_THIS, visitorParameter);
    String generatedErrorCode = visitorService.getGeneratedErrorCode(visitorType.printType(
        new MCSimpleGenericTypesPrettyPrinter(new IndentPrinter())) + SET_REAL_THIS);
    this.replaceTemplate(EMPTY_BODY, setRealThis, new StringHookPoint(
        "    throw new UnsupportedOperationException(\"0xA7011"+generatedErrorCode+" The setter for realThis is " +
            "not implemented. You might want to implement a wrapper class to allow setting/getting realThis.\");\n"));
    return setRealThis;
  }

  protected List<ASTCDMethod> addClassVisitorMethods(List<ASTCDClass> astcdClassList) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDClass astcdClass : astcdClassList) {
      ASTMCType classType = getMCTypeFacade().createQualifiedType(astcdClass.getName());
      visitorMethods.add(addVisitMethod(classType));
      visitorMethods.add(addEndVisitMethod(classType));
    }
    return visitorMethods;
  }

  protected List<ASTCDMethod> addEnumVisitorMethods(List<ASTCDEnum> astcdEnumList, String definitionName) {
    // not for literals Enum
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDEnum astcdEnum : astcdEnumList) {
      if (!visitorService.isLiteralsEnum(astcdEnum, definitionName)) {
        ASTMCType enumType = getMCTypeFacade().createQualifiedType(astcdEnum.getName());
        visitorMethods.add(addVisitMethod(enumType));
        visitorMethods.add(addEndVisitMethod(enumType));
      }
    }
    return visitorMethods;
  }

  protected List<ASTCDMethod> addInterfaceVisitorMethods(List<ASTCDInterface> astcdInterfaceList) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDInterface astcdInterface : astcdInterfaceList) {
      ASTMCType interfaceType = getMCTypeFacade().createQualifiedType(astcdInterface.getName());
      visitorMethods.add(addVisitMethod(interfaceType));
      visitorMethods.add(addEndVisitMethod(interfaceType));
    }
    return visitorMethods;
  }

  protected ASTCDMethod addVisitMethod(ASTMCType astType) {
    return visitorService.getVisitorMethod(VISIT, astType);
  }

  protected ASTCDMethod addEndVisitMethod(ASTMCType astType) {
    return visitorService.getVisitorMethod(END_VISIT, astType);
  }

  protected ASTCDMethod addHandleMethod(ASTMCType astType, boolean traverse) {
    ASTCDMethod handleMethod = visitorService.getVisitorMethod(HANDLE, astType);
    this.replaceTemplate(EMPTY_BODY, handleMethod, new TemplateHookPoint(HANDLE_TEMPLATE, traverse));
    return handleMethod;
  }

  protected ASTCDMethod addTraversMethod(ASTMCType astType, ASTCDClass astcdClass) {
    ASTCDMethod traverseMethod = visitorService.getVisitorMethod(TRAVERSE, astType);
    boolean isScopeSpanningSymbol = symbolTableService.hasScopeStereotype(astcdClass.getModifier()) ||
        symbolTableService.hasInheritedScopeStereotype(astcdClass.getModifier());
    this.replaceTemplate(EMPTY_BODY, traverseMethod, new TemplateHookPoint(TRAVERSE_TEMPLATE, astcdClass, isScopeSpanningSymbol));
    return traverseMethod;
  }
  
  /**
   * Adds visit, endVisit, handle, and traverse methods for the general but
   * language specific symbol interface.
   * 
   * @return The corresponding visitor methods for the symbol interface
   */
  protected List<ASTCDMethod> addISymbolVisitorMethods() {
    List<ASTCDMethod> methodList = new ArrayList<>();
    ASTMCQualifiedType iScopeType = getMCTypeFacade().createQualifiedType(I_SYMBOL);
    methodList.add(visitorService.getVisitorMethod(VISIT, iScopeType));
    methodList.add(visitorService.getVisitorMethod(END_VISIT, iScopeType));
    return methodList;
  }
  
  /**
   * Adds visit, endVisit, handle, and traverse methods for a set of symbols.
   * 
   * @param symbolNames The input set of symbol names
   * @return Created visitor methods to visit the symbols
   */
  protected List<ASTCDMethod> addSymbolVisitorMethods(Set<String> symbolNames) {
    List<ASTCDMethod> visitorMethodList = new ArrayList<>();
    for (String symbolName : symbolNames) {
      ASTMCQualifiedType symbolType = getMCTypeFacade().createQualifiedType(symbolName);
      visitorMethodList.add(visitorService.getVisitorMethod(VISIT, symbolType));
      visitorMethodList.add(visitorService.getVisitorMethod(END_VISIT, symbolType));
    }
    return visitorMethodList;
  }
  
  /**
   * Adds visit, endVisit, handle, and traverse methods for the general but
   * language specific scope interface.
   * 
   * @return The corresponding visitor methods for the scope interface
   */
  protected List<ASTCDMethod> addIScopeVisitorMethods() {
    List<ASTCDMethod> methodList = new ArrayList<>();
    ASTMCQualifiedType iScopeType = getMCTypeFacade().createQualifiedType(I_SCOPE);
    methodList.add(visitorService.getVisitorMethod(VISIT, iScopeType));
    methodList.add(visitorService.getVisitorMethod(END_VISIT, iScopeType));
    return methodList;
  }
  
  /**
   * Adds visit, endVisit, handle, and traverse methods for scope, scope interface, and artifact
   * scope of a given class diagram definition by delegating the respective
   * names to createScopeVisitorMethods().
   * 
   * @param symbolsNameList The symbols to traverse
   * @param astcdDefinition The input class diagram
   * @return Created visitor methods to visit a (artifact) scope
   */
  protected List<ASTCDMethod> addScopeVisitorMethods(Set<String> symbolsNameList, ASTCDDefinition astcdDefinition) {
    ASTMCQualifiedType scopeType = symbolTableService.getScopeInterfaceType();
    ASTMCQualifiedType artifactScopeType = symbolTableService.getArtifactScopeInterfaceType();

    List<ASTCDMethod> methodList = new ArrayList<>();
    methodList.addAll(createScopeVisitorMethods(scopeType));
    // only create artifact scope methods if grammar contains productions or
    // refers to a starting production of a super grammar
    if (symbolTableService.hasProd(astcdDefinition) || symbolTableService.hasStartProd()) {
      methodList.addAll(createScopeVisitorMethods(artifactScopeType));
    }
    return methodList;
  }
  
  /**
   * Creates visit, endVisit, handle, and traverse methods for a given scope.
   * 
   * @param scopeName The scope name
   * @return A List of created methods to visit a scope
   */
  protected List<ASTCDMethod> createScopeVisitorMethods(ASTMCType scopeName) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    methodList.add(visitorService.getVisitorMethod(VISIT, scopeName));
    methodList.add(visitorService.getVisitorMethod(END_VISIT, scopeName));
    return methodList;
  }
  
  /**
   * Returns a set of qualified symbol names. Considers the complete inheritance
   * hierarchy and thus, contains local symbols as well as inherited symbols.
   * 
   * @return The set of all qualified symbol names
   */
  protected Set<String> getSymbolsTransitive() {
    Set<String> superSymbolNames = new HashSet<String>();
    // add local symbols
    superSymbolNames.addAll(symbolTableService.retrieveSymbolNamesFromCD(visitorService.getCDSymbol()));
    
    // add symbols of super CDs
    List<CDDefinitionSymbol> superCDsTransitive = visitorService.getSuperCDsTransitive();
    for (CDDefinitionSymbol cdSymbol : superCDsTransitive) {
      superSymbolNames.addAll(symbolTableService.retrieveSymbolNamesFromCD(cdSymbol));
    }
    return superSymbolNames;
  }
  
  public boolean isTop() {
    return isTop;
  }

  public void setTop(boolean top) {
    isTop = top;
  }
}
