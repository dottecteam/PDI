package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.*;
import com.dottec.pdi.project.pdi.dao.DashboardDAO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.dottec.pdi.project.pdi.enums.*;
import com.dottec.pdi.project.pdi.model.Department;
import com.dottec.pdi.project.pdi.model.Tag;
import com.dottec.pdi.project.pdi.model.User;

import com.dottec.pdi.project.pdi.utils.FXUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;
import java.nio.file.Path;
import java.sql.SQLException;

import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;


import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;

public class DashboardViewModel implements Initializable {

    // Adicionado: Padrão Singleton simples para acesso pelo Header
    private static DashboardViewModel instance;

    // --- FXML FIELDS ---
    @FXML private VBox rootVBox; // Mantido para acesso ao Stage
    @FXML private AnchorPane dashboardContentPane;

    @FXML private BarChart<String, Number> tagBarChart;
    @FXML private PieChart taskPieChart;
    @FXML private Label percentage;
    @FXML private LineChart monthsLineChart;
    @FXML private BarChart<Number, String> progressBarChart;
    @FXML private PieChart skillsDistributionPieChart;

    @FXML private AnchorPane widgetTagBarChart;
    @FXML private AnchorPane widgetTaskPieChart;
    @FXML private AnchorPane widgetMonthsLineChart;
    @FXML private AnchorPane widgetProgressBarChart;
    @FXML private AnchorPane widgetSkillsDistribution;


    // --- DRAG & GRID FIELDS ---
    private double xOffset = 0;
    private double yOffset = 0;
    private boolean isResizing = false;
    private Node draggingNode = null;

    // Grid Config
    private final double MIN_CELL_SIZE = 220.0;
    private final double GAP = 8.0;
    private final double RESIZE_THRESHOLD = 40.0;
    private int cols = 3;
    private int rows = 3;
    private double cellWidth;
    private double cellHeight;

    private boolean[][] occupied;
    private final Map<Node, GridItem> items = new HashMap<>();

    private final List<Rectangle> highlightRects = new ArrayList<>();
    private Rectangle targetRect = null;

    // --- Filters ---
    private LocalDate startDay = LocalDate.now().withMonth(1).withDayOfMonth(1);
    private LocalDate endDay = LocalDate.now().withMonth(12).withDayOfMonth(31);
    private List<ActivityStatus> filteredActivityStatuses = new ArrayList<>(Arrays.asList(ActivityStatus.values()));
    private List<GoalStatus> filteredGoalStatuses = new ArrayList<>(Arrays.asList(GoalStatus.values()));
    private List<Department> filteredDepartments = DepartmentController.findAllDepartments();
    private List<Tag> filteredTags = TagController.findAllTags();
    private List<TagType> filteredTagTypes = new ArrayList<>(Arrays.asList(TagType.values()));
    // Lista de todos os departamentos (para o filtro do Gerente Geral/RH)
    private List<Department> allDepartments = DepartmentController.findAllDepartments();


    private final String STATUS_CONCLUIDO = "completed";
    private final DashboardDAO tagDAO;

    public DashboardViewModel() {
        this.tagDAO = new DashboardDAO();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instance = this;

        tagBarChart.setAnimated(false);
        taskPieChart.setAnimated(false);
        taskPieChart.setLegendVisible(false);
        monthsLineChart.setAnimated(false);
        progressBarChart.setAnimated(false);
        progressBarChart.setLegendVisible(false);
        skillsDistributionPieChart.setAnimated(false);

        tagBarChart.setMouseTransparent(true);
        taskPieChart.setMouseTransparent(true);
        monthsLineChart.setMouseTransparent(true);
        progressBarChart.setMouseTransparent(true);
        percentage.setMouseTransparent(true);
        skillsDistributionPieChart.setMouseTransparent(true);

        dashboardContentPane.widthProperty().addListener((obs, oldV, newV) -> recomputeGridAndReposition());
        dashboardContentPane.heightProperty().addListener((obs, oldV, newV) -> recomputeGridAndReposition());

        Platform.runLater(() -> {
            // Configura o layout inicial dos widgets
            registerWidget(widgetMonthsLineChart, 0, 0, 2, 1);
            registerWidget(widgetTagBarChart, 2, 0, 2, 1);
            registerWidget(widgetProgressBarChart, 0, 1, 2, 1);
            registerWidget(widgetSkillsDistribution, 2, 1, 2, 1);
            registerWidget(widgetTaskPieChart, 4, 0, 1, 2);

            createHighlightLayer();
            recomputeGridAndReposition();
        });

        setFilter(); // Configura o menu de filtro
        loadData();
    }

    public static DashboardViewModel getInstance() {
        return instance;
    }

    private void loadData() {
        User loggedUser = AuthController.getInstance().getLoggedUser();
        if (loggedUser == null) {
            FXUtils.showErrorMessage("Usuário não encontrado", "Erro: Nenhum usuário está logado.");
            return;
        }

        // Limpa os dados dos gráficos antes de recarregar
        clearCharts();

        boolean isDeptManager = loggedUser.getRole() == Role.department_manager;
        boolean isGeneral = loggedUser.getRole() == Role.hr_manager || loggedUser.getRole() == Role.general_manager;

        if (isDeptManager && loggedUser.getDepartment() != null) {
            loadDepartmentData(loggedUser.getDepartment().getId(), loggedUser.getDepartment().getName());
        } else if (isGeneral) {
            loadGeneralData();
        } else {
            FXUtils.showErrorMessage("Permissão necessária", "Você não tem permissão para visualizar este dashboard.");
        }
    }

