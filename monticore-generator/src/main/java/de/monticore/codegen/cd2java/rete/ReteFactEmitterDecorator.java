/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.rete;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.codegen.CD2JavaTemplates.JAVADOC;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISIT;
import static de.monticore.codegen.cd2java.rete.ReteConstants.*;

/**
 * creates the fact emitter of a grammar, a visitor reporting the feature values
 * of the AST nodes of the grammar to a de.monticore.rete.ReteNetwork: for each
 * AST type, its visit method reports the values of exactly the features the
 * ReteFeatureDecorator creates for this type. The nodes themselves are
 * reported by the FullReteFactEmitter.
 * <p>
 * Used with an inheritance traverser, every node is visited once for its
 * concrete class and once for each super type, so each fact is reported
 * exactly once, also for types inherited from super grammars.
 */
public class ReteFactEmitterDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected final ReteService reteService;

  protected final VisitorService visitorService;

  public ReteFactEmitterDecorator(final GlobalExtensionManagement glex,
                                  final ReteService reteService,
                                  final VisitorService visitorService) {
    super(glex);
    this.reteService = reteService;
    this.visitorService = visitorService;
  }

  public void decorate(ASTCDCompilationUnit input, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage retePackage = getPackage(input, decoratedCD, RETE_PACKAGE);
    retePackage.addCDElement(decorate(input));
  }

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit input) {
    String className = reteService.getFactEmitterSimpleName();

    ASTCDClass emitterClass = CD4CodeMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(className)
        .setCDExtendUsage(CD4CodeMill.cDExtendUsageBuilder()
            .addSuperclass(getMCTypeFacade().createQualifiedType(FACT_EMITTER_TYPE)).build())
        .setCDInterfaceUsage(CD4CodeMill.cDInterfaceUsageBuilder()
            .addInterface(getMCTypeFacade().createQualifiedType(visitorService.getVisitor2FullName())).build())
        .addCDMember(createConstructor(className))
        .addAllCDMembers(createVisitMethods(input.getCDDefinition()))
        .build();

    replaceTemplate(JAVADOC, emitterClass, new StringHookPoint("""
        /**
         * Reports the values of the features in {@link %s} of the AST nodes of
         * the language {@code %s} to a {@link de.monticore.rete.ReteNetwork}.
         * Only handles the types of this language; use the {@link %s} to report
         * the facts of whole subtrees, including the nodes themselves.
         */
        """.formatted(reteService.getFeaturesSimpleName(), reteService.getCDName(),
        reteService.getFullFactEmitterSimpleName())));

    return emitterClass;
  }

  protected ASTCDConstructor createConstructor(String className) {
    ASTCDParameter networkParameter = getCDParameterFacade().createParameter(
        getMCTypeFacade().createQualifiedType(RETE_NETWORK_TYPE), "network");
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), className,
        networkParameter);
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("super(network);"));
    return constructor;
  }

  /**
   * creates a visit method for each type declaring features
   */
  protected List<ASTCDMethod> createVisitMethods(ASTCDDefinition cdDefinition) {
    List<ASTCDMethod> visitMethods = new ArrayList<>();
    for (ASTCDType type : reteService.getFeatureTypes(cdDefinition)) {
      List<String> facts = createFactStatements(type);
      if (facts.isEmpty()) {
        continue;
      }
      String astFullName = reteService.createASTFullName(type.getSymbol());
      ASTCDMethod visitMethod = visitorService.getVisitorMethod(VISIT,
          getMCTypeFacade().createQualifiedType(astFullName));
      this.replaceTemplate(EMPTY_BODY, visitMethod, new TemplateHookPoint("_rete.FactEmitterVisit", facts));
      visitMethods.add(visitMethod);
    }
    return visitMethods;
  }

  /**
   * @return one statement per feature of the type reporting its values, e.g.
   * {@code fact(AutomataReteFeatures.STATE_NAME, node, node.getName());}
   */
  protected List<String> createFactStatements(ASTCDType type) {
    List<String> statements = new ArrayList<>();
    for (ASTCDAttribute attribute : reteService.getFeatureAttributes(type)) {
      String feature = reteService.getFeaturesSimpleName() + "."
          + reteService.getFeatureConstantName(type, attribute);
      String getter = getDecorationHelper().getPlainGetter(attribute);
      if (reteService.isListAttribute(attribute)) {
        statements.add("facts(" + feature + ", node, node." + getter + "());");
      } else if (reteService.isOptionalAttribute(attribute)) {
        String isPresent = "isPresent" + StringTransformations.capitalize(
            getDecorationHelper().getNativeAttributeName(attribute.getName()));
        statements.add("if (node." + isPresent + "()) {\n    fact(" + feature + ", node, node."
            + getter + "());\n  }");
      } else {
        statements.add("fact(" + feature + ", node, node." + getter + "());");
      }
    }
    return statements;
  }
}
