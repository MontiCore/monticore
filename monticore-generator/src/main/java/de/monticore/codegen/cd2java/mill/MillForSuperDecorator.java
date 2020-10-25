/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.mill;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDDefinition;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.StringTransformations;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.cd.facade.CDModifier.PUBLIC_STATIC;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PREFIX;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILDER_SUFFIX;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;

public class MillForSuperDecorator extends AbstractCreator<ASTCDCompilationUnit, Collection<ASTCDClass>> {

  protected ASTCDDefinition astcdDefinition;

  protected final AbstractService<?> service;
  protected final VisitorService visitorService;

  public MillForSuperDecorator(final GlobalExtensionManagement glex, final AbstractService<?> service, VisitorService visitorService) {
    super(glex);
    this.service = service;
    this.visitorService = visitorService;
  }

  public List<ASTCDClass> decorate(final ASTCDCompilationUnit compilationUnit) {
    astcdDefinition = compilationUnit.getCDDefinition().deepClone();
    //filter out all classes that are abstract and remove AST prefix
    astcdDefinition.setCDClassList(astcdDefinition.getCDClassList()
        .stream()
        .filter(ASTCDClass::isPresentModifier)
        .filter(x -> !x.getModifier().isAbstract())
        .collect(Collectors.toList()));

    Collection<CDDefinitionSymbol> superSymbolList = service.getSuperCDsTransitive();
    List<ASTCDClass> superMills = new ArrayList<ASTCDClass>();
    List<ASTCDClass> astcdClassList = Lists.newArrayList(astcdDefinition.getCDClassList());

    for (CDDefinitionSymbol superSymbol : superSymbolList) {
      String millClassName = superSymbol.getName() + MillConstants.MILL_FOR + astcdDefinition.getName();
      List<ASTCDMethod> builderMethodsList = addBuilderMethodsForSuper(astcdClassList, superSymbol);
      String basePackage = superSymbol.getPackageName().isEmpty() ? "" : superSymbol.getPackageName().toLowerCase() + ".";

      ASTMCQualifiedType superclass = this.getMCTypeFacade().createQualifiedType(
          basePackage + superSymbol.getName().toLowerCase() + "." + superSymbol.getName() + MillConstants.MILL_SUFFIX);

      List<ASTCDMethod> correctScopeMethods = createScopeMethods(basePackage + superSymbol.getName(), service.hasStartProd(superSymbol.getAstNode()), service.getCDSymbol().getPackageName()+ ".", service.getCDName());

      superMills.add(CD4AnalysisMill.cDClassBuilder()
          .setModifier(PUBLIC.build())
          .setName(millClassName)
          .setSuperclass(superclass)
          .addAllCDMethods(builderMethodsList)
          .addAllCDMethods(correctScopeMethods)
          .addCDMethod(getSuperTraverserMethod(superSymbol))
          .build());
    }

    return superMills;
  }

