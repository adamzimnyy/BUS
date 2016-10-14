import util.Client;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import util.Server;

import java.io.IOException;

public class Main extends Application {

    @FXML
    TextField clientIp;

    @FXML
    TextField clientPort;

    @FXML
    TextField serverPort;


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("fxml/start.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    @FXML
    private void startClient(ActionEvent event) {
        Parent root;
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/client.fxml"));
            root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Socket chat");
            stage.setScene(new Scene(root, 450, 450));
            stage.show();

            Client controller = loader.getController();
            controller.startClient(clientIp.getText(),Integer.parseInt(clientPort.getText()));
            //hide this current window (if this is whant you want
            ((Node) (event.getSource())).getScene().getWindow().hide();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void startServer(ActionEvent event) {
        ((Node) (event.getSource())).getScene().getWindow().hide();
        Server s = new Server();
        if (serverPort.getText().isEmpty())
            s.start(0);
        else
            s.start(Integer.parseInt(serverPort.getText()));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
