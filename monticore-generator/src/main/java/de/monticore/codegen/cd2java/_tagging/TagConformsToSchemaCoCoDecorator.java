/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._tagging;

import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.tagging.tags._ast.*;

import de.monticore.tagging.tagschema._symboltable.TagSchemaSymbol;

import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.*;

/**
 * Decorate the TagConformsToSchemaCoCo, etc.
 */
public class TagConformsToSchemaCoCoDecorator extends AbstractDecorator {
  protected final AbstractService<?> service;
  protected final VisitorService visitorService;
  protected final ASTMCGrammar originalGrammar;

  public TagConformsToSchemaCoCoDecorator(GlobalExtensionManagement glex, AbstractService<?> service, VisitorService visitorService, ASTMCGrammar originalGrammar) {
    super(glex);
    this.service = service;
    this.visitorService = visitorService;
    this.originalGrammar = originalGrammar;
  }

  public List<ASTCDElement> decorate() {
    if (originalGrammar.getSymbol().getStartProd().isEmpty()) {
      return new ArrayList<>(); // Abort for grammars without a start node
    }
    List<ASTCDElement> elements = new ArrayList<>();
    String cocoName = StringTransformations.capitalize(originalGrammar.getName()) + "TagConformsToSchemaCoCo";

    // Retrieve the FQN of the start production (may be a prod of another grammar)
    List<String> pck = new ArrayList<>();
    if (!originalGrammar.getSymbol().getStartProd().get().getPackageName().isEmpty())
      pck.add(originalGrammar.getSymbol().getStartProd().get().getPackageName());
    pck.add(originalGrammar.getSymbol().getStartProd().get().getEnclosingScope().getName().toLowerCase());
    pck.add("_ast");
    pck.add("AST" + StringTransformations.capitalize(originalGrammar.getSymbol().getStartProd().get().getName()));
    String startProd = Joiners.DOT.join(pck);

    ASTCDClass cocoClass = CD4CodeMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setName(cocoName)
            .setCDExtendUsage(CD4CodeMill.cDExtendUsageBuilder().addSuperclass(mcTypeFacade.createBasicGenericTypeOf("de.monticore.tagging.conforms.TagConformsToSchemaCoCo", startProd)).build())
            .build();
    CD4C.getInstance().addImport(cocoClass, "de.monticore.tagging.conforms.TagConformanceChecker");
    CD4C.getInstance().addImport(cocoClass, "de.monticore.tagging.conforms.TagDataVisitor");
    CD4C.getInstance().addImport(cocoClass, "de.monticore.tagging.tags._visitor.TagsTraverser");
    CD4C.getInstance().addImport(cocoClass, "de.monticore.tagging.tags.TagsMill");
    CD4C.getInstance().addImport(cocoClass, getNames(originalGrammar.getSymbol(), "_ast", "*"));
    CD4C.getInstance().addImport(cocoClass, getNames(originalGrammar.getSymbol(), "_visitor", originalGrammar.getName() + "Traverser"));
    CD4C.getInstance().addImport(cocoClass, getNames(originalGrammar.getSymbol(), originalGrammar.getName() + "Mill"));

    elements.add(cocoClass);

    ASTCDConstructor constructor = cdConstructorFacade.createConstructor(PUBLIC.build(), cocoClass.getName(), cdParameterFacade.createParameter(startProd, "model"));
    cocoClass.addCDMember(constructor);
    this.replaceTemplate(EMPTY_BODY, constructor, new TemplateHookPoint("tagging.coco.Constructor"));

    ASTCDMethod method;
    cocoClass.addCDMember((method = cdMethodFacade.createMethod(PUBLIC.build(), "check",
            cdParameterFacade.createParameter(ASTTagUnit.class.getName(), "tagUnit"),
            cdParameterFacade.createParameter(TagSchemaSymbol.class.getName(), "tagSchemaSymbol")
    )));
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("tagging.coco.Check", originalGrammar.getName()));

