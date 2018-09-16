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

package com.io7m.jstructural.parser.xml;

import com.io7m.jstructural.ast.SContentType;
import com.io7m.jstructural.ast.SModelType;
import com.io7m.jstructural.ast.SParsed;
import com.io7m.jstructural.formats.SFormatDescription;
import com.io7m.jstructural.parser.spi.SPIParserConfigurationException;
import com.io7m.jstructural.parser.spi.SPIParserProviderType;
import com.io7m.jstructural.parser.spi.SPIParserRequest;
import com.io7m.jstructural.parser.spi.SPIParserType;
import com.io7m.jstructural.parser.spi.SParseError;
import com.io7m.jstructural.parser.xml.v6.S6Handler;
import com.io7m.jstructural.parser.xml.v6.S6Schema;
import com.io7m.jstructural.probe.spi.SPIProbeRequest;
import com.io7m.jstructural.probe.spi.SPIProbeType;
import com.io7m.junreachable.UnimplementedCodeException;
import com.io7m.jxe.core.JXEHardenedSAXParsers;
import com.io7m.jxe.core.JXESchemaDefinition;
import com.io7m.jxe.core.JXESchemaResolutionMappings;
import com.io7m.jxe.core.JXEXInclude;
import io.vavr.collection.Seq;
import io.vavr.control.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.ext.Locator2;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * The main XML parser provider.
 */

