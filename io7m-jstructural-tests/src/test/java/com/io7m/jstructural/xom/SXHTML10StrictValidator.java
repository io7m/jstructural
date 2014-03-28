package com.io7m.jstructural.xom;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
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

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jlog.Log;

public final class SXHTML10StrictValidator
{
  private static class TrivialErrorHandler implements ErrorHandler
  {
    private @CheckForNull SAXParseException exception;
    private final @Nonnull Log              log;

    public TrivialErrorHandler(
      final @Nonnull Log in_log)
    {
      this.log = in_log;
    }

    @Override public void error(
      final SAXParseException e)
      throws SAXException
    {
      this.log.error(e + ": " + e.getMessage());
      this.exception = e;
    }

    @Override public void fatalError(
      final SAXParseException e)
      throws SAXException
    {
      this.log.critical(e + ": " + e.getMessage());
      this.exception = e;
    }

    public @CheckForNull SAXParseException getException()
    {
      return this.exception;
    }

    @Override public void warning(
      final SAXParseException e)
      throws SAXException
    {
      this.log.warn(e + ": " + e.getMessage());
      this.exception = e;
    }
  }

  static @Nonnull Document fromStreamValidate(
    final @Nonnull InputStream stream,
    final @Nonnull URI uri,
    final @Nonnull Log log)
    throws SAXException,
      ConstraintError,
      ParserConfigurationException,
      ValidityException,
      ParsingException,
      IOException
  {
    Constraints.constrainNotNull(stream, "Stream");
    Constraints.constrainNotNull(log, "Log");

    final Log log_xml = new Log(log, "xhtml10");

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

        if (handler.getException() != null) {
          throw handler.getException();
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
