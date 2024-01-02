package com.example.xevivuapp.util

import androidx.annotation.DrawableRes
import com.example.xevivuapp.R

sealed class OnBoardingPage(
    @DrawableRes
    val image: Int,
    val title: String,
    val description: String
) {
    object First : OnBoardingPage(
        image = R.drawable.onboarding_first,
        title = "Đặt xe mọi lúc",
        description = "Bạn có thể đặt xe bất cứ khi nào cần, giao diện thân thiện đặt xe trong tích tắc !"
    )

    object Second : OnBoardingPage(
        image = R.drawable.onboarding_second,
        title = "Tài xế mọi nơi",
        description = "Với lực lượng tài xế trải dài từ nông thôn đến thành thị, sẵn sàng phục vụ bạn !"
    )

    object Third : OnBoardingPage(
        image = R.drawable.onboarding_third,
        title = "Điểm đến linh hoạt",
        description = "Dễ dàng chọn địa điểm với hệ thống đồ chi tiết"
    )
}