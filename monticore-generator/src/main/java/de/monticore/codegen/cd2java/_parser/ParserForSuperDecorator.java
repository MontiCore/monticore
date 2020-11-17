package de.monticore.codegen.cd2java._parser;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PREFIX;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.NODE_SUFFIX;
import static de.monticore.codegen.cd2java._ast.constants.ASTConstantsDecorator.LITERALS_SUFFIX;
import static de.monticore.codegen.cd2java._parser.ParserConstants.FOR_SUFFIX;
import static de.monticore.codegen.cd2java._parser.ParserConstants.PARSER_SUFFIX;

public class ParserForSuperDecorator extends AbstractDecorator {

  protected static final String TEMPLATE_PATH = "_parser.";

  protected final ParserService service;

  public ParserForSuperDecorator(GlobalExtensionManagement glex, ParserService service){
    super(glex);
    this.service = service;
  }

  public List<ASTCDClass> decorate(ASTCDCompilationUnit astCD){
    //create a SuperForSubParser for every super grammar that is not component
    List<ASTCDClass> classList = Lists.newArrayList();
    if(!astCD.getCDDefinition().isPresentModifier() || !service.hasComponentStereotype(astCD.getCDDefinition().getModifier())){
      String grammarName = service.getCDName();
      for(CDDefinitionSymbol symbol: service.getSuperCDsTransitive()){
        if(!symbol.getAstNode().isPresentModifier() || !service.hasComponentStereotype(symbol.getAstNode().getModifier())){
          String superGrammarName = symbol.getName();
          List<ASTCDClass> astcdClasses = astCD.getCDDefinition().deepClone().getCDClassList();
          ASTMCObjectType superClass = getMCTypeFacade().createQualifiedType(service.getParserClassFullName(symbol));
          ASTCDClass clazz = CD4AnalysisMill.cDClassBuilder()
              .setName(superGrammarName + PARSER_SUFFIX + FOR_SUFFIX + grammarName)
              .setModifier(PUBLIC.build())
              .setSuperclass(superClass)
              .addAllCDMethods(createParseMethods(astcdClasses, symbol))
              .build();
          classList.add(clazz);
        }
      }
    }
    return classList;
  }

