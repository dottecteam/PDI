module com.dottec.pdi.project.pdi {
    requires javafx.fxml;
    requires java.sql;
    requires io.github.cdimascio.dotenv.java;
    requires javafx.graphics;
    requires javafx.controls;


    opens com.dottec.pdi.project.pdi to javafx.fxml;

    exports com.dottec.pdi.project.pdi;
}