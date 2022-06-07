<!-- (c) https://github.com/MontiCore/monticore -->

# MontiCore Best Practices - Understanding Errors, Defining Errors



Errors happen.
Some happen because of faults in the code (we call that internal errors),
some happen because we haven't explained well how to use MontiCore and 
how to use the generated code.

Here we try to add information how to handle occurring errors.
We use the error code for an easier identification. Error codes start with 
`0x`and use 5(!) hex characters and thus should be at the same time 
memorizable (because not completely unknown, but still not so common that
they could be taken for something else).

## **Handling Errors 0x.....** 

### How to use **Expressions** (0xA0129) 

* `Expression` is a predefined nonterminal in the MontiCore basic grammars. 
  Because of the infix notation of some operators and similar challenges,
  it is usually not possible to use a subset of the expressions only. 
  For example use of `ConditionalExpression` may lead to a parser generation 
  error (i.e. `0xA0129`).
* Solutions:
  1. Use nonterminal `Expression` and forbid all unwanted alternatives through 
     context conditions.
  2. Think of allowing more general expressions?
  3. If especially the syntax of `if . then . else .` shall be reused, 
     why not define this in a new nonterminal and ignore that the same
     syntactic constructs were already available in another production.
* Defined by: CKi, BR.
  
 
    
## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](https://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/opendev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/opendev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)
