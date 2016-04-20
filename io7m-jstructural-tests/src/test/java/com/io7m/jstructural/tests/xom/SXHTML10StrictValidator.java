package com.io7m.jstructural.tests.xom;

import com.io7m.jnull.NullCheck;
import com.io7m.jnull.Nullable;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public final class SXHTML10StrictValidator
{
  private static final Logger LOG;

  static {
    LOG = LoggerFactory.getLogger(SXHTML10StrictValidator.class);
  }

  private SXHTML10StrictValidator()
  {

  }

  static Document fromStreamValidate(
    final InputStream stream,
    final URI uri)
    throws SAXException,
    ParserConfigurationException,
    ValidityException,
    ParsingException,
    IOException
  {
    NullCheck.notNull(stream, "Stream");

    SXHTML10StrictValidator.LOG.debug("xml: creating sax parser");

    final SAXParserFactory factory = SAXParserFactory.newInstance();

    SXHTML10StrictValidator.LOG.debug("xml: opening xml.xsd");

    final InputStream xml_xsd =
      SXHTML10StrictValidator.class
        .getResourceAsStream("/com/io7m/jstructural/tests/xml.xsd");

    try {
      SXHTML10StrictValidator.LOG.debug("xml: opening schema.xsd");

      final InputStream schema_xsd =
        SXHTML10StrictValidator.class
          .getResourceAsStream("/com/io7m/jstructural/tests/xhtml1-strict.xsd");

      try {
        SXHTML10StrictValidator.LOG.debug("xml: creating schema handler");

        final SchemaFactory schema_factory =
          SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

        final Source[] sources = new Source[2];
        sources[0] = new StreamSource(xml_xsd);
        sources[1] = new StreamSource(schema_xsd);
        factory.setSchema(schema_factory.newSchema(sources));

        final TrivialErrorHandler handler = new TrivialErrorHandler();
        final SAXParser parser = factory.newSAXParser();
        final XMLReader reader = parser.getXMLReader();
        reader.setErrorHandler(handler);
        reader.setFeature(
          "http://apache.org/xml/features/nonvalidating/load-external-dtd",
          false);

        SXHTML10StrictValidator.LOG.debug("xml: parsing and validating");
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

  private static class TrivialErrorHandler implements ErrorHandler
  {
    private @Nullable SAXParseException exception;

    public TrivialErrorHandler()
    {

    }

    @Override
    public void error(
      final @Nullable SAXParseException e)
      throws SAXException
    {
      assert e != null;
      SXHTML10StrictValidator.LOG.error(e + ": " + e.getMessage());
      this.exception = e;
    }

    @Override
    public void fatalError(
      final @Nullable SAXParseException e)
      throws SAXException
    {
      assert e != null;
      SXHTML10StrictValidator.LOG.error(e + ": " + e.getMessage());
      this.exception = e;
    }

    public
    @Nullable
    SAXParseException getException()
    {
      return this.exception;
    }

    @Override
    public void warning(
      final @Nullable SAXParseException e)
      throws SAXException
    {
      assert e != null;
      SXHTML10StrictValidator.LOG.warn(e + ": " + e.getMessage());
      this.exception = e;
    }
  }
}
