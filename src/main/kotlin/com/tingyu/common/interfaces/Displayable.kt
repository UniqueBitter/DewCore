package com.tingyu.common.interfaces

/**
 * 可显示实体接口
 *
 * 适用于需要显示名称+颜色的枚举（种族、职业、阵营等）
 */
interface Displayable {
    /** 显示名称 */
    val displayName: String

    /** 显示颜色 */
    val displayColor: String
}