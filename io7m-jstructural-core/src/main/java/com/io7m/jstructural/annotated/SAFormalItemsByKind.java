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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.Nonnull;

import com.io7m.jaux.Constraints;
import com.io7m.jaux.Constraints.ConstraintError;
import com.io7m.jlog.Level;
import com.io7m.jlog.Log;

/**
 * The set of formal items organized by kind.
 */

public final class SAFormalItemsByKind implements
  SAFormalItemsByKindReadable,
  SAFormalItemsByKindWritable
{
  private static final @Nonnull SortedMap<SAFormalItemNumber, SAFormalItem> EMPTY;
  static {
    EMPTY =
      Collections
        .unmodifiableSortedMap(new TreeMap<SAFormalItemNumber, SAFormalItem>());
  }
  private final @Nonnull Log                                                log;

  private final @Nonnull Map<String, Set<SAFormalItem>>                     map;

  /**
   * Construct a new empty map.
   * 
   * @param in_log
   *          A log handle
   */

  public SAFormalItemsByKind(
    final @Nonnull Log in_log)
  {
    this.log = new Log(in_log, "formal-items");
    this.map = new HashMap<String, Set<SAFormalItem>>();
  }

  @Override public SortedMap<SAFormalItemNumber, SAFormalItem> get(
    final @Nonnull String kind)
    throws ConstraintError
  {
    final Set<SAFormalItem> set;
    if (this.map.containsKey(kind)) {
      set = this.map.get(kind);
    } else {
      return SAFormalItemsByKind.EMPTY;
    }

    final SortedMap<SAFormalItemNumber, SAFormalItem> r =
      new TreeMap<SAFormalItemNumber, SAFormalItem>();
    for (final SAFormalItem f : set) {
      r.put(f.getNumber(), f);
    }
    return Collections.unmodifiableSortedMap(r);
  }

  @Override public void put(
    final @Nonnull String kind,
    final @Nonnull SAFormalItem item)
    throws ConstraintError
  {
    if (this.log.enabled(Level.LOG_DEBUG)) {
      final StringBuilder b = new StringBuilder();
      b.append("new: ");
      b.append(item.getNumber().formalItemNumberFormat());
      b.append(" ");
      b.append(item.getTitle().getActual());
      b.append(" (kind ");
      b.append(item.getKind());
      b.append(")");
      this.log.debug(b.toString());
    }

    Set<SAFormalItem> set = null;
    if (this.map.containsKey(kind)) {
      set = this.map.get(kind);
    } else {
      set = new HashSet<SAFormalItem>();
    }

    Constraints.constrainArbitrary(
      set.contains(item) == false,
      "Item not already added to set");
    set.add(item);
    this.map.put(kind, set);
  }
}
