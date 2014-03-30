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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import javax.annotation.Nonnull;

import nu.xom.Attribute;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Text;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
import com.io7m.jaux.functional.Function;
import com.io7m.jaux.functional.Option;
import com.io7m.jaux.functional.PartialFunction;
import com.io7m.jaux.functional.Unit;
import com.io7m.jstructural.annotated.SADocument;
import com.io7m.jstructural.annotated.SAFootnote;
import com.io7m.jstructural.annotated.SAFootnoteContent;
import com.io7m.jstructural.annotated.SAFootnoteContentVisitor;
import com.io7m.jstructural.annotated.SAFormalItem;
import com.io7m.jstructural.annotated.SAFormalItemContent;
import com.io7m.jstructural.annotated.SAFormalItemContentVisitor;
import com.io7m.jstructural.annotated.SAFormalItemList;
import com.io7m.jstructural.annotated.SAFormalItemNumber;
import com.io7m.jstructural.annotated.SAFormalItemsByKindReadable;
import com.io7m.jstructural.annotated.SAID;
import com.io7m.jstructural.annotated.SAImage;
import com.io7m.jstructural.annotated.SALink;
import com.io7m.jstructural.annotated.SALinkContent;
import com.io7m.jstructural.annotated.SALinkContentVisitor;
import com.io7m.jstructural.annotated.SALinkExternal;
import com.io7m.jstructural.annotated.SAListItem;
import com.io7m.jstructural.annotated.SAListItemContent;
import com.io7m.jstructural.annotated.SAListItemContentVisitor;
import com.io7m.jstructural.annotated.SAListOrdered;
import com.io7m.jstructural.annotated.SAListUnordered;
import com.io7m.jstructural.annotated.SAParagraph;
import com.io7m.jstructural.annotated.SAParagraphContent;
import com.io7m.jstructural.annotated.SAParagraphContentVisitor;
import com.io7m.jstructural.annotated.SAParagraphNumber;
import com.io7m.jstructural.annotated.SAPartNumber;
import com.io7m.jstructural.annotated.SAPartTitle;
import com.io7m.jstructural.annotated.SASection;
import com.io7m.jstructural.annotated.SASectionTitle;
import com.io7m.jstructural.annotated.SASubsection;
import com.io7m.jstructural.annotated.SASubsectionContent;
import com.io7m.jstructural.annotated.SASubsectionContentVisitor;
import com.io7m.jstructural.annotated.SASubsectionTitle;
import com.io7m.jstructural.annotated.SATable;
import com.io7m.jstructural.annotated.SATableCell;
import com.io7m.jstructural.annotated.SATableCellContent;
import com.io7m.jstructural.annotated.SATableCellContentVisitor;
import com.io7m.jstructural.annotated.SATableColumnName;
import com.io7m.jstructural.annotated.SATableHead;
import com.io7m.jstructural.annotated.SATableRow;
import com.io7m.jstructural.annotated.SATerm;
import com.io7m.jstructural.annotated.SAText;
import com.io7m.jstructural.annotated.SAVerbatim;
import com.io7m.jstructural.core.SDocumentStyle;
import com.io7m.jstructural.core.SNonEmptyList;

/**
 * XHTML utility functions.
 */

public final class SXHTML
{
  static final @Nonnull String         ATTRIBUTE_PREFIX;
  static final @Nonnull String         FOOTNOTE_CODE;
  static final @Nonnull String         FORMAL_CODE;
  static final @Nonnull Option<String> NO_TYPE;
  static final @Nonnull String         OUTPUT_FILE_SUFFIX;
  static final @Nonnull String         PARAGRAPH_CODE;
  static final @Nonnull String         PART_CODE;
  static final @Nonnull String         SECTION_CODE;
  static final @Nonnull String         SUBSECTION_CODE;

  static final @Nonnull URI            XHTML_URI;

  static {
    try {
      XHTML_URI = new URI("http://www.w3.org/1999/xhtml");
    } catch (final URISyntaxException e) {
      throw new UnreachableCodeException(e);
    }

    ATTRIBUTE_PREFIX = "st200";
    SECTION_CODE = "s";
    SUBSECTION_CODE = "ss";
    PART_CODE = "p";
    PARAGRAPH_CODE = "pg";
    FOOTNOTE_CODE = "fn";
    FORMAL_CODE = "fo";
    NO_TYPE = Option.none();
  }

  static {
    OUTPUT_FILE_SUFFIX = "xhtml";
  }

  static @Nonnull Element body()
  {
    final Element body = new Element("body", SXHTML.XHTML_URI.toString());
    return body;
  }

  static @Nonnull Element bodyContainer()
  {
    final Element e = new Element("div", SXHTML.XHTML_URI.toString());
    e.addAttribute(new Attribute("class", null, SXHTML.cssName("body")));
    return e;
  }

  static @Nonnull String cssName(
    final @Nonnull String name)
  {
    final StringBuilder b = new StringBuilder();
    b.append(SXHTML.ATTRIBUTE_PREFIX);
    b.append("_");
    b.append(name);
    return b.toString();
  }

