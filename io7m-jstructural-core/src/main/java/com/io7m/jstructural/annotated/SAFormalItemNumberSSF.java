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

import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import com.io7m.jranges.RangeCheck;
import com.io7m.jranges.Ranges;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * A formal item number consisting of a section, subsection, and formal item.
 */

public final class SAFormalItemNumberSSF extends SAFormalItemNumber
{
  private final int formal;
  private final int section;
  private final int subsection;

  /**
   * Construct a new formal item number
   *
   * @param in_section    The section number
   * @param in_subsection The subsection number
   * @param in_formal     The formal item number
   */

  public SAFormalItemNumberSSF(
    final int in_section,
    final int in_subsection,
    final int in_formal)
  {
    this.section = RangeCheck.checkIncludedInInteger(
      in_section,
      "Section number",
      Ranges.POSITIVE_INTEGER,
      "Valid section number range");
    this.formal = RangeCheck.checkIncludedInInteger(
      in_formal,
      "Formal item number",
      Ranges.POSITIVE_INTEGER,
      "Valid formal item number range");
    this.subsection = RangeCheck.checkIncludedInInteger(
      in_subsection,
      "Subsection number",
      Ranges.POSITIVE_INTEGER,
      "Valid subsection number range");
  }

  @SuppressWarnings({ "boxing", "null", "synthetic-access" }) @Override
  public int compareTo(
    final @Nullable SAFormalItemNumber o)
  {
    try {
      return NullCheck.notNull(o, "Other").formalItemNumberAccept(
        new SAFormalItemNumberVisitor<Integer>()
        {
          @Override public Integer visitFormalItemNumberPSF(
            final SAFormalItemNumberPSF p)
            throws Exception
          {
            final int rsect = Integer.compare(
              SAFormalItemNumberSSF.this.section, p.getSection());
            if (rsect == 0) {
              return Integer.compare(
                SAFormalItemNumberSSF.this.formal, p.getFormalItem());
            }
            return rsect;
          }

          @Override public Integer visitFormalItemNumberPSSF(
            final SAFormalItemNumberPSSF p)
            throws Exception
          {
            final int rsect = Integer.compare(
              SAFormalItemNumberSSF.this.section, p.getSection());
            if (rsect == 0) {
              final int rsubs = Integer.compare(
                SAFormalItemNumberSSF.this.subsection, p.getSubsection());
              if (rsubs == 0) {
                return Integer.compare(
                  SAFormalItemNumberSSF.this.formal, p.getFormalItem());
              }
              return rsubs;
            }
            return rsect;
          }

          @Override public Integer visitFormalItemNumberSF(
            final SAFormalItemNumberSF p)
            throws Exception
          {
            final int rsect = Integer.compare(
              SAFormalItemNumberSSF.this.section, p.getSection());
            if (rsect == 0) {
              return Integer.compare(
                SAFormalItemNumberSSF.this.formal, p.getFormalItem());
            }
            return rsect;
          }

          @Override public Integer visitFormalItemNumberSSF(
            final SAFormalItemNumberSSF p)
            throws Exception
          {
            final int rsect = Integer.compare(
              SAFormalItemNumberSSF.this.section, p.getSection());
            if (rsect == 0) {
              final int rsubs = Integer.compare(
                SAFormalItemNumberSSF.this.subsection, p.getSubsection());
              if (rsubs == 0) {
                return Integer.compare(
                  SAFormalItemNumberSSF.this.formal, p.getFormalItem());
              }
              return rsubs;
            }
            return rsect;
          }
        }).intValue();

    } catch (final Exception e) {
      throw new UnreachableCodeException(e);
    }
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
    final SAFormalItemNumberSSF other = (SAFormalItemNumberSSF) obj;
    if (this.formal != other.formal) {
      return false;
    }
    if (this.section != other.section) {
      return false;
    }
    return this.subsection == other.subsection;
  }

  @Override public <T> T formalItemNumberAccept(
    final SAFormalItemNumberVisitor<T> v)
    throws Exception
  {
    return v.visitFormalItemNumberSSF(this);
  }

  @SuppressWarnings("boxing") @Override public String formalItemNumberFormat()
  {
    final String r =
      String.format("%d.%d.%d", this.section, this.subsection, this.formal);
    assert r != null;
    return r;
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

  /**
   * @return The subsection number
   */

  public int getSubsection()
  {
    return this.subsection;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.formal;
    result = (prime * result) + this.section;
    result = (prime * result) + this.subsection;
    return result;
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[SAFormalItemNumberPSSF formal=");
    builder.append(this.formal);
    builder.append(" section=");
    builder.append(this.section);
    builder.append(" subsection=");
    builder.append(this.subsection);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
