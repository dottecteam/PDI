module com.dottec.pdi.project.pdi {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.dottec.pdi.project.pdi to javafx.fxml;

    exports com.dottec.pdi.project.pdi;
}