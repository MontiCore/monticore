<#-- (c)  https://github.com/MontiCore/monticore -->
${tc.signature("isTop", "className", "plainName", "symbolsAndScopes", "superMills", "superSymbols", "symbolToMill")}
<#assign genHelper = glex.getGlobalVar("stHelper")>

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
  }

<#list symbolsAndScopes?keys as s>
  protected static ${plainName} mill${genHelper.getJavaConformName(symbolsAndScopes[s])} = null;
</#list>


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
  <#list superMills as m>
    ${m}.reset();
  </#list>
}

}
