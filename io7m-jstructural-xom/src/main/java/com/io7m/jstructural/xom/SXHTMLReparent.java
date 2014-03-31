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
import nu.xom.Node;
import nu.xom.ParentNode;

import com.io7m.jaux.UnreachableCodeException;

final class SXHTMLReparent
{
  private SXHTMLReparent()
  {
    throw new UnreachableCodeException();
  }

  private static @Nonnull Node getAbsoluteAncestor(
    final @Nonnull Element rbody)
  {
    Node n = rbody;

    for (;;) {
      final ParentNode p = n.getParent();
      if (p == null) {
        return n;
      }
      n = p;
    }
  }

  static void reparentBodyNode(
    final @Nonnull Element current_body,
    final @Nonnull Element target_body)
  {
    if (target_body != null) {
      final Node rbody_root = SXHTMLReparent.getAbsoluteAncestor(target_body);

      assert rbody_root != null;
      final ParentNode current_body_parent = current_body.getParent();
      assert current_body_parent != null;

      current_body.detach();
      current_body_parent.appendChild(rbody_root);
      target_body.appendChild(current_body);
    }
  }
}
