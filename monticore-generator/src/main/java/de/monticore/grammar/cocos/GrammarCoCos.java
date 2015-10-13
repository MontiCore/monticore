/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

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
    checker.addCoCo(new ReferencedNTNotDefined());
    checker.addCoCo(new KeywordInvalidName());
    checker.addCoCo(new LexNTsNotEmpty());


    return checker;
  }
}
