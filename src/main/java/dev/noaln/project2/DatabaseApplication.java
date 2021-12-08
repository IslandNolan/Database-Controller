package dev.noaln.project2;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;

import java.io.IOException;
import java.util.ArrayList;


public class DatabaseApplication extends Application {
    private static String connection = "jdbc:mysql://cisvm-winsrv-mysql1.unfcsd.unf.edu:3307/team2";
    private static String usernames="N01440422";
    private static String pass = "Fall20210422";





    @FXML
    private ArrayList<String> queryHistory = new ArrayList<>();
    @FXML
    private TextField commandQuery;
    @FXML
    private Text actionCount;
    @FXML
    private Button applyQueries;
    @FXML
    private Button discardQueries;
    @FXML
    private Button loginButton;
    @FXML
    private TextField address;
    @FXML
    private TextField username;
    @FXML
    private TextField password;

    static Stage stage = null;
    static Connection con=null;


    @Override
    public void start(Stage s) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(DatabaseApplication.class.getResource("DatabaseLogin.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);
        stage=s;
        stage.setTitle("SQL Controller");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws SQLException, MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        launch();
    }
    @FXML
    public void addToExecutionList(ActionEvent actionEvent){
        if(commandQuery.getText().isBlank()){
            commandQuery.setPromptText("Error, input is blank!");
            return;
        }
        String str = commandQuery.getText();
        commandQuery.clear();
        queryHistory.add(str);
        actionCount.setText("Actions Pending Execution "+queryHistory.size());
        commandQuery.setPromptText("Added SQL Query to pending list!");
    }

    @FXML
    public void onLoginButtonClicked(ActionEvent actionEvent) {
        try{
            con = DriverManager.getConnection(connection, usernames, pass);
            System.out.println("Connected to Database: [" + con.getCatalog() + "]");
        }
        catch (Exception E){
            System.out.println("Something went wrong, Please try again! ");
        }


        //This will launch assuming everything is working properly.
        enterConnectedMode();
    }
    public void enterConnectedMode() {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(DatabaseApplication.class.getResource("DatabaseConnectedPage.fxml"));
            Scene scene = new Scene(fxmlLoader.load(),640,480);
            stage.hide();
            stage.setScene(scene);
            stage.show();

        }
        catch (Exception E){
            System.out.println("Something went wrong! ");

        }

    }
    @FXML
    public void onApplyQueriesClicked(ActionEvent actionEvent){

    }
    @FXML
    public void onDiscardQueriesClicked(ActionEvent actionEvent){

    }
    @FXML
    public void onContextMenuRequested(ContextMenuEvent contextMenuEvent) {
    }
}