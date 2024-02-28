package aka.salako;

import javafx.beans.binding.*;
import javafx.beans.property.*;

public class TicTacToeModel {

    /**
    * Taille du plateau de jeu (pour être extensible).
    */
    public final static int BOARD_WIDTH = 4;
    public final static int BOARD_HEIGHT = 4;

    /**
    * Nombre de pièces alignés pour gagner (idem).
    */
    public final static int WINNING_COUNT = 3;

    /**
    * Joueur courant.
    */
    private final ObjectProperty<Owner> turn = new SimpleObjectProperty<>(Owner.FIRST);

    /**
    * Vainqueur du jeu, NONE si pas de vainqueur.
    */
    private final ObjectProperty<Owner> winner = new SimpleObjectProperty<>(Owner.NONE);

    /**
    * Plateau de jeu.
    */
    private final ObjectProperty<Owner>[][] board;

    /**
    * Positions gagnantes.
    */
    final BooleanProperty[][] winningBoard;


    private final IntegerProperty xCount = new SimpleIntegerProperty(0);
    private final IntegerProperty oCount = new SimpleIntegerProperty(0);
    private final IntegerProperty emptyCount = new SimpleIntegerProperty(BOARD_HEIGHT * BOARD_WIDTH);

    public IntegerProperty getXCount() {
        return xCount;
    }

    public IntegerProperty getOCount() {
        return oCount;
    }

    public IntegerProperty getEmptyCount() {
        return emptyCount;
    }

    /**
    * Constructeur privé.
    */
    private TicTacToeModel() {

        board = new ObjectProperty [BOARD_HEIGHT][BOARD_WIDTH];
        winningBoard = new BooleanProperty[BOARD_HEIGHT][BOARD_WIDTH];

        for (int i = 0; i<BOARD_HEIGHT; i++){
            for(int j = 0; j<BOARD_WIDTH; j++){
                board[i][j] = new SimpleObjectProperty<>(Owner.NONE);
                winningBoard[i][j] = new SimpleBooleanProperty(false);
            }
        }

    }

    /**
    * @return la seule instance possible du jeu.
    */
    public static TicTacToeModel getInstance() {
        return MorpionModelHolder.INSTANCE;
    }

    /**
    * Classe interne selon le pattern singleton.
    */
    private static class MorpionModelHolder {
        private static final TicTacToeModel INSTANCE = new TicTacToeModel();
    }

    public void restart() {

        turn.setValue(Owner.FIRST);
        winner.setValue(Owner.NONE);

        for (int i = 0; i<BOARD_HEIGHT; i++){
            for(int j = 0; j<BOARD_WIDTH; j++){
                board[i][j] = new SimpleObjectProperty<>(Owner.NONE);
                winningBoard[i][j] = new SimpleBooleanProperty(false);
            }
        }

    }

    public final ObjectProperty<Owner> turnProperty() {
        return turn;
    }

    public final ObjectProperty<Owner> getSquare(int row, int column) {

        ObjectProperty<Owner> Square = null;
        if (validSquare(row, column)){
            Square = board[row][column];
        }
        return Square;

    }

    public final BooleanProperty getWinningSquare(int row, int column) {

        BooleanProperty winningSquare = null;
        if (validSquare(row, column)){
            winningSquare = winningBoard[row][column];
        }
        return winningSquare;

    }

    /**
    * Cette fonction ne doit donner le bon résultat que si le jeu
    * est terminé. L’affichage peut être caché avant la fin du jeu.
    *
    * @return résultat du jeu sous forme de texte
    */
    public final StringExpression getEndOfGameMessage() {
        SimpleStringProperty msg=new SimpleStringProperty("");

        if (winner.getValue().equals(Owner.FIRST)){
            msg.set("Game Over. Le gagnant est le premier joueur.");
        }
        else if (winner.getValue().equals(Owner.SECOND)){
            msg.set("Game Over. Le gagnant est le deuxième joueur.");
        }
        else{
           msg.set("Match Nul");
        }


        return msg;

    }

    public void setWinner(Owner winner) {
        this.winner.setValue(winner);
    }

    public boolean validSquare(int row, int column) {

        if (row>=0 && column>=0 && row<BOARD_HEIGHT && column<BOARD_WIDTH){
            return true;
        }
        else{
            return false;
        }

    }

