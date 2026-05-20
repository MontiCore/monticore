// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter.frames;

import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;

/**
 * A frame for BasicSymbols.
 * <p>
 * Simply adds references to the corresponding VariableSymbols.
 */
public class MIFrameForBasicSymbols extends MIFrame {

  // only kept to print
  // not peak efficient,
  // but relevant enough to allow for a bit of suboptimal memory usage
  protected final MIFrameLayoutForBasicSymbols frameLayout;

  public MIFrameForBasicSymbols(
      MIFrameLayoutForBasicSymbols frameLayout,
      MIFrame parentFrame
  ) {
    super(frameLayout, parentFrame);
    this.frameLayout = frameLayout;
  }

  public MIFrameForBasicSymbols(
      MIFrameLayoutForBasicSymbols frameLayout
  ) {
    super(frameLayout);
    this.frameLayout = frameLayout;
  }

  // helper

  /**
   * Clones this frame data into a new frame with extended layout.
   * Used first and foremost to extend the topmost frame.
   * <p>
   * This should not be called under most circumstances.
   * And is used in, e.g., REPLs.
   * <p>
   * _could_ be moved to MIFrame, but currently not needed
   * and would require increasing the complexity of MIFrame.
   */
  public MIFrameForBasicSymbols createExpandedCopy(
      MIFrameLayoutForBasicSymbols newLayout
  ) {
    // assert that initial layout did not change
    if (!frameLayout.isPrefixOf(newLayout)) {
      throw new IllegalArgumentException(
          "Expected the frame layout to be a prefix"
      );
    }
    MIFrameForBasicSymbols copy = new MIFrameForBasicSymbols(
        newLayout, parentFrame.orElse(null)
    );
    System.arraycopy(booleans, 0, copy.booleans, 0, booleans.length);
    System.arraycopy(ints, 0, copy.ints, 0, ints.length);
    System.arraycopy(doubles, 0, copy.doubles, 0, doubles.length);
    System.arraycopy(objects, 0, copy.objects, 0, objects.length);
    return copy;
  }

  public MIFrameLayoutForBasicSymbols getFrameLayout() {
    return frameLayout;
  }

  public String printVariablesForLog() {
    StringBuilder r = new StringBuilder();
    boolean printedSomething = false;
    if (booleans.length > 0) {
      r.append("  booleans: ").append(System.lineSeparator());
      for (int i = 0; i < booleans.length; i++) {
        r.append("    ")
            .append(printVariableSymbolForLog(frameLayout.getBooleanSymbol(i)))
            .append(": ")
            .append(booleans[i])
            .append(System.lineSeparator());
      }
      printedSomething = true;
    }
    if (ints.length > 0) {
      r.append("  ints: ").append(System.lineSeparator());
      for (int i = 0; i < ints.length; i++) {
        r.append("    ")
            .append(printVariableSymbolForLog(frameLayout.getIntSymbol(i)))
            .append(": ")
            .append(ints[i])
            .append(System.lineSeparator());
      }
      printedSomething = true;
    }
    if (doubles.length > 0) {
      r.append("  doubles: ").append(System.lineSeparator());
      for (int i = 0; i < doubles.length; i++) {
        r.append("    ")
            .append(printVariableSymbolForLog(frameLayout.getDoubleSymbol(i)))
            .append(": ")
            .append(doubles[i])
            .append(System.lineSeparator());
      }
      printedSomething = true;
    }
    if (objects.length > 0) {
      r.append("  objects: ").append(System.lineSeparator());
      for (int i = 0; i < objects.length; i++) {
        r.append("    ")
            .append(printVariableSymbolForLog(frameLayout.getObjectSymbol(i)))
            .append(": ");
        if (objects[i] != null) {
          r.append(objects[i].printValue())
              .append(" : ")
              .append(objects[i].printType());
        }
        else {
          r.append("null");
        }
        r.append(System.lineSeparator());
      }
      printedSomething = true;
    }
    if (!printedSomething) {
      r.append("No variables in the frame.");
    }
    return r.toString();
  }

  protected String printVariableSymbolForLog(VariableSymbol symbol) {
    StringBuilder r = new StringBuilder();
    r.append(symbol.getName());
    if (symbol.isPresentAstNode()) {
      r.append(" (")
          .append(symbol.getSourcePosition().toString())
          .append(")");
    }
    return r.toString();
  }

}
