/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._tagging;

import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java._ast.ast_class.ASTConstants;
import de.monticore.codegen.cd2java._symboltable.SymbolTableConstants;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbolSurrogate;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.symboltable.IScope;
import de.monticore.tagging.tags._ast.*;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.*;

/**
 * Decorate the ITagger interface and Tagger class (along with used visitor classes)
 */
public class TaggerDecorator extends AbstractDecorator {
  protected final AbstractService<?> service;
  protected final VisitorService visitorService;
  protected final ASTMCGrammar originalGrammar;

  public TaggerDecorator(GlobalExtensionManagement glex, AbstractService<?> service, VisitorService visitorService, ASTMCGrammar originalGrammar) {
    super(glex);
    this.service = service;
    this.visitorService = visitorService;
    this.originalGrammar = originalGrammar;
  }

  public List<ASTCDElement> decorate() {
    List<ASTCDElement> elements = new ArrayList<>();
    String taggerName = StringTransformations.capitalize(originalGrammar.getName()) + "Tagger";


    ASTCDInterface taggerInterface = CD4CodeMill.cDInterfaceBuilder()
            .setModifier(PUBLIC.build())
            .setName("I" + taggerName)
            .build();
    elements.add(taggerInterface);
    // Add I${super}Tagger interfaces
    List<ASTMCObjectType> superInterfaces = new ArrayList<>();
    for (MCGrammarSymbolSurrogate superGrammar : originalGrammar.getSymbol().getSuperGrammars()) {
      String packageName = superGrammar.lazyLoadDelegate().getPackageName();
      List<String> pck = new ArrayList<>();
      if (!packageName.isEmpty())
        pck.add(packageName);
      pck.add(superGrammar.lazyLoadDelegate().getName().toLowerCase());
      pck.add(TaggingConstants.TAGGING_PACKAGE);
      pck.add("I" + superGrammar.lazyLoadDelegate().getName() + "Tagger");
      superInterfaces.add(mcTypeFacade.createQualifiedType(Joiners.DOT.join(pck)));
    }
    if (!superInterfaces.isEmpty()) {
      taggerInterface.setCDExtendUsage(CD4CodeMill.cDExtendUsageBuilder().addAllSuperclass(superInterfaces).build());
    }


    ASTCDClass taggerClass = CD4CodeMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setName(taggerName)
            .setCDInterfaceUsage(CD4CodeMill.cDInterfaceUsageBuilder().addInterface(mcTypeFacade.createQualifiedType("I" + taggerName)).build())
            .setCDExtendUsage(CD4CodeMill.cDExtendUsageBuilder().addSuperclass(mcTypeFacade.createQualifiedType("de.monticore.tagging.AbstractTagger")).build())
            .build();
    CD4C.getInstance().addImport(taggerClass, "de.monticore.tagging.tags.TagsMill");
    CD4C.getInstance().addImport(taggerClass, "de.monticore.tagging.tags._ast.ASTContext");
    CD4C.getInstance().addImport(taggerClass, "de.monticore.tagging.tags._ast.ASTTag");
    CD4C.getInstance().addImport(taggerClass, "java.util.stream.Collectors");
    CD4C.getInstance().addImport(taggerClass, "de.se_rwth.commons.Joiners");
    elements.add(taggerClass);

    for (ProdSymbol prodSymbol : originalGrammar.getSymbol().getProds()) {
      if (prodSymbol.isIsInterface() || prodSymbol.isIsExternal() || prodSymbol.isIsLexerProd()) continue;

      // Skip left recursive productions
      // DISCUSS: Support for left-recursive
      if (prodSymbol.isIsDirectLeftRecursive() || prodSymbol.isIsIndirectLeftRecursive()) continue;
      boolean isSymbolLike = prodSymbol.isIsSymbolDefinition() || MC2CDTaggingTranslation.hasName(prodSymbol);

      taggerInterface.addAllCDMembers(createITaggerMethods(prodSymbol, taggerClass.getName()));
      taggerClass.addAllCDMembers(createTaggerMethods(prodSymbol, isSymbolLike));

      elements.add(createTravCheckerClass(prodSymbol));
    }

    ASTCDMethod method = cdMethodFacade.createMethod(PROTECTED.build(), mcTypeFacade.createQualifiedType(IScope.class.getName()),
            "getArtifactScope",
            cdParameterFacade.createParameter(IScope.class.getName(), "s"));
    taggerClass.addCDMember(method);

    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("tagging.tagger.GetArtifactScope", getMillName(originalGrammar.getSymbol())));

    // Singleton
    taggerClass.addCDMember(cdAttributeFacade.createAttribute(PROTECTED_STATIC.build(), mcTypeFacade.createQualifiedType(taggerInterface.getName()), "INSTANCE"));

