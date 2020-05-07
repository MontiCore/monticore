/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import de.monticore.antlr4.MCConcreteParser;
import de.monticore.modelloader.IModelLoader;

/**
 * @Deprecated File extension will be moved to global scope. Instances of parser, modelloader, etc.
 * are not managed centralized anymore.
 */
@Deprecated
public interface IModelingLanguage {
  
  /**
   * @return the name of the modeling language, e.g., "MontiCore Grammar Language"
   */
  String getName();

  /**
   * @return the file ending, e.g., ".cd"
   */
  String getFileExtension();

  /**
   * @return the parser for models of this language
   */
  MCConcreteParser getParser();

  /**
   * @return the file extension for stored symbol tables, e.g., ".cdsym"
   */
  default String getSymbolFileExtension() {
    return getFileExtension()+"sym";
  }

}