  protected List<ASTCDMethod> createParseMethods(List<ASTCDClass> astcdClassList, CDDefinitionSymbol symbol){
    List<ASTCDMethod> methods = Lists.newArrayList();
    ASTMCQualifiedName ioException = MCBasicTypesMill.mCQualifiedNameBuilder()
        .setPartsList(Lists.newArrayList("java", "io", "IOException"))
        .build();

    HashMap<CDDefinitionSymbol, Collection<CDTypeSymbol>> overridden = Maps.newHashMap();
    Collection<CDTypeSymbol> firstClasses2 = Lists.newArrayList();
    //get all overridden prods, store them in overridden
    calculateOverriddenCds(symbol, astcdClassList.stream().map(ASTCDClass::getName).collect(Collectors.toList()), overridden, firstClasses2);

    HashMap<CDDefinitionSymbol, Collection<CDTypeSymbol>> overridden2 = Maps.newHashMap();
    Collection<CDTypeSymbol> firstClasses = Lists.newArrayList();
    calculateOverriddenCds(service.getCDSymbol(), astcdClassList.stream().map(ASTCDClass::getName).collect(Collectors.toList()), overridden2, firstClasses);
    //get the other (not overridden) super prods whose parse methods needs to be overridden
    Map<CDDefinitionSymbol, Collection<CDTypeSymbol>> superProds = calculateNonOverriddenCds(Maps.newHashMap(), symbol, overridden, Lists.newArrayList());
    for(CDDefinitionSymbol grammar: superProds.keySet()){
      Collection<CDTypeSymbol> typesInGrammar = superProds.get(grammar);
      //remove the prods that have no parse method
      typesInGrammar.removeIf(type -> type.getAstNode() instanceof ASTCDEnum);
      typesInGrammar.removeIf(type -> type.getName().equals(grammar.getName() + LITERALS_SUFFIX));
      typesInGrammar.removeIf(type -> type.getName().equals(AST_PREFIX + grammar.getName() + NODE_SUFFIX));
      typesInGrammar.removeIf(type -> type.getAstNode().isPresentModifier() &&
          (service.hasLeftRecursiveStereotype(type.getAstNode().getModifier())|| service.hasExternalInterfaceStereotype(type.getAstNode().getModifier())));
    }
    List<CDTypeSymbol> allOverriddenTypes = overridden.values().stream().flatMap(Collection::stream).collect(Collectors.toList());


    String millFullName = service.getMillFullName();

    //iterate over every overridden prod, generate parse methods for them
    for(Map.Entry<CDDefinitionSymbol, Collection<CDTypeSymbol>> entry: overridden.entrySet()){
      for(CDTypeSymbol type: entry.getValue()) {
        if (type.getAstNode().isPresentModifier() &&
            (service.hasExternalInterfaceStereotype(type.getAstNode().getModifier()) || service.hasLeftRecursiveStereotype(type.getAstNode().getModifier()))) {
          continue;
        }
        //if any of the other overriddenTypes has the same name and overrides the type, then the type is not the most specified type -> generate no parser method for this type
        if(allOverriddenTypes.stream().anyMatch(overriddenType -> overriddenType.getName().equals(type.getName()) && overrides(overriddenType, type))){
          continue;
        }
        String prod = type.getName();
        String prodName = service.removeASTPrefix(prod);
        String superProdFullName = service.getASTPackage(entry.getKey()) + "." + prod;
        String prodFullName = service.getASTPackage() + "." + prod;

        ASTMCType returnType = getMCTypeFacade().createOptionalTypeOf(getMCTypeFacade().createQualifiedType(superProdFullName));
        ASTCDParameter fileNameParameter = getCDParameterFacade().createParameter(String.class, "fileName");
        ASTCDMethod parse = getCDMethodFacade().createMethod(PUBLIC, returnType, "parse" + prodName, fileNameParameter);
        parse.addException(ioException);


        ASTMCType readerType = getMCTypeFacade().createQualifiedType("java.io.Reader");
        ASTCDParameter readerParameter = getCDParameterFacade().createParameter(readerType, "reader");
        ASTCDMethod parseReader = getCDMethodFacade().createMethod(PUBLIC, returnType, "parse" + prodName, readerParameter);
        parseReader.addException(ioException);

        //if a nonterminal overrides two or more other nonterminals that do not extend each other then the nonterminal can only extend one of them due to single inheritance
        //the parse-method generated for the second or third/fourth... overridden nonterminal logs an error upon invocation because the overridding nonterminal does not extend it and
        //so the parse method cannot be generated normally. A correct parse method is only generated for the first overridden nonterminal because the overriding nonterminal extends it
        if (firstClasses.contains(type)) {
          this.replaceTemplate(EMPTY_BODY, parse, new TemplateHookPoint(TEMPLATE_PATH + "ParseOverridden", prodName, prodFullName, millFullName));
          this.replaceTemplate(EMPTY_BODY, parseReader, new TemplateHookPoint(TEMPLATE_PATH + "ParseOverriddenReader", prodName, prodFullName, millFullName));
        } else {
          String generatedErrorCode1 = service.getGeneratedErrorCode(prod + "Parse");
          String generatedErrorCode2 = service.getGeneratedErrorCode(prod + "ParseReader");
          this.replaceTemplate(EMPTY_BODY, parse, new StringHookPoint("Log.error(\"0xA7056" + generatedErrorCode1 + " Overridden production " +
              prodName + " is not reachable\");\nreturn null;"));

          this.replaceTemplate(EMPTY_BODY, parseReader, new StringHookPoint("Log.error(\"0xA7057" + generatedErrorCode2 + " Overridden production " +
              prodName + " is not reachable\");\nreturn null;"));
        }

        methods.add(parse);
        methods.add(parseReader);
      }
    }

    //iterate over every non-overridden prod, generate parse methods for them
    for(Map.Entry<CDDefinitionSymbol, Collection<CDTypeSymbol>> entry: superProds.entrySet()){
      for(CDTypeSymbol type: entry.getValue()){
        String prod = type.getName();
        String prodName = service.removeASTPrefix(prod);
        String superProdFullName = service.getASTPackage(entry.getKey()) + "." + prod;

        ASTMCType returnType = getMCTypeFacade().createOptionalTypeOf(getMCTypeFacade().createQualifiedType(superProdFullName));
        ASTCDParameter fileNameParameter = getCDParameterFacade().createParameter(String.class, "fileName");
        ASTCDMethod parse = getCDMethodFacade().createMethod(PUBLIC, returnType, "parse" + prodName, fileNameParameter);
        parse.addException(ioException);
        this.replaceTemplate(EMPTY_BODY, parse, new TemplateHookPoint(TEMPLATE_PATH + "ParseSup", millFullName, prodName));
        methods.add(parse);

        ASTMCType readerType = getMCTypeFacade().createQualifiedType("java.io.Reader");
        ASTCDParameter readerParameter = getCDParameterFacade().createParameter(readerType, "reader");
        ASTCDMethod parseReader = getCDMethodFacade().createMethod(PUBLIC, returnType, "parse" + prodName, readerParameter);
        parseReader.addException(ioException);
        this.replaceTemplate(EMPTY_BODY, parseReader, new TemplateHookPoint(TEMPLATE_PATH + "ParseSupReader", millFullName, prodName));
        methods.add(parseReader);
      }
    }

    return methods;
  }

