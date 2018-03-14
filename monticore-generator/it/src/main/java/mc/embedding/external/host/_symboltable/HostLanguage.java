/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.external.host._symboltable;

import de.monticore.antlr4.MCConcreteParser;

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


  /**
   * @see de.monticore.ModelingLanguage#getParser()
   */
  @Override
  public MCConcreteParser getParser() {

    return null;
  }

  @Override
  protected void initResolvingFilters() {
    addResolvingFilter(new ContentResolvingFilter());
    addResolvingFilter(new HostResolvingFilter());
  }
}
