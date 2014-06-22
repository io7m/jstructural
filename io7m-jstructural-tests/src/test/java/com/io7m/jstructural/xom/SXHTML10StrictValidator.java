package com.io7m.jstructural.xom;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import com.io7m.jlog.LogUsableType;
import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;

public final class SXHTML10StrictValidator
{
  private static class TrivialErrorHandler implements ErrorHandler
  {
    private @Nullable SAXParseException exception;
    private final LogUsableType         log;

    public TrivialErrorHandler(
      final LogUsableType in_log)
    {
      this.log = in_log;
    }

    @Override public void error(
      final @Nullable SAXParseException e)
      throws SAXException
    {
      assert e != null;
      this.log.error(e + ": " + e.getMessage());
      this.exception = e;
    }

    @Override public void fatalError(
      final @Nullable SAXParseException e)
      throws SAXException
    {
      assert e != null;
      this.log.critical(e + ": " + e.getMessage());
      this.exception = e;
    }

    public @Nullable SAXParseException getException()
    {
      return this.exception;
    }

    @Override public void warning(
      final @Nullable SAXParseException e)
      throws SAXException
    {
      assert e != null;
      this.log.warn(e + ": " + e.getMessage());
      this.exception = e;
    }
  }

  static Document fromStreamValidate(
    final InputStream stream,
    final URI uri,
    final LogUsableType log)
    throws SAXException,
      ParserConfigurationException,
      ValidityException,
      ParsingException,
      IOException
  {
    NullCheck.notNull(stream, "Stream");
    NullCheck.notNull(log, "Log");

    final LogUsableType log_xml = log.with("xhtml10");

    log_xml.debug("creating sax parser");

    final SAXParserFactory factory = SAXParserFactory.newInstance();

    log_xml.debug("opening xml.xsd");

    final InputStream xml_xsd =
      SXHTML10StrictValidator.class
        .getResourceAsStream("/com/io7m/jstructural/xml.xsd");

    try {
      log_xml.debug("opening schema.xsd");

      final InputStream schema_xsd =
        SXHTML10StrictValidator.class
          .getResourceAsStream("/com/io7m/jstructural/xhtml1-strict.xsd");

      try {
        log_xml.debug("creating schema handler");

        final SchemaFactory schema_factory =
          SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

        final Source[] sources = new Source[2];
        sources[0] = new StreamSource(xml_xsd);
        sources[1] = new StreamSource(schema_xsd);
        factory.setSchema(schema_factory.newSchema(sources));

        final TrivialErrorHandler handler = new TrivialErrorHandler(log_xml);
        final SAXParser parser = factory.newSAXParser();
        final XMLReader reader = parser.getXMLReader();
        reader.setErrorHandler(handler);
        reader.setFeature(
          "http://apache.org/xml/features/nonvalidating/load-external-dtd",
          false);

        log_xml.debug("parsing and validating");
        final Builder builder = new Builder(reader, false);
        final Document doc = builder.build(stream, uri.toString());

        final SAXParseException ex = handler.getException();
        if (ex != null) {
          throw ex;
        }

        return doc;
      } finally {
        schema_xsd.close();
      }
    } finally {
      xml_xsd.close();
    }
  }
}
