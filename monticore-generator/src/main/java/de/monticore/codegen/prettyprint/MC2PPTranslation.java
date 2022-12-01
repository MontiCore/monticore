// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.prettyprint;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis.CDBasisMill;
import de.monticore.cdbasis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.CoreTemplates;
import de.monticore.codegen.cd2java._ast.ast_class.ASTConstants;
import de.monticore.codegen.cd2java._visitor.VisitorConstants;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._visitor.GrammarTraverser;
import de.monticore.grammar.grammar._visitor.GrammarVisitor2;
import de.monticore.grammar.grammarfamily.GrammarFamilyMill;
import de.monticore.types.mcbasictypes._ast.ASTMCPackageDeclaration;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.umlmodifier.UMLModifierMill;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.StringTransformations;

import static de.monticore.cd.codegen.CD2JavaTemplates.ANNOTATIONS;
import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

import java.util.ArrayList;
import java.util.List;

public class MC2PPTranslation extends AbstractCreator<ASTMCGrammar, ASTCDCompilationUnit> {

  public MC2PPTranslation(GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public ASTCDCompilationUnit decorate(ASTMCGrammar grammar) {
    List<String> packageName = new ArrayList<>(grammar.getPackageList());
    packageName.add(grammar.getName().toLowerCase());


    ASTCDCompilationUnitBuilder cdCompilationUnitBuilder = CDBasisMill.cDCompilationUnitBuilder();


    ASTCDDefinition astcdDefinition = CDBasisMill.cDDefinitionBuilder()
            .setName(grammar.getName())
            .setModifier(UMLModifierMill.modifierBuilder().build())
            .build();

    List<String> prettyPrintPackageName = new ArrayList<>(packageName);
    prettyPrintPackageName.add(PrettyPrinterGenerator.PACKAGE_NAME);
    astcdDefinition.setDefaultPackageName(Joiners.DOT.join(prettyPrintPackageName));
    ASTMCPackageDeclaration packageDecl = CD4CodeMill.mCPackageDeclarationBuilder().setMCQualifiedName(
            CD4CodeMill.mCQualifiedNameBuilder().setPartsList(prettyPrintPackageName).build()).build();

    cdCompilationUnitBuilder.setMCPackageDeclaration(packageDecl);
    cdCompilationUnitBuilder.setCDDefinition(astcdDefinition);

    ASTCDCompilationUnit cdCompilationUnit = cdCompilationUnitBuilder.build();

    // The PrettyPrinter class
    ASTCDClass prettyPrinterCDClass = CDBasisMill.cDClassBuilder().setName(grammar.getName() + "PrettyPrinter")
            .setModifier(CDBasisMill.modifierBuilder().setPublic(true).build())
            .build();
    astcdDefinition.addCDElement(prettyPrinterCDClass);

    // The FullPrettyPrinter class
    ASTCDClass fullPrettyPrinterCDClass = CDBasisMill.cDClassBuilder()
            .setName(grammar.getName() + "FullPrettyPrinter")
            .setModifier(CDBasisMill.modifierBuilder().setPublic(true).build())
            .build();
    astcdDefinition.addCDElement(fullPrettyPrinterCDClass);


    List<MCGrammarSymbol> superGrammars = grammar.getSymbol().getAllSuperGrammars();

    GrammarTraverser traverser = GrammarFamilyMill.traverser();

    // Collect information about the NonTerminals first
    NonTermAccessorVisitor nonTermAccessorVisitor = new NonTermAccessorVisitor();
    traverser.add4Grammar(nonTermAccessorVisitor);
    traverser.setGrammarHandler(new PrettyPrinterReducedTraverseHandler());
    grammar.accept(traverser);


    // Traverse the Grammar ast and decorate the PP-handle methods
    PrettyPrinterGenerationVisitor transformer = new PrettyPrinterGenerationVisitor(glex,
            prettyPrinterCDClass,
            nonTermAccessorVisitor.getClassProds());
    traverser = GrammarFamilyMill.traverser();
    traverser.setGrammarHandler(new PrettyPrinterReducedTraverseHandler());
    traverser.add4Grammar(transformer);

    grammar.accept(traverser);

    // FQN for imports
    String visitorPackage = Joiners.DOT.join(packageName) + "." + VisitorConstants.VISITOR_PACKAGE + "." + grammar.getName();

    prettyPrinterCDClass.setCDInterfaceUsage(
            CDBasisMill.cDInterfaceUsageBuilder()
                    .addInterface(
                            mcTypeFacade.createQualifiedType(visitorPackage + VisitorConstants.HANDLER_SUFFIX))
                    .addInterface(
                            mcTypeFacade.createQualifiedType(visitorPackage + VisitorConstants.VISITOR2_SUFFIX))
                    .build());

    // PrettyPrinter class attributes
    ASTCDAttribute ppPrinterAttribute = addAttribute(prettyPrinterCDClass, true, false, "de.monticore.prettyprint.IndentPrinter", "printer");
    ASTCDAttribute ppPrintCommentsAttribute = addAttribute(prettyPrinterCDClass, true, true, getMCTypeFacade().createBooleanType(), "printComments");
    addAttribute(prettyPrinterCDClass, true, true, Joiners.DOT.join(packageName) + "._visitor." + grammar.getName() + VisitorConstants.TRAVERSER_SUFFIX, "traverser");

    ASTCDConstructor constructor = this.getCDConstructorFacade().createConstructor(CD4CodeMill.modifierBuilder().setPublic(true).build(), prettyPrinterCDClass.getName(),
            getCDParameterFacade().createParameters(ppPrinterAttribute, ppPrintCommentsAttribute));
    prettyPrinterCDClass.addCDMember(constructor);
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("this.printer=printer; this.printComments=printComments;"));

    // And generate the FullPrettyPrinter
    ASTCDAttribute fPPPrinter = addAttribute(fullPrettyPrinterCDClass, true, false, "de.monticore.prettyprint.IndentPrinter", "printer");
    addAttribute(fullPrettyPrinterCDClass, true, true, Joiners.DOT.join(packageName) + "._visitor." + grammar.getName() + VisitorConstants.TRAVERSER_SUFFIX, "traverser");

    constructor = this.getCDConstructorFacade().createConstructor(CD4CodeMill.modifierBuilder().setPublic(true).build(), fullPrettyPrinterCDClass.getName(),
            getCDParameterFacade().createParameters(ppPrinterAttribute, ppPrintCommentsAttribute));
    fullPrettyPrinterCDClass.addCDMember(constructor);
    this.replaceTemplate(EMPTY_BODY, constructor, new TemplateHookPoint("_prettyprinter.full.FPPConstructor",
            grammar.getName(), Joiners.DOT.join(packageName), superGrammars));

    constructor = this.getCDConstructorFacade().createConstructor(CD4CodeMill.modifierBuilder().setPublic(true).build(), fullPrettyPrinterCDClass.getName(),
            getCDParameterFacade().createParameters(fPPPrinter));
    fullPrettyPrinterCDClass.addCDMember(constructor);
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("this(printer, true);"));

