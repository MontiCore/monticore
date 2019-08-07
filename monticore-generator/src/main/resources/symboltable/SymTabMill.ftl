<#-- (c)  https://github.com/MontiCore/monticore -->
${tc.signature("isTop", "className", "plainName", "symbolsAndScopes", "superMills", "superSymbols", "symbolToMill", "languageName", "existsHW", "symbols")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign existsSTC = genHelper.getGrammarSymbol().getStartProd().isPresent()>
<#assign existsSTCDel = !genHelper.getGrammarSymbol().isComponent() && existsSTC>
<#assign existsModelLoader = existsSTCDel>
<#assign existsLanguage = existsModelLoader && existsHW>

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTargetPackage()?lower_case};

<#--<#list astImports as astImport>-->
<#--import ${astImport};-->
<#--</#list>-->

public <#if isTop>abstract </#if> class ${className} {

protected static ${plainName} getMill() {
if (mill == null) {
mill = new ${plainName}();
}
return mill;
}

protected static ${plainName} mill = null;

public static void initMe(${plainName} a) {
mill = a;
<#list symbolsAndScopes?keys as s>
  mill${genHelper.getJavaConformName(symbolsAndScopes[s])} = a;
</#list>
<#list symbols?keys as s>
  mill${genHelper.getJavaConformName(symbols[s])}Reference = a;
</#list>
${languageName?uncap_first}ModelLoaderMill = a;
${languageName?uncap_first}LanguageMill = a;
${languageName?uncap_first}SymbolTableCreatorMill = a;
${languageName?uncap_first}SymbolTableCreatorDelegatorMill = a;
${languageName?uncap_first}GlobalScopeMill = a;
${languageName?uncap_first}ArtifactScopeMill = a;
}

<#list symbolsAndScopes?keys as s>
  protected static ${plainName} mill${genHelper.getJavaConformName(symbolsAndScopes[s])} = null;
</#list>
<#list symbols?keys as s>
  protected static ${plainName} mill${genHelper.getJavaConformName(symbols[s])}Reference = null;
</#list>
protected static ${plainName} ${languageName?uncap_first}ModelLoaderMill = null;
protected static ${plainName} ${languageName?uncap_first}LanguageMill = null;
protected static ${plainName} ${languageName?uncap_first}SymbolTableCreatorMill = null;
protected static ${plainName} ${languageName?uncap_first}SymbolTableCreatorDelegatorMill = null;
protected static ${plainName} ${languageName?uncap_first}GlobalScopeMill = null;
protected static ${plainName} ${languageName?uncap_first}ArtifactScopeMill = null;

protected ${className} () {}

<#list symbolsAndScopes?keys as s>
  public  static  ${s}Builder ${symbolsAndScopes[s]?uncap_first}Builder()   {
  if (mill${genHelper.getJavaConformName(symbolsAndScopes[s])} == null) {
  mill${genHelper.getJavaConformName(symbolsAndScopes[s])} = getMill();
  }
  return mill${genHelper.getJavaConformName(symbolsAndScopes[s])}._${symbolsAndScopes[s]?uncap_first}Builder();
  }

  protected  ${s}Builder _${symbolsAndScopes[s]?uncap_first}Builder()   {
  return new ${s}Builder();
  }
</#list>

<#list symbols?keys as s>
  public static ${s}ReferenceBuilder ${symbols[s]?uncap_first}ReferenceBuilder() {
  if(mill${genHelper.getJavaConformName(symbols[s])}Reference == null) {
  mill${genHelper.getJavaConformName(symbols[s])}Reference = getMill();
  }
  return mill${genHelper.getJavaConformName(symbols[s])}Reference._${symbols[s]?uncap_first}ReferenceBuilder();
  }

  protected ${s}ReferenceBuilder _${symbols[s]?uncap_first}ReferenceBuilder() {
  return new ${s}ReferenceBuilder();
  }
</#list>

<#list superSymbols?keys as s>
  public  static  ${s}Builder ${superSymbols[s]?uncap_first}Builder()   {
  return ${symbolToMill[s]}.${superSymbols[s]?uncap_first}Builder();
  }
</#list>

public  static  void init()   {
mill = new ${plainName}();
}

