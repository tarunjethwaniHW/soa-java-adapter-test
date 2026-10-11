package com.soa.spring;

/**
 * Document Classifier Implementation - Spring Bean
 * Bean ID: docClassifierBean
 * Exposed as SCA service: DocClassifierService
 *
 * This class is instantiated by Spring and exposed to SOA Suite via:
 *   <implementation.spring src="spring/doc-classifier-context.xml"/>
 */
public class DocClassifierImpl implements IDocClassifier {

    @Override
    public String classifyDocument(String documentContent) {
        if (documentContent == null || documentContent.isEmpty()) {
            return DOC_TYPE_UNKNOWN;
        }

        String lowerContent = documentContent.toLowerCase();

        // W-2 detection
        if (lowerContent.contains("w-2") ||
            lowerContent.contains("wage and tax statement") ||
            (lowerContent.contains("employer") && lowerContent.contains("wages"))) {
            return DOC_TYPE_W2;
        }

        // Paystub detection
        if (lowerContent.contains("pay period") ||
            lowerContent.contains("pay stub") ||
            lowerContent.contains("earnings statement") ||
            (lowerContent.contains("gross") && lowerContent.contains("net"))) {
            return DOC_TYPE_PAYSTUB;
        }

        // Tax return detection
        if (lowerContent.contains("form 1040") ||
            lowerContent.contains("tax return") ||
            lowerContent.contains("internal revenue")) {
            return DOC_TYPE_TAX_RETURN;
        }

        // Bank statement detection
        if (lowerContent.contains("account balance") ||
            lowerContent.contains("statement period") ||
            lowerContent.contains("checking") ||
            lowerContent.contains("savings")) {
            return DOC_TYPE_BANK_STATEMENT;
        }

        // ID document detection
        if (lowerContent.contains("driver") ||
            lowerContent.contains("license") ||
            lowerContent.contains("passport") ||
            lowerContent.contains("identification")) {
            return DOC_TYPE_ID_DOCUMENT;
        }

        return DOC_TYPE_UNKNOWN;
    }

    @Override
    public int getConfidenceScore(String documentContent, String classifiedType) {
        if (documentContent == null || DOC_TYPE_UNKNOWN.equals(classifiedType)) {
            return 0;
        }

        String lowerContent = documentContent.toLowerCase();
        int keywordCount = 0;

        switch (classifiedType) {
            case DOC_TYPE_W2:
                if (lowerContent.contains("w-2")) keywordCount += 2;
                if (lowerContent.contains("employer")) keywordCount++;
                if (lowerContent.contains("wages")) keywordCount++;
                if (lowerContent.contains("federal")) keywordCount++;
                if (lowerContent.contains("social security")) keywordCount++;
                break;

            case DOC_TYPE_PAYSTUB:
                if (lowerContent.contains("pay period")) keywordCount += 2;
                if (lowerContent.contains("gross")) keywordCount++;
                if (lowerContent.contains("net")) keywordCount++;
                if (lowerContent.contains("deductions")) keywordCount++;
                if (lowerContent.contains("ytd")) keywordCount++;
                break;

            case DOC_TYPE_TAX_RETURN:
                if (lowerContent.contains("1040")) keywordCount += 2;
                if (lowerContent.contains("irs")) keywordCount++;
                if (lowerContent.contains("adjusted gross")) keywordCount++;
                if (lowerContent.contains("taxable income")) keywordCount++;
                break;

            case DOC_TYPE_BANK_STATEMENT:
                if (lowerContent.contains("balance")) keywordCount++;
                if (lowerContent.contains("deposits")) keywordCount++;
                if (lowerContent.contains("withdrawals")) keywordCount++;
                if (lowerContent.contains("account number")) keywordCount++;
                break;

            case DOC_TYPE_ID_DOCUMENT:
                if (lowerContent.contains("date of birth")) keywordCount++;
                if (lowerContent.contains("expiration")) keywordCount++;
                if (lowerContent.contains("photo")) keywordCount++;
                break;

            default:
                return 0;
        }

        return Math.min(100, keywordCount * 20);
    }

    @Override
    public boolean isIncomeDocument(String documentType) {
        return DOC_TYPE_W2.equals(documentType) ||
               DOC_TYPE_PAYSTUB.equals(documentType) ||
               DOC_TYPE_TAX_RETURN.equals(documentType);
    }
}
