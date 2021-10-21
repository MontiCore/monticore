/* (c) https://github.com/MontiCore/monticore */

package de.monticore.javalight.cocos;


import de.monticore.javalight._ast.ASTMethodDeclaration;
import de.monticore.javalight._cocos.JavaLightASTMethodDeclarationCoCo;
import de.monticore.javalight._symboltable.JavaMethodSymbol;
import de.se_rwth.commons.logging.Log;

public class MethodAbstractAndOtherModifiers implements JavaLightASTMethodDeclarationCoCo {

  //JLS3 8.4.3-3
  @Override
  public void check(ASTMethodDeclaration node) {
    JavaMethodSymbol methodSymbol = node.getSymbol();
    if (methodSymbol.isIsAbstract()) {
      if (methodSymbol.isIsPrivate()) {
        Log.error("0xA0802 abstract method must not be declared 'private'.",
                node.get_SourcePositionStart());
      }
      if (methodSymbol.isIsStatic()) {
        Log.error("0xA0803 abstract method must not be declared 'static'.",
                node.get_SourcePositionStart());
      }
      if (methodSymbol.isIsFinal()) {
        Log.error("0xA0804 abstract method must not be declared 'final'.",
                node.get_SourcePositionStart());
      }
      if (methodSymbol.isIsNative()) {
        Log.error("0xA0805 abstract method must not be declared 'native'.",
                node.get_SourcePositionStart());
      }
      if (methodSymbol.isIsStrictfp()) {
        Log.error("0xA0806 abstract method must not be declared 'strictfp'.",
                node.get_SourcePositionStart());
      }
      if (methodSymbol.isIsSynchronized()) {
        Log.error("0xA0807 abstract method must not be declared 'synchronized'.",
                node.get_SourcePositionStart());
      }
    }

  }
}
