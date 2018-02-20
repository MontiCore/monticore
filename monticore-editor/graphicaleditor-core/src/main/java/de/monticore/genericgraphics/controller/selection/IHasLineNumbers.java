/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.selection;

/**
 * An interface providing methods for accessing line numbers.
 * 
 * @author Tim Enger
 */
public interface IHasLineNumbers {
  
  /**
   * @return The line number of the start
   */
  public int getStartLine();
  
  /**
   * @return The offset in the start line number
   */
  public int getStartOffset();
  
  /**
   * @return The line number of the end
   */
  public int getEndLine();
  
  /**
   * @return The offset in the end line number
   */
  public int getEndOffset();
}
