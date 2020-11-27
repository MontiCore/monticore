package mc.testcd4analysis._symboltable;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;
import mc.testcd4analysis._ast.*;
import mc.testcd4analysis._visitor.TestCD4AnalysisVisitor;
import mc.testcd4analysis._visitor.TestCD4AnalysisVisitor2;

import java.util.Deque;
import java.util.List;

public class TestCD4AnalysisSTCompleteTypes implements TestCD4AnalysisVisitor2 {

  protected Deque<? extends ITestCD4AnalysisScope> scopeStack;

  public TestCD4AnalysisSTCompleteTypes(Deque<? extends ITestCD4AnalysisScope> scopeStack){
    this.scopeStack = scopeStack;
  }

  @Override
  public void endVisit(ASTCDAttribute astAttribute){
    CDFieldSymbol fieldSymbol = astAttribute.getSymbol();
    ASTMCType astType = astAttribute.getMCType();
    final String typeName;
    if (astType instanceof ASTMCGenericType) {
      typeName = ((ASTMCGenericType) astType).printWithoutTypeArguments();
    } else {
      typeName = astAttribute.getMCType().printType(new MCCollectionTypesPrettyPrinter(new IndentPrinter()));
    }

    final CDTypeSymbolSurrogate typeReference = new CDTypeSymbolSurrogate(typeName);
    typeReference.setEnclosingScope(scopeStack.peekLast());
    fieldSymbol.setType(typeReference);

    if (astAttribute.isPresentModifier()) {
      final ASTModifier astModifier = astAttribute.getModifier();

      fieldSymbol.setIsDerived(astModifier.isDerived());
      fieldSymbol.setIsStatic(astModifier.isStatic());
      fieldSymbol.setIsFinal(astModifier.isFinal());

      if (astModifier.isProtected()) {
        fieldSymbol.setIsProtected(true);
      } else if (astModifier.isPrivate()) {
        fieldSymbol.setIsPrivate(true);
      } else {
        // public is default
        fieldSymbol.setIsPublic(true);
      }
    }
  }

  @Override
  public void endVisit(ASTCDMethod astMethod){
    CDMethOrConstrSymbol methodSymbol = astMethod.getSymbol();
    setReturnTypeOfMethod(methodSymbol, astMethod);
    if (!astMethod.getCDParameterList().isEmpty()) {
      if (astMethod.getCDParameter(astMethod.getCDParameterList().size() - 1).isEllipsis()) {
        methodSymbol.setIsEllipsis(true);
      }
    }
  }

  public void setReturnTypeOfMethod(final CDMethOrConstrSymbol methodSymbol, ASTCDMethod astMethod) {
    final CDTypeSymbolSurrogate returnSymbol = new CDTypeSymbolSurrogate(
        ( astMethod.getMCReturnType().printType(new MCCollectionTypesPrettyPrinter(new IndentPrinter()))));
    returnSymbol.setEnclosingScope(scopeStack.peekLast());
    methodSymbol.setReturnType(returnSymbol);
  }

  @Override
  public void endVisit(ASTCDInterface astInterface){
    CDTypeSymbol interfaceSymbol = astInterface.getSymbol();
    interfaceSymbol.setIsInterface(true);
    // Interfaces are always abstract
    interfaceSymbol.setIsAbstract(true);

    addInterfacesToType(interfaceSymbol, astInterface.getInterfaceList());

    // Interfaces are always abstract
    interfaceSymbol.setIsAbstract(true);
  }

  public void addInterfacesToType(final CDTypeSymbol typeSymbol, final List<ASTMCObjectType> astInterfaces) {
    if (astInterfaces != null) {
      for (final ASTMCObjectType superInterface : astInterfaces) {
        final CDTypeSymbolSurrogate superInterfaceSymbol = createCDTypeSymbolFromReference(superInterface);
        typeSymbol.getCdInterfacesList().add(superInterfaceSymbol);
      }

    }
  }

  CDTypeSymbolSurrogate createCDTypeSymbolFromReference(final ASTMCObjectType astmcObjectType) {
    CDTypeSymbolSurrogate surrogate =  new CDTypeSymbolSurrogate(
        astmcObjectType.printType(new MCCollectionTypesPrettyPrinter(new IndentPrinter())));
    surrogate.setEnclosingScope(scopeStack.peekLast());
    return surrogate;
  }

  @Override
  public void endVisit(ASTCDClass astClass){
    CDTypeSymbol symbol = astClass.getSymbol();
    symbol.setIsClass(true);

    if (astClass.isPresentSuperclass()) {
      ASTMCObjectType superC = astClass.getSuperclass();
      final CDTypeSymbolSurrogate superClassSymbol = createCDTypeSymbolFromReference(superC);
      symbol.setSuperClass(superClassSymbol);
    }

    addInterfacesToType(symbol, astClass.getInterfaceList());
  }

}
