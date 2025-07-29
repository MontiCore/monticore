<!-- (c) https://github.com/MontiCore/monticore -->

# MontiCore Best Practices - Testing a Language

!!! note "This Best Practice is still under development"


Defining a new grammar is a task as complex and error prone as writing arbitrary software.
Much can be said about how to assure the quality of a grammar. 
To ensure the quality of a grammar, 
 a comprehensive set of parsable model, as well as negative examples are useful.
It is necessary to 

- review the grammar thoroughly 
- use a comprehensive set of input models to be parsed, and
- identify a set of negative (not parseable) models to prevent false positives.

However, languages consist of more than just syntax; 
they also include context conditions and additional tools, which have to be tested.

Unit tests are among the most helpful and efficient techniques to check a grammar 
 respectively its outcome for the desired behavior.
Therefore, we use a JUnit 5 infrastructure to check the desired behavior of a grammar's result.

## Testing the Syntax of a Language

A typical testing class, such as `CheckScannerlessTest` 
 is annotated as a test class for the language under test (here Scannerless).
The following code contains a positive and a negative test.
 Both use the parsing from a String functionality.
In the first case a `Type` is parsed and in the second case an `Expression`
 (here types are also expressions like in OCL, but the comparison `">>"` contains an erroneous space).

``` { .java .annotate linenums="1" }
@TestWithMCLanguage(ScannerlessMill.class) // Initializes a language's mill in addition to the AbstractMCTest hooks
public class CheckScannerlessTest {
  // The @TestWithMCLanguage ensures, that before each test:
  //  - a side effect free log variant is newly initialized
  //  - quickFail is disabled
  //  - the specified Mill is initialized
  @Test
  public void testType2() throws IOException {
    // A positive test
    ASTType ast = ScannerlessMill.parser().parse_StringType( " List < Theo > " )
            .orElseGet(MCAssertions::failAndPrintFindings);
    assertEquals("List", ast.getName());
    ASTTypeArguments ta = ast.getTypeArguments();
    assertEquals("Theo", ta.getType(0).getName());
    // MCAssertions.assertNoFindings(); is implicitly called due to @TestWithMCLanguage
  }

  @Test
  public void testType8() throws IOException {
    // This cannot be parsed as a Type >> wert
    // This cannot be parsed because of the illegal space in ">>"
    var parser = ScannerlessMill.parser();
    parser.parse_StringExpression("List<Set<Theo>>> >wert" );
    assertTrue(parser.hasErrors()); // check that the parser has errors
    
    // assert a findings is present & remove it from the log
    // We ignore the content of the finding, as it is a parser error 
    MCAssertions.assertHasFinding(); 
  }
  // The @TestWithMCLanguage ensures, that after each test:
  //  - no more findings are present
  //  - the mill is torn down
}
```

The `@TestWithMCLanguage` annotation sets-up the test for a language.
Before each test,
 the logger is replaced with a side effect free stub that collects,
 the previous findings cleared,
 and the given mill initialized.
After each test, the log must not have any findings present.
If no Mill setup is desired, the `AbstractMCTest` class provides the same functionality.

In addition to jUnits `Assertions`, MontiCore provides a [`MCAssertions`](../../monticore-runtime/testFixturesJavadoc) class for e.g. Log assertions:

The notable methods are:

* `MCAssertions#assertNoFindings(String message)` - Ensure no findings are present (always called after each test)
* `MCAssertions#assertHasFindingStartingWith(String expectedPrefix, String message)` - Ensures that at least one finding starts with the prefix and removes & returns that one finding
* `MCAssertions#assertHasFindingsStartingWith(String expectedPrefix, String message)` - Ensures that at least one finding starts with the prefix and removes & returns all matching findings
* `MCAssertions#assertHasFinding(Predicate<Finding> predicate, String message)` - Ensures that at least one finding matches the predicate and removes & returns that one finding
* `MCAssertions#assertHasFindings(Predicate<Finding> predicate, String message)` - Ensures that at least one finding matches the predicate and removes & returns all matching findings

When the generated pretty printer is customized via the TOP-mechanism,
 the `PrettyPrinterTester` class provides functionality for quickly writing a bunch of tests for the pretty printer.

