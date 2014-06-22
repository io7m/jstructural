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

import nu.xom.Attribute;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Text;

import com.io7m.jfunctional.FunctionType;
import com.io7m.jfunctional.Unit;
import com.io7m.jnull.NullCheck;
import com.io7m.jstructural.core.SDocument;
import com.io7m.jstructural.core.SDocumentContents;
import com.io7m.jstructural.core.SDocumentStyle;
import com.io7m.jstructural.core.SDocumentTitle;
import com.io7m.jstructural.core.SDocumentVisitor;
import com.io7m.jstructural.core.SDocumentWithParts;
import com.io7m.jstructural.core.SDocumentWithSections;
import com.io7m.jstructural.core.SFootnote;
import com.io7m.jstructural.core.SFootnoteContent;
import com.io7m.jstructural.core.SFootnoteContentVisitor;
import com.io7m.jstructural.core.SFormalItem;
import com.io7m.jstructural.core.SFormalItemContent;
import com.io7m.jstructural.core.SFormalItemContentVisitor;
import com.io7m.jstructural.core.SFormalItemList;
import com.io7m.jstructural.core.SFormalItemTitle;
import com.io7m.jstructural.core.SID;
import com.io7m.jstructural.core.SImage;
import com.io7m.jstructural.core.SLink;
import com.io7m.jstructural.core.SLinkContent;
import com.io7m.jstructural.core.SLinkContentVisitor;
import com.io7m.jstructural.core.SLinkExternal;
import com.io7m.jstructural.core.SListItem;
import com.io7m.jstructural.core.SListItemContent;
import com.io7m.jstructural.core.SListItemContentVisitor;
import com.io7m.jstructural.core.SListOrdered;
import com.io7m.jstructural.core.SListUnordered;
import com.io7m.jstructural.core.SParagraph;
import com.io7m.jstructural.core.SParagraphContent;
import com.io7m.jstructural.core.SParagraphContentVisitor;
import com.io7m.jstructural.core.SPart;
import com.io7m.jstructural.core.SPartContents;
import com.io7m.jstructural.core.SPartTitle;
import com.io7m.jstructural.core.SSection;
import com.io7m.jstructural.core.SSectionContents;
import com.io7m.jstructural.core.SSectionTitle;
import com.io7m.jstructural.core.SSectionVisitor;
import com.io7m.jstructural.core.SSectionWithParagraphs;
import com.io7m.jstructural.core.SSectionWithSubsections;
import com.io7m.jstructural.core.SSubsection;
import com.io7m.jstructural.core.SSubsectionContent;
import com.io7m.jstructural.core.SSubsectionContentVisitor;
import com.io7m.jstructural.core.SSubsectionTitle;
import com.io7m.jstructural.core.STable;
import com.io7m.jstructural.core.STableBody;
import com.io7m.jstructural.core.STableCell;
import com.io7m.jstructural.core.STableCellContent;
import com.io7m.jstructural.core.STableCellContentVisitor;
import com.io7m.jstructural.core.STableColumnName;
import com.io7m.jstructural.core.STableHead;
import com.io7m.jstructural.core.STableRow;
import com.io7m.jstructural.core.STableSummary;
import com.io7m.jstructural.core.STerm;
import com.io7m.jstructural.core.SText;
import com.io7m.jstructural.core.SVerbatim;
import com.io7m.jstructural.core.SXML;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * Serialization functions that use XOM to serialize document elements to XML.
 */

public final class SDocumentSerializer
{
  private static class IDAdder implements FunctionType<SID, Unit>
  {
    private final Element e;

    public IDAdder(
      final Element in_e)
    {
      this.e = in_e;
    }

    @Override public Unit call(
      final SID x)
    {
      final Attribute a =
        new Attribute(
          "xml:id",
          "http://www.w3.org/XML/1998/namespace",
          x.getActual());
      this.e.addAttribute(a);
      return Unit.unit();
    }
  }

  private static class TypeAdder implements FunctionType<String, Unit>
  {
    private final Element e;

    public TypeAdder(
      final Element in_e)
    {
      this.e = in_e;
    }

