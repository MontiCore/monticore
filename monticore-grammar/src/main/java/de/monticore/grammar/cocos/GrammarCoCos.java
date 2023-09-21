/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;

public class GrammarCoCos {
  public Grammar_WithConceptsCoCoChecker getCoCoChecker() {
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
    checker.addCoCo(new GrammarExtensionOnce());
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
    checker.addCoCo(new SubrulesUseInterfaceNTs());
    checker.addCoCo(new ReferenceSymbolSameAttribute());
    checker.addCoCo(new ReferenceSymbolNotName());
    checker.addCoCo(new ReferencedSymbolExists());
    checker.addCoCo(new ConservativeExtensionCheck());
    checker.addCoCo(new NoTokenDefined());
    // checker.addCoCo(new InheritedSymbolProperty());
    // checker.addCoCo(new InheritedScopeProperty());
    checker.addCoCo(new SymbolRuleWithoutSymbolRef());
    checker.addCoCo(new SymbolRuleHasName());
    checker.addCoCo(new NoNestedGenericsInAdditionalAttributes());
    checker.addCoCo(new NoMultipleSymbolRule());
    checker.addCoCo(new SymbolProdOverwrittenBySymbol());
    checker.addCoCo(new ScopeProdOverwrittenByScope());
    checker.addCoCo(new UniqueProdNameInGrammar());
    checker.addCoCo(new ProdExtendsNotExistingProd());
    checker.addCoCo(new TokenConstantInvalid());
    checker.addCoCo(new SplitRuleInvalid());
    checker.addCoCo(new KeyConstantInvalid());
    checker.addCoCo(new KeywordRuleInvalid());
    checker.addCoCo(new TerminalCritical());
    checker.addCoCo(new PackageNameLowerCase());
    checker.addCoCo(new NoOverridingNTHasAnnotation());
    checker.addCoCo(new OverridingNTHasNoAnnotation());
    checker.addCoCo(new ProdWithDoubleAnnos());
    checker.addCoCo(new ExternalNTNoASTRule());
    checker.addCoCo(new DerivedAndManualListName());
    checker.addCoCo(new KeyRuleWithoutName());
    checker.addCoCo(new SymbolWithManyNames());
    checker.addCoCo(new OverridingAdditionalAttributes());
    checker.addCoCo(new NoExtensionOfSymbolThatOnlySpansScope());
    // checker.addCoCo(new NoNTInheritanceCycle());
    checker.addCoCo(new LexProdModeNameUpperCase());
    checker.addCoCo(new InheritedModiOverwrite());
    checker.addCoCo(new NoForbiddenGrammarName());
    checker.addCoCo(new NoForbiddenProdName());
    checker.addCoCo(new NoForbiddenProdAndSymbolName());
    checker.addCoCo(new NoForbiddenProdNameAddon());
    checker.addCoCo(new NoForbiddenSymbolName());
    checker.addCoCo(new NoForbiddenSymbolNameAddon());
    checker.addCoCo(new RuleComponentsCompatible());

    return checker;
  }

  public Grammar_WithConceptsCoCoChecker getSymbolTableCoCoChecker() {
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
