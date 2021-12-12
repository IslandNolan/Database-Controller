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
import javafx.scene.control.*;
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
import java.sql.Date;
import java.util.*;


public class DatabaseApplication extends Application {

    private static String connection = "jdbc:mysql://cisvm-winsrv-mysql1.unfcsd.unf.edu:3307/team2";
    private static String usernames="N01440422";
    private static String pass = "Fall20210422";



    private ArrayList<Transaction> thistory = new ArrayList<>();
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

        ObservableList<Entry> selectedStatements = executionPane.getSelectionModel().getSelectedItems();
        if(selectedStatements.size()==0){
            System.out.println("No Statement Selected to Execute, Please add one and try again.");
            return;
        }
        selectedStatements.forEach(e -> {
            Boolean b = execute(e);
            if(b){
                executionPane.getItems().remove(e); //if succeeds then remove
                queryHistory.remove(e);
                queryHistory.trimToSize();
                System.out.println("Successfully Executed Query: "+e.query);
            }
        });
        updateActionCount();
    }
    private boolean execute(Entry E){
        try{

            createTransactionIfVoid();
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

        address.setText("jdbc:mysql://localhost/team2");
        username.setText("Nolan");
        password.setText("jyzma0");
        try{
            con = DriverManager.getConnection(address.getText(), username.getText(), password.getText());
            System.out.println("Connected to Database: ["+con.getCatalog()+" @ "+con.getMetaData().getURL()+"]");
        }
        catch (Exception E){
            E.printStackTrace();
            System.out.println("Something went wrong, Please try again! ");
            return;
        }
        //This will launch if everything is working properly.
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
    public void createTransactionIfVoid(){
        try{
            System.out.print("--Triggered Transaction Table Check-- ");
            if(!tableExists("Transaction")){ //Ensure that the Table "Transaction" Exists.
                Statement smt = con.createStatement();
                con.beginRequest();
                smt.executeUpdate("CREATE TABLE Transaction (\n" +
                        "\tID INT NOT NULL PRIMARY KEY AUTO_INCREMENT,\n" +
                        "    Sch VARCHAR(100),\n" +
                        "    Db VARCHAR(100),\n" +
                        "    Usr VARCHAR(100),\n" +
                        "    Dt Date NOT NULL,\n" +
                        "    Command VARCHAR(200),\n" +
                        "    Ip VARCHAR(16),\n" +
                        "    OS VARCHAR(20)\n" +
                        ");");
            }
        }
        catch(Exception ignored){ System.out.println("Failed to create Table: Transaction.. ");}

    }
    public boolean tableExists(String tableName){
        try{

            ResultSet r = con.createStatement().executeQuery("SHOW TABLES;");
            HashSet<String> temp = new HashSet<>();
            while (r.next()) {
                for (int i = 1; i <= r.getMetaData().getColumnCount(); i++) {
                    temp.add(r.getString(i));
                }
            }
            return temp.contains(tableName.toLowerCase(Locale.ROOT));
        }
        catch (Exception E){
            System.out.println("[Critical] An error has occurred while fetching list of tables");
            return false;

        }
    }
    class Entry { //Two inner classes.
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
    class Transaction {
        String schema;
        String database;
        String user;
        Date date;
        String query;
        String ipAddress;
        String operatingSystem;


        public Transaction(String schema,String database,String user,String query){
            this.schema = schema;
            this.database = database;
            this.user = user;
            this.query = query;

        }
        public String toString(){ //Simple toString
            return " | "+date.toString()+" | "+user+" | "+query+" | ";
        }
    }
}