The test functionality of MontiCore is provided by the [test fixture](https://docs.gradle.org/current/userguide/java_testing.html#sec:java_test_fixtures) 
`testFixture("de.monticore:monticore-grammar:$mc_version")` dependency.
For smaller examples of parsing, pretty printing, etc., models can be written within the test class and
external model files are not needed.

## Testing the Symbol Table of a Language

!!! note "This Best Practice is still under development"
      
      The testing of symbol table creation, the resolving of symbols, possible adapters, and DeSers is WIP


## Testing Context Conditions

As a best practice for testing context conditions, one should test both, valid and invalid models.
The former make sure that valid models do not violate the context condition (i.e. true positives and no false negatives), 
whereas the latter ensure that invalid models do violate the context condition 
(i.e. true negatives and no false positives). 
Consequently, two different kinds of tests should exist for every context condition.

Similar to parser tests, the `@TestWithMCLanguage` annotation or `AbstractMCTest` class can be used to set up tests.

### Testing a Context Condition on a Valid Model

Testing a context condition on a *valid model* consists of the following three steps:

  * Parse the model, obtain the AST, and create its symbol table.
  * Check the context condition on that AST.
  * Verify that no errors were found.

``` { .java .annotate linenums="1" }
@TestWithMCLanguage(AutomataMill.class)  // Sets up the log, initializes the AutomataMill, and tests that no findings are present after each test (1)!
public class TransitionSourceExistsTest {
 @Test
 public void testOnValidModel() throws IOException {
  ASTAutomaton ast = AutomataMill.parser().parse_String(
      "automaton Simple { state A;  state B;  A -x> A;  B -y> A; }" // Parses an automaton model from the given String (2)!
  ).orElseGet(MCAssertions::failAndPrintFindings);                  // and stores the AST - in case the parsing fails, the findings are printed (3)!

  // set up the symbol table
  IAutomataArtifactScope modelTopScope = createSymbolTable(ast); 

  // set up context condition infrastructure & check
  AutomataCoCoChecker checker = new AutomataCoCoChecker();
  checker.addCoCo(new TransitionSourceExists());

  checker.checkAll(ast);

  // MCAssertions.assertNoFindings(); // only necessary when TestWithMCLanguage is not used
 }
}
```

1. Sets up the log, initializes the AutomataMill, and tests that no findings are present after each test
2. Parses an automaton model from the given String
3. and stores the AST - in case the parsing fails, the findings are printed

The shown test case demonstrates this by testing the context condition _TransitionSourceExists_.
First of all, the model is specified in ll. 5f.
An ArtifactScope that contains the symboltable of the model is created from this
model (cf. l. 10). 
For more information about ArtifactScopes, see [Chapter 9 of the handbook](https://www.monticore.de/handbook.pdf).
The context condition is instantiated and added to a checker. 
Next, the checker is executed on the model (cf. ll. 13f).
Finally, if the `@TestWithMCLanguage` is not used, the test verifies that no errors occurred in l. 18.


### Testing a Context Condition on an Invalid Model

Testing a context condition on an *invalid model* is similar to the above check, 
but at the end checks for the expected errors.

The following listing shows a test on an invalid model that does not define the source state of a transition.
Again, the model is specified (cf. l. 3) and the symbol table for this model is created in l. 9.
This model uses a state that has not been defined.
A checker is configured with the context condition under test and executed on the invalid model (cf. ll. 12f). 
This example expects exactly one error with a given text. 
Checking that all expected findings occurred (cf. ll. 18) ensures that the context condition identifies the invalid model as such


``` { .java .annotate linenums="1" }
 @Test
 public void testOnInvalidModel() throws IOException {
  ASTAutomaton ast = AutomataMill.parser().parse_String(
      "automaton Simple { " +
      "  state A;  state B; A - x > A;  Blubb - y > A; }"
  ).orElseGet(MCAssertions::failAndPrintFindings);
   
  // setup the symbol table
  IAutomataArtifactScope modelTopScope = createSymbolTable(ast);

  // setup context condition infrastructure & check
  AutomataCoCoChecker checker = new AutomataCoCoChecker();
  checker.addCoCo(new TransitionSourceExists());

  checker.checkAll(ast);
  
  // we expect one error in the findings
  MCAssertions.assertHasFindingsStartingWith(TransitionSourceExists.ERROR_CODE);
 }
```

It is possible to check the source position of the error in the invalid model 
or complete error message as well.
However, it is often useful to reduce the assertion to checking the error code (`0xADD03`), 
because error messages are relatively often modified.

### Uniqueness of Error Codes

!!! note "This Best Practice is still under development"
    Check the `check-error-codes` GitHub action for now
   

## Testing the Command Line Interface of a Language

!!! note "This Best Practice is still under development"


The handbook describes testing of language features in [Sections 10.3 and 21.5](https://www.monticore.de/handbook.pdf).

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](https://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/dev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [License definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

