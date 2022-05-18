/* (c) https://github.com/MontiCore/monticore */
package de.monticore.dstlgen.ruletranslation;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.monticore.grammar.grammar._ast.ASTGrammarReference;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.se_rwth.commons.Names;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class DSTLGenInheritanceHelper {

  /**
   * Constructor for de.monticore.tf.ruletranslation.InheritanceHelper
   */
  public DSTLGenInheritanceHelper() {
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

  public String getSuperGrammarName(ASTMCGrammar ast){
    if (!ast.getSupergrammarList().isEmpty()) {
      List<String> result = ast.getSupergrammarList().stream()
                                                 .filter(p -> !isCommonSuperGrammar(p.getNameList()))
                                                 .findFirst().get().getNameList();
      return  result.get(result.size() - 1);
    }
    return "";
  }

  public String getSuperGrammarNameLowerCased(ASTMCGrammar ast){
    return getSuperGrammarName(ast).toLowerCase();
  }


  public boolean hasSupergrammar(ASTMCGrammar ast){
    List<ASTGrammarReference> superGrammars = ast.getSupergrammarList();
    return !superGrammars.isEmpty()
           && !(superGrammars.size() == 1 && isCommonSuperGrammar(superGrammars.get(0).getNameList()));
  }

  public boolean isCommonSuperGrammar(String grammar) {
    return isCommonSuperGrammar(Collections.singletonList(grammar));
  }

  public boolean isCommonSuperGrammar(List<String> grammar) {
    return grammar.contains("MCBasics") || grammar.contains("JavaDSL") || grammar.contains("OCL")
            || grammar.contains("StringLiterals") || grammar.contains("MCNumbers") || grammar.contains("MCJavaLiterals");
  }
  
  public String getOwnSetMethodName(String _package, String grammar){
    return "set_" +_package.replace('.', '_') + "_tr_" + grammar.toLowerCase()+"tr__visitor";
  }
  
  public List<MCGrammarSymbol> getSuperGrammars(ASTMCGrammar ast){
    MCGrammarSymbol grammar = (MCGrammarSymbol) ast.getSymbol();
    return getSuperGrammars(grammar);
    
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
  
  public String getGrammarSetter(MCGrammarSymbol grammar){
    return "set_" + grammar.getPackageName().replace('.','_') + "_tr_" + grammar.getName().toLowerCase() + "tr__visitor";
    
  }
  
  
  
  
}
