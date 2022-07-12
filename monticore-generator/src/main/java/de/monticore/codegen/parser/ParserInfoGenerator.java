package de.monticore.codegen.parser;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.grammar.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbolTOP;
import de.monticore.grammar.grammar._visitor.GrammarVisitor2;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsTraverser;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ParserInfoGenerator {
  public static final String LOG = "ParserInfoGenerator";

  private ParserInfoGenerator(){
    // noninstantiable
  }

  public static List<ASTNonTerminal> collectNonTerminals(ASTMCGrammar grammar) {
    List<ASTNonTerminal> res = new ArrayList<>();

    GrammarVisitor2 visitor = new GrammarVisitor2() {
      @Override
      public void visit(ASTNonTerminal node) {
        res.add(node);
      }
    };

    Grammar_WithConceptsTraverser traverser = Grammar_WithConceptsMill.traverser();
    traverser.add4Grammar(visitor);
    grammar.accept(traverser);
    return res;
  }

  /**
   * Generates ParserInfo class and all additional classes for the static delegate pattern for a component grammar.
   * All generated methods always return false, but will be overridden via the static delegate pattern by languages using the component.
   * @param astGrammar the MontiCore component grammar
   * @param setup configuration for the generator
   * @param parserPackage the target package of the generated classes. Classes for the static delegate pattern are written to the subpackage ${parserPackage}._auxiliary
   * @param lang the language used for the generated parser
   */
  public static void generateParserInfoForComponent(ASTMCGrammar astGrammar, GeneratorSetup setup, String parserPackage, Languages lang) {
    Map<ASTNonTerminal, Set<Integer>> nonTerminalToEmptyParserStates = collectNonTerminals(astGrammar)
        .stream()
        .collect(Collectors.toMap(nt -> nt, nt -> new LinkedHashSet<>(), (u,v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u));}, LinkedHashMap::new));
    generateParserInfo(astGrammar, setup, nonTerminalToEmptyParserStates, parserPackage, lang);
  }

  /**
   * Generates ParserInfo class and all additional classes for the static delegate pattern.
   * The generated classes can be used to find the referenced type and usage name in the current state of the Antlr parser.
   * This is useful for autocompletion based on the Antlr parser.
   * @param astGrammar the MontiCore Grammar
   * @param setup configuration for the generator
   * @param nonTerminalToParserStates A mapping of all non-terminals to their corresponding antlr parser states
   * @param parserPackage the target package of the generated classes. Classes for the static delegate pattern are written to the subpackage ${parserPackage}._auxiliary
   * @param lang the language used for the generated parser
   */
  public static void generateParserInfo(ASTMCGrammar astGrammar, GeneratorSetup setup, Map<ASTNonTerminal, Set<Integer>> nonTerminalToParserStates, String parserPackage, Languages lang) {
    if(!Languages.JAVA.equals(lang)){
      Log.info("Don't generate ParserInfo for the grammar " + astGrammar.getName() + " since the target language is " + lang.getLanguage() + " and not Java", LOG);
      return;
    }

    Log.info("Generate ParserInfo for the grammar " + astGrammar.getName(), LOG);
    Map<String, Set<Integer>> usageNameToStates = new LinkedHashMap<>();

    // Group states by referencedSymbol
    Map<String, Set<Integer>> referencedSymbolToStates = new LinkedHashMap<>();
    nonTerminalToParserStates.forEach((astNonTerminal, states) -> {
      if(astNonTerminal.isPresentReferencedSymbol()) {
        String referencedSymbol = astNonTerminal.getReferencedSymbol();
        if (!referencedSymbolToStates.containsKey(referencedSymbol)) {
          referencedSymbolToStates.put(referencedSymbol, new LinkedHashSet<>());
        }
        for (Integer state : states) {
          referencedSymbolToStates.get(referencedSymbol).add(state);
        }
      }
    });

    // Group states by usageName
    nonTerminalToParserStates.forEach((astNonTerminal, states) -> {
      String usageName = ParserGeneratorHelper.getUsageName(astNonTerminal);
      if(!usageNameToStates.containsKey(usageName)) {
        usageNameToStates.put(usageName, new LinkedHashSet<>());
      }
      for (Integer state : states) {
        usageNameToStates.get(usageName).add(state);
      }
    });

    // Find name defining states
    Set<Integer> nameDefiningStates =
        astGrammar.getSymbol()
            .getProdsWithInherited()
            .values()
            .stream()
            // Find symbol defining class productions
            // Abstract, interface, and external productions will always be overridden when a parser is generated
            // and therefore the generated method will never directly parse the name token
            .filter(s -> {
              if(s.isIsSymbolDefinition()){
                return true;
              }
              Set<ProdSymbol> superProds = MCGrammarSymbolTableHelper.getAllSuperProds(s);
              return superProds.stream().anyMatch(ProdSymbolTOP::isIsSymbolDefinition);
            })
            // find name non-terminals with the usage name "name", which parse a name definition
            .flatMap(s -> {
              final List<ASTNonTerminal> results = new ArrayList<>();
              GrammarVisitor2 visitor = new GrammarVisitor2(){
                @Override
                public void visit(ASTNonTerminal node) {
                  if("Name".equals(node.getName()) && (!node.isPresentUsageName() || "name".equals(node.getUsageName()))){
                    results.add(node);
                  }
                }
              };
              Grammar_WithConceptsTraverser t = Grammar_WithConceptsMill.traverser();
              t.add4Grammar(visitor);
              s.getAstNode().accept(t);
              return results.stream();
            })
            // don't fail if the nonTerminal is not found in Map, since it might be overridden and therefore have no parser state associated with it
            .flatMap(nonTerminal -> nonTerminalToParserStates.getOrDefault(nonTerminal, Collections.emptySet()).stream())
            .collect(Collectors.toCollection( LinkedHashSet::new ) );


    // Generate XParserInfo for this language
    final Path ParserInfoPath = Paths.get(Names.getPathFromPackage(parserPackage),
            (astGrammar.getName() + "ParserInfo") + ".java");
    GeneratorEngine generatorEngine = new GeneratorEngine(setup);
    generatorEngine.generate(
            "parser.ParserInfo",
            ParserInfoPath,
            astGrammar,
            referencedSymbolToStates,
            usageNameToStates,
            astGrammar.getSymbol().getAllSuperGrammars().stream().map(MCGrammarSymbol::getAstGrammar).map(Optional::get).collect(Collectors.toList()),
            nameDefiningStates
    );

    // Generate EmptyXParserInfo for this language
    final Path emptyParserInfoPath = Paths.get(Names.getPathFromPackage(parserPackage),
        ("Empty" + astGrammar.getName() + "ParserInfo") + ".java");
    generatorEngine.generate(
        "parser.EmptyParserInfo",
        emptyParserInfoPath,
        astGrammar,
        referencedSymbolToStates,
        usageNameToStates,
        astGrammar.getSymbol().getAllSuperGrammars().stream().map(MCGrammarSymbol::getAstGrammar).map(Optional::get).collect(Collectors.toList())
    );

    // Generate XParserInfoForY for all super languages Y
    for (MCGrammarSymbol superGrammar : astGrammar.getSymbol().getAllSuperGrammars()) {
      Path forPath = Paths.get(Names.getPathFromPackage(parserPackage), "_auxiliary", (astGrammar.getName()) + "ParserInfoFor" + superGrammar.getName() + ".java");
      generatorEngine.generate(
              "parser.ParserInfoForSuperLang",
              forPath,
              astGrammar,
              superGrammar.getAstGrammar().get(),
              referencedSymbolToStates,
              usageNameToStates
      );
    }
  }
}
