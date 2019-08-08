local key = KEYS[1];
local rate = cjson.decode(ARGV[1]).rate;
local capacity = cjson.decode(ARGV[1]).capacity;
local current = tonumber(redis.call("get",key));
if current == nil then
    redis.call("setex",key,rate,capacity - 1);
else
    if current == 0 then
        return false
    else
        redis.call("setex",key,rate,current - 1);
    end
end
return true