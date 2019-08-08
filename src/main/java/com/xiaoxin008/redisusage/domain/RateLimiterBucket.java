package com.xiaoxin008.redisusage.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * 令桶
 *
 * @author xiaoxin008(313595055 @ qq.com)
 * @since 1.0.0
 */
@Data
public class RateLimiterBucket implements Serializable {

    private Long capacity;

    private Long rate;
}
