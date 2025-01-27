/* (c) https://github.com/MontiCore/monticore */
package de.monticore.simplecd._symboltable;

import de.monticore.simplecd._ast.ASTCDAttribute;
import de.monticore.simplecd._ast.ASTCDClass;
import de.monticore.simplecd._ast.ASTCDCompilationUnit;
import de.monticore.simplecd._visitor.SimpleCDTraverser;
import de.monticore.simplecd._visitor.SimpleCDVisitor2;
import de.monticore.simplecd._prettyprint.SimpleCDFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.check.FullSynthesizeFromMCSimpleGenericTypes;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types3.TypeCheck3;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.Log;
import java.util.stream.Collectors;

public class SimpleCDSymbolTableCompleter implements SimpleCDVisitor2 {

  protected SimpleCDTraverser traverser;

  @Deprecated(forRemoval = true)
  protected ISynthesize typeSynthesizer;
  protected SimpleCDFullPrettyPrinter prettyPrinter;

  /**
   * @deprecated use default constructor
   */
  @Deprecated(forRemoval = true)
  public SimpleCDSymbolTableCompleter(ISynthesize typeSynthesizer) {
    this.typeSynthesizer = typeSynthesizer;
    prettyPrinter = new SimpleCDFullPrettyPrinter(new IndentPrinter());
  }

  public SimpleCDSymbolTableCompleter() {
    prettyPrinter = new SimpleCDFullPrettyPrinter(new IndentPrinter());
  }

  @Override
  public void visit(ASTCDCompilationUnit node) {
    final ISimpleCDScope artifactScope = node.getCDDefinition().getEnclosingScope();
    if (artifactScope instanceof ISimpleCDArtifactScope) {
      ((ISimpleCDArtifactScope) artifactScope)
        .addAllImports(
          node.getMCImportStatementList().stream()
            .map(i -> new ImportStatement(i.getQName(), i.isStar()))
            .collect(Collectors.toList()));
    }
  }

  @Override
  public void endVisit(ASTCDClass node) {
    assert node.getSymbol() != null;
    initialize_CDClass(node);
    SimpleCDVisitor2.super.endVisit(node);
  }

  protected void initialize_CDClass(ASTCDClass ast) {
    CDClassSymbol symbol = ast.getSymbol();
  }

  @Override
  public void visit(ASTCDAttribute node) {
    final VariableSymbol symbol = node.getSymbol();

    // Compute the !final! SymTypeExpression for the type of the field
    SymTypeExpression typeResult;
    if(getTypeSynthesizer() != null) {
      typeResult = getTypeSynthesizer().synthesizeType(node.getMCType()).getResult();
    } else {
      typeResult = TypeCheck3.symTypeFromAST(node.getMCType());
    }
    if (typeResult.isObscureType()) {
      Log.error(
        String.format(
          "0xCDA02: The type (%s) of the attribute (%s) could not be calculated",
          getPrettyPrinter().prettyprint(node.getMCType()), node.getName()),
        node.getMCType().get_SourcePositionStart());
    } else {
      symbol.setType(typeResult);
    }
  }

  @Override
  public void endVisit(ASTCDAttribute node) {
    assert node.getSymbol() != null;
    initialize_CDAttribute(node);
    SimpleCDVisitor2.super.endVisit(node);
  }

  protected void initialize_CDAttribute(ASTCDAttribute ast) {
    VariableSymbol symbol = ast.getSymbol();
  }

  @Deprecated(forRemoval = true)
  public ISynthesize getTypeSynthesizer() {
    return typeSynthesizer;
  }

  @Deprecated(forRemoval = true)
  public void setTypeSynthesizer(ISynthesize typeSynthesizer) {
    this.typeSynthesizer = typeSynthesizer;
  }

  public SimpleCDFullPrettyPrinter getPrettyPrinter() {
    return prettyPrinter;
  }

  public void setPrettyPrinter(SimpleCDFullPrettyPrinter prettyPrinter) {
    this.prettyPrinter = prettyPrinter;
  }

  public SimpleCDTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(SimpleCDTraverser traverser) {
    this.traverser = traverser;
  }
}
