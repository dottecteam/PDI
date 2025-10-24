package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.AuthController;
import com.dottec.pdi.project.pdi.dao.DashboardDAO;
import com.dottec.pdi.project.pdi.controllers.DashboardTagFrequencyController;
import com.dottec.pdi.project.pdi.enums.Role;
import com.dottec.pdi.project.pdi.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;


public class DashboardViewModel implements Initializable {

    @FXML
    private BarChart<String, Number> tagBarChart;

    private DashboardDAO tagDAO; //O objeto que acessa o banco

    public DashboardViewModel() {
        this.tagDAO = new DashboardDAO();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tagBarChart.setAnimated(false); //Deixar em 'false' para arrumar o nome das tags no grafico

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

            tagBarChart.setTitle("Tags Mais Usadas no Setor " + loggedUser.getDepartment().getName());
            //Executa o sql do gerente de departamento
            dataFetchingTask = () -> DashboardDAO.getTopTagsDepartment(departmentId);

        } else if (loggedUser.getRole() == Role.hr_manager) {

            tagBarChart.setTitle("Tags Mais Usadas");
            //Executa o sql de gerente geral
            dataFetchingTask = () -> DashboardDAO.getTopTags();

        } else {
            showAlert("Permissão necessária", "Você não tem permissão para visualizar este gráfico.");
            return;
        }


        loadChartData(dataFetchingTask);
    }


    private void loadChartData(Callable<List<DashboardTagFrequencyController>> dataFetchingTask) {

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
                            newNode.setStyle("-fx-bar-fill: #9032BB;");
                        } else {
                            newNode.setStyle("-fx-bar-fill: #AF69CD;");
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


//DashboardViewModel.java