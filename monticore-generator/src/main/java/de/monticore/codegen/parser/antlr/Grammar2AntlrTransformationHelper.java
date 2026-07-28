/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.parser.antlr;

import de.monticore.codegen.parser.MCGrammarInfo;
import de.monticore.grammar.PredicatePair;
import de.monticore.grammar.grammar._ast.ASTAlt;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Shared methods for Grammar to ANTLR related generation.
 * Used by the Grammar2Antlr, Grammar2ParseVisitor, etc.
 */
public class Grammar2AntlrTransformationHelper {

  /**
   * Collects all productions implementing the prodSymbol and maps them to their
   * highest priority.
   * Duplicates are reduced to one pair with the max prio
   * @param prodSymbol the current production
   * @param implementers resulting map
   * @param grammarInfo the current grammar
   */
  public void addImplementers(ProdSymbol prodSymbol, Map<PredicatePair, Integer> implementers, MCGrammarInfo grammarInfo) {
    List<PredicatePair> interfaces = grammarInfo.getSubRulesForParsing(prodSymbol.getName());
    for (PredicatePair interf : interfaces) {
      Optional<ProdSymbol> symbol = grammarInfo.getGrammarSymbol().getSpannedScope().resolveProd(interf.getClassname());
      if (symbol.isEmpty()) {
        continue;
      }
      ProdSymbol superSymbol = symbol.get();
      if (!prodSymbol.isPresentAstNode()) {
        continue;
      }

      if (superSymbol.isIsIndirectLeftRecursive()) {
        if (superSymbol.isClass()) {
          implementers.compute(interf, (predicatePair, integer) -> Math.max(predicatePair.getRuleReference().isPresentPrio()?
                  Integer.parseInt(predicatePair.getRuleReference().getPrio()):0, integer==null?0:integer));
        } else if (prodSymbol.isIsInterface()) {
          addImplementers(superSymbol, implementers, grammarInfo);
        }
      } else {
        implementers.compute(interf, (predicatePair, integer) -> Math.max(predicatePair.getRuleReference().isPresentPrio()?
                Integer.parseInt(predicatePair.getRuleReference().getPrio()):0, integer==null?0:integer));
      }
    }
  }

  /**
   * Expands a set of implementers by inlining left-recursive alts
   * @param pairs the list of predicate pairs
   * @param alts the resulting list of inlined alts
   * @param grammarInfo the current grammar
   * @return whether the interface is left-recursive
   */
  public boolean expandAlternatives(List<PredicatePair> pairs, List<InterfaceInliningAlt> alts, MCGrammarInfo grammarInfo) {
    boolean isLeft = false;
    for (PredicatePair predicatePair : pairs) {
      Optional<ProdSymbol> symbol = grammarInfo.getGrammarSymbol().getSpannedScope().resolveProd(predicatePair.getClassname());
      if (symbol.isEmpty()) continue;
      ASTProd astNode = symbol.get().getAstNode();
      if (symbol.get().isIsIndirectLeftRecursive()) {
        isLeft = true;
        if (symbol.get().isClass()) {
          List<ASTAlt> localAlts = ((ASTClassProd) astNode).getAltList();
          for (ASTAlt alt : localAlts) {
            alts.add(new InterfaceInliningAlt(alt, astNode, predicatePair, symbol.get()));
          }
        } else if (symbol.get().isIsInterface()) {
          // will be done in the loop
        }
      } else {
        alts.add(new InterfaceInliningAlt(astNode, astNode, predicatePair, symbol.get()));
      }
    }

    return isLeft;
  }


  public int countLastLeftRecursive(List<InterfaceInliningAlt> alts, ProdSymbol prod) {
    int lastLeftRec = -1;
    if (alts.size() > 200) {
      Log.debug("Optimizing large interface " + prod.getName() + " with " + alts.size() + " inlined alts.", "ParserGrammarTransformer");
      for (int i=0,l=alts.size();i<l;i++) {
        InterfaceInliningAlt alt = alts.get(i);
        if (alt.prodSymbol.isIsIndirectLeftRecursive()
                || alt.prodSymbol.isIsDirectLeftRecursive()) {
          lastLeftRec = i;
        }
      }
      Log.debug("The last left-recursive rule is at index " + lastLeftRec, "ParserGrammarTransformer");
    }
    return lastLeftRec;
  }

  /**
   * Heuristic for determining where to split large rules
   *
   * @param lastLeftRec the last left-recursive alt position
   * @param altSize     the count of total alts
   * @return an index where to split
   */
  public int splitCountHeuristic(int lastLeftRec, int altSize) {
    // TODO: Keep rule splitting disabled for now: https://git.rwth-aachen.de/monticore/monticore/-/issues/5055
    if (false) {
      if (((double) lastLeftRec) / altSize > 0.1) {
        return Math.max(lastLeftRec, (int) (altSize * 0.1));
      }
    }
    return altSize;
  }
}
