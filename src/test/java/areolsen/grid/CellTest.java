package areolsen.grid;

import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
* Testing the class GridCell
*/
public class CellTest {

  @Test
  void sanityTest() {
    String item = "Test";
    Position pos = new Position(4, 2);
    Cell<String> gridCell = new Cell<>(pos, item);

    assertEquals(pos, gridCell.position());
    assertEquals(item, gridCell.value());
  }

  @Test
  void gridCellEqualityAndHashCodeTest() {
    String item = "Test";
    Position pos = new Position(4, 2);
    Cell<String> gridCell = new Cell<>(pos, item);

    String item2 = "Test";
    Position pos2 = new Position(4, 2);
    Cell<String> gridCell2 = new Cell<>(pos2, item2);

    assertTrue(gridCell2.equals(gridCell));
    assertTrue(gridCell.equals(gridCell2));
    assertTrue(Objects.equals(gridCell, gridCell2));
    assertTrue(gridCell.hashCode() == gridCell2.hashCode());
  }

  @Test
  void gridCellInequalityTest() {
    String item = "Test";
    Position pos = new Position(4, 2);
    Cell<String> gridCell = new Cell<>(pos, item);

    String item2 = "Test2";
    Position pos2 = new Position(2, 4);

    Cell<String> gridCell2 = new Cell<>(pos2, item);
    Cell<String> gridCell3 = new Cell<>(pos, item2);

    assertFalse(gridCell2.equals(gridCell));
    assertFalse(gridCell.equals(gridCell2));
    assertFalse(gridCell.equals(gridCell3));
    assertFalse(gridCell2.equals(gridCell3));
    assertFalse(Objects.equals(gridCell, gridCell2));
    assertFalse(Objects.equals(gridCell, gridCell3));
    assertFalse(Objects.equals(gridCell2, gridCell3));
  }
}
