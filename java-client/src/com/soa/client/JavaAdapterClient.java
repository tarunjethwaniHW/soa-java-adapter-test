package com.soa.client;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;

/**
 * JAX-WS client that calls JavaAdapterProcess SOA composite via WSDL.
 * This is the entry point that triggers the BPEL orchestration with Java embeddings.
 *
 * WSDL Contract: java-adapter-composite/JavaAdapterProcess.wsdl
 * Service: javaadapter_client_ep
 * PortType: JavaAdapterProcess
 * Operation: processLoan
 * Endpoint: http://localhost:8080/soap/JavaAdapterProcess
 *
 * Call Chain:
 *   JavaAdapterClient → WSDL → composite.xml → BPEL → <bpelx:exec> → Java Adapter
 */
public class JavaAdapterClient {

    private static final String WSDL_URL = "http://localhost:8080/soap/JavaAdapterProcess?wsdl";
    private static final String NAMESPACE = "http://xmlns.oracle.com/JavaAdapterTest/JavaAdapterProcess";
    private static final String SERVICE_NAME = "javaadapter_client_ep";

    private final JavaAdapterProcessPort port;

    public JavaAdapterClient() throws Exception {
        URL wsdlURL = new URL(WSDL_URL);
        QName serviceName = new QName(NAMESPACE, SERVICE_NAME);
        Service service = Service.create(wsdlURL, serviceName);
        this.port = service.getPort(JavaAdapterProcessPort.class);
    }

    public JavaAdapterClient(String wsdlUrl) throws Exception {
        URL wsdlURL = new URL(wsdlUrl);
        QName serviceName = new QName(NAMESPACE, SERVICE_NAME);
        Service service = Service.create(wsdlURL, serviceName);
        this.port = service.getPort(JavaAdapterProcessPort.class);
    }

    /**
     * Calls the SOA Suite JavaAdapterProcess.processLoan operation.
     * This triggers the BPEL orchestration which uses Java embeddings to call:
     * - DecisionEngine.evaluateLoan() (Underwriting)
     * - DTICalculator.calculateDTI() (Borrower Intake)
     * - OCRProcessor.processDocument() (Document Processing)
     * - AmortizationCalculator.calculateRate() (Pricing)
     */
    public LoanProcessResponse processLoan(LoanProcessRequest request) throws ProcessingFaultException {
        System.out.println("[JavaAdapterClient] Calling SOA Suite: processLoan");
        System.out.println("[JavaAdapterClient] WSDL: " + WSDL_URL);
        System.out.println("[JavaAdapterClient] Applicant: " + request.getApplicantName());
        System.out.println("[JavaAdapterClient] Loan Amount: $" + request.getLoanAmount());

        LoanProcessResponse response = port.processLoan(request);

        System.out.println("[JavaAdapterClient] Response received from SOA Suite");
        return response;
    }

