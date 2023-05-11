package com.app.babybaby.repository.purchase.coupon;

import com.app.babybaby.entity.purchase.coupon.Coupon;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CouponQueryDsl {
//    나의 쿠폰 조회
    public Slice<Coupon> findCouponByMemberId(Pageable pageable, Long memberId);
}
