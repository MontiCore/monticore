
/* (c) https://github.com/MontiCore/monticore */

import de.monticore.ast.ASTNode;
import de.monticore.literals.mccommonliterals._ast.ASTMCCommonLiteralsNode;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import questionnaire._ast.*;
import questionnaire._visitor.*;

import java.util.Optional;

/**
 * Small pretty printer for questionnaires
 */
public class QuestionnaireTreeHandler implements QuestionnaireHandler {
  
  QuestionnaireTraverser traverser;
  
  public QuestionnaireTraverser getTraverser() {
    return traverser;
  }
  
  public void setTraverser(QuestionnaireTraverser traverser) {
    this.traverser = traverser;
  }
  
  @Override
  public void handle(questionnaire._ast.ASTQDefinition node) {
    getTraverser().visit((ASTNode) node);
    getTraverser().visit((questionnaire._ast.ASTQuestionnaireNode) node);
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);
    getTraverser().endVisit((questionnaire._ast.ASTQuestionnaireNode) node);
    getTraverser().endVisit((ASTNode) node);
  }
  
  @Override
  public void handle(questionnaire._ast.ASTItem node) {
    getTraverser().visit((ASTNode) node);
    getTraverser().visit((questionnaire._ast.ASTQuestionnaireNode) node);
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);
    getTraverser().endVisit((questionnaire._ast.ASTQuestionnaireNode) node);
    getTraverser().endVisit((ASTNode) node);
  }
  
  @Override
  public void handle(questionnaire._ast.ASTScale node) {
    getTraverser().visit((ASTNode) node);
    getTraverser().visit((questionnaire._ast.ASTQuestionnaireNode) node);
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);
    getTraverser().endVisit((questionnaire._ast.ASTQuestionnaireNode) node);
    getTraverser().endVisit((ASTNode) node);
  }
  
  @Override
  public void handle(questionnaire._ast.ASTRange node) {
    getTraverser().visit((ASTNode) node);
    getTraverser().visit((questionnaire._ast.ASTQuestionnaireNode) node);
    getTraverser().visit((questionnaire._ast.ASTScaleType) node);
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);
    getTraverser().endVisit((questionnaire._ast.ASTScaleType) node);
    getTraverser().endVisit((questionnaire._ast.ASTQuestionnaireNode) node);
    getTraverser().endVisit((ASTNode) node);
  }
  
  @Override
  public void handle(questionnaire._ast.ASTNumber node) {
    getTraverser().visit((ASTNode) node);
    getTraverser().visit((questionnaire._ast.ASTQuestionnaireNode) node);
    getTraverser().visit((questionnaire._ast.ASTScaleType) node);
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);
    getTraverser().endVisit((questionnaire._ast.ASTScaleType) node);
    getTraverser().endVisit((questionnaire._ast.ASTQuestionnaireNode) node);
    getTraverser().endVisit((ASTNode) node);
  }
  
  @Override
  public void handle(questionnaire._ast.ASTText node) {
    getTraverser().visit((ASTNode) node);
    getTraverser().visit((questionnaire._ast.ASTQuestionnaireNode) node);
    getTraverser().visit((questionnaire._ast.ASTScaleType) node);
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);
    getTraverser().endVisit((questionnaire._ast.ASTScaleType) node);
    getTraverser().endVisit((questionnaire._ast.ASTQuestionnaireNode) node);
    getTraverser().endVisit((ASTNode) node);
  }
  
  @Override
  public void handle(questionnaire._ast.ASTSelect node) {
    getTraverser().visit((ASTNode) node);
    getTraverser().visit((questionnaire._ast.ASTQuestionnaireNode) node);
    getTraverser().visit((questionnaire._ast.ASTScaleType) node);
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);
    getTraverser().endVisit((questionnaire._ast.ASTScaleType) node);
    getTraverser().endVisit((questionnaire._ast.ASTQuestionnaireNode) node);
    getTraverser().endVisit((ASTNode) node);
  }
  
  @Override
  public void handle(questionnaire._ast.ASTSelectOption node) {
    getTraverser().visit((ASTNode) node);
    getTraverser().visit((questionnaire._ast.ASTQuestionnaireNode) node);
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);
    getTraverser().endVisit((questionnaire._ast.ASTQuestionnaireNode) node);
    getTraverser().endVisit((ASTNode) node);
  }
  
  @Override
  public void handle(questionnaire._ast.ASTScaleType node) {
    getTraverser().visit((ASTNode) node);
    getTraverser().visit((questionnaire._ast.ASTQuestionnaireNode) node);
    getTraverser().visit(node);
    getTraverser().endVisit(node);
    getTraverser().endVisit((questionnaire._ast.ASTQuestionnaireNode) node);
    getTraverser().endVisit((ASTNode) node);
  }
  
  @Override
  public void handle(questionnaire._ast.ASTQuestionnaireNode node) {
    getTraverser().visit((ASTNode) node);
    getTraverser().visit((questionnaire._ast.ASTQuestionnaireNode) node);
    getTraverser().visit(node);
    getTraverser().endVisit(node);
    getTraverser().endVisit((questionnaire._ast.ASTQuestionnaireNode) node);
    getTraverser().endVisit((ASTNode) node);
  }
  
  @Override
  public void handle(questionnaire._symboltable.IQuestionnaireScope node) {
    getTraverser().visit((de.monticore.symboltable.IScope) node);
    getTraverser().visit((de.monticore.literals.mcliteralsbasis._symboltable.IMCLiteralsBasisScope) node);
    getTraverser().visit((de.monticore.mcbasics._symboltable.IMCBasicsScope) node);
    getTraverser().visit((de.monticore.literals.mccommonliterals._symboltable.IMCCommonLiteralsScope) node);
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);
    getTraverser().endVisit((de.monticore.literals.mccommonliterals._symboltable.IMCCommonLiteralsScope) node);
    getTraverser().endVisit((de.monticore.mcbasics._symboltable.IMCBasicsScope) node);
    getTraverser().endVisit((de.monticore.literals.mcliteralsbasis._symboltable.IMCLiteralsBasisScope) node);
    getTraverser().endVisit((de.monticore.symboltable.IScope) node);
  }
  
  @Override
  public void handle(questionnaire._symboltable.IQuestionnaireArtifactScope node) {
    getTraverser().visit((de.monticore.symboltable.IScope) node);
    getTraverser().visit((de.monticore.literals.mcliteralsbasis._symboltable.IMCLiteralsBasisScope) node);
    getTraverser().visit((de.monticore.mcbasics._symboltable.IMCBasicsScope) node);
    getTraverser().visit((de.monticore.literals.mccommonliterals._symboltable.IMCCommonLiteralsScope) node);
    getTraverser().visit((questionnaire._symboltable.IQuestionnaireScope) node);
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);
    getTraverser().endVisit((questionnaire._symboltable.IQuestionnaireScope) node);
    getTraverser().endVisit((de.monticore.literals.mccommonliterals._symboltable.IMCCommonLiteralsScope) node);
    getTraverser().endVisit((de.monticore.mcbasics._symboltable.IMCBasicsScope) node);
    getTraverser().endVisit((de.monticore.literals.mcliteralsbasis._symboltable.IMCLiteralsBasisScope) node);
    getTraverser().endVisit((de.monticore.symboltable.IScope) node);
  }
  
  @Override
  public void handle(questionnaire._symboltable.QDefinitionSymbol node) {
    getTraverser().visit((de.monticore.symboltable.ISymbol) node);
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);
    getTraverser().endVisit((de.monticore.symboltable.ISymbol) node);
  }
  
  @Override
  public void handle(questionnaire._symboltable.ItemSymbol node) {
    getTraverser().visit((de.monticore.symboltable.ISymbol) node);
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);
    getTraverser().endVisit((de.monticore.symboltable.ISymbol) node);
  }
  
  @Override
  public void handle(questionnaire._symboltable.ScaleSymbol node) {
    getTraverser().visit((de.monticore.symboltable.ISymbol) node);
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);
    getTraverser().endVisit((de.monticore.symboltable.ISymbol) node);
  }
}
