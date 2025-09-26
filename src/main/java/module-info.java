module com.dottec.pdi.project.pdi {
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.controls;


    opens com.dottec.pdi.project.pdi to javafx.fxml;

    exports com.dottec.pdi.project.pdi;
}