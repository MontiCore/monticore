/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import de.monticore.antlr4.MCConcreteParser;
import de.monticore.modelloader.IModelLoader;

public interface IModelingLanguage<M extends IModelLoader<?,?>> {
  
  public static final String SYMBOL_FILE_ENDING = "sym";

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


  M getModelLoader();
  
  default String getSymbolFileExtension() {
    return getFileExtension()+SYMBOL_FILE_ENDING;
  }

}
