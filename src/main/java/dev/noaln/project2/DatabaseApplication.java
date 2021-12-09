package dev.noaln.project2;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
import java.util.Iterator;


public class DatabaseApplication extends Application {

    private static String connection = "jdbc:mysql://cisvm-winsrv-mysql1.unfcsd.unf.edu:3307/team2";
    private static String usernames="N01440422";
    private static String pass = "Fall20210422";


    private ArrayList<ResultSet> rs = new ArrayList<>();
    @FXML
    private ListView<Entry> executionPane;
    @FXML
    private ArrayList<Entry> queryHistory = new ArrayList<>();
    @FXML
    private TextField commandQuery;
    @FXML
    private Text actionCount;
    @FXML
    private TextField address;
    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private Text connectedTo = new Text();


    static Stage stage = null;
    static Connection con = null;


    @Override
    public void start(Stage s) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(DatabaseApplication.class.getResource("DatabaseLogin.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);
        stage=s;
        stage.setTitle("SQL Controller [Group 2: Nolan, CJ, Elizabeth, Parthi]");
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
    }

    public static void main(String[] args) throws SQLException, MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        launch();
    }
    @FXML
    public void addToExecutionList(ActionEvent actionEvent){
        updateUsingDatabase();
        if(commandQuery.getText().isBlank()){
            commandQuery.setPromptText("Error, input is blank!");
            return;
        }
        String str = commandQuery.getText();
        commandQuery.clear();
        Entry current = new Entry(queryHistory.size(),str);
        queryHistory.add(current);
        executionPane.getItems().add(current); //We want to reverse this eventually

        updateActionCount();
        commandQuery.setPromptText("Added SQL Query to pending list!");
    }
    public void updateUsingDatabase(){
        try{
            connectedTo.setText("Using Database: "+con.getCatalog());
        }
        catch (SQLException E){
            E.printStackTrace();
        }
    }
    public void updateActionCount(){
        actionCount.setText("Actions Pending Execution: "+queryHistory.size());
    }
    @FXML
    public void dropSelectedEntry(){
        Entry E = executionPane.getSelectionModel().getSelectedItem();
        if(E==null){
            System.out.println("No Statement Selected to Drop, Please add one and try again.");
            return;
        }
        executionPane.getItems().remove(E);
        queryHistory.remove(E);
        updateActionCount();
        queryHistory.trimToSize();
        System.out.println("Dropped Selected Statement: "+E.query);
    }
    @FXML
    public void executeSelectedEntry(){
        Entry E = executionPane.getSelectionModel().getSelectedItem();
        if(E==null){
            System.out.println("No Statement Selected to Execute, Please add one and try again.");
            return;
        }
        Boolean b = execute(E);
        if(b){
            executionPane.getItems().remove(E); //if succeeds then remove
            queryHistory.remove(E);
            queryHistory.trimToSize();
            System.out.println("Successfully Executed Query: "+E.query);
        }
        updateActionCount();
    }
    private boolean execute(Entry E){
        try{
            Statement s = con.createStatement();
            s.execute(E.query);
            System.out.println("THIS WILL BE DISPLAYED VIA A POP-UP ON APPLICABLE QUERIES ON A LATER VERSION OF THIS CONTROLLER");
            System.out.println("Result Set: " + s.getResultSet());

            return true;
        }
        catch(SQLException e){
            System.out.println("An error has occurred while processing Statement: "+E.query);
            return false;
        }
    }

    @FXML
    public void onLoginButtonClicked(ActionEvent actionEvent) {
        try{
            con = DriverManager.getConnection(address.getText(), username.getText(), password.getText());
            System.out.println("Connected to Database: [" + con.getCatalog() + "]");
        }
        catch (Exception E){
            E.printStackTrace();
            System.out.println("Something went wrong, Please try again! ");
            return;
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
            E.printStackTrace();
            System.out.println("Something went wrong! ");

        }
    }

    @FXML
    public void onApplyQueriesClicked(ActionEvent actionEvent){
        if(queryHistory.size()==0){
            System.out.println("No Statements to Execute..");
            return;
        }
        Iterator<Entry> itr = queryHistory.iterator();
        while(itr.hasNext()){
            Entry E = itr.next();
            System.out.println("["+execute(E)+ "] ---> "+E.query);
            executionPane.getItems().remove(E);
            itr.remove();
        }
        updateActionCount();
    }
    @FXML
    public void onDiscardQueriesClicked(ActionEvent actionEvent){
        if(queryHistory.size()==0){
            System.out.println("No Statements to Drop..");
            return;
        }
        queryHistory.clear();
        executionPane.getItems().clear();
        updateActionCount();
    }
    @FXML
    public void onResetLoginClicked(){
        try{
            start(stage);
            username.setText(usernames);
            password.setText(pass);
            address.setText(connection);
        }
        catch (Exception E){

        }
    }
    @FXML
    public void onDisconnect(){
        try{
            queryHistory.clear();
            con.close();
            con=null;
            executionPane.getItems().clear();
            queryHistory.clear();
            executionPane.getItems().clear();
            updateActionCount();
            rs.clear();
            start(stage);
            System.gc();
        }
        catch  (Exception E){
            E.printStackTrace();
            System.out.println("Unable to Reset- An Error has occurred");
        }
    }

    @FXML
    public void onContextMenuRequested(ContextMenuEvent contextMenuEvent) {
    }
    class Entry {
        String query;
        Integer index;
        String errorMessage=null;
        public Entry(Integer index, String query){
            this.query = query;
            this.index = index+1;
        }
        public String toString(){
            return index+". "+query;
        }
        public void setErrorMessage(String s){
            this.errorMessage=s;
        }
    }
}