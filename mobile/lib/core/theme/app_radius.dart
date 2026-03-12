import 'package:flutter/material.dart';

abstract final class AppRadius {
  static const double xs   = 4.0;
  static const double sm   = 8.0;
  static const double md   = 12.0;
  static const double lg   = 16.0;
  static const double xl   = 24.0;
  static const double full = 999.0;

  static const BorderRadius cardRadius =
      BorderRadius.all(Radius.circular(md));
  static const BorderRadius buttonRadius =
      BorderRadius.all(Radius.circular(sm));
  static const BorderRadius inputRadius =
      BorderRadius.all(Radius.circular(sm));
  static const BorderRadius chipRadius =
      BorderRadius.all(Radius.circular(full));
}
