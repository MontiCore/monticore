<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "STCName")}
<#assign IScopeName = "I"+(ast.getName()?cap_first)+"Scope">
<#assign genHelper = glex.getGlobalVar("stHelper")>

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

package ${genHelper.getTargetPackage()};

import java.util.Deque;
import java.util.ArrayDeque;

public class ${className}{

    protected Deque<${IScopeName}> scopeStack = new ArrayDeque<>();

    protected ${className}(){}

    public ${STCName} build(){
        ${STCName} obj = new ${STCName}(scopeStack);
        return obj;
    }

    public ${className} setScopeStack(Deque<${IScopeName}> scopeStack){
        this.scopeStack = scopeStack;
        return this;
    }

    public ${className} addToScopeStack(${IScopeName} scope){
        this.scopeStack.add(scope);
        return this;
    }

    public ${className} removeFromScopeStack(${IScopeName} scope){
        this.scopeStack.remove(scope);
        return this;
    }

    public Deque<${IScopeName}> getScopeStack(){
        return this.scopeStack;
    }
}