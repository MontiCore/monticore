<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className","package")}
package ${package}.${grammarNameLower}tr._cocos;

public class ${className} {

  protected static TransCoCos transCoCos;

  protected static TransCoCos getTransCoCos() {
    if (transCoCos == null) {
      setTransCoCos(new TransCoCos());
    }
    return TransCoCos.transCoCos;
  }

  public static void setTransCoCos(TransCoCos transCoCos){
    TransCoCos.transCoCos = transCoCos;
  }

  public static ${ast.getName()}TRCoCoChecker getCheckerForAllCoCos() {
    ${ast.getName()}TRCoCoChecker checker = getCheckerForLanguageCoCos();
    getTransCoCos().addGenericCocos(checker);

    // Add CoCos from all super grammars
    <#list symbolTable.getAllSuperGrammars() as superG>
        <#if superG.getFullName() != "de.monticore.MCBasics" >
          // Also add cocos from super (trans) grammar ${superG.getFullName()}
          checker.addChecker(
            ${superG.getPackageName()}.tr.${superG.getName()?lower_case}tr._cocos.TransCoCos.getCheckerForLanguageCoCos()
          );
        </#if>
    </#list>

    return checker;
  }

  public static ${ast.getName()}TRCoCoChecker getCheckerForLanguageCoCos() {
    ${ast.getName()}TRCoCoChecker checker = new ${ast.getName()}TRCoCoChecker();
    getTransCoCos().addCocos(checker);

    return checker;
  }

  // Language specific cocos
  protected void addCocos(${ast.getName()}TRCoCoChecker checker) {
    <#list productions as prod>
    new NoEmptyRepCoCo_${prod.getName()}().addTo(checker);
    new NoRepOnRHSCoCo_${prod.getName()}().addTo(checker);
    new NoNegElemChangeCoCo_${prod.getName()}().addTo(checker);
    new NoNegElemCoCo_${prod.getName()}().addTo(checker);
    new NoNegElemNestCoCo_${prod.getName()}().addTo(checker);
    new SchemaVarNamingCoCo_${prod.getName()}().addTo(checker);
    new ReplacementOpCoCo_${prod.getName()}().addTo(checker);
    </#list>

    new NoOptOnRHSCoCo().addTo(checker);
    new NoOptWithinNotCoCo().addTo(checker);
    new SchemaVarRepRhsCoCo().addTo(checker);
    new NoSchemaVarAnonRhsCoCo().addTo(checker);
    new DiffSchemaVarTypeCoCo().addTo(checker);
    new NoDeleteWithoutParentCoCo().addTo(checker);
  }

  // CoCos independent from this languages productions
  protected void addGenericCocos(${ast.getName()}TRCoCoChecker checker) {
    new AssignedOnlyOnceCoCo().addTo(checker);

  }
}
