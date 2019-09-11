package cube;

import java.util.Observable;


/** Models an instance of the Cube puzzle: a cube with color on some sides
 *  sitting on a cell of a square grid, some of whose cells are colored.
 *  Any object may register to observe this model, using the (inherited)
 *  addObserver method.  The model notifies observers whenever it is modified.
 *  @author P. N. Hilfinger
 */
public class CubeModel extends Observable {

    private int side;
    private int cubeRow;
    private int cubeCol;
    private boolean [][] fieldPainted;
    private boolean[] facesPainted;
    private int moves;


    /** A blank cube puzzle of size 4. */
    public CubeModel() {
        boolean [][] blank = new boolean[6][6];
        initialize(4, 0, 0, blank);

    }

    /** A copy of CUBE. */
    public CubeModel(CubeModel cube) {
        initialize(cube);
    }

    /** Initialize puzzle of size SIDExSIDE with the cube initially at
     *  ROW0 and COL0, with square r, c painted iff PAINTED[r][c], and
     *  with face k painted iff FACEPAINTED[k] (see isPaintedFace).
     *  Assumes that
     *    * SIDE > 2.
     *    * PAINTED is SIDExSIDE.
     *    * 0 <= ROW0, COL0 < SIDE.
     *    * FACEPAINTED has length 6.
     */
    public void initialize(int sideinit, int row0, int col0, boolean[][] painted,
                    boolean[] facePainted) {
        this.side = sideinit;
        this.cubeRow = row0;
        this.cubeCol = col0;
        this.fieldPainted = painted;
        this.facesPainted = facePainted;
        this.moves = 0;
        setChanged();
        notifyObservers();
    }

    /** Initialize puzzle of size SIDExSIDE with the cube initially at
     *  ROW0 and COL0, with square r, c painted iff PAINTED[r][c].
     *  The cube is initially blank.
     *  Assumes that
     *    * SIDE > 2.
     *    * PAINTED is SIDExSIDE.
     *    * 0 <= ROW0, COL0 < SIDE.
     */
    public void initialize(int sideinit, int row0, int col0, boolean[][] painted) {
        initialize(sideinit, row0, col0, painted, new boolean[6]);
    }

    /** Initialize puzzle to be a copy of CUBE. */
    public void initialize(CubeModel cube) {
        this.side = cube.side();
        this.cubeRow = cube.cubeRow();
        this.cubeCol = cube.cubeCol();
        boolean[][] fP = new boolean[cube.side()][cube.side()];
        java.lang.System.arraycopy(cube.getFieldPainted(), 0, fP, 0, cube.side());
        this.fieldPainted = fP;
        boolean[]faP = new boolean[6];
        java.lang.System.arraycopy(cube.getFacesPainted(), 0, faP, 0, 6);
        this.facesPainted = faP;
        this.moves = cube.moves();
        setChanged();
        notifyObservers();
    }

    /** Move the cube to (ROW, COL), if that position is on the board and
     *  vertically or horizontally adjacent to the current cube position.
     *  Transfers colors as specified by the rules.
     *  Throws IllegalArgumentException if preconditions are not met.
     */
    public void move(int row, int col) {
        if ((row >= side()) || (col >= side()) || (row < 0) || (col < 0)) {
            throw new IllegalArgumentException("Move row or column out of bounds");
        }
        if (!isAdjMove(row, col)) {
            throw new IllegalArgumentException("Move row or column not adjacent");
        } else { // legal move
            String direction = moveDirection(row, col);
            this.cubeRow = row;
            this.cubeCol = col;
            if (direction.equals("D")) {
                moveDown();
                moves += 1;
                if (isPaintedFace(4)) {
                    swapPaintedFace(row, col);
                } else if (fieldPainted[row][col]) {
                    swapPaintedFace(row, col);
                }
            } else if (direction.equals("U")) {
                moveUp();
                moves += 1;
                if (isPaintedFace(4)) {
                    swapPaintedFace(row, col);
                } else if (fieldPainted[row][col]) {
                    swapPaintedFace(row, col);
                }
            } else if (direction.equals("L")) {
                moveLeft();
                moves += 1;
                if (isPaintedFace(4)) {
                    swapPaintedFace(row, col);
                } else if (fieldPainted[row][col]) {
                    swapPaintedFace(row, col);
                }
            } else {
                moveRight();
                moves += 1;
                if (isPaintedFace(4)) {
                    swapPaintedFace(row, col);
                } else if (fieldPainted[row][col]) {
                    swapPaintedFace(row, col);
                }
            }
        }

        setChanged();
        notifyObservers();
    }

