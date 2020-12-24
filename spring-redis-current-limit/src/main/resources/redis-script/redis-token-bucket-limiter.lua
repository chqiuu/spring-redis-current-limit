--[[
命令桶算法
--]]
-- 获取唯一值Key
local key = KEYS[1];
-- 获取可请求次数
local limit = tonumber(KEYS[2]);
-- 获取递减步长
local step = tonumber(KEYS[3]);
-- 获取单位时间（间隔时间）
local interval = tonumber(KEYS[4]);
-- 获取当期时间
local nowTime = tonumber(KEYS[5]);
local value = 1;

local lastClearTimeKey = 'limiter-last-clear-time'..key
local lastClearTime = redis.call('GET',lastClearTimeKey);
local hasKey = redis.call('EXISTS',key);

if hasKey == 1 then
    local diff = tonumber(nowTime) - tonumber(lastClearTime);
    value = tonumber(redis.call('GET',key));
    if  diff >= interval then
            local maxValue = value + math.floor(diff / interval) * step;
            if maxValue > limit then
                value = limit;
            else
                value = maxValue;
            end
            redis.call('SET',lastClearTimeKey,nowTime);
            redis.call('SET',key,value);
    end
    if value <= 0 then
        return value;
    else
        redis.call('DECR',key);
    end
else
    redis.call('SET',key,limit-1);
    redis.call('SET',lastClearTimeKey,nowTime);
end
return value;
