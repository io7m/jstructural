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

import com.io7m.jstructural.ast.SModelType;
import com.io7m.jstructural.ast.SParsed;
import com.io7m.jstructural.ast.SText;
import com.io7m.jstructural.parser.spi.SPIParserRequest;
import com.io7m.jstructural.parser.xml.SXMLContentHandlerType;
import com.io7m.jstructural.parser.xml.SXMLErrorLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.ext.Locator2;

import java.util.Objects;

import static com.io7m.jstructural.ast.SParsed.PARSED;

/**
 * A content handler for version 6.0 schemas.
 */

public final class S6Handler
  extends DefaultHandler2 implements SXMLContentHandlerType
{
  private static final Logger LOG = LoggerFactory.getLogger(S6Handler.class);

  private final SPIParserRequest request;
  private final XMLReader reader;
  private final SXMLErrorLog errors;
  private Locator2 locator;
  private S6ElementHandler handler;

  /**
   * Construct a handler.
   *
   * @param in_request The original request
   * @param in_reader  The XML reader
   * @param in_errors  The error log
   * @param in_locator The current locator
   */

  public S6Handler(
    final SPIParserRequest in_request,
    final XMLReader in_reader,
    final SXMLErrorLog in_errors,
    final Locator2 in_locator)
  {
    this.request = Objects.requireNonNull(in_request, "Request");
    this.reader = Objects.requireNonNull(in_reader, "Reader");
    this.errors = Objects.requireNonNull(in_errors, "Errors");
    this.locator = Objects.requireNonNull(in_locator, "Locator");
    this.reader.setErrorHandler(this);
    this.reader.setContentHandler(this);
  }

  @Override
  public void startDocument()
    throws SAXException
  {
    LOG.debug("startDocument");
  }

  @Override
  public void warning(
    final SAXParseException e)
    throws SAXException
  {
    this.errors.warning(e);
  }

  @Override
  public void error(
    final SAXParseException e)
    throws SAXException
  {
    this.errors.error(e);
  }

  @Override
  public void fatalError(
    final SAXParseException e)
    throws SAXException
  {
    this.errors.fatalError(e);
    throw e;
  }

  @Override
  public void endDocument()
    throws SAXException
  {
    LOG.debug("endDocument");
  }

  @Override
  public void startPrefixMapping(
    final String prefix,
    final String uri)
    throws SAXException
  {
    LOG.debug("startPrefixMapping: {} {}", prefix, uri);
  }

  @Override
  public void endPrefixMapping(
    final String prefix)
    throws SAXException
  {
    LOG.debug("endPrefixMapping: {}", prefix);
  }

  @Override
  public void characters(
    final char[] ch,
    final int start,
    final int length)
    throws SAXException
  {
    LOG.debug(
      "characters: {} of {}",
      Integer.valueOf(length),
      this.locator.getEncoding());

    if (!this.errors.errors().isEmpty()) {
      return;
    }

    final S6ElementHandler h = this.handler;
    if (h != null) {
      final SText<SParsed> text =
        SText.<SParsed>builder()
          .setText(String.valueOf(ch, start, length))
          .setData(PARSED)
          .setLexical(S6Lexical.makeFromLocator(this.locator))
          .build();
      h.onText(text);
    }
  }

  // CHECKSTYLE:OFF
  @Override
  public void startElement(
    final String uri,
    final String local_name,
    final String qual_name,
    final Attributes attrs)
    throws SAXException
  {
    if (!this.errors.errors().isEmpty()) {
      return;
    }

    if (!Objects.equals(uri, S6Schema.SCHEMA.namespace().toString())) {
      return;
    }

    LOG.trace("startElement: {} {} {} {}", uri, local_name, qual_name, attrs);

    switch (local_name) {
      case "table-body": {
        this.handler = new S6TableBodyHandler(this.handler, attrs, this.locator);
        break;
      }

      case "table-cell": {
        this.handler = new S6TableCellHandler(this.handler, attrs, this.locator);
        break;
      }

      case "document": {
        break;
      }

      case "footnote-ref": {
        this.handler = new S6FootnoteRefHandler(this.handler, attrs, this.locator);
        break;
      }

      case "footnote": {
        break;
      }

      case "formal-item-ref": {
        this.handler = new S6FormalItemRefHandler(this.handler, attrs, this.locator);
        break;
      }

      case "formal-item": {
        break;
      }

      case "table-head": {
        this.handler =
          new S6TableHeadHandler(this.handler, attrs, this.locator);
        break;
      }

      case "image": {
        this.handler = new S6ImageHandler(this.handler, attrs, this.locator);
        break;
      }

      case "link-external": {
        this.handler =
          new S6LinkExternalHandler(this.handler, attrs, this.locator);
        break;
      }

      case "link": {
        this.handler = new S6LinkHandler(this.handler, attrs, this.locator);
        break;
      }

      case "list-item": {
        this.handler = new S6ListItemHandler(this.handler, attrs, this.locator);
        break;
      }

      case "list-ordered": {
        this.handler =
          new S6ListOrderedHandler(this.handler, attrs, this.locator);
        break;
      }

      case "list-unordered": {
        this.handler =
          new S6ListUnorderedHandler(this.handler, attrs, this.locator);
        break;
      }

      case "table-column-name": {
        this.handler = new S6TableColumnNameHandler(this.handler, attrs, this.locator);
        break;
      }

      case "paragraph": {
        break;
      }

      case "table-row": {
        this.handler = new S6TableRowHandler(this.handler, attrs, this.locator);
        break;
      }

      case "section": {
        break;
      }

      case "subsection": {
        break;
      }

      case "table": {
        this.handler = new S6TableHandler(this.handler, attrs, this.locator);
        break;
      }

      case "term": {
        this.handler = new S6TermHandler(this.handler, attrs, this.locator);
        break;
      }

      case "verbatim": {
        this.handler = new S6VerbatimHandler(this.handler, attrs, this.locator);
        break;
      }

      default: {
        break;
      }
    }
  }
  // CHECKSTYLE:ON

  @Override
  public void endElement(
    final String uri,
    final String local_name,
    final String qual_name)
    throws SAXException
  {
    if (!this.errors.errors().isEmpty()) {
      return;
    }

    if (!Objects.equals(uri, S6Schema.SCHEMA.namespace().toString())) {
      return;
    }

    LOG.trace("endElement: {} {} {} {}", uri, local_name, qual_name);

    this.handler.onCompleted();
    if (this.handler.parent() != null) {
      this.handler = this.handler.parent();
    }
  }

  @Override
  public SModelType<SParsed> content()
    throws SAXParseException
  {
    return this.handler.finishContent();
  }
}
