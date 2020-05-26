/* (c) https://github.com/MontiCore/monticore */

package automata5._symboltable;

@Deprecated //will be removed soon
public class Automata5Language extends Automata5LanguageTOP {

  public Automata5Language(){
    super("Automata5","aut");
  }

  @Override protected Automata5ModelLoader provideModelLoader() {
    return new Automata5ModelLoader(this);
  }
}
