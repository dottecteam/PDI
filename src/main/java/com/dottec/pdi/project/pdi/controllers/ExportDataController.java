package com.dottec.pdi.project.pdi.controllers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.dottec.pdi.project.pdi.dao.DashboardDAO;
import com.dottec.pdi.project.pdi.utils.ExportUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/**
 * CONTROLADOR DE EXPORTAÇÃO DE DADOS
 * <p>
 * RESPONSABILIDADE: Orquestrar a geração e exportação de relatórios CSV
 * <p>
 * FUNCIONAMENTO:
 * 1. Recebe dados do DashboardDAO em diversos formatos
 * 2. Utiliza ExportUtils para converter em CSV
 * 3. Exporta para arquivos no sistema de arquivos
 * <p>
 * FORMATOS SUPORTADOS:
 * - Map<String, Object[]>  → Dados completos (chave + array de valores)
 * - Map<String, Number>    → Dados numéricos (percentuais, totais)
 * - Map<String, Object>    → Objetos variados
 * - List<Object[]>         → Listas de dados tabulares
 * <p>
 * RELATÓRIOS DISPONÍVEIS:
 * - sendConsolidatedCSV()       → Relatório geral do sistema
 * - sendDepartmentConsolidatedCSV() → Relatório específico por departamento
 * - Métodos individuais para cada tipo de dado
 * <p>
 * EXEMPLOS DE USO:
 * // Relatório geral
 * ExportDataController.sendConsolidatedCSV("relatorio_geral.csv");
 * <p>
 * // Relatório departamental
 * ExportDataController.sendDepartmentConsolidatedCSV("departamento_1.csv", 1);
 * <p>
 * // Exportação específica
 * ExportDataController.sendCSVFile("status.csv", dados, new String[]{"Status", "Qtd", "%"});
 * <p>
 * OBS: Os filtros são aplicados anteriormente nos dados fornecidos ao controlador
 */

public class ExportDataController {

    // Para Map<String, Object[]> com cabeçalhos
    public static void sendCSVFile(String path, Map<String, Object[]> entry, String[] headers) throws IOException {
        ExportUtils.exportToCSV(ExportUtils.converStringToPath(path),
                ExportUtils.createCSV(entry, headers));
    }

    // Para Map<String, Object[]> SEM cabeçalhos
    public static void sendCSVFile(String path, Map<String, Object[]> entry) throws IOException {
        ExportUtils.exportToCSV(ExportUtils.converStringToPath(path),
                ExportUtils.createCSV(entry, null));
    }

    // Para Map<String, Number> (percentuais, totais)
    public static void sendCSVFileFromNumbers(String path, Map<String, ? extends Number> map) throws IOException {
        ExportUtils.exportToCSV(ExportUtils.converStringToPath(path),
                ExportUtils.createCSVFromNumberMap(map));
    }

    // Para Map<String, Object> (dados variados)
    public static void sendCSVFileFromObjects(String path, Map<String, Object> map) throws IOException {
        ExportUtils.exportToCSV(ExportUtils.converStringToPath(path),
                ExportUtils.createCSVFromObjectMap(map));
    }

    // Para List<Object[]> com cabeçalhos
    public static void sendCSVFileFromList(String path, List<Object[]> list, String[] headers) throws IOException {
        ExportUtils.exportToCSV(ExportUtils.converStringToPath(path),
                ExportUtils.createCSVFromList(list, headers));
    }

    // Para List<Object[]> SEM cabeçalhos
    public static void sendCSVFileFromList(String path, List<Object[]> list) throws IOException {
        ExportUtils.exportToCSV(ExportUtils.converStringToPath(path),
                ExportUtils.createCSVFromList(list, null));
    }

