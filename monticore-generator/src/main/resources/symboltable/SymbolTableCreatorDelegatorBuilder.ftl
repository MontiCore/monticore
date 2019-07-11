<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className","STCDelName")}

<#assign IGlobalScopeName = "I"+(ast.getName()?cap_first)+"GlobalScope">
<#assign genHelper = glex.getGlobalVar("stHelper")>

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

package ${genHelper.getTargetPackage()};

public class ${className}{


    protected ${IGlobalScopeName} globalScope;


    protected ${className}(){}

    public ${STCDelName} build(){
        ${STCDelName} obj = new ${STCDelName}(globalScope);
        return obj;
    }

    public ${className} setGlobalScope(${IGlobalScopeName} globalScope){
        this.globalScope = globalScope;
        return this;
    }

    public ${IGlobalScopeName} getGlobalScope(){
        return this.globalScope;
    }
}