  /**
   * calculates the super prods which are not overridden in the grammar and stores them with their associated grammar
   * @param otherSuperProds a container that stores the super prods that are not overridden by the actual grammar
   * @param symbol a super grammar
   * @param overridden the (previously calculated) overridden prods of every grammar
   * @param prodNames stores the prods names so that no prod name is added twice -> only one parse method for every NT with the same name
   * @return the otherSuperProds container containing all super prods that are not overridden
   */
  protected Map<CDDefinitionSymbol, Collection<CDTypeSymbol>> calculateNonOverriddenCds(Map<CDDefinitionSymbol, Collection<CDTypeSymbol>> otherSuperProds,
                                                                                        CDDefinitionSymbol symbol,
                                                                                        Map<CDDefinitionSymbol, Collection<CDTypeSymbol>> overridden,
                                                                                        List<String> prodNames){
    Collection<CDTypeSymbol> types = overridden.get(symbol);
    Collection<CDTypeSymbol> typesInSymbol = symbol.getTypes();
    Collection<CDTypeSymbol> otherProds = Lists.newArrayList();

    for(CDTypeSymbol type: typesInSymbol){
      //if type is already in the list or in prodnames, then it is overridden -> in this method, the non-overridden prods are collected
      if((null==types || !types.contains(type)) && !prodNames.contains(type.getName())){
        otherProds.add(type);
        prodNames.add(type.getName());
      }
    }
    //add all non-overridden prods to the non-overridden map with their grammar
    otherSuperProds.put(symbol, otherProds);

    //make method recursive so that all transitive super prods are taken into account
    for(CDDefinitionSymbol superSymbol: service.getSuperCDsDirect(symbol)){
      calculateNonOverriddenCds(otherSuperProds, superSymbol, overridden, prodNames);
    }

    return otherSuperProds;
  }

  /**
   * calculates all overridden cds and stores them in the overridden parameter,
   * store firstClasses to differentiate whether the method can be generated normally or an error code must be logged upon its execution
   * @param cd a super grammar, the method checks if its prods are overridden by the native classes
   * @param nativeClasses the native prods introduced in the grammar
   * @param overridden a container in which the overridden prods are stored in
   * @param firstClasses a container to check whether a parse method can be generated normally or an error code must be logged upon its execution
   */
  protected void calculateOverriddenCds(CDDefinitionSymbol cd, Collection<String> nativeClasses, HashMap<CDDefinitionSymbol,
      Collection<CDTypeSymbol>> overridden, Collection<CDTypeSymbol> firstClasses) {
    HashMap<String, CDTypeSymbol> l = Maps.newHashMap();
    //get all super cds / imports of the original cd
    Collection<CDDefinitionSymbol> importedClasses = cd.getImports().stream().map(service::resolveCD).collect(Collectors.toList());
    Collection<CDTypeSymbol> overriddenSet = Lists.newArrayList();
    //determine for every native prod of the original grammar if the super grammar has a prod with the same name
    //if yes then the prod is overridden
    for (String className : nativeClasses) {
      Optional<CDTypeSymbol> cdType = cd.getType(className);
      if (cdType.isPresent()) {
        overriddenSet.add(cdType.get());
        //if the type is already in the first classes container, then ignore it so that no correct parse method for it can be generated
        boolean ignore = firstClasses.stream().anyMatch(s -> s.getName().equals(className));
        if (!ignore && !l.containsKey(className)) {
          l.put(className, cdType.get());
        }
      }
    }
    if (!overriddenSet.isEmpty()) {
      overridden.put(cd, overriddenSet);
    }
    //repeat this for every super grammar -> recursion
    for(CDDefinitionSymbol superCd: importedClasses){
      calculateOverriddenCds(superCd, nativeClasses, overridden, firstClasses);
    }

    firstClasses.addAll(l.values());
  }

  protected boolean overrides(CDTypeSymbol first, CDTypeSymbol second){
    return first.getSuperTypesTransitive().stream().map(CDTypeSymbol::getFullName).collect(Collectors.toList()).contains(second.getFullName());
  }

}
