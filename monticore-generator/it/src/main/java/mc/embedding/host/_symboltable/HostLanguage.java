/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.host._symboltable;

public class HostLanguage extends HostLanguageTOP {

  public static final String FILE_ENDING = "host";

  public HostLanguage() {
    super("Host Language", FILE_ENDING);

    setModelNameCalculator(new HostModelNameCalculator());
  }


  @Override
  protected HostModelLoader provideModelLoader() {
    return new HostModelLoader(this);
  }


}
