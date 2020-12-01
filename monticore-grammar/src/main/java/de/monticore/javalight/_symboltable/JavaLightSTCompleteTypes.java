package de.monticore.javalight._symboltable;

import de.monticore.javalight._ast.*;
import de.monticore.javalight._visitor.JavaLightVisitor;
import de.monticore.javalight._visitor.JavaLightVisitor2;
import de.monticore.statements.mccommonstatements._ast.ASTJavaModifier;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCModifier;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsScope;
import de.monticore.types.check.*;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.monticore.types.mcfullgenerictypes._visitor.MCFullGenericTypesTraverser;

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
  public void endVisit(ASTInterfaceMethodDeclaration ast){
    JavaMethodSymbol symbol = ast.getSymbol();
    addModifiersToMethOrConstr(symbol, ast.getMCModifierList());
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

  private SymTypeExpression createTypeLoader(ASTMCQualifiedName ast) {
    SynthesizeSymTypeFromMCFullGenericTypes synFromFull = new SynthesizeSymTypeFromMCFullGenericTypes();
    // Start visitor
    ast.accept(getSynthesizer(synFromFull));
    return synFromFull.getResult().orElse(new SymTypeOfNull());
  }

  private SymTypeExpression createTypeLoader(ASTMCType ast) {
    SynthesizeSymTypeFromMCFullGenericTypes synFromFull = new SynthesizeSymTypeFromMCFullGenericTypes();
    // Start visitor
    ast.accept(getSynthesizer(synFromFull));
    return synFromFull.getResult().orElse(new SymTypeOfNull());
  }

  private MCFullGenericTypesTraverser getSynthesizer(SynthesizeSymTypeFromMCFullGenericTypes synFromFull){
    SynthesizeSymTypeFromMCSimpleGenericTypes synFromSimple = new SynthesizeSymTypeFromMCSimpleGenericTypes();
    SynthesizeSymTypeFromMCCollectionTypes synFromCollection = new SynthesizeSymTypeFromMCCollectionTypes();
    SynthesizeSymTypeFromMCBasicTypes synFromBasic = new SynthesizeSymTypeFromMCBasicTypes();

    MCFullGenericTypesTraverser traverser = MCFullGenericTypesMill.traverser();
    traverser.add4MCFullGenericTypes(synFromFull);
    traverser.setMCFullGenericTypesHandler(synFromFull);
    traverser.add4MCSimpleGenericTypes(synFromSimple);
    traverser.setMCSimpleGenericTypesHandler(synFromSimple);
    traverser.add4MCCollectionTypes(synFromCollection);
    traverser.setMCCollectionTypesHandler(synFromCollection);
    traverser.add4MCBasicTypes(synFromBasic);
    traverser.setMCBasicTypesHandler(synFromBasic);

    return traverser;
  }

  private SymTypeExpression createTypeLoader(ASTMCReturnType ast) {
    if (ast.isPresentMCType()) {
      return createTypeLoader(ast.getMCType());
    } else {
      // TODO Bessere Lösung
      return SymTypeExpressionFactory.createTypeObject("void", (IOOSymbolsScope) ast.getEnclosingScope());
    }

  }
}
