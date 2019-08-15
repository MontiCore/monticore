package mc.typescalculator.combineexpressionswithliterals._symboltable;

public class CombineExpressionsWithLiteralsLanguage extends CombineExpressionsWithLiteralsLanguageTOP {

  public CombineExpressionsWithLiteralsLanguage(){
    super("CombineExpressionsWithLiterals","cex");
  }

  @Override
  protected CombineExpressionsWithLiteralsModelLoader provideModelLoader() {
    return CombineExpressionsWithLiteralsSymTabMill.combineExpressionsWithLiteralsModelLoaderBuilder().setModelingLanguage(this).build();
  }

}
