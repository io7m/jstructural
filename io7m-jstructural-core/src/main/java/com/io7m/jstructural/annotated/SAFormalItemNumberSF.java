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
 * SFECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.io7m.jstructural.annotated;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;

/**
 * A formal item number consisting of a section, and formal item.
 */

public final class SAFormalItemNumberSF extends SAFormalItemNumber
{
  private final int formal;
  private final int section;

  /**
   * Construct a new formal item number
   * 
   * @param in_section
   *          The section number
   * @param in_formal
   *          The formal item number
   * @throws ConstraintError
   *           If any parameter is outside of the range
   *           <code>[1, {@link Integer#MAX_VALUE}]</code>
   */

  public SAFormalItemNumberSF(
    final int in_section,
    final int in_formal)
    throws ConstraintError
  {
    this.section =
      Constraints.constrainRange(in_section, 1, Integer.MAX_VALUE, "Section");
    this.formal =
      Constraints.constrainRange(
        in_formal,
        1,
        Integer.MAX_VALUE,
        "Formal item");
  }

  @SuppressWarnings({ "boxing", "synthetic-access" }) @Override public
    int
    compareTo(
      final SAFormalItemNumber o)
  {
    try {
      return o.formalItemNumberAccept(
        new SAFormalItemNumberVisitor<Integer>() {
          @Override public Integer visitFormalItemNumberPSF(
            final @Nonnull SAFormalItemNumberPSF p)
            throws ConstraintError,
              Exception
          {
            final int rsect =
              Integer.compare(
                SAFormalItemNumberSF.this.section,
                p.getSection());
            if (rsect == 0) {
              return Integer.compare(
                SAFormalItemNumberSF.this.formal,
                p.getFormalItem());
            }
            return rsect;
          }

          @Override public Integer visitFormalItemNumberPSSF(
            final @Nonnull SAFormalItemNumberPSSF p)
            throws ConstraintError,
              Exception
          {
            final int rsect =
              Integer.compare(
                SAFormalItemNumberSF.this.section,
                p.getSection());
            if (rsect == 0) {
              return Integer.compare(
                SAFormalItemNumberSF.this.formal,
                p.getFormalItem());
            }
            return rsect;
          }

          @Override public Integer visitFormalItemNumberSF(
            final @Nonnull SAFormalItemNumberSF p)
            throws ConstraintError,
              Exception
          {
            final int rsect =
              Integer.compare(
                SAFormalItemNumberSF.this.section,
                p.getSection());
            if (rsect == 0) {
              return Integer.compare(
                SAFormalItemNumberSF.this.formal,
                p.getFormalItem());
            }
            return rsect;
          }

          @Override public Integer visitFormalItemNumberSSF(
            final @Nonnull SAFormalItemNumberSSF p)
            throws ConstraintError,
              Exception
          {
            final int rsect =
              Integer.compare(
                SAFormalItemNumberSF.this.section,
                p.getSection());
            if (rsect == 0) {
              return Integer.compare(
                SAFormalItemNumberSF.this.formal,
                p.getFormalItem());
            }
            return rsect;
          }
        }).intValue();
    } catch (final ConstraintError e) {
      throw new UnreachableCodeException(e);
    } catch (final Exception e) {
      throw new UnreachableCodeException(e);
    }
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
    final SAFormalItemNumberSF other = (SAFormalItemNumberSF) obj;
    if (this.formal != other.formal) {
      return false;
    }
    if (this.section != other.section) {
      return false;
    }
    return true;
  }

  /**
   * @return The formal item number
   */

  @Override public int getFormalItem()
  {
    return this.formal;
  }

  /**
   * @return The section number
   */

  public int getSection()
  {
    return this.section;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.formal;
    result = (prime * result) + this.section;
    return result;
  }

  @Override public <T> T formalItemNumberAccept(
    final SAFormalItemNumberVisitor<T> v)
    throws ConstraintError,
      Exception
  {
    return v.visitFormalItemNumberSF(this);
  }

  @SuppressWarnings("boxing") @Override public
    String
    formalItemNumberFormat()
  {
    return String.format("%d.%d", this.section, this.formal);
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[SAFormalItemNumberSF formal=");
    builder.append(this.formal);
    builder.append(" section=");
    builder.append(this.section);
    builder.append("]");
    return builder.toString();
  }
}
