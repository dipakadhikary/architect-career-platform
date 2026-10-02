package com.acos.tutorial.util;

import java.util.Locale;
import java.util.Objects;

/** Generates URL-safe tutorial slugs. */
public final class TutorialSlugger {

  private TutorialSlugger() {}

  public static String slugify(String title) {
    Objects.requireNonNull(title, "title");
    String slug =
        title.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-").replaceAll("^-+|-+$", "");
    if (slug.isBlank()) {
      throw new IllegalArgumentException("Unable to derive slug from title");
    }
    if (slug.length() > 200) {
      slug = slug.substring(0, 200).replaceAll("-+$", "");
    }
    return slug;
  }
}
