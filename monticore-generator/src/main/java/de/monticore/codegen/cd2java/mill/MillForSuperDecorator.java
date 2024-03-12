/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.mill;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDBasisArtifactScope;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java._parser.ParserService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.StringTransformations;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.*;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILDER_SUFFIX;
import static de.monticore.codegen.cd2java._parser.ParserConstants.FOR_SUFFIX;
import static de.monticore.codegen.cd2java._parser.ParserConstants.PARSER_SUFFIX;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.INHERITANCE_TRAVERSER;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.TRAVERSER;
import static de.monticore.codegen.cd2java.typedispatcher.TypeDispatcherConstants.TYPE_DISPATCHER_SUFFIX;
import static de.monticore.codegen.cd2java.typedispatcher.TypeDispatcherConstants.UTILS_PACKAGE;

public class MillForSuperDecorator extends AbstractCreator<ASTCDCompilationUnit, Collection<ASTCDClass>> {


  protected final AbstractService<?> service;
  protected final VisitorService visitorService;
  protected final ParserService parserService;

  public MillForSuperDecorator(final GlobalExtensionManagement glex,
                               final AbstractService<?> service,
                               final VisitorService visitorService,
                               final ParserService parserService) {
    super(glex);
    this.service = service;
    this.visitorService = visitorService;
    this.parserService = parserService;
  }

  public List<ASTCDClass> decorate(final ASTCDCompilationUnit compilationUnit) {
    //filter out all classes that are abstract and remove AST prefix
    List<ASTCDClass> astcdClassList = compilationUnit.getCDDefinition().getCDClassesList()
        .stream()
        .filter(x -> !x.getModifier().isAbstract())
        .collect(Collectors.toList());

    Collection<DiagramSymbol> superSymbolList = service.getSuperCDsTransitive();
    List<ASTCDClass> superMills = new ArrayList<>();

    Map<DiagramSymbol, Collection<CDTypeSymbol>> overridden = Maps.newLinkedHashMap();
    Collection<CDTypeSymbol> firstClasses = Lists.newArrayList();
    calculateOverriddenCds(service.getCDSymbol(), astcdClassList.stream().map(ASTCDClass::getName).collect(Collectors.toList()), overridden, firstClasses);

    for (DiagramSymbol superSymbol : superSymbolList) {
      String millClassName = superSymbol.getName() + MillConstants.MILL_FOR + compilationUnit.getCDDefinition().getName();
      List<ASTCDMethod> builderMethodsList = addBuilderMethodsForSuper(astcdClassList, superSymbol, overridden, firstClasses);
      String basePackage = superSymbol.getPackageName().isEmpty() ? "" : superSymbol.getPackageName().toLowerCase() + ".";

      ASTMCQualifiedType superclass = this.getMCTypeFacade().createQualifiedType(
          basePackage + superSymbol.getName().toLowerCase() + "." + superSymbol.getName() + MillConstants.MILL_SUFFIX);

      List<ASTCDMethod> correctScopeMethods = createScopeMethods(basePackage + superSymbol.getName(),service.getCDSymbol().getPackageName()+ ".", service.getCDName());
      ASTCDClass superMill = CD4AnalysisMill.cDClassBuilder()
          .setModifier(PUBLIC.build())
          .setName(millClassName)
          .setCDExtendUsage(CD4CodeMill.cDExtendUsageBuilder().addSuperclass(superclass).build())
          .addAllCDMembers(builderMethodsList)
          .addAllCDMembers(correctScopeMethods)
          .addCDMember(getSuperTraverserMethod(superSymbol))
          .addCDMember(getSuperInheritanceTraverserMethod(superSymbol))
          .addCDMember(getSuperTypeDispatcherMethod(superSymbol))
          .addCDMember(getSuperPrettyPrintMethod(superSymbol))
          .build();

      if(!service.hasComponentStereotype(((ASTCDDefinition) superSymbol.getAstNode()).getModifier())){
        if(!service.hasComponentStereotype(((ASTCDDefinition) service.getCDSymbol().getAstNode()).getModifier())) {
          superMill.addCDMember(createParserMethod(superSymbol));
        }
      }
      superMills.add(superMill);
    }

    return superMills;
  }

