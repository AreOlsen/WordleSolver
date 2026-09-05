package areolsen.grid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BasicGrid<E> implements Grid<E> {
    private final int cols;
    private final int rows;
    protected List<List<E>> grid;

    public BasicGrid(int rows, int cols) {
        this(rows, cols, null);
    }

    public BasicGrid(int rows, int cols, E defaultValue) {
        this.rows = rows;
        this.cols = cols;
        grid = new ArrayList<>();
        for (int row = 0; row < rows; row++) {
            List<E> rowList = new ArrayList<>();
            for (int col = 0; col < cols; col++) {
                rowList.add(defaultValue);
            }
            grid.add(rowList);
        }
    }


    @Override
    public int rows() {
        return rows;
    }

    @Override
    public int cols() {
        return cols;
    }

    @Override
    public Iterator<Cell<E>> iterator() {
        List<Cell<E>> list = new ArrayList<>();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Position pos = new Position(col, row);
                list.add(new Cell<E>(pos, get(pos)));
            }
        }
        return list.iterator();
    }

    @Override
    public void set(Position pos, E value) {
        this.grid.get(pos.y()).set(pos.x(), value);
    }

    @Override
    public E get(Position pos) {
        return this.grid.get(pos.y()).get(pos.x());
    }

    @Override
    public boolean positionIsOnGrid(Position pos) {
        if (pos.y() < 0 || pos.y() >= rows)
            return false;
        if (pos.x() < 0 || pos.x() >= cols)
            return false;
        return true;
    }

    public void printGrid() {
        for (List<E> list : grid) {
            System.out.println(list);
        }
    }
}
