/* (c) Monticore license: https://github.com/MontiCore/monticore */
package sm2._symboltable;

public class SM2Language extends SM2LanguageTOP {
  public static final String FILE_ENDING = "aut";
  
  public SM2Language() {
    super("SM2 Language", FILE_ENDING);

    setModelNameCalculator(new SM2ModelNameCalculator());
  }
  
  @Override
  protected SM2ModelLoader provideModelLoader() {
  return new SM2ModelLoader(this);
 }
}
