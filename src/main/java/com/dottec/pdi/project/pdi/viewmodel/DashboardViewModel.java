package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.*;
import com.dottec.pdi.project.pdi.dao.DashboardDAO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.dottec.pdi.project.pdi.enums.*;
import com.dottec.pdi.project.pdi.model.Department;
import com.dottec.pdi.project.pdi.model.Goal;
import com.dottec.pdi.project.pdi.model.Tag;
import com.dottec.pdi.project.pdi.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;
import java.util.concurrent.Callable;


public class DashboardViewModel implements Initializable {

    @FXML
    private BarChart<String, Number> tagBarChart;

    @FXML
    private PieChart taskPieChart;

    @FXML
    private Label percentage;

    @FXML
    private LineChart monthsLineChart;

    @FXML
    private BarChart<Number, String> progressBarChart;

    // --- Filters ---
    private LocalDate startDay = LocalDate.now().withMonth(1).withDayOfMonth(1);
    private LocalDate endDay = LocalDate.now().withMonth(12).withDayOfMonth(31);
    private List<ActivityStatus> filteredActivityStatuses = new ArrayList<>(Arrays.asList(ActivityStatus.values()));
    private List<GoalStatus> filteredGoalStatuses = new ArrayList<>(Arrays.asList(GoalStatus.values()));
    private List<Department> filteredDepartments = DepartmentController.findAllDepartments();
    private List<Tag> filteredTags = TagController.findAllTags();
    private List<TagType> filteredTagTypes = new ArrayList<>(Arrays.asList(TagType.values()));

    private final String STATUS_CONCLUIDO = "completed";

    private DashboardDAO tagDAO; //O objeto que acessa o banco

    public DashboardViewModel() {
        this.tagDAO = new DashboardDAO();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setFilter();
        tagBarChart.setAnimated(false); //Deixar em 'false' para arrumar o nome das tags no grafico
        taskPieChart.setAnimated(false);
        taskPieChart.setLegendVisible(false); //Não exibe a legenda
        monthsLineChart.setAnimated(false);
        progressBarChart.setAnimated(false);
        progressBarChart.setLegendVisible(false);

        //Pega a ID do usuário logado
        User loggedUser = AuthController.getInstance().getLoggedUser();

        if (loggedUser == null) {
            showAlert("Usuário não encontrado","Erro: Nenhum usuário está logado.");
            return;
        }


        Callable<List<DashboardTagFrequencyController>> dataFetchingTask;

        if (loggedUser.getRole() == Role.department_manager) {

            if (loggedUser.getDepartment() == null) {
                showAlert("Departamento não encontrado", "Erro: O usuário é um gerente, mas não possui um departamento.");
                return;
            }
            //Pega o ID depois de verificar que não é nulo
            int departmentId = loggedUser.getDepartment().getId();

            tagBarChart.setTitle("Tags Mais Usadas");
            //Executa o sql do gerente de departamento
            dataFetchingTask = () -> DashboardDAO.getTopTagsDepartment(departmentId);

            taskPieChart.setTitle("Relação de conclusão de tarefas");
            monthsLineChart.setTitle("Progresso médio mensal de tarefas");
            progressBarChart.setTitle("Objetivos com menor progresso");
            loadPieChartData(departmentId);
            loadLineChartData(departmentId);
            loadProgressChartData(departmentId);

        } else if (loggedUser.getRole() == Role.hr_manager) {

            tagBarChart.setTitle("Tags Mais Usadas");
            //Executa o sql de gerente geral
            dataFetchingTask = () -> DashboardDAO.getTopTags();

        } else {
            showAlert("Permissão necessária", "Você não tem permissão para visualizar este gráfico.");
            return;
        }


        loadBarChartData(dataFetchingTask);
    }