  static @Nonnull Element documentTitle(
    final @Nonnull SADocument doc)
  {
    final String[] classes = new String[1];
    classes[0] = "document_title";
    final Element et =
      SXHTML.elementWithClasses("div", SXHTML.NO_TYPE, classes);
    et.appendChild(doc.getTitle().getActual());
    return et;
  }

  static @Nonnull Element elementWithClasses(
    final @Nonnull String name,
    final @Nonnull Option<String> type,
    final @Nonnull String[] classes)
  {
    final StringBuilder cs = new StringBuilder();
    for (int index = 0; index < classes.length; ++index) {
      cs.append(SXHTML.cssName(classes[index]));
      if ((index + 1) < classes.length) {
        cs.append(" ");
      }
    }

    type.map(new Function<String, Unit>() {
      @Override public Unit call(
        final @Nonnull String x)
      {
        cs.append(" ");
        cs.append(x);
        return Unit.unit();
      }
    });

    final Element e = new Element(name, SXHTML.XHTML_URI.toString());
    e.addAttribute(new Attribute("class", null, cs.toString()));
    return e;
  }

  static @Nonnull Element footnoteBody(
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull SAFormalItemsByKindReadable formals,
    final @Nonnull SAFootnote f)
    throws ConstraintError,
      Exception
  {
    final Element e = SXHTML.footnoteContainer(link_provider, formals, f);
    final String[] classes = new String[1];
    classes[0] = "footnote_body";
    final Element ea =
      SXHTML.elementWithClasses("div", SXHTML.NO_TYPE, classes);

    final SNonEmptyList<Node> eac =
      SXHTML.footnoteContentList(link_provider, formals, f.getContent());
    for (final Node x : eac.getElements()) {
      ea.appendChild(x);
    }

    e.appendChild(ea);
    return e;
  }

  static @Nonnull Element footnoteContainer(
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull SAFormalItemsByKindReadable formals,
    final @Nonnull SAFootnote f)
  {
    final String[] sect_classes = new String[1];
    sect_classes[0] = "footnote_container";
    final Element e =
      SXHTML.elementWithClasses("div", SXHTML.NO_TYPE, sect_classes);

    {
      final String[] classes = new String[1];
      classes[0] = "footnote_number";
      final Element epn =
        SXHTML.elementWithClasses("div", SXHTML.NO_TYPE, classes);

      final String id = SXHTML.getFootnoteCode(f);
      final Element elink = SXHTML.linkRawIDTarget("#" + id + "_ref", id);
      elink.appendChild(Integer.toString(f.getNumber()));

      epn.appendChild("[");
      epn.appendChild(elink);
      epn.appendChild("]");

      e.appendChild(epn);
    }

    return e;
  }

  static @Nonnull Node footnoteContent(
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull SAFormalItemsByKindReadable formals,
    final @Nonnull SAFootnoteContent c)
    throws ConstraintError,
      Exception
  {
    return c.footnoteContentAccept(new SAFootnoteContentVisitor<Node>() {
      @Override public Node visitFootnote(
        final @Nonnull SAFootnote footnote)
        throws ConstraintError,
          Exception
      {
        return SXHTML.footnoteReference(footnote);
      }

      @Override public Node visitImage(
        final @Nonnull SAImage image)
        throws ConstraintError,
          Exception
      {
        return SXHTML.image(image);
      }

      @Override public Node visitLink(
        final @Nonnull SALink link)
        throws ConstraintError,
          Exception
      {
        return SXHTML.link(link_provider, link);
      }

      @Override public Node visitLinkExternal(
        final @Nonnull SALinkExternal link)
        throws ConstraintError,
          Exception
      {
        return SXHTML.linkExternal(link);
      }

      @Override public Node visitListOrdered(
        final @Nonnull SAListOrdered list)
        throws ConstraintError,
          Exception
      {
        return SXHTML.listOrdered(link_provider, list);
      }

      @Override public Node visitListUnordered(
        final @Nonnull SAListUnordered list)
        throws ConstraintError,
          Exception
      {
        return SXHTML.listUnordered(link_provider, list);
      }

      @Override public Node visitTerm(
        final @Nonnull SATerm term)
        throws ConstraintError,
          Exception
      {
        return SXHTML.term(term);
      }

      @Override public Node visitText(
        final @Nonnull SAText text)
        throws ConstraintError,
          Exception
      {
        return SXHTML.text(text);
      }

      @Override public Node visitVerbatim(
        final @Nonnull SAVerbatim text)
        throws ConstraintError,
          Exception
      {
        return SXHTML.verbatim(text);
      }
    });
  }

  static @Nonnull SNonEmptyList<Node> footnoteContentList(
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull SAFormalItemsByKindReadable formals,
    final @Nonnull SNonEmptyList<SAFootnoteContent> contents)
    throws ConstraintError,
      Exception
  {
    final List<Node> nodes = new ArrayList<Node>();
    for (final SAFootnoteContent c : contents.getElements()) {
      nodes.add(SXHTML.footnoteContent(link_provider, formals, c));
    }
    return SNonEmptyList.newList(nodes);
  }

