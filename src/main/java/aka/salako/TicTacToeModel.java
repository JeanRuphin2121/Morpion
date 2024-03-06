package aka.salako;

import javafx.beans.Observable;
import javafx.beans.binding.*;
import javafx.beans.property.*;


public class TicTacToeModel {

    /**
    * Taille du plateau de jeu (pour être extensible).
    */
    public final static int BOARD_WIDTH = 3;
    public final static int BOARD_HEIGHT = 3;

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
    private final ObjectProperty<Owner>  winner = new SimpleObjectProperty<>(Owner.NONE);

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
        msg.set("");
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                board[i][j].setValue(Owner.NONE);
                winningBoard[i][j].set(false);
            }
        }

        xCount.bind(getScore(Owner.FIRST));
        oCount.bind(getScore(Owner.SECOND));
        emptyCount.set(BOARD_HEIGHT * BOARD_WIDTH);
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

        if (validSquare(row, column)) {
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
    SimpleStringProperty msg=new SimpleStringProperty("");

    public final StringExpression getEndOfGameMessage() {
        Owner value = winner.getValue();
        if (value == Owner.FIRST) {
            msg.set("Game Over. Le gagnant est le premier joueur.");
        } else if (value == Owner.SECOND) {
            msg.set("Game Over. Le gagnant est le deuxième joueur.");
        } else if (value == Owner.NONE && emptyCount.get() == 0) {
            msg.set("Match nul. Aucun joueur n'a gagné.");
        }

        return msg;
    }


    public void setWinner(Owner winner) {
        this.winner.setValue(winner);
    }

    public boolean validSquare(int row, int column) {

        return row >= 0 && column >= 0 && row < BOARD_HEIGHT && column < BOARD_WIDTH;

    }

    public void nextPlayer() {
        turn.setValue(turn.getValue().opposite());
    }



    /**
    * Jouer dans la case (row, column) quand c’est possible.
    */
    public void play(int row, int column) {
        if (legalMove(row, column).getValue()) {
            board[row][column].setValue(turn.getValue());

            xCount.bind(getScore(Owner.FIRST));
            oCount.bind(getScore(Owner.SECOND));
            emptyCount.set(emptyCount.get() - 1);

            System.out.println("Empty Count: " + emptyCount.get());

            checkWinConditions(row, column);

            if (!gameOver().getValue()) {
                nextPlayer();
            }

            // Appeler getEndOfGameMessage() pour mettre à jour le message
            getEndOfGameMessage();
        }
    }



    /**
    * @return true s’il est possible de jouer dans la case
    * c’est-à-dire la case est libre et le jeu n’est pas terminé
    */
    public BooleanBinding legalMove(int row, int column) {
        return Bindings.createBooleanBinding(()-> board[row][column].getValue().equals(Owner.NONE) && !(gameOver().getValue()));
    }
    public NumberExpression getScore(Owner owner) {
        Observable[] dependencies = new Observable[BOARD_HEIGHT * BOARD_WIDTH];

        int index = 0;

        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                dependencies[index++] = board[i][j];
            }
        }

        IntegerBinding score;
        score = Bindings.createIntegerBinding(() -> {
            int count = 0;

            for (int i = 0; i < BOARD_HEIGHT; i++) {
                for (int j = 0; j < BOARD_WIDTH; j++) {
                    if (board[i][j].get() == owner) {
                        count++;
                    }
                }
            }

            return count;
        }, dependencies);

        return score;
    }




    /**
    * @return true si le jeu est terminé
    * (soit un joueur a gagné, soit il n’y a plus de cases à jouer)
    */
    public BooleanBinding gameOver() {
        return Bindings.createBooleanBinding(() -> {
            boolean win = !winner.getValue().equals(Owner.NONE);

            if (win) {
                return true;
            }

            int c = 0;
            for (int i = 0; i < BOARD_HEIGHT; i++) {
                for (int j = 0; j < BOARD_WIDTH; j++) {
                    if (!board[i][j].getValue().equals(Owner.NONE)) {
                        c++;
                    }
                    if (c == BOARD_WIDTH * BOARD_HEIGHT) {
                        return true;
                    }
                }
            }

            return false;
        }, winner);
    }



    private void checkWinConditions(int row, int column) {
        // check row
        int countRow = 0;
        int winningRow = -1;
        for (int i = 0; i < BOARD_WIDTH; i++) {
            if (board[row][i].getValue() == turn.getValue()) {
                countRow++;
                if (countRow == WINNING_COUNT) {
                    winningRow = row;
                    break;
                }
            } else {
                countRow = 0;
            }
        }

        if (winningRow != -1) {
            markWinningPositions(winningRow, 0, 0, 1);
            setWinner(turn.getValue());
            System.out.println("Row win!");
            System.out.println(winner);
            getEndOfGameMessage();
            return;
        }

        // check col
        int countCol = 0;
        int winningCol = -1;
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            if (board[i][column].getValue() == turn.getValue()) {
                countCol++;
                if (countCol == WINNING_COUNT) {
                    winningCol = column;
                    break;
                }
            } else {
                countCol = 0;
            }
        }

        if (winningCol != -1) {
            markWinningPositions(0, winningCol, 1, 0);
            setWinner(turn.getValue());
            System.out.println("Column win!");
            System.out.println(winner);
            getEndOfGameMessage();
            return;
        }

        // check diag
        if (row == column) {
            int countDiag = 0;
            int winningDiagRow = -1;
            for (int i = 0; i < BOARD_HEIGHT; i++) {
                if (board[i][i].getValue() == turn.getValue()) {
                    countDiag++;
                    if (countDiag == WINNING_COUNT) {
                        winningDiagRow = i;
                        break;
                    }
                } else {
                    countDiag = 0;
                }
            }

            if (winningDiagRow != -1) {
                markWinningPositions(0, 0, 1, 1);
                setWinner(turn.getValue());
                System.out.println("Diagonal win!");
                System.out.println(winner);
                getEndOfGameMessage();
                return;
            }
        }


        // check anti diag
        if (row + column == BOARD_WIDTH - 1) {
            int countAntiDiag = 0;
            int winningAntiDiagRow = -1;
            for (int i = 0; i < BOARD_HEIGHT; i++) {
                if (board[i][BOARD_WIDTH - 1 - i].getValue() == turn.getValue()) {
                    countAntiDiag++;
                    if (countAntiDiag == WINNING_COUNT) {
                        winningAntiDiagRow = i;
                        break;
                    }
                } else {
                    countAntiDiag = 0;
                }
            }

            if (winningAntiDiagRow != -1) {
                markWinningPositions(0, BOARD_WIDTH - 1, 1, -1);
                setWinner(turn.getValue());
                System.out.println("Anti-Diagonal win!");
                System.out.println(winner);
                getEndOfGameMessage();
                return;
            }
        }

    }


    private void markWinningPositions(int startRow, int startColumn, int rowIncrement, int colIncrement) {
        for (int i = 0; i < WINNING_COUNT; i++) {
            int rowIndex = startRow + i * rowIncrement;
            int colIndex = startColumn + i * colIncrement;

            // Vérifier que les indices restent dans les limites du tableau
            if (validSquare(rowIndex, colIndex)) {
                winningBoard[rowIndex][colIndex].set(true);
            } else {
                // Gérer le cas où les indices sortent des limites du tableau
                System.out.println("Erreur : Indices hors limites - rowIndex=" + rowIndex + ", colIndex=" + colIndex);
                // Vous pouvez choisir de ne rien faire ou de gérer cette situation d'une autre manière
            }
        }
    }








}
