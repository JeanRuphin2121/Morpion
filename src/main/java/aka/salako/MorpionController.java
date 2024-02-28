package aka.salako;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;

import java.net.URL;
import java.util.ResourceBundle;

public class MorpionController implements Initializable {

    @FXML
    private Label labelXCount;

    @FXML
    private Label labelOCount;

    @FXML
    private Label labelEmptyCount;
    @FXML
    private HBox plateau;

    @FXML
    public Label victoire;

    static GridPane grid;

    @FXML
    void restart(ActionEvent event) {
        plateau.getChildren().remove(grid);
        TicTacToeModel.getInstance().restart();
        this.initialize(null, null);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        grid = new GridPane();
        grid.setPrefHeight(34* TicTacToeModel.BOARD_HEIGHT);
        grid.setPrefWidth(34* TicTacToeModel.BOARD_WIDTH);

        for (int i = 0; i < TicTacToeModel.BOARD_HEIGHT; i++) {
            grid.getRowConstraints().add(new RowConstraints(100));
        }
        for (int j = 0; j < TicTacToeModel.BOARD_WIDTH; j++) {
            grid.getColumnConstraints().add(new ColumnConstraints(100));
        }

        for (int i = 0; i < TicTacToeModel.BOARD_HEIGHT; i++) {
            for (int j = 0; j < TicTacToeModel.BOARD_WIDTH; j++) {
                grid.add(new TicTacToeSquare(i,j), j, i);
            }
        }
        plateau.setAlignment(Pos.CENTER);
        grid.setGridLinesVisible(true);
        plateau.getChildren().add(grid);

        TicTacToeModel ticTacToeModel = TicTacToeModel.getInstance();
        updateUIProperties(ticTacToeModel);


    }

    private void updateUIProperties(TicTacToeModel ticTacToeModel) {
        // Exemple de mise à jour de la propriété 'text' de la Label 'victoire'
        victoire.textProperty().bind(ticTacToeModel.getEndOfGameMessage());

        // Mise à jour des compteurs
        labelXCount.textProperty().bind(ticTacToeModel.getXCount().asString("%d case pour ✕ "));
        labelOCount.textProperty().bind(ticTacToeModel.getOCount().asString("%d case pour ○ "));
        labelEmptyCount.textProperty().bind(ticTacToeModel.getEmptyCount().asString("%d cases libres"));
    }
}