    /**
     * NOVO: Carrega dados para Gerente Geral / RH (Visão Geral).
     * Nota: A filtragem por setor só pode ser implementada nos gráficos globais (Tags/Skills)
     * devido à estrutura atual do DashboardDAO.
     */
    private void loadGeneralData() {
        // --- VISUAL SETUP / LABELS ---
        tagBarChart.setTitle("Tags Mais Usadas (Geral)");
        skillsDistributionPieChart.setTitle("Distribuição de Soft/Hard Skills (Geral)");

        // Oculta gráficos que requerem agregação complexa (não suportada pelo DAO atual)
        setWidgetVisibility(widgetTaskPieChart, false);
        setWidgetVisibility(widgetMonthsLineChart, false);
        setWidgetVisibility(widgetProgressBarChart, false);

        // Garante que os widgets globais estejam visíveis
        setWidgetVisibility(widgetTagBarChart, true);
        setWidgetVisibility(widgetSkillsDistribution, true);

        // Atualiza a label de porcentagem para refletir a simplificação
        percentage.setText("Progresso Detalhado: N/A");

        // --- LOAD DATA FOR GLOBAL CHARTS ---

        // Load Tags (Bar Chart) - Global Available
        Callable<List<DashboardTagFrequencyController>> tagsFetchingTask =
                () -> DashboardDAO.getTopTags(filteredTags, filteredTagTypes);
        loadBarChartData(tagsFetchingTask);

        // Load Skills Distribution (Pie Chart) - Global Available
        loadSkillsDistributionChartData(filteredTagTypes);

        FXUtils.showErrorMessage("Aviso", "Na visão Geral, a filtragem por Setor e a maioria dos gráficos detalhados não estão disponíveis, pois exigem agregação de dados não implementada no sistema.");
    }

    /**
     * Carrega dados para Gerente de Setor (Visão Departamental).
     */
    private void loadDepartmentData(int departmentId, String departmentName) {
        // --- VISUAL SETUP / LABELS ---
        tagBarChart.setTitle("Tags Mais Usadas (Setor: " + departmentName + ")");
        taskPieChart.setTitle("Relação de conclusão de metas");
        monthsLineChart.setTitle("Metas criadas por mês");
        progressBarChart.setTitle("Progresso de colaboradores");

        // Garante que todos os widgets estejam visíveis para o gerente de setor
        setWidgetVisibility(widgetTaskPieChart, true);
        setWidgetVisibility(widgetMonthsLineChart, true);
        setWidgetVisibility(widgetProgressBarChart, true);
        setWidgetVisibility(widgetTagBarChart, true);
        setWidgetVisibility(widgetSkillsDistribution, true);

        // --- LOAD DATA FOR DEPARTMENT CHARTS ---

        // Load Tags (Bar Chart)
        Callable<List<DashboardTagFrequencyController>> tagsFetchingTask =
                () -> DashboardDAO.getTopTagsDepartment(departmentId, filteredTags, filteredTagTypes);
        loadBarChartData(tagsFetchingTask);

        // Load Pie Chart (Task Status)
        loadPieChartData(departmentId, filteredGoalStatuses);

        // Load Line Chart (Monthly Goals)
        loadLineChartData(departmentId, startDay, endDay);

        // Load Progress Bar Chart (Bottom Collaborator Progress)
        loadProgressChartData(departmentId, filteredGoalStatuses);

        // Load Skills Distribution (Pie Chart)
        loadSkillsDistributionChartData(filteredTagTypes);
    }

    // Método auxiliar para alternar a visibilidade
    private void setWidgetVisibility(Node widget, boolean visible) {
        if (widget == null) return;
        widget.setVisible(visible);
        widget.setManaged(visible);
    }

    // Método auxiliar para limpar todos os gráficos
    private void clearCharts() {
        tagBarChart.getData().clear();
        taskPieChart.getData().clear();
        monthsLineChart.getData().clear();
        progressBarChart.getData().clear();
        skillsDistributionPieChart.getData().clear();
    }


    @FXML
    private void handleFilter() {
        // Recarrega os dados com os filtros aplicados
        loadData();
    }

