/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammarfamily._symboltable;

import de.monticore.cd4code._symboltable.CD4AnalysisSTCompleteTypes;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTableCompleter;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTableCompleter;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._symboltable.GrammarSTCompleteTypes;
import de.monticore.grammar.grammarfamily.GrammarFamilyMill;
import de.monticore.grammar.grammarfamily._visitor.GrammarFamilyTraverser;
import de.monticore.javalight._symboltable.JavaLightSTCompleteTypes;
import de.monticore.statements.mccommonstatements._symboltable.MCCommonStatementsSTCompleteTypes;
import de.monticore.statements.mcvardeclarationstatements._symboltable.MCVarDeclarationStatementsSTCompleteTypes;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;

import java.util.ArrayList;
import java.util.List;

public class GrammarFamilyPhasedSTC {

  protected IGrammarFamilyGlobalScope globalScope;

  protected GrammarFamilyScopesGenitorDelegator scopesGenitorDelegator;

  protected List<GrammarFamilyTraverser> priorityList;

  public GrammarFamilyPhasedSTC(){
    this(GrammarFamilyMill.globalScope());
  }

  public GrammarFamilyPhasedSTC(IGrammarFamilyGlobalScope globalScope){
    this.globalScope = globalScope;
    this.scopesGenitorDelegator = new GrammarFamilyScopesGenitorDelegator(globalScope);
    this.priorityList = new ArrayList<>();
    GrammarFamilyTraverser traverser = GrammarFamilyMill.traverser();
    traverser.add4Grammar(new GrammarSTCompleteTypes());
    traverser.add4JavaLight(new JavaLightSTCompleteTypes());
    traverser.add4MCCommonStatements(new MCCommonStatementsSTCompleteTypes());
    traverser.add4MCVarDeclarationStatements(new MCVarDeclarationStatementsSTCompleteTypes());

    // TODO: activate transformation and real symbol table completer, when cd4a is ready
    CD4AnalysisSTCompleteTypes stc = new CD4AnalysisSTCompleteTypes();
    traverser.add4CDBasis(stc);
    traverser.add4CDInterfaceAndEnum(stc);

    priorityList.add(traverser);
  }

  public IGrammarFamilyArtifactScope createFromAST(ASTMCGrammar node){
    IGrammarFamilyArtifactScope as = scopesGenitorDelegator.createFromAST(node);
    priorityList.forEach(node::accept);
    return as;
  }

  public IGrammarFamilyArtifactScope createFromAST(ASTCDCompilationUnit node){
    IGrammarFamilyArtifactScope as = scopesGenitorDelegator.createFromAST(node);
    priorityList.forEach(node::accept);
    return as;
  }

}