public final class SXMLParserProvider
  implements SPIProbeType, SPIParserProviderType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(SXMLParserProvider.class);

  private static final URI XML_NAMESPACE =
    URI.create("http://www.w3.org/XML/1998/namespace");

  private static final JXESchemaDefinition XML_SCHEMA =
    JXESchemaDefinition.builder()
      .setNamespace(XML_NAMESPACE)
      .setFileIdentifier("file::xml.xsd")
      .setLocation(SXMLParserProvider.class.getResource(
        "/com/io7m/jstructural/parser/xml/xml.xsd"))
      .build();

  private static final JXESchemaResolutionMappings SCHEMAS =
    JXESchemaResolutionMappings.builder()
      .putMappings(S6Schema.SCHEMA.namespace(), S6Schema.SCHEMA)
      .putMappings(XML_NAMESPACE, XML_SCHEMA)
      .build();

  private static final Map<URI, SFormatDescription> FORMATS;

  static {
    FORMATS = new HashMap<>(8);
    FORMATS.put(S6Schema.SCHEMA.namespace(), S6Schema.FORMAT);
  }

  private final JXEHardenedSAXParsers parsers;

  /**
   * Instantiate a parser provider.
   */

  public SXMLParserProvider()
  {
    this.parsers = new JXEHardenedSAXParsers();
  }

  @Override
  public SPIParserType create(
    final SPIParserRequest r)
    throws SPIParserConfigurationException
  {
    Objects.requireNonNull(r, "Request");

    try {
      final XMLReader reader =
        this.parsers.createXMLReader(
          Optional.of(r.baseDirectory()), JXEXInclude.XINCLUDE_ENABLED, SCHEMAS);
      final SHandlerInitial handler = new SHandlerInitial(r, reader);
      reader.setContentHandler(handler);
      return new Parser(r, reader, handler);
    } catch (final ParserConfigurationException | SAXException e) {
      throw new SPIParserConfigurationException(e.getMessage(), e);
    }
  }

  @Override
  public Optional<SFormatDescription> probe(
    final SPIProbeRequest r)
    throws IOException
  {
    Objects.requireNonNull(r, "Request");

    final SchemaFinder finder = new SchemaFinder();

    LOG.debug("probe: {}", r.uri());

    try (InputStream stream = r.streams().open()) {
      final XMLReader reader =
        this.parsers.createXMLReaderNonValidating(
          Optional.of(r.baseDirectory()), JXEXInclude.XINCLUDE_ENABLED);

      final InputSource source = new InputSource(stream);
      source.setSystemId(r.uri().toString());
      reader.setContentHandler(finder);
      reader.parse(source);
    } catch (final SAXException | ParserConfigurationException e) {
      if (!(e instanceof StopSAXParsing)) {
        LOG.error("could not create probe: ", e);
        return Optional.empty();
      }
    }

    return finder.schema;
  }

  private static final class StopSAXParsing extends SAXException
  {
    StopSAXParsing()
    {

    }
  }

  private static final class SchemaFinder extends DefaultHandler2
  {
    private Optional<SFormatDescription> schema = Optional.empty();

    SchemaFinder()
    {

    }

    @Override
    public void startPrefixMapping(
      final String prefix,
      final String uri)
      throws SAXException
    {
      if (LOG.isTraceEnabled()) {
        LOG.trace("startPrefixMapping: {} {}", prefix, uri);
      }

      this.schema = Optional.ofNullable(FORMATS.get(URI.create(uri)));
      if (this.schema.isPresent()) {
        throw new StopSAXParsing();
      }
    }

    @Override
    public void startElement(
      final String uri,
      final String local_name,
      final String qual_name,
      final Attributes attributes)
      throws SAXException
    {
      throw new StopSAXParsing();
    }
  }

  private static final class Parser implements SPIParserType
  {
    private final XMLReader reader;
    private final SPIParserRequest request;
    private final SHandlerInitial handler;

    Parser(
      final SPIParserRequest in_request,
      final XMLReader in_reader,
      final SHandlerInitial in_handler)
    {
      this.request = Objects.requireNonNull(in_request, "Request");
      this.reader = Objects.requireNonNull(in_reader, "Reader");
      this.handler = Objects.requireNonNull(in_handler, "Handler");
    }

    @Override
    public Validation<Seq<SParseError>, SContentType<SParsed>> parse()
      throws IOException
    {
      try {
        final InputSource source = new InputSource(this.request.stream());
        source.setSystemId(this.request.file().toString());
        this.reader.parse(source);

        if (this.handler.errors.errors().isEmpty()) {
          final SModelType<SParsed> result =
            Objects.requireNonNull(this.handler, "Handler")
              .content();

          if (result instanceof SContentType) {
            return Validation.valid((SContentType<SParsed>) result);
          }

          throw new UnimplementedCodeException();
        }

      } catch (final SAXParseException e) {
        this.handler.errors.addError(
          SXMLErrorLog.createErrorFromParseException(e));
      } catch (final SAXException e) {
        this.handler.errors.addError(
          SXMLErrorLog.createErrorFromException(this.request.file(), e));
      }

      return Validation.invalid(this.handler.errors.errors());
    }
  }

  private static final class SHandlerInitial
    extends DefaultHandler2 implements SXMLContentHandlerType
  {
    private final SPIParserRequest request;
    private final XMLReader reader;
    private final SXMLErrorLog errors;
    private SXMLContentHandlerType sub_handler;
    private Locator2 locator;

    SHandlerInitial(
      final SPIParserRequest in_request,
      final XMLReader in_reader)
    {
      this.request = Objects.requireNonNull(in_request, "Request");
      this.reader = Objects.requireNonNull(in_reader, "Reader");
      this.reader.setErrorHandler(this);
      this.reader.setContentHandler(this);
      this.errors = new SXMLErrorLog();
    }

    @Override
    public void setDocumentLocator(
      final Locator in_locator)
    {
      this.locator =
        Objects.requireNonNull((Locator2) in_locator, "Locator");
    }

    @Override
    public void warning(
      final SAXParseException e)
    {
      this.errors.warning(e);
    }

    @Override
    public void error(
      final SAXParseException e)
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
    public void startDocument()
    {
      LOG.trace("startDocument");
    }

    @Override
    public void endDocument()
    {
      LOG.trace("endDocument");
    }

    @Override
    public void startPrefixMapping(
      final String prefix,
      final String uri)
      throws SAXException
    {
      if (LOG.isTraceEnabled()) {
        LOG.trace("startPrefixMapping: {} {}", prefix, uri);
      }

      if (Objects.equals(uri, S6Schema.SCHEMA.namespace().toString())) {
        this.sub_handler =
          new S6Handler(this.request, this.reader, this.errors, this.locator);
        this.reader.setContentHandler(this.sub_handler);
        return;
      }

      throw new SAXParseException(
        "Unrecognized schema namespace URI: " + uri,
        null,
        this.request.file().toString(), 1, 0);
    }

    @Override
    public SModelType<SParsed> content()
      throws SAXParseException
    {
      return this.sub_handler.content();
    }
  }
}
