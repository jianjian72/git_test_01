package cn.wolfcode.service;

import cn.wolfcode.key.KeyPrefix;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 专门用于访问 redis 的服务
 */
public interface IRedisService<K extends KeyPrefix, V> {

    /**
     * 访问 STRING 类型的设置值命令
     */
    void set(K prefix, V value, String... suffix);

    /**
     * 从 STRING 结构中获取 value
     */
    V get(K prefix, String... suffix);

    /**
     * 删除一个 key
     */
    void del(K prefix, String... suffix);

    /**
     * Hash 自增命令
     *
     * @param prefix    前缀
     * @param hashFiled hash 字段
     * @param value     原子性增加的值
     * @param suffix    后缀
     */
    void hincr(K prefix, String hashFiled, V value, String... suffix);

    /**
     * String 自增命令
     *
     * @param prefix 前缀
     * @param value  自增的值
     * @param suffix 后缀
     * @return 自增以后的值
     */
    Long incr(K prefix, long value, String... suffix);

    /**
     * 获取整个 hash 对象
     *
     * @param prefix hash 前缀
     * @param suffix hash 后缀
     * @return 完整的 hash 对象
     */
    Map<Object, V> hgetAll(K prefix, String... suffix);

    Map<Object, V> hgetAll(String key);

    /**
     * 判断 SET 集合中是否有该成员
     *
     * @param prefix 前缀
     * @param value  成员
     * @param suffix 后缀
     * @return true代表存在, false代表不存在
     */
    Boolean isMember(K prefix, V value, String... suffix);

    /**
     * 删除 set 中的指定成员
     *
     * @param prefix 前缀
     * @param value  要删除的成员
     * @param suffix 后缀
     */
    void sdel(K prefix, V value, String... suffix);

    /**
     * 往 set 集合中追加成员
     *
     * @param prefix 前缀
     * @param value  成员
     * @param suffix 后缀
     */
    void sadd(K prefix, V value, String... suffix);

    /**
     * 设置 key 过期时间
     *
     * @param prefix     前缀
     * @param expireTime 过期时间
     * @param unit       单位
     * @param suffix     后缀
     */
    void expire(K prefix, long expireTime, TimeUnit unit, String... suffix);

    /**
     * 判断 key 是否存在
     *
     * @param prefix 前缀
     * @param suffix 后缀
     * @return 是否存在
     */
    Boolean exists(K prefix, String... suffix);

    /**
     * 一次性往 hash 中 put 多个键值对
     *
     * @param prefix 前缀
     * @param map    要存入的键值对 map
     * @param suffix 后缀
     */
    void hputAll(K prefix, Map<String, Object> map, String... suffix);

    /**
     * 通过 redis 的 keys 命令模糊匹配查询对应的 key
     *
     * @param pattern 模糊匹配的格式
     * @return 所有匹配的 key
     */
    Set<String> keys(String pattern);

    /**
     * 为 zset 的指定 value 增加 increment 的分数
     *
     * @param prefix    前缀
     * @param value     要加分数的成员
     * @param increment 要增加的分数
     * @param suffix    后缀
     */
    Double zincrBy(K prefix, V value, double increment, String... suffix);

    /**
     * String 的 SETNX 命令, 如果 key 不存在才能保存成功
     *
     * @param prefix 前缀
     * @param value  值
     * @param suffix 后缀
     * @return 保存成功还是失败 true/false
     */
    Boolean setnx(K prefix, V value, String... suffix);
}
