package com.tingyu.common.interfaces

/**
 * 可校验有效性接口
 *
 * 用于区分有效值与兜底值（如 NONE）
 */
interface Validatable {
    /**
     * 是否为有效值（排除兜底状态如 NONE）
     * 子类需自行实现具体逻辑
     *
     * @return `true` 有效，`false` 无效（如未选择/初始化状态）
     */
    fun isValid(): Boolean
}