    ASTCDClass tagDataVisitorClass = CD4CodeMill.cDClassBuilder()
            .setName(originalGrammar.getName() + "TagDataVisitor")
            .setModifier(PUBLIC.build())
            .setCDExtendUsage(CD4CodeMill.cDExtendUsageBuilder().addSuperclass(mcTypeFacade.createQualifiedType("de.monticore.tagging.conforms.TagDataVisitor")).build())
            .build();
    elements.add(tagDataVisitorClass);
    ASTMCQualifiedType specificTagDefinitionTraverserType = mcTypeFacade.createQualifiedType(originalGrammar.getName() + "TagDefinitionTraverser");

    // Traverser working on de.monticore.tagging.tags._ast.ASTModelElementIdentifier for tag-TargetElements
    tagDataVisitorClass.addCDMember(cdAttributeFacade.createAttribute(PROTECTED.build(), specificTagDefinitionTraverserType, "identifierTravers"));
    // Traverser working on de.monticore.tagging.tags._ast.ASTModelElementIdentifier for within-contexts
    tagDataVisitorClass.addCDMember(cdAttributeFacade.createAttribute(PROTECTED.build(), specificTagDefinitionTraverserType, "contextWithinTravers"));

    tagDataVisitorClass.addCDMember((method = cdMethodFacade.createMethod(PROTECTED.build(), specificTagDefinitionTraverserType, "getIdentifierTraverser")));
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("tagging.coco.GetTraverser", originalGrammar.getName(), "identifierTravers"));

    tagDataVisitorClass.addCDMember((method = cdMethodFacade.createMethod(PROTECTED.build(), specificTagDefinitionTraverserType, "getContextWithinTraverser")));
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("tagging.coco.GetTraverser", originalGrammar.getName(), "contextWithinTravers"));

    tagDataVisitorClass.addCDMember((constructor = cdConstructorFacade.createConstructor(PUBLIC.build(), tagDataVisitorClass.getName(),
            cdParameterFacade.createParameter(TagSchemaSymbol.class.getName(), "tagSchemaSymbol"),
            cdParameterFacade.createParameter("de.monticore.tagging.conforms.TagConformanceChecker", "tagConformanceChecker"))));
    this.replaceTemplate(EMPTY_BODY, constructor, new TemplateHookPoint("tagging.coco.VisitorConstructor",
            originalGrammar.getName(),
            originalGrammar.getSymbol().getProds().stream()
                    .filter(p -> !p.isIsInterface() && !p.isIsExternal() && !p.isIsLexerProd())
                    .filter(p -> !p.isIsIndirectLeftRecursive() && !p.isIsDirectLeftRecursive()) // skip left-rec (as not Identifiers are present in the TagSchemas)
                    .map(ProdSymbol::getName)
                    .collect(Collectors.toList())));

    CD4C.getInstance().addImport(tagDataVisitorClass, getTagDefNames(originalGrammar.getSymbol(), "_visitor", "*"));
    CD4C.getInstance().addImport(tagDataVisitorClass, getTagDefNames(originalGrammar.getSymbol(), "_ast", "*"));
    CD4C.getInstance().addImport(tagDataVisitorClass, getTagDefNames(originalGrammar.getSymbol(), originalGrammar.getName() + TaggingConstants.TAGDEFINITION_SUFFIX + "Mill"));
    CD4C.getInstance().addImport(tagDataVisitorClass, ASTTag.class.getName());
    return elements;
  }

  protected String getNames(MCGrammarSymbol symbol, String... parts) {
    List<String> pck = symbol.getPackageName().isEmpty() ? new ArrayList<>() : new ArrayList<>(Splitters.DOT.splitToList(symbol.getPackageName()));
    pck.add(symbol.getName().toLowerCase());
    pck.addAll(Arrays.asList(parts));
    return Joiners.DOT.join(pck);
  }

  protected String getTagDefNames(MCGrammarSymbol symbol, String... parts) {
    List<String> pck = symbol.getPackageName().isEmpty() ? new ArrayList<>() : new ArrayList<>(Splitters.DOT.splitToList(symbol.getPackageName()));
    pck.add(symbol.getName().toLowerCase() + TaggingConstants.TAGDEFINITION_SUFFIX.toLowerCase());
    pck.addAll(Arrays.asList(parts));
    return Joiners.DOT.join(pck);
  }
}
