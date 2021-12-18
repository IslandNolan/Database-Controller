package dev.noaln.project2;

import javafx.application.Application;
import javafx.application.Preloader;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;

import javax.xml.transform.Result;
import java.net.MalformedURLException;
import java.sql.*;

import java.io.IOException;
import java.sql.Date;
import java.util.*;

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
    private Text connectedTo;

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
            if(s.execute(E.query)){ }
            printResultSet(E,s.getResultSet());
            return true;
        }
        catch(SQLException e){
            e.printStackTrace();
            System.out.println("An error has occurred while processing Statement: "+E.query);
            return false;
        }
    }

    @FXML
    public void onLoginButtonClicked(ActionEvent actionEvent) {

        //address.setText("jdbc:mysql://localhost/team2");
        //username.setText("Nolan");
        //password.setText("jyzma0");
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
    public void printResultSet(Entry entry,ResultSet rs){
        try{
            Stage temp = new Stage();
            ResultSetTableView r = new ResultSetTableView(rs);
            temp.setScene(new Scene(r));
            temp.setTitle(entry.toString());

            temp.show();
        }
        catch (Exception E){}

    }
    public void createTransactionIfVoid(){
        try{
            if(!tableExists("Transaction")){ //Ensure that the Table "Transaction" Exists.
                Statement smt = con.createStatement();
                con.beginRequest();
                smt.executeUpdate("CREATE TABLE Transaction ( Id INT NOT NULL PRIMARY KEY AUTO_INCREMENT, Success CHAR(5), User VARCHAR(100), Date Date NOT NULL, Query VARCHAR(200),IP_Address VARCHAR(16),Operating_System VARCHAR(20));");
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


    class ResultSetTableView extends TableView {

        private ResultSet resultSet;

        private List<String> columnNames = new ArrayList<>();

        public ResultSetTableView(ResultSet resultSet) throws SQLException {
            super();
            this.resultSet = resultSet;
            buildData();
        }
        private void buildData() throws SQLException {
            ObservableList<ObservableList> data = FXCollections.observableArrayList();
            for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {

                final int j = i;
                TableColumn col = new TableColumn(resultSet.getMetaData().getColumnName(i + 1));
                col.setPrefWidth(resultSet.getMetaData().getColumnName(i+1).length()*15);
                col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> {
                    if (param.getValue().get(j) != null) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    } else {
                        return null;
                    }
                });
                getColumns().addAll(col);
                this.columnNames.add(col.getText());
            }
            while (resultSet.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {

                    row.add(resultSet.getString(i));
                }
                data.add(row);
            }
            setItems(data);
        }
        public List<String> getColumnNames() {
            return columnNames;
        }
    }
}