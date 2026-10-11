package com.soa.adapters;

import java.util.Map;

/**
 * Decision Engine - Java Adapter for BPEL Java Embedding
 * Automated Underwriting System (AUS) decision engine for loan evaluation.
 *
 * This class is instantiated from BPEL via:
 * <bpelx:exec name="RunDecisionEngine" language="java">
 *     com.soa.adapters.DecisionEngine engine = new com.soa.adapters.DecisionEngine();
 *     LoanDecision decision = engine.evaluateLoan(loanData);
 * </bpelx:exec>
 *
 * JAR Location: SCA-INF/lib/decision-engine.jar
 */
public class DecisionEngine {

    public static final String APPROVE = "APPROVE";
    public static final String APPROVE_WITH_CONDITIONS = "APPROVE_WITH_CONDITIONS";
    public static final String REFER = "REFER";
    public static final String DECLINE = "DECLINE";

    private static final int MIN_CREDIT_SCORE = 620;
    private static final double MAX_DTI = 43.0;
    private static final double MAX_LTV = 97.0;

    /**
     * Evaluates a loan application and returns a decision.
     */
    public LoanDecision evaluateLoan(Map<String, Object> loanData) {
        double loanAmount = getDouble(loanData, "loanAmount", 0);
        double propertyValue = getDouble(loanData, "propertyValue", 0);
        double monthlyIncome = getDouble(loanData, "monthlyIncome", 0);
        double monthlyDebt = getDouble(loanData, "monthlyDebt", 0);
        int creditScore = getInt(loanData, "creditScore", 0);

        double ltv = (propertyValue > 0) ? (loanAmount / propertyValue) * 100 : 0;
        double dti = (monthlyIncome > 0) ? (monthlyDebt / monthlyIncome) * 100 : 0;

        int riskScore = calculateRiskScore(creditScore, dti, ltv);
        String decision = makeDecision(creditScore, dti, ltv, riskScore);

        return new LoanDecision(decision, riskScore, ltv, dti);
    }

    private int calculateRiskScore(int creditScore, double dti, double ltv) {
        double creditFactor = Math.min(100, Math.max(0, (creditScore - 500) / 3.5));
        double dtiFactor = Math.min(100, Math.max(0, (60 - dti) * 2.5));
        double ltvFactor = Math.min(100, Math.max(0, (100 - ltv) * 1.25));
        return (int) ((creditFactor * 0.4) + (dtiFactor * 0.35) + (ltvFactor * 0.25));
    }

    private String makeDecision(int creditScore, double dti, double ltv, int riskScore) {
        if (creditScore < MIN_CREDIT_SCORE) return DECLINE;
        if (dti > MAX_DTI) return DECLINE;
        if (ltv > MAX_LTV) return DECLINE;
        if (riskScore >= 75) return APPROVE;
        if (riskScore >= 60) return APPROVE_WITH_CONDITIONS;
        if (riskScore >= 40) return REFER;
        return DECLINE;
    }

    private double getDouble(Map<String, Object> map, String key, double defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number) return ((Number) value).doubleValue();
        return defaultValue;
    }

    private int getInt(Map<String, Object> map, String key, int defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number) return ((Number) value).intValue();
        return defaultValue;
    }

    /**
     * Loan decision result containing decision, risk score, and ratios.
     */
    public static class LoanDecision {
        private final String decision;
        private final int riskScore;
        private final double ltv;
        private final double dti;

        public LoanDecision(String decision, int riskScore, double ltv, double dti) {
            this.decision = decision;
            this.riskScore = riskScore;
            this.ltv = ltv;
            this.dti = dti;
        }

        public String getDecision() { return decision; }
        public int getRiskScore() { return riskScore; }
        public double getLtv() { return ltv; }
        public double getDti() { return dti; }
    }
}
