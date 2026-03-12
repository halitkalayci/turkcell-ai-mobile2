import 'package:flutter/material.dart';
import '../model/category_model.dart';
import '../../../core/theme/app_colors.dart';
import '../../../core/theme/app_radius.dart';
import '../../../core/theme/app_spacing.dart';
import '../../../core/theme/app_text_styles.dart';

class CategoryCard extends StatelessWidget {
  const CategoryCard({
    super.key,
    required this.category,
  });

  final CategoryModel category;

  @override
  Widget build(BuildContext context) {
    return Card(
      color: AppColors.cardBackground,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(AppRadius.md),
      ),
      child: Padding(
        padding: const EdgeInsets.all(AppSpacing.lg),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildIcon(),
            const SizedBox(width: AppSpacing.md),
            Expanded(
              child: _buildContent(),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildIcon() {
    return const Icon(
      Icons.category,
      color: AppColors.primary,
      size: 40,
    );
  }

  Widget _buildContent() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _buildName(),
        const SizedBox(height: AppSpacing.sm),
        _buildDescription(),
      ],
    );
  }

  Widget _buildName() {
    final displayName = category.name ?? 'İsimsiz Kategori';
    return Text(
      displayName,
      style: AppTextStyles.headingSmall.copyWith(
        color: AppColors.textPrimary,
      ),
    );
  }

  Widget _buildDescription() {
    final displayDescription = category.description ?? 'Açıklama yok';
    return Text(
      displayDescription,
      style: AppTextStyles.bodySmall.copyWith(
        color: AppColors.textSecondary,
      ),
    );
  }
}
