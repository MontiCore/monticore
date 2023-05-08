/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammarfamily._symboltable;

import com.google.common.collect.Lists;
import de.monticore.cd4codebasis._symboltable.CD4CodeBasisSymbolTableCompleter;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTableCompleter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTableCompleter;
import de.monticore.cdinterfaceandenum._symboltable.CDInterfaceAndEnumSymbolTableCompleter;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._symboltable.GrammarSTCompleteTypes;
import de.monticore.grammar.grammar_withconcepts.FullSynthesizeFromMCSGT4Grammar;
import de.monticore.grammar.grammarfamily.GrammarFamilyMill;
import de.monticore.grammar.grammarfamily._visitor.GrammarFamilyTraverser;
import de.monticore.javalight._symboltable.JavaLightSTCompleteTypes;
import de.monticore.statements.mccommonstatements._symboltable.MCCommonStatementsSTCompleteTypes;
import de.monticore.statements.mcvardeclarationstatements._symboltable.MCVarDeclarationStatementsSTCompleteTypes;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.check.SynthesizeSymTypeFromMCBasicTypes;
import de.se_rwth.commons.Joiners;

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
    this.scopesGenitorDelegator = new GrammarFamilyScopesGenitorDelegator();
    this.priorityList = new ArrayList<>();
    GrammarFamilyTraverser traverser = GrammarFamilyMill.traverser();
    traverser.add4Grammar(new GrammarSTCompleteTypes());
    traverser.add4JavaLight(new JavaLightSTCompleteTypes());
    traverser.add4MCCommonStatements(new MCCommonStatementsSTCompleteTypes());
    traverser.add4MCVarDeclarationStatements(new MCVarDeclarationStatementsSTCompleteTypes());

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
    String packageName = Joiners.DOT.join(node.getCDPackageList());
    as.getLocalDiagramSymbols().forEach(s -> s.setPackageName(packageName));
    as.setPackageName(packageName);

    // Complete symbol table
    FullSynthesizeFromMCSGT4Grammar synthesize = new FullSynthesizeFromMCSGT4Grammar();
    GrammarFamilyTraverser traverser = GrammarFamilyMill.traverser();
    final CDBasisSymbolTableCompleter cDBasisVisitor = new CDBasisSymbolTableCompleter(synthesize);
    traverser.add4CDBasis(cDBasisVisitor);
    traverser.add4OOSymbols(cDBasisVisitor);
    final CDAssociationSymbolTableCompleter cDAssociationVisitor = new CDAssociationSymbolTableCompleter(synthesize);
    traverser.add4CDAssociation(cDAssociationVisitor);
    traverser.setCDAssociationHandler(cDAssociationVisitor);
    final CDInterfaceAndEnumSymbolTableCompleter cdInterfaceAndEnumVisitor = new CDInterfaceAndEnumSymbolTableCompleter(synthesize);
    traverser.add4CDInterfaceAndEnum(cdInterfaceAndEnumVisitor);
    final CD4CodeBasisSymbolTableCompleter cd4CodeBasisVisitor = new CD4CodeBasisSymbolTableCompleter(synthesize);
    traverser.add4CD4CodeBasis(cd4CodeBasisVisitor);
    node.accept(traverser);

    return as;
  }

}
