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

package com.io7m.jstructural.ast;

import io.vavr.collection.Vector;

import java.math.BigInteger;
import java.util.Objects;

/**
 * Functions to parse content numbers.
 */

public final class SContentNumbers
{
  private SContentNumbers()
  {

  }

  /**
   * Parse a content number from the given text.
   *
   * @param text Text of the form {@code [0-9]+(\.[0-9]+)+}
   *
   * @return A parsed content number
   *
   * @throws NumberFormatException If the number cannot be parsed
   */

  public static SContentNumber parse(
    final String text)
    throws NumberFormatException
  {
    Objects.requireNonNull(text, "text");
    return SContentNumber.of(Vector.of(text.split("\\.")).map(BigInteger::new));
  }
}
