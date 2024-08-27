package com.atguigu.daijia.driver.service;

import com.atguigu.daijia.model.vo.order.TextAuditingVo;

public interface CiService {


    /**
     * 图片审核功能函数
     *
     * @param path 图片文件的路径，用于识别图片内容
     * @return 返回一个Boolean值，指示图片是否通过审核
     *
     * 函数说明：
     * 本函数主要用于对给定路径的图片进行内容审核，判断其是否符合发布或展示的标准。
     * 审核的具体标准和流程未在注释中详细说明，假设已存在一套完整的审核逻辑。
     * 选择以Boolean类型作为返回值，是为了直接反映审核结果：true表示通过，false表示未通过。
     * 此函数不处理图片路径的错误或异常情况，假设调用者已确保路径的有效性。
     */
    Boolean imageAuditing(String path);

    /**
     * 文本审核接口
     * 该接口用于对给定的文本内容进行审核，以检测其中是否包含敏感或违规信息
     *
     * @param content 待审核的文本内容
     * @return 返回审核结果，包括是否通过审核及审核详情
     */
    TextAuditingVo textAuditing(String content);
}