  static @Nonnull Element footnoteReference(
    final @Nonnull SAFootnote footnote)
  {
    final String[] classes = new String[1];
    classes[0] = "footnote_reference";

    final String id = SXHTML.getFootnoteCode(footnote);
    final Element e =
      SXHTML.elementWithClasses("span", SXHTML.NO_TYPE, classes);

    final Element elink = SXHTML.linkRawIDTarget("#" + id, id + "_ref");
    elink.appendChild("[" + Integer.toString(footnote.getNumber()) + "]");
    e.appendChild(elink);
    return e;
  }

  static void footnotes(
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull SAFormalItemsByKindReadable formals,
    final @Nonnull SADocument doc,
    final @Nonnull Element body)
    throws ConstraintError,
      Exception
  {
    final List<SAFootnote> footnotes = doc.getFootnotes();
    if (footnotes.size() > 0) {
      final String[] classes = new String[1];
      classes[0] = "footnotes";
      final Element e =
        SXHTML.elementWithClasses("div", SXHTML.NO_TYPE, classes);
      e.appendChild(new Element("hr", SXHTML.XHTML_URI.toString()));

      for (final SAFootnote f : footnotes) {
        e.appendChild(SXHTML.footnoteBody(link_provider, formals, f));
      }

      body.appendChild(e);
    }
  }

  static @Nonnull Element formalItem(
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull SAFormalItemsByKindReadable formals,
    final @Nonnull SAFormalItem formal)
    throws ConstraintError,
      Exception
  {
    final String[] classes = new String[1];
    classes[0] = "formal_item";
    final Element e =
      SXHTML.elementWithClasses("div", formal.getType(), classes);

    {
      final String[] et_classes = new String[1];
      et_classes[0] = "formal_item_title";

      final Element et =
        SXHTML.elementWithClasses("div", formal.getType(), et_classes);

      final String id =
        SXHTMLAnchors.getFormalItemAnchorID(formal.getNumber());
      final Element elink = SXHTML.linkRawIDTarget("#" + id, id);
      final StringBuilder title = new StringBuilder();
      title.append(formal.getNumber().formalItemNumberFormat());
      title.append(". ");
      title.append(formal.getTitle().getActual());
      elink.appendChild(title.toString());
      et.appendChild(elink);
      e.appendChild(et);
    }

    e.appendChild(SXHTML.formalItemContent(
      link_provider,
      formals,
      formal.getContent()));
    return e;
  }

  static @Nonnull Element formalItemContent(
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull SAFormalItemsByKindReadable formals,
    final @Nonnull SAFormalItemContent content)
    throws ConstraintError,
      Exception
  {
    return content
      .formalItemContentAccept(new SAFormalItemContentVisitor<Element>() {
        @Override public Element visitFormalItemList(
          final SAFormalItemList list)
          throws ConstraintError,
            Exception
        {
          return SXHTML.formalItemList(link_provider, formals, list);
        }

        @Override public Element visitImage(
          final SAImage image)
          throws ConstraintError,
            Exception
        {
          return SXHTML.image(image);
        }

        @Override public Element visitListOrdered(
          final SAListOrdered list)
          throws ConstraintError,
            Exception
        {
          return SXHTML.listOrdered(link_provider, list);
        }

        @Override public Element visitListUnordered(
          final SAListUnordered list)
          throws ConstraintError,
            Exception
        {
          return SXHTML.listUnordered(link_provider, list);
        }

        @Override public Element visitTable(
          final SATable e)
          throws ConstraintError,
            Exception
        {
          return SXHTML.table(link_provider, e);
        }

        @Override public Element visitVerbatim(
          final SAVerbatim text)
          throws ConstraintError,
            Exception
        {
          return SXHTML.verbatim(text);
        }
      });
  }

  static @Nonnull Element formalItemList(
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull SAFormalItemsByKindReadable formals,
    final @Nonnull SAFormalItemList list)
    throws ConstraintError
  {
    final String[] classes = new String[1];
    classes[0] = "formal_item_list";
    final Element e =
      SXHTML.elementWithClasses("ul", SXHTML.NO_TYPE, classes);

    final SortedMap<SAFormalItemNumber, SAFormalItem> f =
      formals.get(list.getKind());

    for (final SAFormalItemNumber p : f.keySet()) {
      final SAFormalItem formal = f.get(p);

      final String[] el_classes = new String[1];
      el_classes[0] = "formal_item_list_item";
      final Element el =
        SXHTML.elementWithClasses("li", SXHTML.NO_TYPE, el_classes);

      final Element elink =
        SXHTML.linkRaw(link_provider.getFormalItemLinkTarget(formal
          .getNumber()));
      final StringBuilder title = new StringBuilder();
      title.append(formal.getNumber().formalItemNumberFormat());
      title.append(". ");
      title.append(formal.getTitle().getActual());
      elink.appendChild(title.toString());

      el.appendChild(elink);
      e.appendChild(el);
    }

    return e;
  }

