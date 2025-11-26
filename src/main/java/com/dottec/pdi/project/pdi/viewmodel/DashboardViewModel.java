package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.*;
import com.dottec.pdi.project.pdi.dao.DashboardDAO;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.dottec.pdi.project.pdi.enums.Role;
import com.dottec.pdi.project.pdi.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Alert;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;


public class DashboardViewModel implements Initializable {

    @FXML
    private BarChart<String, Number> tagBarChart;

    @FXML
    private PieChart taskPieChart;

    @FXML
    private Label percentage;

    @FXML
    private Button exportButton;

    @FXML
    private LineChart monthsLineChart;

    @FXML
    private BarChart<Number, String> progressBarChart;

    private final String STATUS_CONCLUIDO = "completed";

    private DashboardDAO tagDAO; //O objeto que acessa o banco

    public DashboardViewModel() {
        this.tagDAO = new DashboardDAO();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tagBarChart.setAnimated(false); //Deixar em 'false' para arrumar o nome das tags no grafico
        taskPieChart.setAnimated(false);
        taskPieChart.setLegendVisible(false); //Não exibe a legenda
        monthsLineChart.setAnimated(false);
        progressBarChart.setAnimated(false);
        progressBarChart.setLegendVisible(false);

        //Pega a ID do usuário logado
        User loggedUser = AuthController.getInstance().getLoggedUser();

        if (loggedUser == null) {
            showAlert("Usuário não encontrado", "Erro: Nenhum usuário está logado.");
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

    @FXML
    private void handleExportData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Relatório PDI");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("CSV files (*.csv)", "*.csv"));

        User loggedUser = AuthController.getInstance().getLoggedUser();
        String defaultFileName = "relatorio_pdi.csv";
        boolean isDepartmentManager = loggedUser != null && loggedUser.getRole() == Role.department_manager;

        if (isDepartmentManager && loggedUser.getDepartment() != null) {
            int departmentId = loggedUser.getDepartment().getId();
            defaultFileName = "relatorio_departamento_" + departmentId + ".csv";
        } else {
            defaultFileName = "relatorio_geral_pdi.csv";
        }

        fileChooser.setInitialFileName(defaultFileName);

        File file = fileChooser.showSaveDialog(exportButton.getScene().getWindow());

        if (file != null) {
            Path savePath = file.toPath();

            try {
                if (isDepartmentManager && loggedUser.getDepartment() != null) {
                    int departmentId = loggedUser.getDepartment().getId();
                    ExportDataController.sendDepartmentConsolidatedCSV(savePath.toString(), departmentId);
                } else {
                    ExportDataController.sendConsolidatedCSV(savePath.toString());
                }

                TemplateViewModel.showSuccessMessage("Exportação Concluída",
                        "O relatório foi salvo em:\n" + savePath.toString());

            } catch (Exception e) {
                e.printStackTrace();
                TemplateViewModel.showErrorMessage("Erro na Exportação",
                        "Falha ao salvar o arquivo:\n" + e.getMessage());
            }
        }
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
                switch (statusDoBanco.toLowerCase()) {
                    case "completed":
                        nomeParaExibir = "Concluído";
                        break;
                    case "in_progress":
                        nomeParaExibir = "Em Progresso";
                        break;
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
                    case "completed":
                        color = "#01B8AA";
                        break;
                    case "in_progress":
                        color = "#FD625E";
                        break;
                }

                newNode.setStyle("-fx-background-color: " + color + ";");
            }
        });
    }
}


//DashboardViewModel.java