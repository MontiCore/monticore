/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.serialization;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4code.CD4CodeMill;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.facade.CDParameterFacade;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.utils.Names;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.JSON_PRINTER;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.*;

/**
 * creates a SymbolTablePrinter class from a grammar
 */
public class Symbols2JsonDecorator extends AbstractDecorator {

  protected final SymbolTableService symbolTableService;

  protected final VisitorService visitorService;

  protected final MethodDecorator methodDecorator;

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> accessorDecorator;

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> mutatorDecorator;

  protected static final String TEMPLATE_PATH = "_symboltable.serialization.";

  protected static final String PRINTER_END_OBJECT = "printer.endObject();";

  protected static final String PRINTER_END_ARRAY = "printer.endArray();";

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
    String artifactScopeFullName = symbolTableService.getArtifactScopeFullName();
    String artifactScopeInterfaceFullName = symbolTableService.getArtifactScopeInterfaceFullName();
    String scopeClassFullName = symbolTableService.getScopeClassFullName();
    String deSerFullName = symbolTableService.getScopeDeSerFullName();
    List<ASTCDType> symbolDefiningProds = symbolTableService.getSymbolDefiningProds(symbolCD.getCDDefinition());
    String visitorFullName = visitorService.getVisitor2FullName();
    String traverserFullName = visitorService.getTraverserInterfaceFullName();
    String millName = visitorService.getMillFullName();

    List<ASTCDClass> symbolTypes = symbolCD.getCDDefinition().getCDClassList();
    List<ASTCDClass> scopeTypes = scopeCD.getCDDefinition().getCDClassList();

    ASTCDAttribute traverserAttribute = createTraverserAttribute(traverserFullName);