    /**Check which direction the cube will be moving.
     * Assumes valid move
     * @param row
     * @param col
     * @return string
     */
    public String moveDirection(int row, int col) {
        if (this.cubeRow() - row > 0) { // move down
            return "D";
        } else if (this.cubeRow() - row < 0) { // move up
            return "U";
        } else if (this.cubeCol() - col > 0) { // move left
            return "L";
        } else {
            return "R";
        }
    }

    /**
     * Sets the facesPainted array to reflect the moved cube.
     * Move Right
     */
    public void moveRight() {
        boolean[] update = new boolean[]{facesPainted[0], facesPainted[1],
                facesPainted[4], facesPainted[5], facesPainted[3], facesPainted[2]};
        facesPainted = update;
    }

    /**
     * Sets the facesPainted array to reflect the moved cube.
     * Move Left
     */
    public void moveLeft() {
        boolean[] update = new boolean[]{facesPainted[0], facesPainted[1],
                facesPainted[5], facesPainted[4], facesPainted[2], facesPainted[3]};
        facesPainted = update;
    }

    /**
     * Sets the facesPainted array to reflect the moved cube.
     * Move Up
     */
    public void moveUp() {
        boolean[] update = new boolean[]{facesPainted[4], facesPainted[5],
                facesPainted[2], facesPainted[3], facesPainted[1], facesPainted[0]};
        facesPainted = update;
    }

    /**
     * Sets the facesPainted array to reflect the moved cube.
     * Move Down
     */
    public void moveDown() {
        boolean[] update = new boolean[]{facesPainted[5], facesPainted[4],
                facesPainted[2], facesPainted[3], facesPainted[0], facesPainted[1]};
        facesPainted = update;
    }

    /**
     * checks the input column and row to ensure the move is a valid one.
     * @param row
     * @param col
     * @return
     */
    public boolean isAdjMove(int row, int col) {
        if (row == this.cubeRow()) {
            return (Math.abs(col - this.cubeCol()) == 1);
        } else if (col == this.cubeCol()) {
            return (Math.abs(row - this.cubeRow()) == 1);
        }
        return false;
    }

    /** Return the number of squares on a side. */
    public int side() {
        return this.side;
    }

    /** Return true iff square ROW, COL is painted.
     *  Requires 0 <= ROW, COL < board size. */
    public boolean isPaintedSquare(int row, int col) {


        return this.fieldPainted[row][col];
    }

    /** Return current row of cube. */
    public int cubeRow() {

        return this.cubeRow;
    }

    /** Return current column of cube. */
    public int cubeCol() {
        return this.cubeCol;
    }

    /** Return the number of moves made on current puzzle. */
    public int moves() {
        return this.moves;
    }

    /** Return true iff face #FACE, 0 <= FACE < 6, of the cube is painted.
     *  Faces are numbered as follows:
     *    0: Vertical in the direction of row 0 (nearest row to player).
     *    1: Vertical in the direction of last row.
     *    2: Vertical in the direction of column 0 (left column).
     *    3: Vertical in the direction of last column.
     *    4: Bottom face.
     *    5: Top face.
     */
    public boolean isPaintedFace(int face) {
        return facesPainted[face];
    }

    /** Return true iff all faces are painted. */
    public boolean allFacesPainted() {

        for (int i = 0; i < facesPainted.length; i++) {
            if (!facesPainted[i]) {
                return false;
            }
        }
        return true;
    }
    /** Return fieldPainted array */
    public boolean[][] getFieldPainted() {
        return this.fieldPainted;
    }
    /** Return facesPainted array */
    public boolean[] getFacesPainted() {
        return facesPainted;
    }

    /**
     * Swaps the painted face on the cube with the color on the board at row,col.
     * assumes cube bottom face is painted.
     * @param row
     * @param col
     */
    public void swapPaintedFace(int row, int col) {
        boolean temp = fieldPainted[row][col];
        fieldPainted[row][col] = facesPainted[4];
        facesPainted[4] = temp;
    }
    // ADDITIONAL FIELDS AND PRIVATE METHODS HERE, AS NEEDED.

}
