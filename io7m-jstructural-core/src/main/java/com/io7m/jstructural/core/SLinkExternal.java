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

import java.net.URI;

import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;

/**
 * A link to an external resource
 */

public final class SLinkExternal implements
  SParagraphContent,
  SListItemContent,
  SFootnoteContent,
  STableCellContent
{
  /**
   * Construct a new external link element.
   * 
   * @param target
   *          The target
   * @param content
   *          The content in the body of the link
   * @return A new link element
   */

  public static SLinkExternal link(
    final URI target,
    final SNonEmptyList<SLinkContent> content)
  {
    return new SLinkExternal(target, content);
  }

  private final SNonEmptyList<SLinkContent> content;
  private final URI                         target;

  private SLinkExternal(
    final URI in_target,
    final SNonEmptyList<SLinkContent> in_content)
  {
    this.target = NullCheck.notNull(in_target, "Target");
    this.content = NullCheck.notNull(in_content, "Content");
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
    final SLinkExternal other = (SLinkExternal) obj;
    return this.content.equals(other.content)
      && this.target.equals(other.target);
  }

  @Override public <A> A footnoteContentAccept(
    final SFootnoteContentVisitor<A> v)
    throws Exception
  {
    return v.visitLinkExternal(this);
  }

  /**
   * @return The content of the link
   */

  public SNonEmptyList<SLinkContent> getContent()
  {
    return this.content;
  }

  /**
   * @return The link target
   */

  public URI getTarget()
  {
    return this.target;
  }

  @Override public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = (prime * result) + this.content.hashCode();
    result = (prime * result) + this.target.hashCode();
    return result;
  }

  @Override public <A> A listItemContentAccept(
    final SListItemContentVisitor<A> v)
    throws Exception
  {
    return v.visitLinkExternal(this);
  }

  @Override public <A> A paragraphContentAccept(
    final SParagraphContentVisitor<A> v)
    throws Exception
  {
    return v.visitLinkExternal(this);
  }

  @Override public <A> A tableCellContentAccept(
    final STableCellContentVisitor<A> v)
    throws Exception
  {
    return v.visitLinkExternal(this);
  }
}
