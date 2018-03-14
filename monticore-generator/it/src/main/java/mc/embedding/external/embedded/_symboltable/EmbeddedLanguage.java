/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.external.embedded._symboltable;

public class EmbeddedLanguage extends EmbeddedLanguageTOP {
  public static final String FILE_ENDING = "embedded";

  public EmbeddedLanguage() {
    super("Embedded Language", FILE_ENDING);

    setModelNameCalculator(new EmbeddedModelNameCalculator());
  }


  @Override
  protected EmbeddedModelLoader provideModelLoader() {
    return new EmbeddedModelLoader(this);
  }



}
