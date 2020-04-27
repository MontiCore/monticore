/* (c) https://github.com/MontiCore/monticore */

package basicjava._symboltable;

@Deprecated //will be removed soon
public class BasicJavaLanguage extends BasicJavaLanguageTOP {

  public BasicJavaLanguage(){
    super("BasicJava","javamodel");
  }

  @Override protected BasicJavaModelLoader provideModelLoader() {
    return new BasicJavaModelLoader(this);
  }
}
