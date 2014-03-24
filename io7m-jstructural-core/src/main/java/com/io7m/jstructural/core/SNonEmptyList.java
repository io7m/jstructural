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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;

/**
 * The type of non-empty lists.
 * 
 * @param <T>
 *          The type of elements
 */

@Immutable public final class SNonEmptyList<T>
{
  /**
   * Construct a new non-empty list from the given list
   * 
   * @param elements
   *          The list of elements
   * @return A non-empty list
   * @throws ConstraintError
   *           If the list is empty
   * @param <T>
   *          The type of elements
   */

  public static @Nonnull <T> SNonEmptyList<T> newList(
    final @Nonnull List<T> elements)
    throws ConstraintError
  {
    return new SNonEmptyList<T>(elements);
  }

  /**
   * Construct a new non-empty list from the given element
   * 
   * @param e
   *          A single element
   * @return A non-empty list
   * @throws ConstraintError
   *           If the list is empty
   * @param <T>
   *          The type of elements
   */

  public static @Nonnull <T> SNonEmptyList<T> one(
    final @Nonnull T e)
    throws ConstraintError
  {
    final List<T> es = new LinkedList<T>();
    es.add(e);
    return new SNonEmptyList<T>(es);
  }

  private final @Nonnull List<T> elements;

  private SNonEmptyList(
    final @Nonnull List<T> in_elements)
    throws ConstraintError
  {
    this.elements = Constraints.constrainNotNull(in_elements, "Elements");
    Constraints.constrainRange(
      in_elements.size(),
      1,
      Integer.MAX_VALUE,
      "List size");
  }

  @Override public boolean equals(
    final Object obj)
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

  public @Nonnull List<T> getElements()
  {
    return Collections.unmodifiableList(this.elements);
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
    return builder.toString();
  }
}