    ASTCDClass symbols2JsonClass = CD4CodeMill.cDClassBuilder()
            .setName(symbols2JsonName)
            .setModifier(PUBLIC.build())
            .addInterface(getMCTypeFacade().createQualifiedType(visitorFullName))
            .addCDAttribute(getCDAttributeFacade().createAttribute(PROTECTED, JSON_PRINTER, "printer"))
            .addCDMethod(createGetJsonPrinterMethod())
            .addCDMethod(createSetJsonPrinterMethod())
            .addCDAttribute(traverserAttribute)
            .addAllCDMethods(accessorDecorator.decorate(traverserAttribute))
            .addAllCDMethods(mutatorDecorator.decorate(traverserAttribute))
            .addAllCDConstructors(createConstructors(millName, traverserFullName, symbols2JsonName))
            .addCDMethod(createInitMethod())
            .addCDMethod(createGetSerializedStringMethod())
            .addAllCDMethods(createLoadMethods(artifactScopeInterfaceFullName, deSerFullName))
            .addCDMethod(createStoreMethod(artifactScopeInterfaceFullName, deSerFullName))
            .addAllCDMethods(createScopeVisitorMethods(scopeClassFullName, scopeInterfaceFullName, scopeCD, symbolDefiningProds))
            .addAllCDMethods(createSymbolVisitorMethods(symbolDefiningProds))
            .addAllCDMethods(createSymbolRuleMethods(symbolTypes))
            .addAllCDMethods(createScopeRuleMethods(scopeTypes, scopeClassFullName, scopeInterfaceFullName, artifactScopeInterfaceFullName))
            .addAllCDMethods(createArtifactScopeVisitorMethods(artifactScopeFullName, artifactScopeInterfaceFullName, scopeTypes))
            .addCDMethod(createPrintKindHierarchyMethod(symbolDefiningProds))
            .build();
    return symbols2JsonClass;
  }

  protected ASTCDAttribute createTraverserAttribute(String traverserFullName) {
    return getCDAttributeFacade()
            .createAttribute(PRIVATE, traverserFullName, "traverser");
  }

  protected List<ASTCDConstructor> createConstructors(String millName, String traverserFullName, String symbolTablePrinterName) {
    List<ASTCDConstructor> constructors = new ArrayList<>();

    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC, symbolTablePrinterName);
    StringBuilder sb = new StringBuilder("this(" + millName + ".traverser(), new " + JSON_PRINTER + "());\n");
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint(sb.toString()));
    constructors.add(constructor);

    List<ASTCDParameter> constructorParameters = new ArrayList<>();
    String traverserParam = "traverser";
    constructorParameters.add(getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(traverserFullName), traverserParam));
    String printerParam = "printer";
    constructorParameters.add(getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(JSON_PRINTER), printerParam));
    ASTCDConstructor constructorB = getCDConstructorFacade().createConstructor(PUBLIC, symbolTablePrinterName, constructorParameters);
    StringBuilder sb2 = new StringBuilder("this.printer = " + printerParam + ";\n");
    sb2.append("this.traverser = " + traverserParam + ";\n");
    sb2.append("init();\n");
    this.replaceTemplate(EMPTY_BODY, constructorB, new StringHookPoint(sb2.toString()));
    constructors.add(constructorB);
    return constructors;
  }

  protected ASTCDMethod createInitMethod() {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, "init");
    return method;
  }

  protected ASTCDMethod createGetJsonPrinterMethod() {
    ASTMCType type = getMCTypeFacade().createQualifiedType(JSON_PRINTER);
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, type, "getJsonPrinter");
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return this.printer;"));
    return method;
  }

  protected ASTCDMethod createSetJsonPrinterMethod() {
    ASTMCType type = getMCTypeFacade().createQualifiedType(JSON_PRINTER);
    ASTCDParameter parameter = getCDParameterFacade().createParameter(type, "printer");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, "setJsonPrinter", parameter);
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("this.printer=printer;"));
    return method;
  }

  protected ASTCDMethod createPrintKindHierarchyMethod(List<ASTCDType> symbolProds) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, "printKindHierarchy");
    List<ASTCDType> symbolProdsIncludingInherited = new ArrayList<>(symbolProds);
    symbolProdsIncludingInherited.addAll(symbolTableService.getSymbolDefiningSuperProds());
    Map<String, String> kindHierarchy = SymbolKindHierarchyCollector.calculateKindHierarchy(symbolProdsIncludingInherited, symbolTableService);
    HookPoint hp = new TemplateHookPoint(TEMPLATE_PATH + "symbols2Json.PrintKindHierarchy",
            kindHierarchy);
    this.replaceTemplate(EMPTY_BODY, method, hp);
    return method;
  }


  protected ASTCDMethod createGetSerializedStringMethod() {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createStringType(), "getSerializedString");
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return this.printer.getContent();"));
    return method;
  }

  protected List<ASTCDMethod> createScopeVisitorMethods(String scopeClassName, String scopeInterfaceName, ASTCDCompilationUnit scopeCD, List<ASTCDType> symbolProds) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    List<String> simpleSymbolNames = symbolProds.stream()
            .map(symbolTableService::removeASTPrefix)
            .collect(Collectors.toList());

    List<String> superScopeInterfaces = symbolTableService.getSuperCDsDirect().stream()
            .map(cd -> symbolTableService.getScopeInterfaceFullName(cd))
            .collect(Collectors.toList());

    for (ASTCDClass scopeClass : scopeCD.getCDDefinition().getCDClassList()) {
      ASTCDMethod visitMethod = visitorService.getVisitorMethod(VISIT, getMCTypeFacade().createQualifiedType(scopeInterfaceName));
      this.replaceTemplate(EMPTY_BODY, visitMethod, new TemplateHookPoint(TEMPLATE_PATH
              + "symbols2Json.VisitScope4STP", scopeInterfaceName, scopeClass.getName(), scopeClass.getCDAttributeList()));
      visitorMethods.add(visitMethod);

      ASTCDMethod endVisitMethod = visitorService.getVisitorMethod(END_VISIT, getMCTypeFacade().createQualifiedType(scopeInterfaceName));
      this.replaceTemplate(EMPTY_BODY, endVisitMethod, new StringHookPoint(PRINTER_END_ARRAY + "\n" + PRINTER_END_OBJECT));
      visitorMethods.add(endVisitMethod);
    }
    return visitorMethods;
  }

  protected List<ASTCDMethod> createArtifactScopeVisitorMethods(String artifactScopeClassName, String artifactScopeInterfaceName, List<ASTCDClass> scopeTypes) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDClass artScopeClass : scopeTypes) {
      ASTCDMethod visitMethod = visitorService.getVisitorMethod(VISIT, getMCTypeFacade().createQualifiedType(artifactScopeInterfaceName));
      this.replaceTemplate(EMPTY_BODY, visitMethod, new TemplateHookPoint(TEMPLATE_PATH
              + "symbols2Json.VisitArtifactScope", artifactScopeInterfaceName, artScopeClass.getName(), artScopeClass.getCDAttributeList()));
      visitorMethods.add(visitMethod);

      ASTCDMethod endVisitMethod = visitorService
              .getVisitorMethod(END_VISIT, getMCTypeFacade().createQualifiedType(artifactScopeInterfaceName));
      this.replaceTemplate(EMPTY_BODY, endVisitMethod, new StringHookPoint(PRINTER_END_ARRAY + "\n" + PRINTER_END_OBJECT));
      visitorMethods.add(endVisitMethod);
    }
    return visitorMethods;
  }

  protected List<ASTCDMethod> createSymbolVisitorMethods(List<ASTCDType> symbolProds) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();

    for (ASTCDType symbolProd : symbolProds) {
      String symbolFullName = symbolTableService.getSymbolFullName(symbolProd);
      String kind = symbolTableService.getSymbolFullName(symbolProd);
      ASTCDMethod visitMethod = visitorService.getVisitorMethod(VISIT, getMCTypeFacade().createQualifiedType(symbolFullName));
      this.replaceTemplate(EMPTY_BODY, visitMethod,
              new TemplateHookPoint(TEMPLATE_PATH + "symbols2Json.VisitSymbol", kind, symbolProd.getName(), symbolProd.getCDAttributeList()));
      visitorMethods.add(visitMethod);

      ASTCDMethod endVisitMethod = visitorService.getVisitorMethod(END_VISIT, getMCTypeFacade().createQualifiedType(symbolFullName));
      this.replaceTemplate(EMPTY_BODY, endVisitMethod, new StringHookPoint(PRINTER_END_OBJECT));
      visitorMethods.add(endVisitMethod);
      if (symbolTableService.hasSymbolSpannedScope(symbolProd)) {
        ASTCDMethod traverseMethod = visitorService.getVisitorMethod(TRAVERSE, getMCTypeFacade().createQualifiedType(symbolFullName));
        this.replaceTemplate(EMPTY_BODY, traverseMethod, new TemplateHookPoint(TEMPLATE_PATH + "symbols2Json.TraverseSymbol"));
        visitorMethods.add(traverseMethod);
      }
    }
    return visitorMethods;
  }

  protected List<ASTCDMethod> createScopeRuleMethods(List<ASTCDClass> scopeProds, String scopeClassFullName,
                                                     String scopeInterfaceFullName, String artifactScopeInterfaceFullName) {
    List<ASTCDMethod> methodsCreated = new ArrayList<>();

    ASTCDParameter scopeParam = CDParameterFacade.getInstance().createParameter(getMCTypeFacade().createQualifiedType(scopeInterfaceFullName), "node");
    ASTCDMethod serAddScopeAttrMethod = CDMethodFacade.getInstance().createMethod(PROTECTED, "serializeAdditionalScopeAttributes", scopeParam);
    methodsCreated.add(serAddScopeAttrMethod);

    ASTCDParameter artScopeParam = CDParameterFacade.getInstance().createParameter(getMCTypeFacade().createQualifiedType(artifactScopeInterfaceFullName), "node");
    ASTCDMethod serAddArtifactScopeAttrMethod = CDMethodFacade.getInstance().createMethod(PROTECTED, "serializeAdditionalArtifactScopeAttributes", artScopeParam);
    methodsCreated.add(serAddArtifactScopeAttrMethod);


    String scopeSimpleName = Names.getSimpleName(scopeClassFullName);
    for (ASTCDClass scopeProd : scopeProds) {
      String attrMethodPrefix = "serialize" + scopeSimpleName;
      for (ASTCDAttribute attr : scopeProd.getCDAttributeList()) {
        methodsCreated.add(createSerializeMethodForAttr(attrMethodPrefix, attr));
      }
    }

    return methodsCreated;
  }

  /**
   * for each symbol, creates methods for serializing each symbol attribute given via symbolrules
   * and a nmethod for serializing additional attributes of this symbol kinds.
   *
   * @param symbolProds
   * @return
   */
  protected List<ASTCDMethod> createSymbolRuleMethods(List<ASTCDClass> symbolProds) {
    List<ASTCDMethod> methodsCreated = new ArrayList<>();

    for (ASTCDClass symbolProd : symbolProds) {
      String symbolName = StringTransformations.capitalize(symbolProd.getName());
      String attrMethodPrefix = "serialize" + symbolName;
      for (ASTCDAttribute attr : symbolProd.getCDAttributeList()) {
        methodsCreated.add(createSerializeMethodForAttr(attrMethodPrefix, attr));
      }
      String symbolFullName = symbolTableService.getSymbolFullName(symbolProd);
      ASTCDParameter symbolParam = CDParameterFacade.getInstance().createParameter(getMCTypeFacade().createQualifiedType(symbolFullName), "node");
      ASTCDMethod serAddSymbolAttrMethod = CDMethodFacade.getInstance().createMethod(PROTECTED, "serializeAdditional" + symbolName + "SymbolAttributes", symbolParam);
      methodsCreated.add(serAddSymbolAttrMethod);
    }
    return methodsCreated;
  }

  protected ASTCDMethod createSerializeMethodForAttr(String methodNamePrefix, ASTCDAttribute attr) {
    String attribute = attr.getName();
    String methodName = methodNamePrefix + StringTransformations.capitalize(attribute);
    String operation = "serialize a complex attr";
    String returnValue = "";

    // isList? -> Begin/End array + methodCall in-between
    // elem autoSerializable? -> gen method that serializes or errorCode if elem complex
    // additional serialize Optional iff present
    ASTCDParameter serializeParameter = CDParameterFacade.getInstance().createParameter(attr);
    ASTCDMethod serializeAttrMethod = CDMethodFacade.getInstance().createMethod(PUBLIC, methodName, serializeParameter);

    if (isAutoSerialized(attr)) {
      if (isSerializedAsList(attr)) {
        serializeAsList(serializeAttrMethod, attr);
      } else if (isSerializedAsOptional(attr)) {
        this.replaceTemplate(EMPTY_BODY, serializeAttrMethod, new TemplateHookPoint(TEMPLATE_PATH
                + "symbols2Json.SerializeOptAttribute", attr));
      } else {
        this.replaceTemplate(EMPTY_BODY, serializeAttrMethod, new TemplateHookPoint(
                TEMPLATE_PATH + "PrintSimpleAttribute", attr.getName(), attr.getName()));
      }
    } else {
      String generatedError = symbolTableService.getGeneratedErrorCode(methodName);
      this.replaceTemplate(EMPTY_BODY, serializeAttrMethod, new TemplateHookPoint(
              TEMPLATE_PATH + "PrintComplexAttribute", attribute, methodName, operation, returnValue, generatedError));
    }
    return serializeAttrMethod;
  }

  protected void serializeAsList(ASTCDMethod method, ASTCDAttribute attr) {
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH
            + "symbols2Json.SerializeSimpleListAttribute", attr.getName()));
  }

  protected boolean isAutoSerialized(ASTCDAttribute attr) {
    String type = attr.getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter());

    if (isPrimitive(type)) {
      return true;
    }
    int lessThanPos = type.indexOf("<");
    // Determine if List or Optional with primitive values
    if (isSerializedAsList(attr) || isSerializedAsOptional(attr)) {
      // exclude '<' '>'
      return isPrimitive(type.substring(lessThanPos + 1, type.length() - 1));
    }
    // Some unknown type
    return false;
  }

  protected boolean isSerializedAsList(ASTCDAttribute attr) {
    String type = attr.printType();
    int lessThanPos = type.indexOf("<");
    if (lessThanPos > -1) {
      if (type.substring(0, lessThanPos).endsWith("List")) {
        return true;
      }
    }
    return false;
  }

  protected boolean isSerializedAsOptional(ASTCDAttribute attr) {
    String type = attr.printType();
    int lessThanPos = type.indexOf("<");
    if (lessThanPos > -1) {
      if (type.substring(0, lessThanPos).equals("Optional")) {
        return true;
      }
    }
    return false;
  }

  protected boolean isPrimitive(String type) {
    switch (type) {
      case "boolean":
      case "short":
      case "int":
      case "long":
      case "char":
      case "float":
      case "double":
      case "Boolean":
      case "Short":
      case "Int":
      case "Long":
      case "Char":
      case "Float":
      case "Double":
      case "String":
      case "java.lang.Boolean":
      case "java.lang.Character":
      case "java.lang.Short":
      case "java.lang.Integer":
      case "java.lang.Long":
      case "java.lang.Float":
      case "java.lang.String":
        return true;
      default:
        return false;
    }
  }

  protected ASTCDMethod createLoadMethod(ASTCDParameter parameter, String parameterInvocation,
                                         ASTMCQualifiedType returnType, String deSerFullName) {
    ASTCDMethod loadMethod = getCDMethodFacade()
            .createMethod(PUBLIC, returnType, "load", parameter);
    this.replaceTemplate(EMPTY_BODY, loadMethod,
            new TemplateHookPoint(TEMPLATE_PATH + "symbols2Json.Load",
                    parameterInvocation, deSerFullName));
    return loadMethod;
  }

  protected ASTCDMethod createStoreMethod(String artifactScopeName, String deSerFullName) {
    ASTCDParameter artifactScopeParam = getCDParameterFacade()
            .createParameter(getMCTypeFacade().createQualifiedType(artifactScopeName), "scope");
    ASTCDParameter fileNameParam = getCDParameterFacade()
            .createParameter(getMCTypeFacade().createStringType(), "fileName");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createStringType(), "store", artifactScopeParam, fileNameParam);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "symbols2Json.Store", deSerFullName));
    return method;
  }

  protected List<ASTCDMethod> createLoadMethods(String artifactScopeName, String deSerFullName) {
    ASTMCQualifiedType returnType = getMCTypeFacade().createQualifiedType(artifactScopeName);

    ASTCDParameter urlParam = getCDParameterFacade()
            .createParameter(getMCTypeFacade().createQualifiedType("java.net.URL"), "url");
    ASTCDMethod loadURLMethod = createLoadMethod(urlParam, "url", returnType, deSerFullName);

    ASTCDParameter readerParam = getCDParameterFacade()
            .createParameter(getMCTypeFacade().createQualifiedType("java.io.Reader"), "reader");
    ASTCDMethod loadReaderMethod = createLoadMethod(readerParam, "reader", returnType, deSerFullName);

    ASTCDParameter stringParam = getCDParameterFacade()
            .createParameter(getMCTypeFacade().createStringType(), "model");
    ASTCDMethod loadStringMethod = createLoadMethod(stringParam, "java.nio.file.Paths.get(model)",
            returnType, deSerFullName);

    return Lists.newArrayList(loadURLMethod, loadReaderMethod, loadStringMethod);
  }

}