  static String getFootnoteCode(
    final SAFootnote footnote)
  {
    final StringBuilder idb = new StringBuilder();
    idb.append(SXHTML.ATTRIBUTE_PREFIX);
    idb.append("_");
    idb.append(SXHTML.FOOTNOTE_CODE);
    idb.append("_");
    idb.append(footnote.getNumber());

    final String id = idb.toString();
    return id;
  }

  static @Nonnull String getPartAnchorID(
    final @Nonnull SAPartNumber part)
  {
    final StringBuilder b = new StringBuilder();
    b.append(SXHTML.ATTRIBUTE_PREFIX);
    b.append("_p");
    b.append(part.getActual());
    return b.toString();
  }

  static @Nonnull Element head(
    final @Nonnull String title,
    final @Nonnull Option<SDocumentStyle> style)
  {
    final Element e = new Element("head", SXHTML.XHTML_URI.toString());
    final Element e_title = new Element("title", SXHTML.XHTML_URI.toString());
    e_title.appendChild(title);
    e.appendChild(e_title);
    e.appendChild(SXHTML.stylesheetLink("jstructural-2_0_0-layout.css"));
    e.appendChild(SXHTML.stylesheetLink("jstructural-2_0_0-colour.css"));

    style.map(new Function<SDocumentStyle, Unit>() {
      @Override public Unit call(
        final SDocumentStyle x)
      {
        e.appendChild(SXHTML.stylesheetLink(x.getActual().toString()));
        return Unit.unit();
      }
    });

    return e;
  }

  static @Nonnull Element image(
    final @Nonnull SAImage image)
    throws ConstraintError
  {
    final String[] classes = new String[1];
    classes[0] = "image";

    final Element e =
      SXHTML.elementWithClasses("img", image.getType(), classes);

    image.getHeight().mapPartial(
      new PartialFunction<Integer, Unit, ConstraintError>() {
        @Override public Unit call(
          final Integer x)
          throws ConstraintError
        {
          e.addAttribute(new Attribute("height", null, x.toString()));
          return Unit.unit();
        }
      });

    image.getWidth().mapPartial(
      new PartialFunction<Integer, Unit, ConstraintError>() {
        @Override public Unit call(
          final Integer x)
          throws ConstraintError
        {
          e.addAttribute(new Attribute("width", null, x.toString()));
          return Unit.unit();
        }
      });

    e.addAttribute(new Attribute("alt", null, image.getText()));
    e.addAttribute(new Attribute("src", null, image.getURI().toString()));
    return e;
  }

  static @Nonnull Element link(
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull SALink link)
    throws ConstraintError,
      Exception
  {
    final String[] classes = new String[1];
    classes[0] = "link";
    final SAID id = new SAID(link.getTarget());
    final Element e =
      SXHTML.linkRawWithClasses(
        link_provider.getLinkTargetForID(id),
        SXHTML.NO_TYPE,
        classes);

    final SNonEmptyList<Node> cs =
      SXHTML.linkTargetContentList(link.getContent());
    for (final Node c : cs.getElements()) {
      e.appendChild(c);
    }
    return e;
  }

  static @Nonnull Node linkContent(
    final @Nonnull SALinkContent c)
    throws ConstraintError,
      Exception
  {
    return c.linkContentAccept(new SALinkContentVisitor<Node>() {
      @Override public Node visitImage(
        final @Nonnull SAImage image)
        throws ConstraintError,
          Exception
      {
        return SXHTML.image(image);
      }

      @Override public Node visitText(
        final @Nonnull SAText text)
        throws ConstraintError,
          Exception
      {
        return SXHTML.text(text);
      }
    });
  }

  static @Nonnull Node linkExternal(
    final @Nonnull SALinkExternal link)
    throws ConstraintError,
      Exception
  {
    final String[] classes = new String[1];
    classes[0] = "link_external";
    final Element e =
      SXHTML.linkRawWithClasses(
        link.getTarget().toString(),
        SXHTML.NO_TYPE,
        classes);

    final SNonEmptyList<Node> nodes =
      SXHTML.linkTargetContentList(link.getContent());
    for (final Node n : nodes.getElements()) {
      e.appendChild(n);
    }

    return e;
  }

  static @Nonnull Element linkRaw(
    final @Nonnull String target)
  {
    final Element e = new Element("a", SXHTML.XHTML_URI.toString());
    e.addAttribute(new Attribute("href", null, target));
    return e;
  }

  static @Nonnull Element linkRawIDTarget(
    final @Nonnull String target,
    final @Nonnull String id)
  {
    final Element e = new Element("a", SXHTML.XHTML_URI.toString());
    e.addAttribute(new Attribute("id", null, id));
    e.addAttribute(new Attribute("href", null, target));
    return e;
  }

  static @Nonnull Element linkRawIDWithClasses(
    final @Nonnull String id,
    final @Nonnull String target,
    final @Nonnull Option<String> type,
    final @Nonnull String[] classes)
  {
    final Element e = SXHTML.elementWithClasses("a", type, classes);
    e.addAttribute(new Attribute("href", null, target));
    e.addAttribute(new Attribute("id", null, id));
    return e;
  }

