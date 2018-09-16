/*
 * Copyright © 2018 Mark Raynsford <code@io7m.com> http://io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jstructural.tests.compiler.main;

import com.io7m.jstructural.compiler.main.SContentNumbering;
import io.vavr.collection.Vector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.TWO;
import static java.math.BigInteger.ZERO;

public final class SContentNumberingTest
{
  private static final Logger LOG = LoggerFactory.getLogger(SContentNumberingTest.class);
  private static final BigInteger THREE = BigInteger.valueOf(3L);

  @Test
  public void testEmpty()
  {
    final SContentNumbering c = SContentNumbering.create();

    Assertions.assertEquals(Vector.of(ZERO), c.current().components());
    Assertions.assertEquals(1, c.components());
    LOG.debug("{}", c.current().toHumanString());

    Assertions.assertEquals(Vector.of(ZERO), c.current().components());
    Assertions.assertEquals(1, c.components());
    LOG.debug("{}", c.current().toHumanString());
  }

  @Test
  public void testIncrement()
  {
    final SContentNumbering c = SContentNumbering.create();
    Assertions.assertEquals(Vector.of(ZERO), c.current().components());
    Assertions.assertEquals(1, c.components());
    LOG.debug("{}", c.current().toHumanString());

    c.increment();
    Assertions.assertEquals(Vector.of(ZERO.add(ONE)), c.current().components());
    Assertions.assertEquals(1, c.components());
    LOG.debug("{}", c.current().toHumanString());

    c.increment();
    Assertions.assertEquals(Vector.of(ZERO.add(ONE).add(ONE)), c.current().components());
    Assertions.assertEquals(1, c.components());
    LOG.debug("{}", c.current().toHumanString());
  }

  @Test
  public void testPushIncrementPop()
  {
    final SContentNumbering c = SContentNumbering.create();
    Assertions.assertEquals(Vector.of(ZERO), c.current().components());
    Assertions.assertEquals(1, c.components());
    LOG.debug("{}", c.current().toHumanString());

    c.push();
    Assertions.assertEquals(Vector.of(ZERO, ZERO), c.current().components());
    Assertions.assertEquals(2, c.components());
    LOG.debug("{}", c.current().toHumanString());

    c.increment();
    Assertions.assertEquals(Vector.of(ZERO, ONE), c.current().components());
    Assertions.assertEquals(2, c.components());
    LOG.debug("{}", c.current().toHumanString());

    c.increment();
    Assertions.assertEquals(Vector.of(ZERO, TWO), c.current().components());
    Assertions.assertEquals(2, c.components());
    LOG.debug("{}", c.current().toHumanString());

    c.push();
    Assertions.assertEquals(Vector.of(ZERO, TWO, ZERO), c.current().components());
    Assertions.assertEquals(3, c.components());
    LOG.debug("{}", c.current().toHumanString());

    c.increment();
    Assertions.assertEquals(Vector.of(ZERO, TWO, ONE), c.current().components());
    Assertions.assertEquals(3, c.components());
    LOG.debug("{}", c.current().toHumanString());

    c.increment();
    Assertions.assertEquals(Vector.of(ZERO, TWO, TWO), c.current().components());
    Assertions.assertEquals(3, c.components());
    LOG.debug("{}", c.current().toHumanString());

    c.pop();
    Assertions.assertEquals(Vector.of(ZERO, TWO), c.current().components());
    Assertions.assertEquals(2, c.components());
    LOG.debug("{}", c.current().toHumanString());

    c.pop();
    Assertions.assertEquals(Vector.of(ZERO), c.current().components());
    Assertions.assertEquals(1, c.components());
    LOG.debug("{}", c.current().toHumanString());
  }

  @Test
  public void testPopEmpty()
  {
    final SContentNumbering c = SContentNumbering.create();
    Assertions.assertThrows(IllegalStateException.class, c::pop);
  }
}
