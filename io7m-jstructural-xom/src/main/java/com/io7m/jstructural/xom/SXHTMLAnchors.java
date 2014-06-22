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

import com.io7m.jstructural.annotated.SAFormalItemNumber;
import com.io7m.jstructural.annotated.SAFormalItemNumberPSF;
import com.io7m.jstructural.annotated.SAFormalItemNumberPSSF;
import com.io7m.jstructural.annotated.SAFormalItemNumberSF;
import com.io7m.jstructural.annotated.SAFormalItemNumberSSF;
import com.io7m.jstructural.annotated.SAFormalItemNumberVisitor;
import com.io7m.jstructural.annotated.SAParagraphNumber;
import com.io7m.jstructural.annotated.SAParagraphNumberPSP;
import com.io7m.jstructural.annotated.SAParagraphNumberPSSP;
import com.io7m.jstructural.annotated.SAParagraphNumberSP;
import com.io7m.jstructural.annotated.SAParagraphNumberSSP;
import com.io7m.jstructural.annotated.SAParagraphNumberVisitor;
import com.io7m.jstructural.annotated.SAPartNumber;
import com.io7m.jstructural.annotated.SASectionNumber;
import com.io7m.jstructural.annotated.SASectionNumberPS;
import com.io7m.jstructural.annotated.SASectionNumberS;
import com.io7m.jstructural.annotated.SASectionNumberVisitor;
import com.io7m.jstructural.annotated.SASubsectionNumber;
import com.io7m.jstructural.annotated.SASubsectionNumberPSS;
import com.io7m.jstructural.annotated.SASubsectionNumberSS;
import com.io7m.jstructural.annotated.SASubsectionNumberVisitor;
import com.io7m.junreachable.UnreachableCodeException;

/**
 * XHTML anchor values.
 */

