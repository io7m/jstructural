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

import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import net.jcip.annotations.Immutable;

/**
 * A list of formal items of a given kind.
 */

@Immutable public final class SAFormalItemList
  implements SAFormalItemContent, SAParagraphContent
{
  private final String kind;

  SAFormalItemList(
    final String in_kind)
  {
    this.kind = NullCheck.notNull(in_kind, "Kind");
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
    final SAFormalItemList other = (SAFormalItemList) obj;
    return this.kind.equals(other.kind);
  }

  @Override public <A> A formalItemContentAccept(
    final SAFormalItemContentVisitor<A> v)
    throws Exception
  {
    return v.visitFormalItemList(this);
  }

  /**
   * @return The kind of formal items
   */

  public String getKind()
  {
    return this.kind;
  }

  @Override public int hashCode()
  {
    return this.kind.hashCode();
  }

  @Override public <A> A paragraphContentAccept(
    final SAParagraphContentVisitor<A> v)
    throws Exception
  {
    return v.visitFormalItemList(this);
  }

  @Override public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[SAFormalItemList ");
    builder.append(this.kind);
    builder.append("]");
    final String r = builder.toString();
    assert r != null;
    return r;
  }
}
