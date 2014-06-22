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

package com.io7m.jstructural.annotated;

import com.io7m.jfunctional.OptionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;

/**
 * A table.
 */

public final class SATable implements SAFormalItemContent, SAParagraphContent
{
  private final SATableBody             body;
  private final OptionType<SATableHead> header;
  private final SATableSummary          summary;

  SATable(
    final SATableSummary in_summary,
    final OptionType<SATableHead> in_header,
    final SATableBody in_body)
  {
    this.summary = NullCheck.notNull(in_summary, "Summary");
    this.header = NullCheck.notNull(in_header, "Header");
    this.body = NullCheck.notNull(in_body, "Body");
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
    final SATable other = (SATable) obj;
    return this.body.equals(other.body)
      && this.header.equals(other.header)
      && this.summary.equals(other.summary);
  }

  @Override public <A> A formalItemContentAccept(
    final SAFormalItemContentVisitor<A> v)
    throws Exception
  {
    return v.visitTable(this);
  }

  /**
   * @return The table body
   */

  public SATableBody getBody()
  {
    return this.body;
  }

  /**
   * @return The table header
   */

  public OptionType<SATableHead> getHeader()
  {
    return this.header;
  }

  /**
   * @return The table summary
   */

  public SATableSummary getSummary()
  {
    return this.summary;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.body.hashCode();
    result = (prime * result) + this.header.hashCode();
    result = (prime * result) + this.summary.hashCode();
    return result;
  }

  @Override public <A> A paragraphContentAccept(
    final SAParagraphContentVisitor<A> v)
    throws Exception
  {
    return v.visitTable(this);
  }
}
