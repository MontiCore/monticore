/* (c) https://github.com/MontiCore/monticore */
package de.monticore.dstlgen.ruletranslation;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.monticore.grammar.grammar.GrammarMill;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.se_rwth.commons.Names;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Optional;
import java.util.Collection;
import java.util.HashSet;

public class DSTLGenInheritanceHelper {

  private static DSTLGenInheritanceHelper instance = null;

  /**
   * Getter to get the only instance of the
   * DSTLGenInheritanceHelper (singleton)
   *
   * @return the instance of the factory
   */
  public static synchronized DSTLGenInheritanceHelper getInstance() {
    if (instance == null) {
      instance = new DSTLGenInheritanceHelper();
    }
    return instance;
  }

  /**
   * The names of all lexical productions known to TFCommons.
   * Required, as the transformation languages are all extending TFCommons
   */
  protected final Collection<String> tfCommonLexProds = new HashSet<>();
  /**
   * FQN of TFCommons and all its super grammars
   */
  protected final Collection<String> tfCommonSuperFQNs = new HashSet<>();

  /**
   * Constructor for de.monticore.tf.ruletranslation.InheritanceHelper
   */
  protected DSTLGenInheritanceHelper() {
    // Populate information about TFCommon and its super grammars
    Optional<MCGrammarSymbol> tfCommonsGrammarSymbol = GrammarMill.globalScope().resolveMCGrammar("de.monticore.tf.TFCommons");
    if (tfCommonsGrammarSymbol.isPresent()) {
      // modelPath appears to not contain TFCommons (e.g. when using the script tests)
      // Thus, we do not have the problem of potentially overlapping tokens (and instead are unable to run monticore on the generated TR grammar)
      tfCommonsGrammarSymbol.get().getSpannedScope().getLocalProdSymbols().stream().filter(ps -> ps.isIsLexerProd()).forEach(ps -> tfCommonLexProds.add(ps.getName()));
      tfCommonSuperFQNs.add(tfCommonsGrammarSymbol.get().getFullName());
      for (MCGrammarSymbol superGrammar : tfCommonsGrammarSymbol.get().getAllSuperGrammars()) {
        superGrammar.getSpannedScope().getLocalProdSymbols().stream().filter(ps -> ps.isIsLexerProd()).forEach(ps -> tfCommonLexProds.add(ps.getName()));
        tfCommonSuperFQNs.add(superGrammar.getFullName());
      }
    }
  }

  public boolean isLexicalProdKnownInTFCommons(String name) {
    return tfCommonLexProds.contains(name);
  }

  public String getSuperGrammarPackage(ASTMCGrammar ast){
    if (!ast.getSupergrammarList().isEmpty()) {
      List<String> p = ast.getSupergrammarList().stream()
                                            .filter(s -> !isCommonSuperGrammar(s.getNameList()))
                                            .findFirst().get().getNameList();
      return Names.getQualifiedName(p.subList(0,p.size()-1));
    }
    return "";
  }


  public boolean isCommonSuperGrammar(String grammar) {
    return isCommonSuperGrammar(Collections.singletonList(grammar));
  }

  // Method to skip (language)TRHandler- setting of traversers, such as in the rule2od, cocos, etc.
  public boolean isCommonSuperGrammar(List<String> grammar) {
    // We only exclude MCBasics, as it only consists of lexical productions
    // Once all(?) shared super grammars between the TFCommons and original DSL were skipped,
    // leading to (among other) all lexical non-terminal references to their lexical productions to being absent,
    // causing invalid TR grammars to be generated (this was required as otherwise tokens would be duplicate).

    // That case is now handled by the #isLexicalProdKnownInTFCommons method

    //TODO: Switch to the fully qualified name once we can be certain that the FQN of a super-grammar-symbol is correct
    return grammar.contains("MCBasics");
  }

  // used by templates
  @SuppressWarnings("unused")
  public List<MCGrammarSymbol> getSuperGrammars(ASTMCGrammar ast){
    return getSuperGrammars(ast.getSymbol());
  }
  
  public List<MCGrammarSymbol> getSuperGrammars(MCGrammarSymbol ast){
    Set<MCGrammarSymbol> supers = Sets.newLinkedHashSet();
    for(MCGrammarSymbol s : ast.getSuperGrammarSymbols()){
      if(!isCommonSuperGrammar(s.getName())) {
        supers.add(s);
        supers.addAll(getSuperGrammars(s));
      }
    }
    return Lists.newArrayList(supers);
  }
  
}

