import 'package:flutter/material.dart';
import '../model/product_model.dart';
import '../../../core/network/app_constants.dart';
import '../../../core/theme/app_colors.dart';
import '../../../core/theme/app_spacing.dart';
import '../../../core/theme/app_text_styles.dart';
import '../../../core/theme/app_radius.dart';

class ProductCard extends StatefulWidget {
  const ProductCard({
    super.key,
    required this.product,
    this.onAddToCart,
    this.onFavoriteChanged,
  });

  final ProductModel product;
  final VoidCallback? onAddToCart;
  final ValueChanged<bool>? onFavoriteChanged;

  @override
  State<ProductCard> createState() => _ProductCardState();
}

class _ProductCardState extends State<ProductCard> {
  bool _isFavorite = false;

  void _toggleFavorite() {
    setState(() => _isFavorite = !_isFavorite);
    widget.onFavoriteChanged?.call(_isFavorite);
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      color: AppColors.cardBackground,
      shape: const RoundedRectangleBorder(borderRadius: AppRadius.cardRadius),
      elevation: 2,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildImageSection(),
          Padding(
            padding: const EdgeInsets.all(AppSpacing.lg),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                _buildProductName(),
                const SizedBox(height: AppSpacing.xs),
                _buildRatingRow(),
                const SizedBox(height: AppSpacing.md),
                _buildPriceSection(),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildImageSection() {
    return Stack(
      children: [
        _buildImage(),
        Positioned(
          top: AppSpacing.sm,
          right: AppSpacing.sm,
          child: _buildIdBadge(),
        ),
        Positioned(
          bottom: AppSpacing.sm,
          right: AppSpacing.sm,
          child: _buildFavoriteButton(),
        ),
      ],
    );
  }

  Widget _buildImage() {
    return ClipRRect(
      borderRadius: const BorderRadius.vertical(
        top: Radius.circular(AppRadius.md),
      ),
      child: Image.network(
        AppConstants.placeholderImageUrl,
        height: 200,
        width: double.infinity,
        fit: BoxFit.cover,
      ),
    );
  }

  Widget _buildIdBadge() {
    return Text(
      'id: ${widget.product.id ?? '-'}',
      style: AppTextStyles.caption.copyWith(
        color: AppColors.textSecondary,
      ),
      maxLines: 1,
      overflow: TextOverflow.ellipsis,
    );
  }

  Widget _buildFavoriteButton() {
    return _IconCircleButton(
      icon: _isFavorite ? Icons.favorite : Icons.favorite_border,
      iconColor: _isFavorite ? AppColors.error : AppColors.textPrimary,
      onTap: _toggleFavorite,
    );
  }

  Widget _buildProductName() {
    return Text(
      widget.product.name ?? '',
      style: AppTextStyles.headingSmall.copyWith(
        color: AppColors.textPrimary,
      ),
      maxLines: 2,
      overflow: TextOverflow.ellipsis,
    );
  }

  Widget _buildRatingRow() {
    return const Row(
      children: [
        _StarRating(),
        SizedBox(width: AppSpacing.sm),
        _ReviewCountBadge(count: 97),
      ],
    );
  }

  Widget _buildPriceSection() {
    final double currentPrice = widget.product.price ?? 0.0;
    const int discountPercent = 10;
    final double originalPrice = currentPrice / (1 - discountPercent / 100);

    return Row(
      crossAxisAlignment: CrossAxisAlignment.end,
      children: [
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  _buildOriginalPrice(originalPrice),
                  const SizedBox(width: AppSpacing.sm),
                  const _DiscountBadge(percent: discountPercent),
                ],
              ),
              const SizedBox(height: AppSpacing.xs),
              _buildCurrentPrice(currentPrice),
            ],
          ),
        ),
        _buildAddToCartButton(),
      ],
    );
  }

  Widget _buildOriginalPrice(double price) {
    return Text(
      '\$${price.toStringAsFixed(2)}',
      style: AppTextStyles.bodyMedium.copyWith(
        color: AppColors.textSecondary,
        decoration: TextDecoration.lineThrough,
      ),
      maxLines: 1,
      overflow: TextOverflow.ellipsis,
    );
  }

  Widget _buildCurrentPrice(double price) {
    return Text(
      '\$${price.toStringAsFixed(2)}',
      style: AppTextStyles.headingMedium.copyWith(
        color: AppColors.textPrimary,
      ),
      maxLines: 1,
      overflow: TextOverflow.ellipsis,
    );
  }

  Widget _buildAddToCartButton() {
    return _IconCircleButton(
      icon: Icons.shopping_cart_outlined,
      iconColor: AppColors.onPrimary,
      backgroundColor: AppColors.primary,
      onTap: widget.onAddToCart,
      size: 48,
    );
  }
}

class _IconCircleButton extends StatelessWidget {
  const _IconCircleButton({
    required this.icon,
    required this.iconColor,
    this.backgroundColor,
    this.onTap,
    this.size = 36,
  });

  final IconData icon;
  final Color iconColor;
  final Color? backgroundColor;
  final VoidCallback? onTap;
  final double size;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        width: size,
        height: size,
        decoration: BoxDecoration(
          color: backgroundColor ?? AppColors.surface,
          shape: BoxShape.circle,
          boxShadow: [
            BoxShadow(
              color: AppColors.cardShadow,
              blurRadius: AppSpacing.sm,
              offset: const Offset(0, 2),
            ),
          ],
        ),
        child: Icon(
          icon,
          color: iconColor,
          size: size * 0.5,
        ),
      ),
    );
  }
}

class _StarRating extends StatelessWidget {
  const _StarRating({this.rating = 5.0});

  final double rating;

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: List.generate(5, (index) {
        return Icon(
          index < rating.floor() ? Icons.star : Icons.star_border,
          color: AppColors.warning,
          size: AppSpacing.lg,
        );
      }),
    );
  }
}

class _ReviewCountBadge extends StatelessWidget {
  const _ReviewCountBadge({required this.count});

  final int count;

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        Icon(
          Icons.chat_bubble_outline,
          size: AppSpacing.md,
          color: AppColors.textSecondary,
        ),
        const SizedBox(width: AppSpacing.xs),
        Text(
          '$count',
          style: AppTextStyles.bodySmall.copyWith(
            color: AppColors.textSecondary,
          ),
          maxLines: 1,
          overflow: TextOverflow.ellipsis,
        ),
      ],
    );
  }
}

class _DiscountBadge extends StatelessWidget {
  const _DiscountBadge({required this.percent});

  final int percent;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(
        horizontal: AppSpacing.sm,
        vertical: AppSpacing.xs,
      ),
      decoration: const BoxDecoration(
        color: AppColors.primary,
        borderRadius: AppRadius.chipRadius,
      ),
      child: Text(
        '-$percent%',
        style: AppTextStyles.caption.copyWith(
          color: AppColors.onPrimary,
        ),
        maxLines: 1,
        overflow: TextOverflow.ellipsis,
      ),
    );
  }
}
