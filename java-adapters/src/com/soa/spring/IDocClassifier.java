package com.soa.spring;

/**
 * Document Classifier Interface - Exposed via Spring SCA
 * Referenced from: composite.xml → spring/doc-classifier-context.xml
 *
 * Call Chain:
 *   SOA_COMPONENT (DocClassifierComponent)
 *       → COMPONENT_TO_SPRING → doc-classifier-context.xml
 *       → SPRING_BEAN (docClassifierBean)
 *       → SPRING_BEAN_TO_CLASS → DocClassifierImpl.java
 */
public interface IDocClassifier {

    // Document type constants
    String DOC_TYPE_W2 = "W2";
    String DOC_TYPE_PAYSTUB = "PAYSTUB";
    String DOC_TYPE_TAX_RETURN = "TAX_RETURN";
    String DOC_TYPE_BANK_STATEMENT = "BANK_STATEMENT";
    String DOC_TYPE_ID_DOCUMENT = "ID_DOCUMENT";
    String DOC_TYPE_UNKNOWN = "UNKNOWN";

    /**
     * Classify a document based on its content.
     * @param documentContent The document content as text
     * @return Document type classification
     */
    String classifyDocument(String documentContent);

    /**
     * Get confidence score for the classification.
     * @param documentContent The document content
     * @param classifiedType The classified type
     * @return Confidence score 0-100
     */
    int getConfidenceScore(String documentContent, String classifiedType);

    /**
     * Check if document is a valid income document.
     * @param documentType The classified document type
     * @return true if W2, paystub, or tax return
     */
    boolean isIncomeDocument(String documentType);
}
