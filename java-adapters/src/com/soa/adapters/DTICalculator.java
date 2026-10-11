package com.soa.adapters;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * DTI Calculator - Java Adapter for BPEL Java Embedding
 * Calculates Debt-to-Income (DTI) and Loan-to-Value (LTV) ratios.
 *
 * This class is instantiated from BPEL via:
 * <bpelx:exec name="CalculateDTI" language="java">
 *     com.soa.adapters.DTICalculator calculator = new com.soa.adapters.DTICalculator();
 *     double dti = calculator.calculateDTI(income, debt);
 * </bpelx:exec>
 *
 * JAR Location: SCA-INF/lib/dti-calculator.jar
 */
public class DTICalculator {

    private static final int SCALE = 4;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_UP;

    /**
     * Calculates the Debt-to-Income ratio.
     * DTI = (Total Monthly Debt / Gross Monthly Income) * 100
     */
    public double calculateDTI(double monthlyIncome, double monthlyDebt) {
        if (monthlyIncome <= 0) return 0.0;
        BigDecimal income = BigDecimal.valueOf(monthlyIncome);
        BigDecimal debt = BigDecimal.valueOf(monthlyDebt);
        return debt.divide(income, SCALE, ROUNDING)
                   .multiply(BigDecimal.valueOf(100))
                   .setScale(2, ROUNDING)
                   .doubleValue();
    }

    /**
     * Calculates the Loan-to-Value ratio.
     * LTV = (Loan Amount / Property Value) * 100
     */
    public double calculateLTV(double loanAmount, double propertyValue) {
        if (propertyValue <= 0) return 0.0;
        BigDecimal loan = BigDecimal.valueOf(loanAmount);
        BigDecimal value = BigDecimal.valueOf(propertyValue);
        return loan.divide(value, SCALE, ROUNDING)
                   .multiply(BigDecimal.valueOf(100))
                   .setScale(2, ROUNDING)
                   .doubleValue();
    }

    /**
     * Calculates the Front-End DTI (Housing Ratio).
     */
    public double calculateFrontEndDTI(double monthlyIncome, double proposedPITI) {
        if (monthlyIncome <= 0) return 0.0;
        BigDecimal income = BigDecimal.valueOf(monthlyIncome);
        BigDecimal piti = BigDecimal.valueOf(proposedPITI);
        return piti.divide(income, SCALE, ROUNDING)
                   .multiply(BigDecimal.valueOf(100))
                   .setScale(2, ROUNDING)
                   .doubleValue();
    }

    /**
     * Calculates the Back-End DTI (Total Debt Ratio).
     */
    public double calculateBackEndDTI(double monthlyIncome, double proposedPITI, double otherMonthlyDebt) {
        if (monthlyIncome <= 0) return 0.0;
        BigDecimal income = BigDecimal.valueOf(monthlyIncome);
        BigDecimal totalDebt = BigDecimal.valueOf(proposedPITI + otherMonthlyDebt);
        return totalDebt.divide(income, SCALE, ROUNDING)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, ROUNDING)
                        .doubleValue();
    }

    /**
     * Evaluates if the loan meets QM (Qualified Mortgage) DTI requirements.
     */
    public boolean isQMCompliant(double dtiRatio) {
        return dtiRatio <= 43.0;
    }
}
