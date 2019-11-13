package de.monticore.codegen.cd2java._symboltable.serialization;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4code._ast.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.factories.CDMethodFacade;
import de.monticore.codegen.cd2java.factories.CDParameterFacade;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

/**
 * creates a SymbolTablePrinter class from a grammar
 */
public class SymbolTablePrinterDecorator extends AbstractDecorator {

  protected final SymbolTableService symbolTableService;

  protected final VisitorService visitorService;

  protected static final String TEMPLATE_PATH = "_symboltable.serialization.";

  protected static final String PRINTER_END_OBJECT = "printer.endObject();";

  public SymbolTablePrinterDecorator(final GlobalExtensionManagement glex,
                                     final SymbolTableService symbolTableService,
                                     final VisitorService visitorService) {
    super(glex);
    this.symbolTableService = symbolTableService;
    this.visitorService = visitorService;
  }

  public ASTCDClass decorate(ASTCDCompilationUnit scopeCD, ASTCDCompilationUnit symbolCD) {
    String symbolTablePrinterName = symbolTableService.getSymbolTablePrinterSimpleName();
    String scopeInterfaceFullName = symbolTableService.getScopeInterfaceFullName();
    String artifactScopeFullName = symbolTableService.getArtifactScopeFullName();
    String scopeClassFullName = symbolTableService.getScopeClassFullName();
    List<ASTCDType> symbolDefiningProds = symbolTableService.getSymbolDefiningProds(symbolCD.getCDDefinition());
    String symbolVisitorFullName = visitorService.getSymbolVisitorFullName();
    String scopeVisitorFullName = visitorService.getScopeVisitorFullName();
    List<ASTCDClass> symbolTypes = symbolCD.getCDDefinition().getCDClassList();

    return CD4CodeMill.cDClassBuilder()
        .setName(symbolTablePrinterName)
        .setModifier(PUBLIC.build())
        .addInterface(getMCTypeFacade().createQualifiedType(symbolVisitorFullName))
        .addInterface(getMCTypeFacade().createQualifiedType(scopeVisitorFullName))
        .addCDAttribute(createJsonPrinterAttribute())
        .addCDMethod(createRealThisMethod(symbolTablePrinterName))
        .addCDMethod(createGetSerializedStringMethod())
        .addCDMethod(createFilterRelevantSubScopesMethod(scopeInterfaceFullName))
        .addCDMethod(createHasSymbolsInSubScopesMethod(scopeInterfaceFullName))
        .addCDMethod(createAddScopeSpanningSymbolMethod())
        .addAllCDMethods(createScopeVisitorMethods(artifactScopeFullName, scopeClassFullName,
            scopeInterfaceFullName, symbolDefiningProds, scopeCD.getCDDefinition()))
        .addAllCDMethods(createSymbolVisitorMethods(symbolDefiningProds))
        .addAllCDMethods(createSerializeSymbolruleMethods(symbolTypes))
        .build();
  }

  protected ASTCDAttribute createJsonPrinterAttribute() {
    ASTCDAttribute printerAttribute = getCDAttributeFacade().createAttribute(PROTECTED, JSON_PRINTER, "printer");
    this.replaceTemplate(VALUE, printerAttribute, new StringHookPoint("= new " + JSON_PRINTER + "()"));
    return printerAttribute;
  }