    method = cdMethodFacade.createMethod(PUBLIC_STATIC.build(), mcTypeFacade.createQualifiedType(taggerInterface.getName()), "getInstance");
    taggerClass.addCDMember(method);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("tagging.tagger.GetInstance", taggerClass.getName()));

    return elements;
  }


  protected List<ASTCDMethod> createITaggerMethods(ProdSymbol prodSymbol, String clazzname) {
    final String astFQN = getASTPackageName(prodSymbol) + ".AST" + StringTransformations.capitalize(prodSymbol.getName());
    final String symbolFQN = getSymbolPackageName(prodSymbol) + "." + StringTransformations.capitalize(prodSymbol.getName()) + "Symbol";

    List<ASTCDMethod> methods = new ArrayList<>();

    ASTCDMethod m;
    methods.add((m = cdMethodFacade.createMethod(PACKAGE_PRIVATE.build(), mcTypeFacade.createListTypeOf(ASTTag.class.getName()),
            "getTags",
            cdParameterFacade.createParameter(astFQN, "model"),
            cdParameterFacade.createParameter(ASTTagUnit.class.getName(), "astTagUnit"))));
    this.replaceTemplate(EMPTY_BODY, m, new TemplateHookPoint("tagging.itagger.GetTags", clazzname));

    if (prodSymbol.isIsSymbolDefinition()) {
      methods.add((m = cdMethodFacade.createMethod(PACKAGE_PRIVATE.build(), mcTypeFacade.createListTypeOf(ASTTag.class.getName()),
              "getTags",
              cdParameterFacade.createParameter(symbolFQN, "model"),
              cdParameterFacade.createParameter(ASTTagUnit.class.getName(), "astTagUnit"))));
      this.replaceTemplate(EMPTY_BODY, m, new TemplateHookPoint("tagging.itagger.GetTags", clazzname));
    }

    methods.add((m = cdMethodFacade.createMethod(PACKAGE_PRIVATE.build(), mcTypeFacade.createBooleanType(),
            "removeTag",
            cdParameterFacade.createParameter(astFQN, "model"),
            cdParameterFacade.createParameter(ASTTagUnit.class.getName(), "astTagUnit"),
            cdParameterFacade.createParameter(ASTTag.class.getName(), "astTag")
    )));
    this.replaceTemplate(EMPTY_BODY, m, new TemplateHookPoint("tagging.itagger.RemoveTag", clazzname));


    methods.add((m = cdMethodFacade.createMethod(PACKAGE_PRIVATE.build(),
            "addTag",
            cdParameterFacade.createParameter(astFQN, "model"),
            cdParameterFacade.createParameter(ASTTagUnit.class.getName(), "astTagUnit"),
            cdParameterFacade.createParameter(ASTTag.class.getName(), "astTag")
    )));
    this.replaceTemplate(EMPTY_BODY, m, new TemplateHookPoint("tagging.itagger.AddTag", clazzname));

    return methods;
  }

  protected List<ASTCDMethod> createTaggerMethods(ProdSymbol prodSymbol, boolean isSymbolLike) {
    final String astFQN = getASTPackageName(prodSymbol) + ".AST" + StringTransformations.capitalize(prodSymbol.getName());
    final String symbolFQN = getSymbolPackageName(prodSymbol) + "." + StringTransformations.capitalize(prodSymbol.getName()) + "Symbol";

    List<ASTCDMethod> methods = new ArrayList<>();
    ASTCDMethod method;

    methods.add((method = cdMethodFacade.createMethod(PUBLIC.build(),
            mcTypeFacade.createListTypeOf(ASTTag.class.getName()),
            "getTags",
            cdParameterFacade.createParameter(astFQN, "model"),
            cdParameterFacade.createParameter(ASTTagUnit.class.getName(), "astTagUnit"))));
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("tagging.tagger.GetTags", prodSymbol.getName(), isSymbolLike));

    if (prodSymbol.isIsSymbolDefinition()) {
      methods.add((method = cdMethodFacade.createMethod(PUBLIC.build(),
              mcTypeFacade.createListTypeOf(ASTTag.class.getName()),
              "getTags",
              cdParameterFacade.createParameter(symbolFQN, "symbol"),
              cdParameterFacade.createParameter(ASTTagUnit.class.getName(), "astTagUnit"))));
      this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("tagging.tagger.GetTagsSymbol", prodSymbol.getName()));
    }

    methods.add((method = cdMethodFacade.createMethod(PUBLIC.build(),
            mcTypeFacade.createBooleanType(),
            "removeTag",
            cdParameterFacade.createParameter(astFQN, "model"),
            cdParameterFacade.createParameter(ASTTagUnit.class.getName(), "astTagUnit"),
            cdParameterFacade.createParameter(ASTTag.class.getName(), "astTag")
    )));
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("tagging.tagger.RemoveTag", prodSymbol.getName(), isSymbolLike));

    methods.add((method = cdMethodFacade.createMethod(PUBLIC.build(),
            "addTag",
            cdParameterFacade.createParameter(astFQN, "model"),
            cdParameterFacade.createParameter(ASTTagUnit.class.getName(), "astTagUnit"),
            cdParameterFacade.createParameter(ASTTag.class.getName(), "astTag")
    )));
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("tagging.tagger.AddTag",
            prodSymbol.getName(), originalGrammar.getName(), getPackageName(originalGrammar.getSymbol()), prodSymbol.isIsSymbolDefinition()));

    methods.add((method = cdMethodFacade.createMethod(PROTECTED.build(),
            mcTypeFacade.createBasicGenericTypeOf(Stream.class.getName(), ASTTargetElement.class.getName()),
            "findTargetsBy",
            cdParameterFacade.createParameter(ASTTagUnit.class.getName(), "ast"),
            cdParameterFacade.createParameter(astFQN, "element"),
            cdParameterFacade.createParameter("TravChecker" + prodSymbol.getName(), "travChecker")
    )));
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("tagging.tagger.FindTargetsBy"));

    if (!isSymbolLike) {
      methods.add((method = cdMethodFacade.createMethod(PROTECTED.build(),
              mcTypeFacade.createBasicGenericTypeOf(Stream.class.getName(), ASTTargetElement.class.getName()),
              "findTargetsBy",
              cdParameterFacade.createParameter(ASTContext.class.getName(), "ast"),
              cdParameterFacade.createParameter(astFQN, "element"),
              cdParameterFacade.createParameter("TravChecker" + prodSymbol.getName(), "travChecker")
      )));
      this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("tagging.tagger.FindTargetsBy"));
    }

    methods.add((method = cdMethodFacade.createMethod(PROTECTED.build(),
            mcTypeFacade.createBooleanType(),
            "isIdentified",
            cdParameterFacade.createParameter(ASTModelElementIdentifier.class.getName(), "elementIdentifier"),
            cdParameterFacade.createParameter(astFQN, "element"),
            cdParameterFacade.createParameter("TravChecker" + prodSymbol.getName(), "travChecker")
    )));
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("tagging.tagger.IsIdentified"));

    return methods;
  }

  // The TravChecker, a class used to check if an AST element is present
  protected ASTCDClass createTravCheckerClass(ProdSymbol prodSymbol) {
    final String symbolASTFQN = getASTPackageName(prodSymbol) + ".AST" + StringTransformations.capitalize(prodSymbol.getName());
    String traverserPkg = getPackageName(originalGrammar.getSymbol());
    traverserPkg += "tagdefinition._visitor." + originalGrammar.getName() + "TagDefinitionTraverser";

    ASTCDClass clazz = CD4CodeMill.cDClassBuilder().setName("TravChecker" + StringTransformations.capitalize(prodSymbol.getName()))
            .setModifier(PACKAGE_PRIVATE.build())
            .build();
    clazz.addCDMember(cdAttributeFacade.createAttribute(PROTECTED_FINAL.build(), mcTypeFacade.createArrayType(mcTypeFacade.createBooleanType(), 1), "ret"));
    clazz.addCDMember(cdAttributeFacade.createAttribute(PROTECTED_FINAL.build(), mcTypeFacade.createQualifiedType(traverserPkg), "traverser"));
    clazz.addCDMember(cdAttributeFacade.createAttribute(PROTECTED.build(), mcTypeFacade.createQualifiedType(symbolASTFQN), "element"));

    ASTCDConstructor constructor = cdConstructorFacade.createDefaultConstructor(PUBLIC.build(), clazz);
    clazz.addCDMember(constructor);
    this.replaceTemplate(EMPTY_BODY, constructor, new TemplateHookPoint("tagging.travchecker.TravCheckerConstructor", prodSymbol.getName(), originalGrammar.getName(), getPackageName(originalGrammar.getSymbol())));

    return clazz;
  }

  /**
   * Retrieve the AST package name of the original grammar of a production
   * @param symbol the production's symbol
   * @return the _ast package name
   */
  protected String getASTPackageName(ProdSymbol symbol) {
    return Names.getQualifiedName(symbol.getPackageName(), symbol.getEnclosingScope().getName().toLowerCase() + "." + ASTConstants.AST_PACKAGE);
  }

  /**
   * Retrieve the symboltable package name of the original grammar of a production
   * @param symbol the production's symbol
   * @return the _symboltable package name
   */
  protected String getSymbolPackageName(ProdSymbol symbol) {
    return Names.getQualifiedName(symbol.getPackageName(), symbol.getEnclosingScope().getName().toLowerCase() + "." + SymbolTableConstants.SYMBOL_TABLE_PACKAGE);
  }

  /**
   * Retrieve the FQN of a grammar's mill
   * @param symbol the grammar's symbol
   * @return the FQN mill classname
   */
  protected String getMillName(MCGrammarSymbol symbol) {
    return Names.getQualifiedName(symbol.getPackageName(),
            symbol.getName().toLowerCase()
                    + "." + StringTransformations.capitalize(symbol.getName()) + "Mill");
  }

  /**
   * Retrieve the AST package name of a grammar
   * @param symbol the symbol of the grammar
   * @return the _ast package name
   */
  protected String getPackageName(MCGrammarSymbol symbol) {
    return Names.getQualifiedName(symbol.getPackageName(), symbol.getName().toLowerCase());
  }
}
