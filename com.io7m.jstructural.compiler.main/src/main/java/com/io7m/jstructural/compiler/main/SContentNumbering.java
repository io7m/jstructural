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

package com.io7m.jstructural.compiler.main;

import com.io7m.jstructural.ast.SContentNumber;
import io.vavr.collection.Vector;

import java.math.BigInteger;
import java.util.ArrayList;

/**
 * A producer of content numbers.
 */

public final class SContentNumbering
{
  private final ArrayList<BigInteger> stack;

  private SContentNumbering()
  {
    this.stack = new ArrayList<>(16);
    this.stack.add(BigInteger.ZERO);
  }

  /**
   * @return A new content numbering context
   */

  public static SContentNumbering create()
  {
    return new SContentNumbering();
  }

  /**
   * @return The current content number
   */

  public SContentNumber current()
  {
    return SContentNumber.of(Vector.ofAll(this.stack));
  }

  /**
   * Increment the current component of the number
   */

  public void increment()
  {
    final int last = this.stack.size() - 1;
    this.stack.set(last, this.stack.get(last).add(BigInteger.ONE));
  }

  /**
   * @return The number of components on the stack
   */

  public int components()
  {
    return this.stack.size();
  }

  /**
   * Push a new zero component to the stack
   */

  public void push()
  {
    this.stack.add(BigInteger.ZERO);
  }

  /**
   * Pop a component from the stack
   *
   * @throws IllegalStateException If the stack only contains one element
   */

  public void pop()
    throws IllegalStateException
  {
    if (this.stack.size() == 1) {
      throw new IllegalStateException("Cannot remove the last element of a numbering stack");
    }

    this.stack.remove(this.stack.size() - 1);
  }
}
