module com.dottec.pdi.project.pdi {
    requires javafx.fxml;
    requires java.sql;
    requires io.github.cdimascio.dotenv.java;
    requires javafx.graphics;
    requires javafx.controls;
    requires jbcrypt;

    opens com.dottec.pdi.project.pdi to javafx.fxml;
    opens com.dottec.pdi.project.pdi.controllers;
    opens com.dottec.pdi.project.pdi.viewmodel;

    exports com.dottec.pdi.project.pdi;
    exports com.dottec.pdi.project.pdi.controllers;
    exports com.dottec.pdi.project.pdi.utils;
    exports com.dottec.pdi.project.pdi.viewmodel;
    opens com.dottec.pdi.project.pdi.utils to javafx.fxml;
}