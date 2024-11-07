package com.seulmae.seulmae.global.health;

import com.seulmae.seulmae.global.util.ByteSizeFormatter;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component("redis")
public class CustomRedisHealthIndicator implements HealthIndicator {

    private final RedisConnectionFactory redisConnectionFactory;

    public CustomRedisHealthIndicator(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Override
    public Health health() {
        try {
            RedisConnection connection = RedisConnectionUtils.getConnection(this.redisConnectionFactory);

            Properties memoryInfo = connection.serverCommands().info("memory");

            String usedMemory = memoryInfo.getProperty("used_memory_human", "N/A");
            long usedMemoryBytes = Long.parseLong(memoryInfo.getProperty("used_memory", "0"));
            long maxMemoryBytes = Long.parseLong(memoryInfo.getProperty("maxmemory", "0"));

            String remainingMemory;
            String totalSystemMemoryHuman;

            if (memoryInfo.containsKey("total_system_memory")) {
                // 리눅스 환경에서는 Redis의 total_system_memory 사용
                long totalSystemMemoryBytes = Long.parseLong(memoryInfo.getProperty("total_system_memory", "0"));
                totalSystemMemoryHuman = ByteSizeFormatter.formatBytes(totalSystemMemoryBytes);
                remainingMemory = maxMemoryBytes > 0 ?
                        ByteSizeFormatter.formatBytes(maxMemoryBytes - usedMemoryBytes) :
                        ByteSizeFormatter.formatBytes(totalSystemMemoryBytes - usedMemoryBytes);
            } else {
                // 윈도우 환경에서는 메모리 측정을 하지 않고 "Unmeasurable"로 처리
                totalSystemMemoryHuman = "Unmeasurable";
                remainingMemory = "Unmeasurable";
            }

            Health.Builder healthBuilder;

            if (connection instanceof RedisClusterConnection clusterConnection) {

                healthBuilder = "fail".equalsIgnoreCase(clusterConnection.clusterGetClusterInfo().getState()) ?
                        Health.down() :
                        Health.up();

                healthBuilder
                        .withDetail("cluster_size", clusterConnection.clusterGetClusterInfo().getClusterSize())
                        .withDetail("slot_up", clusterConnection.clusterGetClusterInfo().getSlotsOk())
                        .withDetail("slot_fail", clusterConnection.clusterGetClusterInfo().getSlotsFail());
            } else {
                healthBuilder = Health.up()
                        .withDetail("version", connection.serverCommands().info().getProperty("redis_version"));
            }

            healthBuilder
                    .withDetail("usedMemory", usedMemory)
                    .withDetail("maxMemory", maxMemoryBytes > 0 ? ByteSizeFormatter.formatBytes(maxMemoryBytes) : "Unlimited")
                    .withDetail("remainingMemory", remainingMemory)
                    .withDetail("totalSystemMemory", totalSystemMemoryHuman);

            return healthBuilder.build();

        } catch (Exception e) {
            return Health.down(e).build();
        }
    }

}
