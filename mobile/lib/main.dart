import 'package:flutter/material.dart';
import 'core/theme/app_theme.dart';
import 'features/product/screens/product_list_screen.dart';

void main() {
  runApp(const EcommerceApp());
}

class EcommerceApp extends StatelessWidget {
  const EcommerceApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Turkcell E-Ticaret',
      debugShowCheckedModeBanner: false,
      theme: AppTheme.light,
      home: const ProductListScreen(),
    );
  }
}
