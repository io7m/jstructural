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

package com.io7m.jstructural.schema;

import com.io7m.junreachable.UnreachableCodeException;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Functions involving the XML schema files.
 */

public final class SSchema
{
  private SSchema()
  {
    throw new UnreachableCodeException();
  }

  /**
   * @return The URI of the RNG schema
   */

  public static URI getSchemaRNGLocation()
  {
    try {
      return SSchema.class.getResource(
        "/com/io7m/jstructural/schema/schema.rng").toURI();
    } catch (final URISyntaxException e) {
      throw new UnreachableCodeException(e);
    }
  }

  /**
   * @return The URI of the RNG schema
   */

  public static URI getSchemaXMLXSDLocation()
  {
    try {
      return SSchema.class.getResource("/com/io7m/jstructural/schema/xml.xsd")
        .toURI();
    } catch (final URISyntaxException e) {
      throw new UnreachableCodeException(e);
    }
  }

  /**
   * @return The URI of the XSD schema
   */

  public static URI getSchemaXSDLocation()
  {
    try {
      return SSchema.class.getResource(
        "/com/io7m/jstructural/schema/schema.xsd").toURI();
    } catch (final URISyntaxException e) {
      throw new UnreachableCodeException(e);
    }
  }
}
