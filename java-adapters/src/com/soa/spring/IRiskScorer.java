package com.soa.spring;

import java.util.Map;

/**
 * Risk Scorer Interface - Exposed via Spring SCA
 * Referenced from: composite.xml → spring/risk-scorer-context.xml
 *
 * Call Chain:
 *   SOA_COMPONENT (RiskScorerComponent)
 *       → COMPONENT_TO_SPRING → risk-scorer-context.xml
 *       → SPRING_BEAN (riskScorerBean)
 *       → SPRING_BEAN_TO_CLASS → RiskScorerImpl.java
 */
public interface IRiskScorer {

    /**
     * Calculate risk score for a loan application.
     * @param applicationData Map containing loan application data
     *        - creditScore: Integer
     *        - dti: Double (debt-to-income ratio)
     *        - ltv: Double (loan-to-value ratio)
     *        - loanAmount: Double
     * @return Risk score between 300-850
     */
    int calculateRiskScore(Map<String, Object> applicationData);

    /**
     * Get risk category based on score.
     * @param score Risk score (300-850)
     * @return Category: "EXCELLENT", "GOOD", "FAIR", or "POOR"
     */
    String getRiskCategory(int score);

    /**
     * Check if score meets minimum threshold.
     * @param score Risk score
     * @param threshold Minimum required score
     * @return true if score >= threshold
     */
    boolean meetsThreshold(int score, int threshold);
}
