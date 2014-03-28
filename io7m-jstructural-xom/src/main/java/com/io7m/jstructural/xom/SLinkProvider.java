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

import nu.xom.Element;

import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jstructural.annotated.SAFormalItem;
import com.io7m.jstructural.annotated.SAID;
import com.io7m.jstructural.annotated.SAPart;
import com.io7m.jstructural.annotated.SASection;
import com.io7m.jstructural.annotated.SASubsection;

interface SLinkProvider
{
  @Nonnull String getLinkTargetForID(
    final @Nonnull SAID id)
    throws ConstraintError;

  @Nonnull Element getFormalItemLink(
    final @Nonnull SAFormalItem f)
    throws ConstraintError;

  @Nonnull Element getPartLink(
    final @Nonnull SAPart p)
    throws ConstraintError;

  @Nonnull Element getSectionLink(
    final @Nonnull SASection s)
    throws ConstraintError;

  @Nonnull Element getSubsectionLink(
    final @Nonnull SASubsection s)
    throws ConstraintError;
}
