### What is the main functionality?

**Decisify** is a decision intelligence platform that assists users in making complex, high-stakes decisions by combining structured inputs, unstructured data, and AI-driven reasoning.

The system enables users to:

* define decision scenarios with two or more alternatives
* specify evaluation criteria and their relative importance (weights)
* enter values manually and/or ingest data from uploaded documents
* automatically extract relevant decision factors using Generative AI
* review, confirm, edit, or reject AI-extracted values before they count
* compute and rank alternatives based on normalized, weighted scoring
* simulate "what-if" scenarios under changed assumptions
* receive transparent, explainable recommendations
* revisit and compare past decisions

Unlike a chat-based assistant, Decisify does not simply provide answers — it models the decision process explicitly (alternatives, criteria, weights, values) and exposes the reasoning behind the outcome.

Example:

```text
Decision: Which job offer should I accept?

Alternatives:
- Company A
- Company B

Criteria:
- Salary             35%
- Career Growth      25%
- Work-Life Balance  20%
- Location           10%
- Benefits           10%

Documents:
- company-a-offer.pdf
- company-b-offer.pdf

Result:
1. Company A — 84/100
2. Company B — 78/100
```


### Who are the intended users?

The platform is designed for users who regularly face complex, multi-criteria decisions:

* **Students & early professionals**<br/>
    Evaluating job offers, study programs, or relocation choices
* **Engineers & technical professionals**<br/>
    Comparing tools, architectures, or cloud providers
* **Business users & analysts**<br/>
    Making strategic decisions based on cost, risk, and performance
* **General users**<br/>
    Making structured personal decisions (financial, career, lifestyle)


### How will you integrate GenAI meaningfully?

GenAI is not used as a generic chatbot but as a core reasoning and transformation component within the system, isolated in its own service and triggered asynchronously via Kafka so it never blocks user-facing requests:

* **Information Extraction**<br/>
    Extract structured variables (e.g. salary, benefits, risks) from unstructured sources such as uploaded PDFs
* **Normalization & Structuring**<br/>
    Convert extracted data into comparable decision metrics (unit- and direction-aware, via `higherIsBetter` metadata)
* **Human-in-the-loop trust**<br/>
    Every extracted value carries a confidence score and stays a proposal — the user must accept, edit, or reject it before it feeds into scoring
* **Explainability Layer**<br/>
    Generate human-readable justifications for a recommendation, including trade-offs and confidence levels
* **RAG-based Support (optional extension)**<br/>
    Retrieve relevant context from ChromaDB-stored embeddings to support explanations and follow-up questions

This ensures GenAI adds **functional value** to the decision pipeline rather than surface-level interaction, while architectural rule #4 ("AI output is not automatically trusted") keeps the human in control of the final decision.


### Describe some scenarios how your app will function

**Scenario A: Job Offer Comparison**

A user inputs two job offers (either manually or via uploaded PDF documents). The Document Service stores the files and emits `document.uploaded`; the GenAI Service extracts key attributes such as salary, location cost, benefits, and growth potential. The user assigns weights (e.g. salary = high, growth = medium), reviews the extracted values, and the Decision Service computes a ranking with an explanation of why one offer scores higher.

**Scenario B: Cloud Provider Selection**

An engineer compares AWS, GCP, and Azure for a system deployment, evaluating cost, scalability, latency, and operational complexity. The Simulation Service tests different workload assumptions ("what if traffic doubles"), returning original vs. simulated rankings so the user can see how sensitive the recommendation is to those assumptions.

**Scenario C: Personal Relocation Decision**

A user compares living in two cities, aggregating cost-of-living data, job opportunities, and lifestyle factors. "What-if" scenarios (e.g. salary increase, rent change) are simulated without mutating the original decision, and a recommendation is generated with an explanation and the underlying trade-offs.

**Scenario D: Multi-Document Analysis**

A user uploads multiple reports (e.g. financial or technical). The GenAI Service extracts key signals from each and integrates them into a single decision model; conflicting insights across documents are surfaced for the user to resolve rather than silently merged.


### Does a similar solution exist? How do you go beyond your idea?

Existing tools such as general-purpose AI assistants (e.g. ChatGPT) or comparison platforms provide:

* static recommendations, or
* conversational answers without structured, revisitable reasoning

However, they lack:

* formal decision modeling (explicit alternatives, criteria, weights)
* simulation capabilities that separate "what-if" exploration from the committed decision
* transparency in how a numeric conclusion was derived
* a review step where AI-extracted data is confirmed before it affects an outcome

**Decisify goes beyond these solutions by:**

* introducing a structured **decision framework** (criteria, weights, alternatives, normalized scoring)
* integrating **multi-source data fusion** (uploaded documents + manual inputs)
* implementing a **simulation engine** for scenario analysis that never mutates the original decision
* providing **explainable AI outputs** with confidence levels, not just answers
* enabling **decision traceability**, letting users revisit and adjust past decisions
* keeping a **human-in-the-loop** checkpoint between AI extraction and the numbers that drive the recommendation

This transforms the system from a passive assistant into an **active, auditable decision-support engine**.
