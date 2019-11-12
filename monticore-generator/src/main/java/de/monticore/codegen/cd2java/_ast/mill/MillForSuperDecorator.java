/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.mill;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.StringTransformations;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PREFIX;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILDER_SUFFIX;
import static de.monticore.codegen.cd2java._ast.mill.MillConstants.MILL_FOR;
import static de.monticore.codegen.cd2java._ast.mill.MillConstants.MILL_SUFFIX;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class MillForSuperDecorator extends AbstractCreator<ASTCDCompilationUnit, Collection<ASTCDClass>> {

  protected ASTCDDefinition astcdDefinition;

  protected final AbstractService<?> service;

  public MillForSuperDecorator(final GlobalExtensionManagement glex, final AbstractService<?> service) {
    super(glex);
    this.service = service;
  }

  public List<ASTCDClass> decorate(final ASTCDCompilationUnit compilationUnit) {
    astcdDefinition = compilationUnit.getCDDefinition().deepClone();
    //filter out all classes that are abstract and remove AST prefix
    astcdDefinition.setCDClassList(astcdDefinition.getCDClassList()
        .stream()
        .filter(ASTCDClassTOP::isPresentModifier)
        .filter(x -> !x.getModifier().isAbstract())
        .collect(Collectors.toList()));

    Collection<CDDefinitionSymbol> superSymbolList = service.getSuperCDsTransitive();
    List<ASTCDClass> superMills = new ArrayList<ASTCDClass>();
    List<ASTCDClass> astcdClassList = Lists.newArrayList(astcdDefinition.getCDClassList());

    for (CDDefinitionSymbol superSymbol : superSymbolList) {
      String millClassName = superSymbol.getName() + MILL_FOR + astcdDefinition.getName();
      List<ASTCDMethod> builderMethodsList = addBuilderMethodsForSuper(astcdClassList, superSymbol);
      ASTMCQualifiedType superclass = this.getMCTypeFacade().createQualifiedType(
          service.getASTPackage(superSymbol) + "." + superSymbol.getName() + MILL_SUFFIX);

      superMills.add(CD4AnalysisMill.cDClassBuilder()
          .setModifier(PUBLIC.build())
          .setName(millClassName)
          .setSuperclass(superclass)
          .addAllCDMethods(builderMethodsList)
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
        ASTMCType builderType = this.getMCTypeFacade().createQualifiedType(astName + BUILDER_SUFFIX);
        protectedMethod = this.getCDMethodFacade().createMethod(PROTECTED, builderType, "_" + methodName);
        this.replaceTemplate(EMPTY_BODY, protectedMethod, new TemplateHookPoint("_ast.mill.ProtectedBuilderForSuperMethod",
            astcdDefinition.getName() + MILL_SUFFIX, methodName));
      } else {
        ASTMCQualifiedType builderType = this.getMCTypeFacade().createQualifiedType(service.getASTPackage(superSymbol) + "." + astName + BUILDER_SUFFIX);
        protectedMethod = this.getCDMethodFacade().createMethod(PROTECTED, builderType, "_" + methodName);
        this.replaceTemplate(EMPTY_BODY, protectedMethod, new StringHookPoint("Log.error(\"0xA7009" + DecorationHelper
            .getGeneratedErrorCode(clazz) + " Overridden production " + clazz.getName() + " is not reachable\");\nreturn null;\n"));
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

}