    // Add all the AST${SuperGrammar}Node prettyprint methods
    for (MCGrammarSymbol reference : grammar.getSymbol().getAllSuperGrammars()){
      fullPrettyPrinterCDClass.addCDMember(createPrettyPrintNodeMethod(reference));
    }

    // Also add one for the AST${Grammar}Node
    fullPrettyPrinterCDClass.addCDMember(createPrettyPrintNodeMethod(grammar.getSymbol()));

    for (ASTCDClass cdClass : cdCompilationUnit.getCDDefinition().getCDClassesList()) {
      this.replaceTemplate(ANNOTATIONS, cdClass, CoreTemplates.createAnnotationsHookPoint(cdClass.getModifier()));
    }

    return cdCompilationUnit;
  }

  /**
   * Creates the PrettyPrint helper method for nodes of a given grammar symbol
   * @param grammarSymbol the (super) grammar symbol a method should be created for
   * @return the templated method
   */
  protected ASTCDMethod createPrettyPrintNodeMethod(MCGrammarSymbol grammarSymbol) {
    List<String> grammarAstFQN = new ArrayList<>();
    if (!grammarSymbol.getPackageName().isEmpty())
      grammarAstFQN.add(grammarSymbol.getPackageName());
    grammarAstFQN.add(grammarSymbol.getEnclosingScope().getName().toLowerCase());
    grammarAstFQN.add(ASTConstants.AST_PACKAGE);
    grammarAstFQN.add(ASTConstants.AST_PREFIX + StringTransformations.capitalize(grammarSymbol.getName()) + "Node");

    ASTCDMethod method = getCDMethodFacade().createMethod(CD4CodeMill.modifierBuilder().setPublic(true).build(),
            getMCTypeFacade().createStringType(),
            "prettyprint",
            getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(Joiners.DOT.join(grammarAstFQN)), "node"));
    replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_prettyprinter.full.FullPrettyPrintMethod"));
    return method;
  }

  /**
   * @param astcdClass the class
   * @param getter     whether to generate a getter
   * @param setter     whether to generate a setter
   * @param type       the type
   * @param name       the name of the attribute
   * @return the generated attribute
   */
  protected ASTCDAttribute addAttribute(ASTCDClass astcdClass, boolean getter, boolean setter, String type, String name) {
    return this.addAttribute(astcdClass, getter, setter, getMCTypeFacade().createQualifiedType(type), name);
  }

  /**
   * @param astcdClass the class
   * @param getter     whether to generate a getter
   * @param setter     whether to generate a setter
   * @param type       the type
   * @param name       the name of the attribute
   * @return the generated attribute
   */
  protected ASTCDAttribute addAttribute(ASTCDClass astcdClass, boolean getter, boolean setter, ASTMCType type, String name) {
    ASTCDAttribute attribute = this.getCDAttributeFacade().createAttribute(CD4CodeMill.modifierBuilder().setProtected(true).build(), type, name);
    astcdClass.addCDMember(attribute);
    if (getter) {
      String getterName = getMCTypeFacade().isBooleanType(type) ? "is" : "get";
      getterName += StringTransformations.capitalize(name);
      ASTCDMethod method = this.getCDMethodFacade().createMethod(CD4CodeMill.modifierBuilder().setPublic(true).build(), type, getterName);
      this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint(String.format("return this.%s;", name)));
      astcdClass.addCDMember(method);
    }
    if (setter) {
      String setterName = "set" + StringTransformations.capitalize(name);
      ASTCDMethod method = this.getCDMethodFacade().createMethod(CD4CodeMill.modifierBuilder().setPublic(true).build(), setterName, getCDParameterFacade().createParameter(type, name));
      this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint(String.format("this.%s = %s;", name, name)));
      astcdClass.addCDMember(method);
    }
    return attribute;
  }

}