final class SXHTMLAnchors
{
  static  String getFormalItemAnchorID(
    final  SAFormalItemNumber n)
    throws Exception
  {
    return n.formalItemNumberAccept(new SAFormalItemNumberVisitor<String>() {
      @Override public String visitFormalItemNumberPSF(
        final  SAFormalItemNumberPSF p)
        throws Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.ATTRIBUTE_PREFIX);
        b.append("_");
        b.append(SXHTML.PART_CODE);
        b.append(p.getPart());
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        b.append(SXHTML.FORMAL_CODE);
        b.append(p.getFormalItem());
        final String r = b.toString();
        assert r != null;
        return r;
      }

      @Override public String visitFormalItemNumberPSSF(
        final  SAFormalItemNumberPSSF p)
        throws Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.ATTRIBUTE_PREFIX);
        b.append("_");
        b.append(SXHTML.PART_CODE);
        b.append(p.getPart());
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        b.append(SXHTML.SUBSECTION_CODE);
        b.append(p.getSubsection());
        b.append(SXHTML.FORMAL_CODE);
        b.append(p.getFormalItem());
        final String r = b.toString();
        assert r != null;
        return r;
      }

      @Override public String visitFormalItemNumberSF(
        final  SAFormalItemNumberSF p)
        throws Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.ATTRIBUTE_PREFIX);
        b.append("_");
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        b.append(SXHTML.FORMAL_CODE);
        b.append(p.getFormalItem());
        final String r = b.toString();
        assert r != null;
        return r;
      }

      @Override public String visitFormalItemNumberSSF(
        final  SAFormalItemNumberSSF p)
        throws Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.ATTRIBUTE_PREFIX);
        b.append("_");
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        b.append(SXHTML.SUBSECTION_CODE);
        b.append(p.getSubsection());
        b.append(SXHTML.FORMAL_CODE);
        b.append(p.getFormalItem());
        final String r = b.toString();
        assert r != null;
        return r;
      }
    });
  }

  static  String getFormalItemFile(
    final  SAFormalItemNumber n)
    throws Exception
  {
    return n.formalItemNumberAccept(new SAFormalItemNumberVisitor<String>() {
      @Override public String visitFormalItemNumberPSF(
        final  SAFormalItemNumberPSF p)
        throws Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.PART_CODE);
        b.append(p.getPart());
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        b.append(".");
        b.append(SXHTML.OUTPUT_FILE_SUFFIX);
        final String r = b.toString();
        assert r != null;
        return r;
      }

      @Override public String visitFormalItemNumberPSSF(
        final  SAFormalItemNumberPSSF p)
        throws Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.PART_CODE);
        b.append(p.getPart());
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        b.append(".");
        b.append(SXHTML.OUTPUT_FILE_SUFFIX);
        final String r = b.toString();
        assert r != null;
        return r;
      }

      @Override public String visitFormalItemNumberSF(
        final  SAFormalItemNumberSF p)
        throws Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        b.append(".");
        b.append(SXHTML.OUTPUT_FILE_SUFFIX);
        final String r = b.toString();
        assert r != null;
        return r;
      }

      @Override public String visitFormalItemNumberSSF(
        final  SAFormalItemNumberSSF p)
        throws Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        b.append(".");
        b.append(SXHTML.OUTPUT_FILE_SUFFIX);
        final String r = b.toString();
        assert r != null;
        return r;
      }
    });
  }

  static  String getParagraphAnchorID(
    final  SAParagraphNumber n)
    throws Exception
  {
    return n.paragraphNumberAccept(new SAParagraphNumberVisitor<String>() {
      @Override public String visitParagraphNumberPSP(
        final  SAParagraphNumberPSP p)
        throws Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.ATTRIBUTE_PREFIX);
        b.append("_");
        b.append(SXHTML.PART_CODE);
        b.append(p.getPart());
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        b.append(SXHTML.PARAGRAPH_CODE);
        b.append(p.getParagraph());
        final String r = b.toString();
        assert r != null;
        return r;
      }

      @Override public String visitParagraphNumberPSSP(
        final  SAParagraphNumberPSSP p)
        throws Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.ATTRIBUTE_PREFIX);
        b.append("_");
        b.append(SXHTML.PART_CODE);
        b.append(p.getPart());
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        b.append(SXHTML.SUBSECTION_CODE);
        b.append(p.getSubsection());
        b.append(SXHTML.PARAGRAPH_CODE);
        b.append(p.getParagraph());
        final String r = b.toString();
        assert r != null;
        return r;
      }

      @Override public String visitParagraphNumberSP(
        final  SAParagraphNumberSP p)
        throws Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.ATTRIBUTE_PREFIX);
        b.append("_");
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        b.append(SXHTML.PARAGRAPH_CODE);
        b.append(p.getParagraph());
        final String r = b.toString();
        assert r != null;
        return r;
      }

      @Override public String visitParagraphNumberSSP(
        final  SAParagraphNumberSSP p)
        throws Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.ATTRIBUTE_PREFIX);
        b.append("_");
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        b.append(SXHTML.SUBSECTION_CODE);
        b.append(p.getSubsection());
        b.append(SXHTML.PARAGRAPH_CODE);
        b.append(p.getParagraph());
        final String r = b.toString();
        assert r != null;
        return r;
      }
    });
  }

  static  String getParagraphFile(
    final  SAParagraphNumber n)
    throws Exception
  {
    return n.paragraphNumberAccept(new SAParagraphNumberVisitor<String>() {
      @Override public String visitParagraphNumberPSP(
        final  SAParagraphNumberPSP p)
        throws Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.PART_CODE);
        b.append(p.getPart());
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        b.append(".");
        b.append(SXHTML.OUTPUT_FILE_SUFFIX);
        final String r = b.toString();
        assert r != null;
        return r;
      }

      @Override public String visitParagraphNumberPSSP(
        final  SAParagraphNumberPSSP p)
        throws Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.PART_CODE);
        b.append(p.getPart());
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        b.append(".");
        b.append(SXHTML.OUTPUT_FILE_SUFFIX);
        final String r = b.toString();
        assert r != null;
        return r;
      }

      @Override public String visitParagraphNumberSP(
        final  SAParagraphNumberSP p)
        throws Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        b.append(".");
        b.append(SXHTML.OUTPUT_FILE_SUFFIX);
        final String r = b.toString();
        assert r != null;
        return r;
      }

      @Override public String visitParagraphNumberSSP(
        final  SAParagraphNumberSSP p)
        throws Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        b.append(".");
        b.append(SXHTML.OUTPUT_FILE_SUFFIX);
        final String r = b.toString();
        assert r != null;
        return r;
      }
    });
  }

  static  String getPartAnchorID(
    final  SAPartNumber part)
  {
    final StringBuilder b = new StringBuilder();
    b.append(SXHTML.ATTRIBUTE_PREFIX);
    b.append("_p");
    b.append(part.getActual());
    final String r = b.toString();
    assert r != null;
    return r;
  }

  static  String getPartFile(
    final  SAPartNumber part)
  {
    final StringBuilder b = new StringBuilder();
    b.append(SXHTML.PART_CODE);
    b.append(part.getActual());
    b.append(".");
    b.append(SXHTML.OUTPUT_FILE_SUFFIX);
    final String r = b.toString();
    assert r != null;
    return r;
  }

  static  String getSectionAnchorID(
    final  SASectionNumber n)
    throws Exception
  {
    return n.sectionNumberAccept(new SASectionNumberVisitor<String>() {
      @Override public String visitSectionNumberWithoutPart(
        final  SASectionNumberS p)
        throws Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.ATTRIBUTE_PREFIX);
        b.append("_");
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        final String r = b.toString();
        assert r != null;
        return r;
      }

      @Override public String visitSectionNumberWithPart(
        final  SASectionNumberPS p)
        throws Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.ATTRIBUTE_PREFIX);
        b.append("_");
        b.append(SXHTML.PART_CODE);
        b.append(p.getPart());
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        final String r = b.toString();
        assert r != null;
        return r;
      }
    });
  }

  static  String getSectionFile(
    final  SASectionNumber n)
    throws Exception
  {
    return n.sectionNumberAccept(new SASectionNumberVisitor<String>() {
      @Override public String visitSectionNumberWithoutPart(
        final  SASectionNumberS s)
        throws Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.SECTION_CODE);
        b.append(s.getSection());
        b.append(".");
        b.append(SXHTML.OUTPUT_FILE_SUFFIX);
        final String r = b.toString();
        assert r != null;
        return r;
      }

      @Override public String visitSectionNumberWithPart(
        final  SASectionNumberPS sps)
        throws Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.PART_CODE);
        b.append(sps.getPart());
        b.append(SXHTML.SECTION_CODE);
        b.append(sps.getSection());
        b.append(".");
        b.append(SXHTML.OUTPUT_FILE_SUFFIX);
        final String r = b.toString();
        assert r != null;
        return r;
      }
    });
  }

  static  String getSubsectionAnchorID(
    final  SASubsectionNumber n)
    throws Exception
  {
    return n.subsectionNumberAccept(new SASubsectionNumberVisitor<String>() {
      @Override public String visitSubsectionNumberPSS(
        final  SASubsectionNumberPSS p)
        throws Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.ATTRIBUTE_PREFIX);
        b.append("_");
        b.append(SXHTML.PART_CODE);
        b.append(p.getPart());
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        b.append(SXHTML.SUBSECTION_CODE);
        b.append(p.getSubsection());
        final String r = b.toString();
        assert r != null;
        return r;
      }

      @Override public String visitSubsectionNumberSS(
        final  SASubsectionNumberSS p)
        throws Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.ATTRIBUTE_PREFIX);
        b.append("_");
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        b.append(SXHTML.SUBSECTION_CODE);
        b.append(p.getSubsection());
        final String r = b.toString();
        assert r != null;
        return r;
      }
    });
  }

  static  String getSubsectionFile(
    final  SASubsectionNumber n)
    throws Exception
  {
    return n.subsectionNumberAccept(new SASubsectionNumberVisitor<String>() {
      @Override public String visitSubsectionNumberPSS(
        final  SASubsectionNumberPSS p)
        throws Exception
      {
        return SXHTMLAnchors.getSectionFile(new SASectionNumberPS(
          p.getPart(),
          p.getSection()));
      }

      @Override public String visitSubsectionNumberSS(
        final  SASubsectionNumberSS p)
        throws Exception
      {
        return SXHTMLAnchors.getSectionFile(new SASectionNumberS(p
          .getSection()));
      }
    });
  }

  private SXHTMLAnchors()
  {
    throw new UnreachableCodeException();
  }
}
