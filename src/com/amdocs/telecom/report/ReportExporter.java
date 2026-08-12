package com.amdocs.telecom.report;

import com.amdocs.telecom.service.ReportService;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ReportExporter {

    public static void exportToTXT(String content, String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.write(content);
        }
    }

    public static void exportToCSV(ReportService reportService, String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("MetricCategory,KeyName,ValueCount");

            reportService.getTicketsByStatus().forEach((k, v) ->
                    writer.println("TicketsByStatus," + k + "," + v));

            reportService.getTicketsByPriority().forEach((k, v) ->
                    writer.println("TicketsByPriority," + k + "," + v));

            reportService.getEngineerWorkload().forEach((k, v) ->
                    writer.println("EngineerWorkload," + k + "," + v));

            reportService.getSlaBreachAnalysis().forEach((k, v) ->
                    writer.println("SLABreachAnalysis," + k + "," + v));

            reportService.getTopIncidentCategories().forEach((k, v) ->
                    writer.println("IncidentCategories," + k + "," + v));
        }
    }
}
