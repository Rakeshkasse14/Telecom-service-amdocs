package com.amdocs.telecom.scheduler;

import com.amdocs.telecom.report.ReportExporter;
import com.amdocs.telecom.service.ReportService;
import com.amdocs.telecom.service.impl.ReportServiceImpl;

import java.util.concurrent.*;

/**
 * Demonstrates async report generation using ExecutorService, Callable, Future, and File I/O.
 */
public class ReportGenerator implements Callable<String> {

    private final ReportService reportService = new ReportServiceImpl();

    @Override
    public String call() throws Exception {
        System.out.println("[ASYNC REPORT GENERATOR] Starting report computation on background thread...");
        Thread.sleep(1000); // Simulate analytical computation work

        String reportText = reportService.generateFullReportText();
        String csvFileName = "telecom_report_" + System.currentTimeMillis() + ".csv";
        String txtFileName = "telecom_report_" + System.currentTimeMillis() + ".txt";

        ReportExporter.exportToTXT(reportText, txtFileName);
        ReportExporter.exportToCSV(reportService, csvFileName);

        return "Report successfully generated asynchronously!\nSaved TXT: " + txtFileName + "\nSaved CSV: " + csvFileName;
    }

    public static Future<String> generateAsyncReport() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(new ReportGenerator());
        executor.shutdown();
        return future;
    }
}