public  static  void reset()   {

mill = null;
<#list symbolsAndScopes?keys as s>
  mill${genHelper.getJavaConformName(symbolsAndScopes[s])} = null;
</#list>
<#list symbols?keys as s>
  mill${genHelper.getJavaConformName(symbols[s])}Reference = null;
</#list>
${languageName?uncap_first}ModelLoaderMill = null;
${languageName?uncap_first}LanguageMill = null;
${languageName?uncap_first}SymbolTableCreatorMill = null;
${languageName?uncap_first}SymbolTableCreatorDelegatorMill = null;
${languageName?uncap_first}GlobalScopeMill = null;
${languageName?uncap_first}ArtifactScopeMill = null;
<#list superMills as m>
  ${m}.reset();
</#list>
}
<#if existsLanguage>
  public static ${languageName}LanguageBuilder ${languageName?uncap_first}LanguageBuilder(){
  if(${languageName?uncap_first}LanguageMill == null){
  ${languageName?uncap_first}LanguageMill = getMill();
  }
  return ${languageName?uncap_first}LanguageMill._${languageName?uncap_first}LanguageBuilder();
  }

  protected ${languageName}LanguageBuilder _${languageName?uncap_first}LanguageBuilder(){
  return new ${languageName}LanguageBuilder();
  }
</#if>
<#if existsModelLoader>
  public static ${languageName}ModelLoaderBuilder ${languageName?uncap_first}ModelLoaderBuilder(){
  if(${languageName?uncap_first}ModelLoaderMill == null){
  ${languageName?uncap_first}ModelLoaderMill = getMill();
  }
  return ${languageName?uncap_first}ModelLoaderMill._${languageName?uncap_first}ModelLoaderBuilder();
  }

  protected ${languageName}ModelLoaderBuilder _${languageName?uncap_first}ModelLoaderBuilder(){
  return new ${languageName}ModelLoaderBuilder();
  }
</#if>
<#if existsSTCDel>
  public static ${languageName}SymbolTableCreatorDelegatorBuilder ${languageName?uncap_first}SymbolTableCreatorDelegatorBuilder(){
  if(${languageName?uncap_first}SymbolTableCreatorDelegatorMill == null){
  ${languageName?uncap_first}SymbolTableCreatorDelegatorMill = getMill();
  }
  return ${languageName?uncap_first}SymbolTableCreatorDelegatorMill._${languageName?uncap_first}SymbolTableCreatorDelegatorBuilder();
  }

  protected ${languageName}SymbolTableCreatorDelegatorBuilder _${languageName?uncap_first}SymbolTableCreatorDelegatorBuilder(){
  return new ${languageName}SymbolTableCreatorDelegatorBuilder();
  }
</#if>
<#if existsSTC>
  public static ${languageName}SymbolTableCreatorBuilder ${languageName?uncap_first}SymbolTableCreatorBuilder(){
  if(${languageName?uncap_first}SymbolTableCreatorMill == null){
  ${languageName?uncap_first}SymbolTableCreatorMill = getMill();
  }
  return ${languageName?uncap_first}SymbolTableCreatorMill._${languageName?uncap_first}SymbolTableCreatorBuilder();
  }

  protected ${languageName}SymbolTableCreatorBuilder _${languageName?uncap_first}SymbolTableCreatorBuilder(){
  return new ${languageName}SymbolTableCreatorBuilder();
  }

  public static ${languageName}GlobalScopeBuilder ${languageName?uncap_first}GlobalScopeBuilder(){
  if(${languageName?uncap_first}GlobalScopeMill == null){
  ${languageName?uncap_first}GlobalScopeMill = getMill();
  }
  return ${languageName?uncap_first}GlobalScopeMill._${languageName?uncap_first}GlobalScopeBuilder();
  }

  protected ${languageName}GlobalScopeBuilder _${languageName?uncap_first}GlobalScopeBuilder(){
  return new ${languageName}GlobalScopeBuilder();
  }

  public static ${languageName}ArtifactScopeBuilder ${languageName?uncap_first}ArtifactScopeBuilder(){
  if(${languageName?uncap_first}ArtifactScopeMill == null){
  ${languageName?uncap_first}ArtifactScopeMill = getMill();
  }
  return ${languageName?uncap_first}ArtifactScopeMill._${languageName?uncap_first}ArtifactScopeBuilder();
  }

  protected ${languageName}ArtifactScopeBuilder _${languageName?uncap_first}ArtifactScopeBuilder(){
  return new ${languageName}ArtifactScopeBuilder();
  }
</#if>
}
