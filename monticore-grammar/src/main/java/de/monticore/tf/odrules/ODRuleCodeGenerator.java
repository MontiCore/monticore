/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf.odrules;

import com.google.common.collect.Lists;
import de.monticore.expressions.commonexpressions._ast.ASTBooleanAndOpExpression;
import de.monticore.expressions.commonexpressions._ast.ASTBracketExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcarraystatements._ast.ASTArrayInit;
import de.monticore.statements.mccommonstatements._ast.ASTMCJavaBlock;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableInit;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;
import de.monticore.tf.odrulegeneration.ODRuleGenerationMill;
import de.monticore.tf.odrulegeneration._ast.*;
import de.monticore.tf.odrules._ast.*;
import de.monticore.tf.odrules._visitor.ODRulesTraverser;
import de.monticore.tf.odrules.subConstraints.*;
import de.monticore.tf.odrules.util.ODRuleStereotypes;
import de.monticore.tf.odrules.util.TFExpressionFullPrettyPrinter;
import de.monticore.tf.odrules.util.Util;
import de.monticore.tf.rule2od.Variable2AttributeMap;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class ODRuleCodeGenerator {
  // contains all the data for the code generator
  protected ASTTransformationStructure root;
  protected String filename;
  protected ASTODDefinition lhs;
  protected ASTODDefinition rhs;
  protected Variable2AttributeMap var2attr = new Variable2AttributeMap();
  protected HierarchyHelper hierarchyHelper;
  protected List<ASTMatchingObject> lhsObjects;
  //Same assignments as in the parsedModel, but without "m." prefixes
  protected List<ASTAssignment> modifiedAssignments;


  public static void generate(ASTODRule parsedModel, File targetDir) {
    String packageName = "de.monticore.tf";
    if (!parsedModel.getPackageList().isEmpty()) {
      packageName = Names.constructQualifiedName(parsedModel.getPackageList());
    }
    generate(parsedModel, targetDir, packageName);
  }

  public static void generate(ASTODRule parsedModel, File targetDir, String packageName) {
    generate(parsedModel, new GlobalExtensionManagement(), targetDir, parsedModel.getName(), packageName);
  }

  public static void generate(ASTODRule parsedModel, GlobalExtensionManagement glex, File targetDir, String fileName) {
    generate(parsedModel, glex, targetDir, fileName, "");
  }

  public static void generate(ASTODRule parsedModel, GlobalExtensionManagement glex, File targetDir, String fileName, String packageName) {
    List<ODSubConstraint> subConstraints = new ArrayList<>();
    ODRuleCodeGenerator odRuleCodeGenerator = initOdRuleCodeGenerator(parsedModel, fileName);

    if (parsedModel.isPresentConstraint()) {
      ASTExpression constraint = parsedModel.getConstraint();
      subConstraints = odRuleCodeGenerator.findSubConstraints(constraint);
    }
    glex.setGlobalValue("subConstraints", subConstraints);
    glex.setGlobalValue("dependVars", getAllDependVars(subConstraints));
    odRuleCodeGenerator.hierarchyHelper.setPackageName(packageName);

    for (ASTMCImportStatement d : parsedModel.getMCImportStatementList()) {
      odRuleCodeGenerator.hierarchyHelper.addCustomImports(Util.printImportDeclaration(d));
    }
    // Import the mill
    List<String> millImportList = new ArrayList<>(parsedModel.getGrammarPackageList());
    millImportList.add(parsedModel.getGrammarName().toLowerCase());
    millImportList.add(parsedModel.getGrammarName() + "Mill");
    String p = Joiners.DOT.join(millImportList);
    if(p.startsWith(".")){
      p = p.substring(1);
    }
    odRuleCodeGenerator.hierarchyHelper.addCustomImports("import " + p + ";");
    glex.setGlobalValue("hierarchyHelper", odRuleCodeGenerator.hierarchyHelper);
    glex.setGlobalValue("grammarName", parsedModel.getGrammarName());


    final GeneratorSetup setup = new GeneratorSetup();
    setup.setOutputDirectory(targetDir);
    setup.setGlex(glex);

    final GeneratorEngine generator = new GeneratorEngine(setup);
    ASTTransformationStructure tfStructure = odRuleCodeGenerator.generateASTTransformationStructure(parsedModel);

    final Path filePath = Paths.get(Names.getPathFromPackage(packageName), fileName + ".java");
    generator.generate("de.monticore.tf.odrules.TransformationUnit", filePath, tfStructure);
  }

  protected static ODRuleCodeGenerator initOdRuleCodeGenerator(ASTODRule parsedModel, String filename) {
    final ODRuleCodeGenerator odRuleCodeGenerator = new ODRuleCodeGenerator();

    odRuleCodeGenerator.filename = filename;
    odRuleCodeGenerator.lhs = parsedModel.getLhs();
    odRuleCodeGenerator.rhs = parsedModel.isPresentRhs() ? parsedModel.getRhs() : ODRulesMill.oDDefinitionBuilder().uncheckedBuild();
    odRuleCodeGenerator.lhsObjects = odRuleCodeGenerator.generateLHSObjectList(parsedModel);
    odRuleCodeGenerator.hierarchyHelper = new HierarchyHelper(parsedModel);
    if (parsedModel.getVariables() != null) {
      odRuleCodeGenerator.var2attr = parsedModel.getVariables();
    }
    odRuleCodeGenerator.modifiedAssignments = odRuleCodeGenerator.fixAssignments(parsedModel);

    return odRuleCodeGenerator;
  }

  public ASTTransformationStructure generateASTTransformationStructure(ASTODRule ast) {
    hierarchyHelper = new HierarchyHelper(ast);
    ASTTransformationStructureBuilder tsBuilder = ODRuleGenerationMill.transformationStructureBuilder();
    ASTPatternBuilder patternBuilder = ODRuleGenerationMill.patternBuilder();

    if (!ast.getPackageList().isEmpty()) {
      tsBuilder.setPackage(Names.getQualifiedName(ast.getPackageList()));
    } else {
      tsBuilder.setPackage("de.monticore.tf");
    }
    tsBuilder.setClassname(getClassName());

    patternBuilder.setMatchingObjectsList(generateObjectList(ast));
    patternBuilder.setObjectConditionsList(generateObjectConditions(ast));
    patternBuilder.setLinkConditionsList(generateLinkConditions(ast));
    patternBuilder.setAssocList(generateAssociations(ast));
    patternBuilder.setTypesList(generateTypesList(ast));
    patternBuilder.setLHSObjectsList(lhsObjects);
    tsBuilder.setPattern(patternBuilder.build());
    List<ASTVariable> vars = generateVariables(ast);
    tsBuilder.setConstraintExpression(generateConstraintExpression(ast));
    tsBuilder.setDoStatement(generateDoStatement(ast));
    tsBuilder.setUndoStatement(generateUndoStatement(ast));
    tsBuilder.setAssignmentsList(generateAssignments());

    tsBuilder.setVariablesList(vars);
    tsBuilder.setReplacement(generateReplacement(new DifferenceFinder(hierarchyHelper).getDifference(ast)));
    tsBuilder.setFoldingHash(generateFoldingHash(ast));
    return tsBuilder.build();
  }

  protected String generateDoStatement(ASTODRule ast) {
    if (ast.isPresentDoBlock()) {
      ASTMCJavaBlock doblock = ast.getDoBlock();
      if (doblock == null) {
        return "";
      }
      return Util.print(doblock, lhsObjects, this.hierarchyHelper);
    } else {
      return "";
    }
  }

  protected String generateUndoStatement(ASTODRule ast) {
    if (ast.isPresentUndoBlock()) {
      ASTMCJavaBlock undoblock = ast.getDoBlock();
      if (undoblock == null) {
        return "";
      }
      String undoStatement = Util.print(undoblock, lhsObjects, this.hierarchyHelper);
      if(!ast.isPresentDoBlock())
        undoStatement += "\n Log.warn(\"UndoBlock was present and undoReplacement was executed, but there was no DoBlock\");";
      return undoStatement;
    } else if(ast.isPresentDoBlock()) {
      return "Log.warn(\"DoBlock was present and undoReplacement was executed, but there is no UndoBlock\");";
    } else {
      return "";
    }
  }

  protected List<String> generateAssignments() {

    List<String> result = new ArrayList<>();

    for (ASTAssignment exp : modifiedAssignments) {
      ODRulesTraverser traverser = ODRulesMill.inheritanceTraverser();

      FindOptionalsVisitor findOptVisitor = new FindOptionalsVisitor(lhsObjects, hierarchyHelper);
      traverser.add4ExpressionsBasis(findOptVisitor);

      exp.getRhs().accept(traverser);

      traverser = ODRulesMill.inheritanceTraverser();
      AddAffixesToAssignmentsVisitor addAffixVisitor = new AddAffixesToAssignmentsVisitor(lhsObjects, hierarchyHelper, exp.getRhs());
      traverser.add4ExpressionsBasis(addAffixVisitor);
      traverser.add4CommonExpressions(addAffixVisitor);

      exp.getRhs().accept(traverser);
      exp.setRhs(addAffixVisitor.rootExp);

      StringBuilder stringbuilder = new StringBuilder();
      IndentPrinter iPrinter = new IndentPrinter(stringbuilder);
      TFExpressionFullPrettyPrinter p = new TFExpressionFullPrettyPrinter(iPrinter);

      p.prettyprint(exp.getRhs());
      iPrinter.flushBuffer();


      /**
       * This code inserts isPresent()-checks into assignments that contain optional variables.
       *
       * Let $O be an optional variable in this assignment:  $name = $O.getName().
       * In case that no value is present for $O, the evaluation of this assignment would produce a NoSuchElementException.
       * In order to prevent this we add an isPresent()-check to the expression like this: $O.isPresent ? $O.getName() : "undef"
       */
      if (!findOptVisitor.optVars.isEmpty()) {

        stringbuilder.insert(0, "()?");
        stringbuilder.insert(stringbuilder.length(), ":\"undef\"");
        Iterator<ASTMatchingObject> optVarsIterator = findOptVisitor.optVars.iterator();

        while (optVarsIterator.hasNext()) {
          ASTMatchingObject o = optVarsIterator.next();
          String condition = "m." + o.getObjectName() + ".isPresent()";
          if (optVarsIterator.hasNext()) {
            condition = "&&" + condition;
          }
          stringbuilder.insert(1, condition);
        }
      }

      result.add(exp.getLhs() + " =" + stringbuilder.toString() + ";");
    }
    return result;

  }

  /**
   * Iterates over all conjugated boolean expressions and calculates ODSbuConstraints for each.
   *
   * @return the set of all ODSubConstraints calculated in this process..
   */
  protected List<ODSubConstraint> findSubConstraints(ASTExpression constrExpr) {
    List<ODSubConstraint> subConstraints = new ArrayList<>();

    if (constrExpr instanceof ASTBooleanAndOpExpression) {
      ASTBooleanAndOpExpression booleanAndOp = (ASTBooleanAndOpExpression) constrExpr;
      subConstraints.addAll(findSubConstraints(booleanAndOp.getLeft()));
      subConstraints.addAll(findSubConstraints(booleanAndOp.getRight()));
    } else if (constrExpr instanceof ASTBracketExpression) {
      ASTExpression subExpr = ((ASTBracketExpression)constrExpr).getExpression();
      subConstraints.addAll(findSubConstraints(subExpr));
    } else {
      subConstraints.add(calculateSubConstraint(constrExpr));
    }

    return subConstraints;
  }

  /**
   * Transforms a given ASTExpression into a ODSubConstraint
   *
   * @return the resulting ODSubConstraint
   */
  protected ODSubConstraint calculateSubConstraint(ASTExpression constrExpr) {
    ODRulesTraverser traverser = ODRulesMill
            .inheritanceTraverser();

    //Create copy of assignments to avoid side effects
    List<ASTAssignment> modifiedAssignmentsCopy = this.modifiedAssignments
            .stream()
            .map(ASTAssignment::deepClone)
            .collect(Collectors.toList());

    ReplaceIdentifierVisitor replaceIdentVisitor = new ReplaceIdentifierVisitor(modifiedAssignmentsCopy);
    traverser.add4CommonExpressions(replaceIdentVisitor);
    traverser.add4ExpressionsBasis(replaceIdentVisitor);
    constrExpr.accept(traverser);

    traverser = ODRulesMill.inheritanceTraverser();
    FindDependVarsVisitor findDependVarsVisitor = new FindDependVarsVisitor(lhsObjects);
    traverser.add4ExpressionsBasis(findDependVarsVisitor);
    constrExpr.accept(traverser);

    traverser = ODRulesMill.inheritanceTraverser();
    InsertIsPresentChecksVisitor isPresentCheckVisitor =
            new InsertIsPresentChecksVisitor(lhsObjects, hierarchyHelper, constrExpr);
    traverser.add4CommonExpressions(isPresentCheckVisitor);
    constrExpr.accept(traverser);
    constrExpr = isPresentCheckVisitor.subConstraint;

    traverser = ODRulesMill.inheritanceTraverser();
    AddSuffixToOptionalsVisitor suffixVisitor = new AddSuffixToOptionalsVisitor(lhsObjects, hierarchyHelper);
    traverser.add4CommonExpressions(suffixVisitor);
    traverser.add4ExpressionsBasis(suffixVisitor);
    constrExpr.accept(traverser);

    StringBuilder exprSB = new StringBuilder();
    IndentPrinter iPrinter = new IndentPrinter(exprSB);
    TFExpressionFullPrettyPrinter p = new TFExpressionFullPrettyPrinter(iPrinter);
    p.prettyprint(constrExpr);

    ODSubConstraint subConstraint = new ODSubConstraint();
    subConstraint.dependVars = findDependVarsVisitor.dependVars;
    subConstraint.constrExpr = exprSB.toString();
    subConstraint.optionalInOrPresent = isPresentCheckVisitor.optionalInOrPresent;

    return subConstraint;
  }

  /**
   * Deletes "m." prefix from assignments if present.
   */
  protected List<ASTAssignment> fixAssignments(ASTODRule parsedModel) {
    List<ASTAssignment> modifiedAssignments = new ArrayList<>();
    for (ASTAssignment assignment : parsedModel.getAssignmentList()) {
      ASTAssignment clone = assignment.deepClone();
      ODRulesTraverser deleteMatchTraverser = ODRulesMill.inheritanceTraverser();
      deleteMatchTraverser.add4CommonExpressions(new DeleteMatchAccessVisitor());
      clone.getRhs().accept(deleteMatchTraverser);
      modifiedAssignments.add(clone);
      if (isObjectVariable(assignment.getLhs())) {
        clone.setLhs("m." + assignment.getLhs());
      }
    }

    return modifiedAssignments;
  }

  protected boolean isObjectVariable(String var) {
    for (ASTODObject o : rhs.getODObjectList()) {
      if (o.getName().equals(var)) {
        return true;
      }
    }
    for (ASTODObject o : lhs.getODObjectList()) {
      if (o.getName().equals(var)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Calculates a set of all variables that are used inside of constraints
   */
  protected static Map<String, Integer> getAllDependVars(List<ODSubConstraint> subConstraints) {

    Map<String, Integer> allDependVars = new HashMap<>();
    for (ODSubConstraint subConstraint : subConstraints) {
      for (ASTMatchingObject object : subConstraint.dependVars) {
        String objectName = object.getObjectName();
        if (allDependVars.containsKey(objectName)) {
          allDependVars.put(objectName, allDependVars.get(objectName) + 1);
        } else {
          allDependVars.put(objectName, 1);
        }
      }
    }
    return allDependVars;
  }


  protected String getClassName() {
    // compute the classname from the filename by taking the filename without
    // extension and leading directories
    String cnHelper = filename;
    if (cnHelper.contains(".")) {
      cnHelper = cnHelper.substring(0, cnHelper.lastIndexOf('.'));
    }
    if (cnHelper.contains(File.separator)) {
      cnHelper = cnHelper.substring(cnHelper.lastIndexOf(File.separatorChar) + 1);
    }
    // consider forward slash separately, because it may also occur on Windows
    // systems
    if (cnHelper.contains("/")) {
      cnHelper = cnHelper.substring(cnHelper.lastIndexOf('/') + 1);
    }
    return StringTransformations.capitalize(cnHelper);
  }

  protected List<ASTMatchingObject> generateLHSObjectList(ASTODRule dslRoot) {
    List<ASTODObject> lhsObjects = Util.getAllODObjects(dslRoot.getLhs());
    List<ASTMatchingObject> objects = new ArrayList<>();
    for (ASTODObject obj : lhsObjects) {
      ASTMatchingObjectBuilder builder = ODRuleGenerationMill.matchingObjectBuilder();
      // getting the name is easy
      if (obj.isPresentName()) {
        builder.setObjectName(obj.getName());
      }

      // generates a String from a type
      if (obj.isPresentType()) {
        ASTMCType type = obj.getType();
        builder.setType(Util.printType(type));

        if (obj.hasStereotype(ODRuleStereotypes.LIST)) {
          builder.setListtype("List<Match" + obj.getName() + ">");
          builder.setListimpltype("ArrayList<Match" + obj.getName() + ">");
        } else {
          builder.setListtype("");
          builder.setListimpltype("#");
        }
      } else {
        builder.setType("");
      }


      // process stereotypes
      builder.setLHSObject(true);
      builder.setNotObject(obj.hasStereotype(ODRuleStereotypes.NOT));
      builder.setOptObject(obj.hasStereotype(ODRuleStereotypes.OPTIONAL));
      builder.setListObject(obj.hasStereotype(ODRuleStereotypes.LIST));


      // get the names of all inner links
      List<String> innerLinkObjectNamesList = new ArrayList<String>();
      for (ASTODInnerLink link : obj.getInnerLinksList()) {
        if (link.getODObject().isPresentName()) {
          innerLinkObjectNamesList.add(link.getODObject().getName());
        }
      }
      builder.setInnerLinkObjectNamesList(innerLinkObjectNamesList);


      objects.add(builder.build());
    }
    return objects;
  }

    protected List<ASTMatchingObject> generateObjectList(ASTODRule ast) {
    List<String> objectNames = new LinkedList<>();
    List<ASTMatchingObject> objects = generateLHSObjectList(ast);
    for (ASTMatchingObject o : objects) {
      objectNames.add(o.getObjectName());
    }

    // we still need the Objects from the right side
    if (ast.isPresentRhs()) {
      List<ASTODObject> rhsObjects = Util.getAllODObjects(ast.getRhs());
      for (ASTODObject obj : rhsObjects) {
        // if it does not already exists
        if (!objectNames.contains(obj.getName())) {
          ASTMatchingObjectBuilder builder = ODRuleGenerationMill.matchingObjectBuilder();
          // getting the name is easy
          if (obj.isPresentName()) {
            builder.setObjectName(obj.getName());
          }

          // generates a String from a type
          ASTMCType type = obj.getType();
          builder.setType(Util.printType(type));

          // Right side objects can't be negated, optional or lists
          builder.setLHSObject(false);
          builder.setNotObject(false);
          builder.setOptObject(false);
          builder.setListObject(false);

          objects.add(builder.build());
          objectNames.add(obj.getName());
        }
      }
    }

    return objects;
  }

  protected List<ASTObjectCondition> generateObjectConditions(ASTODRule ast) {
    ODRulesTraverser traverser = ODRulesMill.inheritanceTraverser();
    GenerateConditionsVisitor v = new GenerateConditionsVisitor();
    traverser.add4ODRules(v);
    ast.getLhs().accept(traverser);

    return v.getObjectConditions();
  }

  protected List<ASTLinkCondition> generateLinkConditions(ASTODRule ast) {
    GenerateLinkConditionsVisitor v = new GenerateLinkConditionsVisitor();
    ODRulesTraverser t = ODRulesMill.inheritanceTraverser();
    t.add4ODRules(v);
    ast.getLhs().accept(t);
    return v.getLinkConditions();
  }

  protected List<ASTAssociation> generateAssociations(ASTODRule dslRoot) {
    List<ASTAssociation> associations = new ArrayList<>();

    List<String> types = new ArrayList<>();

    List<ASTODLink> astLinks = new LinkedList<>();
    astLinks.addAll(dslRoot.getLhs().getODLinkList());
    if (dslRoot.isPresentRhs()) {
      astLinks.addAll(dslRoot.getRhs().getODLinkList());
    }

    for (ASTODLink link : astLinks) {

      // try to determine the type of this link
      String grammarAssocName = link.getStereotypeValue("gAssoc");

      if (link.isLink() && grammarAssocName != null && !types.contains(grammarAssocName)) {
        String name = StringTransformations.uncapitalize(Names.getSimpleName(grammarAssocName));

        types.add(grammarAssocName);
        ASTAssociationBuilder builder = ODRuleGenerationMill.associationBuilder();
        builder.setName(name);
        builder.setGname(grammarAssocName);
        associations.add(builder.build());
      }
    }
    return associations;

  }

  protected String generateConstraintExpression(ASTODRule ast) {
    return ast.isPresentConstraint() ? Util.printExpression(ast.getConstraint(), lhsObjects,
            this.hierarchyHelper) : "true";
  }

  protected List<ASTVariable> generateVariables(ASTODRule ast) {
    Collection<String> collectedNames = new HashSet<>();

    List<ASTVariable> variables = calculateVariablesFor(Util.getAllODObjects(ast.getLhs()), collectedNames);
    if (ast.isPresentRhs()) {
      variables.addAll(calculateVariablesFor(Util.getAllODObjects(ast.getRhs()), collectedNames));
    }

    variables.addAll(calculateVariablesFor(var2attr, collectedNames));

    return variables;
  }

  protected List<ASTVariable> calculateVariablesFor(Variable2AttributeMap var2attr,
                                                  Collection<String> collectedNames) {
    List<ASTVariable> variables = new ArrayList<>();
    for (String key : var2attr.getV2a().keySet()) {
      if (!collectedNames.contains(key) && !"$_".equals(key)) {
        ASTVariableBuilder variable = ODRuleGenerationMill.variableBuilder();
        variable.setName(key);
        variable.setType("String");
        variables.add(variable.build());
        collectedNames.add(key);
      }
    }
    return variables;
  }

  protected List<ASTVariable> calculateVariablesFor(List<ASTODObject> ast,
                                                  Collection<String> collectedNames) {
    List<ASTVariable> variables = new ArrayList<>();
    for (ASTODObject obj : ast) {
      for (ASTODAttribute attr : obj.getAttributesList()) {
        if ((attr.isPresentSingleValue() && !(collectedNames.contains(
                Util.printExpression(attr.getSingleValue()))))) {
          ASTVariable variable = generateVariable(obj, attr);

          if (variable != null) {
            if (attr.isPresentSingleValue()) {
              collectedNames.add(Util.printExpression(attr.getSingleValue()));
            } else {
              collectedNames.add(Util.printFirstListValue(attr.getList()));
            }
            variables.add(variable);
          }
        } else if (attr.isPresentList()) {
          variables.addAll(generateVariables(attr.getList(), collectedNames));
        }
      }
    }
    return variables;
  }

  protected Collection<ASTVariable> generateVariables(
          ASTArrayInit list,
          Collection<String> collectedNames) {
    Collection result = new ArrayList<>();
    for (ASTVariableInit initializer : list.getVariableInitList()) {
      String init = new TFExpressionFullPrettyPrinter(new IndentPrinter()).prettyprint(initializer);
      if (init.startsWith("\"$")) {
        init = init.substring(1, init.length() - 1);
        if (!collectedNames.contains(init)) {
          ASTVariable v = ODRuleGenerationMill.variableBuilder().uncheckedBuild();
          v.setName(init);
          v.setType("String");
          result.add(v);
          collectedNames.add(init);
        }
      }
    }
    return result;
  }

  protected ASTVariable generateVariable(ASTODObject obj, ASTODAttribute attr) {

    // The value is a literal
    if (attr.isPresentSingleValue() && attr.getSingleValue() instanceof ASTLiteral) {
      return null;
    }

    if (attr.isPresentList() && attr.getList().getVariableInitList().size() > 1) {
      return null;
    }

    String attrValue = Util.printExpression(attr.getSingleValue());

    // Qualified names are references to objects
    if (attrValue.contains(".")) {
      return null;
    }

    if (!attrValue.startsWith("$")) {
      return null;
    }

    // Reference to an object
    if (Util.getODObject(lhs, attrValue) != null || Util.getODObject(rhs, attrValue) != null) {
      return null;
    }

    // Create a new variable for the rule
    ASTVariableBuilder variable = ODRuleGenerationMill.variableBuilder();
    variable.setName(attrValue);
    variable.setType(attr.printType());

    return variable.build();
  }

  protected HashMap<String, List<String>> generateFoldingHash(ASTODRule ast) {
    HashMap<String, List<String>> result = new HashMap<String, List<String>>();
    // create an (initially empty) sequence for all objects
    for (ASTODObject odObject : Util.getAllODObjects(ast.getLhs())) {
      result.put(odObject.getName(), new ArrayList<String>());
    }
    List<ASTFoldingSet> f = ast.getFoldingSetList();
    for (ASTFoldingSet t : f) {
      for (String o1 : t.getObjectNamesList()) {
        for (String o2 : t.getObjectNamesList()) {
          if (result.get(o1) != null) {
            result.get(o1).add(o2);
          }
        }

      }
    }
    return result;
  }

  protected ASTReplacement generateReplacement(List<ASTChangeOperation> changes) {
    ASTReplacementBuilder replacement = ODRuleGenerationMill.replacementBuilder();
    Map<String, String> requirementNames = new HashMap<String, String>();
    replacement.setRequirementsList(generateRequirements(changes, requirementNames));
    replacement.setChangesList(generateChanges(changes, requirementNames));
    replacement.setCreateObjectsList(generateCreateObjects(changes));
    replacement.setDeleteObjectsList(generateDeleteObjects(changes));
    return replacement.build();
  }

  protected List<ASTRequirement> generateRequirements(List<ASTChangeOperation> changesList,
                                                      Map<String, String> requirementNames) {
    List<ASTRequirement> requirements = new ArrayList<>();

    for (ASTChangeOperation co : changesList) {

      List<ASTChange> setAttributeList = co.getSetAttributeOperationsList();

      for (ASTChange set : setAttributeList) {
        // if the value is collected from other objects
        // save the Value before it might be changed

        if (set.isPresentValue()) {
          String value = set.getValue();
          if (!set.isValueStringList() && value.contains(".") && !requirementNames.containsKey(value)) {
            // create new name
            StringBuilder sb = new StringBuilder("_");

            sb.append(value.replace(".get()", ""));
            for (int i = 0; i < sb.length(); i++) {
              if (sb.charAt(i) == '.') {
                sb.setCharAt(i, '_');
              }
            }

            requirementNames.put(value, sb.toString());

            // create Requirement
            ASTRequirementBuilder requirement = ODRuleGenerationMill.requirementBuilder();

            String type = set.getType();

            String[] strings = value.split("\\.");
            String attributeName = strings[1];
            String getterName = ("boolean".equals(type)) ? "is" + StringTransformations.capitalize(
                    attributeName) : "get" + StringTransformations.capitalize(attributeName);
            String varName = strings[0];


            // fill Requirement with data
            requirement.setType(type);
            requirement.setAttribute(attributeName);
            requirement.setGetter(getterName);
            requirement.setObject(varName);

            requirements.add(requirement.build());
          }
        }


      }
    }

    return requirements;
  }

  protected List<ASTChange> generateChanges(List<ASTChangeOperation> changesList,
                                            Map<String, String> requirementNames) {

    List<ASTChange> changesSequence = new ArrayList<>();

    for (ASTChangeOperation co : changesList) {
      List<ASTChange> setAttributeList = co.getSetAttributeOperationsList();

      for (ASTChange set : setAttributeList) {
        if (set.isPresentValue() && requirementNames.get(set.getValue()) != null) {
          set.setValue(requirementNames.get(set.getValue()));
        }
        changesSequence.add(set);
      }

    }
    return changesSequence;
  }

  protected List<ASTCreateOperation> generateCreateObjects(List<ASTChangeOperation> changesList) {
    List<ASTCreateOperation> createSequence = new ArrayList<>();

    for (ASTChangeOperation co : changesList) {
      createSequence.addAll(co.getCreateOperationsList());
    }
    return createSequence;
  }

  protected List<ASTDeleteOperation> generateDeleteObjects(List<ASTChangeOperation> changesList) {

    List<ASTDeleteOperation> deleteSequence = new ArrayList<>();

    for (ASTChangeOperation co : changesList) {
      deleteSequence.addAll(co.getDeleteOperationsList());
    }
    return deleteSequence;
  }

  protected List<String> generateTypesList(ASTODRule ast) {
    Set<String> types = new LinkedHashSet<String>();

    List<ASTODObject> astObjects = Util.getAllODObjects(ast.getLhs());
    for (ASTODObject obj : astObjects) {

      // generates a String from a type

      ASTMCType type = obj.getType();
      types.add(Util.printType(type));
    }
    return Lists.newArrayList(types);
  }


}
