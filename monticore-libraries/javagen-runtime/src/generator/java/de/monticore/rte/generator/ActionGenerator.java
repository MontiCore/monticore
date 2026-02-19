/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rte.generator;

import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDElement;
import de.monticore.cdbasis._ast.ASTCDExtendUsage;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.typeparameters._ast.ASTTypeParameter;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ActionGenerator extends InterfaceRteGenerator {

  public ActionGenerator(File outputDirectory) {
    super(outputDirectory);
  }

  protected String getTypeName(int n) {
    return "Arg" + n;
  }

  protected String getParameterName(int n) {
    return "arg" + n;
  }

  protected String getClassName(int n) {
    return "Action" + n;
  }

  protected List<ASTTypeParameter> constructTypeParameterList(int n) {
    return IntStream.range(0, n).mapToObj(i -> CD4CodeMill.typeParameterBuilder().setName(getTypeName(i)).build()).collect(Collectors.toList());
  }

  @Override
  public ASTCDElement constructClass(int n) {
    ASTCDElement clazz = super.constructClass(n);
    setAnnotations(clazz, "@FunctionalInterface");
    return clazz;
  }

  @Override
  public ASTCDExtendUsage constructExtendUsage(int n) {
    if (n > 2)
      return null;
    ASTMCObjectType superclass;
    if (n == 0) {
      superclass = MCTypeFacade.getInstance().createQualifiedType("Runnable");
    }
    else {
      String name = n == 1 ? "Consumer" : "BiConsumer";
      superclass = CD4CodeMill.mCBasicGenericTypeBuilder().addName(name).addAllMCTypeArguments(IntStream.range(0, n).mapToObj(i -> CD4CodeMill.mCBasicTypeArgumentBuilder().setMCQualifiedType(MCTypeFacade.getInstance().createQualifiedType(getTypeName(i))).build()).collect(Collectors.toList())).build();
    }
    if (superclass == null)
      return null;
    return CD4CodeMill.cDExtendUsageBuilder().addSuperclass(superclass).build();
  }

  @Override
  protected List<ASTCDMethod> constructMethods(int n) {
    ASTCDParameter[] args = IntStream.range(0, n).mapToObj(i -> buildParameter(getParameterName(i), getTypeName(i))).toArray(ASTCDParameter[]::new);
    ASTCDMethod method = buildMethod("apply", CDModifier.PUBLIC_ABSTRACT.build(), null, args).build();
    if (n > 2)
      return List.of(method);
    ASTCDMethod acceptMethod = completeMethod(buildMethod(n == 0 ? "run" : "accept", CDModifier.PUBLIC.build(), null, args).setIsDefault(true), "de.monticore.rte.actions.ActionApply", n);
    return List.of(method, acceptMethod);
  }

  @Override
  protected List<String> buildPackageName() {
    return List.of("de", "monticore", "rte", "actions");
  }
}