    //Para um relatorio geral do Sistema
    public static void sendConsolidatedCSV(String path) throws IOException, SQLException {
        StringBuilder consolidatedCSV = new StringBuilder();

        consolidatedCSV.append("sep=,\n");
        consolidatedCSV.append("RELATÓRIO GERAL - SISTEMA PDI\n\n");

        consolidatedCSV.append("=== STATUS DAS ATIVIDADES ===\n");
        Map<String, Object[]> statusData = DashboardDAO.getStatusList();
        consolidatedCSV.append(createSectionCSV(statusData,
                new String[]{"Status", "Quantidade", "Percentual"}));
        consolidatedCSV.append("\n");

        consolidatedCSV.append("=== TAGS MAIS UTILIZADAS ===\n");
        Map<String, Object[]> tagsData = DashboardDAO.getTagNameUsagePercentualMap();
        consolidatedCSV.append(createSectionCSV(tagsData,
                new String[]{"Tag", "Quantidade", "Percentual"}));
        consolidatedCSV.append("\n");

        consolidatedCSV.append("=== DISTRIBUIÇÃO DE SKILLS ===\n");
        Map<String, Object[]> skillsData = DashboardDAO.getSkillsDistribution();
        consolidatedCSV.append(createSectionCSV(skillsData,
                new String[]{"Tipo Skill", "Quantidade", "Percentual"}));
        consolidatedCSV.append("\n");

        consolidatedCSV.append("=== PERFORMANCE POR DEPARTAMENTO ===\n");
        Map<String, Object[]> deptData = DashboardDAO.getDepartmentPerformance();
        consolidatedCSV.append(createSectionCSV(deptData,
                new String[]{"Departamento", "Progresso Médio", "% Concluídas"}));
        consolidatedCSV.append("\n");

        consolidatedCSV.append("=== TOP DEPARTAMENTOS (PDIs CONCLUÍDOS) ===\n");
        Map<String, Integer> topDeptData = DashboardDAO.getTopDepartmentsCompletedPDIs();
        consolidatedCSV.append(createSimpleSectionCSV(topDeptData,
                new String[]{"Departamento", "PDIs Concluídos"}));
        consolidatedCSV.append("\n");

        consolidatedCSV.append("=== MÉTRICAS GERAIS ===\n");
        Double progressoMedio = DashboardDAO.getAveragePDIProgress();
        consolidatedCSV.append("Progresso Médio do PDI,").append(progressoMedio).append("%\n\n");

        ExportUtils.exportToCSV(ExportUtils.converStringToPath(path), consolidatedCSV.toString());
    }

    // Método auxiliar para seções com Map<String, Object[]>
    private static String createSectionCSV(Map<String, Object[]> data, String[] headers) {
        if (data.isEmpty()) return "Nenhum dado disponível\n";

        StringBuilder section = new StringBuilder();
        for (int i = 0; i < headers.length; i++) {
            if (i > 0) section.append(",");
            section.append(ExportUtils.escapeCSV(headers[i]));
        }
        section.append("\n");

        for (Map.Entry<String, Object[]> entry : data.entrySet()) {
            section.append(ExportUtils.escapeCSV(entry.getKey()));
            for (Object value : entry.getValue()) {
                section.append(",").append(ExportUtils.escapeCSV(value != null ? value.toString() : ""));
            }
            section.append("\n");
        }
        return section.toString();
    }

