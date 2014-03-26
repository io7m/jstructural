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

package com.io7m.jstructural.annotated;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;

/**
 * Mappings from IDs to content.
 */

public final class SAIDMap implements SAIDMapWritable, SAIDMapReadable
{
  private final @Nonnull Map<SAID, SAIDTargetContent> map;

  /**
   * Construct a new empty map.
   */

  public SAIDMap()
  {
    this.map = new HashMap<SAID, SAIDTargetContent>();
  }

  @Override public SAIDTargetContent get(
    final @Nonnull SAID id)
    throws ConstraintError
  {
    return this.map.get(Constraints.constrainNotNull(id, "ID"));
  }

  @Override public void put(
    final @Nonnull SAID id,
    final @Nonnull SAIDTargetContent c)
    throws ConstraintError
  {
    Constraints.constrainNotNull(id, "ID");
    Constraints.constrainNotNull(c, "Content");
    Constraints.constrainArbitrary(
      this.map.containsKey(id) == false,
      "ID not already used");
    this.map.put(id, c);
  }

  @Override public int size()
  {
    return this.map.size();
  }
}
