package ua.kpi.mobiledev.web.security.service;

public interface RedisStoreService<K, V> {

    void save(K key, V value);

    V get(K key);
}
