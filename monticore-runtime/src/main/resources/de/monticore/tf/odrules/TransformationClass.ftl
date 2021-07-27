<#-- (c) https://github.com/MontiCore/monticore -->
@SuppressWarnings("unused")
public class ${ast.getClassname()} extends ODRule {

  ${tc.include("de.monticore.tf.odrules.ClassMatch")}

  ${tc.include("de.monticore.tf.odrules.ClassMatchList")}

  ${tc.include("de.monticore.tf.odrules.ConstantCode")}

  ${tc.include("de.monticore.tf.odrules.ParameterSetters")}

  ${tc.include("de.monticore.tf.odrules.HostGraphElementGetters")}

  ${tc.include("de.monticore.tf.odrules.Constructor")}

  ${tc.include("de.monticore.tf.odrules.DoReplacement")}

  ${tc.include("de.monticore.tf.odrules.UndoReplacement")}

  ${tc.include("de.monticore.tf.odrules.DoPatternMatching")}

  ${tc.include("de.monticore.tf.odrules.DoPatternMatchingOptional")}

  ${tc.include("de.monticore.tf.odrules.DoPatternMatchingList")}

  ${tc.include("de.monticore.tf.odrules.CheckConstraints")}

  ${tc.include("de.monticore.tf.odrules.CheckSubConstraints")}

  ${tc.include("de.monticore.tf.odrules.MatchStates")}

  ${tc.include("de.monticore.tf.odrules.CheckConditions")}

  ${tc.include("de.monticore.tf.odrules.FindSearchPlan")}

  ${tc.include("de.monticore.tf.odrules.SplitSearchplan")}

  ${tc.include("de.monticore.tf.odrules.FindCandidates")}

  ${tc.include("de.monticore.tf.odrules.FindActualCandidates")}

}
