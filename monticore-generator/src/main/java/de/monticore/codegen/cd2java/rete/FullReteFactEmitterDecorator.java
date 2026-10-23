/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.rete;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.codegen.CD2JavaTemplates.JAVADOC;
import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.rete.ReteConstants.*;

/**
 * creates the full fact emitter of a grammar, which reports the facts of whole
 * AST subtrees: it registers the fact emitters of the grammar and of all its
 * (transitive) super grammars in an inheritance traverser of the grammar, like
 * the FullPrettyPrinter does for the pretty printers
 */
public class FullReteFactEmitterDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected static final String INITIALIZE_TRAVERSER = "initializeTraverser";

  protected static final String GET_TRAVERSER = "getTraverser";

  protected final ReteService reteService;

  protected final VisitorService visitorService;

  public FullReteFactEmitterDecorator(final GlobalExtensionManagement glex,
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
    String className = reteService.getFullFactEmitterSimpleName();

    ASTCDClass fullEmitterClass = CD4CodeMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(className)
        .setCDExtendUsage(CD4CodeMill.cDExtendUsageBuilder()
            .addSuperclass(getMCTypeFacade().createQualifiedType(FULL_FACT_EMITTER_TYPE)).build())
        .addCDMember(createConstructor(className))
        .addCDMember(createInitializeTraverserMethod())
        .addCDMember(createGetTraverserMethod())
        .build();

    replaceTemplate(JAVADOC, fullEmitterClass, new StringHookPoint("""
        /**
         * Reports the facts of whole AST subtrees of the language {@code %s} to a
         * {@link de.monticore.rete.ReteNetwork}, using the fact emitters of the
         * language and of all its super languages. Requires an initialized
         * {@link %s}.
         */
        """.formatted(reteService.getCDName(), reteService.getMillFullName())));

    return fullEmitterClass;
  }

  protected ASTCDConstructor createConstructor(String className) {
    ASTCDParameter networkParameter = getCDParameterFacade().createParameter(
        getMCTypeFacade().createQualifiedType(RETE_NETWORK_TYPE), "network");
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), className,
        networkParameter);
    this.replaceTemplate(EMPTY_BODY, constructor,
        new TemplateHookPoint("_rete.FullFactEmitterConstructor", reteService.getMillFullName()));
    return constructor;
  }

  /**
   * creates the method registering the fact emitters of the grammar and of all
   * its super grammars in the traverser
   */
  protected ASTCDMethod createInitializeTraverserMethod() {
    List<List<String>> emitters = new ArrayList<>();
    emitters.add(describeEmitter(reteService.getCDSymbol()));
    for (DiagramSymbol superCD : reteService.getSuperCDsTransitive()) {
      emitters.add(describeEmitter(superCD));
    }
    ASTCDMethod method = getCDMethodFacade().createMethod(PROTECTED.build(), INITIALIZE_TRAVERSER);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_rete.FullFactEmitterInit", emitters));
    return method;
  }

  /**
   * @return [full name of the emitter class, variable name, add4 method of the traverser]
   */
  protected List<String> describeEmitter(DiagramSymbol cdSymbol) {
    return List.of(reteService.getFactEmitterFullName(cdSymbol),
        StringTransformations.uncapitalize(cdSymbol.getName()) + "Emitter",
        "add4" + cdSymbol.getName());
  }

  /**
   * overrides the getter of the base class with the traverser type of the language
   */
  protected ASTCDMethod createGetTraverserMethod() {
    String traverserType = visitorService.getTraverserInterfaceFullName();
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(),
        getMCTypeFacade().createQualifiedType(traverserType), GET_TRAVERSER);
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return (" + traverserType + ") traverser;"));
    return method;
  }
}
