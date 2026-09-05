package areolsen.grid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;

import org.junit.jupiter.api.Test;

/**
* Testing the class CellPosition
*/
public class PositionTest {

  @Test
  void sanityTest() {
    Position cp = new Position(4, 3);
    assertEquals(4, cp.x());
    assertEquals(3, cp.y());
  }

  @Test
  void coordinateEqualityTest() {
    Position a = new Position(2, 3);
    Position b = new Position(2, 3);

    assertFalse(a == b);
    assertTrue(a.equals(b));
    assertTrue(b.equals(a));
    assertTrue(Objects.equals(a, b));
  }

  @Test
  void coordinateInequalityTest() {
    Position a = new Position(2, 3);
    Position b = new Position(3, 2);

    assertFalse(a == b);
    assertFalse(a.equals(b));
    assertFalse(b.equals(a));
    assertFalse(Objects.equals(a, b));
  }

  @Test
  void coordinateHashcodeTest() {
    Position a = new Position(2, 3);
    Position b = new Position(2, 3);
    assertTrue(a.hashCode() == b.hashCode());

    Position c = new Position(100, 100);
    Position d = new Position(100, 100);
    assertTrue(c.hashCode() == d.hashCode());
  }
}
