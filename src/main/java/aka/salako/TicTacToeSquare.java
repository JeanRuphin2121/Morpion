package aka.salako;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;

public class TicTacToeSquare extends TextField {

    private static TicTacToeModel model = TicTacToeModel.getInstance();

    private ObjectProperty<Owner> ownerProperty = new SimpleObjectProperty<>(Owner.NONE);

    public ObjectProperty<Owner> ownerProperty() {
        if (model.turnProperty().getValue() == Owner.FIRST) {
            ownerProperty.setValue(Owner.FIRST);
        } else {
            ownerProperty.setValue(Owner.SECOND);
        }
        return ownerProperty;
    }

    public TicTacToeSquare(int row, int column) {

        this.setEditable(false);
        this.setPrefWidth(99.9);
        this.setFont(Font.font("System", 50));

        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(13,0,13,0));
        this.setOnMouseClicked(mouseEvent -> {
            if (model.legalMove(row, column).getValue()) {
                if (ownerProperty().getValue() == Owner.FIRST) {
                    this.setText("✕");
                } else {
                    this.setText("○");
                }
                model.play(row, column);
            }
        });
        this.setOnMouseEntered(mouseEvent ->{
            if (model.legalMove(row, column).getValue()){
                this.setCursor(Cursor.HAND);
                this.setStyle("-fx-background-color: GREEN");
            }
            else{
                this.setCursor(Cursor.HAND);
                this.setStyle("-fx-background-color: RED");
            }
        });
        this.setOnMouseExited(mouseEvent -> {
            this.setStyle("-fx-background-color: WHITE");
        });

        model.getWinningSquare(row, column).addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                enlargeSquare();
            }
        });
    }

    private void enlargeSquare() {
        this.setFont(Font.font("System", 80));
        this.setPadding(new Insets(-10, 0, -10, 0));
    }
}
