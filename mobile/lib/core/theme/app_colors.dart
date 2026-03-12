import 'package:flutter/material.dart';

abstract final class AppColors {
  // Primary
  static const Color primary = Color(0xFF6200EE);
  static const Color primaryVariant = Color(0xFF3700B3);
  static const Color onPrimary = Color(0xFFFFFFFF);

  // Secondary
  static const Color secondary = Color(0xFF03DAC6);
  static const Color secondaryVariant = Color(0xFF018786);
  static const Color onSecondary = Color(0xFF000000);

  // Background & Surface
  static const Color background = Color(0xFFF5F5F5);
  static const Color surface = Color(0xFFFFFFFF);
  static const Color onBackground = Color(0xFF1C1C1E);
  static const Color onSurface = Color(0xFF1C1C1E);

  // Semantic
  static const Color error = Color(0xFFB00020);
  static const Color onError = Color(0xFFFFFFFF);
  static const Color success = Color(0xFF4CAF50);
  static const Color warning = Color(0xFFFFC107);

  // Text
  static const Color textPrimary = Color(0xFF1C1C1E);
  static const Color textSecondary = Color(0xFF6B6B6B);
  static const Color textDisabled = Color(0xFFBDBDBD);
  static const Color textInverse = Color(0xFFFFFFFF);

  // Border & Divider
  static const Color border = Color(0xFFE0E0E0);
  static const Color divider = Color(0xFFF0F0F0);

  // Card
  static const Color cardBackground = Color(0xFFFFFFFF);
  static const Color cardShadow = Color(0x1A000000);
}
