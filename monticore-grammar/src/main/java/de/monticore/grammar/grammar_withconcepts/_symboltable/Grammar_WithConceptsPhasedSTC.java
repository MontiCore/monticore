/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammar_withconcepts._symboltable;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._symboltable.GrammarSTCompleteTypes;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsTraverser;
import de.monticore.javalight._symboltable.JavaLightSTCompleteTypes;
import de.monticore.statements.mccommonstatements._symboltable.MCCommonStatementsSTCompleteTypes;
import de.monticore.statements.mcvardeclarationstatements._symboltable.MCVarDeclarationStatementsSTCompleteTypes;

import java.util.ArrayList;
import java.util.List;

public class Grammar_WithConceptsPhasedSTC {

  protected IGrammar_WithConceptsGlobalScope globalScope;

  protected Grammar_WithConceptsScopesGenitorDelegator scopesGenitorDelegator;

  protected List<Grammar_WithConceptsTraverser> priorityList;

  public Grammar_WithConceptsPhasedSTC(){
    this(Grammar_WithConceptsMill.globalScope());
  }

  public Grammar_WithConceptsPhasedSTC(IGrammar_WithConceptsGlobalScope globalScope){
    this.globalScope = globalScope;
    this.scopesGenitorDelegator = new Grammar_WithConceptsScopesGenitorDelegator(globalScope);
    this.priorityList = new ArrayList<>();
    Grammar_WithConceptsTraverser traverser = Grammar_WithConceptsMill.traverser();
    traverser.add4Grammar(new GrammarSTCompleteTypes());
    traverser.add4JavaLight(new JavaLightSTCompleteTypes());
    traverser.add4MCCommonStatements(new MCCommonStatementsSTCompleteTypes());
    traverser.add4MCVarDeclarationStatements(new MCVarDeclarationStatementsSTCompleteTypes());
    priorityList.add(traverser);
  }

  public IGrammar_WithConceptsArtifactScope createFromAST(ASTMCGrammar node){
    IGrammar_WithConceptsArtifactScope as = scopesGenitorDelegator.createFromAST(node);
    priorityList.forEach(node::accept);
    return as;
  }

}
