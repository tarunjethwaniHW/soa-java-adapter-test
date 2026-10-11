# SOA Java Adapter Test Project

Test project for validating parser extraction of **BPEL Java Embedding** (`<bpelx:exec>`) patterns in Oracle SOA Suite.

## Purpose

This project demonstrates the complete call chain from Java client through SOA composite to embedded Java code:

```
JavaAdapterClient.java
    │
    │  JAX-WS SOAP call
    ▼
JavaAdapterProcess.wsdl (Entry Point)
    │
    │  WSDL_TO_SOA_SERVICE
    ▼
composite.xml (javaadapter_client_ep)
    │
    │  SERVICE_TO_COMPONENT (wire)
    ▼
composite.xml (LoanProcessOrchestration)
    │
    │  COMPONENT_TO_BPEL
    ▼
LoanProcessOrchestration.bpel
    │
    │  4× <bpelx:exec language="java"> (BPEL_JAVA_EMBED)
    ▼
Java Adapter Classes (SCA-INF/lib/*.jar)
    ├── DTICalculator.java
    ├── OCRProcessor.java
    ├── DecisionEngine.java
    └── AmortizationCalculator.java
```

## Project Structure

```
soa-java-adapter-test/
├── README.md
├── java-adapter-composite/
│   ├── composite.xml                    # Single component → single BPEL
│   ├── JavaAdapterProcess.wsdl          # Service entry point
│   ├── JavaAdapterProcess.xsd           # Types schema
│   └── LoanProcessOrchestration.bpel    # 4× <bpelx:exec> Java embeddings
├── java-adapters/
│   └── src/com/soa/adapters/
│       ├── DecisionEngine.java          # Loan decision logic
│       ├── DTICalculator.java           # DTI/LTV calculations
│       ├── OCRProcessor.java            # Document OCR processing
│       └── AmortizationCalculator.java  # Pricing calculations
└── java-client/
    └── src/com/soa/client/
        └── JavaAdapterClient.java       # JAX-WS client
```

## Java Embedding Patterns (Single BPEL, 4 Adapters)

| Step | Embedding Name | Java Class | Method |
|------|----------------|------------|--------|
| 1 | `CalculateDTI` | `com.soa.adapters.DTICalculator` | `calculateDTI()`, `calculateLTV()` |
| 2 | `ProcessOCR` | `com.soa.adapters.OCRProcessor` | `processDocument()` |
| 3 | `RunDecisionEngine` | `com.soa.adapters.DecisionEngine` | `evaluateLoan()` |
| 4 | `CalculatePricing` | `com.soa.adapters.AmortizationCalculator` | `calculateRate()`, `calculateMonthlyPayment()` |

## BPEL Java Embedding Example

```xml
<bpelx:exec name="RunDecisionEngine" language="java">
    <![CDATA[
        // Instantiate Java adapter from SCA-INF/lib/decision-engine.jar
        com.soa.adapters.DecisionEngine engine = new com.soa.adapters.DecisionEngine();

        // Get data from BPEL variables
        double dti = ((Double)getVariableData("dtiRatio")).doubleValue();
        
        // Build loan data map
        java.util.Map<String, Object> loanData = new java.util.HashMap<>();
        loanData.put("dti", dti);
        loanData.put("creditScore", 720);

        // Call Java method
        com.soa.adapters.DecisionEngine.LoanDecision decision = engine.evaluateLoan(loanData);

        // Set results back to BPEL variables
        setVariableData("decisionStatus", decision.getDecision());
    ]]>
</bpelx:exec>
```

## Expected Parser Output

After BpelParser enhancement to extract `<bpelx:exec>`, the parser should produce:

### From composite.xml
- `SOA_COMPOSITE` snippet (JavaAdapterTestComposite)
- `SOA_SERVICE` snippet (javaadapter_client_ep)
- `SOA_COMPONENT` snippet (LoanProcessOrchestration) with `COMPONENT_TO_BPEL` callee

### From LoanProcessOrchestration.bpel
- `BPEL_ORCH` snippet with **4× `BPEL_JAVA_EMBED` callees**:

```json
{
  "TypeOfSnippet": "BPEL_ORCH",
  "FunctionName": "LoanProcessOrchestration",
  "Callees": [
    {
      "CalleeType": "BPEL_JAVA_EMBED",
      "CalleeClass": "DTICalculator",
      "CalleeFunction": "DTICalculator",
      "Target": "com.soa.adapters.DTICalculator"
    },
    {
      "CalleeType": "BPEL_JAVA_EMBED",
      "CalleeClass": "OCRProcessor",
      "CalleeFunction": "OCRProcessor",
      "Target": "com.soa.adapters.OCRProcessor"
    },
    {
      "CalleeType": "BPEL_JAVA_EMBED",
      "CalleeClass": "DecisionEngine",
      "CalleeFunction": "DecisionEngine",
      "Target": "com.soa.adapters.DecisionEngine"
    },
    {
      "CalleeType": "BPEL_JAVA_EMBED",
      "CalleeClass": "AmortizationCalculator",
      "CalleeFunction": "AmortizationCalculator",
      "Target": "com.soa.adapters.AmortizationCalculator"
    }
  ]
}
```

### From Java files
- 4× Java class snippets that the `BPEL_JAVA_EMBED` callees can link to

## Running the Parser

```bash
cd Parser3.0/parser-service-v2
dotnet run --project ParserService.Runner -- "C:\Users\2000173735\Desktop\Repos\app-codescout\soa-java-adapter-test" java
```

## Expected Neo4j Call Chain

```cypher
-- Full path from Java Client to Java Adapters
MATCH path = (client:Snippet)-[:CALLS*1..6]->(adapter:Snippet)
WHERE client.file_name = 'JavaAdapterClient.java'
  AND adapter.file_name IN ['DecisionEngine.java', 'DTICalculator.java', 
                            'OCRProcessor.java', 'AmortizationCalculator.java']
RETURN path
```

Expected chain for each adapter:
```
JavaAdapterClient.java (FUNCTION)
    ↓ CALLS (JAX_WS_CALL)
JavaAdapterProcess.wsdl (WSDL_OPERATION)
    ↓ CALLS (WSDL_TO_SOA_SERVICE)
composite.xml (SOA_SERVICE: javaadapter_client_ep)
    ↓ CALLS (SERVICE_TO_COMPONENT)
composite.xml (SOA_COMPONENT: LoanProcessOrchestration)
    ↓ CALLS (COMPONENT_TO_BPEL)
LoanProcessOrchestration.bpel (BPEL_ORCH)
    ↓ CALLS (BPEL_JAVA_EMBED)  ← 4 edges, one per adapter
*.java (FUNCTION or DEFAULT_CONSTRUCTOR)
```

## Key Pattern Characteristics

| Aspect | Value |
|--------|-------|
| Java Invocation | Direct instantiation via `new ClassName()` |
| JAR Location | `SCA-INF/lib/*.jar` (deployment artifact) |
| No `<reference>` | Java embedding is inline code, not external service |
| No partnerLink | Unlike `<invoke>`, `<bpelx:exec>` has no partnerLink |
| Namespace | `xmlns:bpelx="http://schemas.oracle.com/bpel/extension"` |