  protected ASTCDMethod createRealThisMethod(String symbolTablePrinterName) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createQualifiedType(symbolTablePrinterName), GET_REAL_THIS);
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return this;"));
    return method;
  }

  protected ASTCDMethod createGetSerializedStringMethod() {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createStringType(), "getSerializedString");
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return this.printer.getContent();"));
    return method;
  }

  protected ASTCDMethod createFilterRelevantSubScopesMethod(String scopeInterfaceName) {
    ASTMCListType listTypeOfScopeInterface = getMCTypeFacade().createListTypeOf(scopeInterfaceName);
    ASTCDParameter subScopesParam = getCDParameterFacade().createParameter(listTypeOfScopeInterface, "subScopes");
    ASTCDMethod method = getCDMethodFacade().createMethod(PROTECTED, listTypeOfScopeInterface, "filterRelevantSubScopes", subScopesParam);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "symbolTablePrinter.FilterRelevantSubScopes", scopeInterfaceName));
    return method;
  }

  protected ASTCDMethod createHasSymbolsInSubScopesMethod(String scopeInterfaceName) {
    ASTCDParameter scopeParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(scopeInterfaceName), SCOPE_VAR);
    ASTCDMethod method = getCDMethodFacade().createMethod(PROTECTED, getMCTypeFacade().createBooleanType(), "hasSymbolsInSubScopes", scopeParam);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "symbolTablePrinter.HasSymbolsInSubScopes", scopeInterfaceName));
    return method;
  }

  protected ASTCDMethod createAddScopeSpanningSymbolMethod() {
    ASTCDParameter scopeParam = getCDParameterFacade().createParameter(getMCTypeFacade().createOptionalTypeOf(I_SCOPE_SPANNING_SYMBOL), "spanningSymbol");
    ASTCDMethod method = getCDMethodFacade().createMethod(PROTECTED, "addScopeSpanningSymbol", scopeParam);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "symbolTablePrinter.AddScopeSpanningSymbol"));
    return method;
  }

  protected List<ASTCDMethod> createScopeVisitorMethods(String artifactScopeName, String scopeName,
                                                        String scopeInterfaceName, List<ASTCDType> symbolProds,
                                                        ASTCDDefinition astcdDefinition) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    if (symbolTableService.hasStartProd(astcdDefinition)) {
      visitorMethods.add(createVisitArtifactScopeMethod(artifactScopeName));
      visitorMethods.add(createEndVisitArtifactScopeMethod(artifactScopeName));
      visitorMethods.add(createTraverseArtifactScopeMethod(artifactScopeName, scopeName));
    }

    visitorMethods.add(createVisitScopeMethod(scopeName));
    visitorMethods.add(createEndVisitScopeMethod(scopeName));
    visitorMethods.add(createTraverseScopeMethod(scopeName, scopeInterfaceName, symbolProds));
    return visitorMethods;
  }

  protected ASTCDMethod createVisitArtifactScopeMethod(String artifactScopeFullName) {
    ASTCDMethod visitorMethod = visitorService.getVisitorMethod(VISIT, getMCTypeFacade().createQualifiedType(artifactScopeFullName));
    this.replaceTemplate(EMPTY_BODY, visitorMethod, new TemplateHookPoint(TEMPLATE_PATH + "symbolTablePrinter.VisitArtifactScope", artifactScopeFullName));
    return visitorMethod;
  }

  protected ASTCDMethod createVisitScopeMethod(String scopeName) {
    ASTCDMethod visitorMethod = visitorService.getVisitorMethod(VISIT, getMCTypeFacade().createQualifiedType(scopeName));
    this.replaceTemplate(EMPTY_BODY, visitorMethod, new TemplateHookPoint(TEMPLATE_PATH + "symbolTablePrinter.VisitScope", scopeName));
    return visitorMethod;
  }

  protected ASTCDMethod createTraverseArtifactScopeMethod(String artifactScopeFullName, String scopeFullName) {
    ASTCDMethod visitorMethod = visitorService.getVisitorMethod(TRAVERSE, getMCTypeFacade().createQualifiedType(artifactScopeFullName));
    this.replaceTemplate(EMPTY_BODY, visitorMethod, new StringHookPoint(" traverse((" + scopeFullName + ") node);"));
    return visitorMethod;
  }

  protected ASTCDMethod createTraverseScopeMethod(String scopeName, String scopeInterfaceName, List<ASTCDType> symbolProds) {
    List<String> simpleSymbolNames = symbolProds.stream()
        .map(symbolTableService::removeASTPrefix)
        .collect(Collectors.toList());
    ASTCDMethod visitorMethod = visitorService.getVisitorMethod(TRAVERSE, getMCTypeFacade().createQualifiedType(scopeName));
    this.replaceTemplate(EMPTY_BODY, visitorMethod, new TemplateHookPoint(
        TEMPLATE_PATH + "symbolTablePrinter.TraverseScope", simpleSymbolNames, scopeInterfaceName));
    return visitorMethod;
  }

  protected ASTCDMethod createEndVisitArtifactScopeMethod(String artifactScopeFullName) {
    ASTCDMethod visitorMethod = visitorService.getVisitorMethod(END_VISIT, getMCTypeFacade().createQualifiedType(artifactScopeFullName));
    this.replaceTemplate(EMPTY_BODY, visitorMethod, new StringHookPoint(PRINTER_END_OBJECT));
    return visitorMethod;
  }

  protected ASTCDMethod createEndVisitScopeMethod(String scopeName) {
    ASTCDMethod visitorMethod = visitorService.getVisitorMethod(END_VISIT, getMCTypeFacade().createQualifiedType(scopeName));
    this.replaceTemplate(EMPTY_BODY, visitorMethod, new StringHookPoint(PRINTER_END_OBJECT));
    return visitorMethod;
  }

  protected List<ASTCDMethod> createSymbolVisitorMethods(List<ASTCDType> symbolProds) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();

    for (ASTCDType symbolProd : symbolProds) {
      String symbolFullName = symbolTableService.getSymbolFullName(symbolProd);
      ASTCDMethod visitMethod = visitorService.getVisitorMethod(VISIT, getMCTypeFacade().createQualifiedType(symbolFullName));
      for(ASTCDAttribute a : symbolProd.getCDAttributeList()){

      }
      this.replaceTemplate(EMPTY_BODY, visitMethod, new TemplateHookPoint(TEMPLATE_PATH + "symbolTablePrinter.VisitSymbol", symbolProd, symbolFullName));

      visitorMethods.add(visitMethod);

      ASTCDMethod endVisitMethod = visitorService.getVisitorMethod(END_VISIT, getMCTypeFacade().createQualifiedType(symbolFullName));
      this.replaceTemplate(EMPTY_BODY, endVisitMethod, new StringHookPoint(PRINTER_END_OBJECT));
      visitorMethods.add(endVisitMethod);
    }
    return visitorMethods;
  }

  protected List<ASTCDMethod> createSerializeSymbolruleMethods(List<ASTCDClass> symbolProds) {
    List<ASTCDMethod> methodsCreated = new ArrayList<>();
    // 1. Create Method for whole symbol
    // 2. Create Methods for each attr of sym which are called in 1.
    for (ASTCDClass symbolProd : symbolProds) {
      String methodName = "serialize" + StringTransformations.capitalize(symbolProd.getName());
      String symbolFullName = symbolTableService.getSymbolFullName(symbolProd);
      ASTMCQualifiedType type = getMCTypeFacade().getInstance().createQualifiedType(symbolFullName);
      ASTCDParameter serializeParameter = CDParameterFacade.getInstance().createParameter(type, "node");
      ASTCDMethod serializeSymMethod = CDMethodFacade.getInstance().createMethod(PROTECTED, methodName, serializeParameter);

      List<ASTCDAttribute> listAttr = new ArrayList<>();
      List<ASTCDAttribute> nonListAttr = new ArrayList<>();
      //List<ASTCDAttribute> optAttr = new ArrayList<>();

      for (ASTCDAttribute attr : symbolProd.deepClone().getCDAttributeList()) {
        if (isSerializedAsList(attr)) {
          listAttr.add(attr);
        } /*else if (isSerializedAsOptional(attr)) {
          optAttr.add(attr);
        }*/else {
          nonListAttr.add(attr);
        }
        methodsCreated.add(createSerializeMethodForAttr(symbolProd.getName(), attr));
      }
      this.replaceTemplate(EMPTY_BODY, serializeSymMethod, new TemplateHookPoint(
              TEMPLATE_PATH + "symbolTablePrinter.SerializeSymbol", symbolProd.getName(), nonListAttr, listAttr));
      methodsCreated.add(serializeSymMethod);
    }
    return methodsCreated;
  }

  protected ASTCDMethod createSerializeMethodForAttr(String symName, ASTCDAttribute attr) {
    String attribute = attr.getName();
    String methodName = "serialize" + symName + StringTransformations.capitalize(attribute);
    String operation = "serialize a complex attr";
    String returnValue = "";

    // isList? -> Begin/End array + methodCall in-between
    // elem autoSerializable? -> gen method that serializes or errorCode if elem complex
    // additional serialize Optional iff present
    ASTCDParameter serializeParameter = CDParameterFacade.getInstance().createParameter(attr);
    ASTCDMethod serializeAttrMethod = CDMethodFacade.getInstance().createMethod(PROTECTED, methodName, serializeParameter);

    if (isAutoSerialized(attr)) {
      if (isSerializedAsList(attr)) {
        serializeAsList(serializeAttrMethod, attr);
      } else if (isSerializedAsOptional(attr)) {
        //serializeAsOptional(serializeAttrMethod, attr);
        this.replaceTemplate(EMPTY_BODY, serializeAttrMethod, new TemplateHookPoint(TEMPLATE_PATH
                + "symbolTablePrinter.SerializeOptAttribute", attr));
      } else {//if(!isSerializedAsList(attr) && !isSerializedAsOptional(attr)) {
        this.replaceTemplate(EMPTY_BODY, serializeAttrMethod, new TemplateHookPoint(
                TEMPLATE_PATH + "PrintSimpleAttribute", attr.getName(), attr.getName()));
      }
    } else {
      this.replaceTemplate(EMPTY_BODY, serializeAttrMethod, new TemplateHookPoint(
              TEMPLATE_PATH + "PrintComplexAttribute", attribute, methodName, operation, returnValue));
    }
    return serializeAttrMethod;
  }

  protected void serializeAsList(ASTCDMethod method, ASTCDAttribute attr) {
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH
            + "symbolTablePrinter.SerializeSimpleListAttribute", attr.getName()));
  }

  protected boolean isAutoSerialized(ASTCDAttribute attr) {
    String type = attr.printType().toLowerCase();

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
    String type = attr.printType().toLowerCase();
    int lessThanPos = type.indexOf("<");
    if (lessThanPos > -1) {
      if (type.substring(0, lessThanPos).endsWith("list")) {
        return true;
      }
    }
    return false;
  }

  protected boolean isSerializedAsOptional(ASTCDAttribute attr) {
    String type = attr.printType().toLowerCase();
    int lessThanPos = type.indexOf("<");
    if (lessThanPos > -1) {
      if (type.substring(0, lessThanPos).equals("optional")) {
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
      case "string":
        return true;
      default:
        return false;
    }
  }
}