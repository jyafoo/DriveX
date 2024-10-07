package com.atguigu.daijia.model.form.map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.geo.Distance;

import java.math.BigDecimal;

@Data
public class SearchNearByDriverForm {

    @Schema(description = "经度")
    private BigDecimal longitude;

    @Schema(description = "纬度")
    private BigDecimal latitude;

    @Schema(description = "里程")
    private BigDecimal mileageDistance;

    @Schema(description = "距离半径")
    private Distance distance;
}