  protected List<ASTCDMethod> addBuilderMethodsForSuper(List<ASTCDClass> astcdClassList,
                                                        DiagramSymbol superSymbol,
                                                        Map<DiagramSymbol, Collection<CDTypeSymbol>> overridden,
                                                        Collection<CDTypeSymbol> firstClasses) {
    List<ASTCDMethod> builderMethodsList = new ArrayList<>();

    Collection<CDTypeSymbol> cdsForSuper = overridden.get(superSymbol);

    // check if super cds exist
    if (cdsForSuper == null) {
      return builderMethodsList;
    }

    // Add builder-creating methods
    for (CDTypeSymbol cdType : cdsForSuper) {
      if (!cdType.isPresentAstNode()) {
        continue;
      }
      ASTCDClass clazz = (ASTCDClass) cdType.getAstNode();
      if (cdType.isIsAbstract() || !cdType.getName().startsWith(AST_PREFIX)) {
        continue;
      }

      String astName = cdType.getName();
      String methodName = StringTransformations.uncapitalize(astName.replaceFirst("AST", "")) + BUILDER_SUFFIX;
      ASTCDMethod protectedMethod = null;

      // Add method body based on whether method is overridden by this cdType
      if (firstClasses.contains(cdType)) {
        String packageDef = service.getASTPackage(superSymbol);
        ASTMCType builderType = this.getMCTypeFacade().createQualifiedType(packageDef+"."+astName + BUILDER_SUFFIX);
        protectedMethod = this.getCDMethodFacade().createMethod(PROTECTED.build(), builderType, "_" + methodName);
        this.replaceTemplate(EMPTY_BODY, protectedMethod, new TemplateHookPoint("mill.ProtectedBuilderForSuperMethod",
            service.getMillFullName(), methodName));
      } else {
        ASTMCQualifiedType builderType = this.getMCTypeFacade().createQualifiedType(service.getASTPackage(superSymbol) + "." + astName + BUILDER_SUFFIX);
        protectedMethod = this.getCDMethodFacade().createMethod(PROTECTED.build(), builderType, "_" + methodName);
        this.replaceTemplate(EMPTY_BODY, protectedMethod, new StringHookPoint("Log.error(\"0xA7009" +
            service.getGeneratedErrorCode(clazz.getName() + cdType.getFullName()) + " Overridden production " +
            clazz.getName() + " is not reachable\");\nreturn null;\n"));
      }
      builderMethodsList.add(protectedMethod);
    }

    return builderMethodsList;
  }

  // Cache CDTypeSymbol#resolveCDTypeDown
  protected final LoadingCache<Pair<ICDBasisScope, String>, Optional<CDTypeSymbol>> calcOCDCDTypeDownCache = CacheBuilder.newBuilder()
          .maximumSize(10000)
          .build(new CacheLoader<Pair<ICDBasisScope, String>, Optional<CDTypeSymbol>>() {
            @Override
            public Optional<CDTypeSymbol> load(Pair<ICDBasisScope, String> key) {
              return key.getLeft().resolveCDTypeDown(key.getRight());
            }
          });

  protected void calculateOverriddenCds(DiagramSymbol cd, final Collection<String> nativeClasses, Map<DiagramSymbol,
      Collection<CDTypeSymbol>> overridden, Collection<CDTypeSymbol> firstClasses) {
    Map<String, CDTypeSymbol> l = Maps.newLinkedHashMap();
    Collection<DiagramSymbol> importedClasses = ((ICDBasisArtifactScope) cd.getEnclosingScope()).getImportsList().stream()
        .map(i -> i.getStatement())
        .filter(i -> !service.isJava(i))
        .map(service::resolveCD)
        .collect(Collectors.toList());
    for (DiagramSymbol superCd : importedClasses) {
      Collection<CDTypeSymbol> overriddenSet = Lists.newArrayList();
      for (String className : nativeClasses) {
        Optional<CDTypeSymbol> cdType = calcOCDCDTypeDownCache.getUnchecked(Pair.of((ICDBasisScope) superCd.getEnclosingScope(),className));
        if (cdType.isPresent()) {
          overriddenSet.add(cdType.get());
          boolean ignore = firstClasses.stream().anyMatch(s -> s.getName().equals(className));
          if (!ignore && !l.containsKey(className)) {
            l.put(className, cdType.get());
          }
        }
      }
      if (!overriddenSet.isEmpty()) {
        overridden.put(superCd, overriddenSet);
      }
      calculateOverriddenCds(superCd, nativeClasses, overridden, firstClasses);
    }
    firstClasses.addAll(l.values());
  }

  public ASTCDMethod createParserMethod(DiagramSymbol superSymbol){
    String parserForSuper = String.join(".", parserService.getPackage(),
        superSymbol.getName() + PARSER_SUFFIX + FOR_SUFFIX + service.getCDName());
    ASTMCType superSymbolParser = getMCTypeFacade().createQualifiedType(parserService.getParserClassFullName(superSymbol));
    ASTCDMethod method = getCDMethodFacade().createMethod(PROTECTED.build(), superSymbolParser, "_parser");
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return new " + parserForSuper + "();"));
    return method;
  }

