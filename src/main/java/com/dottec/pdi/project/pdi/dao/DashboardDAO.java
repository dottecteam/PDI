package com.dottec.pdi.project.pdi.dao;

import java.sql.*;

import com.dottec.pdi.project.pdi.config.Database;

import com.dottec.pdi.project.pdi.controllers.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dottec.pdi.project.pdi.enums.ActivityStatus;
import com.dottec.pdi.project.pdi.enums.GoalStatus;
import com.dottec.pdi.project.pdi.enums.TagType;
import com.dottec.pdi.project.pdi.model.Tag;

public class DashboardDAO {

    private static Connection getConnection() throws SQLException {
        return Database.getConnection();
    }

    // --- MÉTODOS AUXILIARES PARA FILTROS ---
    private static String buildGoalStatusWhereClause(List<GoalStatus> statuses) {
        if (statuses == null || statuses.isEmpty() || statuses.size() == GoalStatus.values().length) {
            return "1=1"; // Retorna true se nenhum filtro foi aplicado ou todos estão selecionados
        }
        String statusList = statuses.stream()
                .map(s -> "'" + s.name() + "'")
                .collect(Collectors.joining(","));
        return "G.goa_status IN (" + statusList + ")";
    }

    private static String buildTagWhereClause(List<Tag> tags, List<TagType> tagTypes) {
        String tagTypeWhere = "1=1";
        if (tagTypes != null && !tagTypes.isEmpty() && tagTypes.size() != TagType.values().length) {
            String typeList = tagTypes.stream()
                    .map(t -> "'" + t.name() + "'")
                    .collect(Collectors.joining(","));
            tagTypeWhere = "T.tag_type IN (" + typeList + ")";
        }

        // Se houver tags específicas selecionadas, elas têm precedência.
        if (tags != null && !tags.isEmpty() && tags.size() != TagController.findAllTags().size()) {
            String tagIdList = tags.stream()
                    .map(t -> String.valueOf(t.getId()))
                    .collect(Collectors.joining(","));
            return "T.tag_id IN (" + tagIdList + ")";
        }

        return tagTypeWhere;
    }


