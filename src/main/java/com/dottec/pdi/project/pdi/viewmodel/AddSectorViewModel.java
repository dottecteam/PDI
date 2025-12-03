package com.dottec.pdi.project.pdi.viewmodel;

import com.dottec.pdi.project.pdi.controllers.UserController;
import com.dottec.pdi.project.pdi.dao.DepartmentDAO;
import com.dottec.pdi.project.pdi.dao.UserDAO;
import com.dottec.pdi.project.pdi.enums.DepartmentStatus;
import com.dottec.pdi.project.pdi.enums.Role;
import com.dottec.pdi.project.pdi.model.Department;
import com.dottec.pdi.project.pdi.model.User;
import com.dottec.pdi.project.pdi.utils.FXUtils;
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
            // 1. Busca no banco todos os usuários com a role 'department_manager'
            List<User> gerentes = UserDAO.findByRole(Role.department_manager);

            // NOVO: 2. Filtra os gerentes que AINDA NÃO estão associados a um departamento.
            // O UserDAO já carrega o objeto Department se o campo department_id for preenchido.
            List<User> gerentesDisponiveis = gerentes.stream()
                    // Mantém apenas os usuários onde o campo Department é null
                    .filter(user -> user.getDepartment() == null)
                    .toList();

            ObservableList<User> observableGerentes = FXCollections.observableArrayList(gerentesDisponiveis);
            cbGerente.setItems(observableGerentes);

            // 3. Configura o Converter (restante do método)
            cbGerente.setConverter(new StringConverter<User>() {
                @Override
                public String toString(User user) {
                    return (user != null) ? user.getName() : "";
                }
                @Override
                public User fromString(String string) {
                    return cbGerente.getItems().stream()
                            .filter(user -> user.getName().equals(string))
                            .findFirst().orElse(null);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            FXUtils.showErrorMessage("Erro ao carregar gerentes", e.getMessage());
        }
    }

    @FXML
    void goBack(MouseEvent event) {
        TemplateViewModel.goBack();
    }

    @FXML
    void salvarSetor() {
        String nome = txtNome.getText();
        User gerenteSelecionado = cbGerente.getValue();
        // String contato = txtContato.getText(); // O Department Model/DAO não suporta o campo contato

        if (nome == null || nome.trim().isEmpty()) {
            FXUtils.showErrorMessage("Erro", "O nome do setor é obrigatório.");
            return;
        }

        try {
            Department novoSetor = new Department();
            novoSetor.setName(nome);
            novoSetor.setStatus(DepartmentStatus.active);

            // 1. Salva o departamento (o DAO modificado irá preencher novoSetor.id)
            DepartmentDAO.insert(novoSetor);

            // 2. Se um gerente foi selecionado (ele estará desassociado graças ao filtro),
            // vincula o novo departamento ao usuário.
            if (gerenteSelecionado != null) {
                // Associa o departamento recém-criado ao objeto User
                gerenteSelecionado.setDepartment(novoSetor);

                // Atualiza o usuário no banco de dados para setar o department_id
                UserController.updateUser(gerenteSelecionado);
            }

            // Feedback de sucesso
            FXUtils.showSuccessMessage("Sucesso", "Setor cadastrado com sucesso!");

            // Retorna para a tela de listagem
            TemplateViewModel.switchScreen("Settings.fxml");

        } catch (Exception e) {
            e.printStackTrace();
            FXUtils.showErrorMessage("Erro ao salvar", e.getMessage());
        }
    }
}