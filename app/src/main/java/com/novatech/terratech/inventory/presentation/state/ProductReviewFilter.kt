package com.novatech.terratech.inventory.presentation.state

enum class ProductReviewFilter(val stars: Int?) {
    ALL(null),
    FIVE_STARS(5),
    FOUR_STARS(4),
    THREE_STARS(3),
    TWO_STARS(2),
    ONE_STAR(1),
}
