package com.dottec.pdi.project.pdi.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.dottec.pdi.project.pdi.dao.DashboardDAO;
import com.dottec.pdi.project.pdi.utils.ExportUtils;

/**
 * CONTROLADOR DE EXPORTAÇÃO DE DADOS
 * 
 * RESPONSABILIDADE: Orquestrar a geração e exportação de relatórios CSV
 * 
 * FUNCIONAMENTO:
 * 1. Recebe dados do DashboardDAO em diversos formatos
 * 2. Utiliza ExportUtils para converter em CSV
 * 3. Exporta para arquivos no sistema de arquivos
 * 
 * FORMATOS SUPORTADOS:
 * - Map<String, Object[]>  → Dados completos (chave + array de valores)
 * - Map<String, Number>    → Dados numéricos (percentuais, totais)  
 * - Map<String, Object>    → Objetos variados
 * - List<Object[]>         → Listas de dados tabulares
 * 
 * RELATÓRIOS DISPONÍVEIS:
 * - sendConsolidatedCSV()       → Relatório geral do sistema
 * - sendDepartmentConsolidatedCSV() → Relatório específico por departamento
 * - Métodos individuais para cada tipo de dado
 * 
 * EXEMPLOS DE USO:
 * // Relatório geral
 * ExportDataController.sendConsolidatedCSV("relatorio_geral.csv");
 * 
 * // Relatório departamental  
 * ExportDataController.sendDepartmentConsolidatedCSV("departamento_1.csv", 1);
 * 
 * // Exportação específica
 * ExportDataController.sendCSVFile("status.csv", dados, new String[]{"Status", "Qtd", "%"});
 * 
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
}