    public static void main(String[] args) {
        System.out.println("=========================================================================");
        System.out.println("     JAVA ADAPTER CLIENT - Calling SOA Suite via WSDL");
        System.out.println("     Tests BPEL Java Embedding patterns");
        System.out.println("=========================================================================");
        System.out.println("Target WSDL: " + WSDL_URL);
        System.out.println("Target Namespace: " + NAMESPACE);
        System.out.println("Service Name: " + SERVICE_NAME);
        System.out.println("=========================================================================");

        try {
            JavaAdapterClient client = new JavaAdapterClient();

            // Test Case 1: Standard loan application
            System.out.println("\n--- TEST CASE 1: Standard Application ---");
            LoanProcessRequest request1 = new LoanProcessRequest();
            request1.setApplicantName("John Doe");
            request1.setSsn("123-45-6789");
            request1.setLoanAmount(250000.0);
            request1.setMonthlyIncome(12000.0);
            request1.setMonthlyDebt(2000.0);
            request1.setPropertyValue(300000.0);

            LoanProcessResponse response1 = client.processLoan(request1);
            printResponse(response1);

            // Test Case 2: High DTI application
            System.out.println("\n--- TEST CASE 2: High DTI Application ---");
            LoanProcessRequest request2 = new LoanProcessRequest();
            request2.setApplicantName("Jane Smith");
            request2.setSsn("987-65-4321");
            request2.setLoanAmount(350000.0);
            request2.setMonthlyIncome(8000.0);
            request2.setMonthlyDebt(4000.0);
            request2.setPropertyValue(400000.0);

            LoanProcessResponse response2 = client.processLoan(request2);
            printResponse(response2);

        } catch (ProcessingFaultException e) {
            System.out.println("[JavaAdapterClient] PROCESSING FAULT: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[JavaAdapterClient] Error connecting to SOA Suite: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n=========================================================================");
    }

    private static void printResponse(LoanProcessResponse response) {
        System.out.println("[RESPONSE] Loan ID: " + response.getLoanId());
        System.out.println("[RESPONSE] Status: " + response.getStatus());
        System.out.println("[RESPONSE] Decision: " + response.getDecision());
        System.out.println("[RESPONSE] DTI Ratio: " + response.getDtiRatio() + "%");
        System.out.println("[RESPONSE] LTV Ratio: " + response.getLtvRatio() + "%");
        System.out.println("[RESPONSE] Risk Score: " + response.getRiskScore());
        System.out.println("[RESPONSE] Monthly Payment: $" + response.getMonthlyPayment());
    }

    // Inner stub classes for compilation (would be generated from WSDL in practice)

    public interface JavaAdapterProcessPort {
        LoanProcessResponse processLoan(LoanProcessRequest request) throws ProcessingFaultException;
    }

    public static class LoanProcessRequest {
        private String applicantName;
        private String ssn;
        private double loanAmount;
        private double monthlyIncome;
        private double monthlyDebt;
        private double propertyValue;
        private byte[] documentData;

        public String getApplicantName() { return applicantName; }
        public void setApplicantName(String applicantName) { this.applicantName = applicantName; }
        public String getSsn() { return ssn; }
        public void setSsn(String ssn) { this.ssn = ssn; }
        public double getLoanAmount() { return loanAmount; }
        public void setLoanAmount(double loanAmount) { this.loanAmount = loanAmount; }
        public double getMonthlyIncome() { return monthlyIncome; }
        public void setMonthlyIncome(double monthlyIncome) { this.monthlyIncome = monthlyIncome; }
        public double getMonthlyDebt() { return monthlyDebt; }
        public void setMonthlyDebt(double monthlyDebt) { this.monthlyDebt = monthlyDebt; }
        public double getPropertyValue() { return propertyValue; }
        public void setPropertyValue(double propertyValue) { this.propertyValue = propertyValue; }
        public byte[] getDocumentData() { return documentData; }
        public void setDocumentData(byte[] documentData) { this.documentData = documentData; }
    }

    public static class LoanProcessResponse {
        private String loanId;
        private String status;
        private String decision;
        private String decisionNotes;
        private double dtiRatio;
        private double ltvRatio;
        private int riskScore;
        private double monthlyPayment;
        private String documentClassification;

        public String getLoanId() { return loanId; }
        public void setLoanId(String loanId) { this.loanId = loanId; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getDecision() { return decision; }
        public void setDecision(String decision) { this.decision = decision; }
        public String getDecisionNotes() { return decisionNotes; }
        public void setDecisionNotes(String decisionNotes) { this.decisionNotes = decisionNotes; }
        public double getDtiRatio() { return dtiRatio; }
        public void setDtiRatio(double dtiRatio) { this.dtiRatio = dtiRatio; }
        public double getLtvRatio() { return ltvRatio; }
        public void setLtvRatio(double ltvRatio) { this.ltvRatio = ltvRatio; }
        public int getRiskScore() { return riskScore; }
        public void setRiskScore(int riskScore) { this.riskScore = riskScore; }
        public double getMonthlyPayment() { return monthlyPayment; }
        public void setMonthlyPayment(double monthlyPayment) { this.monthlyPayment = monthlyPayment; }
        public String getDocumentClassification() { return documentClassification; }
        public void setDocumentClassification(String documentClassification) { this.documentClassification = documentClassification; }
    }

    public static class ProcessingFaultException extends Exception {
        private String errorCode;
        private String errorMessage;

        public ProcessingFaultException(String message) { super(message); }
        public String getErrorCode() { return errorCode; }
        public String getErrorMessage() { return errorMessage; }
    }
}
