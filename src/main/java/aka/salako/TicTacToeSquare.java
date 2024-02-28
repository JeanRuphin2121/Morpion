package aka.salako;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.Objects;


public class TicTacToeSquare extends TextField {

    private static TicTacToeModel model = TicTacToeModel.getInstance();

    private ObjectProperty<Owner> ownerProperty = new SimpleObjectProperty<>(Owner.NONE);

    private BooleanProperty winnerProperty = new SimpleBooleanProperty(false);

    public ObjectProperty<Owner> ownerProperty() {
        if (Objects.equals(model.turnProperty().toString(), new SimpleObjectProperty<>(Owner.FIRST).toString())) {
            ownerProperty = new SimpleObjectProperty<>(Owner.FIRST);
        }else {
            ownerProperty = new SimpleObjectProperty<>(Owner.SECOND);
        }
        return ownerProperty;
    }

    public BooleanProperty colorProperty() {
        return null;
    }

    public TicTacToeSquare(int row, int column) {

        this.setEditable(false);
        this.setPrefWidth(99.9);
        this.setFont(Font.font("System", 80));
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(-10,0,-10,0));
        this.setOnMouseClicked(mouseEvent ->{

            if (model.legalMove(row, column).getValue()) {
                if (ownerProperty().getValue()==Owner.FIRST) {
                    this.setText("✕");
                } else {
                    this.setText("○");
                }
                model.play(row, column);
            }
            if (model.winningBoard[row][column].getValue()) {
                this.setFont(Font.font("Verdana", 120));
            }

        });
        this.setOnMouseEntered(mouseEvent ->{
            if (model.legalMove(row, column).getValue()){
                this.setCursor(Cursor.HAND); // Définir le curseur de la main
                this.setStyle("-fx-background-color: GREEN");
            }
            else{
                this.setCursor(Cursor.HAND); // Définir le curseur de la main
                this.setStyle("-fx-background-color: RED");
            }
        });
        this.setOnMouseExited(mouseEvent -> {
            this.setStyle("-fx-background-color: NONE");
        });


    }

}
