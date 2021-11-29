package me.shrunkie.websocket.uuid.impl;

import me.shrunkie.websocket.WebServer;
import me.shrunkie.websocket.uuid.redis.RedisUtil;
import me.shrunkie.websocket.uuid.UUIDCache;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RedisUUIDCache implements UUIDCache {
    private static final Map<UUID, String> uuidToName = new ConcurrentHashMap<>();
    private static final Map<String, UUID> nameToUUID = new ConcurrentHashMap<>();

    public RedisUUIDCache() throws URISyntaxException {
        URI redisURI = new URI("redis://127.0.0.1:6379");
        WebServer.getInstance().jedisPool = new JedisPool(new JedisPoolConfig(),
                redisURI.getHost(),
                redisURI.getPort(),
                Protocol.DEFAULT_TIMEOUT);
        RedisUtil.runRedisCommand((redis) -> {
            final Map<String, String> cache = redis.hgetAll("UUIDCache");
            for (Map.Entry<String, String> cacheEntry : cache.entrySet()) {
                final UUID uuid = UUID.fromString(cacheEntry.getKey());
                final String name = cacheEntry.getValue();
                uuidToName.put(uuid, name);
                nameToUUID.put(name.toLowerCase(), uuid);
            }
            return null;
        });
    }

    @Override
    public UUID uuid(String p0) {
        return nameToUUID.get(p0.toLowerCase());
    }

    @Override
    public String name(UUID p0) {
        return uuidToName.get(p0);
    }

    @Override
    public void ensure(UUID p0) {
    }

    @Override
    public void update(UUID p0, String p1) {
        uuidToName.put(p0, p1);
        for (Map.Entry<String, UUID> entry : new HashMap<>(nameToUUID).entrySet()) {
            if (entry.getValue().equals(p0)) {
                nameToUUID.remove(entry.getKey());
            }
        }

        nameToUUID.put(p1.toLowerCase(), p0);
        RedisUtil.runRedisCommand((redis) -> {
            redis.hset("UUIDCache", p0.toString(), p1);
            return null;
        });
    }
}