  protected List<ASTCDMethod> addBuilderMethodsForSuper(List<ASTCDClass> astcdClassList, CDDefinitionSymbol superSymbol) {
    List<ASTCDMethod> builderMethodsList = new ArrayList<>();

    HashMap<CDDefinitionSymbol, Collection<CDTypeSymbol>> overridden = Maps.newHashMap();
    Collection<CDTypeSymbol> firstClasses = Lists.newArrayList();
    calculateOverriddenCds(service.getCDSymbol(), astcdClassList.stream().map(ASTCDClass::getName).collect(Collectors.toList()), overridden, firstClasses);
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
        protectedMethod = this.getCDMethodFacade().createMethod(PROTECTED, builderType, "_" + methodName);
        this.replaceTemplate(EMPTY_BODY, protectedMethod, new TemplateHookPoint("mill.ProtectedBuilderForSuperMethod",
            service.getMillFullName(), methodName));
      } else {
        ASTMCQualifiedType builderType = this.getMCTypeFacade().createQualifiedType(service.getASTPackage(superSymbol) + "." + astName + BUILDER_SUFFIX);
        protectedMethod = this.getCDMethodFacade().createMethod(PROTECTED, builderType, "_" + methodName);
        this.replaceTemplate(EMPTY_BODY, protectedMethod, new StringHookPoint("Log.error(\"0xA7009" +
            service.getGeneratedErrorCode(clazz.getName() + cdType.getFullName()) + " Overridden production " +
            clazz.getName() + " is not reachable\");\nreturn null;\n"));
      }
      builderMethodsList.add(protectedMethod);
    }

    return builderMethodsList;
  }

  protected void calculateOverriddenCds(CDDefinitionSymbol cd, Collection<String> nativeClasses, HashMap<CDDefinitionSymbol,
      Collection<CDTypeSymbol>> overridden, Collection<CDTypeSymbol> firstClasses) {
    HashMap<String, CDTypeSymbol> l = Maps.newHashMap();
    Collection<CDDefinitionSymbol> importedClasses = cd.getImports().stream().map(service::resolveCD).collect(Collectors.toList());
    for (CDDefinitionSymbol superCd : importedClasses) {
      Collection<CDTypeSymbol> overriddenSet = Lists.newArrayList();
      for (String className : nativeClasses) {
        Optional<CDTypeSymbol> cdType = superCd.getType(className);
        if (cdType.isPresent()) {
          overriddenSet.add(cdType.get());
          boolean ignore = firstClasses.stream().filter(s -> s.getName().equals(className)).count() > 0;
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

  public List<ASTCDMethod> createScopeMethods(String fullSuperSymbolName, boolean superSymbolHasStartProd, String packageName, String grammarName){
    List<ASTCDMethod> methods = Lists.newArrayList();
    //if the super symbol does not have a start prod the mill of the super grammar (the superclass of this class) does not have methods for the artifactscope and globalscope
    String[] nameParts = fullSuperSymbolName.split("\\.");
    String superSymbolSimpleName = nameParts[nameParts.length-1];
    if(superSymbolHasStartProd && service.hasStartProd()){
      //additionally create scope builder for artifact and global scope
      methods.add(getScopeMethods(packageName, grammarName, superSymbolSimpleName, ARTIFACT_PREFIX));
      methods.add(getScopeMethods(packageName, grammarName, superSymbolSimpleName, GLOBAL_SUFFIX));
    }
    //create scope builder for normal scope
    methods.add(getScopeMethods(packageName, grammarName, superSymbolSimpleName, ""));
    return methods;
  }

  protected ASTCDMethod getScopeMethods(String packageName, String grammarName, String superSymbolSimpleName, String prefix) {
    if(packageName.equals(".")){
      packageName = "";
    }
    String grammarMillName = service.getMillFullName();
    String scopeClassName = grammarName + prefix + SCOPE_SUFFIX;;
    String scopeInterfaceName = "I" + scopeClassName;
    String returnType = packageName + grammarName.toLowerCase() + "." + SYMBOL_TABLE_PACKAGE + "." + scopeInterfaceName;
    String methodName = "_"+ StringTransformations.uncapitalize(superSymbolSimpleName) + prefix + SCOPE_SUFFIX;
    String scopeName = StringTransformations.uncapitalize(grammarName + prefix + SCOPE_SUFFIX);
    ASTCDMethod scopeMethod = getCDMethodFacade().createMethod(PROTECTED, getMCTypeFacade().createQualifiedType(returnType), methodName);
    this.replaceTemplate(EMPTY_BODY, scopeMethod, new TemplateHookPoint("mill.ProtectedScopeMethodForSuper", grammarMillName, scopeName));
    return scopeMethod;
  }

  /**
   * Creates the protected internal traverser method for the given cd symbol.
   * 
   * @param cdSymbol The symbol of the given class diagram
   * @return The list of all internal traverser accessor methods
   */
  protected ASTCDMethod getSuperTraverserMethod(CDDefinitionSymbol cdSymbol) {
      String traverserName = visitorService.getTraverserSimpleName(cdSymbol);
      String traverserType = visitorService.getTraverserFullName();
      String traverserInterfaceType = visitorService.getTraverserInterfaceFullName(cdSymbol);
      return getAttributeMethod(traverserName, traverserType, traverserInterfaceType);
  }
  
  /**
   * Creates protected internal method for a given attribute. The attribute is
   * specified by its simple name, its qualified type, and the qualified return
   * type of the methods. The return type of the method may by equals to the
   * attribute type or a corresponding super type.
   * 
   * @param attributeName The name of the attribute
   * @param attributeType The qualified type of the attribute
   * @param methodType The return type of the methods
   * @return The internal method for the attribute
   */
  protected ASTCDMethod getAttributeMethod(String attributeName, String attributeType, String methodType) {
    // method names and return type
    String protectedMethodName = "_" + StringTransformations.uncapitalize(attributeName);
    ASTMCType returnType = getMCTypeFacade().createQualifiedType(methodType);
    
    // protected internal method
    ASTCDMethod protectedMethod = getCDMethodFacade().createMethod(PROTECTED, returnType, protectedMethodName);
    this.replaceTemplate(EMPTY_BODY, protectedMethod, new StringHookPoint("return new " + attributeType + "();"));
    
    return protectedMethod;
  }

}
