package yappy.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import yappy.Yappy;

/**
 * Controls Yappy's main conversation window.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    @FXML
    private Button sendButton;

    private final Image userImage = new Image(
            this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image yappyImage = new Image(
            this.getClass().getResourceAsStream("/images/DaDuke.png"));

    private Yappy yappy;

    /**
     * Keeps the latest dialog visible as the conversation grows.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Supplies the chatbot that handles commands and adds its welcome dialog.
     */
    public void setYappy(Yappy yappy) {
        this.yappy = yappy;
        dialogContainer.getChildren().add(
                DialogBox.getYappyDialog(yappy.getWelcomeMessage(), yappyImage, yappy.getCommandType()));
    }

    /**
     * Appends the user's input and Yappy's response, then clears the input field.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = yappy.getResponse(input);
        String commandType = yappy.getCommandType();

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getYappyDialog(response, yappyImage, commandType));
        userInput.clear();
    }
}