    @Override public Unit call(
      final String x)
    {
      final Attribute a = new Attribute("s:type", SXML.XML_URI.toString(), x);
      this.e.addAttribute(a);
      return Unit.unit();
    }
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param d
   *          The element
   * @return An XML element
   */

  public static Element document(
    final SDocument d)
  {
    NullCheck.notNull(d, "Document");

    try {
      return d.documentAccept(new SDocumentVisitor<Element>() {
        @Override public Element visitDocumentWithParts(
          final SDocumentWithParts dp)
          throws Exception
        {
          return SDocumentSerializer.documentWithParts(dp);
        }

        @Override public Element visitDocumentWithSections(
          final SDocumentWithSections ds)
          throws Exception
        {
          return SDocumentSerializer.documentWithSections(ds);
        }
      });
    } catch (final Exception e) {
      throw new UnreachableCodeException(e);
    }
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element documentContents(
    final SDocumentContents s)
  {
    NullCheck.notNull(s, "Document contents");
    final Element e =
      new Element("s:document-contents", SXML.XML_URI.toString());
    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element documentStyle(
    final SDocumentStyle s)
  {
    NullCheck.notNull(s, "Document style");
    final Element e =
      new Element("s:document-style", SXML.XML_URI.toString());
    e.appendChild(s.getActual().toString());
    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element documentTitle(
    final SDocumentTitle s)
  {
    NullCheck.notNull(s, "Document title");
    final Element e =
      new Element("s:document-title", SXML.XML_URI.toString());
    e.appendChild(s.getActual().toString());
    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element documentWithParts(
    final SDocumentWithParts s)
  {
    NullCheck.notNull(s, "Document");

    final Element e = new Element("s:document", SXML.XML_URI.toString());
    s.getStyle().map(new FunctionType<SDocumentStyle, Unit>() {
      @Override public Unit call(
        final SDocumentStyle x)
      {
        e.appendChild(SDocumentSerializer.documentStyle(x));
        return Unit.unit();
      }
    });

    s.getContents().map(new FunctionType<SDocumentContents, Unit>() {
      @Override public Unit call(
        final SDocumentContents x)
      {
        e.appendChild(SDocumentSerializer.documentContents(x));
        return Unit.unit();
      }
    });

    e.appendChild(SDocumentSerializer.documentTitle(s.getTitle()));

    for (final SPart c : s.getParts().getElements()) {
      e.appendChild(SDocumentSerializer.part(c));
    }

    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element documentWithSections(
    final SDocumentWithSections s)
  {
    NullCheck.notNull(s, "Document");

    final Element e = new Element("s:document", SXML.XML_URI.toString());
    s.getStyle().map(new FunctionType<SDocumentStyle, Unit>() {
      @Override public Unit call(
        final SDocumentStyle x)
      {
        e.appendChild(SDocumentSerializer.documentStyle(x));
        return Unit.unit();
      }
    });

    s.getContents().map(new FunctionType<SDocumentContents, Unit>() {
      @Override public Unit call(
        final SDocumentContents x)
      {
        e.appendChild(SDocumentSerializer.documentContents(x));
        return Unit.unit();
      }
    });

    e.appendChild(SDocumentSerializer.documentTitle(s.getTitle()));

    for (final SSection c : s.getSections().getElements()) {
      e.appendChild(SDocumentSerializer.section(c));
    }

    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element footnote(
    final SFootnote s)
  {
    NullCheck.notNull(s, "Footnote");

    final Element e = new Element("s:footnote", SXML.XML_URI.toString());
    for (final SFootnoteContent c : s.getContent().getElements()) {
      e.appendChild(SDocumentSerializer.footnoteContent(c));
    }
    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param c
   *          The element
   * @return An XML element
   */

  public static Node footnoteContent(
    final SFootnoteContent c)
  {
    NullCheck.notNull(c, "Footnote content");

    try {
      return c.footnoteContentAccept(new SFootnoteContentVisitor<Node>() {
        @Override public Node visitFootnote(
          final SFootnote footnote)
          throws Exception
        {
          return SDocumentSerializer.footnote(footnote);
        }

        @Override public Node visitImage(
          final SImage image)
          throws Exception
        {
          return SDocumentSerializer.image(image);
        }

        @Override public Node visitLink(
          final SLink link)
          throws Exception
        {
          return SDocumentSerializer.link(link);
        }

        @Override public Node visitLinkExternal(
          final SLinkExternal link)
          throws Exception
        {
          return SDocumentSerializer.linkExternal(link);
        }

        @Override public Node visitListOrdered(
          final SListOrdered list)
          throws Exception
        {
          return SDocumentSerializer.listOrdered(list);
        }

        @Override public Node visitListUnordered(
          final SListUnordered list)
          throws Exception
        {
          return SDocumentSerializer.listUnordered(list);
        }

        @Override public Node visitTerm(
          final STerm term)
          throws Exception
        {
          return SDocumentSerializer.term(term);
        }

        @Override public Node visitText(
          final SText text)
          throws Exception
        {
          return SDocumentSerializer.text(text);
        }

        @Override public Node visitVerbatim(
          final SVerbatim text)
          throws Exception
        {
          return SDocumentSerializer.verbatim(text);
        }
      });
    } catch (final Exception e) {
      throw new UnreachableCodeException(e);
    }
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element formalItem(
    final SFormalItem s)
  {
    NullCheck.notNull(s, "Formal item");

    final Element e = new Element("s:formal-item", SXML.XML_URI.toString());
    s.getType().map(new TypeAdder(e));

    final Attribute ak =
      new Attribute("s:kind", SXML.XML_URI.toString(), s.getKind());
    e.addAttribute(ak);
    e.appendChild(SDocumentSerializer.formalItemTitle(s.getTitle()));
    e.appendChild(SDocumentSerializer.formalItemContent(s.getContent()));
    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element formalItemContent(
    final SFormalItemContent s)
  {
    NullCheck.notNull(s, "Content");
    try {
      return s
        .formalItemContentAccept(new SFormalItemContentVisitor<Element>() {
          @Override public Element visitFormalItemList(
            final SFormalItemList list)
            throws Exception
          {
            return SDocumentSerializer.formalItemList(list);
          }

          @Override public Element visitImage(
            final SImage image)
            throws Exception
          {
            return SDocumentSerializer.image(image);
          }

          @Override public Element visitListOrdered(
            final SListOrdered list)
            throws Exception
          {
            return SDocumentSerializer.listOrdered(list);
          }

          @Override public Element visitListUnordered(
            final SListUnordered list)
            throws Exception
          {
            return SDocumentSerializer.listUnordered(list);
          }

          @Override public Element visitTable(
            final STable e)
            throws Exception
          {
            return SDocumentSerializer.table(e);
          }

          @Override public Element visitVerbatim(
            final SVerbatim text)
            throws Exception
          {
            return SDocumentSerializer.verbatim(text);
          }
        });
    } catch (final Exception e) {
      throw new UnreachableCodeException(e);
    }
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element formalItemList(
    final SFormalItemList s)
  {
    NullCheck.notNull(s, "List");

    final Element e =
      new Element("s:formal-item-list", SXML.XML_URI.toString());
    final Attribute ak =
      new Attribute("s:kind", SXML.XML_URI.toString(), s.getKind());
    e.addAttribute(ak);
    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element formalItemTitle(
    final SFormalItemTitle s)
  {
    NullCheck.notNull(s, "Title");

    final Element e =
      new Element("s:formal-item-title", SXML.XML_URI.toString());
    e.appendChild(s.getActual().toString());
    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element image(
    final SImage s)
  {
    NullCheck.notNull(s, "Image");

    final Element e = new Element("s:image", SXML.XML_URI.toString());
    final Attribute at =
      new Attribute("s:source", SXML.XML_URI.toString(), s
        .getURI()
        .toString());

    e.appendChild(s.getText());
    e.addAttribute(at);
    s.getType().map(new TypeAdder(e));

    s.getHeight().map(new FunctionType<Integer, Unit>() {
      @Override public Unit call(
        final Integer x)
      {
        final Attribute a =
          new Attribute("s:height", SXML.XML_URI.toString(), x.toString());
        e.addAttribute(a);
        return Unit.unit();
      }
    });

    s.getWidth().map(new FunctionType<Integer, Unit>() {
      @Override public Unit call(
        final Integer x)
      {
        final Attribute a =
          new Attribute("s:width", SXML.XML_URI.toString(), x.toString());
        e.addAttribute(a);
        return Unit.unit();
      }
    });

    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element link(
    final SLink s)
  {
    NullCheck.notNull(s, "Link");
    final Attribute at =
      new Attribute("s:target", SXML.XML_URI.toString(), s.getTarget());
    final Element e = new Element("s:link", SXML.XML_URI.toString());
    e.addAttribute(at);

    for (final SLinkContent c : s.getContent().getElements()) {
      e.appendChild(SDocumentSerializer.linkContent(c));
    }

    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param c
   *          The element
   * @return An XML element
   */

  public static Node linkContent(
    final SLinkContent c)
  {
    NullCheck.notNull(c, "Link content");

    try {
      return c.linkContentAccept(new SLinkContentVisitor<Node>() {
        @Override public Node visitImage(
          final SImage image)
          throws Exception
        {
          return SDocumentSerializer.image(image);
        }

        @Override public Node visitText(
          final SText text)
          throws Exception
        {
          return SDocumentSerializer.text(text);
        }
      });
    } catch (final Exception e) {
      throw new UnreachableCodeException(e);
    }
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element linkExternal(
    final SLinkExternal s)
  {
    NullCheck.notNull(s, "Link");
    final Attribute at =
      new Attribute("s:target", SXML.XML_URI.toString(), s
        .getTarget()
        .toString());
    final Element e = new Element("s:link-external", SXML.XML_URI.toString());
    e.addAttribute(at);

    for (final SLinkContent c : s.getContent().getElements()) {
      e.appendChild(SDocumentSerializer.linkContent(c));
    }

    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element listItem(
    final SListItem s)
  {
    NullCheck.notNull(s, "List item");

    final Element e = new Element("s:list-item", SXML.XML_URI.toString());
    s.getType().map(new TypeAdder(e));

    for (final SListItemContent c : s.getContent().getElements()) {
      e.appendChild(SDocumentSerializer.listItemContent(c));
    }

    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param c
   *          The element
   * @return An XML element
   */

  public static Node listItemContent(
    final SListItemContent c)
  {
    try {
      return c.listItemContentAccept(new SListItemContentVisitor<Node>() {
        @Override public Node visitFootnote(
          final SFootnote footnote)
          throws Exception
        {
          return SDocumentSerializer.footnote(footnote);
        }

        @Override public Node visitImage(
          final SImage image)
          throws Exception
        {
          return SDocumentSerializer.image(image);
        }

        @Override public Node visitLink(
          final SLink link)
          throws Exception
        {
          return SDocumentSerializer.link(link);
        }

        @Override public Node visitLinkExternal(
          final SLinkExternal link)
          throws Exception
        {
          return SDocumentSerializer.linkExternal(link);
        }

        @Override public Node visitListOrdered(
          final SListOrdered list)
          throws Exception
        {
          return SDocumentSerializer.listOrdered(list);
        }

        @Override public Node visitListUnordered(
          final SListUnordered list)
          throws Exception
        {
          return SDocumentSerializer.listUnordered(list);
        }

        @Override public Node visitTerm(
          final STerm term)
          throws Exception
        {
          return SDocumentSerializer.term(term);
        }

        @Override public Node visitText(
          final SText text)
          throws Exception
        {
          return SDocumentSerializer.text(text);
        }

        @Override public Node visitVerbatim(
          final SVerbatim text)
          throws Exception
        {
          return SDocumentSerializer.verbatim(text);
        }
      });
    } catch (final Exception e) {
      throw new UnreachableCodeException(e);
    }
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element listOrdered(
    final SListOrdered s)
  {
    NullCheck.notNull(s, "List");

    final Element e = new Element("s:list-ordered", SXML.XML_URI.toString());
    s.getType().map(new TypeAdder(e));

    for (final SListItem c : s.getItems().getElements()) {
      e.appendChild(SDocumentSerializer.listItem(c));
    }

    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element listUnordered(
    final SListUnordered s)
  {
    NullCheck.notNull(s, "List");

    final Element e =
      new Element("s:list-unordered", SXML.XML_URI.toString());
    s.getType().map(new TypeAdder(e));

    for (final SListItem c : s.getItems().getElements()) {
      e.appendChild(SDocumentSerializer.listItem(c));
    }

    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element paragraph(
    final SParagraph s)
  {
    NullCheck.notNull(s, "Paragraph");
    final Element e = new Element("s:paragraph", SXML.XML_URI.toString());
    s.getType().map(new TypeAdder(e));
    s.getID().map(new IDAdder(e));

    for (final SParagraphContent c : s.getContent().getElements()) {
      e.appendChild(SDocumentSerializer.paragraphContent(c));
    }

    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param c
   *          The element
   * @return An XML element
   */

  public static Node paragraphContent(
    final SParagraphContent c)
  {
    NullCheck.notNull(c, "Paragraph content");
    try {
      return c.paragraphContentAccept(new SParagraphContentVisitor<Node>() {
        @Override public Node visitFootnote(
          final SFootnote footnote)
          throws Exception
        {
          return SDocumentSerializer.footnote(footnote);
        }

        @Override public Node visitFormalItemList(
          final SFormalItemList list)
          throws Exception
        {
          return SDocumentSerializer.formalItemList(list);
        }

        @Override public Node visitImage(
          final SImage image)
          throws Exception
        {
          return SDocumentSerializer.image(image);
        }

        @Override public Node visitLink(
          final SLink link)
          throws Exception
        {
          return SDocumentSerializer.link(link);
        }

        @Override public Node visitLinkExternal(
          final SLinkExternal link)
          throws Exception
        {
          return SDocumentSerializer.linkExternal(link);
        }

        @Override public Node visitListOrdered(
          final SListOrdered list)
          throws Exception
        {
          return SDocumentSerializer.listOrdered(list);
        }

        @Override public Node visitListUnordered(
          final SListUnordered list)
          throws Exception
        {
          return SDocumentSerializer.listUnordered(list);
        }

        @Override public Node visitTable(
          final STable table)
          throws Exception
        {
          return SDocumentSerializer.table(table);
        }

        @Override public Node visitTerm(
          final STerm term)
          throws Exception
        {
          return SDocumentSerializer.term(term);
        }

        @Override public Node visitText(
          final SText text)
          throws Exception
        {
          return SDocumentSerializer.text(text);
        }

        @Override public Node visitVerbatim(
          final SVerbatim text)
          throws Exception
        {
          return SDocumentSerializer.verbatim(text);
        }
      });
    } catch (final Exception e) {
      throw new UnreachableCodeException(e);
    }
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param p
   *          The element
   * @return An XML element
   */

  public static Element part(
    final SPart p)
  {
    NullCheck.notNull(p, "Part");

    final Element e = new Element("s:part", SXML.XML_URI.toString());
    p.getID().map(new IDAdder(e));
    p.getType().map(new TypeAdder(e));

    e.appendChild(SDocumentSerializer.partTitle(p.getTitle()));

    p.getContents().map(new FunctionType<SPartContents, Unit>() {
      @Override public Unit call(
        final SPartContents x)
      {
        e.appendChild(SDocumentSerializer.partContents(x));
        return Unit.unit();
      }
    });

    for (final SSection c : p.getSections().getElements()) {
      e.appendChild(SDocumentSerializer.section(c));
    }

    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element partContents(
    final SPartContents s)
  {
    NullCheck.notNull(s, "Contents");

    final Element e = new Element("s:part-contents", SXML.XML_URI.toString());
    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element partTitle(
    final SPartTitle s)
  {
    NullCheck.notNull(s, "Title");

    final Element e = new Element("s:part-title", SXML.XML_URI.toString());
    e.appendChild(s.getActual().toString());
    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element section(
    final SSection s)
  {
    NullCheck.notNull(s, "Section");

    try {
      return s.sectionAccept(new SSectionVisitor<Element>() {
        @Override public Element visitSectionWithParagraphs(
          final SSectionWithParagraphs ss)
          throws Exception
        {
          return SDocumentSerializer.sectionWithParagraphs(ss);
        }

        @Override public Element visitSectionWithSubsections(
          final SSectionWithSubsections ss)
          throws Exception
        {
          return SDocumentSerializer.sectionWithSubsections(ss);
        }
      });
    } catch (final Exception e) {
      throw new UnreachableCodeException(e);
    }
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element sectionContents(
    final SSectionContents s)
  {
    NullCheck.notNull(s, "Section contents");
    final Element e =
      new Element("s:section-contents", SXML.XML_URI.toString());
    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element sectionTitle(
    final SSectionTitle s)
  {
    NullCheck.notNull(s, "Section title");
    final Element e = new Element("s:section-title", SXML.XML_URI.toString());
    e.appendChild(s.getActual().toString());
    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element sectionWithParagraphs(
    final SSectionWithParagraphs s)
  {
    NullCheck.notNull(s, "Section");

    final Element e = new Element("s:section", SXML.XML_URI.toString());
    s.getID().map(new IDAdder(e));
    s.getType().map(new TypeAdder(e));

    e.appendChild(SDocumentSerializer.sectionTitle(s.getTitle()));

    s.getContents().map(new FunctionType<SSectionContents, Unit>() {
      @Override public Unit call(
        final SSectionContents x)
      {
        e.appendChild(SDocumentSerializer.sectionContents(x));
        return Unit.unit();
      }
    });

    for (final SSubsectionContent c : s.getSectionContent().getElements()) {
      e.appendChild(SDocumentSerializer.subsectionContent(c));
    }

    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element sectionWithSubsections(
    final SSectionWithSubsections s)
  {
    NullCheck.notNull(s, "Section");

    final Element e = new Element("s:section", SXML.XML_URI.toString());
    s.getID().map(new IDAdder(e));
    s.getType().map(new TypeAdder(e));

    e.appendChild(SDocumentSerializer.sectionTitle(s.getTitle()));

    s.getContents().map(new FunctionType<SSectionContents, Unit>() {
      @Override public Unit call(
        final SSectionContents x)
      {
        e.appendChild(SDocumentSerializer.sectionContents(x));
        return Unit.unit();
      }
    });

    for (final SSubsection c : s.getSubsections().getElements()) {
      e.appendChild(SDocumentSerializer.subsection(c));
    }

    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element subsection(
    final SSubsection s)
  {
    NullCheck.notNull(s, "Subsection");

    final Element e = new Element("s:subsection", SXML.XML_URI.toString());
    s.getID().map(new IDAdder(e));
    s.getType().map(new TypeAdder(e));

    e.appendChild(SDocumentSerializer.subsectionTitle(s.getTitle()));

    for (final SSubsectionContent c : s.getContent().getElements()) {
      e.appendChild(SDocumentSerializer.subsectionContent(c));
    }

    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param c
   *          The element
   * @return An XML element
   */

  public static Element subsectionContent(
    final SSubsectionContent c)
  {
    NullCheck.notNull(c, "Subsection content");

    try {
      return c
        .subsectionContentAccept(new SSubsectionContentVisitor<Element>() {
          @Override public Element visitFormalItem(
            final SFormalItem formal)
            throws Exception
          {
            return SDocumentSerializer.formalItem(formal);
          }

          @Override public Element visitParagraph(
            final SParagraph paragraph)
            throws Exception
          {
            return SDocumentSerializer.paragraph(paragraph);
          }
        });
    } catch (final Exception e) {
      throw new UnreachableCodeException(e);
    }
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element subsectionTitle(
    final SSubsectionTitle s)
  {
    NullCheck.notNull(s, "Subsection title");
    final Element e =
      new Element("s:subsection-title", SXML.XML_URI.toString());
    e.appendChild(s.getActual().toString());
    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element table(
    final STable s)
  {
    NullCheck.notNull(s, "Table");

    final Element e = new Element("s:table", SXML.XML_URI.toString());

    e.appendChild(SDocumentSerializer.tableSummary(s.getSummary()));
    s.getHeader().map(new FunctionType<STableHead, Unit>() {
      @Override public Unit call(
        final STableHead x)
      {
        e.appendChild(SDocumentSerializer.tableHead(x));
        return Unit.unit();
      }
    });
    e.appendChild(SDocumentSerializer.tableBody(s.getBody()));
    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element tableBody(
    final STableBody s)
  {
    NullCheck.notNull(s, "Table body");

    final Element e = new Element("s:table-body", SXML.XML_URI.toString());

    for (final STableRow r : s.getRows().getElements()) {
      e.appendChild(SDocumentSerializer.tableRow(r));
    }

    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param c
   *          The element
   * @return An XML element
   */

  public static Element tableCell(
    final STableCell c)
  {
    NullCheck.notNull(c, "Table cell");

    final Element e = new Element("s:table-cell", SXML.XML_URI.toString());

    for (final STableCellContent cc : c.getContent().getElements()) {
      e.appendChild(SDocumentSerializer.tableCellContent(cc));
    }

    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param cc
   *          The element
   * @return An XML element
   */

  public static Node tableCellContent(
    final STableCellContent cc)
  {
    NullCheck.notNull(cc, "Table cell content");

    try {
      return cc.tableCellContentAccept(new STableCellContentVisitor<Node>() {
        @Override public Node visitFootnote(
          final SFootnote footnote)
          throws Exception
        {
          return SDocumentSerializer.footnote(footnote);
        }

        @Override public Node visitImage(
          final SImage image)
          throws Exception
        {
          return SDocumentSerializer.image(image);
        }

        @Override public Node visitLink(
          final SLink link)
          throws Exception
        {
          return SDocumentSerializer.link(link);
        }

        @Override public Node visitLinkExternal(
          final SLinkExternal link)
          throws Exception
        {
          return SDocumentSerializer.linkExternal(link);
        }

        @Override public Node visitListOrdered(
          final SListOrdered list)
          throws Exception
        {
          return SDocumentSerializer.listOrdered(list);
        }

        @Override public Node visitListUnordered(
          final SListUnordered list)
          throws Exception
        {
          return SDocumentSerializer.listUnordered(list);
        }

        @Override public Node visitTerm(
          final STerm term)
          throws Exception
        {
          return SDocumentSerializer.term(term);
        }

        @Override public Node visitText(
          final SText text)
          throws Exception
        {
          return SDocumentSerializer.text(text);
        }

        @Override public Node visitVerbatim(
          final SVerbatim text)
          throws Exception
        {
          return SDocumentSerializer.verbatim(text);
        }
      });
    } catch (final Exception e) {
      throw new UnreachableCodeException(e);
    }
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element tableColumnName(
    final STableColumnName s)
  {
    NullCheck.notNull(s, "Table column");

    final Element e =
      new Element("s:table-column-name", SXML.XML_URI.toString());
    e.appendChild(s.getText());
    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element tableHead(
    final STableHead s)
  {
    NullCheck.notNull(s, "Table header");

    final Element e = new Element("s:table-head", SXML.XML_URI.toString());

    for (final STableColumnName cn : s.getHeader().getElements()) {
      e.appendChild(SDocumentSerializer.tableColumnName(cn));
    }
    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param r
   *          The element
   * @return An XML element
   */

  public static Element tableRow(
    final STableRow r)
  {
    NullCheck.notNull(r, "Table row");

    final Element e = new Element("s:table-row", SXML.XML_URI.toString());

    for (final STableCell c : r.getColumns().getElements()) {
      e.appendChild(SDocumentSerializer.tableCell(c));
    }

    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element tableSummary(
    final STableSummary s)
  {
    NullCheck.notNull(s, "Table summary");

    final Element e = new Element("s:table-summary", SXML.XML_URI.toString());
    e.appendChild(s.getText());
    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element term(
    final STerm s)
  {
    NullCheck.notNull(s, "Term");
    final Element e = new Element("s:term", SXML.XML_URI.toString());
    s.getType().map(new TypeAdder(e));
    e.appendChild(s.getText().getText());
    return e;
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Text text(
    final SText s)
  {
    NullCheck.notNull(s, "Text");
    return new Text(s.getText());
  }

  /**
   * Serialize the given element to XML.
   * 
   * @param s
   *          The element
   * @return An XML element
   */

  public static Element verbatim(
    final SVerbatim s)
  {
    NullCheck.notNull(s, "Verbatim");
    final Element e = new Element("s:verbatim", SXML.XML_URI.toString());
    s.getType().map(new TypeAdder(e));
    e.appendChild(s.getText());
    return e;
  }

  private SDocumentSerializer()
  {

  }
}