    // Método auxiliar para seções com Map<String, Integer>
    private static String createSimpleSectionCSV(Map<String, Integer> data, String[] headers) {
        if (data.isEmpty()) return "Nenhum dado disponível\n";

        StringBuilder section = new StringBuilder();
        for (int i = 0; i < headers.length; i++) {
            if (i > 0) section.append(",");
            section.append(ExportUtils.escapeCSV(headers[i]));
        }
        section.append("\n");

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            section.append(ExportUtils.escapeCSV(entry.getKey()))
                    .append(",")
                    .append(entry.getValue())
                    .append("\n");
        }
        return section.toString();
    }

    // Método para consolidado de um departamento específico
    public static void sendDepartmentConsolidatedCSV(String path, int departmentId) throws IOException, SQLException {
        StringBuilder consolidatedCSV = new StringBuilder();

        consolidatedCSV.append("sep=,\n");
        consolidatedCSV.append("RELATÓRIO GERAL - DEPARTAMENTO ID: ").append(departmentId).append("\n\n");

        consolidatedCSV.append("=== TAGS MAIS UTILIZADAS NO DEPARTAMENTO ===\n");
        Map<String, Integer> tagsData = DashboardDAO.getTopDepartmentTags(departmentId);
        consolidatedCSV.append(createSimpleSectionCSV(tagsData,
                new String[]{"Tag", "Quantidade"}));
        consolidatedCSV.append("\n");

        consolidatedCSV.append("=== METAS COM BAIXO PROGRESSO ===\n");
        List<Object[]> lowProgressData = DashboardDAO.getLowestProgressGoals(departmentId);
        consolidatedCSV.append(createListSectionCSV(lowProgressData,
                new String[]{"Objetivo", "Responsável", "Progresso (%)"}));
        consolidatedCSV.append("\n");

        consolidatedCSV.append("=== HISTÓRICO DE PROGRESSO MENSAL ===\n");
        Map<String, Double> progressHistory = DashboardDAO.getDepartmentProgressHistory(departmentId);
        consolidatedCSV.append(createProgressHistoryCSV(progressHistory));
        consolidatedCSV.append("\n");

        consolidatedCSV.append("=== TAXA DE CONCLUSÃO DO DEPARTAMENTO ===\n");
        Map<String, Object> completionRate = DashboardDAO.getDepartmentCompletionRate(departmentId);
        consolidatedCSV.append(createCompletionRateCSV(completionRate));
        consolidatedCSV.append("\n");

        ExportUtils.exportToCSV(ExportUtils.converStringToPath(path), consolidatedCSV.toString());
    }

    // Para List<Object[]> (metas com baixo progresso)
    private static String createListSectionCSV(List<Object[]> data, String[] headers) {
        if (data.isEmpty()) return "Nenhum dado disponível\n";

        StringBuilder section = new StringBuilder();
        for (int i = 0; i < headers.length; i++) {
            if (i > 0) section.append(",");
            section.append(ExportUtils.escapeCSV(headers[i]));
        }
        section.append("\n");

        for (Object[] row : data) {
            for (int i = 0; i < row.length; i++) {
                if (i > 0) section.append(",");
                section.append(ExportUtils.escapeCSV(row[i] != null ? row[i].toString() : ""));
            }
            section.append("\n");
        }
        return section.toString();
    }

    // Para Map<String, Double> (histórico de progresso)
    private static String createProgressHistoryCSV(Map<String, Double> data) {
        if (data.isEmpty()) return "Nenhum dado disponível\n";

        StringBuilder section = new StringBuilder();
        section.append("Mês,Progresso Médio (%)\n");

        for (Map.Entry<String, Double> entry : data.entrySet()) {
            section.append(ExportUtils.escapeCSV(entry.getKey()))
                    .append(",")
                    .append(entry.getValue())
                    .append("\n");
        }
        return section.toString();
    }

    // Para Map<String, Object> (taxa de conclusão)
    private static String createCompletionRateCSV(Map<String, Object> data) {
        if (data.isEmpty()) return "Nenhum dado disponível\n";

        StringBuilder section = new StringBuilder();
        section.append("Métrica,Valor\n");
        section.append("Total de Atividades,").append(data.get("total")).append("\n");
        section.append("Atividades Concluídas,").append(data.get("concluidas")).append("\n");
        section.append("Taxa de Conclusão,").append(data.get("taxa")).append("%\n");

        return section.toString();
    }

    // =========== PARA XLSX =============

    public static void sendConsolidatedXLSX(String path) throws IOException, SQLException {
        try (Workbook workbook = new XSSFWorkbook()) {

            Map<String, Object[]> statusData = DashboardDAO.getStatusList();
            String[] statusHeaders = {"Status", "Quantidade", "Percentual"};
            createSheetFromMap(workbook, "Status das Atividades", statusHeaders, statusData);

            Map<String, Object[]> tagsData = DashboardDAO.getTagNameUsagePercentualMap();
            String[] tagsHeaders = {"Tag", "Quantidade", "Percentual"};
            createSheetFromMap(workbook, "Tags Mais Usadas", tagsHeaders, tagsData);

            Map<String, Object[]> skillsData = DashboardDAO.getSkillsDistribution();
            String[] skillsHeaders = {"Tipo Skill", "Quantidade", "Percentual"};
            createSheetFromMap(workbook, "Distribuição de Skills", skillsHeaders, skillsData);

            Map<String, Object[]> deptData = DashboardDAO.getDepartmentPerformance();
            String[] deptHeaders = {"Departamento", "Progresso Médio", "% Concluídas"};
            createSheetFromMap(workbook, "Performance Dept", deptHeaders, deptData);

            Map<String, Integer> topDeptData = DashboardDAO.getTopDepartmentsCompletedPDIs();
            String[] topDeptHeaders = {"Departamento", "PDIs Concluídos"};
            createSimpleSheetFromIntegerMap(workbook, "Top Depts (PDIs)", topDeptHeaders, topDeptData);

            Double progressoMedio = DashboardDAO.getAveragePDIProgress();
            createSummarySheet(workbook, progressoMedio);

            try (FileOutputStream fileOut = new FileOutputStream(path)) {
                workbook.write(fileOut);
            }
        }
    }

    public static void sendDepartmentConsolidatedXLSX(String path, int departmentId) throws IOException, SQLException {
        try (Workbook workbook = new XSSFWorkbook()) {

            Map<String, Integer> tagsData = DashboardDAO.getTopDepartmentTags(departmentId);
            String[] tagsHeaders = {"Tag", "Quantidade"};
            createSimpleSheetFromIntegerMap(workbook, "Tags do Depto", tagsHeaders, tagsData);

            List<Object[]> lowProgressData = DashboardDAO.getLowestProgressGoals(departmentId);
            String[] lowProgressHeaders = {"Objetivo", "Responsável", "Progresso (%)"};
            createSheetFromList(workbook, "Baixo Progresso", lowProgressHeaders, lowProgressData);

            Map<String, Double> progressHistory = DashboardDAO.getDepartmentProgressHistory(departmentId);
            String[] historyHeaders = {"Mês", "Progresso Médio (%)"};
            createProgressHistorySheet(workbook, progressHistory, historyHeaders);

            Map<String, Object> completionRate = DashboardDAO.getDepartmentCompletionRate(departmentId);
            createCompletionRateSheet(workbook, completionRate);

            try (FileOutputStream fileOut = new FileOutputStream(path)) {
                workbook.write(fileOut);
            }
        }
    }

    private static void createSheetFromMap(Workbook workbook, String sheetName, String[] headers, Map<String, Object[]> data) {
        Sheet sheet = workbook.createSheet(sheetName);
        AtomicInteger rowNum = new AtomicInteger(0);

        Row headerRow = sheet.createRow(rowNum.getAndIncrement());
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        data.forEach((key, values) -> {
            Row row = sheet.createRow(rowNum.getAndIncrement());
            int colNum = 0;

            row.createCell(colNum++).setCellValue(key);

            for (Object value : values) {
                Cell cell = row.createCell(colNum++);
                if (value instanceof Number) {
                    cell.setCellValue(((Number) value).doubleValue());
                } else {
                    cell.setCellValue(value != null ? value.toString() : "");
                }
            }
        });

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private static void createSheetFromList(Workbook workbook, String sheetName, String[] headers, List<Object[]> data) {
        Sheet sheet = workbook.createSheet(sheetName);
        AtomicInteger rowNum = new AtomicInteger(0);

        Row headerRow = sheet.createRow(rowNum.getAndIncrement());
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        for (Object[] rowData : data) {
            Row row = sheet.createRow(rowNum.getAndIncrement());
            for (int i = 0; i < rowData.length; i++) {
                Cell cell = row.createCell(i);
                Object value = rowData[i];
                if (value instanceof Number) {
                    cell.setCellValue(((Number) value).doubleValue());
                } else {
                    cell.setCellValue(value != null ? value.toString() : "");
                }
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }


    private static void createSimpleSheetFromIntegerMap(Workbook workbook, String sheetName, String[] headers, Map<String, Integer> data) {
        Sheet sheet = workbook.createSheet(sheetName);
        AtomicInteger rowNum = new AtomicInteger(0);

        Row headerRow = sheet.createRow(rowNum.getAndIncrement());
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        data.forEach((key, value) -> {
            Row row = sheet.createRow(rowNum.getAndIncrement());
            row.createCell(0).setCellValue(key);
            row.createCell(1).setCellValue(value);
        });

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private static void createProgressHistorySheet(Workbook workbook, Map<String, Double> data, String[] headers) {
        Sheet sheet = workbook.createSheet("Histórico Progresso");
        AtomicInteger rowNum = new AtomicInteger(0);

        Row headerRow = sheet.createRow(rowNum.getAndIncrement());
        headerRow.createCell(0).setCellValue(headers[0]);
        headerRow.createCell(1).setCellValue(headers[1]);

        data.forEach((key, value) -> {
            Row row = sheet.createRow(rowNum.getAndIncrement());
            row.createCell(0).setCellValue(key);
            row.createCell(1).setCellValue(value);
        });

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }


    private static void createSummarySheet(Workbook workbook, Double averageProgress) {
        Sheet sheet = workbook.createSheet("Métricas Gerais");

        Row row1 = sheet.createRow(0);
        row1.createCell(0).setCellValue("Métrica");
        row1.createCell(1).setCellValue("Valor");

        Row row2 = sheet.createRow(1);
        row2.createCell(0).setCellValue("Progresso Médio do PDI");
        row2.createCell(1).setCellValue(averageProgress + "%");

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private static void createCompletionRateSheet(Workbook workbook, Map<String, Object> data) {
        Sheet sheet = workbook.createSheet("Taxa de Conclusão");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Métrica");
        headerRow.createCell(1).setCellValue("Valor");

        int rowNum = 1;
        Row row1 = sheet.createRow(rowNum++);
        row1.createCell(0).setCellValue("Total de Atividades");
        row1.createCell(1).setCellValue(data.get("total").toString());

        Row row2 = sheet.createRow(rowNum++);
        row2.createCell(0).setCellValue("Atividades Concluídas");
        row2.createCell(1).setCellValue(data.get("concluidas").toString());

        Row row3 = sheet.createRow(rowNum++);
        row3.createCell(0).setCellValue("Taxa de Conclusão");
        row3.createCell(1).setCellValue(data.get("taxa").toString() + "%");

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }
}