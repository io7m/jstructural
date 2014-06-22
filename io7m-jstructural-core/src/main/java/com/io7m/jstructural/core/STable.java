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

import com.io7m.jfunctional.Option;
import com.io7m.jfunctional.OptionType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;

/**
 * A table.
 */

public final class STable implements SFormalItemContent, SParagraphContent
{
  /**
   * Construct a new table with the given summary and body.
   * 
   * @param in_summary
   *          The summary
   * @param in_body
   *          The body
   * @return A new table
   */

  public static STable table(
    final STableSummary in_summary,
    final STableBody in_body)
  {
    final OptionType<STableHead> no_header = Option.none();
    return new STable(in_summary, no_header, in_body);
  }

  /**
   * Construct a new table with the given summary, header, and body.
   * 
   * @param in_head
   *          The header
   * @param in_summary
   *          The summary
   * @param in_body
   *          The body
   * @return A new table
   */

  public static STable tableHeader(
    final STableSummary in_summary,
    final STableHead in_head,
    final STableBody in_body)
  {
    final OptionType<STableHead> some_header =
      Option.some(NullCheck.notNull(in_head, "Header"));
    return new STable(in_summary, some_header, in_body);
  }

  private final STableBody             body;
  private final OptionType<STableHead> header;
  private final STableSummary          summary;

  private STable(
    final STableSummary in_summary,
    final OptionType<STableHead> in_header,
    final STableBody in_body)
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
    final STable other = (STable) obj;
    return this.body.equals(other.body)
      && this.header.equals(other.header)
      && this.summary.equals(other.summary);
  }

  @Override public <A> A formalItemContentAccept(
    final SFormalItemContentVisitor<A> v)
    throws Exception
  {
    return v.visitTable(this);
  }

  /**
   * @return The table body
   */

  public STableBody getBody()
  {
    return this.body;
  }

  /**
   * @return The table header
   */

  public OptionType<STableHead> getHeader()
  {
    return this.header;
  }

  /**
   * @return The table summary
   */

  public STableSummary getSummary()
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
    final SParagraphContentVisitor<A> v)
    throws Exception
  {
    return v.visitTable(this);
  }
}
