/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._parser;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis.CD4CodeBasisMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.CDTypeSymbolSurrogate;
import de.monticore.cdbasis._symboltable.ICDBasisArtifactScope;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CDModifier.PUBLIC;
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
    if(!service.hasComponentStereotype(astCD.getCDDefinition().getModifier())){
      String grammarName = service.getCDName();
      for(DiagramSymbol symbol: service.getSuperCDsTransitive()){
        if(!service.hasComponentStereotype(((ASTCDDefinition) symbol.getAstNode()).getModifier())){
          String superGrammarName = symbol.getName();
          List<ASTCDClass> astcdClasses = astCD.getCDDefinition().deepClone().getCDClassesList();
          ASTMCQualifiedType superClass = getMCTypeFacade().createQualifiedType(service.getParserClassFullName(symbol));
          String className = superGrammarName + PARSER_SUFFIX + FOR_SUFFIX + grammarName;

          ASTCDClass clazz = CD4AnalysisMill.cDClassBuilder()
              .setName(className)
              .setModifier(PUBLIC.build())
              .setCDExtendUsage(CD4CodeMill.cDExtendUsageBuilder().addSuperclass(superClass).build())
              .addAllCDMembers(createParseMethods(astcdClasses, symbol))
              .build();
          classList.add(clazz);
        }
      }
    }
    return classList;
  }

  protected List<ASTCDMethod> createParseMethods(List<ASTCDClass> astcdClassList, DiagramSymbol symbol){
    List<ASTCDMethod> methods = Lists.newArrayList();

    HashMap<DiagramSymbol, Collection<CDTypeSymbol>> overridden = getOverridden(astcdClassList, symbol);
    Collection<CDTypeSymbol> firstClasses = getFirstClasses(astcdClassList);

    //get the other (not overridden) super prods whose parse methods needs to be overridden
    Map<DiagramSymbol, Collection<CDTypeSymbol>> superProds = calculateNonOverriddenCds(Maps.newHashMap(), symbol, overridden, Lists.newArrayList());
    Map<DiagramSymbol, Collection<CDTypeSymbol>> superProdsFromThis = calculateNonOverriddenCds(Maps.newHashMap(), service.getCDSymbol(), overridden, Lists.newArrayList());

    //necessary if a nt is overridden in a grammar between the super grammar and this grammar
    List<CDTypeSymbol> allSuperProdsFromThis = superProdsFromThis.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    List<String> superProdsFullNames = allSuperProdsFromThis.stream().map(CDTypeSymbol::getFullName).collect(Collectors.toList());

    for(DiagramSymbol grammar: superProds.keySet()){
      Collection<CDTypeSymbol> typesInGrammar = superProds.get(grammar);
      //remove the prods that have no parse method
      typesInGrammar.removeIf(type -> type.getAstNode() instanceof ASTCDEnum);
      typesInGrammar.removeIf(type -> type.getName().equals(grammar.getName() + LITERALS_SUFFIX));
      typesInGrammar.removeIf(type -> type.getName().equals(AST_PREFIX + grammar.getName() + NODE_SUFFIX));
      typesInGrammar.removeIf(type ->
          (service.hasLeftRecursiveStereotype(type.getAstNode().getModifier())|| service.hasExternalInterfaceStereotype(type.getAstNode().getModifier())));
    }
    List<CDTypeSymbol> allOverriddenTypes = overridden.values().stream().flatMap(Collection::stream).collect(Collectors.toList());


    //iterate over every overridden prod, generate parse methods for them
    for(Map.Entry<DiagramSymbol, Collection<CDTypeSymbol>> entry: overridden.entrySet()){
      for(CDTypeSymbol type: entry.getValue()) {
        if (service.hasExternalInterfaceStereotype(type.getAstNode().getModifier()) || service.hasLeftRecursiveStereotype(type.getAstNode().getModifier())) {
          continue;
        }
        //if any of the other overriddenTypes has the same name and overrides the type, then the type is not the most specified type -> generate no parser method for this type
        if(allOverriddenTypes.stream().anyMatch(overriddenType -> overriddenType.getName().equals(type.getName()) && overrides(overriddenType, type))){
          continue;
        }
        methods.addAll(getOverriddenMethods(type, entry.getKey(), firstClasses));
      }
    }

    //iterate over every non-overridden prod, generate parse methods for them
    for(Map.Entry<DiagramSymbol, Collection<CDTypeSymbol>> entry: superProds.entrySet()){
      for(CDTypeSymbol type: entry.getValue()){
        //necessary if a nt of a grammar between this grammar and the super grammar is overridden
        if(!superProdsFullNames.contains(type.getFullName())){
          String name = type.getName();
          Optional<CDTypeSymbol> optType = allSuperProdsFromThis.stream().filter(typeSymbol -> typeSymbol.getName().equals(name)).findFirst();
          if(optType.isPresent()){
            methods.addAll(getOverriddenMethods(type, entry.getKey(), firstClasses));
          }
        }else {
          methods.addAll(getParseMethodsForOtherProds(type, entry.getKey()));
        }
      }
    }

    return methods;
  }

  public List<CDTypeSymbol> getFirstClasses(List<ASTCDClass> astcdClassList){
    HashMap<DiagramSymbol, Collection<CDTypeSymbol>> overridden = Maps.newHashMap();
    List<CDTypeSymbol> firstClasses = Lists.newArrayList();
    calculateOverriddenCds(service.getCDSymbol(), astcdClassList.stream().map(ASTCDClass::getName).collect(Collectors.toList()), overridden, firstClasses);
    return firstClasses;
  }

  public HashMap<DiagramSymbol, Collection<CDTypeSymbol>> getOverridden(List<ASTCDClass> astcdClassList, DiagramSymbol symbol){
    HashMap<DiagramSymbol, Collection<CDTypeSymbol>> overridden = Maps.newHashMap();
    List<CDTypeSymbol> firstClasses = Lists.newArrayList();
    calculateOverriddenCds(symbol, astcdClassList.stream().map(ASTCDClass::getName).collect(Collectors.toList()), overridden, firstClasses);
    return overridden;
  }

  /**
   * calculates the super prods which are not overridden in the grammar and stores them with their associated grammar
   * @param otherSuperProds a container that stores the super prods that are not overridden by the actual grammar
   * @param symbol a super grammar
   * @param overridden the (previously calculated) overridden prods of every grammar
   * @param prodNames stores the prods names so that no prod name is added twice -> only one parse method for every NT with the same name
   * @return the otherSuperProds container containing all super prods that are not overridden
   */
  protected Map<DiagramSymbol, Collection<CDTypeSymbol>> calculateNonOverriddenCds(Map<DiagramSymbol, Collection<CDTypeSymbol>> otherSuperProds,
                                                                                        DiagramSymbol symbol,
                                                                                        Map<DiagramSymbol, Collection<CDTypeSymbol>> overridden,
                                                                                        List<String> prodNames){
    Collection<CDTypeSymbol> types = overridden.get(symbol);
    Collection<CDTypeSymbol> typesInSymbol = ((ICDBasisScope) symbol.getEnclosingScope()).getLocalCDTypeSymbols();
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
    for(DiagramSymbol superSymbol: service.getSuperCDsDirect(symbol)){
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
  protected void calculateOverriddenCds(DiagramSymbol cd, Collection<String> nativeClasses, HashMap<DiagramSymbol,
      Collection<CDTypeSymbol>> overridden, Collection<CDTypeSymbol> firstClasses) {
    HashMap<String, CDTypeSymbol> l = Maps.newHashMap();
    //get all super cds / imports of the original cd
    Collection<DiagramSymbol> importedClasses = ((ICDBasisArtifactScope) cd.getEnclosingScope()).getImportsList().stream()
        .map(i -> i.getStatement())
        .map(service::resolveCD)
        .collect(Collectors.toList());
    Collection<CDTypeSymbol> overriddenSet = Lists.newArrayList();
    //determine for every native prod of the original grammar if the super grammar has a prod with the same name
    //if yes then the prod is overridden
    for (String className : nativeClasses) {
      Optional<CDTypeSymbol> cdType = ((ICDBasisArtifactScope) cd.getEnclosingScope()).resolveCDTypeLocally(className);
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
    for(DiagramSymbol superCd: importedClasses){
      calculateOverriddenCds(superCd, nativeClasses, overridden, firstClasses);
    }

    firstClasses.addAll(l.values());
  }

  protected boolean overrides(CDTypeSymbol first, CDTypeSymbol second){
     return getSuperTypesTransitive(first).stream().map(CDTypeSymbol::getFullName).collect(Collectors.toList()).contains(second.getFullName());
  }

  protected List<ASTCDMethod> getOverriddenMethods(CDTypeSymbol type, DiagramSymbol grammar, Collection<CDTypeSymbol> firstClasses){
    List<ASTCDMethod> methods = Lists.newArrayList();
    ASTMCQualifiedName ioException = MCBasicTypesMill.mCQualifiedNameBuilder()
        .setPartsList(Lists.newArrayList("java", "io", "IOException"))
        .build();

    String millFullName = service.getMillFullName();

    String prod = type.getName();
    String prodName = service.removeASTPrefix(prod);
    String superProdFullName = service.getASTPackage(grammar) + "." + prod;
    String prodFullName = service.getASTPackage() + "." + prod;

    ASTMCType returnType = getMCTypeFacade().createOptionalTypeOf(getMCTypeFacade().createQualifiedType(superProdFullName));
    ASTCDParameter fileNameParameter = getCDParameterFacade().createParameter(String.class, "fileName");
    ASTCDMethod parse = getCDMethodFacade().createMethod(PUBLIC.build(), returnType, "parse" + prodName, fileNameParameter);
    parse.setCDThrowsDeclaration(CD4CodeBasisMill.cDThrowsDeclarationBuilder().addException(ioException).build());


    ASTMCType readerType = getMCTypeFacade().createQualifiedType("java.io.Reader");
    ASTCDParameter readerParameter = getCDParameterFacade().createParameter(readerType, "reader");
    ASTCDMethod parseReader = getCDMethodFacade().createMethod(PUBLIC.build(), returnType, "parse" + prodName, readerParameter);
    parseReader.setCDThrowsDeclaration(CD4CodeBasisMill.cDThrowsDeclarationBuilder().addException(ioException).build());

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
          prodName + " is not reachable\");\n\treturn Optional.empty();"));

      this.replaceTemplate(EMPTY_BODY, parseReader, new StringHookPoint("Log.error(\"0xA7057" + generatedErrorCode2 + " Overridden production " +
          prodName + " is not reachable\");\n\treturn Optional.empty();"));
    }

    methods.add(parse);
    methods.add(parseReader);

    return methods;
  }

  protected List<ASTCDMethod> getParseMethodsForOtherProds(CDTypeSymbol type, DiagramSymbol grammar){
    List<ASTCDMethod> methods = Lists.newArrayList();
    ASTMCQualifiedName ioException = MCBasicTypesMill.mCQualifiedNameBuilder()
        .setPartsList(Lists.newArrayList("java", "io", "IOException"))
        .build();

    String millFullName = service.getMillFullName();

    String prod = type.getName();
    String prodName = service.removeASTPrefix(prod);
    String superProdFullName = service.getASTPackage(grammar) + "." + prod;

    ASTMCType returnType = getMCTypeFacade().createOptionalTypeOf(getMCTypeFacade().createQualifiedType(superProdFullName));
    ASTCDParameter fileNameParameter = getCDParameterFacade().createParameter(String.class, "fileName");
    ASTCDMethod parse = getCDMethodFacade().createMethod(PUBLIC.build(), returnType, "parse" + prodName, fileNameParameter);
    parse.setCDThrowsDeclaration(CD4CodeBasisMill.cDThrowsDeclarationBuilder().addException(ioException).build());
    this.replaceTemplate(EMPTY_BODY, parse, new TemplateHookPoint(TEMPLATE_PATH + "ParseSup", millFullName, prodName));
    methods.add(parse);

    ASTMCType readerType = getMCTypeFacade().createQualifiedType("java.io.Reader");
    ASTCDParameter readerParameter = getCDParameterFacade().createParameter(readerType, "reader");
    ASTCDMethod parseReader = getCDMethodFacade().createMethod(PUBLIC.build(), returnType, "parse" + prodName, readerParameter);
    parseReader.setCDThrowsDeclaration(CD4CodeBasisMill.cDThrowsDeclarationBuilder().addException(ioException).build());
    this.replaceTemplate(EMPTY_BODY, parseReader, new TemplateHookPoint(TEMPLATE_PATH + "ParseSupReader", millFullName, prodName));
    methods.add(parseReader);
    return methods;
  }

  // TODO (MB|ND): Kann man diese Methode durch etwas anderes ersetzen?
  protected List<CDTypeSymbol> getSuperTypesTransitive(CDTypeSymbol startType) {
    List<CDTypeSymbol> superTypes = new ArrayList();
    if (startType.isPresentSuperClass()) {
      SymTypeExpression ste = startType.getSuperClass();
      CDTypeSymbolSurrogate s = new CDTypeSymbolSurrogate(ste.getTypeInfo().getFullName());
      s.setEnclosingScope(ste.getTypeInfo().getEnclosingScope());
      superTypes.add(s.lazyLoadDelegate());
      superTypes.addAll(getSuperTypesTransitive(s.lazyLoadDelegate()));
    }

    for (SymTypeExpression ste : startType.getSuperTypesList()) {
      CDTypeSymbolSurrogate tss = new CDTypeSymbolSurrogate(ste.getTypeInfo().getFullName());
      tss.setEnclosingScope(ste.getTypeInfo().getEnclosingScope());
      CDTypeSymbol i = tss.lazyLoadDelegate();
      superTypes.add(i);
      superTypes.addAll(getSuperTypesTransitive(i));
    }
    return superTypes;
  }



}
