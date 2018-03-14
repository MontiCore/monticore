/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;

/**
 * Created by
 *
 * @author KH
 */
public class GrammarCoCos {
  public Grammar_WithConceptsCoCoChecker getCoCoChecker(){
    Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new OverridingNTsHaveNoSuperRules());
    checker.addCoCo(new OverridingAbstractNTsHaveNoSuperRules());
    checker.addCoCo(new OverridingEnumNTs());
    checker.addCoCo(new OverridingNTs());
    checker.addCoCo(new OverridingAbstractNTs());
    checker.addCoCo(new OverridingInterfaceNTs());
    checker.addCoCo(new DuplicatedEnumConstant());
    checker.addCoCo(new NTAndASTRuleExtendType());
    checker.addCoCo(new NoASTRuleForEnumNTs());
    checker.addCoCo(new NTForASTRuleExists());
    checker.addCoCo(new MultipleASTRules());
    checker.addCoCo(new NoASTExtendsForClasses());
    checker.addCoCo(new LexNTsOnlyUseLexNTs());
    checker.addCoCo(new UsedLexNTNotDefined());
    checker.addCoCo(new UsedNTNotDefined());
    checker.addCoCo(new InterfaceNTWithoutImplementationOnlyInComponentGrammar());
    checker.addCoCo(new ExternalNTOnlyInComponentGrammar());
    checker.addCoCo(new AbstractNTWithoutExtensionOnlyInComponentGrammar());
    checker.addCoCo(new ProdAndExtendedProdUseSameAttrNameForDiffNTs());
    checker.addCoCo(new GrammarNameUpperCase());
    checker.addCoCo(new AbstractNTNotExtendInterfaceOrExternalNTs());
    checker.addCoCo(new AbstractNTOnlyExtendOrAstextendNTOrClass());
    checker.addCoCo(new AbstractNTOnlyExtendsOneNTOrClass());
    checker.addCoCo(new AbstractNTOnlyImplementInterfaceNTs());
    checker.addCoCo(new AttributeNameLowerCase());
    checker.addCoCo(new InterfaceNTOnlyExtendInterfaceNTs());
    checker.addCoCo(new KeywordAlternativeName());
    checker.addCoCo(new NTNotExtendInterfaceOrExternalNTs());
    checker.addCoCo(new NTOnlyExtendOrAstextendNTOrClass());
    checker.addCoCo(new NTOnlyExtendsOneNTOrClass());
    checker.addCoCo(new NTOnlyImplementInterfaceNTs());
    checker.addCoCo(new ProdStartsWithCapital());
    checker.addCoCo(new ProdAndOverriddenProdUseSameAttrNameForDiffNTs());
    checker.addCoCo(new ProdWithExtensionMustNotBeOverridden());
    checker.addCoCo(new ASTRuleAndNTUseSameAttrNameForDiffNTs());
    checker.addCoCo(new OverridingLexNTs());
    checker.addCoCo(new GrammarInheritanceCycle());
    checker.addCoCo(new LeftRecursiveRulesInBlock());
    checker.addCoCo(new DuplicatedSymbolDefinitionInProd());
    checker.addCoCo(new SymbolWithoutName());

    // checker.addCoCo(new NoNTInheritanceCycle());

    return checker;
  }

  public Grammar_WithConceptsCoCoChecker getSymbolTableCoCoChecker(){
    Grammar_WithConceptsCoCoChecker checker = new Grammar_WithConceptsCoCoChecker();
    checker.addCoCo(new OverridingNTsHaveNoSuperRules());
    checker.addCoCo(new OverridingAbstractNTsHaveNoSuperRules());
    checker.addCoCo(new OverridingEnumNTs());
    checker.addCoCo(new OverridingNTs());
    checker.addCoCo(new OverridingAbstractNTs());
    checker.addCoCo(new UsedNTNotDefined());
    checker.addCoCo(new KeywordAlternativeName());
    checker.addCoCo(new NTDefinedByAtmostOneProduction());
    checker.addCoCo(new NTUniqueIgnoreCase());
    checker.addCoCo(new ReferencedNTNotDefined());
    checker.addCoCo(new KeywordInvalidName());
    checker.addCoCo(new LexNTsNotEmpty());


    return checker;
  }
}