    /**
     * Retorna um Map com o percentual de atividades por status
     * Formato: { "status1": percentual1, "status2": percentual2, ... }
     * Exemplo: { "completed": 45.5, "in_progress": 30.2, "pending": 24.3 }
     */
    public static Map<String, Double> getPercentualStatusMap() throws SQLException {
        String sql = """
				SELECT
					act_status,
						ROUND((COUNT(*) * 100.0 / (SELECT COUNT(*) FROM activities)), 2) as percentual
						FROM activities
						GROUP BY act_status
						ORDER BY percentual DESC
				""";

        Map<String, Double> percentualMap = new HashMap<>();

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String status = rs.getString("act_status");
                double percentual = rs.getDouble("percentual");

                percentualMap.put(status, percentual);
            }
        }

        return percentualMap;
    }

    /**
     * Retorna um Map com o total de atividades por status
     * Formato: { "status1": total1, "status2": total2, ... }
     * Exemplo: { "completed": 45, "in_progress": 30, "pending": 25 }
     */
    public static Map<String, Integer> getTotalStatusMap() throws SQLException {
        String sql = """
					SELECT act_status,
						 COUNT(*) as total FROM activities
						 GROUP BY act_status
						 ORDER BY total
				""";

        Map<String, Integer> totalMap = new HashMap<>();

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String status = rs.getString("act_status");
                int total = rs.getInt("total");

                totalMap.put(status, total);
            }
        }
        return totalMap;
    }

    /**
     * Retorna um Map com detalhes completos por status
     * Formato: { "status": [quantidade, percentual] }
     * Exemplo: { "completed": [45, 45.5], "in_progress": [30, 30.2] }
     */
    public static Map<String, Object[]> getStatusList() throws SQLException {
        String sql = """
				SELECT
				    act_status,
				    COUNT(*) as quantidade,
				    ROUND((COUNT(*) * 100.0 / (SELECT COUNT(*) FROM activities)), 2) as percentual
				FROM activities
				GROUP BY act_status
				ORDER BY percentual DESC
				""";
        Map<String, Object[]> resultado = new HashMap<>();
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String status = rs.getString("act_status");
                int quantidade = rs.getInt("quantidade");
                double percentual = rs.getDouble("percentual");

                resultado.put(status, new Object[] { quantidade, percentual });
            }
        }
        return resultado;
    }

    /**
     * Retorna o percentual de atividades para um status específico
     * Formato: Double (percentual)
     * Exemplo: 45.5
     */
    public static Double getPercentualStatus(ActivityStatus status) throws SQLException {
        String sql = """
				SELECT act_status,
					ROUND((COUNT(*) * 100.0 / (SELECT COUNT(*) FROM activities)), 2) as percentual
					FROM activities WHERE act_status = ?
				""";
        try (Connection connection = getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("percentual");
            }
            return 0.0;
        }
    }

    /**
     * Retorna o total de atividades para um status específico
     * Formato: int (quantidade)
     * Exemplo: 45
     */
    public static int getTotalStatus(ActivityStatus status) throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM activities WHERE act_status = ?";

        try (Connection connection = getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
            return 0;
        }
    }

    /**
     * Retorna a média de progresso do PDI
     * Formato: Double (percentual médio)
     * Exemplo: 65.8
     */
    public static Double getAveragePDIProgress() throws SQLException {
        String sql = """
			        SELECT ROUND(AVG(
			            CASE 
			                WHEN goa_status = 'completed' THEN 100
			                WHEN goa_status = 'in_progress' THEN 50
			                WHEN goa_status = 'pending' THEN 0
			                ELSE 0
			            END
			        ), 2) as average_pdi_progress
			        FROM goals 
			        WHERE goa_status != 'canceled'
			        """;

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("average_pdi_progress");
            }
            return 0.0;
        }
    }

    /**
     * Retorna um Map com o uso percentual de cada tag
     * Formato: { "tag_name": [quantidade_activities, percentual] }
     * Exemplo: { "Java": [25, 25.5], "Spring": [20, 20.4] }
     */
    public static Map<String, Object[]> getTagNameUsagePercentualMap() throws SQLException {
        String sql = """
				SELECT
				    t.tag_name,
				    COUNT(DISTINCT a.act_id) as quantidade_activities,
				    ROUND((COUNT(DISTINCT a.act_id) * 100.0 / (SELECT COUNT(*) FROM activities)), 2) as percentual
				FROM tags t
				INNER JOIN activity_tags at ON t.tag_id = at.tag_id
				INNER JOIN activities a ON at.activity_id = a.act_id
				GROUP BY t.tag_name
				ORDER BY percentual DESC
				""";

        Map<String, Object[]> resultado = new HashMap<>();

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String tagName = rs.getString("tag_name");
                int quantidade = rs.getInt("quantidade_activities");
                double percentual = rs.getDouble("percentual");

                resultado.put(tagName, new Object[] { quantidade, percentual });
            }
        }
        return resultado;
    }

    /**
     * Retorna a distribuição entre Soft Skills e Hard Skills
     * Formato: { "tipo_skill": [quantidade, percentual] }
     * Exemplo: { "Soft Skills": [60, 60.0], "Hard Skills": [40, 40.0] }
     * * SOBRECARREGADO para aceitar filtros de TagType (Skills)
     */
    public static Map<String, Object[]> getSkillsDistribution(List<TagType> tagTypes) throws SQLException {
        String tagTypeWhere = buildTagWhereClause(null, tagTypes);

        String sql = String.format("""
				SELECT
					CASE
					    WHEN t.tag_type = 'SOFT' THEN 'Soft Skills'
					    WHEN t.tag_type = 'HARD' THEN 'Hard Skills'
					    ELSE 'Outras'
					END as tipo_skill,
				    COUNT(DISTINCT a.act_id) as quantidade,
				    ROUND((COUNT(DISTINCT a.act_id) * 100.0 / (SELECT COUNT(*) FROM activities)), 2) as percentual
				FROM activities a
				INNER JOIN activity_tags at ON a.act_id = at.activity_id
				INNER JOIN tags t ON at.tag_id = t.tag_id
				WHERE %s
				GROUP BY tipo_skill
				ORDER BY percentual DESC
				""", tagTypeWhere);

        Map<String, Object[]> resultado = new HashMap<>();
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String tipo = rs.getString("tipo_skill");
                int quantidade = rs.getInt("quantidade");
                double percentual = rs.getDouble("percentual");
                resultado.put(tipo, new Object[] { quantidade, percentual });
            }
        }
        return resultado;
    }

    // Mantenha o método original para compatibilidade com o Controller
    public static Map<String, Object[]> getSkillsDistribution() throws SQLException {
        return getSkillsDistribution(null);
    }


    /**
     * Retorna o desempenho por departamento
     * Formato: { "departamento": [progresso_medio, percentual_concluidas] }
     * Exemplo: { "TI": [75.5, 70.2], "RH": [60.3, 55.8] }
     */
    public static Map<String, Object[]> getDepartmentPerformance() throws SQLException {
        String sql = """
				SELECT
				    d.dep_name as setor,
				    ROUND(AVG(
				        CASE
				            WHEN a.act_status = 'completed' THEN 100
				            WHEN a.act_status = 'in_progress' THEN 50
				            WHEN a.act_status = 'pending' THEN 0
				            ELSE 0
				        END
				    ), 2) as avarage_progress,
				    ROUND((SUM(CASE WHEN a.act_status = 'completed' THEN 1 ELSE 0 END) * 100.0 / COUNT(*)), 2) as percentual_completed
				FROM departments d
				LEFT JOIN collaborators c ON d.dep_id = c.department_id
				LEFT JOIN goals g ON c.col_id = g.collaborator_id
				LEFT JOIN activities a ON g.goa_id = a.goal_id
				WHERE d.dep_status = 'active'
				GROUP BY d.dep_id, d.dep_name
				ORDER BY avarage_progress DESC
				""";

        Map<String, Object[]> resultado = new HashMap<>();
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String setor = rs.getString("setor");
                double progresso = rs.getDouble("avarage_progress");
                double concluidas = rs.getDouble("percentual_completed");
                resultado.put(setor, new Object[] { progresso, concluidas });
            }
        }
        return resultado;
    }

    /**
     * Retorna os top 5 departamentos com mais PDIs concluídos
     * Formato: { "departamento": quantidade_pdis_concluidos }
     * Exemplo: { "TI": 15, "Vendas": 12, "Marketing": 10 }
     */
    public static Map<String, Integer> getTopDepartmentsCompletedPDIs() throws SQLException {
        String sql = """
				SELECT
				    d.dep_name as setor,
				    COUNT(DISTINCT g.goa_id) as completed_pdis
				FROM departments d
				LEFT JOIN collaborators c ON d.dep_id = c.department_id
				LEFT JOIN goals g ON c.col_id = g.collaborator_id
				WHERE g.goa_status = 'completed'
				GROUP BY d.dep_id, d.dep_name
				ORDER BY completed_pdis DESC
				LIMIT 5
				""";

        Map<String, Integer> resultado = new HashMap<>();
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String setor = rs.getString("setor");
                int concluidos = rs.getInt("completed_pdis");
                resultado.put(setor, concluidos);
            }
        }
        return resultado;
    }

    /**
     * Retorna as top 10 tags mais utilizadas em um departamento
     * Formato: { "tag_name": quantidade }
     * Exemplo: { "Java": 25, "Spring": 20, "SQL": 15 }
     */
    public static Map<String, Integer> getTopDepartmentTags(int departmentId) throws SQLException {
        String sql = """
				SELECT
				    t.tag_name,
				    COUNT(DISTINCT a.act_id) as quantidade
				FROM tags t
				INNER JOIN activity_tags at ON t.tag_id = at.tag_id
				INNER JOIN activities a ON at.activity_id = a.act_id
				INNER JOIN goals g ON a.goal_id = g.goa_id
				INNER JOIN collaborators c ON g.collaborator_id = c.col_id
				WHERE c.department_id = ?
				GROUP BY t.tag_id, t.tag_name
				ORDER BY quantidade DESC
				LIMIT 10
				""";

        Map<String, Integer> resultado = new HashMap<>();
        try (Connection connection = getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, departmentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String tag = rs.getString("tag_name");
                int quantidade = rs.getInt("quantidade");
                resultado.put(tag, quantidade);
            }
        }
        return resultado;
    }

    /**
     * Retorna lista das 10 metas com menor progresso em um departamento
     * Formato: List<Object[]> onde cada array contém [objetivo, responsavel, progresso_percentual]
     * Exemplo: [ ["Objetivo A", "João Silva", 25.5], ["Objetivo B", "Maria Santos", 30.2] ]
     */
    public static List<Object[]> getLowestProgressGoals(int departmentId) throws SQLException {
        String sql = """
				SELECT
				    g.goa_name as objetivo,
				    c.col_name as responsavel,
				    ROUND((SUM(
				        CASE
				            WHEN a.act_status = 'completed' THEN 100
				            WHEN a.act_status = 'in_progress' THEN 50
				            WHEN a.act_status = 'pending' THEN 0
				            ELSE 0
				        END
				    ) / COUNT(a.act_id)), 2) as progresso_percentual
				FROM goals g
				INNER JOIN collaborators c ON g.collaborator_id = c.col_id
				LEFT JOIN activities a ON g.goa_id = a.goal_id
				WHERE c.department_id = ? AND g.goa_status != 'canceled'
				GROUP BY g.goa_id, g.goa_name, c.col_name
				ORDER BY progresso_percentual ASC
				LIMIT 10
				""";

        List<Object[]> resultado = new ArrayList<>();
        try (Connection connection = getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, departmentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String objetivo = rs.getString("objetivo");
                String responsavel = rs.getString("responsavel");
                double progresso = rs.getDouble("progresso_percentual");
                resultado.add(new Object[] { objetivo, responsavel, progresso });
            }
        }
        return resultado;
    }

    /**
     * Retorna histórico mensal de progresso de um departamento
     * Formato: { "YYYY-MM": progresso_medio }
     * Exemplo: { "2024-01": 45.5, "2024-02": 60.2, "2024-03": 75.8 }
     */
    public static Map<String, Double> getDepartmentProgressHistory(int departmentId) throws SQLException {
        String sql = """
				SELECT
				    DATE_FORMAT(h.his_changed_at, '%Y-%m') as mes,
				    ROUND(AVG(
				        CASE
				            WHEN h.his_new_status = 'completed' THEN 100
				            WHEN h.his_new_status = 'in_progress' THEN 50
				            WHEN h.his_new_status = 'pending' THEN 0
				            ELSE 0
				        END
				    ), 2) as progresso_medio
				FROM progress_history h
				INNER JOIN goals g ON h.his_entity_id = g.goa_id AND h.his_entity_type = 'goal'
				INNER JOIN collaborators c ON g.collaborator_id = c.col_id
				WHERE c.department_id = ?
				GROUP BY DATE_FORMAT(h.his_changed_at, '%Y-%m')
				ORDER BY mes
				""";

        Map<String, Double> resultado = new HashMap<>();
        try (Connection connection = getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, departmentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String mes = rs.getString("mes");
                double progresso = rs.getDouble("progresso_medio");
                resultado.put(mes, progresso);
            }
        }
        return resultado;
    }

    /**
     * Retorna taxa de conclusão de um departamento
     * Formato: { "total": total_atividades, "concluidas": atividades_concluidas, "taxa": percentual_conclusao }
     * Exemplo: { "total": 150, "concluidas": 90, "taxa": 60.0 }
     */
    public static Map<String, Object> getDepartmentCompletionRate(int departmentId) throws SQLException {
        String sql = """
				SELECT
				    COUNT(*) as total_atividades,
				    SUM(CASE WHEN act_status = 'completed' THEN 1 ELSE 0 END) as concluidas,
				    ROUND((SUM(CASE WHEN act_status = 'completed' THEN 1 ELSE 0 END) * 100.0 / COUNT(*)), 2) as taxa_conclusao
				FROM activities a
				INNER JOIN goals g ON a.goal_id = g.goa_id
				INNER JOIN collaborators c ON g.collaborator_id = c.col_id
				WHERE c.department_id = ?
				""";

        Map<String, Object> resultado = new HashMap<>();
        try (Connection connection = getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, departmentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                resultado.put("total", rs.getInt("total_atividades"));
                resultado.put("concluidas", rs.getInt("concluidas"));
                resultado.put("taxa", rs.getDouble("taxa_conclusao"));
            }
        }
        return resultado;
    }

    /**
     * Retorna progresso de um colaborador por status
     * Formato: { "status": [quantidade, percentual] }
     * Exemplo: { "completed": [25, 50.0], "in_progress": [15, 30.0], "pending": [10, 20.0] }
     */
    public static Map<String, Object[]> getCollaboratorProgress(int collaboratorId) throws SQLException {
        String sql = """
				SELECT
				    act_status,
				    COUNT(*) as quantidade,
				    ROUND((COUNT(*) * 100.0 / (SELECT COUNT(*) FROM activities a
				                             INNER JOIN goals g ON a.goal_id = g.goa_id
				                             WHERE g.collaborator_id = ?)), 2) as percentual
				FROM activities a
				INNER JOIN goals g ON a.goal_id = g.goa_id
				WHERE g.collaborator_id = ?
				GROUP BY act_status
				ORDER BY quantidade DESC
				""";

        Map<String, Object[]> resultado = new HashMap<>();
        try (Connection connection = getConnection(); PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, collaboratorId);
            stmt.setInt(2, collaboratorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String status = rs.getString("act_status");
                int quantidade = rs.getInt("quantidade");
                double percentual = rs.getDouble("percentual");
                resultado.put(status, new Object[] { quantidade, percentual });
            }
        }
        return resultado;
    }

    // SOBRECARREGADO para aceitar filtros de Tag e TagType (Geral)
    public static List<DashboardTagFrequencyController> getTopTags(List<Tag> tags, List<TagType> tagTypes) {
        List<DashboardTagFrequencyController> frequencies = new ArrayList<>();
        String tagWhere = buildTagWhereClause(tags, tagTypes);

        String sql = String.format("""
            SELECT T.tag_name, COUNT(GT.tag_id) AS frequencia FROM goal_tags AS GT 
            JOIN tags AS T ON GT.tag_id = T.tag_id 
            WHERE %s
            GROUP BY T.tag_id, T.tag_name ORDER BY frequencia DESC LIMIT 15
            """, tagWhere);

        try (
                Connection conn = Database.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String nomeTag = rs.getString("tag_name");
                int contagem = rs.getInt("frequencia");
                frequencies.add(new DashboardTagFrequencyController(nomeTag, contagem));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return frequencies;
    }

    // Mantenha o método original para compatibilidade
    public static List<DashboardTagFrequencyController> getTopTags() {
        return getTopTags(null, null);
    }



    //INTERAÇÕES NO BANCO PARA *GERENTE DE SETOR*

    // SOBRECARREGADO para aceitar filtros de Tag e TagType (Departamento)
    public static List<DashboardTagFrequencyController> getTopTagsDepartment(int id, List<Tag> tags, List<TagType> tagTypes) {
        List<DashboardTagFrequencyController> frequencies = new ArrayList<>();
        String tagWhere = buildTagWhereClause(tags, tagTypes);

        String sql = String.format("""
            SELECT T.tag_name, COUNT(T.tag_id) AS frequencia FROM goal_tags AS GT 
            JOIN goals AS G ON GT.goal_id = G.goa_id 
            JOIN collaborators AS C ON G.collaborator_id = C.col_id 
            JOIN tags AS T ON GT.tag_id = T.tag_id 
            WHERE C.department_id = ? AND %s
            GROUP BY T.tag_id, T.tag_name ORDER BY frequencia DESC LIMIT 15
            """, tagWhere);


        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String tagName = rs.getString("tag_name");
                    int frequencia = rs.getInt("frequencia");
                    frequencies.add(new DashboardTagFrequencyController(tagName, frequencia));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return frequencies;
    }

    // Mantenha o método original para compatibilidade
    public static List<DashboardTagFrequencyController> getTopTagsDepartment(int id) {
        return getTopTagsDepartment(id, null, null);
    }

    // SOBRECARREGADO para aceitar filtros de GoalStatus
    public static List<DashboardStatusData> getGoalStatusCountsForDepartment(int departmentId, List<GoalStatus> statuses) {
        List<DashboardStatusData> statusCounts = new ArrayList<>();
        // Note: O filtro de status global está sendo aplicado aqui. O SQL original já filtrava status específicos.
        // O `buildGoalStatusWhereClause` agora controla quais status estão incluídos.
        String statusWhere = buildGoalStatusWhereClause(statuses);

        String sql = String.format("""
            SELECT G.goa_status, COUNT(G.goa_id) AS quantidade
            FROM goals AS G JOIN collaborators AS C ON
            G.collaborator_id = C.col_id
            WHERE C.department_id = ? AND %s
            GROUP BY G.goa_status
            """, statusWhere);

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, departmentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("goa_status");
                    int quantidade = rs.getInt("quantidade");
                    statusCounts.add(new DashboardStatusData(status, quantidade));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar contagem de status de metas por setor: " + e.getMessage(), e);
        }

        return statusCounts;
    }

    // Mantenha o método original para compatibilidade
    public static List<DashboardStatusData> getGoalStatusCountsForDepartment(int departmentId) {
        // O método original limitava a completed/in_progress. O novo permite todos se 'statuses' for null.
        // Para manter a funcionalidade original (e evitar erro de assinatura no ViewModel), vou deixar ele chamar o novo
        // permitindo todos, pois a tela de filtro já está preparada. Se precisar da restrição original, o filtro deve ser ajustado na UI.
        return getGoalStatusCountsForDepartment(departmentId, List.of(GoalStatus.completed, GoalStatus.in_progress));
    }


    // SOBRECARREGADO para aceitar filtros de data
    public static List<DashboardMonthlyData> getMonthlyActivityCounts(int departmentId, LocalDate startDate, LocalDate endDate) {
        List<DashboardMonthlyData> monthlyData = new ArrayList<>();

        String dateWhere = "";
        if (startDate != null && endDate != null) {
            // Usa DATE_ADD para incluir o último dia
            dateWhere = String.format(" AND G.goa_created_at BETWEEN '%s' AND DATE_ADD('%s', INTERVAL 1 DAY)", startDate.toString(), endDate.toString());
        } else if (startDate != null) {
            dateWhere = String.format(" AND G.goa_created_at >= '%s'", startDate.toString());
        } else if (endDate != null) {
            dateWhere = String.format(" AND G.goa_created_at <= DATE_ADD('%s', INTERVAL 1 DAY)", endDate.toString());
        }

        String sql = String.format("""
            SELECT DATE_FORMAT(G.goa_created_at, '%%Y-%%m') AS mes_ano,
            COUNT(G.goa_id) AS quantidade FROM goals AS G JOIN
            collaborators AS C ON G.collaborator_id = C.col_id
            WHERE C.department_id = ? %s
            GROUP BY mes_ano ORDER BY
            mes_ano ASC
            """, dateWhere);


        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, departmentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String mesAno = rs.getString("mes_ano");
                    int quantidade = rs.getInt("quantidade");
                    monthlyData.add(new DashboardMonthlyData(mesAno, quantidade));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar contagem de metas por mês: ", e);
        }

        return monthlyData;
    }

    // Mantenha o método original para compatibilidade
    public static List<DashboardMonthlyData> getMonthlyActivityCounts(int departmentId) {
        return getMonthlyActivityCounts(departmentId, null, null);
    }


    // SOBRECARREGADO para aceitar filtros de GoalStatus
    public static List<DashboardProgressData> getBottomCollaboratorProgress(int departmentId, List<GoalStatus> statuses) {
        List<DashboardProgressData> progressList = new ArrayList<>();
        // A SQL usa 'g.goa_status', então o helper precisa usar isso ao invés de 'G.goa_status'
        String statusWhere = buildGoalStatusWhereClause(statuses).replace("G.goa_status", "g.goa_status");

        String sql = String.format("""
            SELECT T.col_name, (CASE WHEN T.total_metas = 0 THEN 0 ELSE (T.metas_completas * 100.0 / T.total_metas) END)
            AS percentual_concluido FROM (
                SELECT c.col_name, COUNT(g.goa_id) AS total_metas, 
                SUM(CASE WHEN g.goa_status = 'completed' THEN 1 ELSE 0 END) AS metas_completas 
                FROM collaborators AS c LEFT JOIN goals AS g ON c.col_id = g.collaborator_id 
                WHERE c.col_deleted_at IS NULL AND c.department_id = ? AND %s 
                GROUP BY c.col_id, c.col_name
            ) AS T ORDER BY percentual_concluido ASC LIMIT 10
            """, statusWhere);

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, departmentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("col_name");
                    double percentage = rs.getDouble("percentual_concluido");

                    progressList.add(new DashboardProgressData(name, percentage));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar progresso de colaboradores por setor: " + e.getMessage(), e);
        }

        return progressList;
    }

    // Mantenha o método original para compatibilidade
    public static List<DashboardProgressData> getBottomCollaboratorProgress(int departmentId) {
        // O método original não tinha filtro, então passamos a lista completa (sem restrições)
        return getBottomCollaboratorProgress(departmentId, null);
    }
}