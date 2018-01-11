/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
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

package de.monticore.grammar.transformation;

import static de.monticore.grammar.Multiplicity.multiplicityByDuplicates;
import static de.monticore.grammar.Multiplicity.multiplicityOfAttributeInAST;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.grammar.Multiplicity;
import de.monticore.grammar.grammar._ast.ASTASTRule;
import de.monticore.grammar.grammar._ast.ASTAlt;
import de.monticore.grammar.grammar._ast.ASTAttributeInAST;
import de.monticore.grammar.grammar._ast.ASTBlock;
import de.monticore.grammar.grammar._ast.ASTConstantsGrammar;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._ast.ASTNonTerminalSeparator;
import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.monticore.utils.ASTNodes;
import de.monticore.utils.ASTTraverser;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

/**
 * Static facade for the transformation of MC AST.
 */
public class GrammarTransformer {
  
  private GrammarTransformer() {
    // noninstantiable
  }
  
  public static void transform(ASTMCGrammar grammar) {
    removeNonTerminalSeparators(grammar);
    changeNamesOfMultivaluedAttributes(grammar);
  }
  
  /**
   * The shortcut NonTerminalSeparator is replaced by the detailed description.
   * Example: List(Element || ',')* ==> (List:Element (',' List:Element)+)
   */
  public static void removeNonTerminalSeparators(ASTMCGrammar grammar) {
    Map<ASTNonTerminalSeparator, ASTAlt> map = new HashMap<ASTNonTerminalSeparator, ASTAlt>();
    RuleComponentListFinder componentListTransformer = new RuleComponentListFinder(map);
    ASTTraverser traverser = new ASTTraverser(componentListTransformer);
    
    // execute the search
    traverser.traverse(grammar);
    
    // execute the transformation
    for (Entry<ASTNonTerminalSeparator, ASTAlt> entry : map.entrySet()) {
      Log.debug("Find NonTerminalSeparator", GrammarTransformer.class.getName());
      Optional<ASTBlock> block = transform(entry.getKey());
      if (block.isPresent()) {
        ASTAlt parent = entry.getValue();
        int ind = parent.getComponents().indexOf(entry.getKey());
        if (ind >= 0) {
          Log.debug("Remove NonTerminalSeparator", GrammarTransformer.class.getName());
          parent.getComponents().remove(ind);
          Log.debug("Added new generated block", GrammarTransformer.class.getName());
          parent.getComponents().add(ind, block.get());
        }
        else {
          Log.error("0xA1009 Can't transform grammar");
        }
      }
    }
  }
  
  /**
   * Append suffix 's' to the names of multi-valued att * Append suffix 's' to
   * the names of multi-valued attributes (NonTerminals and attributesinAst) if
   * no usage names were set. Examples: Name ("." Name&)* ==> names:Name ("."
   * names:Name&)* (State | Transition)* ==> (states:State |
   * transitions:Transition)*
   */
  public static void changeNamesOfMultivaluedAttributes(ASTMCGrammar grammar) {
    grammar.getClassProds().forEach(c -> transformNonTerminals(grammar, c));
    grammar.getInterfaceProds().forEach(c -> transformNonTerminals(grammar, c));
    grammar.getASTRules().forEach(c -> transformAttributesInAST(c));
  }
  
  private static void transformNonTerminals(ASTMCGrammar grammar,
      ASTProd classProd) {
    Set<ASTNonTerminal> components = new LinkedHashSet<>();
    
    ASTNodes.getSuccessors(classProd, ASTNonTerminal.class).stream()
        .filter(nonTerminal -> GeneratorHelper.getMultiplicity(grammar,
            nonTerminal) == Multiplicity.LIST)
        //.filter(nonTerminal -> !nonTerminal.getUsageName().isPresent())
        .forEach(components::add);
    
    ASTNodes.getSuccessors(classProd, ASTNonTerminal.class).stream()
        .filter(nonTerminal -> multiplicityByDuplicates(grammar, nonTerminal) == Multiplicity.LIST)
       // .filter(nonTerminal -> !nonTerminal.getUsageName().isPresent())
        .forEach(components::add);
    Collection<String> changedNames = new LinkedHashSet<>();
    components.forEach(s -> {
      s.setUsageName(s.getUsageName().orElse(StringTransformations.uncapitalize(s.getName())) + 's');
      Log.debug("Change the name of " + classProd.getName()
          + " list-attribute: " + s.getName(), GrammarTransformer.class.getName());
      changedNames.add(s.getName());
    });
    
    // Change corresponding ASTRules
    grammar.getASTRules().forEach(
        astRule -> {
          if (astRule.getType().equals(classProd.getName())) {
            astRule.getAttributeInASTs().forEach(
                astAttr -> {
                  String name = astAttr.getGenericType().getTypeName();
                  if (name.startsWith(GeneratorHelper.AST_PREFIX)) {
                    name = name.substring(GeneratorHelper.AST_PREFIX.length());
                  }
                  if (!astAttr.getName().isPresent() && changedNames.contains(name)) {
                    astAttr.setName(StringTransformations.uncapitalize(name) + 's');
                    Log.debug("Change the name of " + classProd.getName()
                        + " astRule " + name, GrammarTransformer.class.getName());
                  }
                });
          }
        });
  }
  
  private static void transformAttributesInAST(ASTASTRule astRule) {
    ASTNodes
        .getSuccessors(astRule, ASTAttributeInAST.class)
        .stream()
        .filter(attributeInAST -> multiplicityOfAttributeInAST(attributeInAST) == Multiplicity.LIST)
        .filter(attributeInAST -> !attributeInAST.getName().isPresent())
        .forEach(
            attributeInAST -> {
              List<String> typeName = attributeInAST.getGenericType().getNames();
              attributeInAST.setName(typeName.get(typeName.size() - 1) + 's');
              Log.debug("Change the name of ast-rule " + astRule.getType()
                  + " list-attribute: " + attributeInAST.getGenericType(),
                  GrammarTransformer.class.getName());
            });
  }
  
  /**
   * @param nonTerminalSep
   * @return
   */
  private static Optional<ASTBlock> transform(ASTNonTerminalSeparator nonTerminalSep) {
    String name = "";
    if (nonTerminalSep.getUsageName().isPresent()) {
      name = nonTerminalSep.getUsageName().get() + ":";
    }
    String plusKeywords = (nonTerminalSep.isPlusKeywords()) ? "&" : "";
    String iteration = (nonTerminalSep.getIteration() == ASTConstantsGrammar.STAR) ? "?" : "";
    
    String extendedList = "(%usageName% %nonTerminal% %plusKeywords% (\"%terminal%\" %usageName% %nonTerminal% %plusKeywords%)*)%iterator%";
    extendedList = extendedList.replaceAll("%usageName%", name);
    extendedList = extendedList.replaceAll("%nonTerminal%", nonTerminalSep.getName());
    extendedList = extendedList.replaceAll("%plusKeywords%", plusKeywords);
    extendedList = extendedList.replaceAll("%terminal%", nonTerminalSep.getSeparator());
    extendedList = extendedList.replaceAll("%iterator%", iteration);
    
    Grammar_WithConceptsParser parser = new Grammar_WithConceptsParser();
    Optional<ASTBlock> block = null;
    try {
      Log.debug("Create ast for " + extendedList, GrammarTransformer.class.getName());
      block = parser.parseBlock(new StringReader(extendedList));
      if (parser.hasErrors()) {
        Log.error("0xA0362 RecognitionException during parsing " + extendedList);
      }
    }
    catch (IOException e) {
      Log.error("0xA0361 IOException during parsing " + extendedList);
    }
    return block;
  }
  
}
