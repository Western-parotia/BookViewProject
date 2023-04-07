package com.juziml.read.utils.ext

/**
 * 所有的item都返回true，则返回true
 *
 * 代替像如下方法：
 *  var allOk = true
 *  list?.forEach {
 *      if (!it.isOk) {
 *      allOk = false
 *      return@forEach
 *      }
 *  } ?: let { allOk = false }
 *
 * [all]的优化版
 *
 * @param empty 空数据返回true还是false
 * @param predicate 回调
 */
inline fun <T> Iterable<T>?.allTrue(empty: Boolean = false, predicate: (T) -> Boolean): Boolean {
    if (this == null || (this is Collection && isEmpty())) {
        return empty
    }
    for (element in this) if (!predicate(element)) return false
    return true
}

inline fun <T> Array<T>?.allTrue(empty: Boolean = false, predicate: (T) -> Boolean): Boolean {
    if (isNullOrEmpty()) {
        return empty
    }
    for (element in this) if (!predicate(element)) return false
    return true
}

/**
 * 有一个为true，则返回true
 *
 * @param empty 空数据返回true还是false
 * @param predicate 回调
 */
@JvmOverloads
inline fun <T> Iterable<T>?.oneTrue(empty: Boolean = false, predicate: (T) -> Boolean): Boolean {
    if (this == null || (this is Collection && isEmpty())) {
        return empty
    }
    for (element in this) if (predicate(element)) return true
    return false
}

inline fun <T> Array<T>?.oneTrue(empty: Boolean = false, predicate: (T) -> Boolean): Boolean {
    if (isNullOrEmpty()) {
        return empty
    }
    for (element in this) if (predicate(element)) return true
    return false
}

val Collection<*>.downIndices: IntProgression
    get() = size - 1 downTo 0

/**
 * 安全的遍历删除，倒序
 */
inline fun <T> MutableList<T>?.removeIfReverseSequence(filter: (index: Int, item: T) -> Boolean) {
    this?.let {
        for (index in downIndices) {
            if (filter(index, it[index])) {
                it.removeAt(index)
            }
        }
    }
}

/**
 * 安全的遍历删除，迭代器正序
 */
inline fun <T> MutableCollection<T>?.removeIfIterator(filter: (item: T) -> Boolean) {
    this?.let {
        val iterator = iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            if (filter(next)) {
                iterator.remove()
            }
        }
    }
}

/**
 * 增加了null判断
 */
fun <T> Collection<T>?.isNotEmptyNotNull(): Boolean = !isNullOrEmpty()

/**
 * 根据回调判断是否包含
 * @param predicate 回调判断，true则包含此值
 */
inline fun <T> Array<out T>?.contains(predicate: (T) -> Boolean): Boolean {
    this?.forEach {
        if (predicate(it)) {
            return true
        }
    }
    return false
}

/**
 * 根据回调判断是否包含，和[oneTrue]一个逻辑，所以直接调用
 */
inline fun <T> Iterable<T>?.contains(predicate: (T) -> Boolean) = oneTrue(predicate = predicate)

/**
 * 根据回调获取对应值
 * @param predicate 回调判断，true则返回此item
 */
inline fun <T> Iterable<T>?.getOrNull(predicate: (T) -> Boolean): T? {
    this?.forEach {
        if (predicate(it)) {
            return it
        }
    }
    return null
}

/**
 * 根据回调获取对应值
 * @param predicate 回调判断，true则返回此item
 */
inline fun <T> Array<T>?.getOrNull(predicate: (T) -> Boolean): T? {
    this?.forEach {
        if (predicate(it)) {
            return it
        }
    }
    return null
}