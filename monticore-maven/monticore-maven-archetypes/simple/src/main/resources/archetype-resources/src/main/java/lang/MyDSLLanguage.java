/* (c) https://github.com/MontiCore/monticore */

package ${package}.lang;

import java.util.Optional;

import javax.annotation.Nullable;

import de.monticore.CommonModelingLanguage;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import ${package}.mydsl._parser.MyDSLParser;
import ${package}.symboltable.MyDSLModelLoader;
import ${package}.symboltable.MyDSLSymbolTableCreator;
import ${package}.symboltable.MyElementSymbol;
import ${package}.symboltable.MyFieldSymbol;
import ${package}.symboltable.MyModelSymbol;

public class MyDSLLanguage extends CommonModelingLanguage {
  
  public static final String FILE_ENDING = "mydsl";
  
  public MyDSLLanguage() {
    super("MyDSL", FILE_ENDING);
    
    addResolver(CommonResolvingFilter.create(MyModelSymbol.class, MyModelSymbol.KIND));
    addResolver(CommonResolvingFilter.create(MyElementSymbol.class, MyElementSymbol.KIND));
    addResolver(CommonResolvingFilter.create(MyFieldSymbol.class, MyFieldSymbol.KIND));
  }
  
  @Override
  public MyDSLParser getParser() {
    return new MyDSLParser();
  }
  
  @Override
  public Optional<MyDSLSymbolTableCreator> getSymbolTableCreator(
      ResolvingConfiguration resolvingConfiguration, @Nullable MutableScope enclosingScope) {
    return Optional
        .of(new MyDSLSymbolTableCreator(resolvingConfiguration, enclosingScope));
  }
  
  @Override
  public MyDSLModelLoader getModelLoader() {
    return (MyDSLModelLoader) super.getModelLoader();
  }
  
  @Override
  protected MyDSLModelLoader provideModelLoader() {
    return new MyDSLModelLoader(this);
  }
}
