<!-- (c) https://github.com/MontiCore/monticore -->

The following excerpt of MontiCore features assist with model-based engineering and software language engineering.

Features marked with <span class="badge">Universal</span>
are usable as part of each DSL.
Features marked with <span class="badge">DSL</span> are concrete DSLs,
which can be included as a grammar component,
project dependency and/or are provided as a downloadable CLI tool.


<style>
/* Feature grid*/
.feature-grid {
  display: flex;
  flex-direction: column;
  gap: 0;
}
.feature {
  display: grid;
  grid-template-columns: 340px 1fr;
  gap: 2.5rem;
  align-items: start;
  /*padding: 2.5rem 0;*/
}
/* Alternate image left/right on even features */
.feature:nth-child(even) {
  grid-template-columns: 1fr 340px;
}
.feature:nth-child(even) .feature-image {
  order: 2;
}
.feature:nth-child(even) .feature-body {
  order: 1;
}
.feature-image img {
  width: 100%;
  border-radius: 10px;
  display: block;
  box-shadow: 0 4px 24px rgba(0,0,0,.18);
}
.feature-body {
    overflow: hidden;
}
.feature-body h3 {
  margin-top: 0;
}
/* Responsive — stack on narrow viewports */
@media (max-width: 768px) {
  .feature,
  .feature:nth-child(even) {
    grid-template-columns: 1fr;
  }
  .feature:nth-child(even) .feature-image,
  .feature:nth-child(even) .feature-body {
    order: unset;
  }
}

/* The pill-badge */
.badge {
  display: inline-block;
  padding: 0.25em 0.65em;
  border-radius: 999px;
  font-size: 0.7em;
  font-weight: 600;
  line-height: 1.4;
  vertical-align: 0.15em;
  background: var(--md-accent-fg-color);
  color: var(--md-accent-bg-color);
}
</style>


<div class="feature-grid" markdown>
<div class="feature" markdown>
<div class="feature-image" markdown>

![Modular Language Component Library](https://placehold.co/480x320/1a1a2e/4fc3f7?text=Modular+Language+Component+Library){
loading=lazy }

</div>
<div class="feature-body" markdown>

### Modular Language Component Library

MontiCore provides a library of composable language components,
containing statements, expressions, literals, types, etc.
Each language component is shipped with syntax definitions, context condition checkers, a type system, etc.

