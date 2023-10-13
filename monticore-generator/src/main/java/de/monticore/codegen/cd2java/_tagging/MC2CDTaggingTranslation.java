/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._tagging;

import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.facade.CDParameterFacade;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._symboltable.ICD4AnalysisGlobalScope;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java._ast.ast_class.ASTConstants;
import de.monticore.codegen.prettyprint.PrettyPrinterConstants;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.monticore.tagging.tags._ast.ASTTag;
import de.monticore.tagging.tags._ast.ASTTagUnit;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCPackageDeclaration;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static de.monticore.cd.facade.CDModifier.PACKAGE_PRIVATE_ABSTRACT;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class MC2CDTaggingTranslation implements Function<ASTMCGrammar, ASTCDCompilationUnit> {

  protected final ICD4AnalysisGlobalScope cdScope;

  protected final MCTypeFacade mcTypeFacade = MCTypeFacade.getInstance();

  protected final CDMethodFacade cdMethodFacade = CDMethodFacade.getInstance();
  protected final CDParameterFacade cdParameterFacade = CDParameterFacade.getInstance();

  public MC2CDTaggingTranslation(ICD4AnalysisGlobalScope cdScope) {
    this.cdScope = cdScope;
  }

  @Override
  public ASTCDCompilationUnit apply(ASTMCGrammar originalGrammar) {
    List<String> packageName = new ArrayList<>(originalGrammar.getPackageList());
    packageName.add(PrettyPrinterConstants.PRETTYPRINT_PACKAGE);

    ASTMCPackageDeclaration packageDecl = CD4CodeMill.mCPackageDeclarationBuilder().setMCQualifiedName(CD4CodeMill.mCQualifiedNameBuilder().setPartsList(packageName).build()).build();

    ASTCDCompilationUnit compilationUnit = CD4CodeMill.cDCompilationUnitBuilder()
            .setMCPackageDeclaration(packageDecl)
            .setCDDefinition(CD4CodeMill.cDDefinitionBuilder()
                    .setModifier(CD4AnalysisMill.modifierBuilder().build())
                    .setName(originalGrammar.getName())
                    .uncheckedBuild())
            .uncheckedBuild();

    compilationUnit.getCDDefinition().setDefaultPackageName(Joiners.DOT.join(packageName));

    // specific Interface with add/get/remove Tag methods

    String taggerName = StringTransformations.capitalize(originalGrammar.getName()) + "Tagger";

    ASTCDInterface taggerInterface = CD4CodeMill.cDInterfaceBuilder()
            .setModifier(PUBLIC.build())
            .setName("I" + taggerName)
            .build();

    ASTCDClass taggerClass = CD4CodeMill.cDClassBuilder()
            .setModifier(PUBLIC.build())
            .setName(taggerName)
            .setCDInterfaceUsage(CD4CodeMill.cDInterfaceUsageBuilder().addInterface(mcTypeFacade.createQualifiedType("I" + taggerName)).build())
            .build();

    for (ProdSymbol prodSymbol : originalGrammar.getSymbol().getProds()) {
      // only add tagging methods for concrete productions
      if (prodSymbol.isIsInterface() || prodSymbol.isIsExternal() || prodSymbol.isIsLexerProd()) continue;
      // that are not left recursive
      // DISCUSS: left recursive tagging?
      if (prodSymbol.isIsDirectLeftRecursive() || prodSymbol.isIsIndirectLeftRecursive()) continue;
      // which are either a symbol or have a single name:Name
      if (!prodSymbol.isIsSymbolDefinition() && !hasName(prodSymbol)) continue;

      taggerInterface.addAllCDMembers(createITaggerMethods(prodSymbol));
      taggerClass.addAllCDMembers(createTaggerMethods(prodSymbol));
    }

    compilationUnit.getCDDefinition().addCDElement(taggerInterface);
    compilationUnit.getCDDefinition().addCDElement(taggerClass);


    return compilationUnit;
  }

  protected List<ASTCDMethod> createITaggerMethods(ProdSymbol prodSymbol) {
    final String symbolASTFQN = getASTPackageName(prodSymbol) + ".AST" + StringTransformations.capitalize(prodSymbol.getName());

    List<ASTCDMethod> methods = new ArrayList<>();

    methods.add(cdMethodFacade.createMethod(PACKAGE_PRIVATE_ABSTRACT.build(), mcTypeFacade.createListTypeOf(ASTTag.class.getName()),
            "getTag",
            cdParameterFacade.createParameter(symbolASTFQN, "model"),
            cdParameterFacade.createParameter(ASTTagUnit.class.getName(), "astTagUnit")));

    methods.add(cdMethodFacade.createMethod(PACKAGE_PRIVATE_ABSTRACT.build(), mcTypeFacade.createBooleanType(),
            "removeTag",
            cdParameterFacade.createParameter(symbolASTFQN, "model"),
            cdParameterFacade.createParameter(ASTTagUnit.class.getName(), "astTagUnit"),
            cdParameterFacade.createParameter(symbolASTFQN, "astTag")
    ));

    methods.add(cdMethodFacade.createMethod(PACKAGE_PRIVATE_ABSTRACT.build(),
            "addTag",
            cdParameterFacade.createParameter(symbolASTFQN, "model"),
            cdParameterFacade.createParameter(ASTTagUnit.class.getName(), "astTagUnit"),
            cdParameterFacade.createParameter(symbolASTFQN, "astTag")
    ));

    return methods;
  }

  protected List<ASTCDMethod> createTaggerMethods(ProdSymbol prodSymbol) {
    final String symbolASTFQN = getASTPackageName(prodSymbol) + ".AST" + StringTransformations.capitalize(prodSymbol.getName());

    List<ASTCDMethod> methods = new ArrayList<>();

    methods.add(cdMethodFacade.createMethod(PUBLIC.build(), mcTypeFacade.createListTypeOf(ASTTag.class.getName()),
            "getTag",
            cdParameterFacade.createParameter(symbolASTFQN, "model"),
            cdParameterFacade.createParameter(ASTTagUnit.class.getName(), "astTagUnit")));

    methods.add(cdMethodFacade.createMethod(PUBLIC.build(), mcTypeFacade.createBooleanType(),
            "removeTag",
            cdParameterFacade.createParameter(symbolASTFQN, "model"),
            cdParameterFacade.createParameter(ASTTagUnit.class.getName(), "astTagUnit"),
            cdParameterFacade.createParameter(symbolASTFQN, "astTag")
    ));

    methods.add(cdMethodFacade.createMethod(PUBLIC.build(),
            "addTag",
            cdParameterFacade.createParameter(symbolASTFQN, "model"),
            cdParameterFacade.createParameter(ASTTagUnit.class.getName(), "astTagUnit"),
            cdParameterFacade.createParameter(symbolASTFQN, "astTag")
    ));

    return methods;
  }

  protected String getASTPackageName(ProdSymbol symbol) {
    List<String> pck = new ArrayList<>(Splitters.DOT.splitToList(symbol.getPackageName()));
    pck.add(symbol.getEnclosingScope().getName().toLowerCase());
    pck.add(ASTConstants.AST_PACKAGE);
    return Joiners.DOT.join(pck);
  }

  // If the production symbol has exactly one name production
  public static boolean hasName(ProdSymbol prodSymbol) {
    int nNames = 0;
    for (RuleComponentSymbol comp : prodSymbol.getProdComponents()) {
      if (comp.isIsNonterminal() && comp.isPresentReferencedType()) {
        if (comp.getName().equals("name") && comp.getReferencedType().equals("Name")) {
          if (comp.isIsList() || comp.isIsOptional() || nNames > 1) return false;
          nNames++;
        }
      }
    }
    return nNames == 1;
  }

}
