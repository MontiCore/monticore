/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis._symboltable;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._visitor.CDBasisTraverser;
import de.monticore.cdbasis._visitor.CDBasisVisitor2;
import de.monticore.cdbasis._prettyprint.CDBasisFullPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.check.FullSynthesizeFromMCSimpleGenericTypes;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.se_rwth.commons.logging.Log;
import java.util.stream.Collectors;

public class CDBasisSymbolTableCompleter implements CDBasisVisitor2 {

  protected CDBasisTraverser traverser;

  protected ISynthesize typeSynthesizer;
  protected CDBasisFullPrettyPrinter prettyPrinter;

  public CDBasisSymbolTableCompleter(ISynthesize typeSynthesizer) {
    this.typeSynthesizer = typeSynthesizer;
    prettyPrinter = new CDBasisFullPrettyPrinter(new IndentPrinter());
  }

  public CDBasisSymbolTableCompleter() {
    this(new FullSynthesizeFromMCSimpleGenericTypes());
  }

  @Override
  public void visit(ASTCDCompilationUnit node) {
    final ICDBasisScope artifactScope = node.getCDDefinition().getEnclosingScope();
    if (artifactScope instanceof ICDBasisArtifactScope) {
      ((ICDBasisArtifactScope) artifactScope)
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
    CDBasisVisitor2.super.endVisit(node);
  }

  protected void initialize_CDClass(ASTCDClass ast) {
    CDTypeSymbol symbol = ast.getSymbol();
    symbol.setIsClass(true);
    setupModifiers(ast.getModifier(), symbol);
  }

  @Override
  public void visit(ASTCDAttribute node) {
    final FieldSymbol symbol = node.getSymbol();

    // Compute the !final! SymTypeExpression for the type of the field
    final TypeCheckResult typeResult = getTypeSynthesizer().synthesizeType(node.getMCType());
    if (!typeResult.isPresentResult()) {
      Log.error(
        String.format(
          "0xCDA02: The type (%s) of the attribute (%s) could not be calculated",
          getPrettyPrinter().prettyprint(node.getMCType()), node.getName()),
        node.getMCType().get_SourcePositionStart());
    } else {
      symbol.setType(typeResult.getResult());
    }
  }

  @Override
  public void endVisit(ASTCDAttribute node) {
    assert node.getSymbol() != null;
    initialize_CDAttribute(node);
    CDBasisVisitor2.super.endVisit(node);
  }

  protected void initialize_CDAttribute(ASTCDAttribute ast) {
    FieldSymbol symbol = ast.getSymbol();
    setupModifiers(ast.getModifier(), symbol);
  }

  public void setupModifiers(ASTModifier modifier, CDTypeSymbol typeSymbol) {
    typeSymbol.setIsPublic(modifier.isPublic());
    typeSymbol.setIsPrivate(modifier.isPrivate());
    typeSymbol.setIsProtected(modifier.isProtected());
    typeSymbol.setIsStatic(modifier.isStatic());
    typeSymbol.setIsAbstract(modifier.isAbstract());
  }

  public void setupModifiers(ASTModifier modifier, FieldSymbol fieldSymbol) {
    fieldSymbol.setIsPublic(modifier.isPublic());
    fieldSymbol.setIsPrivate(modifier.isPrivate());
    fieldSymbol.setIsProtected(modifier.isProtected());
    fieldSymbol.setIsStatic(modifier.isStatic());
    fieldSymbol.setIsFinal(modifier.isFinal());
    fieldSymbol.setIsDerived(modifier.isDerived());
  }

  public ISynthesize getTypeSynthesizer() {
    return typeSynthesizer;
  }

  public void setTypeSynthesizer(ISynthesize typeSynthesizer) {
    this.typeSynthesizer = typeSynthesizer;
  }

  public CDBasisFullPrettyPrinter getPrettyPrinter() {
    return prettyPrinter;
  }

  public void setPrettyPrinter(CDBasisFullPrettyPrinter prettyPrinter) {
    this.prettyPrinter = prettyPrinter;
  }

  public CDBasisTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(CDBasisTraverser traverser) {
    this.traverser = traverser;
  }
}
