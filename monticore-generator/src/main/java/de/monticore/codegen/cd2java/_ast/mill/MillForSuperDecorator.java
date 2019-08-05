package de.monticore.codegen.cd2java._ast.mill;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.ast.AstGeneratorHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClassTOP;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisMill;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.StringTransformations;

public class MillForSuperDecorator extends AbstractDecorator<ASTCDCompilationUnit, Collection<ASTCDClass>> {

  private static final String MILL_SUFFIX = "Mill";

  private static final String BUILDER = "Builder";
  
  private static final String MILL_FOR = "MillFor";

  private ASTCDDefinition astcdDefinition;

  private final AbstractService<?> service;

  public MillForSuperDecorator(final GlobalExtensionManagement glex, final AbstractService<?> service) {
    super(glex);
    this.service = service;
  }

  public Collection<ASTCDClass> decorate(final ASTCDCompilationUnit compilationUnit) {
    astcdDefinition = compilationUnit.getCDDefinition().deepClone();
    //filter out all classes that are abstract and remove AST prefix
    astcdDefinition.setCDClassList(astcdDefinition.getCDClassList()
        .stream()
        .filter(ASTCDClassTOP::isPresentModifier)
        .filter(x -> !x.getModifier().isAbstract())
        .collect(Collectors.toList()));
    
    Collection<CDSymbol> superSymbolList = service.getSuperCDs();
    List<ASTCDClass> superMills = new ArrayList<ASTCDClass>();
    List<ASTCDClass> astcdClassList = Lists.newArrayList(astcdDefinition.getCDClassList());
    
    for (CDSymbol superSymbol : superSymbolList) {
      String millClassName = superSymbol.getName() + MILL_FOR + astcdDefinition.getName();
      List<ASTCDMethod> builderMethodsList = addBuilderMethodsForSuper(astcdClassList, superSymbol, superSymbolList);
      ASTReferenceType superclass = this.getCDTypeFacade().createSimpleReferenceType(
          AstGeneratorHelper.getAstPackage(superSymbol.getFullName()) + superSymbol.getName() + MILL_SUFFIX);
      
      superMills.add(CD4AnalysisMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(millClassName)
        .setSuperclass(superclass)
        .addAllCDMethods(builderMethodsList)
        .build());
    }
    
    return superMills;
  }

  protected List<ASTCDMethod> addBuilderMethodsForSuper(List<ASTCDClass> astcdClassList, CDSymbol superSymbol, Collection<CDSymbol> superSymbolList) {
    List<ASTCDMethod> builderMethodsList = new ArrayList<ASTCDMethod>();
    
    HashMap<CDSymbol, Collection<CDTypeSymbol>> overridden = Maps.newHashMap();
    Collection<CDTypeSymbol> firstClasses = Lists.newArrayList();
    calculateOverriddenCds(service.getCDSymbol(), astcdClassList.stream().map(ASTCDClass::getName).collect(Collectors.toList()), overridden, firstClasses);
    Collection<CDTypeSymbol> cdsForSuper = overridden.get(superSymbol);
    
    // check if super cds exist
    if (cdsForSuper == null) {
      return builderMethodsList;
    }
    
    // Add builder-creating methods
    for (CDTypeSymbol cdType : cdsForSuper) {
      if (!cdType.getAstNode().isPresent()) {
        continue;
      }
      ASTCDClass clazz = (ASTCDClass) cdType.getAstNode().get();
      if (AstGeneratorHelper.isAbstract(clazz) || !GeneratorHelper.getPlainName(clazz).startsWith(GeneratorHelper.AST_PREFIX)) {
        continue;
      }
      
      String astName = clazz.getName();
      String methodName = StringTransformations.uncapitalize(astName.replaceFirst("AST", "")) + BUILDER;
      ASTCDMethod protectedMethod = null;
      
      // Add method body based on whether method is overridden by this cdType
      if (firstClasses.contains(cdType)) {
        ASTType builderType = this.getCDTypeFacade().createSimpleReferenceType(astName + BUILDER);
        protectedMethod = this.getCDMethodFacade().createMethod(PROTECTED, builderType, "_" + methodName);
        this.replaceTemplate(EMPTY_BODY, protectedMethod, new TemplateHookPoint("_ast.mill.ProtectedBuilderForSuperMethod", astcdDefinition.getName() + MILL_SUFFIX, methodName));
      }
      else {
        ASTType builderType = this.getCDTypeFacade().createSimpleReferenceType(AstGeneratorHelper.getAstPackage(superSymbol.getFullName()) + astName + BUILDER);
        protectedMethod = this.getCDMethodFacade().createMethod(PROTECTED, builderType, "_" + methodName);
        this.replaceTemplate(EMPTY_BODY, protectedMethod, new StringHookPoint("Log.error(\"0xA7009" + AstGeneratorHelper.getGeneratedErrorCode(clazz) + " Overridden production " + AstGeneratorHelper.getPlainName(clazz) + " is not reachable\");\nreturn null;\n"));
      }
      builderMethodsList.add(protectedMethod);
    }
    
    return builderMethodsList;
  }
  
  protected void calculateOverriddenCds(CDSymbol cd, Collection<String> nativeClasses, HashMap<CDSymbol, Collection<CDTypeSymbol>> overridden, Collection<CDTypeSymbol> firstClasses) {
    HashMap<String, CDTypeSymbol> l = Maps.newHashMap();
    Collection<CDSymbol> importedClasses = cd.getImports().stream().map(service::resolveCD).collect(Collectors.toList());
    for (CDSymbol superCd : importedClasses) {
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

}
