package io.mincongh.classmethod;

import io.mincongh.abstraction.Dog;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test different string representations of class {@code java.lang.Class<T>}.
 *
 * @author Mincong Huang
 */
class ClassMethodTest {

  @Test
  void testMethods() {
    assertEquals("class io.mincongh.abstraction.Dog", Dog.class.toString());
    assertEquals("io.mincongh.abstraction.Dog", Dog.class.getName());
    assertEquals("Dog", Dog.class.getSimpleName());
  }
}