??? info "How to Use"

    All other features tagged with _DSL_ listed here may also be used as a language component.
    
    Simply add the `de.monticore:monticore-grammar:$mc_version` dependency to your project.
    ```groovy
    // build.gradle
    plugins {
        id 'de.monticore.generator' version "$mc_version"
    }
    dependencies {
        grammar("de.monticore:monticore-grammar:$mc_version")
    }
    ```
    
    [:material-book-open-variant: Read the docs](https://monticore.github.io/monticore/monticore-grammar/src/main/grammars/de/monticore/Grammars){ .md-button .md-button--primary }

</div>
</div>
---

<div class="feature" markdown>
<div class="feature-image" markdown>

![LSP](https://placehold.co/480x320/0d2137/4dd0e1?text=LSP){ loading=lazy }

</div>
<div class="feature-body" markdown>

### LSP Support <span class="badge">Universal</span>

MontiCore provides out-of-the-box support for language servers using the Language Server Protocol (LSP).
Support for auto complete, go to definition, javadoc-like documentation on hover, finding references,
quick-fixes for diagnostics from failed context conditions, and auto-formatting are automatically generated.
Further features can be added via an API.

??? info "Get started"

    Simply add the LSP gradle plugin to your DSL's project:
    ```groovy
    // build.gradle
    plugins {
        id 'de.monticore.language-server' version "$mc_version"
    }
    ```

    [:material-book-open-variant: Documentation &amp; Examples](https://git.rwth-aachen.de/monticore/tools/lsp-generator){ .md-button .md-button--primary }

</div>
</div>
---


<div class="feature" markdown>
<div class="feature-image" markdown>

![MontiTrans](https://placehold.co/480x320/0d2137/4dd0e1?text=MontiTrans){ loading=lazy }

</div>
<div class="feature-body" markdown>

### Model-to-Model Transformations <span class="badge">Universal</span>

* DSTL
* Pattern Matching API
* Interpreted / Generated

??? info "Get started"

    [:material-download: Download v2.4.1](https://monticore.de){ .md-button }
    &nbsp;
    [:material-book-open-variant: Validation guide](https://monticore.de/docs){ .md-button .md-button--primary }

</div>
</div>
---


<div class="feature" markdown>
<div class="feature-image" markdown>

![UML ClassDiagrams](https://placehold.co/480x320/0d2137/4dd0e1?text=UML+ClassDiagrams){ loading=lazy }

</div>
<div class="feature-body" markdown>

### UML ClassDiagrams <span class="badge">DSL</span>

The classdiagrams are a DSL, supporting structural modeling,
semantic &amp; syntactic differencing and merging.
Constructive modeling is possible by means of a configurable generator.

??? info "Get started"

    The language components and runtime are available via   
    Maven Coordinates: `"de.monticore.lang:cd4analysis:$mc_version"`

    The [generator plugin](https://github.com/MontiCore/cd4analysis/blob/dev/doc/CDGen.md) can be enabled via

    ```groovy
    // build.gradle
    plugins {
      id 'de.rwth.se.cdgen' version "$mc_version"
    }
    ```


    [:material-download: Download CLI Tool](https://www.monticore.de/download/MCCD.jar){ .md-button }
    &nbsp;
    [:material-book-open-variant: Documentation](https://github.com/MontiCore/cd4analysis/){ .md-button .md-button--primary }
    &nbsp;
    [:material-github: Source](https://github.com/MontiCore/cd4analysis/){ .md-button .md-button--primary }

</div>
</div>
---



<div class="feature" markdown>
<div class="feature-image" markdown>

![UML Statecharts](https://placehold.co/480x320/0d2137/4dd0e1?text=UML+Statecharts){ loading=lazy }

</div>
<div class="feature-body" markdown>

### UML Statecharts <span class="badge">DSL</span>

* TODO

??? info "Get started"

    Maven Coordinates: `"de.monticore.lang:sc-language:$mc_version"`

    [:material-download: Download v2.4.1](https://monticore.de){ .md-button }
    &nbsp;
    [:material-book-open-variant: Validation guide](https://monticore.de/docs){ .md-button .md-button--primary }

</div>
</div>
---


<div class="feature" markdown>
<div class="feature-image" markdown>

![MontiGem](https://placehold.co/480x320/0d2137/4dd0e1?text=MontiGem){ loading=lazy }

</div>
<div class="feature-body" markdown>

### MontiGem: Generated Enterprise Management <span class="badge">DSL</span>

By combining structural class diagrams with a GUI DSL and OCL,
the generation of enterprise management web applications can be done.

??? info "Get started"

    [:material-web: Demo Website](https://montigem.demos.se.rwth-aachen.de/){ .md-button }
    &nbsp;
    [:material-book-open-variant: See more](github.com/montiCore/montigem){ .md-button .md-button--primary }

</div>
</div>
---


<div class="feature" markdown>
<div class="feature-image" markdown>

![SysMLv2 Textual](https://placehold.co/480x320/0d2137/4dd0e1?text=SysMLv2){ loading=lazy }

</div>
<div class="feature-body" markdown>

### SysMLv2 Textual <span class="badge">DSL</span>

* TODO

??? info "Get started"

    [:material-download: Download v2.4.1](https://monticore.de){ .md-button }
    &nbsp;
    [:material-book-open-variant: Validation guide](https://monticore.de/docs){ .md-button .md-button--primary }

</div>
</div>
---



<div class="feature" markdown>
<div class="feature-image" markdown>

![Symbols](https://placehold.co/480x320/0d2137/4dd0e1?text=Symbols){ loading=lazy }

</div>
<div class="feature-body" markdown>

### Symbol Management <span class="badge">Universal</span> <span class="badge">DSL</span>

* Symbol Library
* Class2MC
* SyTabDefinition Tool

??? info "Get started"

    [:material-download: Download v2.4.1](https://monticore.de){ .md-button }
    &nbsp;
    [:material-book-open-variant: Validation guide](https://monticore.de/docs){ .md-button .md-button--primary }

</div>
</div>
---


<div class="feature" markdown>
<div class="feature-image" markdown>

![Symbols](https://placehold.co/480x320/0d2137/4dd0e1?text=Symbols){ loading=lazy }

</div>
<div class="feature-body" markdown>

### MontiFun <span class="badge">DSL</span>

* Functional Language (using the expressions library)
* Interpreter REPL
* Java Generator

??? info "Get started"

    [:material-download: Download v2.4.1](https://monticore.de){ .md-button }
    &nbsp;
    [:material-book-open-variant: Validation guide](https://monticore.de/docs){ .md-button .md-button--primary }

</div>
</div>
---



<div class="feature" markdown>
<div class="feature-image" markdown>

![Object Diagrams](https://placehold.co/480x320/0d2137/4dd0e1?text=Object+Diagrams){ loading=lazy }

</div>
<div class="feature-body" markdown>

### UML Object Diagrams <span class="badge">DSL</span>

*

??? info "Get started"

    [:material-download: Download v2.4.1](https://monticore.de){ .md-button }
    &nbsp;
    [:material-book-open-variant: Validation guide](https://monticore.de/docs){ .md-button .md-button--primary }

</div>
</div>
---


</div>

## Further Tools

* Automaton
* FACT/FD
* MLC
* OCL
* SD4Dev
* JSON
* XML

Please note the [MontiCore 3-Level License](../00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md) of these tools.

## Further Information

* see also [**MontiCore handbook**](https://www.monticore.de/handbook.pdf)
* [MontiCore Reference Languages](https://monticore.github.io/monticore/docs/DevelopedLanguages/) - Languages Built
  Using MontiCore
* [Build MontiCore](https://monticore.github.io/monticore/docs/BuildMontiCore/) - How to Build MontiCore
* [Getting Started](https://monticore.github.io/monticore/docs/GettingStarted/) - How to start using MontiCore
* [Changelog](../00.org/Explanations/CHANGELOG.md) - Release Notes
* [FAQ](../00.org/Explanations/FAQ.md) - FAQ
* [Licenses](../00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md) - MontiCore 3-Level License
* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [**List of languages**](https://monticore.github.io/monticore/docs/Languages/)
* [**MontiCore Core Grammar
  Library**](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://monticore.github.io/monticore/docs/BestPractices/)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
