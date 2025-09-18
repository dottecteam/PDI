module com.dottec.pdi.project.pdi {
    requires javafx.controls;
    requires javafx.fxml;
    requires io.github.cdimascio.dotenv.java;
    requires java.sql;


    opens com.dottec.pdi.project.pdi to javafx.fxml;
    exports com.dottec.pdi.project.pdi;
}