    private void setFilter(){
        FilterMenuViewModel filterMenu = new FilterMenuViewModel();

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

        //Department based filter
        List<Node> departmentCheckBoxes = new ArrayList<>();
        filteredDepartments.sort(Comparator.comparing(Department::getName));
        filteredDepartments.forEach(department -> {
            CheckBox checkBox  = new CheckBox(department.getName());
            checkBox.setSelected(true);
            checkBox.setOnAction(e -> {
                if(filteredDepartments.contains(department)){
                    filteredDepartments.remove(department);
                } else {
                    filteredDepartments.add(department);
                }
            });
            departmentCheckBoxes.add(checkBox);
        });
        filterMenu.addFilterField("Filtrar por departamento", departmentCheckBoxes);

        //Goal status based filter
        List<Node> goalStatusCheckBoxes = new ArrayList<>();
        for(GoalStatus goalStatus : GoalStatus.values()){
            CheckBox checkBox = new CheckBox();
            checkBox.setSelected(true);
            checkBox.setOnAction(e -> {
                if(!filteredGoalStatuses.contains(goalStatus)) {
                    filteredGoalStatuses.add(goalStatus);
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

        //Activity status based filter
        List<Node> activityStatusCheckBoxes = new ArrayList<>();
        for(ActivityStatus activityStatus : ActivityStatus.values()){
            CheckBox checkBox = new CheckBox();
            checkBox.setSelected(true);
            checkBox.setOnAction(e -> {
                if(!filteredActivityStatuses.contains(activityStatus)) {
                    filteredActivityStatuses.add(activityStatus);
                } else {
                    filteredActivityStatuses.remove(activityStatus);
                }
            });
            switch (activityStatus.toString()) {
                case "in_progress" -> checkBox.setText("Em progresso");
                case "pending" -> checkBox.setText("Pendente");
                case "completed" -> checkBox.setText("Completo");
                case "canceled" -> checkBox.setText("Cancelado");
            }
            activityStatusCheckBoxes.add(checkBox);
        }
        filterMenu.addFilterField("Filtrar por status de atividade", activityStatusCheckBoxes);

        //Tag type based filter
        List<Node> tagTypesCheckBoxes = new ArrayList<>();
        filteredTagTypes.forEach(tagType -> {
            CheckBox checkBox  = new CheckBox();
            checkBox.setSelected(true);
            if(tagType.equals(TagType.SOFT)){
                checkBox.setText("Soft Skill");
            } else if (tagType.equals(TagType.HARD)){
                checkBox.setText("Hard Skill");
            }
            checkBox.setOnAction(e -> {
                if(filteredTagTypes.contains(tagType)){
                    filteredTagTypes.remove(tagType);
                } else {
                    filteredTagTypes.add(tagType);
                }
            });
            tagTypesCheckBoxes.add(checkBox);
        });
        filterMenu.addFilterField("Filtrar por skills", tagTypesCheckBoxes);

        //Tag based filter
        List<Node> tagSoftCheckBoxes = new ArrayList<>();
        List<Node> tagHardCheckBoxes = new ArrayList<>();
        filteredTags.sort(Comparator.comparing(Tag::getName));
        filteredTags.forEach(tag -> {
            CheckBox checkBox  = new CheckBox(tag.getName());
            checkBox.setSelected(true);
            checkBox.setOnAction(e -> {
                if(filteredTags.contains(tag)){
                    filteredTags.remove(tag);
                } else {
                    filteredTags.add(tag);
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

    private void handleFilter(){
        //TODO CRIAR LÓGICAS DE FILTRAGEM
    }


    private void loadBarChartData(Callable<List<DashboardTagFrequencyController>> dataFetchingTask) {

        //A Task retorna a List<TagFrequency> que o DAO buscou
        Task<List<DashboardTagFrequencyController>> loadDataTask = new Task<>() {
            @Override
            protected List<DashboardTagFrequencyController> call() throws Exception {
                return dataFetchingTask.call();
            }
        };


        loadDataTask.setOnSucceeded(event -> {
            List<DashboardTagFrequencyController> dataFromDB = loadDataTask.getValue(); //Pega o resultado

            if (dataFromDB == null || dataFromDB.isEmpty()) {
                showAlert("Sem Dados", "Nenhuma tag encontrada para esta visualização.");
                return;
            }

            ObservableList<XYChart.Data<String, Number>> chartData =
                    FXCollections.observableArrayList();

            //Indice para definir a cor no grafico
            int index = 0;

            for (DashboardTagFrequencyController freq : dataFromDB) {
                XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(freq.nome(), freq.cont());

                final int indice = index;

                dataPoint.nodeProperty().addListener((ov, oldNode, newNode) -> {
                    if (newNode != null) {
                        if (indice % 2 == 0) {
                            newNode.setStyle("-fx-bar-fill: #374649;");
                        } else {
                            newNode.setStyle("-fx-bar-fill: #708F95;");
                        }


                        //Animações improvisadas pra dar uma sensação visual mais agradavel
                        newNode.setOpacity(0);
                        newNode.setTranslateY(15);

                        FadeTransition ft = new FadeTransition(Duration.millis(600), newNode);
                        ft.setToValue(1.0);

                        TranslateTransition tt = new TranslateTransition(Duration.millis(500), newNode);
                        tt.setToY(0); //Anima para a posição Y original
                        tt.setInterpolator(Interpolator.EASE_OUT);

                        Duration delay = Duration.millis(indice * 50);
                        ft.setDelay(delay);
                        tt.setDelay(delay);

                        //Executa as animações
                        ft.play();
                        tt.play();
                    }
                });

                chartData.add(dataPoint); //Adiciona o dado à lista
                index++;
            }

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Comparação das tags mais utilizadas");
            series.setData(chartData);

            //Coloca os dados no gráfico
            tagBarChart.getData().clear();
            tagBarChart.getData().add(series);
        });

        //O que fazer se a Task falhar
        loadDataTask.setOnFailed(event -> {
            loadDataTask.getException().printStackTrace();
        });

        new Thread(loadDataTask).start();
    }

    private void loadPieChartData(int departmentId) {

        percentage.setText("Carregando status...");

        Task<List<DashboardStatusData>> loadDataTask = new Task<>() {
            @Override
            protected List<DashboardStatusData> call() throws Exception {
                return DashboardDAO.getGoalStatusCountsForDepartment(departmentId);
            }
        };

        loadDataTask.setOnSucceeded(event -> {
            List<DashboardStatusData> dataFromDB = loadDataTask.getValue();

            if (dataFromDB == null || dataFromDB.isEmpty()) {
                showAlert("Sem Metas", "Nenhuma meta (goal) encontrada para este setor.");
                percentage.setText("Nenhuma meta encontrada.");
                return;
            }

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

            int totalTasks = 0;
            int completedTasks = 0;

            for (DashboardStatusData data : dataFromDB) {
                String statusDoBanco = data.status();
                int contagem = data.cont();

                String nomeParaExibir = statusDoBanco;
                switch(statusDoBanco.toLowerCase()) {
                    case "completed": nomeParaExibir = "Concluído"; break;
                    case "in_progress":   nomeParaExibir = "Em Progresso"; break;
                }


                PieChart.Data pieData = new PieChart.Data(nomeParaExibir, contagem);
                pieChartData.add(pieData);
                applyPieSliceColor(pieData, statusDoBanco);


                totalTasks += contagem;
                if (statusDoBanco.equalsIgnoreCase(STATUS_CONCLUIDO)) {
                    completedTasks = contagem;
                }
            }


            taskPieChart.setData(pieChartData);

            //Define o Label da porcentagem
            if (totalTasks > 0) {
                double percentageValue = ((double) completedTasks / totalTasks) * 100.0;
                percentage.setText(String.format("Metas Concluídas: %.1f%%", percentageValue));
            } else {
                percentage.setText("Nenhuma meta encontrada.");
            }
        });

        loadDataTask.setOnFailed(event -> {
            percentage.setText("Erro ao carregar status.");
            loadDataTask.getException().printStackTrace();
        });

        new Thread(loadDataTask).start();
    }

    private void loadLineChartData(int departmentId) {

        Task<List<DashboardMonthlyData>> loadDataTask = new Task<>() {
            @Override
            protected List<DashboardMonthlyData> call() throws Exception {
                return DashboardDAO.getMonthlyActivityCounts(departmentId);
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

            // Formatação para "mes/ano"
            DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("MMM/yy");

            for (DashboardMonthlyData data : dataFromDB) {
                String monthYear = data.mesAno();

                //Adiciona "-01" para o parse entender que é um dia
                LocalDate date = LocalDate.parse(monthYear + "-01", dbFormatter);
                String formattedLabel = date.format(displayFormatter);

                chartData.add(new XYChart.Data<>(formattedLabel, data.cont()));
            }

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Metas");
            series.setData(chartData);


            monthsLineChart.getData().clear();
            monthsLineChart.getData().add(series);
        });

        loadDataTask.setOnFailed(event -> {
            monthsLineChart.setTitle("Erro ao carregar dados.");
            loadDataTask.getException().printStackTrace();
        });

        new Thread(loadDataTask).start();
    }


    private void loadProgressChartData(int departmentId) {

        Task<List<DashboardProgressData>> loadDataTask = new Task<>() {
            @Override
            protected List<DashboardProgressData> call() throws Exception {
                // Chama o método DAO correto
                return DashboardDAO.getBottomCollaboratorProgress(departmentId);
            }
        };

        loadDataTask.setOnSucceeded(event -> {
            List<DashboardProgressData> dataFromDB = loadDataTask.getValue();

            if (dataFromDB == null || dataFromDB.isEmpty()) {
                progressBarChart.setTitle("Nenhum dado de progresso encontrado.");
                return;
            }

            // Tipos corretos: Número no X, String no Y
            ObservableList<XYChart.Data<Number, String>> chartData =
                    FXCollections.observableArrayList();

            for (DashboardProgressData data : dataFromDB) {
                // (Valor X = Porcentagem, Valor Y = Nome)
                chartData.add(new XYChart.Data<>(data.porcentagem(), data.nome()));
            }

            // Tipos corretos: Número no X, String no Y
            XYChart.Series<Number, String> series = new XYChart.Series<>();
            series.setName("Progresso de Metas Concluídas (%)");
            series.setData(chartData);

            // Adiciona ao gráfico correto
            progressBarChart.getData().clear();
            progressBarChart.getData().add(series);
        });

        loadDataTask.setOnFailed(event -> {
            progressBarChart.setTitle("Erro ao carregar progresso.");
            loadDataTask.getException().printStackTrace();
        });

        new Thread(loadDataTask).start();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void applyPieSliceColor(PieChart.Data pieData, String statusOriginal) {
        pieData.nodeProperty().addListener((ov, oldNode, newNode) -> {
            if (newNode != null) {
                String color = "#808080";
                switch (statusOriginal.toLowerCase()) {
                    case "completed": color = "#01B8AA"; break;
                    case "in_progress":   color = "#FD625E"; break;
                }

                newNode.setStyle("-fx-background-color: " + color + ";");
            }
        });
    }
}


//DashboardViewModel.java