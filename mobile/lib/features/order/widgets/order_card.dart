import 'package:flutter/material.dart';
import '../model/order_model.dart';
import '../../../core/theme/app_colors.dart';
import '../../../core/theme/app_spacing.dart';
import '../../../core/theme/app_text_styles.dart';
import '../../../core/theme/app_radius.dart';

class OrderCard extends StatelessWidget {
  const OrderCard({
    super.key,
    required this.order,
    this.onDelete,
  });

  final OrderModel order;
  final VoidCallback? onDelete;

  @override
  Widget build(BuildContext context) {
    return Card(
      color: AppColors.cardBackground,
      shape: const RoundedRectangleBorder(borderRadius: AppRadius.cardRadius),
      elevation: 2,
      child: Padding(
        padding: const EdgeInsets.all(AppSpacing.lg),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildHeader(),
            const SizedBox(height: AppSpacing.sm),
            _buildCustomerRow(),
            const SizedBox(height: AppSpacing.sm),
            _buildDateRow(),
            const SizedBox(height: AppSpacing.md),
            _buildItemsSummary(),
            const SizedBox(height: AppSpacing.md),
            _buildFooter(),
          ],
        ),
      ),
    );
  }

  Widget _buildHeader() {
    final shortId = (order.id ?? '-').length > 8
        ? '${order.id!.substring(0, 8)}…'
        : (order.id ?? '-');

    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(
          'Sipariş #$shortId',
          style: AppTextStyles.headingSmall.copyWith(
            color: AppColors.textPrimary,
          ),
        ),
        if (onDelete != null)
          IconButton(
            icon: const Icon(Icons.delete_outline, color: AppColors.error),
            onPressed: onDelete,
            tooltip: 'Siparişi sil',
            padding: EdgeInsets.zero,
            constraints: const BoxConstraints(),
          ),
      ],
    );
  }

  Widget _buildCustomerRow() {
    return Row(
      children: [
        const Icon(Icons.person_outline,
            size: 16, color: AppColors.textSecondary),
        const SizedBox(width: AppSpacing.xs),
        Text(
          order.customerId ?? '-',
          style: AppTextStyles.bodyMedium.copyWith(
            color: AppColors.textSecondary,
          ),
        ),
      ],
    );
  }

  Widget _buildDateRow() {
    return Row(
      children: [
        const Icon(Icons.calendar_today_outlined,
            size: 16, color: AppColors.textSecondary),
        const SizedBox(width: AppSpacing.xs),
        Text(
          _formatDate(order.orderDate),
          style: AppTextStyles.bodySmall.copyWith(
            color: AppColors.textSecondary,
          ),
        ),
      ],
    );
  }

  Widget _buildItemsSummary() {
    final itemCount = order.items?.length ?? 0;

    return Container(
      padding: const EdgeInsets.symmetric(
        horizontal: AppSpacing.md,
        vertical: AppSpacing.sm,
      ),
      decoration: BoxDecoration(
        color: AppColors.background,
        borderRadius: AppRadius.cardRadius,
      ),
      child: Row(
        children: [
          const Icon(Icons.inventory_2_outlined,
              size: 16, color: AppColors.textSecondary),
          const SizedBox(width: AppSpacing.xs),
          Text(
            '$itemCount kalem ürün',
            style: AppTextStyles.bodySmall.copyWith(
              color: AppColors.textSecondary,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildFooter() {
    final totalPrice = order.totalPrice ?? 0.0;

    return Row(
      mainAxisAlignment: MainAxisAlignment.end,
      children: [
        Text(
          'Toplam: ',
          style: AppTextStyles.bodyMedium.copyWith(
            color: AppColors.textSecondary,
          ),
        ),
        Text(
          '₺${totalPrice.toStringAsFixed(2)}',
          style: AppTextStyles.headingMedium.copyWith(
            color: AppColors.primary,
          ),
        ),
      ],
    );
  }

  String _formatDate(String? dateStr) {
    if (dateStr == null) return '-';
    final date = DateTime.tryParse(dateStr);
    if (date == null) return dateStr;
    return '${date.day.toString().padLeft(2, '0')}.'
        '${date.month.toString().padLeft(2, '0')}.'
        '${date.year} '
        '${date.hour.toString().padLeft(2, '0')}:'
        '${date.minute.toString().padLeft(2, '0')}';
  }
}