    private void setFilter() {
        FilterMenuViewModel filterMenu = new FilterMenuViewModel();
        User loggedUser = AuthController.getInstance().getLoggedUser();
        boolean filterByDepartment = loggedUser.getRole() == Role.general_manager || loggedUser.getRole() == Role.hr_manager;


        //Date based filter
        DatePicker startDayDatePicker = filterMenu.buildDatePicker();
        startDayDatePicker.setValue(startDay);
        startDayDatePicker.setOnAction(actionEvent -> startDay = startDayDatePicker.getValue());

        DatePicker endDayDatePicker = filterMenu.buildDatePicker();
        endDayDatePicker.setValue(endDay);
        endDayDatePicker.setOnAction(actionEvent -> endDay = endDayDatePicker.getValue());

        filterMenu.addFilterField("Filtrar por data",
                filterMenu.addDatePickerLabel("De: ", startDayDatePicker),
                filterMenu.addDatePickerLabel("À: ", endDayDatePicker));


        // --- Filtro por Departamento (Apenas para Gerente Geral/RH) ---
        if(filterByDepartment) {
            List<Node> departmentCheckBoxes = new ArrayList<>();
            // Recria a lista de departamentos para o filtro
            filteredDepartments.clear();
            allDepartments.forEach(d -> filteredDepartments.add(d));

            allDepartments.stream()
                    .sorted(Comparator.comparing(Department::getName))
                    .forEach(department -> {
                        CheckBox checkBox  = new CheckBox(department.getName());
                        checkBox.setSelected(true);
                        checkBox.setOnAction(e -> {
                            if(checkBox.isSelected()){
                                if(!filteredDepartments.contains(department)) {
                                    filteredDepartments.add(department);
                                }
                            } else {
                                filteredDepartments.remove(department);
                            }
                        });
                        departmentCheckBoxes.add(checkBox);
                    });
            filterMenu.addFilterField("Filtrar por departamento", departmentCheckBoxes);
        }

        //Goal status based filter
        List<Node> goalStatusCheckBoxes = new ArrayList<>();
        filteredGoalStatuses.clear();
        Arrays.asList(GoalStatus.values()).forEach(s -> filteredGoalStatuses.add(s));

        for(GoalStatus goalStatus : GoalStatus.values()){
            CheckBox checkBox = new CheckBox();
            checkBox.setSelected(true);
            checkBox.setOnAction(e -> {
                if(checkBox.isSelected()) {
                    if(!filteredGoalStatuses.contains(goalStatus)) {
                        filteredGoalStatuses.add(goalStatus);
                    }
                } else {
                    filteredGoalStatuses.remove(goalStatus);
                }
            });
            switch (goalStatus.toString()) {
                case "in_progress" -> checkBox.setText("Em progresso");
                case "pending" -> checkBox.setText("Pendente");
                case "completed" -> checkBox.setText("Completo");
                case "canceled" -> checkBox.setText("Cancelado");
            }
            goalStatusCheckBoxes.add(checkBox);
        }
        filterMenu.addFilterField("Filtrar por status de meta", goalStatusCheckBoxes);


        //Tag type based filter
        List<Node> tagTypesCheckBoxes = new ArrayList<>();
        filteredTagTypes.clear();
        Arrays.asList(TagType.values()).forEach(t -> filteredTagTypes.add(t));

        filteredTagTypes.forEach(tagType -> {
            CheckBox checkBox  = new CheckBox();
            checkBox.setSelected(true);
            if(tagType.equals(TagType.SOFT)){
                checkBox.setText("Soft Skill");
            } else if (tagType.equals(TagType.HARD)){
                checkBox.setText("Hard Skill");
            }
            checkBox.setOnAction(e -> {
                if(checkBox.isSelected()){
                    if(!filteredTagTypes.contains(tagType)) {
                        filteredTagTypes.add(tagType);
                    }
                } else {
                    filteredTagTypes.remove(tagType);
                }
            });
            tagTypesCheckBoxes.add(checkBox);
        });
        filterMenu.addFilterField("Filtrar por skills", tagTypesCheckBoxes);

        //Tag based filter
        List<Node> tagSoftCheckBoxes = new ArrayList<>();
        List<Node> tagHardCheckBoxes = new ArrayList<>();
        filteredTags.clear();
        TagController.findAllTags().forEach(t -> filteredTags.add(t));

        List<Tag> allTags = TagController.findAllTags();
        allTags.sort(Comparator.comparing(Tag::getName));
        allTags.forEach(tag -> {
            CheckBox checkBox  = new CheckBox(tag.getName());
            checkBox.setSelected(true);
            checkBox.setOnAction(e -> {
                if(checkBox.isSelected()){
                    if(!filteredTags.contains(tag)) {
                        filteredTags.add(tag);
                    }
                } else {
                    filteredTags.remove(tag);
                }
            });
            if(tag.getType().equals(TagType.HARD)){
                tagHardCheckBoxes.add(checkBox);
            } else {
                tagSoftCheckBoxes.add(checkBox);
            }
        });
        filterMenu.addFilterField("Filtrar por soft skills", tagSoftCheckBoxes);
        filterMenu.addFilterField("Filtrar por hard skills", tagHardCheckBoxes);

        filterMenu.getConfirmFilterButton().setOnMouseClicked(e -> handleFilter());

        Button filterButton = HeaderViewModel.getController().getFilterButton();
        filterButton.setOnMouseClicked(e -> filterMenu.show(filterButton));
    }


