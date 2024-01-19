/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight._symboltable;

import de.monticore.grammar.grammar_withconcepts.FullSynthesizeFromMCSGT4Grammar;
import de.monticore.javalight._ast.*;
import de.monticore.javalight._visitor.JavaLightVisitor2;
import de.monticore.statements.mccommonstatements._ast.ASTJavaModifier;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCModifier;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfNull;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import static de.monticore.statements.mccommonstatements._ast.ASTConstantsMCCommonStatements.*;

public class JavaLightSTCompleteTypes implements JavaLightVisitor2 {

  @Override
  public void endVisit(ASTLastFormalParameter ast) {
    FieldSymbol symbol = ast.getDeclaratorId().getSymbol();
    symbol.setType(createTypeLoader(ast.getMCType()));
  }

  @Override
  public void endVisit(ASTMethodDeclaration ast){
    JavaMethodSymbol symbol = ast.getSymbol();
    addModifiersToMethOrConstr(symbol, ast.getMCModifierList());
    symbol.setType(createTypeLoader(ast.getMCReturnType()));
    if (ast.isPresentThrows()) {
      addThrowsToMethod(symbol, ast.getThrows());
    }
    if (ast.getFormalParameters().isPresentFormalParameterListing()
        && ast.getFormalParameters().getFormalParameterListing().isPresentLastFormalParameter()) {
      symbol.setIsElliptic(true);
    }
  }

  @Override
  public void endVisit(ASTConstructorDeclaration ast){
    JavaMethodSymbol symbol = ast.getSymbol();
    addModifiersToMethOrConstr(symbol, ast.getMCModifierList());
    if (ast.isPresentThrows()) {
      addThrowsToMethod(symbol, ast.getThrows());
    }
    if (ast.getFormalParameters().isPresentFormalParameterListing()
        && ast.getFormalParameters().getFormalParameterListing().isPresentLastFormalParameter()) {
      symbol.setIsElliptic(true);
    }
  }

  protected void addModifiersToMethOrConstr(JavaMethodSymbol javaMethodSymbol,
                                            Iterable<? extends ASTMCModifier> astModifierList) {
    for (ASTMCModifier modifier : astModifierList) {
      if (modifier instanceof ASTJavaModifier) {
        // visibility
        switch (((ASTJavaModifier) modifier).getModifier()) {
          case PUBLIC:
            javaMethodSymbol.setIsPublic(true);
            break;
          case PROTECTED:
            javaMethodSymbol.setIsProtected(true);
            break;
          case PRIVATE:
            javaMethodSymbol.setIsPrivate(true);
            // other variable modifiers as in jls7 8.3.1 Field Modifiers
            break;
          case ABSTRACT:
            javaMethodSymbol.setIsAbstract(true);
            break;
          case STATIC:
            javaMethodSymbol.setIsStatic(true);
            break;
          case FINAL:
            javaMethodSymbol.setIsFinal(true);
            break;
          case NATIVE:
            javaMethodSymbol.setIsNative(true);
            break;
          case STRICTFP:
            javaMethodSymbol.setIsStrictfp(true);
            break;
          case SYNCHRONIZED:
            javaMethodSymbol.setIsSynchronized(true);
            break;
          case MODIFIER_DEFAULT:
            javaMethodSymbol.setIsDefault(true);
          default:
            break;
        }
      } else if (modifier instanceof ASTAnnotation) {
        ASTAnnotation astAnnotation = (ASTAnnotation) modifier;
        javaMethodSymbol.addAnnotations(createTypeLoader(astAnnotation.getAnnotationName()));
      }
    }
  }

  protected void addThrowsToMethod(JavaMethodSymbol javaMethodSymbol, ASTThrows throws1) {
    for (ASTMCQualifiedName astQualifiedName : throws1.getMCQualifiedNameList()) {
      javaMethodSymbol.addExceptions(createTypeLoader(astQualifiedName));
    }
  }

  protected SymTypeExpression createTypeLoader(ASTMCQualifiedName ast) {
    FullSynthesizeFromMCSGT4Grammar synFromFull = new FullSynthesizeFromMCSGT4Grammar();
    // Start visitor
    TypeCheckResult typeCheckResult = synFromFull.synthesizeType(ast);
    if(typeCheckResult.isPresentResult()){
      return typeCheckResult.getResult();
    }
    return new SymTypeOfNull();
  }

  protected SymTypeExpression createTypeLoader(ASTMCType ast) {
    FullSynthesizeFromMCSGT4Grammar synFromFull = new FullSynthesizeFromMCSGT4Grammar();
    // Start visitor
    TypeCheckResult typeCheckResult = synFromFull.synthesizeType(ast);
    if(typeCheckResult.isPresentResult()){
      return typeCheckResult.getResult();
    }
    return new SymTypeOfNull();
  }

  protected SymTypeExpression createTypeLoader(ASTMCReturnType ast) {
    FullSynthesizeFromMCSGT4Grammar synFromFull = new FullSynthesizeFromMCSGT4Grammar();
    // Start visitor
    TypeCheckResult typeCheckResult = synFromFull.synthesizeType(ast);
    if(typeCheckResult.isPresentResult()){
      return typeCheckResult.getResult();
    }
    return new SymTypeOfNull();
  }
}
