/* (c) https://github.com/MontiCore/monticore */
package de.monticore.javalight._symboltable;

import de.monticore.javalight._ast.*;
import de.monticore.statements.mccommonstatements._ast.ASTJavaModifier;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCModifier;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfNull;
import de.monticore.types.check.SynthesizeSymTypeFromMCFullGenericTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;

import java.util.Deque;

import static de.monticore.statements.mccommonstatements._ast.ASTConstantsMCCommonStatements.*;

public class JavaLightSymbolTableCreator extends JavaLightSymbolTableCreatorTOP {

  public JavaLightSymbolTableCreator(IJavaLightScope enclosingScope) {
    super(enclosingScope);
  }

  public JavaLightSymbolTableCreator(Deque<? extends IJavaLightScope> scopeStack) {
    super(scopeStack);
  }

  @Override
  protected void initialize_MethodDeclaration(MethOrConstrSymbol symbol, ASTMethodDeclaration ast) {
    addModifiersToMethOrConstr(symbol, ast.getMCModifiersList());
    symbol.setReturnType(createTypeLoader(ast.getMCReturnType()));
    if (ast.isPresentThrows()) {
      addThrowsToMethod(symbol, ast.getThrows());
    }
    if (ast.getFormalParameters().isPresentFormalParameterListing()
            && ast.getFormalParameters().getFormalParameterListing().isPresentLastFormalParameter()) {
      symbol.setIsElliptic(true);
    }
  }

  @Override
  protected void initialize_InterfaceMethodDeclaration(MethOrConstrSymbol symbol, ASTInterfaceMethodDeclaration ast) {
    addModifiersToMethOrConstr(symbol, ast.getMCModifiersList());
    symbol.setReturnType(createTypeLoader(ast.getMCReturnType()));
    if (ast.isPresentThrows()) {
      addThrowsToMethod(symbol, ast.getThrows());
    }
    if (ast.getFormalParameters().isPresentFormalParameterListing()
            && ast.getFormalParameters().getFormalParameterListing().isPresentLastFormalParameter()) {
      symbol.setIsElliptic(true);
    }
  }

  @Override
  protected void initialize_ConstructorDeclaration(MethOrConstrSymbol symbol, ASTConstructorDeclaration ast) {
    addModifiersToMethOrConstr(symbol, ast.getMCModifiersList());
    if (ast.isPresentThrows()) {
      addThrowsToMethod(symbol, ast.getThrows());
    }
    if (ast.getFormalParameters().isPresentFormalParameterListing()
            && ast.getFormalParameters().getFormalParameterListing().isPresentLastFormalParameter()) {
      symbol.setIsElliptic(true);
    }
  }

  @Override
  public void endVisit(ASTFormalParameter ast) {
    FieldSymbol symbol = ast.getDeclaratorId().getSymbol();
    symbol.setType(createTypeLoader(ast.getMCType()));
  }

  @Override
  public void endVisit(ASTLastFormalParameter ast) {
    FieldSymbol symbol = ast.getDeclaratorId().getSymbol();
    symbol.setType(createTypeLoader(ast.getMCType()));
  }

  protected void addModifiersToMethOrConstr(MethOrConstrSymbol javaMethodSymbol,
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
          default:
            break;
        }
      } else if (modifier instanceof ASTAnnotation) {
        ASTAnnotation astAnnotation = (ASTAnnotation) modifier;
        javaMethodSymbol.addAnnotations(createTypeLoader(astAnnotation.getAnnotationName()));
      }
    }
  }

  protected void addThrowsToMethod(MethOrConstrSymbol javaMethodSymbol, ASTThrows throws1) {
    for (ASTMCQualifiedName astQualifiedName : throws1.getMCQualifiedNamesList()) {
      javaMethodSymbol.addExceptions(createTypeLoader(astQualifiedName));
    }
  }

  private SymTypeExpression createTypeLoader(ASTMCQualifiedName ast) {
    SynthesizeSymTypeFromMCFullGenericTypes syn = new SynthesizeSymTypeFromMCFullGenericTypes();
    // Start visitor and set enclosingScope
    ast.accept(getRealThis());
    ast.accept(syn);
    return syn.getResult().orElse(new SymTypeOfNull());
  }

  private SymTypeExpression createTypeLoader(ASTMCType ast) {
    SynthesizeSymTypeFromMCFullGenericTypes syn = new SynthesizeSymTypeFromMCFullGenericTypes();
    // Start visitor and set enclosingScope
    ast.accept(getRealThis());
    ast.accept(syn);
    return syn.getResult().orElse(new SymTypeOfNull());
  }

  private SymTypeExpression createTypeLoader(ASTMCReturnType ast) {
    if (ast.isPresentMCType()) {
      return createTypeLoader(ast.getMCType());
    } else {
      // Start visitor and set enclosingScope
      ast.accept(getRealThis());
      // TODO Bessere Lösung
      return SymTypeExpressionFactory.createTypeObject("void", (IOOSymbolsScope) ast.getEnclosingScope());
    }

  }

}
