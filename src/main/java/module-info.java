module com.dottec.pdi.project.pdi {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires io.github.cdimascio.dotenv.java;


    opens com.dottec.pdi.project.pdi to javafx.fxml;
    opens com.dottec.pdi.project.pdi.controllers;

    exports com.dottec.pdi.project.pdi;
    exports com.dottec.pdi.project.pdi.controllers;
    exports com.dottec.pdi.project.pdi.utils;
    opens com.dottec.pdi.project.pdi.utils to javafx.fxml;
}