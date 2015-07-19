/*
 * Copyright © 2014 <code@io7m.com> http://io7m.com
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

package com.io7m.jstructural.core;

import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jranges.RangeCheck;
import com.io7m.jranges.Ranges;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * The type of non-empty lists.
 *
 * @param <T> The type of elements
 */

public final class SNonEmptyList<T>
{
  private final List<T> elements;

  private SNonEmptyList(
    final List<T> in_elements)
  {
    this.elements = NullCheck.notNull(in_elements, "Elements");

    RangeCheck.checkIncludedInInteger(
      in_elements.size(),
      "List size",
      Ranges.POSITIVE_INTEGER,
      "Valid list size range");
  }

  /**
   * Construct a new non-empty list from the given list
   *
   * @param elements The list of elements
   * @param <T>      The type of elements
   *
   * @return A non-empty list
   */

  public static <T> SNonEmptyList<T> newList(
    final List<T> elements)
  {
    return new SNonEmptyList<T>(elements);
  }

  /**
   * Construct a new non-empty list from the given element
   *
   * @param e   A single element
   * @param <T> The type of elements
   *
   * @return A non-empty list
   */

  public static <T> SNonEmptyList<T> one(
    final T e)
  {
    final List<T> es = new LinkedList<T>();
    es.add(e);
    return new SNonEmptyList<T>(es);
  }

  @Override public boolean equals(
    final @Nullable Object obj)
  {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final SNonEmptyList<?> other = (SNonEmptyList<?>) obj;
    return this.elements.equals(other.elements);
  }

  /**
   * @return A read-only view of the list.
   */

  public List<T> getElements()
  {
    final List<T> r = Collections.unmodifiableList(this.elements);
    assert r != null;
    return r;
  }

  @Override public int hashCode()
  {
    return this.elements.hashCode();
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[SNonEmptyList ");
    builder.append(this.elements);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
