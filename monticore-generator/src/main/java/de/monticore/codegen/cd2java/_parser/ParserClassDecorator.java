/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._parser;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.monticore.cd.facade.CDAttributeFacade;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.*;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.Names;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class ParserClassDecorator extends AbstractDecorator {

  protected static final String TEMPLATE_PATH = "_parser.";

  protected final ParserService service;

  public ParserClassDecorator(GlobalExtensionManagement glex, ParserService service){
    super(glex);
    this.service = service;
  }

  public Optional<ASTCDClass> decorate(ASTCDCompilationUnit input){
    if(!service.hasComponentStereotype(input.getCDDefinition().getModifier())) {
      Optional<String> startProd = service.getStartProd();
      if(startProd.isPresent()) {
        String startRuleName = Names.getSimpleName(service.getStartProd().get());
        String grammarName = input.getCDDefinition().getName();
        String qualifiedStartRuleName = service.getStartProdASTFullName(input.getCDDefinition()).get();
        ASTMCQualifiedType superClass = getMCTypeFacade().createQualifiedType("de.monticore.antlr4.MCConcreteParser");
        Map<ASTCDType, ASTCDDefinition> prods = getSuperProds(input.getCDDefinition(), Lists.newArrayList());


        return Optional.of(CD4AnalysisMill.cDClassBuilder()
            .setName(service.getParserClassSimpleName())
            .setModifier(PUBLIC.build())
            .setCDExtendUsage(CD4CodeMill.cDExtendUsageBuilder().addSuperclass(superClass).build())
            .addAllCDMembers(createCreateMethods(grammarName))
            .addAllCDMembers(createParseMethods(startRuleName, qualifiedStartRuleName))
            .addAllCDMembers(createParseMethodsForProds(grammarName, prods))
            .addAllCDMembers(createMode())
            .build());
      }
    }
    return Optional.empty();
  }

  protected List<ASTCDMethod> createCreateMethods(String grammarName){
    List<ASTCDMethod> methods = Lists.newArrayList();
    ASTMCQualifiedName ioException = MCBasicTypesMill.mCQualifiedNameBuilder()
        .setPartsList(Lists.newArrayList("java", "io", "IOException")).build();

    ASTMCType returnType = getMCTypeFacade().createQualifiedType(service.getAntlrParserSimpleName());
    ASTCDParameter fileNameParameter = getCDParameterFacade().createParameter(String.class, "fileName");
    ASTCDMethod create = getCDMethodFacade().createMethod(PROTECTED.build(), returnType, "create", fileNameParameter);
    create.setCDThrowsDeclaration(CD4CodeBasisMill.cDThrowsDeclarationBuilder().addException(ioException).build());
    this.replaceTemplate(EMPTY_BODY, create, new TemplateHookPoint(TEMPLATE_PATH + "Create", grammarName));
    methods.add(create);

    ASTMCType readerType = getMCTypeFacade().createQualifiedType("java.io.Reader");
    ASTCDParameter readerParameter = getCDParameterFacade().createParameter(readerType, "reader");
    ASTCDMethod createReader = getCDMethodFacade().createMethod(PROTECTED.build(), returnType, "create", readerParameter);
    createReader.setCDThrowsDeclaration(CD4CodeBasisMill.cDThrowsDeclarationBuilder().addException(ioException).build());
    this.replaceTemplate(EMPTY_BODY, createReader, new TemplateHookPoint(TEMPLATE_PATH + "CreateReader", grammarName));
    methods.add(createReader);

    return methods;
  }

  protected List<ASTCDMember> createMode(){
    List<ASTCDMember> members = Lists.newArrayList();

    ASTCDAttribute modeAttribute = CDAttributeFacade.getInstance().createAttribute(PROTECTED.build(), "String", "lexerMode");
    this.replaceTemplate(VALUE, modeAttribute, new StringHookPoint("= \"\";"));
    members.add(modeAttribute);

    MethodDecorator methodDecorator = new MethodDecorator(glex, service);
    members.addAll(methodDecorator.getMutatorDecorator().decorate(modeAttribute));

    return members;
  }

  protected List<ASTCDMethod> createParseMethods(String startRuleName, String startRuleFullName){
    List<ASTCDMethod> methods = Lists.newArrayList();
    ASTMCQualifiedName ioException = MCBasicTypesMill.mCQualifiedNameBuilder()
        .setPartsList(Lists.newArrayList("java", "io", "IOException"))
        .build();

    String prodName = service.removeASTPrefix(startRuleName);

    ASTMCType returnType = getMCTypeFacade().createOptionalTypeOf(getMCTypeFacade().createQualifiedType(startRuleFullName));
    ASTCDParameter fileNameParameter = getCDParameterFacade().createParameter(String.class, "fileName");
    ASTCDMethod parse = getCDMethodFacade().createMethod(PUBLIC.build(), returnType, "parse", fileNameParameter);
    parse.setCDThrowsDeclaration(CD4CodeBasisMill.cDThrowsDeclarationBuilder().addException(ioException).build());
    this.replaceTemplate(EMPTY_BODY, parse, new TemplateHookPoint(TEMPLATE_PATH + "Parse", prodName));
    methods.add(parse);

    ASTMCType readerType = getMCTypeFacade().createQualifiedType("java.io.Reader");
    ASTCDParameter readerParameter = getCDParameterFacade().createParameter(readerType, "reader");
    ASTCDMethod parseReader = getCDMethodFacade().createMethod(PUBLIC.build(), returnType, "parse", readerParameter);
    parseReader.setCDThrowsDeclaration(CD4CodeBasisMill.cDThrowsDeclarationBuilder().addException(ioException).build());
    this.replaceTemplate(EMPTY_BODY, parseReader, new TemplateHookPoint(TEMPLATE_PATH + "ParseReader", prodName));
    methods.add(parseReader);

    ASTCDParameter strParameter = getCDParameterFacade().createParameter(String.class, "str");
    ASTCDMethod parseString = getCDMethodFacade().createMethod(PUBLIC.build(), returnType, "parse_String", strParameter);
    parseString.setCDThrowsDeclaration(CD4CodeBasisMill.cDThrowsDeclarationBuilder().addException(ioException).build());
    this.replaceTemplate(EMPTY_BODY, parseString, new TemplateHookPoint(TEMPLATE_PATH + "ParseString", prodName));
    methods.add(parseString);

    return methods;
  }

  protected List<ASTCDMethod> createParseMethodsForProds(String grammarName, Map<ASTCDType, ASTCDDefinition> prods){
    List<ASTCDMethod> methods = Lists.newArrayList();
    ASTMCQualifiedName ioException = MCBasicTypesMill.mCQualifiedNameBuilder()
        .setPartsList(Lists.newArrayList("java", "io", "IOException")).build();
    for(Map.Entry<ASTCDType, ASTCDDefinition> entry: prods.entrySet()){
      String packageName = service.getASTPackage(entry.getValue().getSymbol());
      ASTCDType prod = entry.getKey();
      String simpleRuleName = prod.getName();
      String qualifiedRuleName = packageName + "." + simpleRuleName;
      String parseMethodSuffix = service.removeASTPrefix(simpleRuleName);
      ASTMCType returnType = getMCTypeFacade().createOptionalTypeOf(getMCTypeFacade().createQualifiedType(qualifiedRuleName));
      ASTCDParameter fileNameParameter = getCDParameterFacade().createParameter(String.class, "fileName");
      ASTCDMethod parse = getCDMethodFacade().createMethod(PUBLIC.build(), returnType, "parse" + parseMethodSuffix, fileNameParameter);
      parse.setCDThrowsDeclaration(CD4CodeBasisMill.cDThrowsDeclarationBuilder().addException(ioException).build());
      this.replaceTemplate(EMPTY_BODY, parse, new TemplateHookPoint(TEMPLATE_PATH + "ParseRule", grammarName, qualifiedRuleName, service.getParseRuleNameJavaCompatible(prod)));
      methods.add(parse);
      ASTMCType readerType = getMCTypeFacade().createQualifiedType("java.io.Reader");
      ASTCDParameter readerParameter = getCDParameterFacade().createParameter(readerType, "reader");
      ASTCDMethod parseReader = getCDMethodFacade().createMethod(PUBLIC.build(), returnType, "parse" + parseMethodSuffix, readerParameter);
      parseReader.setCDThrowsDeclaration(CD4CodeBasisMill.cDThrowsDeclarationBuilder().addException(ioException).build());
      this.replaceTemplate(EMPTY_BODY, parseReader, new TemplateHookPoint(TEMPLATE_PATH + "ParseRuleReader", grammarName, qualifiedRuleName, service.getParseRuleNameJavaCompatible(prod)));
      methods.add(parseReader);
      ASTCDParameter strParameter = getCDParameterFacade().createParameter(String.class, "str");
      ASTCDMethod parseString = getCDMethodFacade().createMethod(PUBLIC.build(), returnType, "parse_String" + parseMethodSuffix, strParameter);
      parseString.setCDThrowsDeclaration(CD4CodeBasisMill.cDThrowsDeclarationBuilder().addException(ioException).build());
      this.replaceTemplate(EMPTY_BODY, parseString, new TemplateHookPoint(TEMPLATE_PATH + "ParseRuleString", parseMethodSuffix));
      methods.add(parseString);
    }
    return methods;
  }

  protected Map<ASTCDType, ASTCDDefinition> getSuperProds(ASTCDDefinition definition, List<String> nameList){
    Map<ASTCDType, ASTCDDefinition> superProds = Maps.newLinkedHashMap();
    List<ASTCDType> prods = Lists.newArrayList();
    prods.addAll(definition.getCDClassesList());
    prods.removeIf(prod -> service.hasLeftRecursiveStereotype(prod.getModifier()));
    prods.addAll(definition.getCDInterfacesList());
    //no parser method for e.g. ASTAutomataNode
    prods.removeIf(prod -> prod.getName().equals("AST" + definition.getSymbol().getName() + "Node"));
    prods.removeIf(prod -> service.hasExternalInterfaceStereotype(prod.getModifier()));
    for(ASTCDType prod: prods){
      //if prod is already in the nameList: prod was overridden and does not need to be added
      if(!nameList.contains(prod.getName())){
        nameList.add(prod.getName());
        superProds.put(prod, definition);
      }
    }
    //recursive call of the method for all direct super CDs
    for(DiagramSymbol superDefinition: service.getSuperCDsDirect(definition.getSymbol())){
      superProds.putAll(getSuperProds((ASTCDDefinition) superDefinition.getAstNode(), nameList));
    }
    return superProds;
  }


}
