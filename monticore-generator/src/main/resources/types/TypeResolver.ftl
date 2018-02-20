<#-- (c) https://github.com/MontiCore/monticore -->


${tc.signature("astType", "astPackage", "cd")}

<#assign genVisitorHelper = glex.getGlobalVar("visitorHelper")>
<#assign genHelper = glex.getGlobalVar("typeResolverHelper")>
<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTypeResolverPackage()};

import de.monticore.ast.ASTNode;
import ${genVisitorHelper.getVisitorPackage()}.${genVisitorHelper.getVisitorType()};

/**
* Type system is implemented in two steps. First step is to resolve type for AST nodes
* and second step is to check type rules in CoCos by using resolved types.
* To implement type resolving for AST nodes this abstract class must be extended by a handwritten class.
* This abstract class extends de.monticore.types.TypeResolver<T>  and implements Visitor
    * All visit, endVisit, traverse methods of Visitor interface
    * is overwritten as final with empty body to prevent user from using those methods.
    * Only handle methods for each AST node should be overwritten in
    * the handwritten class to provide type resolving.
    */

    abstract class ${genHelper.getTypeResolver()}<T> extends de.monticore.types.TypeResolver<T> implements ${genVisitorHelper.getVisitorType()}{

        @Override
        public final void visit(ASTNode node) {}

        @Override
        public final void endVisit(ASTNode node) {}

    <#list cd.getTypes() as type>
        <#if type.isClass() || type.isInterface() >
            <#assign astName = genHelper.getJavaASTName(type)>
            @Override
            public final void visit(${astName} node) {}

            @Override
            public final void endVisit(${astName} node) {}

            @Override
            public void handle(${astName} node) {}
        </#if>
        <#if type.isClass() && !type.isAbstract()>

            @Override
            public final void traverse(${astName} node) {}

        </#if>
    </#list>
        }
