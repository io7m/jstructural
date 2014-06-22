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

package com.io7m.jstructural.xom;

import nu.xom.Document;
import nu.xom.Element;

final class SXHTMLPage
{
  private final  Element  body;
  private final  Element  body_container;
  private final  Document document;
  private final  Element  head;

  SXHTMLPage(
    final  Document doc,
    final  Element in_head,
    final  Element in_body,
    final  Element in_body_container)
  {
    this.document = doc;
    this.head = in_head;
    this.body = in_body;
    this.body_container = in_body_container;
  }

  public  Element getBody()
  {
    return this.body;
  }

  public  Element getBodyContainer()
  {
    return this.body_container;
  }

  public  Document getDocument()
  {
    return this.document;
  }

  public  Element getHead()
  {
    return this.head;
  }
}
