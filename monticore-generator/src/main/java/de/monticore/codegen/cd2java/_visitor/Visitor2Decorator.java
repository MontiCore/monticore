/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import static de.monticore.codegen.cd2java.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_INTERFACE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.I_SCOPE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.I_SYMBOL;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.END_VISIT;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISIT;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

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
    ASTMCType astNodeType = getMCTypeFacade().createQualifiedType(AST_INTERFACE);
    Set<String> symbolNames = symbolTableService.retrieveSymbolNamesFromCD(visitorService.getCDSymbol());

    ASTCDInterface visitorInterface = CD4CodeMill.cDInterfaceBuilder()
        .setName(this.visitorService.getVisitor2SimpleName())
        .setModifier(PUBLIC.build())
        .addCDMethod(addEndVisitASTNodeMethods(astNodeType))
        .addCDMethod(addVisitASTNodeMethods(astNodeType))
        .addAllCDMethods(addASTNodeVisitorMethods(compilationUnit.getCDDefinition()))
        .addAllCDMethods(addISymbolVisitorMethods())
        .addAllCDMethods(addSymbolVisitorMethods(symbolNames))
        .addAllCDMethods(addIScopeVisitorMethods())
        .addAllCDMethods(addScopeVisitorMethods(getSymbolsTransitive(), ast.getCDDefinition()))
        .build();

    return visitorInterface;
  }

  /**
   * Adds the visit method for the default AST node interface.
   * 
   * @param astNodeType The type of the interface
   * @return The decorated visit method
   */
  protected ASTCDMethod addVisitASTNodeMethods(ASTMCType astNodeType) {
    return visitorService.getVisitorMethod(VisitorConstants.VISIT, astNodeType);
  }

  /**
   * Adds the endVisit method for the default AST node interface.
   * 
   * @param astNodeType The type of the interface
   * @return The decorated endVisit method
   */
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
    visitorMethods.addAll(addClassVisitorMethods(cdDefinition.getCDClassesList()));
    visitorMethods.addAll(addInterfaceVisitorMethods(cdDefinition.getCDInterfacesList()));
    visitorMethods.addAll(addEnumVisitorMethods(cdDefinition.getCDEnumsList(), cdDefinition.getName()));
    return visitorMethods;
  }

  /**
   * Creates visit and endVisit methods for a list of classes.
   * 
   * @param astcdClassList The input list of classes
   * @return The decorated visitor methods
   */
  protected List<ASTCDMethod> addClassVisitorMethods(List<ASTCDClass> astcdClassList) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDClass astcdClass : astcdClassList) {
      ASTMCType classType = getMCTypeFacade().createQualifiedType(astcdClass.getName());
      visitorMethods.add(addVisitMethod(classType));
      visitorMethods.add(addEndVisitMethod(classType));
    }
    return visitorMethods;
  }
  
  /**
   * Creates visit and endVisit methods for a list of interfaces.
   * 
   * @param astcdInterfaceList The input list of interfaces
   * @return The decorated visitor methods
   */
  protected List<ASTCDMethod> addInterfaceVisitorMethods(List<ASTCDInterface> astcdInterfaceList) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDInterface astcdInterface : astcdInterfaceList) {
      ASTMCType interfaceType = getMCTypeFacade().createQualifiedType(astcdInterface.getName());
      visitorMethods.add(addVisitMethod(interfaceType));
      visitorMethods.add(addEndVisitMethod(interfaceType));
    }
    return visitorMethods;
  }
  
  /**
   * Creates visit and endVisit methods for a list of enumerations.
   * 
   * @param astcdEnumList The input list of enumerations
   * @param definitionName Name of the base enumeration of each language
   * @return The decorated visitor methods
   */
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


  /**
   * Adds the visit method for a given AST type.
   * 
   * @param astType The input AST type
   * @return The decorated visit method
   */
  protected ASTCDMethod addVisitMethod(ASTMCType astType) {
    return visitorService.getVisitorMethod(VISIT, astType);
  }
  
  /**
   * Adds the endVisit method for a given AST type.
   * 
   * @param astType The input AST type
   * @return The decorated endVisit method
   */
  protected ASTCDMethod addEndVisitMethod(ASTMCType astType) {
    return visitorService.getVisitorMethod(END_VISIT, astType);
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
    methodList.addAll(createScopeVisitorMethods(artifactScopeType));

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
    List<DiagramSymbol> superCDsTransitive = visitorService.getSuperCDsTransitive();
    for (DiagramSymbol cdSymbol : superCDsTransitive) {
      superSymbolNames.addAll(symbolTableService.retrieveSymbolNamesFromCD(cdSymbol));
    }
    return superSymbolNames;
  }
}
