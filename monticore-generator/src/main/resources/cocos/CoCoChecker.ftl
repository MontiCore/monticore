<#-- (c)  https://github.com/MontiCore/monticore -->

${tc.signature("astType", "astPackage", "checkerType", "visitorPackage", "cd", "allCds")}
<#assign genHelper = glex.getGlobalVar("coCoHelper")>

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

package ${genHelper.getCoCoPackage()};

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.LinkedHashSet;

import ${astPackage}.${genHelper.getASTNodeBaseType()};
import ${visitorPackage}.${genHelper.getVisitorType()};
import ${visitorPackage}.${genHelper.getInheritanceVisitorType()};

/**
 * This class is capable of checking context conditions of the language and all
 * their super languages. Beside such composition of single context conditions
 * ({@code addCoCo(...)}) it also allows composing of existing checkers of
 * the language itself and checkers of (transitive) super languages
 * ({@code addChecker(...)}). Composing multiple checkers of the same (super)
 * language is possible. The composed checkers are not modified. Also it is
 * supported to mix both composing existing checkers as well as single.<br/>
 * <b>Keep in mind</b> that context conditions are executed multiple-times if
 * added multiple times. This might be tricky when multiple composed checkers
 * hold the same context condition.<br/>
 * <br/>
 * <b>Add context conditions</b> using {@code #addCoCo(...}.<br/>
 * <br/>
 * <b>Add checkers</b> using {@code #addCoCo(...}.<br/>
 * <br/>
 * <b>Execute all</b> registered context conditions and checkers by calling
 * {@link #checkAll(${genHelper.getASTNodeBaseType()})}.
 * 
 * @author Robert Heim
 */
public class ${checkerType} implements ${genHelper.getInheritanceVisitorType()} {

  public ${checkerType}() {
  }

  // --- realThis-Pattern ---
  
  private ${genHelper.getVisitorType()} realThis = this;

  @Override
  public ${genHelper.getVisitorType()} getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(${genHelper.getVisitorType()} realThis) {
    this.realThis = realThis;
  }
  
  // --- /realThis-Pattern ---

  <#-- checkAll for each base node of involved languages (current + recursive super languages) -->
  <#list genHelper.getASTNodeBaseTypes() as baseNodeType>
    /**
     * Executes all registered context conditions and checkers on the given ast.
     * @param node the node to check the context conditions on.
     */
    public void checkAll(${baseNodeType} node) {
      // start the double-dispatch visitor
      // checks are performed in the visit methods.
      node.accept(getRealThis());
    }
  </#list>

  <#list allCds as currentCd>
    <#assign isCurrentDiagram = genHelper.isCurrentDiagram(cd, currentCd)>
    <#assign currentCheckerType = genHelper.getQualifiedCheckerType(currentCd)>
    <#assign checker = genHelper.qualifiedJavaTypeToName(currentCheckerType)>

    <#if !isCurrentDiagram>
      private List<${currentCheckerType}> ${checker} = new ArrayList<>(Arrays.asList(new ${currentCheckerType}()));
    <#else>
      private List<${currentCheckerType}> ${checker} = new ArrayList<>();
    </#if>

    /**
     * Registers a checker whose context conditions will be executed once the
     * checker runs.<br/>
     * <br/>
     * Use this to compose checkers that already have cocos added. All
     * registered context conditions of all checkers are executed. By default
     * at least an empty checker is registered for super languages.
     * {@code ${currentCheckerType}} is used.
     * 
     * @param checker the checker to add.
     * @see #checkAll(${genHelper.getASTNodeBaseType()})
     */
    public void addChecker(${currentCheckerType} checker) {
      // note that getRealThis is not needed here.
      this.${checker}.add(checker);
    }

    <#list currentCd.getTypes() as type>
      <#if type.isClass() || type.isInterface() >
        <#assign astType = genHelper.getJavaASTName(type)>
        <#assign cocoType = genHelper.getQualifiedCoCoType(currentCd, type)>
        <#assign cocoCollectionName = genHelper.qualifiedJavaTypeToName(astType) + "CoCos">

        <#if isCurrentDiagram>
          private Collection<${cocoType}> ${cocoCollectionName} = new LinkedHashSet<>();
        </#if>
 
        /**
         * Adds a context condition. It is executed when running the checker.
         * 
         * @param coco the coco to add.
         * @see #checkAll(${genHelper.getASTNodeBaseType()})
         */
        public ${checkerType} addCoCo(${cocoType} coco) {
          <#if isCurrentDiagram>
            ${cocoCollectionName}.add(coco);
          <#else>
            /* add it to the corresponding language's checker.
             * The findFirst is always present because we add at least
             * one checker during initialization. This checker is used, so we
             * do not modify composed checkers.
             */
            ${checker}.stream().findFirst().get().addCoCo(coco);
          </#if>
          return this;
        }

        @Override
        public void visit(${astType} node) {
          <#if isCurrentDiagram>
            // execute all registered cocos of this checker
            for (${cocoType} coco : ${cocoCollectionName}) {
              coco.check(node);
            }
            // and delegate to all registered checkers of the same language as well
            ${checker}.stream().forEach(c -> c.visit(node));
          <#else>
            // delegate to all registered checkers of the corresponding super language
            ${checker}.stream().forEach(c -> c.visit(node));
          </#if>
        }
      </#if>
    </#list>
  </#list>
}
