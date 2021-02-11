/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.serialization;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.monticore.cdbasis._ast.*;
import de.monticore.cd4codebasis._ast.*;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static de.monticore.codegen.cd2java.CDModifier.*;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.END_VISIT;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISIT;

/**
 * creates a Symbols2Json class from a grammar
 */
public class Symbols2JsonDecorator extends AbstractDecorator {

  protected final SymbolTableService symbolTableService;

  protected final VisitorService visitorService;

  protected final MethodDecorator methodDecorator;

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> accessorDecorator;

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> mutatorDecorator;

  protected static final String TEMPLATE_PATH = "_symboltable.serialization.";

  public Symbols2JsonDecorator(final GlobalExtensionManagement glex,
                               final SymbolTableService symbolTableService,
                               final VisitorService visitorService,
                               final MethodDecorator methodDecorator) {
    super(glex);
    this.symbolTableService = symbolTableService;
    this.visitorService = visitorService;
    this.methodDecorator = methodDecorator;
    this.accessorDecorator = methodDecorator.getAccessorDecorator();
    this.mutatorDecorator = methodDecorator.getMutatorDecorator();

  }

  public ASTCDClass decorate(ASTCDCompilationUnit scopeCD, ASTCDCompilationUnit symbolCD) {
    String symbols2JsonName = symbolTableService.getSymbols2JsonSimpleName();
    String scopeInterfaceFullName = symbolTableService.getScopeInterfaceFullName();
    String artifactScopeInterfaceFullName = symbolTableService.getArtifactScopeInterfaceFullName();
    List<ASTCDType> symbolDefiningProds = symbolTableService.getSymbolDefiningProds(symbolCD.getCDDefinition());
    String visitorFullName = visitorService.getVisitor2FullName();
    String traverserFullName = visitorService.getTraverserInterfaceFullName();
    String millName = visitorService.getMillFullName();
    List<DiagramSymbol> superGrammars = symbolTableService.getSuperCDsTransitive();

    ASTCDAttribute traverserAttribute = createTraverserAttribute(traverserFullName);

    ASTCDClass symbols2JsonClass = CD4CodeMill.cDClassBuilder()
            .setName(symbols2JsonName)
            .setModifier(PUBLIC.build())
            .addInterface(getMCTypeFacade().createQualifiedType(visitorFullName))
            .addAllCDAttributes(createDeSerAttrs(symbolDefiningProds))
            .addCDAttribute(getCDAttributeFacade().createAttribute(PROTECTED.build(), JSON_PRINTER, "printer"))
            .addCDMethod(createGetJsonPrinterMethod())
            .addCDMethod(createSetJsonPrinterMethod())
            .addCDAttribute(traverserAttribute)
            .addAllCDMethods(accessorDecorator.decorate(traverserAttribute))
            .addAllCDMethods(mutatorDecorator.decorate(traverserAttribute))
            .addAllCDConstructors(createConstructors(millName, traverserFullName, symbols2JsonName, superGrammars))
            .addCDMethod(createInitMethod(scopeInterfaceFullName, symbolDefiningProds))
            .addCDMethod(createGetSerializedStringMethod())
            .addAllCDMethods(createLoadMethods(artifactScopeInterfaceFullName))
            .addCDMethod(createStoreMethod(artifactScopeInterfaceFullName))
            .addAllCDMethods(createScopeVisitorMethods(scopeInterfaceFullName, symbols2JsonName))
            .addAllCDMethods(createSymbolVisitorMethods(symbolDefiningProds, symbols2JsonName))
             .addAllCDMethods(createArtifactScopeVisitorMethods(artifactScopeInterfaceFullName, symbols2JsonName))
            .build();
    return symbols2JsonClass;
  }

  protected ASTCDAttribute createTraverserAttribute(String traverserFullName) {
    return getCDAttributeFacade()
            .createAttribute(PRIVATE.build(), traverserFullName, "traverser");
  }

