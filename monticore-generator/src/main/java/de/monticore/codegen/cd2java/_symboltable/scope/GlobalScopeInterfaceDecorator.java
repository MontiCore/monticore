/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.scope;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mccollectiontypes._ast.ASTMCSetType;
import net.sourceforge.plantuml.Log;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;

/**
 * creates a globalScope class from a grammar
 */
public class GlobalScopeInterfaceDecorator
    extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {

  protected final SymbolTableService symbolTableService;

  protected final MethodDecorator methodDecorator;

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> accessorDecorator;

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> mutatorDecorator;

  protected static final String ADAPTED_RESOLVING_DELEGATE = "adapted%sResolvingDelegate";

  protected static final String TEMPLATE_PATH = "_symboltable.iglobalscope.";



  /**
   * flag added to define if the GlobalScope interface was overwritten with the TOP mechanism
   * if top mechanism was used, must use setter to set flag true, before the decoration
   * is needed for different getRealThis method implementations
   */
  protected boolean isGlobalScopeInterfaceTop = false;

  public GlobalScopeInterfaceDecorator(final GlobalExtensionManagement glex,
      final SymbolTableService symbolTableService,
      final MethodDecorator methodDecorator) {
    super(glex);
    this.symbolTableService = symbolTableService;
    this.methodDecorator = methodDecorator;
    this.accessorDecorator = methodDecorator.getAccessorDecorator();
    this.mutatorDecorator = methodDecorator.getMutatorDecorator();
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit input) {
    String globalScopeInterfaceName = symbolTableService.getGlobalScopeInterfaceSimpleName();

    List<ASTCDType> symbolClasses = symbolTableService
        .getSymbolDefiningProds(input.getCDDefinition());

    List<ASTCDMethod> resolvingDelegateMethods = createAllResolvingDelegateAttributes(symbolClasses)
        .stream()
        .map(methodDecorator::decorate)
        .flatMap(List::stream)
        .collect(Collectors.toList());
    adjustResolvingDelegateMethodsForInterface(resolvingDelegateMethods);

    return CD4AnalysisMill.cDInterfaceBuilder()
        .setName(globalScopeInterfaceName)
        .setModifier(PUBLIC.build())
        .addAllInterfaces(getSuperGlobalScopeInterfaces())
        .addInterface(symbolTableService.getScopeInterfaceType())
        .addAllCDMethods(createCalculateModelNameMethods(symbolClasses))
        .addAllCDMethods(createModelFileExtensionAttributeMethods())
        .addAllCDMethods(createModelloaderAttributeMethods())
        .addCDMethod(createCacheMethod())
        .build();
  }

  private void adjustResolvingDelegateMethodsForInterface(List<ASTCDMethod> resolvingDelegateMethods) {
    for(ASTCDMethod method : resolvingDelegateMethods){
      if(method.getName().startsWith("set")){
        method.getModifier().setAbstract(true);
      }
      else if(method.getName().startsWith("get") && method.isEmptyCDParameters()){
        method.getModifier().setAbstract(true);
      }
    }
  }

  private List<ASTMCQualifiedType> getSuperGlobalScopeInterfaces() {
    List<ASTMCQualifiedType> result = new ArrayList<>();
    for (CDDefinitionSymbol superGrammar : symbolTableService.getSuperCDsDirect()) {
      if(!superGrammar.isPresentAstNode()){
        Log.error("0xA4323 Unable to load AST of '" +superGrammar.getFullName()
            + "' that is supergrammar of '" + symbolTableService.getCDName() + "'.");
        continue;
      }
      if (symbolTableService.hasStartProd(superGrammar.getAstNode())
          ||!symbolTableService.getSymbolDefiningSuperProds().isEmpty() ) {
        result.add(symbolTableService.getGlobalScopeInterfaceType(superGrammar));
      }
    }
    if (result.isEmpty()) {
      result.add(getMCTypeFacade().createQualifiedType(I_GLOBAL_SCOPE_TYPE));
    }
    return result;
  }

  protected List<ASTCDMethod> createModelFileExtensionAttributeMethods() {
    ASTCDMethod getMethod = getCDMethodFacade()
        .createMethod(PUBLIC_ABSTRACT, getMCTypeFacade().createStringType(), "getModelFileExtension");
    ASTCDMethod setMethod = getCDMethodFacade()
        .createMethod(PUBLIC_ABSTRACT,  "setModelFileExtension",
            getCDParameterFacade().createParameter(getMCTypeFacade().createStringType(), "modelFileExtension"));
    return Lists.newArrayList(getMethod, setMethod);
  }

  protected List<ASTCDMethod> createModelloaderAttributeMethods() {
    ASTCDMethod getMethod = getCDMethodFacade()
        .createMethod(PUBLIC_ABSTRACT, getMCTypeFacade().createStringType(), "getModelLoader");
    ASTCDMethod setMethod = getCDMethodFacade()
        .createMethod(PUBLIC_ABSTRACT,  "setModelLoader",
            getCDParameterFacade().createParameter(getMCTypeFacade().createStringType(), "modelLoader"));
    ASTCDMethod isPresentMethod = getCDMethodFacade()
        .createMethod(PUBLIC_ABSTRACT,  getMCTypeFacade().createBooleanType(), "isPresentModelLoader");
    ASTCDMethod setAbsentMethod = getCDMethodFacade()
        .createMethod(PUBLIC_ABSTRACT,  "setModelLoaderAbent");
    return Lists.newArrayList(getMethod, setMethod, isPresentMethod, setAbsentMethod);
  }

  protected List<ASTCDMethod> createImportsAttributeMethods() {
    ASTMCListType type = getMCTypeFacade().createListTypeOf(IMPORT_STATEMENT);
    ASTCDAttribute attr = getCDAttributeFacade() .createAttribute(PRIVATE, type, "imports");
    List<ASTCDMethod> methods = methodDecorator.decorate(attr).stream()
        .filter(m -> !(m.getName().equals("getImportList") || m.getName().equals("setImportList")))
        .collect(Collectors.toList());

    ASTCDMethod getMethod = getCDMethodFacade().createMethod(PUBLIC_ABSTRACT, type, "getImportList");
    methods.add(getMethod);

    ASTCDMethod setMethod = getCDMethodFacade().createMethod(PUBLIC_ABSTRACT,  "setImportList",
        getCDParameterFacade().createParameter(type, "imports"));
    methods.add(setMethod);

    return methods;
  }

  protected List<ASTCDAttribute> createAllResolvingDelegateAttributes(List<ASTCDType> symbolProds) {
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    List<ASTCDType> symbols = new ArrayList<>(symbolProds);
    symbols.addAll(symbolTableService.getSymbolDefiningSuperProds());
    for (ASTCDType symbolProd : symbolProds) {
      Optional<String> simpleName = symbolTableService.getDefiningSymbolSimpleName(symbolProd);
      if (simpleName.isPresent()) {
        String attrName = String.format(ADAPTED_RESOLVING_DELEGATE, simpleName.get());
        String symbolResolvingDelegateInterfaceTypeName = symbolTableService.
            getSymbolResolvingDelegateInterfaceFullName(symbolProd, symbolTableService.getCDSymbol());
        ASTMCType listType = getMCTypeFacade().createListTypeOf(symbolResolvingDelegateInterfaceTypeName);
        ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PROTECTED, listType, attrName);
        attributeList.add(attribute);
      }
    }
    return attributeList;
  }

  /**
   * This creates only an abstract method, because the implementation of the cache method requires
   * private attributes of the global scope class, such as e.g., the modelName2ModelLoaderCache
   * @return
   */
  protected ASTCDMethod createCacheMethod() {
    ASTCDParameter parameter = getCDParameterFacade().createParameter(getMCTypeFacade().createStringType(), CALCULATED_MODEL_NAME);
    ASTCDMethod cacheMethod = getCDMethodFacade().createMethod(PUBLIC_ABSTRACT, "cache", parameter);
    return cacheMethod;
  }

  protected List<ASTCDMethod> createCalculateModelNameMethods(List<ASTCDType> symbolProds) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDType symbolProd : symbolProds) {
      String simpleName = symbolTableService.removeASTPrefix(symbolProd);
      ASTMCSetType setTypeOfString = getMCTypeFacade().createSetTypeOf(String.class);
      ASTCDParameter nameParam = getCDParameterFacade().createParameter(String.class, NAME_VAR);
      ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, setTypeOfString,
          String.format("calculateModelNamesFor%s", simpleName), nameParam);
      this.replaceTemplate(EMPTY_BODY, method,
          new TemplateHookPoint(TEMPLATE_PATH + "CalculateModelNamesFor"));
      methodList.add(method);
    }
    return methodList;
  }

  public boolean isGlobalScopeInterfaceTop() {
    return isGlobalScopeInterfaceTop;
  }

  public void setGlobalScopeInterfaceTop(boolean globalScopeInterfaceTop) {
    isGlobalScopeInterfaceTop = globalScopeInterfaceTop;
  }

}
