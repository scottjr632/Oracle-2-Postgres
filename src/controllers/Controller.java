package controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static resources.OracleCon.*;
import static resources.ReaderWriter.*;
import static resources.ToPostgres.*;

public class Controller implements Initializable {

    // FXML fields
    public TextField oraHost;
    public TextField oraPort;
    public TextField oraSchema;
    public TextField oraDatabase;
    public TextField oraUser;
    public TextField pgHost;
    public TextField pgPort;
    public TextField pgSchema;
    public TextField pgDatabase;
    public TextField pgUser;
    public TextField pgPassword;
    public TextField oraPassword;
    public Button btnGo;
    
    private Alert runningAlert = new Alert(AlertType.INFORMATION, "Running...");
    private Alert finishedAlert = new Alert(AlertType.CONFIRMATION, "Finished...");
    private Alert errorAlert = new Alert(AlertType.ERROR, "Something went wrong!");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnGo.setOnAction(e -> btnGoAction());
    }

    // Create oracle DDL temp file
    private File createOracleTemp() {
        ResultSet rs = getOracleDDL(oraHost.getText(), Integer.parseInt(oraPort.getText()),
                oraUser.getText(), oraPassword.getText(), oraDatabase.getText(), oraSchema.getText());
        try {
            assert rs != null;
            return createTempFile(rs);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Convert oracle temp file to postgres DDL file
    private void executePgDDLFile(File tempFile){
        try {
            // Create single connection to Postgres
            Connection conn = connect(pgHost.getText(), pgPort.getText(),
                    pgDatabase.getText(), pgUser.getText(), pgPassword.getText());
            // Create Pg DDL file
            File pgFile = createPgFile(tempFile);
            // Create Postgres schema
            createSchema(conn, pgSchema.getText());
            // Execute the newly created Pg DDL file
            executePgFile(conn, pgFile);
            conn.close();
        } catch (IOException | SQLException e) {
            // Show warning message
            e.printStackTrace();
            errorAlert.show();
        }
    }

    private void btnGoAction(){
    	runningAlert.show();
        File oracleTempFile = createOracleTemp();
        executePgDDLFile(oracleTempFile);
        finishedAlert.show();
        try {
			Desktop.getDesktop().open(new File("ErrorFile.txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			new Alert(AlertType.ERROR, "ErrorFile.txt not found...");
		}
  }
}