  static @Nonnull Element linkRawWithClasses(
    final @Nonnull String target,
    final @Nonnull Option<String> type,
    final @Nonnull String[] classes)
  {
    final Element e = SXHTML.elementWithClasses("a", type, classes);
    e.addAttribute(new Attribute("href", null, target));
    return e;
  }

  static @Nonnull SNonEmptyList<Node> linkTargetContentList(
    final @Nonnull SNonEmptyList<SALinkContent> contents)
    throws ConstraintError,
      Exception
  {
    final List<Node> nodes = new ArrayList<Node>();
    for (final SALinkContent c : contents.getElements()) {
      nodes.add(SXHTML.linkContent(c));
    }
    return SNonEmptyList.newList(nodes);
  }

  static @Nonnull Element listItem(
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull SAListItem li)
    throws ConstraintError,
      Exception
  {
    final Element e = new Element("li", SXHTML.XHTML_URI.toString());
    e.addAttribute(new Attribute("class", null, SXHTML.cssName("list_item")));

    final SNonEmptyList<Node> contents =
      SXHTML.listItemContents(link_provider, li.getContent());
    for (final Node n : contents.getElements()) {
      e.appendChild(n);
    }

    return e;
  }

  static @Nonnull Node listItemContent(
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull SAListItemContent c)
    throws ConstraintError,
      Exception
  {
    return c.listItemContentAccept(new SAListItemContentVisitor<Node>() {
      @Override public Node visitFootnote(
        final @Nonnull SAFootnote footnote)
        throws ConstraintError,
          Exception
      {
        return SXHTML.footnoteReference(footnote);
      }

      @Override public Node visitImage(
        final @Nonnull SAImage image)
        throws ConstraintError,
          Exception
      {
        return SXHTML.image(image);
      }

      @Override public Node visitLink(
        final @Nonnull SALink link)
        throws ConstraintError,
          Exception
      {
        return SXHTML.link(link_provider, link);
      }

      @Override public Node visitLinkExternal(
        final @Nonnull SALinkExternal link)
        throws ConstraintError,
          Exception
      {
        return SXHTML.linkExternal(link);
      }

      @Override public Node visitListOrdered(
        final @Nonnull SAListOrdered list)
        throws ConstraintError,
          Exception
      {
        return SXHTML.listOrdered(link_provider, list);
      }

      @Override public Node visitListUnordered(
        final @Nonnull SAListUnordered list)
        throws ConstraintError,
          Exception
      {
        return SXHTML.listUnordered(link_provider, list);
      }

      @Override public Node visitTerm(
        final @Nonnull SATerm term)
        throws ConstraintError,
          Exception
      {
        return SXHTML.term(term);
      }

      @Override public Node visitText(
        final @Nonnull SAText text)
        throws ConstraintError,
          Exception
      {
        return SXHTML.text(text);
      }

      @Override public Node visitVerbatim(
        final @Nonnull SAVerbatim text)
        throws ConstraintError,
          Exception
      {
        return SXHTML.verbatim(text);
      }
    });
  }

  static @Nonnull SNonEmptyList<Node> listItemContents(
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull SNonEmptyList<SAListItemContent> contents)
    throws ConstraintError,
      Exception
  {
    final List<Node> nodes = new ArrayList<Node>();
    for (final SAListItemContent c : contents.getElements()) {
      nodes.add(SXHTML.listItemContent(link_provider, c));
    }
    return SNonEmptyList.newList(nodes);
  }

  static @Nonnull Element listOrdered(
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull SAListOrdered list)
    throws ConstraintError,
      Exception
  {
    final Element e = new Element("ol", SXHTML.XHTML_URI.toString());
    e.addAttribute(new Attribute("class", null, SXHTML
      .cssName("list_ordered")));

    for (final SAListItem li : list.getItems().getElements()) {
      e.appendChild(SXHTML.listItem(link_provider, li));
    }

    return e;
  }

  static @Nonnull Element listUnordered(
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull SAListUnordered list)
    throws ConstraintError,
      Exception
  {
    final Element e = new Element("ul", SXHTML.XHTML_URI.toString());
    e.addAttribute(new Attribute("class", null, SXHTML
      .cssName("list_unordered")));

    for (final SAListItem li : list.getItems().getElements()) {
      e.appendChild(SXHTML.listItem(link_provider, li));
    }

    return e;
  }

