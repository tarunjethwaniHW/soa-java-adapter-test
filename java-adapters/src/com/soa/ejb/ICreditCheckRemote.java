package com.soa.ejb;

import javax.ejb.Remote;

/**
 * Credit Check EJB Remote Interface
 * JNDI: ejb/CreditCheck
 * Referenced from: composite.xml binding.ejb
 *
 * Call Chain:
 *   SOA_REFERENCE (CreditCheckEJB)
 *       → REFERENCE_TO_EJB (binding.ejb jndi-name="ejb/CreditCheck")
 *       → ICreditCheckRemote.java (@Remote interface)
 *       → CreditCheckEJB.java (@Stateless implementation)
 *
 * This interface is invoked from SOA Suite via:
 *   <binding.ejb uri="CreditCheckEJB#com.soa.ejb.ICreditCheckRemote"
 *                jndi-name="ejb/CreditCheck"/>
 */
@Remote
public interface ICreditCheckRemote {

    /**
     * Check credit with external bureau.
     * @param ssn Social Security Number (format: XXX-XX-XXXX)
     * @param firstName Applicant first name
     * @param lastName Applicant last name
     * @return Credit report data
     */
    CreditReport checkCredit(String ssn, String firstName, String lastName);

    /**
     * Get credit score only (faster, no full report).
     * @param ssn Social Security Number
     * @return Credit score (300-850)
     */
    int getCreditScore(String ssn);

    /**
     * Check if credit meets minimum threshold.
     * @param ssn Social Security Number
     * @param minScore Minimum required score
     * @return true if credit score >= minScore
     */
    boolean meetsMinimumScore(String ssn, int minScore);
}
