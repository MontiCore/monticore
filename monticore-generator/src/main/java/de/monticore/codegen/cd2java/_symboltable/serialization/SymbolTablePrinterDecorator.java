/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.serialization;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4code.CD4CodeMill;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.facade.CDParameterFacade;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCFullGenericTypesPrettyPrinter;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.*;

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
    String visitorFullName = visitorService.getVisitorFullName();
    List<ASTCDClass> symbolTypes = symbolCD.getCDDefinition().getCDClassList();
    List<ASTCDClass> scopeTypes = scopeCD.getCDDefinition().getCDClassList();
    List<String> delegateNames = getDelegateClassNames();
    List<ASTCDAttribute> symbolTablePrinterDelegates = getSymbolTablePrinterDelegates(delegateNames);

    ASTCDClass symbolTablePrinterClass = CD4CodeMill.cDClassBuilder()
        .setName(symbolTablePrinterName)
        .setModifier(PUBLIC.build())
        .addInterface(getMCTypeFacade().createQualifiedType(visitorFullName))
        .addCDAttribute(createJsonPrinterAttribute())
        .addAllCDAttributes(symbolTablePrinterDelegates)
        .addAllCDConstructors(createConstructors(symbolTablePrinterName,symbolTablePrinterDelegates))
        .addCDMethod(createGetJsonPrinterMethod())
        .addCDMethod(createSetJsonPrinterMethod(symbolTablePrinterDelegates))
        .addCDMethod(createRealThisMethod(symbolTablePrinterName))
        .addCDMethod(createGetSerializedStringMethod())
        .addCDMethod(createFilterRelevantSubScopesMethod(scopeInterfaceFullName))
        .addCDMethod(createHasSymbolsInSubScopesMethod(scopeInterfaceFullName))
        .addCDMethod(createAddScopeSpanningSymbolMethod())
        .addCDMethod(createSerializeRelevantSubScopes(scopeInterfaceFullName))
        .addCDMethod(createSerializeLocalSymbols(scopeInterfaceFullName, symbolDefiningProds,symbolTablePrinterDelegates))
        .addAllCDMethods(createScopeVisitorMethods(scopeClassFullName))
        .addAllCDMethods(createSymbolVisitorMethods(symbolDefiningProds))
        .addAllCDMethods(createSerializationMethodsForSymbolRules(symbolTypes))
        .addAllCDMethods(createSerializationMethodsForScopeRules(scopeTypes, scopeClassFullName,symbolTableService.getScopeClassSimpleName()))
        .build();
    if (symbolTableService.hasStartProd()) {
      symbolTablePrinterClass.addAllCDMethods(createArtifactScopeVisitorMethods(artifactScopeFullName, scopeClassFullName));
    }
    return symbolTablePrinterClass;
  }

  protected List<String> getDelegateClassNames() {
    List<String> classNames = new ArrayList<>();
    for(CDDefinitionSymbol cdDefinitionSymbol:symbolTableService.getSuperCDsDirect()) {
      String name = "";
      if(null!=cdDefinitionSymbol.getPackageName() && !cdDefinitionSymbol.getPackageName().equals("")){
        name+=cdDefinitionSymbol.getPackageName()+".";
      }
      classNames.add(name+cdDefinitionSymbol.getName().toLowerCase()
          +"."+SYMBOL_TABLE_PACKAGE+"."+cdDefinitionSymbol.getName()+SYMBOL_TABLE_PRINTER_SUFFIX);
    }
    return classNames;
  }

  protected List<ASTCDAttribute> getSymbolTablePrinterDelegates(List<String> symbolTablePrinterDelegateClasses) {
    List<ASTCDAttribute> attributes = new ArrayList<>();
    for(String fullName: symbolTablePrinterDelegateClasses){
      ASTMCType type = getMCTypeFacade().createQualifiedType(fullName);
      String[] names = fullName.split("\\.");
      String attributeName = StringTransformations.uncapitalize(names[names.length-1])+"Delegate";
      ASTCDAttribute delegateAttribute = getCDAttributeFacade().createAttribute(PROTECTED,type,attributeName);
      getDecorationHelper().addAttributeDefaultValues(delegateAttribute, glex);
      attributes.add(delegateAttribute);
    }
    return attributes;
  }

  protected List<ASTCDConstructor> createConstructors(String symbolTablePrinterName,List<ASTCDAttribute> symbolTablePrinterDelegates){
    List<ASTCDConstructor> constructors = new ArrayList<>();

    MCFullGenericTypesPrettyPrinter prettyPrinter = new MCFullGenericTypesPrettyPrinter(new IndentPrinter());
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC,symbolTablePrinterName);
    StringBuilder sb = new StringBuilder("this(new "+ JSON_PRINTER+"());\n");
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint(sb.toString()));
    constructors.add(constructor);

    List<ASTCDParameter> constructorParameters = new ArrayList<>();
    String parameterName = "printer";
    constructorParameters.add(getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(JSON_PRINTER), parameterName));
    ASTCDConstructor constructorB = getCDConstructorFacade().createConstructor(PUBLIC, symbolTablePrinterName, constructorParameters);
    StringBuilder sb2 = new StringBuilder("this.printer = "+parameterName+";\n");
    for(ASTCDAttribute delegate: symbolTablePrinterDelegates){
      String attributeName = delegate.getName();
      String typeName = prettyPrinter.prettyprint(delegate.getMCType());
      sb2.append("    this.").append(attributeName).append(" = new ").append(typeName).append("(").append(parameterName).append(");\n");
    }
    this.replaceTemplate(EMPTY_BODY, constructorB, new StringHookPoint(sb2.toString()));
    constructors.add(constructorB);

    return constructors;
  }

  protected ASTCDAttribute createJsonPrinterAttribute() {
    ASTCDAttribute printerAttribute = getCDAttributeFacade().createAttribute(PROTECTED, JSON_PRINTER, "printer");
    this.replaceTemplate(VALUE, printerAttribute, new StringHookPoint("= new " + JSON_PRINTER + "()"));
    return printerAttribute;
  }

  protected ASTCDMethod createGetJsonPrinterMethod(){
    ASTMCType type = getMCTypeFacade().createQualifiedType(JSON_PRINTER);
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC,type,"getJsonPrinter");
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return this.printer;"));
    return method;
  }

  protected ASTCDMethod createSetJsonPrinterMethod(List<ASTCDAttribute> delegates){
    ASTMCType type = getMCTypeFacade().createQualifiedType(JSON_PRINTER);
    ASTCDParameter parameter = getCDParameterFacade().createParameter(type,"printer");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC,"setJsonPrinter", parameter);
    StringBuilder sb = new StringBuilder("this.printer=printer;\n");
    for(ASTCDAttribute delegate: delegates){
      sb.
          append("    ").append(delegate.getName()).append(".setJsonPrinter(printer);\n");
    }
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint(sb.toString()));
    return method;
  }

  protected ASTCDMethod createRealThisMethod(String symbolTablePrinterName) {
    ASTMCType type = getMCTypeFacade().createQualifiedType(symbolTablePrinterName);
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC,type, GET_REAL_THIS);
    String typeString = type.printType(new MCBasicTypesPrettyPrinter(new IndentPrinter()));
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return ("+typeString+")this;"));
    return method;
  }

  protected ASTCDMethod createGetSerializedStringMethod() {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createStringType(), "getSerializedString");
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return this.printer.getContent();"));
    return method;
  }

  protected ASTCDMethod createFilterRelevantSubScopesMethod(String scopeInterfaceName) {
    ASTMCListType listTypeOfScopeInterface = getMCTypeFacade().createListTypeOf(scopeInterfaceName);
    ASTMCListType listTypeOfExtendsScopeInterface = getMCTypeFacade().createListTypeOf("? extends " + scopeInterfaceName);
    ASTCDParameter subScopesParam = getCDParameterFacade().createParameter(listTypeOfExtendsScopeInterface, "subScopes");
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
    ASTCDParameter scopeParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(I_SCOPE_SPANNING_SYMBOL), "spanningSymbol");
    ASTCDMethod method = getCDMethodFacade().createMethod(PROTECTED, "addScopeSpanningSymbol", scopeParam);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "symbolTablePrinter.AddScopeSpanningSymbol"));
    return method;
  }

  protected List<ASTCDMethod> createScopeVisitorMethods(String scopeName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    visitorMethods.add(createVisitScopeMethod(scopeName));
    visitorMethods.add(createEndVisitScopeMethod(scopeName));
    visitorMethods.add(createTraverseScopeMethod(scopeName));
    return visitorMethods;
  }

  protected List<ASTCDMethod> createArtifactScopeVisitorMethods(String artifactScopeName, String scopeName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    visitorMethods.add(createVisitArtifactScopeMethod(artifactScopeName));
    visitorMethods.add(createEndVisitArtifactScopeMethod(artifactScopeName));
    visitorMethods.add(createTraverseArtifactScopeMethod(artifactScopeName, scopeName));
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
    this.replaceTemplate(EMPTY_BODY, visitorMethod, new StringHookPoint("traverse((" + scopeFullName + ") node);"));
    return visitorMethod;
  }

  protected ASTCDMethod createSerializeLocalSymbols(String scopeInterfaceFullName, List<ASTCDType> symbolProds, List<ASTCDAttribute> superSymbolTablePrinters) {
    List<String> simpleSymbolNames = symbolProds.stream()
        .map(symbolTableService::removeASTPrefix)
        .collect(Collectors.toList());

    List<String> delegateNames = superSymbolTablePrinters.stream()
        .map(ASTCDAttribute::getName)
        .collect(Collectors.toList());

    String methodName = "serializeLocalSymbols";

    ASTCDParameter parameter = getASTCDParameter(scopeInterfaceFullName, "node");
    ASTCDMethod serLocalMethod = CDMethodFacade.getInstance().createMethod(PUBLIC, methodName, parameter);

    this.replaceTemplate(EMPTY_BODY, serLocalMethod, new TemplateHookPoint(
        TEMPLATE_PATH + "symbolTablePrinter.SerializeLocalSymbols", simpleSymbolNames, delegateNames));
    return serLocalMethod;

  }

  protected ASTCDParameter getASTCDParameter(String type, String name) {
    ASTMCQualifiedType paramType = getMCTypeFacade().getInstance().createQualifiedType(type);
    ASTCDParameter param = CDParameterFacade.getInstance().createParameter(paramType, name);
    return param;
  }

  protected ASTCDMethod createSerializeRelevantSubScopes(String scopeInterfaceFullName) {
    String methodName = "serializeRelevantSubScopes";
    ASTCDParameter parameter = getASTCDParameter(scopeInterfaceFullName, "node");
    ASTCDMethod traverseRelevantMethod = CDMethodFacade.getInstance().createMethod(PROTECTED, methodName, parameter);

    this.replaceTemplate(EMPTY_BODY, traverseRelevantMethod, new TemplateHookPoint(
        TEMPLATE_PATH + "symbolTablePrinter.SerializeRelevantSubScopes", scopeInterfaceFullName));
    return traverseRelevantMethod;
  }

  protected ASTCDMethod createTraverseScopeMethod(String scopeName) {
    ASTCDMethod visitorMethod = visitorService.getVisitorMethod(TRAVERSE, getMCTypeFacade().createQualifiedType(scopeName));
    this.replaceTemplate(EMPTY_BODY, visitorMethod, new StringHookPoint("serializeRelevantSubScopes(node);"));

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
      this.replaceTemplate(EMPTY_BODY, visitMethod,
              new TemplateHookPoint(TEMPLATE_PATH + "symbolTablePrinter.VisitSymbol", symbolProd, symbolFullName));
      visitorMethods.add(visitMethod);
      ASTCDMethod endVisitMethod = visitorService.getVisitorMethod(END_VISIT, getMCTypeFacade().createQualifiedType(symbolFullName));
      this.replaceTemplate(EMPTY_BODY, endVisitMethod, new StringHookPoint(PRINTER_END_OBJECT));
      visitorMethods.add(endVisitMethod);
    }
    return visitorMethods;
  }

  /**
   * @param prod
   * @param methodName       the name of the method which handles the serialization of this rule
   * @param paramType        the paramType of method methodName
   * @param attrMethodPrefix the prefix for the generated (serialization) methods of the additional attributes
   * @return the created methods
   */
  protected List<ASTCDMethod> createSerializeMethodsForRule(ASTCDClass prod, String methodName, String paramType, String attrMethodPrefix) {
    List<ASTCDMethod> methodsCreated = new ArrayList<>();

    ASTMCQualifiedType type = getMCTypeFacade().createQualifiedType(paramType);
    ASTCDParameter serializeParameter = CDParameterFacade.getInstance().createParameter(type, "node");
    ASTCDMethod serializeScopeMethod = CDMethodFacade.getInstance().createMethod(PROTECTED, methodName, serializeParameter);

    for (ASTCDAttribute attr : prod.deepClone().getCDAttributeList()) {
      methodsCreated.add(createSerializeMethodForAttr(attrMethodPrefix, attr));
    }

    this.replaceTemplate(EMPTY_BODY, serializeScopeMethod, new TemplateHookPoint(
        TEMPLATE_PATH + "symbolTablePrinter.SerializeSymbol", attrMethodPrefix, prod.deepClone().getCDAttributeList()));
    methodsCreated.add(serializeScopeMethod);

    return methodsCreated;
  }

  protected List<ASTCDMethod> createSerializationMethodsForScopeRules(List<ASTCDClass> scopeProds, String scopeClassFullName, String scopeSimpleName) {
    // every scope is serialized equally
    List<ASTCDMethod> methodsCreated = new ArrayList<>();
    String methodName = "serializeAdditionalScopeAttributes";

    for (ASTCDClass scopeProd : scopeProds) {
      String attrMethodPrefix = "serialize" + scopeSimpleName;
      methodsCreated.addAll(createSerializeMethodsForRule(scopeProd, methodName, scopeClassFullName, attrMethodPrefix));
    }
    return methodsCreated;
  }

  protected List<ASTCDMethod> createSerializationMethodsForSymbolRules(List<ASTCDClass> symbolProds) {
    List<ASTCDMethod> methodsCreated = new ArrayList<>();
    // 1. Create Method for whole symbol
    // 2. Create Methods for each attr of sym which are called in 1.
    for (ASTCDClass symbolProd : symbolProds) {
      String methodName = "serialize" + StringTransformations.capitalize(symbolProd.getName());
      String symbolFullName = symbolTableService.getSymbolFullName(symbolProd);
      String attrMethodPrefix = "serialize" + symbolProd.getName();
      methodsCreated.addAll(createSerializeMethodsForRule(symbolProd, methodName, symbolFullName, attrMethodPrefix));
      //methodsCreated.addAll(createSerializeMethodsForSymRule(symbolProd));
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
    ASTCDMethod serializeAttrMethod = CDMethodFacade.getInstance().createMethod(PROTECTED, methodName, serializeParameter);

    if (isAutoSerialized(attr)) {
      if (isSerializedAsList(attr)) {
        serializeAsList(serializeAttrMethod, attr);
      } else if (isSerializedAsOptional(attr)) {
        this.replaceTemplate(EMPTY_BODY, serializeAttrMethod, new TemplateHookPoint(TEMPLATE_PATH
                + "symbolTablePrinter.SerializeOptAttribute", attr));
      } else {
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
    String type = attr.printType();

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
}