  public List<ASTCDMethod> createScopeMethods(String fullSuperSymbolName, String packageName, String grammarName){
    List<ASTCDMethod> methods = Lists.newArrayList();
    //if the super symbol does not have a start prod the mill of the super grammar (the superclass of this class) does not have methods for the artifactscope and globalscope
    String[] nameParts = fullSuperSymbolName.split("\\.");
      //additionally create scope builder for artifact and global scope
    methods.add(getScopeMethods(packageName, grammarName, ARTIFACT_PREFIX));
    methods.add(getScopeMethods(packageName, grammarName, GLOBAL_SUFFIX));

    //create scope builder for normal scope
    methods.add(getScopeMethods(packageName, grammarName, ""));
    return methods;
  }

  protected ASTCDMethod getScopeMethods(String packageName, String grammarName, String prefix) {
    if(packageName.equals(".")){
      packageName = "";
    }
    String grammarMillName = service.getMillFullName();
    String scopeClassName = grammarName + prefix + SCOPE_SUFFIX;
    String scopeInterfaceName = "I" + scopeClassName;
    String returnType = packageName + grammarName.toLowerCase() + "." + SYMBOL_TABLE_PACKAGE + "." + scopeInterfaceName;
    String methodName = "_" + StringTransformations.uncapitalize(prefix + SCOPE_SUFFIX);
    String scopeName = StringTransformations.uncapitalize( prefix + SCOPE_SUFFIX);
    ASTCDMethod scopeMethod = getCDMethodFacade().createMethod(PROTECTED.build(), getMCTypeFacade().createQualifiedType(returnType), methodName);
    this.replaceTemplate(EMPTY_BODY, scopeMethod, new TemplateHookPoint("mill.ProtectedMethodForSuper", grammarMillName, scopeName));
    return scopeMethod;
  }

  /**
   * Creates the protected internal traverser method for the given cd symbol.
   * 
   * @param cdSymbol The symbol of the given class diagram
   * @return The list of all internal traverser accessor methods
   */
  protected ASTCDMethod getSuperTraverserMethod(DiagramSymbol cdSymbol) {
      String traverserInterfaceType = visitorService.getTraverserInterfaceFullName(cdSymbol);
      return getProtectedForSuperMethod(TRAVERSER, traverserInterfaceType);
  }

  /**
   * Creates the protected internal traverser method for the given cd symbol.
   *
   * @param cdSymbol The symbol of the given class diagram
   * @return The list of all internal traverser accessor methods
   */
  protected ASTCDMethod getSuperInheritanceTraverserMethod(DiagramSymbol cdSymbol) {
    String traverserInterfaceType = visitorService.getTraverserInterfaceFullName(cdSymbol);
    return getProtectedForSuperMethod(INHERITANCE_TRAVERSER, traverserInterfaceType);
  }

  protected ASTCDMethod getSuperTypeDispatcherMethod(DiagramSymbol cdSymbol) {
    String dispatcherInterfaceType = String.format("%s.I%s%s",
        visitorService.getPackage(cdSymbol).replace("_visitor", UTILS_PACKAGE),
        cdSymbol.getName(),
        TYPE_DISPATCHER_SUFFIX);
    return getProtectedForSuperMethod("typeDispatcher", dispatcherInterfaceType);
  }

  protected ASTCDMethod getSuperPrettyPrintMethod(DiagramSymbol cdSymbol) {
    ASTMCType returnType = getMCTypeFacade().createStringType();

    ASTMCType nodeType = getMCTypeFacade().createQualifiedType(AST_INTERFACE);
    ASTCDParameter nodeParameter = getCDParameterFacade().createParameter(nodeType, "node");
    ASTCDParameter printCommentsParameter  = getCDParameterFacade().createParameter(getMCTypeFacade().createBooleanType(), "printComments");
    ASTCDMethod method = getCDMethodFacade().createMethod(PROTECTED.build(), returnType, "_prettyPrint", nodeParameter, printCommentsParameter);

    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return " + service.getMillFullName() + ".prettyPrint(node, printComments);"));
    return method;
  }

  /**
   * Creates protected internal method for a given attribute. The method
   * delegates to the public accessor method of the language-specific mill. The
   * method is specified by its simple name and its qualified return type.
   * 
   * @param methodName The name of the method
   * @param methodType The return type of the methods
   * @return The internal method for the attribute
   */
  protected ASTCDMethod getProtectedForSuperMethod(String methodName, String methodType) {
    // method name and return type
    String protectedMethodName = "_" + methodName;
    ASTMCType returnType = getMCTypeFacade().createQualifiedType(methodType);
    
    // protected internal method
    ASTCDMethod protectedMethod = getCDMethodFacade().createMethod(PROTECTED.build(), returnType, protectedMethodName);
    this.replaceTemplate(EMPTY_BODY, protectedMethod, new TemplateHookPoint("mill.ProtectedMethodForSuper", 
        service.getMillFullName(), methodName));
    
    return protectedMethod;
  }

}
