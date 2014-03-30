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

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jaux.UnreachableCodeException;
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

/**
 * XHTML anchor values.
 */

final class SXHTMLAnchors
{
  static @Nonnull String getFormalItemAnchorID(
    final @Nonnull SAFormalItemNumber n)
    throws ConstraintError,
      Exception
  {
    return n.formalItemNumberAccept(new SAFormalItemNumberVisitor<String>() {
      @Override public String visitFormalItemNumberPSF(
        final @Nonnull SAFormalItemNumberPSF p)
        throws ConstraintError,
          Exception
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
        return b.toString();
      }

      @Override public String visitFormalItemNumberPSSF(
        final @Nonnull SAFormalItemNumberPSSF p)
        throws ConstraintError,
          Exception
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
        return b.toString();
      }

      @Override public String visitFormalItemNumberSF(
        final @Nonnull SAFormalItemNumberSF p)
        throws ConstraintError,
          Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.ATTRIBUTE_PREFIX);
        b.append("_");
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        b.append(SXHTML.FORMAL_CODE);
        b.append(p.getFormalItem());
        return b.toString();
      }

      @Override public String visitFormalItemNumberSSF(
        final @Nonnull SAFormalItemNumberSSF p)
        throws ConstraintError,
          Exception
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
        return b.toString();
      }
    });
  }

  static @Nonnull String getFormalItemFile(
    final @Nonnull SAFormalItemNumber n)
    throws ConstraintError,
      Exception
  {
    return n.formalItemNumberAccept(new SAFormalItemNumberVisitor<String>() {
      @Override public String visitFormalItemNumberPSF(
        final @Nonnull SAFormalItemNumberPSF p)
        throws ConstraintError,
          Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.PART_CODE);
        b.append(p.getPart());
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        b.append(".");
        b.append(SXHTML.OUTPUT_FILE_SUFFIX);
        return b.toString();
      }

      @Override public String visitFormalItemNumberPSSF(
        final @Nonnull SAFormalItemNumberPSSF p)
        throws ConstraintError,
          Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.PART_CODE);
        b.append(p.getPart());
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        b.append(".");
        b.append(SXHTML.OUTPUT_FILE_SUFFIX);
        return b.toString();
      }

      @Override public String visitFormalItemNumberSF(
        final @Nonnull SAFormalItemNumberSF p)
        throws ConstraintError,
          Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        b.append(".");
        b.append(SXHTML.OUTPUT_FILE_SUFFIX);
        return b.toString();
      }

      @Override public String visitFormalItemNumberSSF(
        final @Nonnull SAFormalItemNumberSSF p)
        throws ConstraintError,
          Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        b.append(".");
        b.append(SXHTML.OUTPUT_FILE_SUFFIX);
        return b.toString();
      }
    });
  }

  static @Nonnull String getParagraphAnchorID(
    final @Nonnull SAParagraphNumber n)
    throws ConstraintError,
      Exception
  {
    return n.paragraphNumberAccept(new SAParagraphNumberVisitor<String>() {
      @Override public String visitParagraphNumberPSP(
        final @Nonnull SAParagraphNumberPSP p)
        throws ConstraintError,
          Exception
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
        return b.toString();
      }

      @Override public String visitParagraphNumberPSSP(
        final @Nonnull SAParagraphNumberPSSP p)
        throws ConstraintError,
          Exception
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
        return b.toString();
      }

      @Override public String visitParagraphNumberSP(
        final @Nonnull SAParagraphNumberSP p)
        throws ConstraintError,
          Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.ATTRIBUTE_PREFIX);
        b.append("_");
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        b.append(SXHTML.PARAGRAPH_CODE);
        b.append(p.getParagraph());
        return b.toString();
      }

      @Override public String visitParagraphNumberSSP(
        final @Nonnull SAParagraphNumberSSP p)
        throws ConstraintError,
          Exception
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
        return b.toString();
      }
    });
  }

  static @Nonnull String getParagraphFile(
    final @Nonnull SAParagraphNumber n)
    throws ConstraintError,
      Exception
  {
    return n.paragraphNumberAccept(new SAParagraphNumberVisitor<String>() {
      @Override public String visitParagraphNumberPSP(
        final @Nonnull SAParagraphNumberPSP p)
        throws ConstraintError,
          Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.PART_CODE);
        b.append(p.getPart());
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        b.append(".");
        b.append(SXHTML.OUTPUT_FILE_SUFFIX);
        return b.toString();
      }

      @Override public String visitParagraphNumberPSSP(
        final @Nonnull SAParagraphNumberPSSP p)
        throws ConstraintError,
          Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.PART_CODE);
        b.append(p.getPart());
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        b.append(".");
        b.append(SXHTML.OUTPUT_FILE_SUFFIX);
        return b.toString();
      }

      @Override public String visitParagraphNumberSP(
        final @Nonnull SAParagraphNumberSP p)
        throws ConstraintError,
          Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        b.append(".");
        b.append(SXHTML.OUTPUT_FILE_SUFFIX);
        return b.toString();
      }

      @Override public String visitParagraphNumberSSP(
        final @Nonnull SAParagraphNumberSSP p)
        throws ConstraintError,
          Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        b.append(".");
        b.append(SXHTML.OUTPUT_FILE_SUFFIX);
        return b.toString();
      }
    });
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

  static @Nonnull String getPartFile(
    final @Nonnull SAPartNumber part)
  {
    final StringBuilder b = new StringBuilder();
    b.append(SXHTML.PART_CODE);
    b.append(part.getActual());
    b.append(".");
    b.append(SXHTML.OUTPUT_FILE_SUFFIX);
    return b.toString();
  }

  static @Nonnull String getSectionAnchorID(
    final @Nonnull SASectionNumber n)
    throws ConstraintError,
      Exception
  {
    return n.sectionNumberAccept(new SASectionNumberVisitor<String>() {
      @Override public String visitSectionNumberWithoutPart(
        final @Nonnull SASectionNumberS p)
        throws ConstraintError,
          Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.ATTRIBUTE_PREFIX);
        b.append("_");
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        return b.toString();
      }

      @Override public String visitSectionNumberWithPart(
        final @Nonnull SASectionNumberPS p)
        throws ConstraintError,
          Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.ATTRIBUTE_PREFIX);
        b.append("_");
        b.append(SXHTML.PART_CODE);
        b.append(p.getPart());
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        return b.toString();
      }
    });
  }

  static @Nonnull String getSectionFile(
    final @Nonnull SASectionNumber n)
    throws ConstraintError,
      Exception
  {
    return n.sectionNumberAccept(new SASectionNumberVisitor<String>() {
      @Override public String visitSectionNumberWithoutPart(
        final @Nonnull SASectionNumberS s)
        throws ConstraintError,
          Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.SECTION_CODE);
        b.append(s.getSection());
        b.append(".");
        b.append(SXHTML.OUTPUT_FILE_SUFFIX);
        return b.toString();
      }

      @Override public String visitSectionNumberWithPart(
        final @Nonnull SASectionNumberPS sps)
        throws ConstraintError,
          Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.PART_CODE);
        b.append(sps.getPart());
        b.append(SXHTML.SECTION_CODE);
        b.append(sps.getSection());
        b.append(".");
        b.append(SXHTML.OUTPUT_FILE_SUFFIX);
        return b.toString();
      }
    });
  }

  static @Nonnull String getSubsectionAnchorID(
    final @Nonnull SASubsectionNumber n)
    throws ConstraintError,
      Exception
  {
    return n.subsectionNumberAccept(new SASubsectionNumberVisitor<String>() {
      @Override public String visitSubsectionNumberPSS(
        final @Nonnull SASubsectionNumberPSS p)
        throws ConstraintError,
          Exception
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
        return b.toString();
      }

      @Override public String visitSubsectionNumberSS(
        final @Nonnull SASubsectionNumberSS p)
        throws ConstraintError,
          Exception
      {
        final StringBuilder b = new StringBuilder();
        b.append(SXHTML.ATTRIBUTE_PREFIX);
        b.append("_");
        b.append(SXHTML.SECTION_CODE);
        b.append(p.getSection());
        b.append(SXHTML.SUBSECTION_CODE);
        b.append(p.getSubsection());
        return b.toString();
      }
    });
  }

  static @Nonnull String getSubsectionFile(
    final @Nonnull SASubsectionNumber n)
    throws ConstraintError,
      Exception
  {
    return n.subsectionNumberAccept(new SASubsectionNumberVisitor<String>() {
      @Override public String visitSubsectionNumberPSS(
        final @Nonnull SASubsectionNumberPSS p)
        throws ConstraintError,
          Exception
      {
        return SXHTMLAnchors.getSectionFile(new SASectionNumberPS(
          p.getPart(),
          p.getSection()));
      }

      @Override public String visitSubsectionNumberSS(
        final @Nonnull SASubsectionNumberSS p)
        throws ConstraintError,
          Exception
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
