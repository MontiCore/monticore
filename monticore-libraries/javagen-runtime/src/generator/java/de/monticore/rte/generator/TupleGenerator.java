/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rte.generator;

import de.monticore.cd.facade.CDModifier;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.types.typeparameters._ast.ASTTypeParameter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TupleGenerator extends RteGenerator {

  public TupleGenerator(File outputDirectory) {
    super(outputDirectory);
  }

  protected String getTypeName(int n) {
    return "T" + n;
  }

  protected String getParameterName(int n) {
    return "e" + n;
  }

  protected String getClassName(int n) {
    return "Tuple" + n;
  }

  protected String getGetterName(int n) {
    return "get" + n;
  }

  protected String getClassNameWithArguments(int n) {
    StringBuilder typeAsString = new StringBuilder();
    typeAsString.append(getClassName(n));
    typeAsString.append("<");
    for (int i = 0; i < n; i++) {
      typeAsString.append(getTypeName(i));
      if (i != n - 1) {
        typeAsString.append(", ");
      }
    }
    typeAsString.append(">");
    return typeAsString.toString();
  }

  @Override
  protected List<ASTCDMethod> constructMethods(int n) {
    List<ASTCDMethod> methods = new ArrayList<>(n);
    for (int i = 0; i < n; i++) {
      methods.add(completeMethod(buildMethod(getGetterName(i), CDModifier.PUBLIC.build(), getTypeName(i)), "de.monticore.rte.tuples.Getter", i));
    }
    // To String
    ASTCDMethod toString = completeMethod(buildMethod("toString", CDModifier.PUBLIC.build(), "String"), "de.monticore.rte.tuples.ToString", n);
    setOverride(toString);
    methods.add(toString);
    // Equals
    ASTCDMethod equals = completeMethod(buildMethod("equals", CDModifier.PUBLIC.build(), "boolean", buildParameter("object", "Object")), "de.monticore.rte.tuples.Equals", n);
    setOverride(equals);
    methods.add(equals);
    // HashCode
    ASTCDMethod hashCode = completeMethod(buildMethod("hashCode", CDModifier.PUBLIC.build(), "int"), "de.monticore.rte.tuples.HashCode", n);
    setOverride(hashCode);
    methods.add(hashCode);
    // Of
    methods.add(
        completeMethod(
            buildMethod(
                "of",
                CDModifier.PUBLIC_STATIC.build(),
                getClassNameWithArguments(n),
                IntStream.range(0, n)
                    .mapToObj(i -> buildParameter(getParameterName(i), getTypeName(i)))
                    .toArray(ASTCDParameter[]::new)
            ).setTypeParameters(constructTypeParameters(n)),
            "de.monticore.rte.tuples.Of",
            n
        )
    );
    return methods;
  }

  @Override
  protected List<ASTCDAttribute> constructAttributes(int n) {
    return IntStream.range(0, n).mapToObj(i -> buildAttribute(getParameterName(i), CDModifier.PROTECTED_FINAL.build(), getTypeName(i))).collect(Collectors.toList());
  }

  protected List<ASTTypeParameter> constructTypeParameterList(int n) {
    return IntStream.range(0, n).mapToObj(i -> CD4CodeMill.typeParameterBuilder().setName(getTypeName(i)).build()).collect(Collectors.toList());
  }

  @Override
  protected ASTCDConstructor constructConstructor(ASTCDConstructor constructor, int n) {
    for (int i = 0; i < n; i++) {
      constructor.addCDParameter(buildParameter(getParameterName(i), getTypeName(i)));
    }
    setBody(constructor, "de.monticore.rte.tuples.Constructor", n);
    return constructor;
  }

  @Override
  protected int getMinSize() {
    return 2;
  }

  @Override
  protected List<String> buildPackageName() {
    return List.of("de", "monticore", "rte", "tuples");
  }

}
