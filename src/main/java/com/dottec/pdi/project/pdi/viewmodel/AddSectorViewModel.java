package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.dao.DepartmentDAO;
import com.dottec.pdi.project.pdi.dao.UserDAO;
import com.dottec.pdi.project.pdi.enums.DepartmentStatus;
import com.dottec.pdi.project.pdi.enums.Role;
import com.dottec.pdi.project.pdi.model.Department;
import com.dottec.pdi.project.pdi.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AddSectorViewModel implements Initializable {

    @FXML
    private TextField txtNome;

    @FXML
    private ComboBox<User> cbGerente; // Agora tipado com <User> para manipular o objeto completo

    @FXML
    private TextField txtContato;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        carregarGerentes();
    }

    private void carregarGerentes() {
        try {
            // 1. Busca no banco apenas usuários com a role 'department_manager'
            // Usa o método findByRole que adicionamos no UserDAO
            List<User> gerentes = UserDAO.findByRole(Role.department_manager);

            ObservableList<User> observableGerentes = FXCollections.observableArrayList(gerentes);
            cbGerente.setItems(observableGerentes);

            // 2. Configura o Converter para exibir apenas o NOME na interface,
            // mas mantendo o objeto User selecionado no valor
            cbGerente.setConverter(new StringConverter<User>() {
                @Override
                public String toString(User user) {
                    // Se o usuário não for nulo, mostra o nome. Senão, mostra vazio.
                    return (user != null) ? user.getName() : "";
                }

                @Override
                public User fromString(String string) {
                    // Método inverso (necessário caso o combo fosse editável)
                    return cbGerente.getItems().stream()
                            .filter(user -> user.getName().equals(string))
                            .findFirst().orElse(null);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            TemplateViewModel.showErrorMessage("Erro ao carregar gerentes", e.getMessage());
        }
    }

    @FXML
    void goBack(MouseEvent event) {
        TemplateViewModel.goBack();
    }

    @FXML
    void salvarSetor() {
        String nome = txtNome.getText();
        User gerenteSelecionado = cbGerente.getValue(); // Recupera o objeto User selecionado

        if (nome == null || nome.trim().isEmpty()) {
            TemplateViewModel.showErrorMessage("Erro", "O nome do setor é obrigatório.");
            return;
        }

        try {
            Department novoSetor = new Department();
            novoSetor.setName(nome);
            novoSetor.setStatus(DepartmentStatus.active);

            // LÓGICA PARA SALVAR O GERENTE:
            // Como seu modelo Department atual ainda não tem o campo 'managerId',
            // deixei comentado abaixo. Assim que você adicionar o campo no banco/modelo, basta descomentar:
            /*
            if (gerenteSelecionado != null) {
                novoSetor.setManagerId(gerenteSelecionado.getId());
            }
            */

            DepartmentDAO.insert(novoSetor);

            // Feedback de sucesso
            TemplateViewModel.showSuccessMessage("Sucesso", "Setor cadastrado com sucesso!");

            // Retorna para a tela de listagem
            TemplateViewModel.switchScreen("Settings.fxml");

        } catch (Exception e) {
            e.printStackTrace();
            TemplateViewModel.showErrorMessage("Erro ao salvar", e.getMessage());
        }
    }
}