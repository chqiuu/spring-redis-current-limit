-- 获取调用脚本时传入的第一个key值（用作限流的 key）
local key = KEYS[1];
-- 获取调用脚本时传入的第一个参数值（限流大小）
local limit = tonumber(KEYS[2]);
-- 获取当前流量大小
local expire = tonumber(KEYS[3]);

local hasKey = redis.call('EXISTS',key);
local value = 1;

if hasKey == 1 then
    value = tonumber(redis.call('GET',key));
    if value >= limit then
        return -1;
    end
end
redis.call('INCR',key);

local ttl = redis.call('TTL',key);
if ttl < 0 then
    redis.call('EXPIRE',key,expire);
end
return value;
