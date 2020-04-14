/* (c) https://github.com/MontiCore/monticore */

package ${package}.mydsl._symboltable;

/**
 * create language class with TOP mechanism to set language Name and file Ending
 */
public class MyDSLLanguage extends MyDSLLanguageTOP {

  public static final String FILE_ENDING = "mydsl";

  public MyDSLLanguage() {
    super("MyDSL", FILE_ENDING);
  }

  /**
   * create new MYDSLModelLoader with SymTabMill
   * set ModelingLanguage to this
   */
  @Override
  protected MyDSLModelLoader provideModelLoader() {
    return MyDSLSymTabMill.myDSLModelLoaderBuilder()
        .setModelingLanguage(this)
        .build();
  }
}