    public void nextPlayer() {
        turn.setValue(turn.getValue().opposite());
    }



    /**
    * Jouer dans la case (row, column) quand c’est possible.
    */
    public void play(int row, int column) {

        if(legalMove(row, column).getValue()){
            board[row][column].setValue(turn.getValue());

            // Mise à jour des compteurs
            if (turn.getValue() == Owner.FIRST) {
                xCount.set(xCount.get() + 1);
            } else {
                oCount.set(oCount.get() + 1);
            }
            emptyCount.set(emptyCount.get() - 1);

            checkWinConditions(row, column);

            if (!gameOver().getValue()) {
                nextPlayer();
            }
        }

    }

    /**
    * @return true s’il est possible de jouer dans la case
    * c’est-à-dire la case est libre et le jeu n’est pas terminé
    */
    public BooleanBinding legalMove(int row, int column) {
        return Bindings.createBooleanBinding(
                ()->{
                    return board[row][column].getValue().equals(Owner.NONE) && !(gameOver().getValue());
                });

    }
    public NumberExpression getScore(Owner owner) {
        IntegerBinding score = Bindings.createIntegerBinding(() -> {
            int count = 0;

            // Parcourez le plateau de jeu pour compter le nombre de cases possédées par le joueur spécifié.
            for (int i = 0; i < BOARD_HEIGHT; i++) {
                for (int j = 0; j < BOARD_WIDTH; j++) {
                    if (board[i][j].get() == owner) {
                        count++;
                    }
                }
            }

            return count;
        }, board[0][0], board[0][1], /* ... (add all individual board elements as dependencies) */ board[BOARD_HEIGHT-1][BOARD_WIDTH-1]);

        return score;
    }



    /**
    * @return true si le jeu est terminé
    * (soit un joueur a gagné, soit il n’y a plus de cases à jouer)
    */
    public BooleanBinding gameOver() {

        return Bindings.createBooleanBinding(
                ()->{
                    boolean win = false;
                    boolean notempty_case = false;
                    if (!winner.getValue().equals(Owner.NONE)){
                        win = true;
                    }
                    int c = 0;
                    for (int i = 0; i < BOARD_HEIGHT; i++) {
                        for (int j = 0; j < BOARD_WIDTH; j++) {
                            if (!(board[i][j].getValue().equals(Owner.NONE))){
                                c++;
                            }
                            if (c == BOARD_WIDTH*BOARD_HEIGHT){
                                notempty_case = true;
                                break;
                            }
                        }
                    }
                    return win||notempty_case;
                });
    }


    private void checkWinConditions(int row, int column) {
        //check col
        for(int i = 0; i < BOARD_WIDTH; i++){
            if(board[row][i].getValue() != turn.getValue())
                break;
            if(i == BOARD_WIDTH-1){
                setWinner(turn.getValue());
                getEndOfGameMessage();
            }
        }

        //check row
        for(int i = 0; i < BOARD_HEIGHT; i++){
            if(board[i][column].getValue() != turn.getValue())
                break;
            if(i == BOARD_HEIGHT-1){
                setWinner(turn.getValue());
                getEndOfGameMessage();
            }
        }

        //check diag
        if(row == column){
            //we're on a diagonal
            for(int i = 0; i < (BOARD_HEIGHT+BOARD_WIDTH)/2; i++){
                if(board[i][i].getValue() != turn.getValue())
                    break;
                if(i == ((BOARD_HEIGHT+BOARD_WIDTH)/2)-1){
                    setWinner(turn.getValue());
                    getEndOfGameMessage();
                }
            }
        }

        //check anti diag
        if(row + column == ((BOARD_HEIGHT+BOARD_WIDTH)/2) - 1){
            for(int i = 0; i < ((BOARD_HEIGHT+BOARD_WIDTH)/2); i++){
                if(board[i][(((BOARD_HEIGHT+BOARD_WIDTH)/2)-1)-i].getValue() != turn.getValue())
                    break;
                if(i == ((BOARD_HEIGHT+BOARD_WIDTH)/2)-1){
                    setWinner(turn.getValue());
                    getEndOfGameMessage();
                }
            }
        }

    }
}
