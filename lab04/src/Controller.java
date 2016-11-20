import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    Button button;

    @FXML
    TextField indexField, keyField;

    char[] alphabet = "abcdefghijklmnopqrstuvwxyzO".toCharArray();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        button.setOnAction(this::generateKey);
    }

    private void generateKey(ActionEvent ae) {
        int index = Integer.parseInt(indexField.getText());
        int modulo = index % 26;
        int a, c;
        int esi = 26;
        String key = "";
        for (c = 0; c < 26; c++) {
            a = c * 7;
            a += modulo;
            key += alphabet[a % esi];
        }
        for (c = 26; c < 52; c++) {
            a = c + c * 8;
            a+= modulo;
            key += alphabet[a % esi];
        }
        keyField.setText(key);
    }
}