${tc.signature("states","className")}
public <#if className?ends_with("TOP")>abstract </#if>class ${className?cap_first} {

    ${tc.include("FactoryStateMethod.ftl",states)}

}