/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar;

import de.monticore.grammar.cocos.ASTRuleAndNTUseSameAttrNameForDiffNTs;
import de.monticore.grammar.cocos.AbstractNTNotExtendInterfaceOrExternalNTs;
import de.monticore.grammar.cocos.AbstractNTOnlyExtendOrAstextendNTOrClass;
import de.monticore.grammar.cocos.AbstractNTOnlyExtendsOneNTOrClass;
import de.monticore.grammar.cocos.AbstractNTOnlyImplementInterfaceNTs;
import de.monticore.grammar.cocos.AbstractNTWithoutExtensionOnlyInComponentGrammar;
import de.monticore.grammar.cocos.AttributeNameLowerCase;
import de.monticore.grammar.cocos.ConservativeExtensionCheck;
import de.monticore.grammar.cocos.DerivedAndManualListName;
import de.monticore.grammar.cocos.DuplicatedEnumConstant;
import de.monticore.grammar.cocos.DuplicatedSymbolDefinitionInProd;
import de.monticore.grammar.cocos.ExternalNTNoASTRule;
import de.monticore.grammar.cocos.ExternalNTOnlyInComponentGrammar;
import de.monticore.grammar.cocos.GrammarExtensionOnce;
import de.monticore.grammar.cocos.GrammarInheritanceCycle;
import de.monticore.grammar.cocos.GrammarNameUpperCase;
import de.monticore.grammar.cocos.InheritedModiOverwrite;
import de.monticore.grammar.cocos.InterfaceNTOnlyExtendInterfaceNTs;
import de.monticore.grammar.cocos.KeyConstantInvalid;
import de.monticore.grammar.cocos.KeyRuleWithoutName;
import de.monticore.grammar.cocos.KeywordAlternativeName;
import de.monticore.grammar.cocos.KeywordInvalidName;
import de.monticore.grammar.cocos.KeywordRuleInvalid;
import de.monticore.grammar.cocos.LeftRecursiveRulesInBlock;
import de.monticore.grammar.cocos.LexNTsNotEmpty;
import de.monticore.grammar.cocos.LexNTsOnlyUseLexNTs;
import de.monticore.grammar.cocos.LexProdModeNameUpperCase;
import de.monticore.grammar.cocos.MultipleASTRules;
import de.monticore.grammar.cocos.NTAndASTRuleExtendType;
import de.monticore.grammar.cocos.NTDefinedByAtmostOneProduction;
import de.monticore.grammar.cocos.NTForASTRuleExists;
import de.monticore.grammar.cocos.NTNotExtendInterfaceOrExternalNTs;
import de.monticore.grammar.cocos.NTOnlyExtendOrAstextendNTOrClass;
import de.monticore.grammar.cocos.NTOnlyExtendsOneNTOrClass;
import de.monticore.grammar.cocos.NTOnlyImplementInterfaceNTs;
import de.monticore.grammar.cocos.NTUniqueIgnoreCase;
import de.monticore.grammar.cocos.NoASTExtendsForClasses;
import de.monticore.grammar.cocos.NoASTRuleForEnumNTs;
import de.monticore.grammar.cocos.NoExtensionOfSymbolThatOnlySpansScope;
import de.monticore.grammar.cocos.NoForbiddenGrammarName;
import de.monticore.grammar.cocos.NoForbiddenProdAndSymbolName;
import de.monticore.grammar.cocos.NoForbiddenProdName;
import de.monticore.grammar.cocos.NoForbiddenProdNameAddon;
import de.monticore.grammar.cocos.NoForbiddenSymbolName;
import de.monticore.grammar.cocos.NoForbiddenSymbolNameAddon;
import de.monticore.grammar.cocos.NoMultipleSymbolRule;
import de.monticore.grammar.cocos.NoNestedGenericsInAdditionalAttributes;
import de.monticore.grammar.cocos.NoOverridingNTHasAnnotation;
import de.monticore.grammar.cocos.NoTokenDefined;
import de.monticore.grammar.cocos.OverridingAbstractNTs;
import de.monticore.grammar.cocos.OverridingAbstractNTsHaveNoSuperRules;
import de.monticore.grammar.cocos.OverridingAdditionalAttributes;
import de.monticore.grammar.cocos.OverridingEnumNTs;
import de.monticore.grammar.cocos.OverridingInterfaceNTs;
import de.monticore.grammar.cocos.OverridingLexNTs;
import de.monticore.grammar.cocos.OverridingNTHasNoAnnotation;
import de.monticore.grammar.cocos.OverridingNTs;
import de.monticore.grammar.cocos.OverridingNTsHaveNoSuperRules;
import de.monticore.grammar.cocos.PackageNameLowerCase;
import de.monticore.grammar.cocos.ProdAndExtendedProdUseSameAttrNameForDiffNTs;
import de.monticore.grammar.cocos.ProdAndOverriddenProdUseSameAttrNameForDiffNTs;
import de.monticore.grammar.cocos.ProdExtendsNotExistingProd;
import de.monticore.grammar.cocos.ProdStartsWithCapital;
import de.monticore.grammar.cocos.ProdWithDoubleAnnos;
import de.monticore.grammar.cocos.ProdWithExtensionMustNotBeOverridden;
import de.monticore.grammar.cocos.ReferenceSymbolNotName;
import de.monticore.grammar.cocos.ReferenceSymbolSameAttribute;
import de.monticore.grammar.cocos.ReferencedNTNotDefined;
import de.monticore.grammar.cocos.ReferencedSymbolExists;
import de.monticore.grammar.cocos.RuleComponentsCompatible;
import de.monticore.grammar.cocos.ScopeProdOverwrittenByScope;
import de.monticore.grammar.cocos.SplitRuleInvalid;
import de.monticore.grammar.cocos.SubrulesUseInterfaceNTs;
import de.monticore.grammar.cocos.SymbolProdOverwrittenBySymbol;
import de.monticore.grammar.cocos.SymbolRuleWithoutSymbolRef;
import de.monticore.grammar.cocos.SymbolWithManyNames;
import de.monticore.grammar.cocos.TerminalCritical;
import de.monticore.grammar.cocos.TokenConstantInvalid;
import de.monticore.grammar.cocos.UniqueProdNameInGrammar;
import de.monticore.grammar.cocos.UsedLexNTNotDefined;
import de.monticore.grammar.cocos.UsedNTNotDefined;
import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;

// TODO Delete after release 7.6.0
@Deprecated
public class GrammarCoCosFix {
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
    checker.addCoCo(new InterfaceNTWithoutImplementationOnlyInComponentGrammarFix());
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
