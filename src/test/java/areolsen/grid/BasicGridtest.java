package areolsen.grid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
* Testing the class Grid
*/
public class BasicGridtest {

  @Test
  void gridTestGetRowsAndCols() {
    Grid<Integer> grid = new BasicGrid<>(3, 2);
    assertEquals(3, grid.rows());
    assertEquals(2, grid.cols());
  }

  @Test
  void gridSanityTest() {
    String defaultValue = "x";
    Grid<String> grid = new BasicGrid<>(3, 2, defaultValue);

    assertEquals(3, grid.rows());
    assertEquals(2, grid.cols());

    assertEquals("x", grid.get(new Position(0, 0)));
    assertEquals("x", grid.get(new Position(2, 1)));

    grid.set(new Position(1, 1), "y");

    assertEquals("y", grid.get(new Position(1, 1)));
    assertEquals("x", grid.get(new Position(0, 1)));
    assertEquals("x", grid.get(new Position(1, 0)));
    assertEquals("x", grid.get(new Position(2, 1)));
  }

  @Test
  void gridCanHoldNull() {
    String defaultValue = "x";
    Grid<String> grid = new BasicGrid<>(3, 2, defaultValue);

    assertEquals("x", grid.get(new Position(0, 0)));
    assertEquals("x", grid.get(new Position(2, 1)));

    grid.set(new Position(1, 1), null);

    assertEquals(null, grid.get(new Position(1, 1)));
    assertEquals("x", grid.get(new Position(0, 1)));
    assertEquals("x", grid.get(new Position(1, 0)));
    assertEquals("x", grid.get(new Position(2, 1)));
  }

  @Test
  void gridNullsInDefaultConstructor() {
    Grid<String> grid = new BasicGrid<>(3, 2);

    assertEquals(null, grid.get(new Position(0, 0)));
    assertEquals(null, grid.get(new Position(2, 1)));

    grid.set(new Position(1, 1), "y");

    assertEquals("y", grid.get(new Position(1, 1)));
    assertEquals(null, grid.get(new Position(0, 1)));
    assertEquals(null, grid.get(new Position(1, 0)));
    assertEquals(null, grid.get(new Position(2, 1)));
  }

  @Test
  void coordinateIsOnGridTest() {
    Grid<Double> grid = new BasicGrid<>(3, 2, 0.9);

    assertTrue(grid.positionIsOnGrid(new Position(2, 1)));
    assertFalse(grid.positionIsOnGrid(new Position(3, 1)));
    assertFalse(grid.positionIsOnGrid(new Position(2, 2)));

    assertTrue(grid.positionIsOnGrid(new Position(0, 0)));
    assertFalse(grid.positionIsOnGrid(new Position(-1, 0)));
    assertFalse(grid.positionIsOnGrid(new Position(0, -1)));
  }

  @Test
  void throwsExceptionWhenCoordinateOffGrid() {
    Grid<String> grid = new BasicGrid<>(3, 2, "x");

    try {
      @SuppressWarnings("unused")
      String x = grid.get(new Position(3, 1));
      fail();
    } catch (IndexOutOfBoundsException e) {
      // Test passed
    }
  }

  @Test
  void testIterator() {
    Grid<String> grid = new BasicGrid<>(3, 2, "x");
    grid.set(new Position(0, 0), "a");
    grid.set(new Position(1, 1), "b");
    grid.set(new Position(2, 1), "c");

    List<Cell<String>> items = new ArrayList<>();
    for (Cell<String> coordinateItem : grid) {
      items.add(coordinateItem);
    }

    assertEquals(3 * 2, items.size());
    assertTrue(items.contains(new Cell<String>(new Position(0, 0), "a")));
    assertTrue(items.contains(new Cell<String>(new Position(1, 1), "b")));
    assertTrue(items.contains(new Cell<String>(new Position(2, 1), "c")));
    assertTrue(items.contains(new Cell<String>(new Position(0, 1), "x")));
  }
}