  static @Nonnull Document newDocument()
  {
    final DocType dt =
      new DocType(
        "html",
        "-//W3C//DTD XHTML 1.0 Strict//EN",
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");
    final Element root = new Element("html", SXHTML.XHTML_URI.toString());

    final Document d = new Document(root);
    d.setDocType(dt);
    return d;
  }

  static @Nonnull SXHTMLPage newPage(
    final @Nonnull String title,
    final @Nonnull Option<SDocumentStyle> style)
  {
    final Document doc = SXHTML.newDocument();
    final Element root = doc.getRootElement();
    final Element in_head = SXHTML.head(title, style);
    final Element in_body = SXHTML.body();
    final Element in_body_container = SXHTML.bodyContainer();
    root.appendChild(in_head);
    root.appendChild(in_body);
    in_body.appendChild(in_body_container);
    return new SXHTMLPage(doc, in_head, in_body, in_body_container);
  }

  static @Nonnull Element paragraph(
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull SAFormalItemsByKindReadable formals,
    final @Nonnull SAParagraph paragraph)
    throws ConstraintError,
      Exception
  {
    final Element e =
      SXHTML.paragraphContainer(paragraph.getType(), paragraph.getNumber());
    final String[] classes = new String[1];
    classes[0] = "paragraph";
    final Element ea =
      SXHTML.elementWithClasses("div", paragraph.getType(), classes);

    final SNonEmptyList<Node> eac =
      SXHTML.paragraphContentList(
        link_provider,
        formals,
        paragraph.getContent());
    for (final Node x : eac.getElements()) {
      ea.appendChild(x);
    }

    e.appendChild(ea);
    return e;
  }

  static @Nonnull Element paragraphContainer(
    final @Nonnull Option<String> type,
    final @Nonnull SAParagraphNumber number)
    throws ConstraintError,
      Exception
  {
    final String[] sect_classes = new String[1];
    sect_classes[0] = "paragraph_container";
    final Element e = SXHTML.elementWithClasses("div", type, sect_classes);

    {
      final String[] classes = new String[1];
      classes[0] = "paragraph_number";
      final Element epn = SXHTML.elementWithClasses("div", type, classes);
      final String id = SXHTMLAnchors.getParagraphAnchorID(number);
      final Element elink = SXHTML.linkRawIDTarget("#" + id, id);
      elink.appendChild(Integer.toString(number.getParagraph()));
      epn.appendChild(elink);
      e.appendChild(epn);
    }

    return e;
  }

  static @Nonnull Node paragraphContent(
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull SAFormalItemsByKindReadable formals,
    final @Nonnull SAParagraphContent c)
    throws ConstraintError,
      Exception
  {
    return c.paragraphContentAccept(new SAParagraphContentVisitor<Node>() {
      @Override public Node visitFootnote(
        final @Nonnull SAFootnote footnote)
        throws ConstraintError,
          Exception
      {
        return SXHTML.footnoteReference(footnote);
      }

      @Override public Node visitFormalItemList(
        final @Nonnull SAFormalItemList list)
        throws ConstraintError,
          Exception
      {
        return SXHTML.formalItemList(link_provider, formals, list);
      }

      @Override public Node visitImage(
        final @Nonnull SAImage image)
        throws ConstraintError,
          Exception
      {
        return SXHTML.image(image);
      }

      @Override public Node visitLink(
        final @Nonnull SALink link)
        throws ConstraintError,
          Exception
      {
        return SXHTML.link(link_provider, link);
      }

      @Override public Node visitLinkExternal(
        final @Nonnull SALinkExternal link)
        throws ConstraintError,
          Exception
      {
        return SXHTML.linkExternal(link);
      }

      @Override public Node visitListOrdered(
        final @Nonnull SAListOrdered list)
        throws ConstraintError,
          Exception
      {
        return SXHTML.listOrdered(link_provider, list);
      }

      @Override public Node visitListUnordered(
        final @Nonnull SAListUnordered list)
        throws ConstraintError,
          Exception
      {
        return SXHTML.listUnordered(link_provider, list);
      }

      @Override public Node visitTable(
        final @Nonnull SATable table)
        throws ConstraintError,
          Exception
      {
        return SXHTML.table(link_provider, table);
      }

      @Override public Node visitTerm(
        final @Nonnull SATerm term)
        throws ConstraintError,
          Exception
      {
        return SXHTML.term(term);
      }

      @Override public Node visitText(
        final @Nonnull SAText text)
        throws ConstraintError,
          Exception
      {
        return SXHTML.text(text);
      }

      @Override public Node visitVerbatim(
        final @Nonnull SAVerbatim text)
        throws ConstraintError,
          Exception
      {
        return SXHTML.verbatim(text);
      }
    });
  }

  static @Nonnull SNonEmptyList<Node> paragraphContentList(
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull SAFormalItemsByKindReadable formals,
    final @Nonnull SNonEmptyList<SAParagraphContent> contents)
    throws ConstraintError,
      Exception
  {
    final List<Node> nodes = new ArrayList<Node>();
    for (final SAParagraphContent c : contents.getElements()) {
      nodes.add(SXHTML.paragraphContent(link_provider, formals, c));
    }
    return SNonEmptyList.newList(nodes);
  }

  static @Nonnull Element partContainer(
    final @Nonnull SAPartTitle title)
  {
    final Element e = new Element("div", SXHTML.XHTML_URI.toString());
    e.addAttribute(new Attribute("class", null, SXHTML
      .cssName("part_container")));

    {
      final String[] classes = new String[1];
      classes[0] = "part_title_number";
      final Element epn =
        SXHTML.elementWithClasses("div", SXHTML.NO_TYPE, classes);
      final String id = SXHTML.getPartAnchorID(title.getNumber());
      final String text = Integer.toString(title.getNumber().getActual());
      final Element elink = SXHTML.linkRawIDTarget("#" + id, id);
      elink.appendChild(text);
      epn.appendChild(elink);
      e.appendChild(epn);
    }

    {
      final String[] classes = new String[1];
      classes[0] = "part_title";
      final Element epn =
        SXHTML.elementWithClasses("div", SXHTML.NO_TYPE, classes);
      epn.appendChild(title.getActual());
      e.appendChild(epn);
    }

    return e;
  }

  static @Nonnull Element sectionContainer(
    final @Nonnull SASection section)
    throws ConstraintError,
      Exception
  {
    final SASectionTitle title = section.getTitle();

    final String[] sect_classes = new String[1];
    sect_classes[0] = "section_container";
    final Element e =
      SXHTML.elementWithClasses("div", section.getType(), sect_classes);

    {
      final String[] classes = new String[1];
      classes[0] = "section_title_number";
      final Element epn =
        SXHTML.elementWithClasses("div", SXHTML.NO_TYPE, classes);
      final String id = SXHTMLAnchors.getSectionAnchorID(title.getNumber());
      final String text = title.getNumber().sectionNumberFormat();
      final Element elink = SXHTML.linkRawIDTarget("#" + id, id);
      elink.appendChild(text);
      epn.appendChild(elink);
      e.appendChild(epn);
    }

    {
      final String[] classes = new String[1];
      classes[0] = "section_title";
      final Element epn =
        SXHTML.elementWithClasses("div", SXHTML.NO_TYPE, classes);
      epn.appendChild(title.getActual());
      e.appendChild(epn);
    }

    return e;
  }

  static @Nonnull Element stylesheetLink(
    final @Nonnull String uri)
  {
    final Element e = new Element("link", SXHTML.XHTML_URI.toString());
    e.addAttribute(new Attribute("rel", null, "stylesheet"));
    e.addAttribute(new Attribute("type", null, "text/css"));
    e.addAttribute(new Attribute("href", null, uri));
    return e;
  }

  static @Nonnull Element subsection(
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull SAFormalItemsByKindReadable formals,
    final @Nonnull SASubsection ss)
    throws ConstraintError,
      Exception
  {
    final Element e = SXHTML.subsectionContainer(ss);

    final SNonEmptyList<SASubsectionContent> contents = ss.getContent();
    for (final SASubsectionContent c : contents.getElements()) {
      e.appendChild(SXHTML.subsectionContent(link_provider, formals, c));
    }

    return e;
  }

  static @Nonnull Element subsectionContainer(
    final @Nonnull SASubsection ss)
    throws ConstraintError,
      Exception
  {
    final SASubsectionTitle title = ss.getTitle();

    final String[] sect_classes = new String[1];
    sect_classes[0] = "subsection_container";
    final Element e =
      SXHTML.elementWithClasses("div", ss.getType(), sect_classes);

    {
      final String[] classes = new String[1];
      classes[0] = "subsection_title_number";
      final Element epn =
        SXHTML.elementWithClasses("div", SXHTML.NO_TYPE, classes);
      final String id =
        SXHTMLAnchors.getSubsectionAnchorID(title.getNumber());
      final String text = title.getNumber().subsectionNumberFormat();
      final Element elink = SXHTML.linkRawIDTarget("#" + id, id);
      elink.appendChild(text);
      epn.appendChild(elink);
      e.appendChild(epn);
    }

    {
      final String[] classes = new String[1];
      classes[0] = "subsection_title";
      final Element epn =
        SXHTML.elementWithClasses("div", SXHTML.NO_TYPE, classes);
      epn.appendChild(title.getActual());
      e.appendChild(epn);
    }

    return e;
  }

  static @Nonnull Element subsectionContent(
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull SAFormalItemsByKindReadable formals,
    final @Nonnull SASubsectionContent c)
    throws ConstraintError,
      Exception
  {
    return c
      .subsectionContentAccept(new SASubsectionContentVisitor<Element>() {
        @Override public Element visitFormalItem(
          final @Nonnull SAFormalItem formal)
          throws ConstraintError,
            Exception
        {
          return SXHTML.formalItem(link_provider, formals, formal);
        }

        @Override public Element visitParagraph(
          final @Nonnull SAParagraph paragraph)
          throws ConstraintError,
            Exception
        {
          return SXHTML.paragraph(link_provider, formals, paragraph);
        }
      });
  }

  static @Nonnull Element table(
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull SATable table)
    throws ConstraintError,
      Exception
  {
    final String[] classes = new String[1];
    classes[0] = "table";
    final Element e =
      SXHTML.elementWithClasses("table", SXHTML.NO_TYPE, classes);

    e.addAttribute(new Attribute("summary", null, table
      .getSummary()
      .getText()));

    table.getHeader().mapPartial(
      new PartialFunction<SATableHead, Unit, ConstraintError>() {
        @Override public Unit call(
          final SATableHead th)
          throws ConstraintError
        {
          final String[] eh_classes = new String[1];
          eh_classes[0] = "table_head";
          final Element eh =
            SXHTML.elementWithClasses("thead", SXHTML.NO_TYPE, eh_classes);

          final Element er = new Element("tr", SXHTML.XHTML_URI.toString());
          eh.appendChild(er);

          for (final SATableColumnName tn : th.getHeader().getElements()) {
            final String[] ecn_classes = new String[1];
            ecn_classes[0] = "table_column_name";
            final Element ecn =
              SXHTML.elementWithClasses("th", SXHTML.NO_TYPE, ecn_classes);

            ecn.appendChild(tn.getText());
            er.appendChild(ecn);
          }

          e.appendChild(eh);
          return Unit.unit();
        }
      });

    {
      final String[] tb_classes = new String[1];
      tb_classes[0] = "table_body";
      final Element tbe =
        SXHTML.elementWithClasses("tbody", SXHTML.NO_TYPE, tb_classes);
      e.appendChild(tbe);

      for (final SATableRow tr : table.getBody().getRows().getElements()) {
        final String[] tr_classes = new String[1];
        tr_classes[0] = "table_row";

        final Element tre =
          SXHTML.elementWithClasses("tr", SXHTML.NO_TYPE, tr_classes);
        tbe.appendChild(tre);

        for (final SATableCell tc : tr.getColumns().getElements()) {
          final String[] tc_classes = new String[1];
          tc_classes[0] = "table_cell";
          final Element tce =
            SXHTML.elementWithClasses("td", SXHTML.NO_TYPE, tc_classes);
          tre.appendChild(tce);

          final SNonEmptyList<Node> rc =
            SXHTML.tableCellContentList(link_provider, tc.getContent());
          for (final Node n : rc.getElements()) {
            tce.appendChild(n);
          }
        }
      }
    }

    return e;
  }

  static @Nonnull Node tableCellContent(
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull SATableCellContent c)
    throws ConstraintError,
      Exception
  {
    return c.tableCellContentAccept(new SATableCellContentVisitor<Node>() {
      @Override public Node visitFootnote(
        final SAFootnote footnote)
        throws ConstraintError,
          Exception
      {
        return SXHTML.footnoteReference(footnote);
      }

      @Override public Node visitImage(
        final SAImage image)
        throws ConstraintError,
          Exception
      {
        return SXHTML.image(image);
      }

      @Override public Node visitLink(
        final SALink link)
        throws ConstraintError,
          Exception
      {
        return SXHTML.link(link_provider, link);
      }

      @Override public Node visitLinkExternal(
        final SALinkExternal link)
        throws ConstraintError,
          Exception
      {
        return SXHTML.linkExternal(link);
      }

      @Override public Node visitListOrdered(
        final SAListOrdered list)
        throws ConstraintError,
          Exception
      {
        return SXHTML.listOrdered(link_provider, list);
      }

      @Override public Node visitListUnordered(
        final SAListUnordered list)
        throws ConstraintError,
          Exception
      {
        return SXHTML.listUnordered(link_provider, list);
      }

      @Override public Node visitTerm(
        final SATerm term)
        throws ConstraintError,
          Exception
      {
        return SXHTML.term(term);
      }

      @Override public Node visitText(
        final SAText text)
        throws ConstraintError,
          Exception
      {
        return SXHTML.text(text);
      }

      @Override public Node visitVerbatim(
        final SAVerbatim text)
        throws ConstraintError,
          Exception
      {
        return SXHTML.verbatim(text);
      }
    });
  }

  static @Nonnull SNonEmptyList<Node> tableCellContentList(
    final @Nonnull SLinkProvider link_provider,
    final @Nonnull SNonEmptyList<SATableCellContent> contents)
    throws ConstraintError,
      Exception
  {
    final List<Node> nodes = new ArrayList<Node>();
    for (final SATableCellContent c : contents.getElements()) {
      nodes.add(SXHTML.tableCellContent(link_provider, c));
    }
    return SNonEmptyList.newList(nodes);
  }

  static @Nonnull Node term(
    final @Nonnull SATerm term)
  {
    final String[] classes = new String[1];
    classes[0] = "term";
    final Element e =
      SXHTML.elementWithClasses("span", term.getType(), classes);
    e.appendChild(term.getText().getText());
    return e;
  }

  static @Nonnull Node text(
    final @Nonnull SAText text)
  {
    return new Text(text.getText());
  }

  static @Nonnull Element verbatim(
    final @Nonnull SAVerbatim text)
  {
    final String[] classes = new String[1];
    classes[0] = "verbatim";
    final Element e =
      SXHTML.elementWithClasses("pre", text.getType(), classes);
    e.appendChild(text.getText());
    return e;
  }

  private SXHTML()
  {
    throw new UnreachableCodeException();
  }
}
