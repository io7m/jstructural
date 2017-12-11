/*
 * Copyright © 2017 Mark Raynsford <code@io7m.com> http://io7m.com
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

package com.io7m.jstructural.parser.xml.v6;

import com.io7m.jstructural.formats.SFormatDescription;
import com.io7m.jstructural.parser.xml.SXMLSchemaDefinition;
import com.io7m.junreachable.UnreachableCodeException;

import java.net.URI;

/**
 * The 6.0 schema.
 */

public final class S6Schema
{
  /**
   * The schema definition.
   */

  public static final SXMLSchemaDefinition SCHEMA;

  /**
   * The format.
   */

  public static final SFormatDescription FORMAT;

  static {

    SCHEMA = SXMLSchemaDefinition.builder()
      .setNamespace(URI.create("structural:com.io7m.structural:xml:6.0"))
      .setFileIdentifier("file::schema-6.xsd")
      .setLocation(S6Schema.class.getResource(
        "/com/io7m/jstructural/parser/xml/schema-6.xsd"))
      .build();

    FORMAT =
      SFormatDescription.builder()
        .setVendor("com.io7m.structural")
        .setName("xml")
        .setDescription("XML with schema " + SCHEMA.namespace())
        .setVersionMajor(6)
        .setVersionMinor(0)
        .build();
  }

  private S6Schema()
  {
    throw new UnreachableCodeException();
  }
}