    public void handleExportData(javafx.event.ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Relatório PDI");

        // Adiciona o filtro para CSV
        fileChooser.getExtensionFilters().add(new ExtensionFilter("CSV files (*.csv)", "*.csv"));

        // Adiciona o filtro para XLSX
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Excel Workbook (*.xlsx)", "*.xlsx"));

        User loggedUser = AuthController.getInstance().getLoggedUser();
        String defaultBaseName = "relatorio_pdi";
        boolean isDepartmentManager = loggedUser != null && loggedUser.getRole() == Role.department_manager;

        if (isDepartmentManager && loggedUser.getDepartment() != null) {
            int departmentId = loggedUser.getDepartment().getId();
            defaultBaseName = "relatorio_departamento_" + departmentId;
        } else {
            defaultBaseName = "relatorio_geral_pdi";
        }

        Stage stage = (Stage) rootVBox.getScene().getWindow();

        fileChooser.setInitialFileName(defaultBaseName + ".csv");

        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            Path savePath = file.toPath();
            String pathString = savePath.toString();

            String extension = pathString.substring(pathString.lastIndexOf(".") + 1).toLowerCase();

            try {
                boolean isDepartment = isDepartmentManager && loggedUser.getDepartment() != null;
                int departmentId = isDepartment ? loggedUser.getDepartment().getId() : -1;


                if (extension.equals("xlsx")) {
                    if (isDepartment) {
                        ExportDataController.sendDepartmentConsolidatedXLSX(pathString, departmentId);
                    } else {
                        ExportDataController.sendConsolidatedXLSX(pathString);
                    }
                } else {
                    if (isDepartment) {
                        ExportDataController.sendDepartmentConsolidatedCSV(pathString, departmentId);
                    } else {
                        ExportDataController.sendConsolidatedCSV(pathString);
                    }
                }

                FXUtils.showSuccessMessage("Exportação Concluída",
                        "O relatório foi salvo em:\n" + pathString);

            } catch (Exception e) {
                e.printStackTrace();
                FXUtils.showErrorMessage("Erro na Exportação",
                        "Falha ao salvar o arquivo:\n" + e.getMessage());
            }
        }
    }


    // ---------- GRID / WIDGETS ----------

    private void registerWidget(AnchorPane widget, int startCol, int startRow, int colspan, int rowspan) {
        AnchorPane.clearConstraints(widget);
        GridItem gi = new GridItem(widget, startRow, startCol, colspan, rowspan);
        items.put(widget, gi);
        widget.setLayoutX(widget.getLayoutX());
        widget.setLayoutY(widget.getLayoutY());
        makeDraggable(widget);
    }

    private void recomputeGridAndReposition() {
        double w = dashboardContentPane.getWidth();
        double h = dashboardContentPane.getHeight();

        if (w <= 0 || h <= 0) return;

        cols = Math.max(5, (int) Math.floor(w / MIN_CELL_SIZE));
        rows = Math.max(3, (int) Math.floor(h / MIN_CELL_SIZE));

        cols = Math.max(5, cols);
        rows = Math.max(3, rows);

        cellWidth = w / (double) cols;
        cellHeight = h / (double) rows;

        occupied = new boolean[rows][cols];
        List<GridItem> ordered = new ArrayList<>(items.values());

        ordered.sort(Comparator.comparingInt((GridItem gi) -> gi.colspan * gi.rowspan).reversed()
                .thenComparingInt((GridItem gi) -> gi.row)
                .thenComparingInt((GridItem gi) -> gi.col));

        for (GridItem gi : ordered) {
            int preferredCol = gi.col;
            int preferredRow = gi.row;

            int[] free = findFirstFit(preferredRow, preferredCol, gi.colspan, gi.rowspan);

            if (free == null) free = findFirstFit(0, 0, gi.colspan, gi.rowspan);

            if (free != null) {
                placeItemAt(gi, free[0], free[1]);
            } else {
                placeItemAt(gi, 0, 0);
            }
        }

        if (draggingNode != null) {
            showHighlightsFor(draggingNode, xOffset, yOffset);
        } else {
            clearHighlights();
        }
    }

    private int[] findFirstFit(int startRow, int startCol, int colspan, int rowspan) {
        for (int r = startRow; r < rows; r++) {
            for (int c = (r == startRow ? startCol : 0); c < cols; c++) {
                if (c + colspan > cols || r + rowspan > rows) continue;
                if (!isRegionOccupied(r, c, rowspan, colspan)) {
                    return new int[]{r, c};
                }
            }
        }
        return null;
    }

    private boolean isRegionOccupied(int row, int col, int rowspan, int colspan) {
        for (int r = row; r < row + rowspan; r++) {
            for (int c = col; c < col + colspan; c++) {
                if (occupied[r][c]) return true;
            }
        }
        return false;
    }

    private void occupyRegion(int row, int col, int rowspan, int colspan, boolean value) {
        for (int r = row; r < row + rowspan; r++) {
            for (int c = col; c < col + colspan; c++) {
                occupied[r][c] = value;
            }
        }
    }

    private void placeItemAt(GridItem gi, int row, int col) {
        if (gi.placed) {
            occupyRegion(gi.row, gi.col, gi.rowspan, gi.colspan, false);
        }

        gi.row = row;
        gi.col = col;
        gi.placed = true;
        occupyRegion(row, col, gi.rowspan, gi.colspan, true);

        double lx = col * cellWidth + GAP / 2.0;
        double ly = row * cellHeight + GAP / 2.0;

        double w = gi.colspan * cellWidth - GAP;
        double h = gi.rowspan * cellHeight - GAP;

        if (gi.node instanceof AnchorPane) {
            ((AnchorPane) gi.node).setPrefWidth(w);
            ((AnchorPane) gi.node).setPrefHeight(h);
        } else {
            gi.node.resize(w, h);
        }

        gi.node.setLayoutX(Math.round(lx));
        gi.node.setLayoutY(Math.round(ly));
    }

    private void createHighlightLayer() {
        clearHighlights();
        highlightRects.clear();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Rectangle rect = new Rectangle();
                rect.setMouseTransparent(true);
                rect.setOpacity(0.0);
                rect.setArcWidth(6);
                rect.setArcHeight(6);
                highlightRects.add(rect);
                if (!dashboardContentPane.getChildren().contains(rect)) {
                    dashboardContentPane.getChildren().add(rect);
                }
            }
        }
        layoutHighlights();
    }

    private void layoutHighlights() {
        int needed = rows * cols;
        while (highlightRects.size() < needed) {
            Rectangle rnew = new Rectangle();
            rnew.setMouseTransparent(true);
            rnew.setOpacity(0.0);
            rnew.setArcWidth(6);
            rnew.setArcHeight(6);
            highlightRects.add(rnew);
            dashboardContentPane.getChildren().add(rnew);
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int idx = r * cols + c;
                Rectangle rect = highlightRects.get(idx);

                rect.setWidth(cellWidth - GAP);
                rect.setHeight(cellHeight - GAP);
                rect.setX(c * cellWidth + GAP / 2.0);
                rect.setY(r * cellHeight + GAP / 2.0);

                rect.setOpacity(0.0);
                rect.setFill(Color.TRANSPARENT);
                rect.toFront();
            }
        }

        if (targetRect == null) {
            targetRect = new Rectangle();
            targetRect.setMouseTransparent(true);
            targetRect.setArcWidth(6);
            targetRect.setArcHeight(6);
            targetRect.setOpacity(0.0);
            dashboardContentPane.getChildren().add(targetRect);
        }
        targetRect.toFront();
    }

    private void clearHighlights() {
        for (Rectangle r : highlightRects) {
            r.setOpacity(0.0);
        }
        if (targetRect != null) {
            targetRect.setOpacity(0.0);
        }
    }

    private void showHighlightsFor(Node node, double mouseSceneX, double mouseSceneY) {
        GridItem gi = items.get(node);
        if (gi == null) return;

        layoutHighlights();

        double localX = dashboardContentPane.screenToLocal(mouseSceneX, mouseSceneY).getX();
        double localY = dashboardContentPane.screenToLocal(mouseSceneX, mouseSceneY).getY();

        int hoverCol = (int) Math.floor(localX / cellWidth);
        int hoverRow = (int) Math.floor(localY / cellHeight);

        hoverCol = Math.max(0, Math.min(cols - gi.colspan, hoverCol));
        hoverRow = Math.max(0, Math.min(rows - gi.rowspan, hoverRow));

        clearHighlights();

        for (int r = 0; r <= rows - gi.rowspan; r++) {
            for (int c = 0; c <= cols - gi.colspan; c++) {
                int idx = r * cols + c;
                Rectangle rect = highlightRects.get(idx);

                if (!isRegionOccupied(r, c, gi.rowspan, gi.colspan) || (gi.placed && gi.row == r && gi.col == c)) {
                    rect.setFill(Color.color(0.6, 0.3, 0.8, 0.25));
                    rect.setOpacity(1.0);
                } else {
                    rect.setFill(Color.TRANSPARENT);
                    rect.setOpacity(0.0);
                }
            }
        }

        double tx = hoverCol * cellWidth + GAP / 2.0;
        double ty = hoverRow * cellHeight + GAP / 2.0;
        double tw = gi.colspan * cellWidth - GAP;
        double th = gi.rowspan * cellHeight - GAP;

        targetRect.setX(tx);
        targetRect.setY(ty);
        targetRect.setWidth(Math.max(4, tw));
        targetRect.setHeight(Math.max(4, th));
        targetRect.setFill(Color.color(0.45, 0.15, 0.85, 0.35));
        targetRect.setStroke(Color.color(0.5, 0.18, 0.9));
        targetRect.setStrokeWidth(2);
        targetRect.setOpacity(1.0);
    }

    private void makeDraggable(Node node) {
        node.setOnMousePressed((MouseEvent event) -> {
            if (event.getButton() != MouseButton.PRIMARY) return;

            GridItem gi = items.get(node);
            if (gi == null) return;

            double mouseX = event.getX();
            double mouseY = event.getY();

            if (mouseX > node.getBoundsInLocal().getWidth() - RESIZE_THRESHOLD &&
                    mouseY > node.getBoundsInLocal().getHeight() - RESIZE_THRESHOLD) {
                isResizing = true;
            } else {
                isResizing = false;
            }

            draggingNode = node;
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();

            node.toFront();

            if (gi.placed) {
                occupyRegion(gi.row, gi.col, gi.rowspan, gi.colspan, false);
                gi.placed = false;
            }

            showHighlightsFor(node, event.getSceneX(), event.getSceneY());
            event.consume();
        });

        node.setOnMouseDragged((MouseEvent event) -> {
            if (draggingNode != node) return;

            GridItem gi = items.get(node);
            if (gi == null) return;

            if (isResizing) {
                double newWidth = event.getX();
                double newHeight = event.getY();

                int newColspan = (int) Math.max(gi.minColspan, Math.round(newWidth / cellWidth));
                int newRowspan = (int) Math.max(gi.minRowspan, Math.round(newHeight / cellHeight));

                newColspan = Math.min(newColspan, cols - gi.col);
                newRowspan = Math.min(newRowspan, rows - gi.row);

                if (newColspan != gi.colspan || newRowspan != gi.rowspan) {
                    gi.colspan = newColspan;
                    gi.rowspan = newRowspan;

                    double w = gi.colspan * cellWidth - GAP;
                    double h = gi.rowspan * cellHeight - GAP;
                    ((AnchorPane) gi.node).setPrefWidth(w);
                    ((AnchorPane) gi.node).setPrefHeight(h);

                    showHighlightsFor(node, event.getSceneX(), event.getSceneY());
                }

                node.getScene().setCursor(javafx.scene.Cursor.SE_RESIZE);

            } else {
                double dx = event.getSceneX() - xOffset;
                double dy = event.getSceneY() - yOffset;

                double newLayoutX = node.getLayoutX() + dx;
                double newLayoutY = node.getLayoutY() + dy;

                double maxX = Math.max(0, dashboardContentPane.getWidth() - node.getBoundsInParent().getWidth());
                double maxY = Math.max(0, dashboardContentPane.getHeight() - node.getBoundsInParent().getHeight());
                newLayoutX = Math.max(0, Math.min(newLayoutX, maxX));
                newLayoutY = Math.max(0, Math.min(newLayoutY, maxY));

                node.setLayoutX(newLayoutX);
                node.setLayoutY(newLayoutY);

                xOffset = event.getSceneX();
                yOffset = event.getSceneY();

                showHighlightsFor(node, event.getSceneX(), event.getSceneY());
            }

            event.consume();
        });

        node.setOnMouseReleased((MouseEvent event) -> {
            if (draggingNode != node) return;
            node.getScene().setCursor(javafx.scene.Cursor.DEFAULT);

            GridItem gi = items.get(node);
            if (gi == null) {
                draggingNode = null;
                clearHighlights();
                return;
            }

            int targetCol = (int) Math.round(node.getLayoutX() / cellWidth);
            int targetRow = (int) Math.round(node.getLayoutY() / cellHeight);

            targetCol = Math.max(0, Math.min(cols - gi.colspan, targetCol));
            targetRow = Math.max(0, Math.min(rows - gi.rowspan, targetRow));

            if (!isRegionOccupied(targetRow, targetCol, gi.rowspan, gi.colspan)) {
                placeItemAt(gi, targetRow, targetCol);
            } else {
                resolveCollisionAndPlace(gi, targetRow, targetCol);
            }

            isResizing = false;
            draggingNode = null;
            clearHighlights();
            event.consume();
        });
    }

    private void resolveCollisionAndPlace(GridItem gi, int targetRow, int targetCol) {
        List<GridItem> conflicting = new ArrayList<>();
        for (GridItem other : items.values()) {
            if (!other.placed || other == gi) continue;
            if (regionsOverlap(targetRow, targetCol, gi.rowspan, gi.colspan, other.row, other.col, other.rowspan, other.colspan)) {
                conflicting.add(other);
            }
        }

        for (GridItem conflicted : conflicting) {
            occupyRegion(conflicted.row, conflicted.col, conflicted.rowspan, conflicted.colspan, false);
            conflicted.placed = false;
            int[] free = findNearestFreeSlotFor(conflicted, gi.row, gi.col);

            if (free != null) {
                placeItemAt(conflicted, free[0], free[1]);
            } else {
                placeItemAt(conflicted, conflicted.row, conflicted.col);
            }
        }

        placeItemAt(gi, targetRow, targetCol);
    }

    private boolean regionsOverlap(int r1, int c1, int rs1, int cs1, int r2, int c2, int rs2, int cs2) {
        return !(r1 + rs1 <= r2 || r2 + rs2 <= r1 || c1 + cs1 <= c2 || c2 + cs2 <= c1);
    }

    private int[] findNearestFreeSlotFor(GridItem gi, int originRow, int originCol) {
        boolean[][] seen = new boolean[rows][cols];
        Queue<int[]> q = new ArrayDeque<>();
        q.add(new int[]{originRow, originCol, 0});
        seen[originRow][originCol] = true;

        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int r = cur[0], c = cur[1];
            if (r + gi.rowspan <= rows && c + gi.colspan <= cols && !isRegionOccupied(r, c, gi.rowspan, gi.colspan)) {
                return new int[]{r, c};
            }
            int[][] neigh = {{r - 1, c}, {r + 1, c}, {r, c - 1}, {r, c + 1}, {r - 1, c - 1}, {r - 1, c + 1}, {r + 1, c - 1}, {r + 1, c + 1}};
            for (int[] n : neigh) {
                int nr = n[0], nc = n[1];
                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) continue;
                if (seen[nr][nc]) continue;
                seen[nr][nc] = true;
                q.add(new int[]{nr, nc, cur[2] + 1});
            }
        }
        return null;
    }

    // --- MÉTODOS DE GRÁFICOS E AUXILIARES ---

    private void loadSkillsDistributionChartData(List<TagType> tagTypes) {
        skillsDistributionPieChart.setTitle("Distribuição de Soft/Hard Skills");
        Task<Map<String, Object[]>> loadDataTask = new Task<>() {
            @Override
            protected Map<String, Object[]> call() throws Exception {
                return DashboardDAO.getSkillsDistribution(tagTypes);
            }
        };

        loadDataTask.setOnSucceeded(event -> {
            Map<String, Object[]> dataFromDB = loadDataTask.getValue();
            if (dataFromDB == null || dataFromDB.isEmpty()) {
                skillsDistributionPieChart.setTitle("Sem dados de Skills.");
                return;
            }

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            for (Map.Entry<String, Object[]> entry : dataFromDB.entrySet()) {
                String tipo = entry.getKey();
                int contagem = (int) entry.getValue()[0];
                PieChart.Data pieData = new PieChart.Data(tipo, contagem);
                pieChartData.add(pieData);
                applyPieSliceColorForSkills(pieData, tipo);
            }
            skillsDistributionPieChart.setData(pieChartData);
        });
        new Thread(loadDataTask).start();
    }

    private void applyPieSliceColorForSkills(PieChart.Data pieData, String skillType) {
        pieData.nodeProperty().addListener((ov, oldNode, newNode) -> {
            if (newNode != null) {
                String color = "#808080";
                switch (skillType) {
                    case "Soft Skills":
                        color = "#AF69CD";
                        break;
                    case "Hard Skills":
                        color = "#4B0081";
                        break;
                }
                newNode.setStyle(String.format("-fx-pie-color: %s;", color));
            }
        });
    }

    private void loadBarChartData(Callable<List<DashboardTagFrequencyController>> dataFetchingTask) {
        Task<List<DashboardTagFrequencyController>> loadDataTask = new Task<>() {
            @Override
            protected List<DashboardTagFrequencyController> call() throws Exception {
                return dataFetchingTask.call();
            }
        };

        loadDataTask.setOnSucceeded(event -> {
            List<DashboardTagFrequencyController> dataFromDB = loadDataTask.getValue();
            if (dataFromDB == null || dataFromDB.isEmpty()) {
                FXUtils.showErrorMessage("Sem Dados", "Nenhuma tag encontrada para esta visualização.");
                return;
            }

            ObservableList<XYChart.Data<String, Number>> chartData =
                    FXCollections.observableArrayList();
            int index = 0;

            for (DashboardTagFrequencyController freq : dataFromDB) {
                XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(freq.nome(), freq.cont());
                final int indice = index;

                dataPoint.nodeProperty().addListener((ov, oldNode, newNode) -> {
                    if (newNode != null) {
                        newNode.setStyle(indice % 2 == 0 ? "-fx-bar-fill: #374649;" : "-fx-bar-fill: #708F95;");
                        newNode.setOpacity(0);
                        newNode.setTranslateY(15);

                        FadeTransition ft = new FadeTransition(Duration.millis(600), newNode);
                        ft.setToValue(1.0);
                        TranslateTransition tt = new TranslateTransition(Duration.millis(500), newNode);
                        tt.setToY(0);
                        tt.setInterpolator(Interpolator.EASE_OUT);

                        Duration delay = Duration.millis(indice * 50);
                        ft.setDelay(delay);
                        tt.setDelay(delay);
                        ft.play();
                        tt.play();
                    }
                });
                chartData.add(dataPoint);
                index++;
            }
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Comparação das tags mais utilizadas");
            series.setData(chartData);
            tagBarChart.getData().clear();
            tagBarChart.getData().add(series);
        });

        new Thread(loadDataTask).start();
    }


    private void loadPieChartData(int departmentId, List<GoalStatus> statuses) {
        percentage.setText("Carregando status...");
        Task<List<DashboardStatusData>> loadDataTask = new Task<>() {
            @Override
            protected List<DashboardStatusData> call() throws Exception {
                return DashboardDAO.getGoalStatusCountsForDepartment(departmentId, statuses);
            }
        };

        loadDataTask.setOnSucceeded(event -> {
            List<DashboardStatusData> dataFromDB = loadDataTask.getValue();

            if (dataFromDB == null || dataFromDB.isEmpty()) {
                FXUtils.showErrorMessage("Sem Metas", "Nenhuma meta (goal) encontrada para este setor.");
                percentage.setText("Nenhuma meta encontrada.");
                return;
            }

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            int totalTasks = 0;
            int completedTasks = 0;

            // Mapeamento de cores para os status (inversão Concluído/Cancelado)
            Map<String, String> statusColors = new HashMap<>();
            statusColors.put("completed", "#FD625E"); // NOVO: Cor do Cancelado/Erro
            statusColors.put("canceled", "#01B8AA"); // NOVO: Cor do Concluído/Sucesso
            statusColors.put("in_progress", "#AF69CD"); // Roxo médio
            statusColors.put("pending", "#F2C80F"); // Amarelo/Atenção


            for (DashboardStatusData data : dataFromDB) {
                String statusDoBanco = data.status();
                int contagem = data.cont();

                String nomeParaExibir;

                // CORREÇÃO: Traduzir TODOS os status
                switch (statusDoBanco.toLowerCase()) {
                    case "completed" -> nomeParaExibir = "Concluído";
                    case "in_progress" -> nomeParaExibir = "Em Progresso";
                    case "pending" -> nomeParaExibir = "Pendente";
                    case "canceled" -> nomeParaExibir = "Cancelado";
                    default -> nomeParaExibir = statusDoBanco;
                }

                PieChart.Data pieData = new PieChart.Data(nomeParaExibir, contagem);
                pieChartData.add(pieData);

                // Aplica a cor definida
                String color = statusColors.getOrDefault(statusDoBanco.toLowerCase(), "#808080");
                pieData.nodeProperty().addListener((ov, oldNode, newNode) -> {
                    if (newNode != null) {
                        newNode.setStyle(String.format("-fx-pie-color: %s;", color));
                    }
                });


                totalTasks += contagem;
                if (statusDoBanco.equalsIgnoreCase(STATUS_CONCLUIDO)) {
                    completedTasks = contagem;
                }
            }

            taskPieChart.setData(pieChartData);

            if (totalTasks > 0) {
                double percentageValue = ((double) completedTasks / totalTasks) * 100.0;
                percentage.setText(String.format("Metas Concluídas: %.1f%%", percentageValue));
            } else {
                percentage.setText("Nenhuma meta encontrada.");
            }
        });
        new Thread(loadDataTask).start();
    }


    private void loadLineChartData(int departmentId, LocalDate startDate, LocalDate endDate) {
        monthsLineChart.getData().clear();
        Task<List<DashboardMonthlyData>> loadDataTask = new Task<>() {
            @Override
            protected List<DashboardMonthlyData> call() throws Exception {
                return DashboardDAO.getMonthlyActivityCounts(departmentId, startDate, endDate);
            }
        };

        loadDataTask.setOnSucceeded(event -> {
            List<DashboardMonthlyData> dataFromDB = loadDataTask.getValue();

            if (dataFromDB == null || dataFromDB.isEmpty()) {
                monthsLineChart.setTitle("Sem dados de atividades para este setor.");
                return;
            }

            ObservableList<XYChart.Data<String, Number>> chartData =
                    FXCollections.observableArrayList();

            // Usando "yyyy-MM" para parsear, pois o DAO retorna "mes_ano" formatado.
            DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
            DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("MMM/yy");

            for (DashboardMonthlyData data : dataFromDB) {
                String monthYear = data.mesAno();
                // Assumindo dia 01 para parsing
                LocalDate date = LocalDate.parse(monthYear + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String formattedLabel = date.format(displayFormatter);
                chartData.add(new XYChart.Data<>(formattedLabel, data.cont()));
            }

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Metas");
            series.setData(chartData);
            monthsLineChart.getData().clear();
            monthsLineChart.getData().add(series);
        });
        new Thread(loadDataTask).start();
    }

    private void loadProgressChartData(int departmentId, List<GoalStatus> statuses) {
        Task<List<DashboardProgressData>> loadDataTask = new Task<>() {
            @Override
            protected List<DashboardProgressData> call() throws Exception {
                return DashboardDAO.getBottomCollaboratorProgress(departmentId, statuses);
            }
        };

        loadDataTask.setOnSucceeded(event -> {
            List<DashboardProgressData> dataFromDB = loadDataTask.getValue();

            if (dataFromDB == null || dataFromDB.isEmpty()) {
                progressBarChart.setTitle("Nenhum dado de progresso encontrado.");
                return;
            }

            ObservableList<XYChart.Data<Number, String>> chartData =
                    FXCollections.observableArrayList();

            for (DashboardProgressData data : dataFromDB) {
                chartData.add(new XYChart.Data<>(data.porcentagem(), data.nome()));
            }

            XYChart.Series<Number, String> series = new XYChart.Series<>();
            series.setName("Progresso de Metas Concluídas (%)");
            series.setData(chartData);

            progressBarChart.getData().clear();
            progressBarChart.getData().add(series);
        });
        new Thread(loadDataTask).start();
    }

    // ---------- HELPERS / CLASSES ----------

    private static class GridItem {
        Node node;
        int row = 0;
        int col = 0;
        int rowspan = 1;
        int colspan = 1;
        final int minRowspan;
        final int minColspan;
        boolean placed = false;

        GridItem(Node node, int row, int col, int colspan, int rowspan) {
            this.node = node;
            this.row = row;
            this.col = col;
            this.colspan = colspan;
            this.rowspan = rowspan;
            this.minColspan = 1;
            this.minRowspan = 1;
            this.placed = false;
        }
    }
}