  protected List<ASTCDConstructor> createConstructors(String millName, String traverserFullName, String symbolTablePrinterName, List<DiagramSymbol> superGrammars) {
    List<ASTCDConstructor> constructors = new ArrayList<>();

    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), symbolTablePrinterName);
    StringBuilder sb = new StringBuilder("this(" + millName + ".traverser(), new " + JSON_PRINTER + "());\n");
    sb.append(  "traverser.add4"+symbolTableService.getCDName()+"(this);\n");
    for(DiagramSymbol s: superGrammars){
      String s2j = symbolTableService.getSymbols2JsonFullName(s);
      sb.append(  "traverser.add4"+s.getName()+"(new "+s2j+"(getTraverser(), getJsonPrinter()));\n");
    }

    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint(sb.toString()));
    constructors.add(constructor);

    List<ASTCDParameter> constructorParameters = new ArrayList<>();
    String traverserParam = "traverser";
    constructorParameters.add(getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(traverserFullName), traverserParam));
    String printerParam = "printer";
    constructorParameters.add(getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(JSON_PRINTER), printerParam));
    ASTCDConstructor constructorB = getCDConstructorFacade().createConstructor(PUBLIC.build(), symbolTablePrinterName, constructorParameters);
    StringBuilder sb2 = new StringBuilder("this.printer = " + printerParam + ";\n");
    sb2.append("this.traverser = " + traverserParam + ";\n");
    sb2.append("init();");
    this.replaceTemplate(EMPTY_BODY, constructorB, new StringHookPoint(sb2.toString()));
    constructors.add(constructorB);
    return constructors;
  }

  protected ASTCDMethod createInitMethod(String scopeFullName, List<ASTCDType> prods) {
    ASTCDMethod initMethod = getCDMethodFacade().createMethod(PUBLIC.build(), "init");
    String globalScope = symbolTableService.getGlobalScopeInterfaceFullName();
    String millName = symbolTableService.getMillFullName();

    Map<String, String> deSerMap = Maps.newHashMap();
    for (ASTCDType prod : prods) {
      deSerMap.put(symbolTableService.getSymbolDeSerSimpleName(prod), symbolTableService.getSymbolFullName(prod));
    }
    this.replaceTemplate(EMPTY_BODY, initMethod,
            new TemplateHookPoint(TEMPLATE_PATH + "symbols2Json.Init", globalScope,
                I_DE_SER, scopeFullName, millName, deSerMap));
    return initMethod;
  }

  protected List<ASTCDAttribute> createDeSerAttrs(List<ASTCDType> prods) {
    List<ASTCDAttribute> attrList = Lists.newArrayList();
    attrList.add(getCDAttributeFacade().createAttribute(PROTECTED.build(), I_DE_SER, "scopeDeSer"));
    for (ASTCDType prod : prods) {
      String name = StringTransformations.uncapitalize(symbolTableService.getSymbolDeSerSimpleName(prod));
      attrList.add(getCDAttributeFacade().createAttribute(PROTECTED.build(), symbolTableService.getSymbolDeSerFullName(prod), name));
    }
    return attrList;
  }

  protected ASTCDMethod createGetJsonPrinterMethod() {
    ASTMCType type = getMCTypeFacade().createQualifiedType(JSON_PRINTER);
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), type, "getJsonPrinter");
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return this.printer;"));
    return method;
  }

  protected ASTCDMethod createSetJsonPrinterMethod() {
    ASTMCType type = getMCTypeFacade().createQualifiedType(JSON_PRINTER);
    ASTCDParameter parameter = getCDParameterFacade().createParameter(type, "printer");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), "setJsonPrinter", parameter);
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("this.printer=printer;"));
    return method;
  }

  protected ASTCDMethod createGetSerializedStringMethod() {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), getMCTypeFacade().createStringType(), "getSerializedString");
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return getJsonPrinter().getContent();"));
    return method;
  }

  protected List<ASTCDMethod> createScopeVisitorMethods(String scopeInterfaceName, String symbols2Json) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();

    ASTCDMethod visitMethod = visitorService.getVisitorMethod(VISIT, getMCTypeFacade().createQualifiedType(scopeInterfaceName));
    this.replaceTemplate(EMPTY_BODY, visitMethod, new TemplateHookPoint(TEMPLATE_PATH
            + "symbols2Json.VisitScope4STP", symbols2Json));
    visitorMethods.add(visitMethod);

    ASTCDMethod endVisitMethod = visitorService.getVisitorMethod(END_VISIT, getMCTypeFacade().createQualifiedType(scopeInterfaceName));
    this.replaceTemplate(EMPTY_BODY, endVisitMethod, new TemplateHookPoint(TEMPLATE_PATH
            + "symbols2Json.EndVisit4Scope", I_SCOPE, symbols2Json));
    visitorMethods.add(endVisitMethod);

    return visitorMethods;
  }

  protected List<ASTCDMethod> createArtifactScopeVisitorMethods(String artifactScopeInterfaceName, String symbols2Json) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    ASTCDMethod visitMethod = visitorService.getVisitorMethod(VISIT, getMCTypeFacade().createQualifiedType(artifactScopeInterfaceName));
    this.replaceTemplate(EMPTY_BODY, visitMethod, new TemplateHookPoint(TEMPLATE_PATH
            + "symbols2Json.VisitArtifactScope", symbols2Json));
    visitorMethods.add(visitMethod);

    ASTCDMethod endVisitMethod = visitorService
            .getVisitorMethod(END_VISIT, getMCTypeFacade().createQualifiedType(artifactScopeInterfaceName));
    this.replaceTemplate(EMPTY_BODY, endVisitMethod, new TemplateHookPoint(TEMPLATE_PATH
            + "symbols2Json.EndVisit4Scope", I_ARTIFACT_SCOPE_TYPE, symbols2Json));
    visitorMethods.add(endVisitMethod);

    return visitorMethods;
  }

  protected List<ASTCDMethod> createSymbolVisitorMethods(List<ASTCDType> symbolProds, String symbols2Json) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();

    for (ASTCDType symbolProd : symbolProds) {
      String symbolFullName = symbolTableService.getSymbolFullName(symbolProd);
      ASTCDMethod visitMethod = visitorService.getVisitorMethod(VISIT, getMCTypeFacade().createQualifiedType(symbolFullName));
      this.replaceTemplate(EMPTY_BODY, visitMethod,
              new TemplateHookPoint(TEMPLATE_PATH + "symbols2Json.VisitSymbol",
                      symbolProd.getName(), symbols2Json));
      visitorMethods.add(visitMethod);
    }
    return visitorMethods;
  }

  protected ASTCDMethod createLoadMethod(String artifactScopeName, ASTCDParameter parameter, String parameterInvocation,
                                         ASTMCQualifiedType returnType) {
    ASTCDMethod loadMethod = getCDMethodFacade()
            .createMethod(PUBLIC.build(), returnType, "load", parameter);
    this.replaceTemplate(EMPTY_BODY, loadMethod,
            new TemplateHookPoint(TEMPLATE_PATH + "symbols2Json.Load", artifactScopeName,
                    parameterInvocation));
    return loadMethod;
  }

  protected ASTCDMethod createStoreMethod(String artifactScopeName) {
    ASTCDParameter artifactScopeParam = getCDParameterFacade()
            .createParameter(getMCTypeFacade().createQualifiedType(artifactScopeName), "scope");
    ASTCDParameter fileNameParam = getCDParameterFacade()
            .createParameter(getMCTypeFacade().createStringType(), "fileName");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), getMCTypeFacade().createStringType(), "store", artifactScopeParam, fileNameParam);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "symbols2Json.Store"));
    return method;
  }

  protected List<ASTCDMethod> createLoadMethods(String asName) {
    ASTMCQualifiedType returnType = getMCTypeFacade().createQualifiedType(asName);

    ASTCDParameter urlParam = getCDParameterFacade()
            .createParameter(getMCTypeFacade().createQualifiedType("java.net.URL"), "url");
    ASTCDMethod loadURLMethod = createLoadMethod(asName, urlParam, "url", returnType);

    ASTCDParameter readerParam = getCDParameterFacade()
            .createParameter(getMCTypeFacade().createQualifiedType("java.io.Reader"), "reader");
    ASTCDMethod loadReaderMethod = createLoadMethod(asName, readerParam, "reader", returnType);

    ASTCDParameter stringParam = getCDParameterFacade()
            .createParameter(getMCTypeFacade().createStringType(), "model");
    ASTCDMethod loadStringMethod = createLoadMethod(asName, stringParam, "java.nio.file.Paths.get(model)",
            returnType);

    return Lists.newArrayList(loadURLMethod, loadReaderMethod, loadStringMethod);
  }

}
