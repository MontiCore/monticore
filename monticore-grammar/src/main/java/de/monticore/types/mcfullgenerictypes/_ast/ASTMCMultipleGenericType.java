// (c) https://github.com/MontiCore/monticore

package de.monticore.types.mcfullgenerictypes._ast;

public class ASTMCMultipleGenericType extends ASTMCMultipleGenericTypeTOP {

  protected ASTMCMultipleGenericType() {
  }

  public String printWithoutTypeArguments() {
    // from a.B<C>D.E.<F>.G ist will return a.B.C.D.E.G
    String firstGenericType = getMCBasicGenericType().printWithoutTypeArguments();
    String innerTypes = getMCInnerTypeList()
        .stream()
        .map(ASTMCInnerType::getName)
        .reduce((a, b) -> a + "." + b).get();
    return firstGenericType + "." + innerTypes;
  }
}
